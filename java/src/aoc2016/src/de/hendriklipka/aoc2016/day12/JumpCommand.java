package de.hendriklipka.aoc2016.day12;

import java.util.List;

/**
 * User: hli
 */
public class JumpCommand implements Command
{
    int argValue = -1;

    private int argReg;
    private final String dist;
    private final String argLine;

    public JumpCommand(final String line)
    {
        String[] args = line.split(" ");
        if (!Character.isAlphabetic(args[0].charAt(0)))
        {
            argValue = Integer.parseInt(args[0]);
        }
        else
        {
            argReg = args[0].charAt(0) - 'a';
        }
        dist = args[1];
        argLine = line;
    }

    @Override
    public int execute(final int pc, final int[] regs, List<Command> memory)
    {
        int arg = (-1 == argValue) ? regs[argReg] : argValue;
        if (arg == 0)
        {
            return pc + 1;
        }
        return pc + getDist(regs);
    }

    private int getDist(final int[] regs)
    {
        if (dist.length()==1)
        {
            int reg = dist.charAt(0) - 'a';
            if (reg >= 0 && reg < 4)
                return regs[reg];
        }
        return Integer.parseInt(dist);
    }

    @Override
    public String getArgLine()
    {
        return argLine;
    }
}
