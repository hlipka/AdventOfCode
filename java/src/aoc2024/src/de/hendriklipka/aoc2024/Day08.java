package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;
import java.util.*;

public class Day08 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day08().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final CharMatrix city = data.getLinesAsCharMatrix('.');
        Map<Character, List<Position>> antennas=parseCity(city);
        Set<Position> nodes = new HashSet<>();
        for (Map.Entry<Character, List<Position>> f : antennas.entrySet())
        {
            final List<Position> a = f.getValue();
            a.forEach(ant1 -> a.stream().filter(ant2 -> !ant1.equals(ant2)).forEach(ant2 -> addNodes(ant1, ant2, city, nodes)));
        }
        return nodes.size();
    }

    private void addNodes(final Position ant1, final Position ant2, final CharMatrix city, final Set<Position> nodes)
    {
        int colDiff=ant2.col-ant1.col;
        int rowDiff=ant2.row-ant1.row;
        // since we look at each pair in both orderings, we get both sides of the antenna line
        Position node=new Position(ant1.col-colDiff, ant1.row-rowDiff);
        if (city.in(node))
            nodes.add(node);
    }

    private Map<Character, List<Position>> parseCity(final CharMatrix city)
    {
        final Map<Character, List<Position>> antennas = new HashMap<>();
        city.allPositions().forEach(p->
        {
            char c=city.at(p);
            if (c!='.')
            {
                List<Position> positions=antennas.computeIfAbsent(c, x->new ArrayList<>());
                positions.add(p);
            }
        });
        return antennas;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final CharMatrix city = data.getLinesAsCharMatrix('.');
        Map<Character, List<Position>> antennas = parseCity(city);
        Set<Position> nodes = new HashSet<>();
        for (Map.Entry<Character, List<Position>> f : antennas.entrySet())
        {
            f.getValue()
                    .forEach(ant1 -> f.getValue().stream().filter(ant2 -> !ant1.equals(ant2)).forEach(ant2 -> addResonantNodes(ant1, ant2, city, nodes)));
        }
        return nodes.size();
    }

    private void addResonantNodes(final Position ant1, final Position ant2, final CharMatrix city, final Set<Position> nodes)
    {
        int colDiff = ant2.col - ant1.col;
        int rowDiff = ant2.row - ant1.row;
        // since we look at each pair in both orderings, we get both sides of the antenna line
        // loop through all potential offsets until we leave the city
        int ofs=0;
        while(true)
        {
            Position node = new Position(ant1.row - rowDiff*ofs, ant1.col - colDiff*ofs);
            if (!city.in(node))
            {
                break;
            }
            nodes.add(node);
            ofs++;
        }
    }
}
