package ca.uwaterloo.stock_trends_analyzer.processors;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import ca.uwaterloo.stock_trends_analyzer.beans.StockPrice;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.exceptions.InternalAppError;
import ca.uwaterloo.stock_trends_analyzer.utils.*;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class StockAnalysisProcessor extends Processor
{
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public void process()
        throws Exception
    {
        log.info("StockAnalysisProcessor begun");

        Options options = Options.getInstance();
        File[] stockHistoryFiles = new File(options.getStockHistoryFilePath()).listFiles();

        if (null == stockHistoryFiles)
        {
            throw new InternalAppError("No Stock History files to examine");
        }

        for (File stockHistoryFile : stockHistoryFiles)
        {
            if (stockHistoryFile.isFile())
            {
                processCompany(stockHistoryFile.getAbsolutePath());
            }
        }

        log.info("StockAnalysisProcessor concluded");
    }

    private void processCompany(String companyStockHistoryFilePath)
        throws InternalAppError, IOException, InterruptedException, URISyntaxException
    {
        Options options = Options.getInstance();

        String companyName = StockQueryHelper.getCompanyName(
            companyStockHistoryFilePath,
            options.getStockSymbolMappingFilePath()
        );

        log.info("Working on " + companyName);

        CSVReader reader =
            new CSVReader(
                new FileReader(companyStockHistoryFilePath), CSVParser.DEFAULT_SEPARATOR,
                CSVParser.DEFAULT_QUOTE_CHARACTER, 1
            );

        List<String[]> dayStats = reader.readAll();
        List<StockPrice> stockPrices = new ArrayList<>();
        DateTime date;
        Double closingPrice;

        for (String[] dayStat : dayStats)
        {
            try
            {
                if (dayStat.length < Constants.STOCKHISTORY_COLUMNS)
                {
                    continue;
                }

                date = Constants.DATETIME_FORMATTER.parseDateTime(dayStat[Constants.DATETIME_INDEX]);
                closingPrice = Double.valueOf(dayStat[Constants.CLOSING_PRICE_INDEX]);

                stockPrices.add(new StockPrice(date.getMillis(), closingPrice));
            }
            catch (Exception e)
            {
                log.error("Skipping entry " + Arrays.asList(dayStat));
            }
        }

        Pair<TreeMap<Long, Double>, TreeMap<Long, Double>> stockTrends =
            StatisticalInferenceHelper.identifyDownwardSpirals(stockPrices);

        TreeMap<Long, Double> negativeTrends = stockTrends.getFirst();
        TreeMap<Long, Double> positiveTrends = stockTrends.getSecond();

        List<Long> negativeTrendStartInstants = new ArrayList<>(negativeTrends.keySet());
        List<Long> positiveTrendStartInstants = new ArrayList<>(positiveTrends.keySet());

        Set<String> negativeNewsHeadlines = new HashSet<>();
        Set<String> positiveNewsHeadlines = new HashSet<>();

        for (int i = 0; i < Constants.TIME_PERIODS_TO_CONSIDER && i < negativeTrendStartInstants.size(); i++)
        {
            negativeNewsHeadlines.addAll(
                NewsExtractor.getHeadlines(
                    companyName,
                    new DateTime(negativeTrendStartInstants.get(i)),
                    new DateTime(negativeTrendStartInstants.get(i)).plusMonths(Constants.NUM_MONTHS_REGRESS),
                    options.getAppConfig().getQueryFilePath()
                )
            );
        }

        for (int i = 0; i < Constants.TIME_PERIODS_TO_CONSIDER && i < positiveTrendStartInstants.size(); i++)
        {
            positiveNewsHeadlines.addAll(
                NewsExtractor.getHeadlines(
                    companyName,
                    new DateTime(positiveTrendStartInstants.get(i)),
                    new DateTime(positiveTrendStartInstants.get(i)).plusMonths(Constants.NUM_MONTHS_REGRESS),
                    options.getAppConfig().getQueryFilePath()
                )
            );
        }

        Map<String, Set<String>> labeledSentences = new HashMap<>();
        labeledSentences.put("positive", positiveNewsHeadlines);
        labeledSentences.put("negative", negativeNewsHeadlines);

        log.info("Writing news to file");
        if (labeledSentences.size() > 0)
        {
            FileHelper.writeNewsToFile(companyName, new File(options.getOutputFile()), labeledSentences);
        }
    }
}