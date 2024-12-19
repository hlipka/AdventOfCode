package de.hendriklipka.aoc2016;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class Day20 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day20().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<List<Long>> ranges=data.getLineLongs("-");
        ranges.remove(0); // skip the range information, we need it only for part 2
        // sort by the start value of the ranges
        ranges.sort(Comparator.comparingLong(o -> o.get(0)));

        long firstIP=0;
        int idx=0;
        while (idx<ranges.size())
        {
            List<Long> range = ranges.get(idx);
            if (firstIP < range.get(0))
            {
                return firstIP;
            }
            // we might get ranges which are in the middle of the last one, we need to skip them
            if (firstIP<=range.get(1))
                firstIP=range.get(1)+1;
            idx++;
        }

        return -1;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        long ipCount=0;
        List<List<Long>> ranges = data.getLineLongs("-");
        long upper = ranges.get(0).get(1);
        ranges.remove(0);
        // sort by the start value of the ranges
        ranges.sort(Comparator.comparingLong(o -> o.get(0)));

        long firstIP = 0;
        int idx = 0;
        // basically the same algorithm as part A, but instead of returning the first IP
        // we add free ranges and the continue processing
        while (idx < ranges.size())
        {
            List<Long> range = ranges.get(idx);
            if (firstIP < range.get(0))
            {
                ipCount+=range.get(0) - firstIP;
            }
            // we might get ranges which are in the middle of the last one, we need to skip them
            if (firstIP <= range.get(1))
                firstIP = range.get(1) + 1;
            idx++;
        }
        ipCount+=upper-firstIP+1;
        return ipCount;
    }
}
