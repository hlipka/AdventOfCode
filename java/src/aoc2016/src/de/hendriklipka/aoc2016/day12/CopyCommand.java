package de.hendriklipka.aoc2016.day12;

import java.util.List;

/**
 * User: hli
 */
public class CopyCommand implements Command
{
    int argValue = -1;
    int argReg = -1;
    int targetReg = -1;
    private final String argLine;

    public CopyCommand(final String line)
    {
        String[] args = line.split(" ", -1);
        if (!Character.isAlphabetic(args[0].charAt(0)))
        {
            argValue = Integer.parseInt(args[0]);
        }
        else
        {
            argReg = args[0].charAt(0) - 'a';
        }
        targetReg = args[1].charAt(0) - 'a';
        argLine = line;
    }

    @Override
    public int execute(final int pc, final int[] regs, List<Command> memory)
    {
        if (-1 != argValue)
        {
            regs[targetReg] = argValue;
        }
        else
        {
            // ignore invalid commands
            if (argReg<0)
                return pc+1;
            regs[targetReg] = regs[argReg];
        }
        return pc + 1;
    }

    @Override
    public String getArgLine()
    {
        return argLine;
    }
}
