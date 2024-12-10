package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.IntMatrix;

import java.io.IOException;
import java.util.List;

public class Day10 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day10().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final IntMatrix map = data.getLinesAsIntMatrix(100);
        List<Position> trailHeads = map.allMatchingPositions(0);

        return trailHeads.stream().mapToInt(p->getScore(map,p)).sum();
    }

    private int getScore(final IntMatrix map, final Position p)
    {
        int score=0;
        int current = map.at(p);
        for (Direction d: Direction.values())
        {
            Position newPos=p.updated(d);
            if (map.at(newPos) == current+1)
            {
                score+=getScore(map, newPos);
            }
        }
        return score;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return 0;
    }
}
