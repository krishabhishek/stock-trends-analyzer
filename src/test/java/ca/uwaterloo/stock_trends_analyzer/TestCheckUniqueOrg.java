package ca.uwaterloo.stock_trends_analyzer;

import ca.uwaterloo.stock_trends_analyzer.utils.NERHelper;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestCheckUniqueOrg
{
    @Test
    public void checkUniqueOrg()
        throws IOException
    {
        NERHelper nerHelper = new NERHelper();

        String sentence = "Microsoft Inc. seek exception program on Trump immigration orders, and so does Apple Inc";
        List<String> orgs = nerHelper.extractEntities(sentence,"corpus-1", "1");

        System.out.println("The orgs are " + orgs);
    }
}
