package de.hendriklipka.aoc2024.day01;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

public class Day01a
{
    public static void main(String[] args)
    {
                try
        {
            final List<List<String>> lines = AocParseUtils.getLinesAsCharStrings("2023", "day01");
            System.out.println(42);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }
}
