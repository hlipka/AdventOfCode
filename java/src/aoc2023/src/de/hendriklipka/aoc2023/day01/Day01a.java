package de.hendriklipka.aoc2023.day01;

import de.hendriklipka.aoc.AocDataFileUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 00:00
 */
public class Day01a
{
    public static void main(String[] args)
    {
        try
        {
            final List<List<String>> lines = AocDataFileUtils.getLinesAsCharStrings("2023", "day01");
            int sum = lines.stream().mapToInt(Day01a::lineToNum).sum();
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int lineToNum(final List<String> l)
    {
        String first="";
        String last="";
        for (String s: l)
        {
            if (Character.isDigit(s.charAt(0)))
            {
                last=s;
                if (first.isEmpty())
                    first=s;
            }
        }
        return Integer.parseInt(first+last);
    }
}
