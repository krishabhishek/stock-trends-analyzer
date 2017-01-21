package ca.uwaterloo.stock_trends_analyzer.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileHelper
{
    public static void writeNewsToFile(String companyName, File outputFile, Map<String, List<String>> labeledSentences)
            throws IOException
    {
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(outputFile,true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for (String label : labeledSentences.keySet())
        {
            List<String> sentences = labeledSentences.get(label);
            for (String sentence : sentences)
            {
                bufferedWriter.write(label + ":" + companyName + ":" + sentence +"\n");
            }
        }

        bufferedWriter.close();
        fileWriter.close();
    }
}