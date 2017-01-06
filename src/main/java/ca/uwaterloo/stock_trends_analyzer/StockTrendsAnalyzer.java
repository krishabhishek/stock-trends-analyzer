package ca.uwaterloo.stock_trends_analyzer;

import ca.uwaterloo.stock_trends_analyzer.processors.Processor;
import ca.uwaterloo.stock_trends_analyzer.processors.StockTrendsProcessor;
import ca.uwaterloo.stock_trends_analyzer.utils.Options;

public class StockTrendsAnalyzer
{
    public static void main(String[] args)
    {
        try
        {
            Options.initializeInstance(args);
            Processor processor = new StockTrendsProcessor();
            processor.process();
        }
        catch (Exception e)
        {
            System.err.println("Application terminated");
            System.exit(1);
        }
    }
}
