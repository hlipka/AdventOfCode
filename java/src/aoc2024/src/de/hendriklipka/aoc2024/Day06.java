package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static de.hendriklipka.aoc.Direction.*;

public class Day06 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day06().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        CharMatrix lab = data.getLinesAsCharMatrix(' ');
        Position current = lab.findFirst('^');
        Set<Position> visited = findVisitedPlaces(lab, current);
        return visited.size();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CharMatrix lab = data.getLinesAsCharMatrix(' ');
        Position start = lab.findFirst('^');

        // we need to place obstacles only onto visited places, anything else will never be seen by the guard
        Set<Position> visited = findVisitedPlaces(lab, start);
        // but we need to remove the start position
        visited.remove(start);

        return (int) visited.stream().parallel().filter(p -> isLoop(lab, p, start)).count();
    }

    private static Set<Position> findVisitedPlaces(final CharMatrix lab, Position current)
    {
        // we just track where we have been
        Set<Position> visited = new HashSet<>();
        // both fit sample+real data for me
        Direction dir = UP;
        // and walk until we leave the room
        while (lab.in(current))
        {
            visited.add(current);
            while (true)
            {
                Position newPos = current.updated(dir);
                if (lab.at(newPos) != '#')
                {
                    current = newPos;
                    break;
                }
                dir = dir.right();
            }
        }
        return visited;
    }

    private boolean isLoop(final CharMatrix lab, Position block, final Position start)
    {
        // we are in a loop when we re-visited a place while going into the same direction
        Set<Pair<Position, Direction>> visited = new HashSet<>();
        Position current = start;
        Direction dir = Direction.UP;
        while (lab.in(current))
        {
            Pair<Position, Direction> p = new ImmutablePair<>(current, dir);
            if (visited.contains(p))
            {
                return true;
            }
            visited.add(p);
            while (true)
            {
                Position newPos = current.updated(dir);
                // we track the new obstacle separately so we don't need a copy of the lab
                if (lab.at(newPos) != '#' && !newPos.equals(block) )
                {
                    current = newPos;
                    break;
                }
                dir = dir.right();
            }
        }

        return false;
    }
}
