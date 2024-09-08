package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;

public class Day01a
{
    public static void main(String[] args)
    {
        try
        {
            String data = AocParseUtils.getLines("2017", "day01").get(0);
            int sum = 0;
            for (int i=0; i<data.length()-1; i++)
            {
                if (data.charAt(i) == data.charAt(i+1))
                {
                    sum+=Integer.parseInt(data.charAt(i)+"");
                }
            }
            if (data.charAt(0) == data.charAt(data.length()-1))
            {
                sum += Integer.parseInt(data.charAt(0) + "");
            }
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
