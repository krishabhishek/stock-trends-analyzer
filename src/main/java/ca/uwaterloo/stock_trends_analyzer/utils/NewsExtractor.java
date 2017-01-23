package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import ca.uwaterloo.stock_trends_analyzer.exceptions.InternalAppError;
import com.fasterxml.jackson.core.type.TypeReference;
import com.squareup.okhttp.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

public class NewsExtractor
{
    private static Logger log = LogManager.getLogger(NewsExtractor.class);

    public static Set<String> getHeadlines(
        String companyName, DateTime startDate, DateTime endDate, String queryFilePath
    )
        throws InterruptedException, IOException, URISyntaxException, InternalAppError
    {
        Set<String> articleHeadlines = new HashSet<>();

        URI uri =
            new URIBuilder(Constants.NEWS_ENDPOINT)
                .setParameter("apiKey", Constants.FT_API_KEY)
                .build();

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String payload = buildPayload(companyName, startDate, endDate, queryFilePath);

        RequestBody body = RequestBody.create(mediaType, payload);
        Request request = new Request.Builder()
            .url(uri.toString())
            .post(body)
            .addHeader("content-type", "application/json")
            .build();

        Thread.sleep(5000); // Rate-limiting
        Response response = client.newCall(request).execute();
        String queryResult = response.body().string();

        extractHeadlinesFromResult(queryResult, articleHeadlines);

        log.debug("Extracted " + articleHeadlines.size() + " articles.");

        return articleHeadlines;
    }

    private static void extractHeadlinesFromResult(String queryResult, Set<String> articleHeadlines)
    {
        Map<String, Object> responseMap = null;
        try
        {
            responseMap = Constants.MAPPER.readValue(queryResult, new TypeReference<Map<String, Object>>(){});

            List<Map<String, Object>> outerResults = (List<Map<String, Object>>) responseMap.get("results");
            if (null == outerResults || outerResults.isEmpty())
            {
                return;
            }

            List<Map<String, Object>> innerResults = (List<Map<String, Object>>) outerResults.get(0).get("results");
            if (null == innerResults || innerResults.isEmpty())
            {
                return;
            }

            String title = null;
            for (Map<String, Object> result : innerResults)
            {
                Map<String, String> resultTitle = (Map<String, String>) result.get("title");
                title = resultTitle.get("title");
                articleHeadlines.add(title);
            }
        }
        catch (IOException e)
        {
            log.error("Skipping due to error while parsing json");
        }
    }

    private static String buildPayload(String companyName, DateTime startDate, DateTime endDate,
                                       String queryTemplatePath)
        throws InternalAppError, IOException
    {
        String queryString = FileUtils.readFileToString(new File(queryTemplatePath), Charset.defaultCharset());

        StrBuilder strBuilder = new StrBuilder(queryString);
        strBuilder.replaceAll(Constants.ORG_PLACEHOLDER, companyName);
        strBuilder.replaceAll(Constants.STARTDATE_PLACEHOLDER, Constants.FT_DATETIME_FORMATTER.print(startDate));
        strBuilder.replaceAll(Constants.ENDDATE_PLACEHOLDER, Constants.FT_DATETIME_FORMATTER.print(endDate));

        return strBuilder.toString();
    }
}