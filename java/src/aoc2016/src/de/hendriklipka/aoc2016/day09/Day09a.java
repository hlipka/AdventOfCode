package de.hendriklipka.aoc2016.day09;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 06.11.23
 * Time: 17:57
 */
public class Day09a
{
    public static void main(String[] args)
    {
        try
        {
            String data= AocParseUtils.getLines("2016","day09").get(0);
            StringBuilder result=new StringBuilder();
            int current=0;
            while (current<data.length())
            {
                char c=data.charAt(current);
                if (c!='(')
                {
                    result.append(c);
                    current++;
                }
                else
                {
                    int closing=data.indexOf(')', current+1);
                    String decompress=data.substring(current+1, closing);
                    final List<String> parts = AocParseUtils.parsePartsFromString(decompress, "(\\d+)x(\\d+)");
                    int len=Integer.parseInt(parts.get(0));
                    int count=Integer.parseInt(parts.get(1));
                    current=closing+1;
                    String repeat=data.substring(current,current+len);
                    current+=len;
                    result.append(repeat.repeat(count));
                }
            }
            System.out.println(result);
            System.out.println(result.length());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
