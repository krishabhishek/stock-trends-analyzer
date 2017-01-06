package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.beans.AppConfig;
import ca.uwaterloo.stock_trends_analyzer.exceptions.InternalAppError;
import ca.uwaterloo.stock_trends_analyzer.exceptions.InvalidConfigurationError;
import lombok.Getter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;

@ToString
@Getter
public class Options
{
    private static Options instance;
    private AppConfig appConfig;

    private static Logger log = LogManager.getLogger(Options.class);

    @Option(name = "-help", usage = "help", metaVar = "HELP")
    private Boolean help = false;

    @Option(name = "-configFilePath", usage = "App Config file path", metaVar = "CONFIG_FILEPATH", required = true)
    private String configFilePath;

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
            log.error("CmdLineException while reading options", e);
            throw new InvalidConfigurationError(
                "CmdLineException while reading options", e
            );
        }

        log.debug("configFilePath: " + getConfigFilePath());
        appConfig = Constants.MAPPER.readValue(new File(getConfigFilePath()), AppConfig.class);
        log.debug("appConfig: " + appConfig);

        log.info("Options successfully read");
    }
}
