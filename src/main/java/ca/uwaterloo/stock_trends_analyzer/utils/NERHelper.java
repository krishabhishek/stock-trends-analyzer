package ca.uwaterloo.stock_trends_analyzer.utils;

import edu.stanford.nlp.ie.NERClassifierCombiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class NERHelper
{
    private NERClassifierCombiner nerCombClassifier;
    private String ORG_ENTITY = "ORGANIZATION";

    private static Logger log = LogManager.getLogger(NERHelper.class);

    public NERHelper()
    {
        Properties properties = new Properties();
        this.nerCombClassifier = NERClassifierCombiner.createNERClassifierCombiner("NERHelper", properties);;
    }

    public List<String> extractEntities(String sentence)
    {

        String classify =
            nerCombClassifier.classifyToString(
                sentence, "slashTags", false
            );
        System.out.println(classify);
        List<String> parsedEntities = Arrays.asList(classify.split(" "));



        List<String> orgs = new ArrayList<>();
        for (int i = 0; i < parsedEntities.size(); i++)
        {
            String entity = parsedEntities.get(i);
            if (!StringUtils.isBlank(entity))
            {
                List<String> splitEntity = Arrays.asList(entity.split("/"));
                if (splitEntity.size() == 2 && splitEntity.get(1).equals(ORG_ENTITY))
                {
                    orgs.add(splitEntity.get(0));
                }
            }
        }

        return orgs;
    }
}
