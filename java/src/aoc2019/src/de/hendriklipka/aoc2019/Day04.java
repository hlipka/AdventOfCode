package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.List;

public class Day04 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day04().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> input = data.getLines();
        int from = AocParseUtils.parseIntFromString(input.get(0), "(\\d+)-\\d+");
        int to = AocParseUtils.parseIntFromString(input.get(0), "\\d+-(\\d+)");
        int count=0;
        for (int i=from; i<=to; i++)
        {
            if (isValid(i))
            {
                count++;
            }
        }
        return count;
    }

    private boolean isValid(final int num)
    {
        char[] s=Integer.toString(num).toCharArray();
        boolean hasPair=false;
        boolean noDecrease=true;
        for (int i=0;i<5;i++)
        {
            if (s[i]==s[i+1])
            {
                hasPair=true;
            }
            if (s[i+1]<s[i])
            {
                noDecrease=false;
            }
        }
        return hasPair && noDecrease;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<String> input = data.getLines();
        int from = AocParseUtils.parseIntFromString(input.get(0), "(\\d+)-\\d+");
        int to = AocParseUtils.parseIntFromString(input.get(0), "\\d+-(\\d+)");
        int count = 0;
        for (int i = from; i <= to; i++)
        {
            if (isValid2(i))
            {
                count++;
            }
        }
        return count;
    }

    private boolean isValid2(final int num)
    {
        char[] s = Integer.toString(num).toCharArray();
        boolean hasPair = false;
        boolean noDecrease = true;
        for (int i = 0; i < 5; i++)
        {
            if (s[i] == s[i + 1])
            {
                // ignore when the pair is part of a larger group
                if (!((i>0 && s[i - 1] == s[i]) || (i<4 && s[i+2] == s[i])))
                {
                    hasPair = true;
                }
            }
            if (s[i + 1] < s[i])
            {
                noDecrease = false;
            }
        }
        return hasPair && noDecrease;
    }
}
