package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.Arrays;

public class Day02 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day02().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        String ranges=data.getLines().getFirst();
        return Arrays.stream(ranges.split(",")).mapToLong(Day02::sumInvalidHalf).sum();
    }

    private static long sumInvalidHalf(String range)
    {
        String[] parts=range.split("-");
        long from=Long.parseLong(parts[0]);
        long to=Long.parseLong(parts[1]);
        long sum=0;
        for (long l=from; l<=to; l++)
            if (isInvalidHalf(l))
            {
                sum+=l;
            }
        return sum;
    }

    private static boolean isInvalidHalf(final long num)
    {
        String s=Long.toString(num);
        int len=s.length();
        if (1==len%2)
            return false;
        char[] ca=s.toCharArray();
        for (int i=0;i<len/2;i++)
        {
            if (ca[i]!=ca[i+len/2])
                return false;
        }
        return true;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        String ranges = data.getLines().getFirst();
        return Arrays.stream(ranges.split(",")).mapToLong(Day02::sumInvalidFull).sum();
    }

    private static long sumInvalidFull(String range)
    {
        String[] parts = range.split("-");
        long from = Long.parseLong(parts[0]);
        long to = Long.parseLong(parts[1]);
        long sum = 0;
        for (long l = from; l <= to; l++)
        {
            if (isInvalidFull(l))
            {
                sum += l;
            }
        }
        return sum;
    }

    private static boolean isInvalidFull(final long num)
    {
        String s = Long.toString(num);
        final var ca = s.toCharArray();
        int sLen = s.length();
        // we can have any pattern length from 1 to len/2, so check them all
        for (int currLen=1;currLen<=sLen/2;currLen++)
        {
            if (isInvalidForLength(currLen, sLen, ca))
                return true;
        }
        return false;
    }

    private static boolean isInvalidForLength(final int currLen, int sLen, final char[] ca)
    {
        // does the pattern fir the string length?
        if (0 != sLen % currLen)
            return false;
        int repeats = sLen / currLen;
        for (int pos = 0; pos < currLen; pos++)
        {
            // check for all repeats of the first occurrence whether they match as well
            for (int j = 1; j < repeats; j++)
            {
                if (ca[pos]!=ca[pos+j*currLen])
                    return false;
            }
        }
        return true;
    }
}
