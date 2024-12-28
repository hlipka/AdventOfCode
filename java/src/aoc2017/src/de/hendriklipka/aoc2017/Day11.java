package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

import static de.hendriklipka.aoc.HexDirection.*;

public class Day11 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day11().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> input=data.getLines();
        return input.stream().mapToInt(Day11::getDistance).sum();
    }

    private static int getDistance(String line)
    {
        String[] steps = StringUtils.split(line, ',');
        final var current = walk(steps);

        return calculateDistance(current);
    }

    private static int calculateDistance(final Position pos)
    {
        // mirror all position into the positive quadrant
        int absCol=Math.abs(pos.col);
        int absRow=Math.abs(pos.row);
        int dist = 0;
        // as long as we are in the lower 45° area, we move two columns towards the center
        while (absRow<absCol)
        {
            absCol-=2;
            dist+=2;
        }
        // once we are in the upper 45° area, we can simply calculate the steps
        dist+=(absCol+absRow)/2;
        return dist;
    }

    private static Position walk(final String[] steps)
    {
        Position current = new Position(0,0);
        for (String step : steps)
        {
            current = switch (step)
            {
                case "n" -> current.updated(N);
                case "s" -> current.updated(S);
                case "ne" -> current.updated(NE);
                case "nw" -> current.updated(NW);
                case "se" -> current.updated(SE);
                case "sw" -> current.updated(SW);
                default -> current;
            };
        }
        return current;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        String line = data.getLines().get(0);
        String[] steps = StringUtils.split(line, ',');
        int max=0;
        Position current = new Position(0, 0);
        for (String step : steps)
        {
            current = switch (step)
            {
                case "n" -> current.updated(N);
                case "s" -> current.updated(S);
                case "ne" -> current.updated(NE);
                case "nw" -> current.updated(NW);
                case "se" -> current.updated(SE);
                case "sw" -> current.updated(SW);
                default -> current;
            };
            int dist= calculateDistance(current);
            if (dist>max)
                max=dist;
        }
        return max;
    }
}
