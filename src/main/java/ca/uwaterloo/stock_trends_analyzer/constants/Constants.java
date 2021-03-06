package ca.uwaterloo.stock_trends_analyzer.constants;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Constants
{
    // General
    static {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
    }
    public static final ObjectMapper MAPPER = new ObjectMapper();

    // File IO Semantics
    public static final String STOCKHISTORY_FILE_PREFIX = "STOCK_HISTORY_";
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final Integer DATETIME_INDEX = 0;
    public static final Integer CLOSING_PRICE_INDEX = 6;
    public static final Integer STOCKHISTORY_COLUMNS = 7;

    // Slope detection
    public static final Integer NUM_MONTHS_REGRESS = 3;
    public static final Integer MINIMUM_DATAPOINTS_REGRESSION = 50;
    public static final Double SLOPE_THRESHOLD = Math.pow(10, -50.0);
    public static final Integer TIME_PERIODS_TO_CONSIDER = 5;

    // Stock history API call
    public static final String SYMBOL_PLACEHOLDER = "__SYMBOL_PLACEHOLDER__";
    public static final String START_MONTH_PLACEHOLDER = "__START_MONTH_PLACEHOLDER__";
    public static final String START_DAY_PLACEHOLDER = "__START_DAY_PLACEHOLDER__";
    public static final String START_YEAR_PLACEHOLDER = "__START_YEAR_PLACEHOLDER__";
    public static final String END_MONTH_PLACEHOLDER = "__END_MONTH_PLACEHOLDER__";
    public static final String END_DAY_PLACEHOLDER = "__END_DAY_PLACEHOLDER__";
    public static final String END_YEAR_PLACEHOLDER = "__END_YEAR_PLACEHOLDER__";
    public static final String STOCK_PRICE_ENDPOINT =
        "http://ichart.finance.yahoo.com/table.csv?s=" + SYMBOL_PLACEHOLDER +
        "&a=" + START_MONTH_PLACEHOLDER +
        "&b=" + START_DAY_PLACEHOLDER +
        "&c=" + START_YEAR_PLACEHOLDER +
        "&d=" + END_MONTH_PLACEHOLDER +
        "&e=" + END_DAY_PLACEHOLDER +
        "&f=" + END_YEAR_PLACEHOLDER +
        "&g=d&ignore=.csv";

    // Financial news APi
    public static final String FT_API_KEY = "ft8zeua8xkpu55be6ye63wmv";
    public static final String NEWS_ENDPOINT = "http://api.ft.com/content/search/v1";
    public static final String ORG_PLACEHOLDER = "__ORG_PLACEHOLDER__";
    public static final String STARTDATE_PLACEHOLDER = "__START_DATE_PLACEHOLDER__";
    public static final String ENDDATE_PLACEHOLDER = "__END_DATE_PLACEHOLDER__";
    public static final DateTimeFormatter FT_DATETIME_FORMATTER =
        DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
}
