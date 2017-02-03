package ca.uwaterloo.stock_trends_analyzer.utils;

import ca.uwaterloo.stock_trends_analyzer.beans.LabeledHeadline;
import ca.uwaterloo.stock_trends_analyzer.constants.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class FileHelper
{
    private static Logger log = LogManager.getLogger(FileHelper.class);

    public static void writeNewsToFile(File outputFile, Set<LabeledHeadline> labeledHeadlines)
        throws IOException
    {
        if (!outputFile.exists())
        {
            outputFile.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(outputFile, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        labeledHeadlines.forEach(headline ->
            {
                try
                {
                    bufferedWriter.write(Constants.MAPPER.writeValueAsString(headline));
                    bufferedWriter.newLine();
                } catch (IOException e)
                {
                    log.error("Skipped adding entry to file: " + headline);
                }
            }
        );

        bufferedWriter.close();
        fileWriter.close();
    }
}
