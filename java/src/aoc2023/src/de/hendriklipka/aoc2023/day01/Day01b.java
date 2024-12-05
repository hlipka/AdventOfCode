package de.hendriklipka.aoc2023.day01;

import de.hendriklipka.aoc.AocDataFileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 00:00
 */
public class Day01b
{
    static final Map<String, String> digitMap=new HashMap<>(Map.of(
            "one","1",
            "two","2",
            "three","3",
            "four","4",
            "five","5",
            "six","6",
            "seven","7",
            "eight","8",
            "nine","9"
    ));
    static final Set<String> digits = digitMap.keySet();

    public static void main(String[] args)
    {
        for (int i=1;i<10;i++)
        {
            digitMap.put(Integer.toString(i), Integer.toString(i));
        }
        try
        {
            final List<String> lines = AocDataFileUtils.getLines("2023", "day01");
            int sum = lines.stream().mapToInt(Day01b::lineToNum).sum();
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int lineToNum(final String line)
    {
        String first = "";
        String last = "";
        for (int i=0;i<line.length();i++)
        {
            String d=getDigit(line.substring(i));
            if (null!=d)
            {
                last = d;
                if (first.isEmpty())
                    first = d;

            }
        }
        return Integer.parseInt(first + last);
    }

    private static String getDigit(final String substring)
    {
        for (String d: digits)
        {
            if (substring.startsWith(d))
            {
                return digitMap.get(d);
            }
        }
        return null;
    }
}
