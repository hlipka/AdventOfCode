package de.hendriklipka.aoc2023.day09;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hli
 * Date: 08.12.23
 * Time: 23:37
 */
public class Day09a
{
    public static void main(String[] args)
    {
        try
        {
            int sum=AocParseUtils.getLineIntegers("2023", "day09", " ").stream().mapToInt(Day09a::nextValue).sum();
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int nextValue(List<Integer> integers)
    {
        if (integers.stream().allMatch(i->i==0))
            return 0;

        List<Integer> diffs=new ArrayList<>();
        for (int i = 0; i < integers.size() - 1; i++)
        {
            diffs.add(integers.get(i+1)-integers.get(i));
        }
        return integers.get(integers.size()-1)+nextValue(diffs);
    }
}
