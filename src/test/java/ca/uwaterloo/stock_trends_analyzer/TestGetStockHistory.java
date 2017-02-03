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
            "FIT",
            0,
            1,
            2011,
            11,
            31,
            2016,
            "/tmp/"
        );
        System.out.println("Completed");
    }
}
