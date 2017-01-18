package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewsExtractor
{
    private static Logger log = LogManager.getLogger(NewsExtractor.class);

    public static List<String> getHeadlines(String searchString, DateTime startDate, DateTime endDate, String section)
        throws InterruptedException, IOException, URISyntaxException
    {
        log.debug("Search term is " + searchString);

        List<String> articleHeadlines = new ArrayList<>();

        CloseableHttpClient httpclient = HttpClients.createDefault();

        URI uri =
            new URIBuilder(Constants.NEWS_ENDPOINT)
                .setParameter("api-key", Constants.GUARDIAN_API_KEY)
                .setParameter("q", searchString)
                .setParameter("from-date", startDate.toString(Constants.DATETIME_FORMATTER))
                .setParameter("to-date", endDate.toString(Constants.DATETIME_FORMATTER))
                .setParameter("page-size", "200")
                .setParameter("section", section)
                .build();

        HttpGet httpget = new HttpGet(uri);
        CloseableHttpResponse response = httpclient.execute(httpget);

        if (null == response || null == response.getEntity())
        {
            log.error("No response from the Financial News API");
        }
        else
        {
            String responseText = IOUtils.toString(response.getEntity().getContent(), Constants.DEFAULT_ENCODING);
            articleHeadlines = parseHeadlinesFromAPIResponse(responseText);
        }
        httpclient.close();

        log.debug("Extracted " + articleHeadlines.size() + " articles.");

        return articleHeadlines;
    }

    private static List<String> parseHeadlinesFromAPIResponse(String apiResponseText)
        throws IOException
    {
        List<String> headlines = new ArrayList<>();
        Map<String, Map<String, Object>> resultsMap =
            Constants.MAPPER.readValue(apiResponseText, new TypeReference<Map<String, Map<String, Object>>>(){});

        List<Map<String, String>> newsArticles = (List<Map<String, String>>) resultsMap.get("response").get("results");
        for (Map<String, String> article : newsArticles)
        {
            headlines.add(article.get("webTitle"));
        }

        return headlines;
    }
}