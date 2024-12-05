package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocDataFileUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 */
public class Day05a
{
    public static void main(String[] args)
    {
        try
        {
            final List<Integer> jumps = AocDataFileUtils.getLinesAsInt("2017", "day05");
            int count=0;
            int addr=0;
            while (addr >=0 && addr < jumps.size())
            {
                addr = doJump(addr, jumps);
                count++;
            }
            System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int doJump(final int addr, final List<Integer> jumps)
    {
        int target = jumps.get(addr);
        jumps.set(addr, target +1);
        return addr+target;
    }
}
