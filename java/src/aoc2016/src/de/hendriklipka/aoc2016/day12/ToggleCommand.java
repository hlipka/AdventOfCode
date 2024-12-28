package de.hendriklipka.aoc2016.day12;

import de.hendriklipka.aoc2016.Day23;

import java.util.List;

/**
 * User: hli
 */
public class ToggleCommand implements Command
{
    private final int reg;
    private final String argLine;

    public ToggleCommand(final String line)
    {
        reg = line.charAt(0) - 'a';
        argLine = line;
    }

    @Override
    public int execute(final int pc, final int[] regs, List<Command> memory)
    {
        int addr = pc + regs[reg];
        if (addr >= memory.size())
            return pc + 1;
        memory.set(addr, getNewCommand(memory.get(addr)));
        return pc + 1;
    }

    private Command getNewCommand(final Command command)
    {
        if (command instanceof IncCommand)
        {
            return new DecCommand(command.getArgLine());
        }
        if (command instanceof DecCommand)
        {
            return new IncCommand(command.getArgLine());
        }
        if (command instanceof ToggleCommand)
        {
            return new IncCommand(command.getArgLine());
        }
        if (command instanceof JumpCommand)
        {
            return new CopyCommand(command.getArgLine());
        }
        if (command instanceof CopyCommand)
        {
            return new JumpCommand(command.getArgLine());
        }
        return null;
    }

    @Override
    public String getArgLine()
    {
        return argLine;
    }
}
