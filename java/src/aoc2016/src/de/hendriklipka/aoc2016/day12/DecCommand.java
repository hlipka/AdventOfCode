package de.hendriklipka.aoc2016.day12;

import java.util.List;

/**
 * User: hli
 */
public class DecCommand implements Command
{
    private final int reg;
    private final String argLine;

    public DecCommand(final String line)
    {
        reg = line.charAt(0) - 'a';
        argLine = line;
    }

    @Override
    public int execute(final int pc, final int[] regs, List<Command> memory)
    {
        regs[reg]--;
        return pc + 1;
    }

    @Override
    public String getArgLine()
    {
        return argLine;
    }
}
