package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.beans.StockPrice;
import ca.uwaterloo.stock_trends_analyzer.beans.Trend;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.enums.Sentiment;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.util.*;

public class StatisticalInferenceHelper
{
    private static Logger log = LogManager.getLogger(StatisticalInferenceHelper.class);

    public static Set<Trend> identifyDownwardSpirals(List<StockPrice> stockPrices)
    {
        Set<Trend> trendSet = new HashSet<>();

        log.debug(stockPrices.size() + " entries to analyze");
        for (int i = 0; i < stockPrices.size(); i++)
        {
            stockPrices.sort(StockPrice.getPricePointComparator());

            DateTime startDate = new DateTime(stockPrices.get(i).getTimestamp());
            DateTime endDate = startDate.plusMonths(Constants.NUM_MONTHS_REGRESS);

            if (DateTime.now().minusMonths(Constants.NUM_MONTHS_REGRESS).isBefore(stockPrices.get(i).getTimestamp()))
            {
                continue;
            }

            SimpleRegression regression = new SimpleRegression();
            Map<Long, Double> dataPoints = new HashMap<>();

            regression.addData(stockPrices.get(i).getTimestamp(), stockPrices.get(i).getClosingPrice());
            dataPoints.put(stockPrices.get(i).getTimestamp(), stockPrices.get(i).getClosingPrice());
            for (int j = i + 1; j < stockPrices.size(); j++)
            {
                DateTime currentDate = new DateTime(stockPrices.get(j).getTimestamp());
                if (currentDate.isBefore(endDate))
                {
                    regression.addData(stockPrices.get(j).getTimestamp(), stockPrices.get(j).getClosingPrice());
                    dataPoints.put(stockPrices.get(j).getTimestamp(), stockPrices.get(j).getClosingPrice());
                }
                else
                {
                    break;
                }
            }

            if (regression.getN() > Constants.MINIMUM_DATAPOINTS_REGRESSION)
            {
                Double slope = regression.getSlope();
                Sentiment sentiment = getSentimentFromSlope(slope);

                if (consistentTrend(dataPoints, sentiment, Constants.NUM_DAYS_REGRESS_MINI))
                {
                    trendSet.add(new Trend(sentiment, startDate, endDate, slope));
                }
            }
            else
            {
                log.debug("Insufficient data points " + regression.getN());
            }
        }

        return trendSet;
    }

    private static Boolean consistentTrend(Map<Long, Double> dataPoints, Sentiment sentiment, int noOfDays)
    {
        Integer i = 0;
        List<Long> timestamps = new ArrayList<>(dataPoints.keySet());
        timestamps.sort(Comparator.comparingLong(l -> l));

        while (i < timestamps.size())
        {
            SimpleRegression regression = new SimpleRegression();

            Long timestamp = timestamps.get(i);
            regression.addData(timestamp, dataPoints.get(timestamp));

            Integer j = i + 1;
            while (
                j < timestamps.size() &&
                new DateTime(timestamps.get(i)).plusDays(noOfDays).isAfter(timestamps.get(j))
            )
            {
                timestamp = timestamps.get(j);
                regression.addData(timestamp, dataPoints.get(timestamp));
                ++j;
            }

            if (getSentimentFromSlope(regression.getSlope()) != sentiment)
            {
                return false;
            }

            i = j + 1;
        }

        return true;
    }

    private static Sentiment getSentimentFromSlope(Double slope)
    {
        return slope > 0 ? Sentiment.POSITIVE : Sentiment.NEGATIVE;
    }
}
