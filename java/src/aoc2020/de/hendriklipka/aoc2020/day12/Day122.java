package de.hendriklipka.aoc2020.day12;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 13.12.20
 * Time: 21:40
 */
public class Day122
{
    private static long dNorth;
    private static long dEast;
    private static long wNorth = 1;
    private static long wEast = 10;
    private final static int DIR_N = 0;
    private final static int DIR_E = 1;
    private final static int DIR_S = 2;
    private final static int DIR_W = 3;
    private static int direction = DIR_E;

    public static void main(String[] args)
    {
        try
        {
            List<String> lines = FileUtils.readLines(new File("data/day12.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            lines.stream().forEach(Day122::moveShip);
            System.out.println(dNorth + "/" + dEast + "/" + (Math.abs(dNorth) + Math.abs(dEast)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static void moveShip(final String line)
    {
        long distance = Long.parseLong(line.substring(1));
        switch (line.charAt(0))
        {
            case 'N':
                north(distance);
                break;
            case 'S':
                south(distance);
                break;
            case 'E':
                east(distance);
                break;
            case 'W':
                west(distance);
                break;
            case 'L':
                while (distance != 0)
                {
                    long oldNorth = wNorth;
                    wNorth = wEast;
                    wEast = -oldNorth;
                    distance -= 90;
                }
                break;
            case 'R':
                while (distance != 0)
                {
                    long oldNorth=wNorth;
                    wNorth=-wEast;
                    wEast=oldNorth;
                    distance -= 90;
                }
                break;
            case 'F':
                for (long i=0;i<distance;i++)
                {
                    dNorth+=wNorth;
                    dEast+=wEast;
                }
                break;
        }
    }

    private static void north(final long distance)
    {
        wNorth += distance;
    }

    private static void south(final long distance)
    {
        wNorth -= distance;
    }

    private static void east(final long distance)
    {
        wEast += distance;
    }

    private static void west(final long distance)
    {
        wEast -= distance;
    }
}
