package de.hendriklipka.aoc2020.day9;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Day091
{

    private static final int PRE_LENGTH = 25;

    public static void main(String[] args)
    {
        try
        {
            List<Long> nums = FileUtils.readLines(new File("../data/2020/day9.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).map(Long::parseLong).collect(Collectors.toList());
            long[] buffer = new long[PRE_LENGTH];
            for (int i = 0; i< PRE_LENGTH; i++)
            {
                buffer[i]=nums.get(i);
            }
            int current=0;
            for (int i = PRE_LENGTH; i<nums.size(); i++)
            {
                long num=nums.get(i);
                if (!isValid(buffer, num))
                {
                    System.out.println(num);
                    break;
                }
                buffer[current]=num;
                current = (current+1)% PRE_LENGTH;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static boolean isValid(long[] buffer, long num)
    {
        for (int i = 0; i<(PRE_LENGTH -1); i++)
        {
            for (int j = i+1; j< PRE_LENGTH; j++)
            {
                if ((buffer[i]+buffer[j])==num)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
