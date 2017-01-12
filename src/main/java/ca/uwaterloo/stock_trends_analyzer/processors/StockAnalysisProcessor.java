package ca.uwaterloo.stock_trends_analyzer.processors;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import ca.uwaterloo.stock_trends_analyzer.beans.StockPrice;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.utils.NewsExtractor;
import ca.uwaterloo.stock_trends_analyzer.utils.Options;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.Pair;
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
            if (dayStat.length < Constants.STOCKHISTORY_COLUMNS)
            {
                continue;
            }

            date = Constants.DATETIME_FORMATTER.parseDateTime(dayStat[Constants.DATETIME_INDEX]);
            closingPrice = Double.valueOf(dayStat[Constants.CLOSING_PRICE_INDEX]);

            stockPrices.add(new StockPrice(date.getMillis(), closingPrice));
        }

        Pair<Map<Double, Long>, Map<Double, Long>> stockTrends = identifyDownwardSpirals(stockPrices);
        List<String> badNews = NewsExtractor.getHeadlines(stockTrends.getFirst());
        List<String> goodNews = NewsExtractor.getHeadlines(stockTrends.getFirst());

        log.info("StockAnalysisProcessor concluded");
    }

    private Pair<Map<Double, Long>, Map<Double, Long>> identifyDownwardSpirals(List<StockPrice> stockPrices)
    {
        Map<Double, Long> declineStartInstants = new TreeMap<>();
        Map<Double, Long> climbStartInstants = new TreeMap<>();

        Comparator<StockPrice> pricePointComparator = StockPrice.getPricePointComparator();
        Collections.sort(stockPrices, pricePointComparator);

        for (int i = 0; i < stockPrices.size(); i++)
        {
            DateTime endOfPeriod =
                new DateTime(stockPrices.get(i).getTimestamp()).plusMonths(Constants.NUM_MONTHS_REGRESS);

            SimpleRegression regression = new SimpleRegression();
            for (int j = i; j < stockPrices.size(); j++)
            {
                DateTime timestamp = new DateTime(stockPrices.get(j).getTimestamp());
                if (timestamp.isAfter(endOfPeriod))
                {
                    break;
                }
                regression.addData(stockPrices.get(j).getTimestamp(), stockPrices.get(j).getClosingPrice());
            }

            if (regression.getN() < Constants.MINIMUM_DATAPOINTS_REGRESSION)
            {
                break;
            }

            if (regression.getSlope() < 0)
            {
                declineStartInstants.put(regression.getSlope(), stockPrices.get(i).getTimestamp());
            }
            else
            {
                climbStartInstants.put(regression.getSlope(), stockPrices.get(i).getTimestamp());
            }
        }

        return new Pair<>(declineStartInstants, climbStartInstants);
    }
}

