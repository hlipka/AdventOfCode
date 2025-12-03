package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.List;

public class Day03 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day03().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<List<Integer>> banks = data.getLinesAsDigits();
        return banks.stream().mapToInt(Day03::getJoltage2).sum();
    }

    private static int getJoltage2(List<Integer> batts)
    {
        int j1=0;
        int pos1=0;
        int j2=0;
        // get the largest value from the first n-1 batteries
        for (int i=0; i<batts.size()-1; i++)
        {
            if (batts.get(i)>j1)
            {
                pos1=i;
                j1=batts.get(i);
            }
        }
        // now find the largest number of the remaining batteries
        for (int i=pos1+1; i<batts.size(); i++)
        {
            if (batts.get(i)>j2)
            {
                j2=batts.get(i);
            }
        }
        return 10*j1+j2;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<List<Integer>> banks = data.getLinesAsDigits();
        return banks.stream().mapToLong(Day03::getJoltage12).sum();
    }

    private static long getJoltage12(List<Integer> batteries)
    {
        return getTotalJoltage(batteries, 0, 11, 0);
    }

    // generic version of part 1, with recursive descent
    private static long getTotalJoltage(final List<Integer> batteries, final int startFrom, final int skip, final long currentJoltage)
    {
        if (-1==skip)
            return currentJoltage;
        int j=-1;
        int pos=-1;
        for (int i=startFrom; i<batteries.size()-skip; i++)
        {
            if (batteries.get(i)>j)
            {
                j=batteries.get(i);
                pos=i;
            }
        }
        return getTotalJoltage(batteries, pos+1, skip-1, 10L*currentJoltage+(long)j);
    }
}
