package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.beans.StockPrice;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.Pair;
import org.joda.time.DateTime;

import java.util.*;

public class StatisticalInferenceHelper
{
    public static Pair<TreeMap<Long, Double>, TreeMap<Long, Double>> identifyDownwardSpirals(List<StockPrice> stockPrices)
    {
        TreeMap<Long, Double> declineStartInstants = new TreeMap<>();
        TreeMap<Long, Double> climbStartInstants = new TreeMap<>(Collections.reverseOrder());

        Comparator<StockPrice> pricePointComparator = StockPrice.getPricePointComparator();
        stockPrices.sort(pricePointComparator);

        for (int i = 0; i < stockPrices.size(); i++)
        {
            DateTime endOfPeriod =
                new DateTime(stockPrices.get(i).getTimestamp()).plusMonths(Constants.NUM_MONTHS_REGRESS);

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

            if (regression.getN() < Constants.MINIMUM_DATAPOINTS_REGRESSION)
            {
                break;
            }

            if (regression.getSlope() < -1 * Constants.SLOPE_THRESHOLD)
            {
                declineStartInstants.put(stockPrices.get(i).getTimestamp(), regression.getSlope());
            }
            else if (regression.getSlope() > Constants.SLOPE_THRESHOLD)
            {
                climbStartInstants.put(stockPrices.get(i).getTimestamp(), regression.getSlope());
            }
        }

        return new Pair<>(declineStartInstants, climbStartInstants);
    }
}
