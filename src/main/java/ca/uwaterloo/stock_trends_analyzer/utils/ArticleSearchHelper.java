package ca.uwaterloo.stock_trends_analyzer.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.internal.MouseAction;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ArticleSearchHelper
{
    private Logger log = LogManager.getLogger(ArticleSearchHelper.class);

    private static final String WEB_DRIVER_PROPERTY = "webdriver.chrome.driver";
    private static final String CHROME_DRIVER_PATH = "/home/v2john/Tools/selenium/chromedriver";
    private static final String SEARCH_ENGINE = "http://www.google.com";
    private static final String SEARCH_BOX_ELEMENT = "q";
    private static final String LINE_BREAK = "\n";
    private static final Integer SEARCH_TIMEOUT_SECONDS = 10;
    private static final String RESULTS_XPATH = "//*[@id='rso']//h3/a";
    private static final String RESULT_URL_ELEMENT = "href";
    private static final String DATE_ADDENDUM =
        "&tbs=cdr%3A1%2Ccd_min%3A" +
        "2014-05-01" +
        "%2Ccd_max%3A" +
        "2016-08-31";

    private WebDriver driver = null;

    public ArticleSearchHelper()
    {
        System.setProperty(WEB_DRIVER_PROPERTY, CHROME_DRIVER_PATH);
        driver = new ChromeDriver();
    }

    public void quitDriver()
    {
        driver.quit();
    }

    public List<String> getArticleHeadlines(String searchString)
        throws InterruptedException
    {
        List<String> articleHeadlines = new ArrayList<>();

        try
        {
            driver.get(SEARCH_ENGINE);
            WebElement element = driver.findElement(By.name(SEARCH_BOX_ELEMENT));
            element.clear();
            element.sendKeys(searchString + LINE_BREAK);
            element.submit();

            String url = driver.getCurrentUrl();

            url = url + DATE_ADDENDUM;
            while (true)
            {
                try
                {
                    driver.get(url);
                    break;
                }
                catch(Exception e)
                {
                    log.debug(e);
                }
            }

            while (true)
            {
                try
                {
                    List<WebElement> findElements =
                        (new WebDriverWait(driver, 100))
                        .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(RESULTS_XPATH)));
                    for (WebElement webElement : findElements)
                    {
                        articleHeadlines.add(webElement.getText());
                    }
                    break;
                }
                catch(Exception e)
                {
                    log.debug(e);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception while processing URL extractor for string " + searchString);
            log.error(e);
        }

        return articleHeadlines;
    }
}