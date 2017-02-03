package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.exceptions.InternalAppError;
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
import java.util.concurrent.ThreadLocalRandom;

public class NewsExtractor
{
    private static final String WEB_DRIVER_PROPERTY = "webdriver.chrome.driver";
    private static final String SEARCH_ENGINE = "https://news.google.com/news/advanced_news_search";
    private static final Long TIMEOUT_SECONDS = 10L;
    private static final DateTimeFormatter GOOGLE_FORMATTER = DateTimeFormat.forPattern("MM/dd/yyyy");
    private static Logger log = LogManager.getLogger(NewsExtractor.class);
    private static WebDriver driver = null;

    public static void initialize(String chromeDriverPath)
        throws InternalAppError
    {
        System.setProperty(WEB_DRIVER_PROPERTY, chromeDriverPath);
        driver = new ChromeDriver();
    }

    public static void destroyInstance()
    {
        driver.quit();
        driver = null;
    }

    public static List<String> getHeadlines(String searchString, DateTime startTime, DateTime endTime,
                                            Integer pagesToExplore)
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

            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));

            WebElement occurrence_selector =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id("position-filter-select")));
            occurrence_selector.click();

            Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1000));

            WebElement headline_selector =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id(":3")));
            headline_selector.click();

            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));

            WebElement date_range_selector =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id("date-filter-select")));
            date_range_selector.click();

            Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1000));

            WebElement custom_range_selector =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id(":e")));
            custom_range_selector.click();

            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));

            WebElement startDateBox =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id("ansp_start-date")));
            startDateBox.sendKeys(GOOGLE_FORMATTER.print(startTime));

            Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1000));

            WebElement endDateBox =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id("ansp_end-date")));
            endDateBox.sendKeys(GOOGLE_FORMATTER.print(endTime));

            Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1000));

            WebElement searchButton =
                new WebDriverWait(driver, TIMEOUT_SECONDS).
                    until(ExpectedConditions.presenceOfElementLocated(By.id("ansp_search-button")));
            searchButton.click();

            while (pagesToExplore > 0)
            {
                List<WebElement> headlineElements = new ArrayList<>();

                Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 4000));
                try
                {
                    List<WebElement> largeHeadlineElements =
                        new WebDriverWait(driver, TIMEOUT_SECONDS).
                            until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                                By.cssSelector(".l._HId"))
                            );
                    headlineElements.addAll(largeHeadlineElements);
                } catch (Exception e)
                {
                    log.error("Failed for large elements ", e);
                }

                try
                {
                    List<WebElement> smallHeadlineElements =
                        new WebDriverWait(driver, TIMEOUT_SECONDS).
                            until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                                By.cssSelector("._sQb"))
                            );
                    headlineElements.addAll(smallHeadlineElements);
                } catch (Exception e)
                {
                    log.error("Failed for small elements", e);
                }

                for (WebElement element : headlineElements)
                {
                    try
                    {
                        String headline = element.getText();

                        if (StringUtils.isBlank(headline) || headline.contains("...") ||
                            !headline.contains(searchString))
                            continue;

                        articleHeadlines.add(headline);
                    } catch (Exception e)
                    {
                        log.error("Element add error", e);
                    }
                }

                if (pagesToExplore > 1)
                {
                    try
                    {
                        WebElement nextPageButton =
                            new WebDriverWait(driver, TIMEOUT_SECONDS).
                                until(ExpectedConditions.presenceOfElementLocated(By.id("pnnext")));
                        nextPageButton.click();
                    } catch (Exception e)
                    {
                        log.error("Failed to trace the page next button", e);
                    }
                }

                --pagesToExplore;
            }
        } catch (Exception e)
        {
            log.error("Exception while processing URL extractor for string " + searchString);
            log.error(e);
        }

        return articleHeadlines;
    }
}
