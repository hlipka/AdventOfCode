package de.hendriklipka.aoc2016.day03;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 05.11.23
 * Time: 15:54
 */
public class Day03b
{
    public static void main(String[] args)
    {
        try
        {
            int valid=0;
            final List<List<Integer>> triangles = AocParseUtils.getLineIntegers("2016", "day03", " ");

            for (int i = 0; i < triangles.size(); i+=3)
            {
                final List<Integer> triangle1 = triangles.get(i);
                final List<Integer> triangle2 = triangles.get(i+1);
                final List<Integer> triangle3 = triangles.get(i+2);
                int a = triangle1.get(0);
                int b = triangle2.get(0);
                int c = triangle3.get(0);
                if ((a + b > c) && (a + c > b) && (b + c > a))
                {
                    valid++;
                }
                a = triangle1.get(1);
                b = triangle2.get(1);
                c = triangle3.get(1);
                if ((a + b > c) && (a + c > b) && (b + c > a))
                {
                    valid++;
                }
                a = triangle1.get(2);
                b = triangle2.get(2);
                c = triangle3.get(2);
                if ((a + b > c) && (a + c > b) && (b + c > a))
                {
                    valid++;
                }
            }
            System.out.println(valid);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
