package ca.uwaterloo.stock_trends_analyzer.processors;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import ca.uwaterloo.stock_trends_analyzer.beans.StockPrice;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.utils.*;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
        CSVReader reader =
            new CSVReader(
                new FileReader(options.getStockHistoryFilePath()), CSVParser.DEFAULT_SEPARATOR,
                CSVParser.DEFAULT_QUOTE_CHARACTER, 1
            );

        List<String[]> dayStats = reader.readAll();
        List<StockPrice> stockPrices = new ArrayList<>();
        DateTime date;
        Double closingPrice;

        for (String[] dayStat : dayStats)
        {
            if (dayStat.length < Constants.STOCKHISTORY_COLUMNS)
            {
                continue;
            }

            date = Constants.DATETIME_FORMATTER.parseDateTime(dayStat[Constants.DATETIME_INDEX]);
            closingPrice = Double.valueOf(dayStat[Constants.CLOSING_PRICE_INDEX]);

            stockPrices.add(new StockPrice(date.getMillis(), closingPrice));
        }

        Pair<TreeMap<Long, Double>, TreeMap<Long, Double>> stockTrends =
            StatisticalInferenceHelper.identifyDownwardSpirals(stockPrices);

        TreeMap<Long, Double> negativeTrends = stockTrends.getFirst();
        TreeMap<Long, Double> positiveTrends = stockTrends.getSecond();

        List<Long> negativeTrendStartInstants = new ArrayList<>(negativeTrends.keySet());
        List<Long> positiveTrendStartInstants = new ArrayList<>(positiveTrends.keySet());

        List<String> negativeNewsHeadlines = new ArrayList<>();
        List<String> positiveNewsHeadlines = new ArrayList<>();

        for (int i = 0; i < Constants.TIME_PERIODS_TO_CONSIDER && i < negativeTrendStartInstants.size(); i++)
        {
            negativeNewsHeadlines.addAll(
                NewsExtractor.getHeadlines(
                    StockQueryHelper.getCompanyName(
                        options.getStockHistoryFilePath(),
                        options.getStockSymbolMappingFilePath()
                    ),
                    new DateTime(negativeTrendStartInstants.get(i)),
                    new DateTime(negativeTrendStartInstants.get(i)).plusMonths(Constants.NUM_MONTHS_REGRESS),
                    "business"
                )
            );
        }

        String companyName = StockQueryHelper.getCompanyName(
            options.getStockHistoryFilePath(),
            options.getStockSymbolMappingFilePath()
        );

        for (int i = 0; i < Constants.TIME_PERIODS_TO_CONSIDER && i < positiveTrendStartInstants.size(); i++)
        {
            positiveNewsHeadlines.addAll(
                NewsExtractor.getHeadlines(
                    companyName,
                    new DateTime(positiveTrendStartInstants.get(i)),
                    new DateTime(positiveTrendStartInstants.get(i)).plusMonths(Constants.NUM_MONTHS_REGRESS),
                    "business"
                )
            );
        }

        Map<String, List<String>> labeledSentences = new HashMap<>();
        labeledSentences.put("positive", positiveNewsHeadlines);
        labeledSentences.put("negative", negativeNewsHeadlines);

        log.info("Writing news to file");
        FileHelper.writeNewsToFile(companyName, new File(options.getOutputFile()), labeledSentences);

        log.info("StockAnalysisProcessor concluded");
    }
}
