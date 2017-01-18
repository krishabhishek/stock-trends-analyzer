package ca.uwaterloo.stock_trends_analyzer;

import ca.uwaterloo.stock_trends_analyzer.utils.StockQueryHelper;
import org.junit.Test;

import java.io.IOException;

public class TestGetCompanyName
{
    @Test
    public void getCompanyName()
        throws IOException
    {
        System.out.println(
            StockQueryHelper.getCompanyName(
                "/home/v2john/MEGA/Academic/Masters/UWaterloo/ResearchProject/" +
                "SemevalTask/semeval-2017-task-5-subtask-2/stockdata/STOCK_HISTORY_GOOG",
                "/home/v2john/MEGA/Academic/Masters/UWaterloo/" +
                "ResearchProject/SemevalTask/stock-data/symbol_mapping.json"
            )
        );
    }
}
