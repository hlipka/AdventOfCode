package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day01 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day01().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        return data.getLinesAsInt().stream().mapToInt(i->i).sum();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<Integer> changes = data.getLinesAsInt();
        Set<Integer> freqs=new HashSet<>();
        int frequency=0;
        boolean found=false;
        while (!found)
        {
            for (int change: changes)
            {
                frequency+=change;
                if (!freqs.add(frequency))
                {
                    found=true;
                    break;
                }
            }
        }
        return frequency;
    }
}
