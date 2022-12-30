package de.hendriklipka.aoc2020.day3;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Day032
{
    public static void main(String[] args)
    {
        try
        {
            List<String> lines = FileUtils.readLines(new File("data/day3.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            long count1=countTrees(lines, 1, 1);
            long count2=countTrees(lines, 3, 1);
            long count3=countTrees(lines, 5, 1);
            long count4=countTrees(lines, 7, 1);
            long count5=countTrees(lines, 1, 2);
            System.out.println(count1*count2*count3*count4*count5);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static long countTrees(List<String> lines, int colSlope, int rowSlope)
    {
        int lineLength = lines.get(0).length();
        int forestSize = lines.size();
        int row = 0;
        int column = 0;
        int trees = 0;
        while (row<forestSize- rowSlope)
        {
            column = (column+ colSlope) % lineLength;
            char object = lines.get(row+ rowSlope).charAt(column);
            if (object == '#')
                trees++;
            row += rowSlope;
        }
        return trees;
    }
}
