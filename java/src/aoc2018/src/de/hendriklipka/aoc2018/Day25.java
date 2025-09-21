package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class Day25 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day25().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<List<Integer>> coords = data.getLineIntegers(",");
        Set<String> remaining = new HashSet<>(coords.size());
        Map<String, List<String>> inDistance=new HashMap<>();
        for (List<Integer> coordinate : coords)
        {
            remaining.add(StringUtils.join(coordinate, ","));
            List<String> others=new ArrayList<>();
            for (List<Integer> other:coords)
            {
                if (coordinate!=other && inRange(coordinate, other))
                {
                    others.add(StringUtils.join(other, ","));
                }
            }
            inDistance.put(StringUtils.join(coordinate,","), others);
        }
        int count=0;
        while (!remaining.isEmpty())
        {
            count++;
            // use the first coordinate as the start of a new constellation
            String coordinate=remaining.iterator().next();
            // recursively find all coordinates which are in a constellation with the first one
            findConstellation(coordinate, remaining, inDistance);
        }
        return count;
    }

    private void findConstellation(final String coordinate, final Set<String> remaining, final Map<String, List<String>> inDistance)
    {
        remaining.remove(coordinate);
        List<String> others=inDistance.get(coordinate);
        for (String other:others)
        {
            if (remaining.contains(other))
                findConstellation(other, remaining, inDistance);
        }
    }

    private boolean inRange(final List<Integer> coord, final List<Integer> coord1)
    {
        return Math.abs(coord.get(0)-coord1.get(0))
               + Math.abs(coord.get(1)-coord1.get(1))
               + Math.abs(coord.get(2)-coord1.get(2))
               + Math.abs(coord.get(3)-coord1.get(3))
                <=3;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return null;
    }
}
