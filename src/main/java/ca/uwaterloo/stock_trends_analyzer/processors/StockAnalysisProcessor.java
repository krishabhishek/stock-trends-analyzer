package ca.uwaterloo.stock_trends_analyzer.processors;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import ca.uwaterloo.stock_trends_analyzer.beans.StockPrice;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.utils.Options;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.io.FileReader;
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
            if (dayStat.length < 7)
            {
                continue;
            }

            date = Constants.DATETIME_FORMATTER.parseDateTime(dayStat[0]);
            closingPrice = Double.valueOf(dayStat[6]);

            stockPrices.add(new StockPrice(date.getMillis(), closingPrice));
        }

        identifyDownwardSpirals(stockPrices);

        log.info("StockAnalysisProcessor concluded");
    }

    private void identifyDownwardSpirals(List<StockPrice> stockPrices)
    {
        Comparator<StockPrice> pricePointComparator = StockPrice.getPricePointComparator();
        Collections.sort(stockPrices, pricePointComparator);
        Double worstHit = Double.MAX_VALUE;
        DateTime startDate = null, endDate = null;

        for (int i = 0; i < stockPrices.size(); i++)
        {
            DateTime endOfMonth = new DateTime(stockPrices.get(i).getTimestamp()).plusMonths(6);

            SimpleRegression regression = new SimpleRegression();
            for (int j = i; j < stockPrices.size(); j++)
            {
                DateTime timestamp = new DateTime(stockPrices.get(j).getTimestamp());
                if (timestamp.isAfter(endOfMonth))
                {
                    break;
                }
                regression.addData(stockPrices.get(j).getTimestamp(), stockPrices.get(j).getClosingPrice());
            }

            if (regression.getN() < 100)
            {
                break;
            }

            if (regression.getSlope() < worstHit)
            {
                worstHit = regression.getSlope();
                startDate = new DateTime(stockPrices.get(i).getTimestamp());
                endDate = new DateTime(stockPrices.get(i).getTimestamp()).plusMonths(6);
            }
        }

        log.debug("Worst decline: " + worstHit);
        log.debug("During " + startDate + " to " + endDate);
    }
}

