package ca.uwaterloo.stock_trends_analyzer;

import ca.uwaterloo.stock_trends_analyzer.utils.ArticleSearchHelper;
import org.junit.Test;

public class TestPeriodSearch
{
    @Test
    public void testGetTopResult()
        throws InterruptedException
    {
        ArticleSearchHelper searchHelper = new ArticleSearchHelper();
        searchHelper.getArticleHeadlines("BlackBerry News");
        searchHelper.quitDriver();
    }
}