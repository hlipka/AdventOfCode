package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day25 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day25().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<List<Integer>> locks=new ArrayList<>();
        List<List<Integer>> keys=new ArrayList<>();
        final List<List<String>> config = data.getStringBlocks();
        config.forEach(b->parseBlock(b, locks, keys));
        int count=0;
        for (List<Integer> lock : locks)
        {
            for (List<Integer> key : keys)
            {
                if (matches(lock, key))
                    count++;
            }
        }
        return count;
    }

    private boolean matches(final List<Integer> lock, final List<Integer> key)
    {
        for (int i=0; i<lock.size(); i++)
        {
            if ((lock.get(i) + key.get(i))>5)
                return false;
        }
        return true;
    }

    private void parseBlock(final List<String> block, final List<List<Integer>> locks, final List<List<Integer>> keys)
    {
        CharMatrix data=CharMatrix.fromStringList(block, '_');
        List<Integer> parsed=new ArrayList<>(5);
        if(data.at(0, 0)=='#')
        { // lock
            for (int col=0;col<5;col++)
            {
                for (int row=1;row<7;row++)
                {
                    if (data.at(row, col)=='.')
                    {
                        parsed.add(row-1);
                        break;
                    }
                }
            }
            locks.add(parsed);
        }
        else
        {
            for (int col=0;col<5;col++)
            {
                for (int row = 6; row >= 0; row--)
                {
                    if (data.at(row, col)=='.')
                    {
                        parsed.add(5-row);
                        break;
                    }
                }
            }
            keys.add(parsed);
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return 0;
    }
}
