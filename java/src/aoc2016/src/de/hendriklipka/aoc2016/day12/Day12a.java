package de.hendriklipka.aoc2016.day12;

import de.hendriklipka.aoc.AocDataFileUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 22:32
 */
public class Day12a
{
    public static void main(String[] args)
    {
        try
        {
            CommandParser p=new CommandParser();
            final List<Command> commands = AocDataFileUtils.getLines("2016", "day12").stream().map(p::parseLine).toList();
            int[] regs = new int[]{0,0,0,0};
            p.doRun(commands, regs);
            System.out.println(regs[0]);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}
