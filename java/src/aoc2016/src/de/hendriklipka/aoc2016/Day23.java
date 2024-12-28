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
        CommandParser2 p = new CommandParser2();
        final List<Command> commands = data.getLines().stream().map(p::parseLine).toList();
        int[] regs = new int[]{7, 0, 0, 0};

        p.doRun(new ArrayList<>(commands), regs);

        return regs[0];
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CommandParser2 p = new CommandParser2();
        final List<Command> commands = data.getLines().stream().map(p::parseLine).toList();
        // run with the new input value
        // technically we are supposed to optimize the execution, but since 261 it seems computers are now fast enough to finish that in 11 seconds
        int[] regs = new int[]{12, 0, 0, 0};

        p.doRun(new ArrayList<>(commands), regs);

        return regs[0];
    }

    static class CommandParser2 extends CommandParser
    {
        @Override
        public Command parseLine(final String line)
        {
            if (line.startsWith("tgl"))
                return new ToggleCommand(line.substring(4));
            return super.parseLine(line);
        }

        private static class ToggleCommand implements Command
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
                int addr=pc+regs[reg];
                if (addr>=memory.size())
                    return pc+1;
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
    }
}
