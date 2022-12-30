package de.hendriklipka.aoc2020.day10;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Day102a
{
    public static void main(String[] args)
    {
        try
        {
            List<Long> jolts = FileUtils.readLines(new File("data/day10.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).map(Long::parseLong).sorted().collect(Collectors.toList());
            int diffs[]=new int[3];
            diffs[2]=1; //
            long current=0;
            for(long jolt: jolts)
            {
                long diff=jolt-current;
                // print the diffs
                // we are interested in continuous streaks of '1's
                // 2 '1's arr worth 2
                // 3 '1's arr worth 4
                // 4 '1's arr worth 7
                // anything else is a
                // multiply everything together
                System.out.println(diff);
                diffs[(int)(diff-1)]++;
                current=jolt;
            }
            System.out.println(diffs[0]*diffs[2]);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
