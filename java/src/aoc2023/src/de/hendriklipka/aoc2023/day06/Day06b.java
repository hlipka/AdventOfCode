package de.hendriklipka.aoc2023.day06;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 00:00
 */
public class Day06b
{
    public static void main(String[] args)
    {
        System.out.println(calculateRace(57726992L, 291117211762026L));
    }

    private static long calculateRace(long time, long dist) {
        long count=0;
        for (long press=0;press<time;press++)
        {
            long race = press * (time-press);
            if (race>dist)
                count++;
        }
        System.out.println(count);
        return count;
    }
}
