package de.hendriklipka.aoc2023.day11;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day11b
{

    public static final int EXPAND_FACTOR = 1000000;

    public static void main(String[] args)
    {
        try
        {
            List<List<Character>> universe = AocParseUtils.getLinesAsChars("2023", "day11");
            boolean[] emptyColumns = getEmptyColumns(universe);
            boolean[] emptyRows = getEmptyRows(universe);
            List<Position> galaxies = findGalaxies(universe);
            long total = getDistances(galaxies, emptyRows, emptyColumns);
            System.out.println(total);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean[] getEmptyRows(List<List<Character>> universe)
    {
        boolean[] emptyRows = new boolean[universe.get(0).size()];
        Arrays.fill(emptyRows, true);
        for (int i = 0; i < universe.size(); i++)
        {
            emptyRows[i] = !universe.get(i).contains('#');
        }
        return emptyRows;
    }

    private static boolean[] getEmptyColumns(List<List<Character>> universe)
    {
        boolean[] emptyColumns = new boolean[universe.get(0).size()];
        Arrays.fill(emptyColumns, true);
        for (List<Character> row : universe)
        {
            for (int i = 0; i < row.size(); i++)
            {
                if ('#' == row.get(i))
                {
                    emptyColumns[i] = false;
                }
            }
        }
        return emptyColumns;
    }

    private static List<Position> findGalaxies(List<List<Character>> universe)
    {
        List<Position> result = new ArrayList<>();
        for (int i = 0; i < universe.size(); i++)
        {
            List<Character> row = universe.get(i);
            for (int j = 0; j < row.size(); j++)
            {
                if (row.get(j) == '#')
                {
                    result.add(new Position(i, j));
                }
            }
        }
        return result;
    }

    private static long getDistances(List<Position> galaxies, boolean[] emptyRows, boolean[] emptyColumns)
    {
        long sum = 0;
        for (int i = 0; i < galaxies.size() - 1; i++)
        {
            for (int j = i + 1; j < galaxies.size(); j++)
            {
                sum += getDistance(galaxies.get(i), galaxies.get(j), emptyRows, emptyColumns);
            }
        }
        return sum;
    }

    private static long getDistance(Position g1, Position g2, boolean[] emptyRows, boolean[] emptyColumns)
    {
        long dist=0;
        int startRow=Math.min(g1.row, g2.row);
        int endRow=Math.max(g1.row, g2.row);
        int startCol=Math.min(g1.col, g2.col);
        int endCol=Math.max(g1.col, g2.col);
        for (int row=startRow;row<endRow;row++)
        {
            if (emptyRows[row])
            {
                dist+= EXPAND_FACTOR;
            }
            else {
                dist++;
            }
        }
        for (int col=startCol;col<endCol;col++)
        {
            if (emptyColumns[col])
            {
                dist+= EXPAND_FACTOR;
            }
            else {
                dist++;
            }
        }
        return dist;
    }
}
