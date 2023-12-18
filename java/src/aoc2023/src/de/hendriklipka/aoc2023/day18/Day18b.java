package de.hendriklipka.aoc2023.day18;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;

import java.io.IOException;
import java.util.*;

public class Day18b
{
    static Position currentPos=new Position(0,0);
    static List<Position> field = new ArrayList<>();
    static long border=0;
    public static void main(String[] args)
    {
        try
        {
            field.add(currentPos);
            AocParseUtils.getLines("2023", "day18").forEach(Day18b::dig);
            System.out.println(field.size());
            long area = calculateArea();
            System.out.println(area);
            System.out.println(border);
            System.out.println(area+border/2+1); // we need to account for the border as well - its only half-way in the area (and the start seems to be missing as well)
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    // using the shoelace formula (https://en.wikipedia.org/wiki/Shoelace_formula)
    private static long calculateArea()
    {
        long count=0;
        // the first and the last point are the same, so we get all areas with this loop
        for (int i=0;i<field.size()-1;i++)
        {
            Position pos=field.get(i);
            Position next=field.get(i+1);
            long area=((long)pos.row+(long)next.row)*((long)pos.col-(long)next.col);
            count+=area;
        }

        return count/2;
    }


    private static void dig(String line)
    {
        String rule=AocParseUtils.parseStringFromString(line,"[RLUD] \\d+ \\(#(.*)\\)");
        int length=Integer.parseInt(rule.substring(0,5), 16);
        char dirStr=rule.charAt(5);
        Direction dir=switch(dirStr)
        {
            case '0'->Direction.RIGHT;
            case '2'->Direction.LEFT;
            case '3'->Direction.UP;
            case '1'->Direction.DOWN;
            default ->throw new IllegalArgumentException("invalid: "+dirStr);
        };
        currentPos = currentPos.updated(dir, length);
        field.add(currentPos);
        border+=length;
    }
}
