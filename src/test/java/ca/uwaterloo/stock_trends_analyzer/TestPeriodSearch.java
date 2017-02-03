package ca.uwaterloo.stock_trends_analyzer;

import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.exceptions.InternalAppError;
import ca.uwaterloo.stock_trends_analyzer.utils.NewsExtractor;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class TestPeriodSearch
{
    @Test
    public void testGetTopResult()
        throws InterruptedException, IOException, URISyntaxException, InternalAppError
    {
        String organizationName = "Apple Inc.";
        String chromeDriverPath = "/home/v2john/Tools/chromedriver";

        NewsExtractor newsExtractor = new NewsExtractor(chromeDriverPath);
        List<String> headlines =  newsExtractor.getHeadlines(
            organizationName,
            Constants.DATETIME_FORMATTER.parseDateTime("2015-01-01"),
            Constants.DATETIME_FORMATTER.parseDateTime("2015-06-30"),
            1
        );

        newsExtractor.quitDriver();

        System.out.println(Constants.MAPPER.writeValueAsString(headlines));
    }
}
