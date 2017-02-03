package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.beans.StockPrice;
import ca.uwaterloo.stock_trends_analyzer.beans.Trend;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.enums.Sentiment;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatisticalInferenceHelper
{
    private static Logger log = LogManager.getLogger(StatisticalInferenceHelper.class);

    public static Set<Trend> identifyDownwardSpirals(List<StockPrice> stockPrices)
    {
        Set<Trend> trendSet = new HashSet<>();

        log.info(stockPrices.size() + " entries to analyze");
        for (int i = 0; i < stockPrices.size(); i++)
        {
            stockPrices.sort(StockPrice.getPricePointComparator());

            DateTime startDate = new DateTime(stockPrices.get(i).getTimestamp());
            DateTime endDate = startDate.plusMonths(Constants.NUM_MONTHS_REGRESS);

            if (DateTime.now().minusMonths(Constants.NUM_MONTHS_REGRESS).isBefore(stockPrices.get(i).getTimestamp()))
            {
                continue;
            }
            log.debug("startDate: " + startDate);

            SimpleRegression regression = new SimpleRegression();
            regression.addData(stockPrices.get(i).getTimestamp(), stockPrices.get(i).getClosingPrice());
            for (int j = 0; j < stockPrices.size() - i; j++)
            {
                DateTime currentDate = new DateTime(stockPrices.get(j).getTimestamp());
                if (currentDate.isBefore(endDate))
                {
                    regression.addData(stockPrices.get(j).getTimestamp(), stockPrices.get(j).getClosingPrice());
                }
            }

            if (regression.getN() > Constants.MINIMUM_DATAPOINTS_REGRESSION)
            {
                Double slope = regression.getSlope();
                Sentiment sentiment = slope > 0 ? Sentiment.POSITIVE : Sentiment.NEGATIVE;
                trendSet.add(new Trend(sentiment, startDate.toDate(), endDate.toDate(), slope));
            }
            else
            {
                log.debug("Insufficient data points " + regression.getN());
            }
        }

        return trendSet;
    }
}
