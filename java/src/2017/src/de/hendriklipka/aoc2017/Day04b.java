package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocStringUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 */
public class Day04b
{
    public static void main(String[] args)
    {
        try
        {
            final List<List<String>> words = AocDataFileUtils.getLineWords("2017", "day04", " ");
            long count = words.stream().map(Day04b::sortWords).filter(Day04b::isValidPassphrase).count();
            System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static List<String> sortWords(List<String> words)
    {
        return words.stream().map(AocStringUtils::sortWord).toList();
    }

    private static boolean isValidPassphrase(List<String> words)
    {
        return words.stream().distinct().count() == words.size();
    }
}
