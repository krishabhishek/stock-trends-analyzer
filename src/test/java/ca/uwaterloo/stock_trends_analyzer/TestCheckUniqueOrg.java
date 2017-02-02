package ca.uwaterloo.stock_trends_analyzer;

import ca.uwaterloo.stock_trends_analyzer.utils.NERHelper;
import org.junit.Test;

import java.util.List;

public class TestCheckUniqueOrg
{
    @Test
    public void checkUniqueOrgStanfordCoreNLP()
    {
        NERHelper nerHelper = new NERHelper();

        String sentence = "Microsoft Inc. seek exception program on Trump immigration orders, and so does Apple Inc";
        List<String> orgs = nerHelper.extractEntities(sentence);

        System.out.println("The orgs are " + orgs);
    }
}
