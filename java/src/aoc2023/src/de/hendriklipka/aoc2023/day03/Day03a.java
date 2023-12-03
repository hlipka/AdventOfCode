package de.hendriklipka.aoc2023.day03;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 00:00
 */
public class Day03a
{
    public static void main(String[] args)
    {
        try
        {
            final List<List<String>> machine = AocParseUtils.getLinesAsChars("2023", "day03");
            dumpMachine(machine);
            int result = calculate(machine);
            System.out.println(result); // 527352 is too low
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int calculate(final List<List<String>> machine)
    {
        int sum=0;
        int width=machine.get(0).size();
        int height=machine.size();
        for (int y=0;y<width;y++)
        {
            System.out.println("---line "+y);
            for (int x=0;x<height;x++)
            {
                char c= atPos(machine, x, y);
                if (Character.isDigit(c))
                {
                    if (isConnectedRight(machine, x, y))
                    {
                        int num=getAndRemoveNumber(machine, x, y);
                        System.out.println("found "+num);
                        sum+=num;
                    }
                    else
                    {
                        // just remove the number (so we do not look at it again)
                        int num=getAndRemoveNumber(machine, x, y);
                        System.out.println("skip "+num);
                    }
                }
            }
        }

        return sum;
    }

    private static void dumpMachine(final List<List<String>> machine)
    {
        for (List<String> line: machine)
        {
            System.out.println(StringUtils.join(line,""));
        }
    }

    private static int getAndRemoveNumber(final List<List<String>> machine, int x, final int y)
    {
        StringBuilder num=new StringBuilder();
        num.append(atPos(machine, x, y));
        clearPos(machine, x, y);
        while (true)
        {
            x++;
            char c = atPos(machine, x, y);
            if (Character.isDigit(c))
            {
                num.append(c);
                clearPos(machine, x, y);
            }
            else
            {
                break;
            }
        }
        return Integer.parseInt(num.toString());
    }

    private static void clearPos(final List<List<String>> machine, final int x, final int y)
    {
        machine.get(y).set(x, ".");
    }

    private static boolean isConnectedRight(final List<List<String>> machine, int x, final int y)
    {
        if (isPosConnected(machine, x, y))
        {
            return true;
        }
        while (true)
        {
            x++;
            char c=atPos(machine, x, y);
            if (!Character.isDigit(c))
            {
                return false;
            }
            if (isPosConnected(machine, x, y))
            {
                return true;
            }
        }
    }

    private static boolean isPosConnected(final List<List<String>> machine, final int x, final int y)
    {
        if (isSymbol(atPos(machine, x-1,y-1))) return true;
        if (isSymbol(atPos(machine, x,y-1))) return true;
        if (isSymbol(atPos(machine, x+1,y-1))) return true;

        if (isSymbol(atPos(machine, x-1,y))) return true;
        if (isSymbol(atPos(machine, x+1,y))) return true;

        if (isSymbol(atPos(machine, x-1,y+1))) return true;
        if (isSymbol(atPos(machine, x,y+1))) return true;
        if (isSymbol(atPos(machine, x+1,y+1))) return true;

        return false;
    }

    private static boolean isSymbol(final char c)
    {
        return c!='.' && !Character.isDigit(c);
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
