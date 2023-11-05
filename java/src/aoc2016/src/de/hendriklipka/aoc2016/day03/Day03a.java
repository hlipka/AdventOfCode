package de.hendriklipka.aoc2016.day03;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 05.11.23
 * Time: 15:54
 */
public class Day03a
{
    public static void main(String[] args)
    {
        try
        {
            int valid=0;
            final List<List<Integer>> triangles = AocParseUtils.getLineIntegers("2016", "day03", " ");
            for (List<Integer> triangle: triangles)
            {
                int a=triangle.get(0);
                int b=triangle.get(1);
                int c=triangle.get(2);
                if ((a+b>c) && (a+c>b)&&(b+c>a))
                    valid++;
            }
            System.out.println(valid);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
