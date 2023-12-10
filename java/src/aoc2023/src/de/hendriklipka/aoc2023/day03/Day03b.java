package de.hendriklipka.aoc2023.day03;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 00:00
 */
public class Day03b
{
    public static void main(String[] args)
    {
        try
        {
            final List<List<String>> machine = AocParseUtils.getLinesAsCharStrings("2023", "day03");
            dumpMachine(machine);
            long result = calculate(machine);
            System.out.println(result); // 527352 is too low
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static long calculate(final List<List<String>> machine)
    {
        long sum=0;
        for (int y = 0; y< machine.get(0).size(); y++)
        {
            for (int x = 0; x< machine.size(); x++)
            {
                char c= atPos(machine, x, y);
                if ('*'==c)
                {
                    sum+=checkGear(machine, x, y);
                }
            }
        }

        return sum;
    }

    private static long checkGear(final List<List<String>> machine, final int x, final int y)
    {
        List<Integer> numbers=new ArrayList<>();
        // to the left
        if (Character.isDigit(atPos(machine, x-1, y)))
            numbers.add(getNumber(machine, x - 1, y));
        // to the right
        if (Character.isDigit(atPos(machine, x + 1, y)))
            numbers.add(getNumber(machine, x + 1, y));
        if (Character.isDigit(atPos(machine, x-1, y-1)))
        {
            numbers.add(getNumber(machine, x - 1, y-1));
            // two numbers in top row?
            if (!Character.isDigit(atPos(machine, x , y - 1)) && Character.isDigit(atPos(machine, x + 1, y - 1)))
            {
                numbers.add(getNumber(machine, x + 1, y - 1));
            }
        }
        // none to the left, so maybe one at the middle?
        else if(Character.isDigit(atPos(machine, x, y - 1)))
        {
            numbers.add(getNumber(machine, x, y - 1));
        }
        // now only the right position is left
        else if(Character.isDigit(atPos(machine, x+1, y - 1)))
        {
            numbers.add(getNumber(machine, x + 1, y - 1));
        }

        // same for bottom row
        if (Character.isDigit(atPos(machine, x - 1, y + 1)))
        {
            numbers.add(getNumber(machine, x - 1, y + 1));
            if (!Character.isDigit(atPos(machine, x, y + 1)) && Character.isDigit(atPos(machine, x + 1, y + 1)))
            {
                numbers.add(getNumber(machine, x + 1, y + 1));
            }
        }
        // none to the left, so maybe one at the middle?
        else if (Character.isDigit(atPos(machine, x, y + 1)))
        {
            numbers.add(getNumber(machine, x, y + 1));
        }
        // now only the right position is left
        else if (Character.isDigit(atPos(machine, x + 1, y + 1)))
        {
            numbers.add(getNumber(machine, x + 1, y + 1));
        }

        if (numbers.size()==2)
        {
            return ((long)numbers.get(0))*((long)numbers.get(1));
        }
        return 0;
    }

    private static Integer getNumber(final List<List<String>> machine, int x, final int y)
    {
        while(Character.isDigit(atPos(machine, x,y))) x--;
        x++;
        StringBuilder sb=new StringBuilder();
        while (Character.isDigit(atPos(machine, x, y)))
        {
            sb.append(atPos(machine, x, y));
            x++;
        }
        return Integer.parseInt(sb.toString());
    }

    private static void dumpMachine(final List<List<String>> machine)
    {
        for (List<String> line: machine)
        {
            System.out.println(StringUtils.join(line,""));
        }
    }

    private static char atPos(final List<List<String>> machine, final int x, final int y)
    {
        if (x<0 || y<0)
        {
            return '.';
        }
        if (y>=machine.size())
        {
            return '.';
        }
        if (x>=machine.get(0).size())
        {
            return '.';
        }
        return machine.get(y).get(x).charAt(0);
    }
}
