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
public class Day07b
{
    public static void main(String[] args)
    {
        try
        {
            int ssl = 0;
            final List<String> addresses = AocDataFileUtils.getLines("2016", "day07");
            for (String addr : addresses)
            {
                if (canDoSSL(addr))
                {
                    System.out.println("SSL for "+addr);
                    ssl++;
                }
            }
            System.out.println(ssl);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean canDoSSL(final String addr)
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
        for (String part: outside)
        {
            if (hasSSL(part, inside))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSSL(String part, List<String> inside) {
        int len=part.length();
        for (int i=0;i<len-2;i++)
        {
            if (part.charAt(i)!=part.charAt(i+1)&&part.charAt(i)==part.charAt(i+2))
            {
                String bab=""+part.charAt(i+1)+part.charAt(i)+part.charAt(i+1);
                for (String innerPart: inside)
                {
                    if (innerPart.contains(bab))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
