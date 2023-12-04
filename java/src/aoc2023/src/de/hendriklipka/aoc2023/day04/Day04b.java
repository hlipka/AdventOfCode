package de.hendriklipka.aoc2023.day04;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 00:00
 */
public class Day04b
{
    public static void main(String[] args)
    {
        try
        {
            List<String> lines = AocParseUtils.getLines("2023", "day04");
            List<Integer> lineCount = new ArrayList<>(Collections.nCopies(lines.size(), 1));
            calculateScore(lines, lineCount);
            System.out.println(lineCount.stream().mapToInt(Integer::intValue).sum());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void calculateScore(List<String> lines, List<Integer> lineCount) {
        for (int i=0;i<lines.size();i++)
        {
            List<String> parts = AocParseUtils.parsePartsFromString(lines.get(i), "Card\\s+(\\d+): ([\\d ]+) \\| ([\\d ]+)");
            Set<Integer> winning = Arrays.stream(StringUtils.split(parts.get(1), " ",-1)).map(Integer::parseInt).collect(Collectors.toSet());
            Set<Integer> numbers = Arrays.stream(StringUtils.split(parts.get(2), " ",-1)).map(Integer::parseInt).collect(Collectors.toSet());
            int winningCards = (int)numbers.stream().filter(winning::contains).count();
            int currentCardCount=lineCount.get(i);
            for (int j=0;j<winningCards;j++)
            {
                if ((i+j+1)<lineCount.size())
                    lineCount.set(i+j+1, lineCount.get(i+j+1)+currentCardCount);
            }
        }
    }
}
