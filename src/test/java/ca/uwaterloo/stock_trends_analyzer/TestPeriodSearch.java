package ca.uwaterloo.stock_trends_analyzer;

import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.exceptions.InternalAppError;
import ca.uwaterloo.stock_trends_analyzer.utils.NewsExtractor;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

public class TestPeriodSearch
{
    @Test
    public void testGetTopResult()
        throws InterruptedException, IOException, URISyntaxException, InternalAppError
    {
        String organizationName = "Apple Inc.";
        String queryFilePath = "/home/v2john/Projects/stock-trends-analyzer/src/main/resources/ft_query.json";

        Set<String> headlines =  NewsExtractor.getHeadlines(
            organizationName,
            Constants.DATETIME_FORMATTER.parseDateTime("2015-01-01")
                .toDateTime(DateTimeZone.UTC)
                .withTimeAtStartOfDay(),
            Constants.DATETIME_FORMATTER.parseDateTime("2015-06-30")
                .toDateTime(DateTimeZone.UTC)
                .withTimeAtStartOfDay(),
            queryFilePath
        );

        System.out.println(headlines);
    }
}
