package de.hendriklipka.aoc2016.day12;

import de.hendriklipka.aoc2016.Day23;

import java.util.List;

/**
 * User: hli
 */
public class CommandParser
{
    public void doRun(final List<Command> commands, final int[] regs)
    {
        int pc=0;
        while (pc<commands.size())
        {
            Command cmd=commands.get(pc);
            pc = cmd.execute(pc, regs, commands);
        }
    }

    public Command parseLine(String line)
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
        if (line.startsWith("tgl"))
        {
            return new ToggleCommand(line.substring(4));
        }
        throw new IllegalArgumentException("unknown command in " + line);
    }
}
