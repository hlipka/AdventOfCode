package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.ObjectMatrix;

import java.io.IOException;
import java.util.*;

public class Day06 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day06().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<Position> coords = data.getLines().stream().map(l->Position.withXY(l, ',')).toList();
        int left=coords.stream().mapToInt(p->p.col).min().orElseThrow();
        int right=coords.stream().mapToInt(p->p.col).max().orElseThrow();
        int up=coords.stream().mapToInt(p->p.row).max().orElseThrow();
        int down=coords.stream().mapToInt(p->p.row).min().orElseThrow();

        // this matrix stored the coordinate which is closest to each position
        // we only need to look to the boundaries on each side - anything which hits these boundaries will extend forever
        ObjectMatrix<Position> grid=new ObjectMatrix<>(up+1, right+1);
        for (Position p : grid.allPositions())
        {
            if (p.row<down || p.col<left)
                continue;
            grid.set(p, getClosestCoordinate(p, coords));
        }

        // count how many locations each coordinate is the closest to, and the return the largest area
        Set<String> border = new HashSet<>();
        Map<String, Integer> counts=new HashMap<>();
        for (Position p : grid.allPositions())
        {
            Position coord=grid.at(p);
            if (null!=coord)
            {
                String key=coord.toString();
                if (p.row==up||p.row==down||p.col==left||p.col==right)
                {
                    border.add(key);
                }
                else
                {
                    int count = counts.getOrDefault(key, 0);
                    counts.put(key, count + 1);
                }
            }
        }
        // remove all coords which reached the border
        for (String key : border)
        {
            counts.remove(key);
        }
        final var highest = counts.values().stream().mapToInt(value -> value).max().orElseThrow();
        for (Map.Entry e: counts.entrySet())
        {
            if (e.getValue().equals(highest))
            {
                System.out.println(e.getKey());
            }
        }
        return highest;
    }

    private Position getClosestCoordinate(final Position p, final List<Position> coords)
    {
        // order by distance, sio the closest ones come first
        final List<Position> first2 = coords.stream().sorted(Comparator.comparingInt(c -> c.dist(p))).limit(2).toList();
        // when the first 2 coords have the same distance, none will be used
        Position first=first2.get(0);
        Position second=first2.get(1);
        if (first.dist(p)==second.dist(p))
            return null;
        return first;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<Position> coords = data.getLines().stream().map(l -> Position.withXY(l, ',')).toList();
        int maxSum=isExample?32:10000;
        int left = coords.stream().mapToInt(p -> p.col).min().orElseThrow();
        int right = coords.stream().mapToInt(p -> p.col).max().orElseThrow();
        int up = coords.stream().mapToInt(p -> p.row).max().orElseThrow();
        int down = coords.stream().mapToInt(p -> p.row).min().orElseThrow();

        ObjectMatrix<Position> grid = new ObjectMatrix<>(up + 1, right + 1);

        return grid.allPositions().stream().mapToInt(p-> distanceSum(p, coords)).filter(s-> s < maxSum).count();
    }

    private int distanceSum(final Position p, final List<Position> coords)
    {
        return coords.stream().mapToInt(c -> c.dist(p)).sum();
    }
}
