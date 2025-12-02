package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.List;

public class Day01 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day01().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<Integer> moves= data.getLines().stream().map(l->l.replace('L','-').replace("R","")).map(Integer::parseInt).toList();
        int dial=50;
        int count=0;
        for (Integer i : moves)
        {
            dial = (dial+i)%100;
            if (0==dial)
            {
                count++;
            }
        }
        return count;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<Integer> moves = data.getLines().stream().map(l -> l.replace('L', '-').replace("R", "")).map(Integer::parseInt).toList();
        int dial = 50;
        int count = 0;
        for (Integer i : moves)
        {
            for (int j = 0; j < Math.abs(i); j++)
            {
                dial = (dial + (int) Math.signum(i)) % 100;
                if (0 == dial)
                {
                    count++;
                }
            }
        }
        return count;
    }
}
