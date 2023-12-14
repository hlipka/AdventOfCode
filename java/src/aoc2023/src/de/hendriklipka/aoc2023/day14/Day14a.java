package de.hendriklipka.aoc2023.day14;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

public class Day14a
{

    public static final char ROUND_ROCK = 'O';
    public static final char FREE = '.';

    public static void main(String[] args)
    {
        try
        {
            List<List<Character>> field = AocParseUtils.getLinesAsChars("2023", "day14");
            dumpField(field);
            int size = field.size();
            for (int i = 1; i < size; i++)
            {
                List<Character> line = field.get(i);
                moveNorth(line, i, field);
            }
            long sum=0;
            for (int i = 0; i < size; i++)
            {
                int weight = size - i;
                long lineWeight = weight * field.get(i).stream().filter(c -> c == ROUND_ROCK).count();
                System.out.println(weight);
                sum+= lineWeight;
            }
            dumpField(field);
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void dumpField(List<List<Character>> field)
    {
        for (List<Character> line: field)
        {
            System.out.println(StringUtils.join(line,""));
        }
    }

    private static void moveNorth(List<Character> line, int row, List<List<Character>> field)
    {
        for (int i=0;i<line.size();i++)
        {
            if (line.get(i) == ROUND_ROCK)
            {
                moveNorth(i, row, field);
            }
        }
    }

    private static void moveNorth(int col, int row, List<List<Character>> field)
    {
        int origRow=row;
        while (row>0 && field.get(row-1).get(col) == FREE)
        {
            row--;
        }
        field.get(origRow).set(col, FREE);
        field.get(row).set(col, ROUND_ROCK);
    }
}
