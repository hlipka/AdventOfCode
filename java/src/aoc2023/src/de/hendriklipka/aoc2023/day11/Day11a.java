package de.hendriklipka.aoc2023.day11;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day11a
{
    public static void main(String[] args)
    {
        try
        {
            List<List<Character>> universe = AocParseUtils.getLinesAsChars("2023", "day11");
            expandUniverse(universe);
            List<Position> galaxies = findGalaxies(universe);
            int total=getDistances(galaxies);
            System.out.println(total);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void expandUniverse(List<List<Character>> universe)
    {
        int[] galaxCount=new int[universe.get(0).size()];
        Arrays.fill(galaxCount, 0);
        for (List<Character> row: universe)
        {
            for (int i=0;i<row.size();i++)
            {
                if ('#'==row.get(i))
                {
                    galaxCount[i]++;
                }
            }
        }
        for (int i=universe.size()-1;i>=0;i--)
        {
            List<Character> row = universe.get(i);
            for (int j=row.size()-1;j>=0;j--)
            {
                if (galaxCount[j]==0)
                {
                    row.add(j, '.');
                }
            }
            if (!row.contains('#'))
            {
                universe.add(i, new ArrayList<>(row));
            }
        }
    }

    private static List<Position> findGalaxies(List<List<Character>> universe)
    {
        List<Position> result = new ArrayList<>();
        for (int i=0;i<universe.size();i++)
        {
            List<Character> row = universe.get(i);
            for (int j = 0; j < row.size(); j++)
            {
                if (row.get(j)=='#')
                {
                    result.add(new Position(i,j));
                }
            }
        }
        return result;
    }

    private static int getDistances(List<Position> galaxies)
    {
        int sum=0;
        for (int i=0;i<galaxies.size()-1;i++)
        {
            for (int j=i+1;j<galaxies.size();j++)
            {
                sum+=getDistance(galaxies.get(i), galaxies.get(j));
            }
        }
        return sum;
    }

    private static int getDistance(Position g1, Position g2)
    {
        return Math.abs(g1.row-g2.row)+Math.abs(g1.col-g2.col);
    }
}
