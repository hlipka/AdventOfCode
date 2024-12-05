package de.hendriklipka.aoc2024.day01;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day01a
{
    public static void main(String[] args)
    {
                try
        {
            final List<String> lines = AocDataFileUtils.getLines("2024", "day01");
            List<Integer> left=new ArrayList<>();
            List<Integer> right=new ArrayList<>();
            lines.stream().forEach(l->{parse(l,left,right);});
            left.sort(Integer::compareTo);
            right.sort(Integer::compareTo);
            long sum=0;
            for(int i=0; i<left.size(); i++)
            {
                int d=Math.abs(left.get(i)-right.get(i));
                sum+=d;
            }
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

    }

    private static void parse(String line, List<Integer> left, List<Integer> right)
    {
        left.add(AocParseUtils.parseIntFromString(line, "(\\d+)\\s+.*"));
        right.add(AocParseUtils.parseIntFromString(line, ".*\\s+(\\d+)"));
    }
}
