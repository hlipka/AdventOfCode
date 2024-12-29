package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.List;

public class Day15 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day15().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> config=data.getLines();
        long genA= AocParseUtils.parseLongFromString(config.get(0),"Generator A starts with (\\d+)");
        long genB= AocParseUtils.parseLongFromString(config.get(1),"Generator B starts with (\\d+)");

        int count=0;
        for (int r=0;r<40000000;r++)
        {
            genA=doRound(genA, 16807L);
            genB=doRound(genB, 48271L);
            if ((genA&0xffff)==(genB&0xffff))
            {
                count++;
            }
        }

        return count;
    }

    private long doRound(final long gen, final long factor)
    {
        return (gen*factor)% 2147483647L;
    }

    private long doRound2(final long gen, final long factor, final long div)
    {
        long next=gen;
        do
        {
            next=(next * factor) % 2147483647L;
        }
        while (0!=(next%div));
        return next;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<String> config = data.getLines();
        long genA = AocParseUtils.parseLongFromString(config.get(0), "Generator A starts with (\\d+)");
        long genB = AocParseUtils.parseLongFromString(config.get(1), "Generator B starts with (\\d+)");

        int count = 0;
        for (int r = 0; r < 5000000; r++)
        {
            genA = doRound2(genA, 16807L, 4L);
            genB = doRound2(genB, 48271L, 8L);
            if ((genA & 0xffff) == (genB & 0xffff))
            {
                count++;
            }
        }

        return count;
    }
}
