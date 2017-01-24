package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.beans.LabeledHeadline;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
                LabeledHeadline labeledHeadline = new LabeledHeadline(label, companyName, sentence);
                bufferedWriter.write(Constants.MAPPER.writeValueAsString(labeledHeadline));
                bufferedWriter.newLine();
            }
        }

        bufferedWriter.close();
        fileWriter.close();
    }
}
