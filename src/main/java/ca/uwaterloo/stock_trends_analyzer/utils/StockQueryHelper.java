package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class StockQueryHelper
{
    private static Logger log = LogManager.getLogger(StockQueryHelper.class);

    public static void getStockHistory(
        String stockSymbol, Integer startMonth, Integer startDay, Integer startYear,
        Integer endMonth, Integer endDay, Integer endYear, String outputDirectory
    )
        throws IOException
    {
        StrBuilder strBuilder = new StrBuilder(Constants.STOCK_PRICE_ENDPOINT);
        strBuilder.replaceAll(Constants.SYMBOL_PLACEHOLDER, stockSymbol);
        strBuilder.replaceAll(Constants.START_MONTH_PLACEHOLDER, String.valueOf(startMonth));
        strBuilder.replaceAll(Constants.START_DAY_PLACEHOLDER, String.valueOf(startDay));
        strBuilder.replaceAll(Constants.START_YEAR_PLACEHOLDER, String.valueOf(startYear));
        strBuilder.replaceAll(Constants.END_MONTH_PLACEHOLDER, String.valueOf(endMonth));
        strBuilder.replaceAll(Constants.END_DAY_PLACEHOLDER, String.valueOf(endDay));
        strBuilder.replaceAll(Constants.END_YEAR_PLACEHOLDER, String.valueOf(endYear));

        String endpoint = strBuilder.toString();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(endpoint);
        CloseableHttpResponse response = httpclient.execute(httpGet);

        String filepath = outputDirectory + Constants.STOCKHISTORY_FILE_PREFIX + stockSymbol;
        FileUtils.copyInputStreamToFile(response.getEntity().getContent(), new File(filepath));
    }

    public static String getCompanyName(String stockHistoryFilePath, String mappingFilePath)
        throws IOException
    {
        String stockSymbol = stockHistoryFilePath.split(Constants.STOCKHISTORY_FILE_PREFIX)[1];

        Map<String, String> stockSymbolMap =
            Constants.MAPPER.readValue(new File(mappingFilePath), new TypeReference<Map<String, String>>()
            {
            });

        return stockSymbolMap.get(stockSymbol);
    }
}
