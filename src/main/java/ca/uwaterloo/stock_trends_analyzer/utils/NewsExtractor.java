package ca.uwaterloo.stock_trends_analyzer.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
    private static final String CHROME_DRIVER_PATH = "/home/v2john/Tools/chromedriver";
    private static final String SEARCH_ENGINE = "https://news.google.com/news/advanced_news_search";
    private static final Long TIMEOUT_SECONDS = 10L;
    public static final DateTimeFormatter GOOGLE_FORMATTER = DateTimeFormat.forPattern("MM/dd/yyyy");

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

    public List<String> getHeadlines(String searchString, DateTime startTime, DateTime endTime, Integer pagesToExplore)
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

            WebElement occurrence_selector =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id("position-filter-select")));
            occurrence_selector.click();

            WebElement headline_selector =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id(":3")));
            headline_selector.click();

            WebElement date_range_selector =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id("date-filter-select")));
            date_range_selector.click();

            WebElement custom_range_selector =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id(":e")));
            custom_range_selector.click();

            WebElement startDateBox =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id("ansp_start-date")));
            startDateBox.sendKeys(GOOGLE_FORMATTER.print(startTime));

            WebElement endDateBox =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id("ansp_end-date")));
            endDateBox.sendKeys(GOOGLE_FORMATTER.print(endTime));

            WebElement searchButton =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id("ansp_search-button")));
            searchButton.click();

            while (pagesToExplore > 0)
            {
                log.info(pagesToExplore + " pages left to explore");

                List<WebElement> headlineElements = new ArrayList<>();

                List<WebElement> largeHeadlineElements =
                    new WebDriverWait(driver, TIMEOUT_SECONDS).
                        until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector(".l._HId"))
                        );
                headlineElements.addAll(largeHeadlineElements);

                List<WebElement> smallHeadlineElements =
                    new WebDriverWait(driver, TIMEOUT_SECONDS).
                        until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("._sQb"))
                        );
                headlineElements.addAll(smallHeadlineElements);

                for (WebElement element : headlineElements)
                {
                    if(StringUtils.isBlank(element.getText()) || element.getText().contains("..."))
                        continue;

                    articleHeadlines.add(element.getText());
                }

                WebElement nextPageButton =
                    new WebDriverWait(driver, TIMEOUT_SECONDS).
                        until(ExpectedConditions.presenceOfElementLocated(By.id("pnnext")));
                nextPageButton.click();

                --pagesToExplore;
            }

//            document.getElementsByClassName("l _HId")
//            document.getElementsByClassName("_sQb")
//            next page button id : pnnext
        }
        catch (Exception e)
        {
            log.error("Exception while processing URL extractor for string " + searchString);
            log.error(e);
        }

        return articleHeadlines;
    }
}