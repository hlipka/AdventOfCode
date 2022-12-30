package de.hendriklipka.aoc2020.day5;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 05.12.20
 * Time: 17:48
 */
public class Day052
{
    public static void main(String[] args)
    {
        try
        {
            List<String> lines = FileUtils.readLines(new File("../data/2020/day5.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            List<Integer> seatNums = lines.stream().map(Day052::calculateSeat).sorted().collect(Collectors.toList());
            int max=seatNums.stream().max(Integer::compare).get();
            int min=seatNums.stream().min(Integer::compare).get();
            for (int i=min;i<max;i++)
            {
                if (!seatNums.contains(i))
                {
                    System.out.println(i);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static Integer calculateSeat(final String line)
    {
        int row=0;
        int offset=64;
        for (int i=0;i<7;i++)
        {
            char c=line.charAt(i);
            if (c=='B')
            {
                row+=offset;
            }
            offset/=2;
        }
        offset=4;
        int col=0;
        for (int i = 7; i < 10; i++)
        {
            char c = line.charAt(i);
            if (c == 'R')
            {
                col += offset;
            }
            offset /= 2;
        }

        int num = row*8+col;
        return num;
    }
}
