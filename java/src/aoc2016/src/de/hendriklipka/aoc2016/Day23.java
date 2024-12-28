package de.hendriklipka.aoc2016;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc2016.day12.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: hli
 */
public class Day23 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day23().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        CommandParser p = new CommandParser();
        final List<Command> commands = data.getLines().stream().map(p::parseLine).toList();
        int[] regs = new int[]{7, 0, 0, 0};

        p.doRun(new ArrayList<>(commands), regs);

        return regs[0];
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CommandParser p = new CommandParser();
        final List<Command> commands = data.getLines().stream().map(p::parseLine).toList();
        // run with the new input value
        // technically we are supposed to optimize the execution, but since 261 it seems computers are now fast enough to finish that in 11 seconds
        int[] regs = new int[]{12, 0, 0, 0};

        p.doRun(new ArrayList<>(commands), regs);

        return regs[0];
    }
}
