package de.hendriklipka.aoc2016.day01;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Position;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: hli
 * Date: 04.11.23
 * Time: 18:16
 */
public class Day01b
{
    static int dir = 0; // 0=N, 1=E
    private static Set<Position> positions = new HashSet<>();
    static char[] dirs={'N','E','S','W'};

    public static void main(String[] args)
    {
        try
        {
            List<String>  directions = AocParseUtils.getFirstLineWords("2016", "day01", ",");
            Position pos = new Position(0, 0);
            positions.add(pos);
            for (String direction: directions)
            {
                boolean found=false;
                System.out.println(direction);
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
                System.out.println("->"+dirs[dir]);
                for (int i = 0; i < dist; i++)
                {
                    pos = switch (dir)
                    {
                        case 0 -> pos.updated(-1, 0);
                        case 1 -> pos.updated(0, 1);
                        case 2 -> pos.updated(1, 0);
                        case 3 -> pos.updated(0, -1);
                        default -> pos;
                    };
                    System.out.println(pos);
                    if (positions.contains(pos))
                    {
                        found=true;
                        break;
                    }
                    positions.add(pos);
                }
                if(found)
                    break;
            }
            int dist = Math.abs(pos.col) + Math.abs(pos.row);
            System.out.println(dist);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
