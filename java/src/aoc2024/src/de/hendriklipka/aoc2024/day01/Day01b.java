package de.hendriklipka.aoc2024.day01;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day01b
{
    public static void main(String[] args)
    {
                try
        {
            final List<String> lines = AocParseUtils.getLines("2024", "day01");
            List<Integer> left=new ArrayList<>();
            List<Integer> right=new ArrayList<>();
            lines.stream().forEach(l->{parse(l,left,right);});
            long score=left.stream().mapToLong(n-> countOcc(n,right)).sum();
            System.out.println(score);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    private static long countOcc(final int n, final List<Integer> list)
    {
        return n*list.stream().filter(i-> i == n).count();
    }

    private static void parse(String line, List<Integer> left, List<Integer> right)
    {
        left.add(AocParseUtils.parseIntFromString(line, "(\\d+)\\s+.*"));
        right.add(AocParseUtils.parseIntFromString(line, ".*\\s+(\\d+)"));
    }
}
