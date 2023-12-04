package de.hendriklipka.aoc2023.day04;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 00:00
 */
public class Day04a
{
    public static void main(String[] args)
    {
        try
        {
            int score = AocParseUtils.getLines("2023", "day04").stream().mapToInt(Day04a::calculateScore).sum();
            System.out.println(score);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int calculateScore(String line) {
        List<String> parts = AocParseUtils.parsePartsFromString(line, "Card\\s+(\\d+): ([\\d ]+) \\| ([\\d ]+)");
        Set<Integer> winning = Arrays.stream(StringUtils.split(parts.get(1), " ",-1)).map(Integer::parseInt).collect(Collectors.toSet());
        Set<Integer> numbers = Arrays.stream(StringUtils.split(parts.get(2), " ",-1)).map(Integer::parseInt).collect(Collectors.toSet());
        long count = numbers.stream().filter(winning::contains).count();
        return (int)Math.pow(2,count-1);
    }
}
