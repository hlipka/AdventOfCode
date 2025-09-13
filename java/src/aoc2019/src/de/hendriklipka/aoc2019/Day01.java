package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;

public class Day01 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day01().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        return data.getLinesAsInt().stream().mapToInt(i -> i / 3 - 2).sum();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return data.getLinesAsInt().stream().mapToInt(Day01::getFuel).sum();
    }

    private static int getFuel(final int mass)
    {
        final int fuel = mass / 3 - 2;
        if (fuel>0)
        {
            return fuel + getFuel(fuel);
        }
        return 0;
    }
}
