package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 */
public class Day04a
{
    public static void main(String[] args)
    {
        try
        {
            final List<List<String>> words = AocParseUtils.getLineWords("2017", "day04", " ");
            long count = words.stream().filter(Day04a::isValidPassphrase).count();
            System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValidPassphrase(List<String> words)
    {
        return words.stream().distinct().count() == words.size();
    }
}
