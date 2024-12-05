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
            final List<Command> commands = AocDataFileUtils.getLines("2016", "day12").stream().map(Day12a::parseLine).toList();
            int[] regs = new int[]{0,0,0,0};
            doRun(commands, regs);
            System.out.println(regs[0]);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void doRun(final List<Command> commands, final int[] regs)
    {
        int pc=0;
        while (pc<commands.size())
        {
            Command cmd=commands.get(pc);
            pc = cmd.execute(pc, regs);
        }
    }

    private static Command parseLine(String line)
    {
        if (line.startsWith("cpy"))
        {
            return new CopyCommand(line.substring(4));
        }
        if (line.startsWith("inc"))
        {
            return new IncCommand(line.substring(4));
        }
        if (line.startsWith("dec"))
        {
            return new DecCommand(line.substring(4));
        }
        if (line.startsWith("jnz"))
        {
            return new JumpCommand(line.substring(4));
        }
        throw new IllegalArgumentException("unknown command in " + line);
    }

    private interface Command
    {
        int execute(int pc, int[] regs);
    }

    private static class CopyCommand implements Command
    {
        int argValue=-1;
        int argReg=-1;
        int targetReg=-1;

        public CopyCommand(final String line)
        {
            String[] args = line.split(" ", -1);
            if (Character.isDigit(args[0].charAt(0)))
            {
                argValue=Integer.parseInt(args[0]);
            }
            else
            {
                argReg=args[0].charAt(0) - 'a';
            }
            targetReg = args[1].charAt(0) - 'a';
        }

        @Override
        public int execute(final int pc, final int[] regs)
        {
            if (-1!=argValue)
            {
                regs[targetReg] = argValue;
            }
            else
            {
                regs[targetReg] = regs[argReg];
            }
            return pc+1;
        }
    }

    private static class IncCommand implements Command
    {
        private final int reg;

        public IncCommand(final String line)
        {
            reg=line.charAt(0)-'a';
        }

        @Override
        public int execute(final int pc, final int[] regs)
        {
            regs[reg]++;
            return pc+1;
        }
    }

    private static class DecCommand implements Command
    {
        private final int reg;

        public DecCommand(final String line)
        {
            reg = line.charAt(0) - 'a';
        }

        @Override
        public int execute(final int pc, final int[] regs)
        {
            regs[reg]--;
            return pc + 1;
        }
    }

    private static class JumpCommand implements Command
    {
        int argValue = -1;

        private int argReg;
        private final int dist;

        public JumpCommand(final String line)
        {
            String[] args = line.split(" ");
            if (Character.isDigit(args[0].charAt(0)))
            {
                argValue = Integer.parseInt(args[0]);
            }
            else
            {
                argReg = args[0].charAt(0) - 'a';
            }
            dist=Integer.parseInt(args[1]);
        }

        @Override
        public int execute(final int pc, final int[] regs)
        {
            int arg=(-1==argValue)? regs[argReg]:argValue;
            if (arg==0)
            {
                return pc+1;
            }
            return pc+dist;
        }
    }
}
