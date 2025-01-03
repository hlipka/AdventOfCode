package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.primes.Primes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day23 extends AocPuzzle
{
    final static int SET = 1, SUB = 2, MUL = 3, JNZ = 4;


    public static void main(String[] args)
    {
        new Day23().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        if (isExample)
            return -1;
        CoPro d=new CoPro();

        d.commands = data.getLines().stream().map(Command::new).toList();
        d.run();

        System.out.println("h="+d.regs.get('h'));

        return d.mulCount;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        if (isExample)
            return -1;
        // what part B actually calculates:
        // from start value to end value, with a given step size, how many numbers are not prime
        // the algorithm is: check _all_ pairs of values from 2 to b, see whether their product matches b, do not exit the loop early

        final List<Command> commands = data.getLines().stream().map(Command::new).toList();

        long startValue=commands.get(0).yVal*commands.get(4).yVal-commands.get(5).yVal;
        long endValue=startValue-commands.get(7).yVal;
        long stepSize=-commands.get(30).yVal;
        int nonPrimes=0;

        for (long num=startValue;num<=endValue;num+=stepSize)
        {
            if (!Primes.isPrime((int)num))
                nonPrimes++;
        }

        return nonPrimes;
    }

    // computer for part A
    static class CoPro
    {
        int mulCount=0;

        public void run()
        {
            int pc = 0;
            while (pc < commands.size() && pc >= 0)
            {
                Command c = commands.get(pc);
                Long offset = execute(c, regs);
                pc += offset;
            }
        }


        List<Command> commands;
        Map<Character, Long> regs = new HashMap<>();

        protected Long execute(final Command cmd, final Map<Character, Long> regs)
        {
            return switch (cmd.type)
            {
                case SET ->
                {
                    regs.put(cmd.xReg, getYValue(cmd, regs));
                    yield 1L;
                }
                case SUB ->
                {
                    if (cmd.xReg=='h')
                        System.out.println("sub from H");
                    regs.put(cmd.xReg, getXValue(cmd, regs) - getYValue(cmd, regs));
                    yield 1L;
                }
                case MUL ->
                {
                    mulCount++;
                    regs.put(cmd.xReg, getXValue(cmd, regs) * getYValue(cmd, regs));
                    yield 1L;
                }
                case JNZ ->
                {
                    if (getXValue(cmd, regs) != 0)
                        yield getYValue(cmd, regs);
                    yield 1L;
                }
                default -> throw new IllegalStateException("Unknown command " + cmd.type);
            };
        }

        private Long getYValue(final Command cmd, final Map<Character, Long> regs)
        {
            if (cmd.yReg != ' ')
                return regs.getOrDefault(cmd.yReg, 0L);
            return cmd.yVal;
        }

        protected Long getXValue(final Command cmd, final Map<Character, Long> regs)
        {
            if (cmd.xReg != ' ')
                return regs.getOrDefault(cmd.xReg, 0L);
            return cmd.xVal;
        }
    }



    static class Command
    {
        final int type;
        char xReg;
        char yReg;
        long xVal;
        long yVal;

        Command(String line)
        {
            String[] parts = StringUtils.split(line, " ");
            switch (parts[0])
            {
                case "set" ->
                {
                    type = SET;
                    parseX(parts[1]);
                    parseY(parts[2]);
                }
                case "sub" ->
                {
                    type = SUB;
                    parseX(parts[1]);
                    parseY(parts[2]);
                }
                case "mul" ->
                {
                    type = MUL;
                    parseX(parts[1]);
                    parseY(parts[2]);
                }
                case "jnz" ->
                {
                    type = JNZ;
                    parseX(parts[1]);
                    parseY(parts[2]);
                }
                default -> throw new IllegalArgumentException("unknown command: " + line);
            }
        }

        private void parseY(final String part)
        {
            if (Character.isAlphabetic(part.charAt(0)))
            {
                yReg = part.charAt(0);
                yVal = -1;
            }
            else
            {
                yReg = ' ';
                yVal = Long.parseLong(part);
            }
        }

        private void parseX(final String part)
        {
            if (Character.isAlphabetic(part.charAt(0)))
            {
                xReg = part.charAt(0);
                xVal = -1;
            }
            else
            {
                xReg = ' ';
                xVal = Long.parseLong(part);
            }
        }

        @Override
        public String toString()
        {
            return "Command{" +
                   "type=" + type +
                   ", xR=" + xReg +
                   ", yR=" + yReg +
                   ", xV=" + xVal +
                   ", yV=" + yVal +
                   '}';
        }
    }
}
