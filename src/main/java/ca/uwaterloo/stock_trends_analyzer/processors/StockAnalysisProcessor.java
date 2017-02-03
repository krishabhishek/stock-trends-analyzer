package ca.uwaterloo.stock_trends_analyzer.processors;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import ca.uwaterloo.stock_trends_analyzer.beans.StockPrice;
import ca.uwaterloo.stock_trends_analyzer.beans.Trend;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.exceptions.InternalAppError;
import ca.uwaterloo.stock_trends_analyzer.utils.Options;
import ca.uwaterloo.stock_trends_analyzer.utils.StatisticalInferenceHelper;
import ca.uwaterloo.stock_trends_analyzer.utils.StockQueryHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
                processCompany(stockHistoryFile.getAbsolutePath(), options);
            }
        }

        log.info("StockAnalysisProcessor concluded");
    }

    private void processCompany(String companyStockHistoryFilePath, Options options)
        throws InternalAppError, IOException, InterruptedException
    {

        String companyName = StockQueryHelper.getCompanyName(
            companyStockHistoryFilePath,
            options.getStockSymbolMappingFilePath()
        );
        if (StringUtils.isBlank(companyName))
        {
            log.error("Company name not found in mapping file");
            return;
        }

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
            } catch (Exception e)
            {
                log.error("Skipping record: " + Arrays.asList(dayStat));
            }
        }

        Set<Trend> stockTrends = StatisticalInferenceHelper.identifyDownwardSpirals(stockPrices);
        log.info(stockTrends);
    }
}
