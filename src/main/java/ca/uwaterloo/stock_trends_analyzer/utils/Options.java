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

@ToString
public class Options
{

    private static Options instance;
    @Getter private AppConfig appConfig;
    @Getter private DateTime startDate;
    @Getter private DateTime endDate;

    private static Logger log = LogManager.getLogger(Options.class);

    @Option(name = "-help", usage = "help",
            metaVar = "HELP")
    private Boolean help = false;

    @Option(name = "-configFilePath", usage = "App Config file path",
            metaVar = "CONFIG_FILEPATH", required = true)
    private String configFilePath;

    @Option(name = "-symbolsFilePath", usage = "Stock symbols file path",
            metaVar = "SYM_FILEPATH", required = true)
    @Getter private String symbolsFilePath;

    @Option(name = "-startDate", usage = "Stock history start date : YYYY-MM-DD",
            metaVar = "START_DATE", required = true)
    private String startDateString;

    @Option(name = "-endDate", usage = "Stock history end date : YYYY-MM-DD",
            metaVar = "END_DATE", required = true)
    private String endDateString;

    @Option(name = "-outputDirectory", usage = "Output Directory",
        metaVar = "OUTPUT_DIR", required = true)
    @Getter private String outputDirectory;

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
            throw new InvalidConfigurationError(msg, e);
        }

        log.debug("configFilePath: " + configFilePath);
        appConfig = Constants.MAPPER.readValue(new File(configFilePath), AppConfig.class);

        startDate = Constants.DATETIME_FORMATTER.parseDateTime(startDateString);
        endDate = Constants.DATETIME_FORMATTER.parseDateTime(endDateString);

        log.info("Options successfully read");
    }
}
