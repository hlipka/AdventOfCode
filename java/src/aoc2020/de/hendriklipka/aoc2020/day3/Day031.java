package de.hendriklipka.aoc2020.day3;

import de.hendriklipka.aoc2020.day2.Day022;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Day031
{
    public static void main(String[] args)
    {
        try
        {
            List<String> lines = FileUtils.readLines(new File("data/day3.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            long count=countTrees(lines);
            System.out.println(count);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static long countTrees(List<String> lines)
    {
        int lineLength = lines.get(0).length();
        int forestSize = lines.size();
        int row = 0;
        int column = 0;
        int trees = 0;
        while (row<forestSize-1)
        {
            column = (column+3) % lineLength;
            char object = lines.get(row+1).charAt(column);
            if (object == '#')
                trees++;
            row ++;
        }
        return trees;
    }
}
