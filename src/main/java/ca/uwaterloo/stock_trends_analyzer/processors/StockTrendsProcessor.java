package ca.uwaterloo.stock_trends_analyzer.processors;

import ca.uwaterloo.stock_trends_analyzer.StockTrendsAnalyzer;
import ca.uwaterloo.stock_trends_analyzer.utils.Options;
import ca.uwaterloo.stock_trends_analyzer.utils.StockQueryHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StockTrendsProcessor extends Processor
{
    private Logger log = LogManager.getLogger(StockTrendsAnalyzer.class);

    @Override
    public void process()
        throws Exception
    {
        log.info("StockTrendsProcessor begun");

        StockQueryHelper.getStockHistory(
            "GOOG",
            Options.getInstance().getStartDate().getMonthOfYear() - 1,
            Options.getInstance().getStartDate().getDayOfMonth(),
            Options.getInstance().getStartDate().getYear(),
            Options.getInstance().getEndDate().getMonthOfYear() - 1,
            Options.getInstance().getEndDate().getDayOfMonth(),
            Options.getInstance().getEndDate().getYear(),
            "/home/v2john/"
        );

        log.info("StockTrendsProcessor concluded");
    }
}
