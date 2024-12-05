package de.hendriklipka.aoc2016.day07;

import de.hendriklipka.aoc.AocDataFileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hli
 * Date: 05.11.23
 * Time: 22:14
 */
public class Day07a
{
    public static void main(String[] args)
    {
        try
        {
            int tls = 0;
            final List<String> addresses = AocDataFileUtils.getLines("2016", "day07");
            for (String addr : addresses)
            {
                if (canDoTLS(addr))
                {
                    System.out.println("TLS for "+addr);
                    tls++;
                }
            }
            System.out.println(tls);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean canDoTLS(final String addr)
    {
        final String[] split = StringUtils.splitByCharacterType(addr);
        List<String> outside = new ArrayList<>();
        List<String> inside = new ArrayList<>();
        boolean isOutside=true;
        for (String part: split)
        {
            if ("[".equals(part))
            {
                isOutside=false;
            }
            else if ("]".equals(part))
            {
                isOutside = true;
            }
            else if (isOutside)
            {
                outside.add(part);
            }
            else
            {
                inside.add(part);
            }
        }
        if (!hasAbba(outside)) return false;
        return inside.stream().noneMatch(Day07a::hasAbba);
    }

    private static boolean hasAbba(final List<String> parts)
    {
        return parts.stream().anyMatch(Day07a::hasAbba);
    }

    private static boolean hasAbba(final String part)
    {
        int len=part.length();
        for (int i=0;i<len-3;i++)
        {
            if (part.charAt(i)!=part.charAt(i+1)&&part.charAt(i)==part.charAt(i+3)&&part.charAt(i+1)==part.charAt(i+2))
                return true;
        }
        return false;
    }
}
