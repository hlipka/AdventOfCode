package de.hendriklipka.aoc2023.day15;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;

/**
 * User: hli
 * Date: 14.12.23
 * Time: 19:13
 */
public class Day15a
{
    public static void main(String[] args)
    {
        try
        {
            String testLine=AocParseUtils.getLines("2023", "ex15").get(0);
            String line=AocParseUtils.getLines("2023", "day15").get(0);
            System.out.println("hash of HASH=" + hashValue("HASH"));
            System.out.println("hash of line="+hashLine(testLine));
            System.out.println("hash of line="+hashLine(line));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static long hashLine(String testLine)
    {
        long h=0;
        String[] parts=testLine.split(",");
        for (String s: parts)
            h+=hashValue(s);
        return h;
    }

    private static long hashValue(String s)
    {
        int h = 0;
        char[] chars = s.toCharArray();
        for(char c:chars)
        {
            int a=(int)c;
            h+=a;
            h*=17;
            h=h%256;
        }
        return h;
    }
}
