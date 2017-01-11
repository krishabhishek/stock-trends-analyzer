package ca.uwaterloo.stock_trends_analyzer.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StockAnalysisProcessor extends Processor
{
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public void process()
        throws Exception
    {
        log.info("StockAnalysisProcessor begun");   

        log.info("StockAnalysisProcessor concluded");
    }
}
