package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.beans.AppConfig;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.exceptions.InternalAppError;
import ca.uwaterloo.stock_trends_analyzer.exceptions.InvalidConfigurationError;
import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;

@Getter
@ToString
public class Options
{
    private static Options instance;
    private AppConfig appConfig;
    private DateTime startDate;
    private DateTime endDate;

    private Logger log = LogManager.getLogger(getClass());

    @Option(name = "-help", usage = "Help",
            metaVar = "HELP")
    private Boolean help = false;

    @Option(name = "-fetch", usage = "Run Mode: Fetch prices", metaVar = "FETCH")
    private Boolean fetch = false;

    @Option(name = "-analyze", usage = "Run Mode: Analyze stock price trends", metaVar = "ANALYZE")
    private Boolean analyze = false;

    @Option(name = "-configFilePath", usage = "App Config file path",
        metaVar = "CONFIG_FILEPATH", required = true)
    private String configFilePath;

    @Option(name = "-symbolsFilePath", usage = "Stock symbols file path",
        metaVar = "SYM_FILEPATH")
    private String symbolsFilePath;

    @Option(name = "-startDate", usage = "Stock history start date : YYYY-MM-DD",
        metaVar = "START_DATE")
    private String startDateString;

    @Option(name = "-endDate", usage = "Stock history end date : YYYY-MM-DD",
        metaVar = "END_DATE")
    private String endDateString;

    @Option(name = "-outputDirectory", usage = "Output Directory",
        metaVar = "OUTPUT_DIR")
    private String outputDirectory;

    @Option(name = "-stockHistoryFilePath", usage = "Stock History File Path",
        metaVar = "STOCK_HISTORY_FILE_PATH")
    private String stockHistoryFilePath;

    public static void initializeInstance(String[] args)
        throws InvalidConfigurationError, IOException
    {
        if (null == instance)
        {
            instance = new Options(args);
        }
    }

    public static Options getInstance()
        throws InternalAppError
    {
        if (null == instance)
        {
            throw new InternalAppError("Tried accessing options without initializing it first.");
        }
        return instance;
    }

    private Options(String[] args)
        throws InvalidConfigurationError, IOException
    {
        CmdLineParser parser = new CmdLineParser(this);

        if (help)
        {
            parser.printUsage(System.out);
            System.exit(0);
        }

        try
        {
                parser.parseArgument(args);
        }
        catch (CmdLineException e)
        {
            String msg = "CmdLineException while reading options ";
            log.error(msg, e);
        }

        appConfig = Constants.MAPPER.readValue(new File(configFilePath), AppConfig.class);
        if (getFetch())
        {
            if (null == getSymbolsFilePath())
            {
                throw new InvalidConfigurationError("Missing argument -symbolsFilePath");
            }
            if (null == getStartDateString())
            {
                throw new InvalidConfigurationError("Missing argument -startDateString");
            }
            if (null == getEndDateString())
            {
                throw new InvalidConfigurationError("Missing argument -endDateString");
            }
            if (null == getOutputDirectory())
            {
                throw new InvalidConfigurationError("Missing argument -outputDirectory");
            }

            startDate = Constants.DATETIME_FORMATTER.parseDateTime(startDateString);
            endDate = Constants.DATETIME_FORMATTER.parseDateTime(endDateString);
        }
        else if (getAnalyze())
        {
            if (null == getStockHistoryFilePath())
            {
                throw new InvalidConfigurationError("Missing argument -stockHistoryFilePath");
            }
        }

        log.info("Options successfully read");
    }
}
