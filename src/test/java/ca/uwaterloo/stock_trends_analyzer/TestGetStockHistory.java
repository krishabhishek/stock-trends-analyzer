package ca.uwaterloo.stock_trends_analyzer;

import ca.uwaterloo.stock_trends_analyzer.utils.StockQueryHelper;
import org.junit.Test;

import java.io.IOException;

public class TestGetStockHistory
{
    @Test
    public void getStockHistory()
        throws IOException
    {
        StockQueryHelper.getStockHistory(
            "GOOG",
            0,
            1,
            2015,
            11,
            31,
            2016,
            "/home/v2john/"
        );
        System.out.println("Completed");
    }
}
