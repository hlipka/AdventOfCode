package de.hendriklipka.aoc2020.day6;

import de.hendriklipka.aoc2020.day4.Day041;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * User: hli
 * Date: 06.12.20
 * Time: 17:04
 */
public class Day061
{
    public static void main(String[] args)
    {
        try
        {
            Iterator<String> lines = FileUtils.readLines(new File("data/day6.txt"), StandardCharsets.UTF_8).listIterator();
            List<int[]> answerGroups = new ArrayList<>();
            int[] currentGroup = new int[26];
            while (lines.hasNext())
            {
                String line = lines.next();
                if (StringUtils.isEmpty(line))
                {
                    answerGroups.add(currentGroup);
                    currentGroup = new int[26];
                }
                parseLine(line, currentGroup);
            }
            answerGroups.add(currentGroup);
            System.out.println(answerGroups.stream().map(Day061::answerCount).reduce(Long::sum).get());

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static long answerCount(final int[] counts)
    {
        return Arrays.stream(counts).filter(i -> i>0).count();
    }

    private static void parseLine(final String line, final int[] group)
    {
        line.chars().forEach(i -> group[i-'a']++);
    }
}
