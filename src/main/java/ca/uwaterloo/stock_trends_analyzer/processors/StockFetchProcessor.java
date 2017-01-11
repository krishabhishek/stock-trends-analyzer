package ca.uwaterloo.stock_trends_analyzer.processors;

import ca.uwaterloo.stock_trends_analyzer.utils.Options;
import ca.uwaterloo.stock_trends_analyzer.utils.StockQueryHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class StockFetchProcessor extends Processor
{
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public void process()
        throws Exception
    {
        log.info("StockFetchProcessor begun");

        Options options = Options.getInstance();
        try (Stream<String> stream = Files.lines(Paths.get(options.getSymbolsFilePath()))) {
            stream.forEach(
                line ->
                {
                    log.info("Fetching stock history for company: " + line);
                    try
                    {
                        StockQueryHelper.getStockHistory(
                            line, options.getStartDate().getMonthOfYear() - 1,
                            options.getStartDate().getDayOfMonth(), options.getStartDate().getYear(),
                            options.getEndDate().getMonthOfYear() - 1, options.getEndDate().getDayOfMonth(),
                            options.getEndDate().getYear(), options.getOutputDirectory());
                    }
                    catch (IOException e)
                    {
                        String msg = "IOException while reading symbols file";
                        log.error(msg, e);
                    }
                }
            );
        }

        log.info("StockFetchProcessor concluded");
    }
}
