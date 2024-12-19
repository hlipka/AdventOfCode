package de.hendriklipka.aoc2016;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.List;

public class Day18 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day18().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> config = data.getLines();
        int steps=Integer.parseInt(config.get(0));
        char[] traps=config.get(1).toCharArray();
        int safeCount=count(traps);
        for (int i=1;i<steps;i++) // we already have the first row
        {
            traps = simulate(traps);
            safeCount+=count(traps);
        }
        return safeCount;
    }

    private int count(final char[] traps)
    {
        int count=0;
        for (char c : traps)
        {
            if (c=='.')
                count++;
        }
        return count;
    }

    private char[] simulate(final char[] traps)
    {
        char[] result = new char[traps.length];
        for (int i=0;i< result.length;i++)
        {
            char left= 0==i?'.':traps[i-1];
            char right= i==result.length-1?'.':traps[i+1];
            char center= traps[i];
            if (left=='^' && center == '^' && right=='.' )
            {
                result[i]='^';
            }
            else if (left == '.' && center == '^' && right == '^')
            {
                result[i] = '^';
            }
            else if (left == '^' && center == '.' && right == '.')
            {
                result[i] = '^';
            }
            else if (left == '.' && center == '.' && right == '^')
            {
                result[i] = '^';
            }
            else
            {
                result[i]='.';
            }

        }

        return result;
    }

    @Override
    protected Object solvePartB()
    {
        // same code, but a higher number
        return null;
    }
}
