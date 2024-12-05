package de.hendriklipka.aoc2023.day06;

import de.hendriklipka.aoc.AocDataFileUtils;
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
public class Day06a
{
    public static void main(String[] args)
    {
        try
        {
            final List<String> lines = AocDataFileUtils.getLines("2023", "day06");
            String timeStr = AocParseUtils.parseStringFromString(lines.get(0), "\\w+:(.+)");
            String distStr = AocParseUtils.parseStringFromString(lines.get(1), "\\w+:(.+)");
            int[] times= Arrays.stream(StringUtils.split(timeStr, " ")).mapToInt(Integer::parseInt).toArray();
            int[] dists= Arrays.stream(StringUtils.split(distStr, " ")).mapToInt(Integer::parseInt).toArray();
            int result=1;
            for (int i=0;i<times.length;i++)
            {
                result *=calculateRace(times[i], dists[i]);
            }
            System.out.println(result);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int calculateRace(int time, int dist) {
        int count=0;
        for (int press=0;press<time;press++)
        {
            int race = press * (time-press);
            if (race>dist)
                count++;
        }
        System.out.println(count);
        return count;
    }
}
