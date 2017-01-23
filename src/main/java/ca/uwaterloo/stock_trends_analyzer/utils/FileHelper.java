package ca.uwaterloo.stock_trends_analyzer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class FileHelper
{
    public static void writeNewsToFile(String companyName, File outputFile, Map<String, Set<String>> labeledSentences)
        throws IOException
    {
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(outputFile,true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for (String label : labeledSentences.keySet())
        {
            Set<String> sentences = labeledSentences.get(label);
            for (String sentence : sentences)
            {
                bufferedWriter.write(label + ":" + companyName + ":" + sentence +"\n");
            }
        }

        bufferedWriter.close();
        fileWriter.close();
    }
}
