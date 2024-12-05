package de.hendriklipka.aoc2016.day01;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.Position;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 04.11.23
 * Time: 18:16
 */
public class Day01a
{
    static int dir = 0; // 0=N, 1=E
    private static Position pos;

    public static void main(String[] args)
    {
        try
        {
            List<String>  directions = AocDataFileUtils.getFirstLineWords("2016", "day01", ",");
            pos = new Position(0, 0);
            for (String direction: directions)
            {
                move(direction.trim());
            }
            int dist = Math.abs(pos.col) + Math.abs(pos.row);
            System.out.println(dist);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void move(final String direction)
    {
        char turn = direction.charAt(0);
        int dist = Integer.parseInt(direction.substring(1));
        if (turn == 'R')
        {
            dir = (dir + 1) % 4;
        }
        else if (turn == 'L')
        {
            dir = (dir + 3) % 4;
        }
        switch (dir)
        {
            case 0: pos.row -= dist;
                    break;
            case 1: pos.col += dist;
                break;
            case 2: pos.row += dist;
                break;
            case 3: pos.col -= dist;
                break;
        }
    }
}
