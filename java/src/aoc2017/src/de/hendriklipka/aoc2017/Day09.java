package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.List;

public class Day09 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day09().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        // examples have a score sum of 50
        return data.getLines().stream().mapToInt(Day09::countScore).sum();
    }

    private static int countScore(String line)
    {
        int pos=-1;
        int depth=0;
        int score=0;
        boolean inGarbage=false;
        while (pos<line.length()-1)
        {
            pos++;
            char c=line.charAt(pos);
            if (c == '!')
            {
                pos++;
            }
            else if (inGarbage)
            {
                if (c=='>')
                {
                    inGarbage=false;
                }
                continue;
            }
            else if (c=='{')
            {
                depth++;
                score+=depth;
            }
            else if (c=='}')
            {
                depth--;
            }
            else if (c=='<')
            {
                inGarbage=true;
            }
        }
        return score;
    }

    private static int countGarbage(String line)
    {
        int pos=-1;
        int garbage=0;
        boolean inGarbage=false;
        while (pos<line.length()-1)
        {
            pos++;
            char c=line.charAt(pos);
            if (c == '!')
            {
                pos++;
            }
            else if (inGarbage)
            {
                if (c=='>')
                {
                    inGarbage=false;
                    continue;
                }
                garbage++;
            }
            else if (c=='<')
            {
                inGarbage=true;
            }
        }
        return garbage;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return data.getLines().stream().mapToInt(Day09::countGarbage).sum();
    }
}
