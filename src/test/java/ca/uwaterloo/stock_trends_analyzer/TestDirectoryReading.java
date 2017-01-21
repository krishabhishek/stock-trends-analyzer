package ca.uwaterloo.stock_trends_analyzer;

import org.junit.Test;

import java.io.File;

public class TestDirectoryReading
{
    @Test
    public void getFilesInDirectory()
    {
        String directoryPath =
            "/home/v2john/MEGA/Academic/Masters/UWaterloo/ResearchProject/" +
            "SemevalTask/semeval-2017-task-5-subtask-2/stockdata/";

        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        for (File file : files)
        {
            System.out.println(file.getAbsolutePath());
        }
    }
}
