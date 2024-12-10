package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.IntMatrix;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        return trailHeads.stream().mapToInt(p->
                getScore(map, p)).sum();
    }

    private int getScore(final IntMatrix map, final Position p)
    {
        Set<Position> ends = new HashSet<>();
        findPath(map, p, ends);
        return ends.size();
    }

    private void findPath(final IntMatrix map, final Position p, Set<Position> ends)
    {
        int current = map.at(p);
        if (current==9)
        {
            ends.add(p);
            return;
        }
        for (Direction d: Direction.values())
        {
            Position newPos=p.updated(d);
            if (map.at(newPos) == current+1)
            {
                findPath(map, newPos, ends);
            }
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final IntMatrix map = data.getLinesAsIntMatrix(100);
        List<Position> trailHeads = map.allMatchingPositions(0);

        return trailHeads.stream().mapToInt(p->
                getRating(map, p)).sum();
    }

    private int getRating(final IntMatrix map, final Position p)
    {
        int current = map.at(p);
        if (current==9)
        {
            return 1;
        }
        int score=0;
        for (Direction d: Direction.values())
        {
            Position newPos=p.updated(d);
            if (map.at(newPos) == current+1)
            {
                score+=getRating(map, newPos);
            }
        }
        return score;
    }

}
