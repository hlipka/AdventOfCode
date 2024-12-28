package de.hendriklipka.aoc2016;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc2016.day12.Command;
import de.hendriklipka.aoc2016.day12.CommandParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class    Day25 extends AocPuzzle
{
    int outCount=0;
    int lastState=-1;
    public static void main(String[] args)
    {
        new Day25().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        if (isExample)
            return "";
        CommandParser p = new CommandParser3();
        final List<Command> commands = data.getLines().stream().map(p::parseLine).toList();
        // brute-force over register start values
        int regValue=0;
        while (true)
        {
            outCount=0;
            lastState=-1;
            int[] regs = new int[]{regValue, 0, 0, 0};

            try
            {
                p.doRun(new ArrayList<>(commands), regs);
            }
            // when we got a non-working solution, ignore it and go to the next value
            catch (IllegalStateException ignored)
            {
                regValue++;
                continue;
            }
            break;
        }
        return regValue;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return "";
    }

    class CommandParser3 extends CommandParser
    {
        @Override
        public Command parseLine(final String line)
        {
            if (line.startsWith("out"))
            {
                return new OutCommand(line.substring(4));
            }

            return super.parseLine(line);
        }

        @Override
        public void doRun(final List<Command> commands, final int[] regs)
        {
            int pc = 0;
            // when the output toggled OK 1000 times we accept this as working
            while (pc < commands.size() && outCount<1000)
            {
                Command cmd = commands.get(pc);
                pc = cmd.execute(pc, regs, commands);
            }
        }
    }

    private class OutCommand implements Command
    {
        private final int reg;
        private final String _argLine;

        public OutCommand(final String line)
        {
            _argLine=line;
            reg=line.charAt(0)-'a';
        }

        @Override
        public int execute(final int pc, final int[] regs, final List<Command> memory)
        {
            // the basic logic happens here:
            // we get the output value, and check that it gets toggled with each output (it also must be 0 or 1)
            final var outValue = regs[reg];
            if (0!=outValue && 1!=outValue)
            {
                throw new IllegalStateException("try to output "+outValue);
            }
            if (lastState==outValue)
            {
                throw new IllegalStateException("did not toggle output");
            }
            lastState = outValue;
            outCount++; // count how often we toggled
            return pc+1;
        }

        @Override
        public String getArgLine()
        {
            return _argLine;
        }
    }
}
