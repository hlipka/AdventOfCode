package de.hendriklipka.aoc2024.day02;

import de.hendriklipka.aoc.AocDataFileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day02b
{
    public static void main(String[] args)
    {
        try
        {
            final List<List<Integer>> lines = AocDataFileUtils.getLineIntegers("2024", "day02", " ");
            long count = lines.stream().map(Day02b::isValid).filter(v->v).count();
            System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValid(List<Integer> l)
    {
        if (isValidLine(l))
            return true;
        for (int i=0;i<l.size();i++)
        {
            final List<Integer> newL = new ArrayList<>(l);
            newL.remove(i);
            if (isValidLine(newL))
                return true;
        }
        return false;
    }

    private static boolean isValidLine(final List<Integer> l)
    {
        int current = l.get(0);
        boolean dir=l.get(1)>current; // true for upwards
        for (int i=1; i<l.size(); i++)
        {
            int next=l.get(i);
            int diff = next-current;
            if (diff==0)
                return false;
            if (dir)
            {
                if (diff<0)
                    return false;
                if (diff>3)
                    return false;
            }
            else
            {
                if (diff>0)
                    return false;
                if (diff < -3)
                    return false;
            }
            current=next;
        }
        return true;
    }
}
