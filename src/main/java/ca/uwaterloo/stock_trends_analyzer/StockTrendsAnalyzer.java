package ca.uwaterloo.stock_trends_analyzer;

import ca.uwaterloo.stock_trends_analyzer.exceptions.InternalAppError;
import ca.uwaterloo.stock_trends_analyzer.processors.Processor;
import ca.uwaterloo.stock_trends_analyzer.processors.StockAnalysisProcessor;
import ca.uwaterloo.stock_trends_analyzer.processors.StockFetchProcessor;
import ca.uwaterloo.stock_trends_analyzer.utils.Options;

public class StockTrendsAnalyzer
{
    public static void main(String[] args)
    {
        try
        {
            Options.initializeInstance(args);
            Processor processor = getProcessor();
            processor.process();
        }
        catch (Exception e)
        {
            System.err.println("Application terminated");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Processor getProcessor()
        throws InternalAppError
    {
        Processor processor = null;
        if (Options.getInstance().getFetch())
        {
            processor = new StockFetchProcessor();
        }
        else if (Options.getInstance().getAnalyze())
        {
            processor = new StockAnalysisProcessor();
        }

        return processor;
    }
}
