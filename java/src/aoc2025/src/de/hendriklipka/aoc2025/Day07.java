package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Day07 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day07().doPuzzle(args);
    }

    Map<Position, Long> memo = new HashMap<>();

    @Override
    protected Object solvePartA() throws IOException
    {
        CharMatrix dia=data.getLinesAsCharMatrix('.');
        Position start=dia.findFirst('S');
        dia.set(start,'|');
        int split=0;
        // simulate the beam(s) from top to bottom
        for (int line=1;line<dia.rows();line++)
        {
            for (int col=0;col<dia.cols();col++)
            {
                if (dia.at(line-1, col)=='|')
                {
                    if (dia.at(line, col)=='^')
                    {
                        split++;
                        if (col>0)
                        {
                            dia.set(new Position(line,col-1),'|');
                        }
                        if (col<dia.cols()-1)
                        {
                            dia.set(new Position(line,col+1),'|');
                        }
                    }
                    else
                    {
                        dia.set(new Position(line,col),'|');
                    }
                }
            }
        }
        return split;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CharMatrix dia = data.getLinesAsCharMatrix('.');
        Position start = dia.findFirst('S');
        return getTimeLine(dia, start);
    }

    private long getTimeLine(final CharMatrix dia, final Position origPos)
    {
        // have we been here already?
        if (memo.containsKey(origPos))
            return memo.get(origPos);
        Position pos= origPos;

        // left to the sides?
        if (pos.col<0)
            return 0;
        if (pos.col>dia.cols()-1)
            return 0;

        // finished this timeline
        if (pos.row==dia.rows())
            return 1;

        // move down unobstructed
        while (dia.at(pos.row+1, pos.col)=='.')
        {
            // finished this timeline
            if (pos.row == dia.rows())
            {
                return 1;
            }
            pos=pos.updated(1,0);
        }
        // split the timelines and return the result
        final long count = getTimeLine(dia, pos.updated(1, -1)) + getTimeLine(dia, pos.updated(1, 1));
        // memoize the result
        memo.put(origPos, count);
        return count;
    }
}
