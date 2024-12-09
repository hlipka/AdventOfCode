package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocDataFileUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 */
public class Day02a
{
    public static void main(String[] args)
    {
        try
        {
            final List<List<Integer>> sheet = AocDataFileUtils.getLineIntegers("2017", "day02", "\t");
            int sum = sheet.stream().mapToInt(Day02a::getDiff).sum();
            System.out.println(sum  );
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int getDiff(List<Integer> row)
    {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (Integer num: row)
        {
            if (num<min) min = num;
            if (num>max) max = num;
        }
        return max-min;
    }
}
