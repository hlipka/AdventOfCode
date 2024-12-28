package de.hendriklipka.aoc2016.day12;

import java.util.List;

/**
 * User: hli
 */
public interface Command
{
    int execute(int pc, int[] regs, List<Command> memory);

    String getArgLine();
}
