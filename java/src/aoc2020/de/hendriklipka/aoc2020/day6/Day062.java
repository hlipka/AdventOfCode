package de.hendriklipka.aoc2020.day6;

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
public class Day062
{
    public static void main(String[] args)
    {
        try
        {
            Iterator<String> lines = FileUtils.readLines(new File("data/day6.txt"), StandardCharsets.UTF_8).listIterator();
            List<int[]> answerGroups = new ArrayList<>();
            int[] currentGroup = new int[27];
            while (lines.hasNext())
            {
                String line = lines.next();
                if (StringUtils.isEmpty(line))
                {
                    answerGroups.add(currentGroup);
                    currentGroup = new int[27];
                    continue;
                }
                parseLine(line, currentGroup);
                currentGroup[26]++;
            }
            answerGroups.add(currentGroup);
            System.out.println(answerGroups.stream().map(Day062::answerCount).reduce((l1, l2) -> l1+l2).get());

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static long answerCount(final int[] counts)
    {
        return Arrays.stream(counts).filter(i -> i== counts[26]).count()-1;
    }

    private static void parseLine(final String line, final int[] group)
    {
        line.chars().forEach(i -> group[i-'a']++);
    }
}
