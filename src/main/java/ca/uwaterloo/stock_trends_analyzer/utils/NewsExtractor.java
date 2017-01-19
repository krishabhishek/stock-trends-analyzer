package ca.uwaterloo.stock_trends_analyzer.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class NewsExtractor
{
    private static Logger log = LogManager.getLogger(NewsExtractor.class);

    private static final String WEB_DRIVER_PROPERTY = "webdriver.chrome.driver";
    private static final String CHROME_DRIVER_PATH = "/home/v2john/Tools/selenium/chromedriver";
    private static final String SEARCH_ENGINE = "https://news.google.com/news/advanced_news_search";
    private static final Long TIMEOUT_SECONDS = 10L;

    private WebDriver driver = null;

    public NewsExtractor()
    {
        System.setProperty(WEB_DRIVER_PROPERTY, CHROME_DRIVER_PATH);
        driver = new ChromeDriver();
    }

    public void quitDriver()
    {
        driver.quit();
    }

    public List<String> getHeadlines(String searchString, DateTime startTime, DateTime endTime, String section)
        throws InterruptedException
    {
        List<String> articleHeadlines = new ArrayList<>();

        try
        {
            driver.get(SEARCH_ENGINE);

            WebElement keywordBox =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id("all-keyword-input")));

            keywordBox.sendKeys(searchString);

        }
        catch (Exception e)
        {
            log.error("Exception while processing URL extractor for string " + searchString);
            log.error(e);
        }

        return articleHeadlines;
    }
}