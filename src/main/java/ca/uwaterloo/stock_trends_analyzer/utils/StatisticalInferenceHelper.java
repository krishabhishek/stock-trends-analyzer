package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.beans.StockPrice;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.util.*;

public class StatisticalInferenceHelper
{
    private static Logger log = LogManager.getLogger(StatisticalInferenceHelper.class);

    public static Pair<TreeMap<Long, Double>, TreeMap<Long, Double>> identifyDownwardSpirals(List<StockPrice> stockPrices)
    {
        TreeMap<Long, Double> declineStartInstants = new TreeMap<>();
        TreeMap<Long, Double> climbStartInstants = new TreeMap<>(Collections.reverseOrder());

        Comparator<StockPrice> pricePointComparator = StockPrice.getPricePointComparator();
        stockPrices.sort(pricePointComparator);

        for (int i = 0; i < stockPrices.size(); i++)
        {
            Long currentInstant = stockPrices.get(i).getTimestamp();

            DateTime endOfPeriod = new DateTime(currentInstant).plusMonths(Constants.NUM_MONTHS_REGRESS);

            SimpleRegression regression = new SimpleRegression();
            for (int j = i; j < stockPrices.size(); j++)
            {
                DateTime timestamp = new DateTime(stockPrices.get(j).getTimestamp());
                if (timestamp.isAfter(endOfPeriod) && endOfPeriod.isAfter(DateTime.now()))
                {
                    break;
                }
                regression.addData(stockPrices.get(j).getTimestamp(), stockPrices.get(j).getClosingPrice());
            }

            if (regression.getN() < Constants.MINIMUM_DATAPOINTS_REGRESSION || Double.isNaN(regression.getSlope()))
            {
                log.error("Skipping for instant " + currentInstant + ". Insufficient model data");
                continue;
            }

            if (regression.getSlope() < 0)
            {
                declineStartInstants.put(currentInstant, regression.getSlope());
            } else
            {
                climbStartInstants.put(currentInstant, regression.getSlope());
            }
        }

        return new Pair<>(declineStartInstants, climbStartInstants);
    }
}
