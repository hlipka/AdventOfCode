package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.*;
import de.hendriklipka.aoc.matrix.CharMatrix;

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
        CharMatrix wh=data.getLinesAsCharMatrix('.');

        return getRemovableRolls(wh).size();
    }

    private List<Position> getRemovableRolls(final CharMatrix wh)
    {
        return wh.allMatchingPositions('@').stream().filter(p -> canBeRemoved(p, wh)).toList();
    }

    private boolean canBeRemoved(final Position pos, final CharMatrix wh)
    {
        return wh.getDiagonalNeighbours(pos).stream().filter(c-> c == '@').count()<4;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        int removed=0;
        CharMatrix wh = data.getLinesAsCharMatrix('.');
        while (true)
        {
            final List<Position> removable = getRemovableRolls(wh);
            if (removable.isEmpty())
            {
                break;
            }
            removed+=removable.size();
            for(Position p:removable)
            {
                wh.set(p,'.');
            }
        }
        return removed;
    }
}
