package ca.uwaterloo.stock_trends_analyzer;

import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.utils.NewsExtractor;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class TestPeriodSearch
{
    @Test
    public void testGetTopResult()
        throws InterruptedException, IOException, URISyntaxException
    {
        String organizationName = "Apple Inc.";

        NewsExtractor newsExtractor = new NewsExtractor();
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
