package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocDataFileUtils;

import java.io.IOException;

public class Day01b
{
    public static void main(String[] args)
    {
        try
        {
            String data = AocDataFileUtils.getLines("2017", "day01").get(0);
            final var length = data.length();
            int half = length / 2;
            int sum = 0;
            for (int i = 0; i < length; i++)
            {
                int other = (i + half) % length;
                if (data.charAt(i) == data.charAt(other))
                {
                    sum+=Integer.parseInt(data.charAt(i)+"");
                }
            }
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
