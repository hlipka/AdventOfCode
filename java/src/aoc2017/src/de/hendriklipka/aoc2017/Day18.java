package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.MathUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class Day18 extends AocPuzzle
{
    final static int SND = 0, SET = 1, ADD = 2, MUL = 3, MOD = 4, RCV = 5, JGZ = 6;

    public static void main(String[] args)
    {
        new Day18().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        DuetSingle d= new DuetSingle();
        d.commands = data.getLines().stream().map(Command::new).toList();
        d.run();
        return d.freq;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // we now create two programs with the same instructions
        // we also need queues between them
        Queue<Long> queue0To1 = new LinkedList<>();
        Queue<Long> queue1To0 = new LinkedList<>();
        DuetParallel d0 = new DuetParallel(0, queue0To1, queue1To0);
        DuetParallel d1 = new DuetParallel(1, queue1To0, queue0To1);
        d0.commands = data.getLines().stream().map(Command::new).toList();
        d1.commands = d0.commands;

        int blockCount=0;
        while(true)
        {
            // execute one command for each, as long as they are not stopped
            if (!d0.stopped)
                d0.run();
            if (!d1.stopped)
                d1.run();
            // detect deadlock situation (the programs do y busy wait when they try to receive values)
            if (d0.waiting && d1.waiting)
            {
                blockCount++;
                if (blockCount>2)
                    break;
            }
            if (d0.stopped && d1.stopped)
                break;
        }
        return d1.sendCount;
    }

    // single thread computer for part A
    static class DuetSingle extends DuetBase
    {
        Long freq = null;

        public void run()
        {
            int pc = 0;
            while (true)
            {
                if (pc >= commands.size() || pc < 0)
                    break;
                Command c = commands.get(pc);
                Long offset = execute(c, regs);
                // a 'NULL' return signals that we did run the 'rcv' instruction
                if (null == offset)
                {
                    break;
                }
                pc += offset;
            }
        }

        @Override
        Long doRcv(final Command cmd, final Map<Character, Long> regs)
        {
            if (0 == getXValue(cmd, regs))
                return 1L;
            return null;
        }

        @Override
        void doSnd(final Command cmd, final Map<Character, Long> regs)
        {
            freq = getXValue(cmd, regs);
        }
    }

    // parallel version for part 2
    static class DuetParallel extends DuetBase
    {
        public long sendCount=0;
        private int pc;
        int pid;
        private final Queue<Long> _outQueue;
        private final Queue<Long> _inQueue;
        boolean waiting=false;
        boolean stopped=false;

        public DuetParallel(final int pid, final Queue<Long> outQueue, final Queue<Long> inQueue)
        {
            this.pid = pid;
            _outQueue = outQueue;
            _inQueue = inQueue;
            pc = 0;
            regs.put('p', (long)pid);
        }   

        public void run()
        {
            if (pc >= commands.size() || pc < 0)
            {
                stopped=true;
                return;
            }
            Command c = commands.get(pc);
            Long offset = execute(c, regs);
            pc += offset;
        }

        @Override
        Long doRcv(final Command cmd, final Map<Character, Long> regs)
        {
            if (_inQueue.isEmpty())
            { // signal that we are blocked
                waiting=true;
                return 0L; // we stay at the same instruction until we get values
            }
            waiting=false;
            regs.put(cmd.xReg, _inQueue.poll());
            return 1L;
        }

        @Override
        void doSnd(final Command cmd, final Map<Character, Long> regs)
        {
            _outQueue.offer(getXValue(cmd, regs));
            sendCount++;
        }
    }


    abstract static class DuetBase
    {
        List<Command> commands;
        Map<Character, Long> regs = new HashMap<>();

        protected Long execute(final Command cmd, final Map<Character, Long> regs)
        {
            return switch (cmd.type)
            {
                case Day18.SND ->
                {
                    doSnd(cmd, regs);
                    yield 1L;
                }
                case Day18.SET ->
                {
                    regs.put(cmd.xReg, getYValue(cmd, regs));
                    yield 1L;
                }
                case Day18.ADD ->
                {
                    regs.put(cmd.xReg, getXValue(cmd, regs) + getYValue(cmd, regs));
                    yield 1L;
                }
                case Day18.MUL ->
                {
                    regs.put(cmd.xReg, getXValue(cmd, regs) * getYValue(cmd, regs));
                    yield 1L;
                }
                case Day18.MOD ->
                {
                    // Note: we can get negative number here, and Java does 'remainder' instead of 'modulo', so use the math utils version
                    regs.put(cmd.xReg, MathUtils.mod(getXValue(cmd, regs), getYValue(cmd, regs)));
                    yield 1L;
                }
                case Day18.RCV -> doRcv(cmd, regs);
                case Day18.JGZ ->
                {
                    if (getXValue(cmd, regs) > 0)
                        yield getYValue(cmd, regs);
                    yield 1L;
                }
                default -> throw new IllegalStateException("Unknown command " + cmd.type);
            };
        }

        abstract Long doRcv(Command cmd, Map<Character, Long> regs);

        abstract void doSnd(Command cmd, Map<Character, Long> regs);

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
                case "snd" ->
                {
                    type = SND;
                    parseX(parts[1]);
                }
                case "set" ->
                {
                    type = SET;
                    parseX(parts[1]);
                    parseY(parts[2]);
                }
                case "add" ->
                {
                    type = ADD;
                    parseX(parts[1]);
                    parseY(parts[2]);
                }
                case "mul" ->
                {
                    type = MUL;
                    parseX(parts[1]);
                    parseY(parts[2]);
                }
                case "mod" ->
                {
                    type = MOD;
                    parseX(parts[1]);
                    parseY(parts[2]);
                }
                case "rcv" ->
                {
                    type = RCV;
                    parseX(parts[1]);
                }
                case "jgz" ->
                {
                    type = JGZ;
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
                yVal =-1;
            }
            else
            {
                yReg =' ';
                yVal =Long.parseLong(part);
            }
        }

        private void parseX(final String part)
        {
            if (Character.isAlphabetic(part.charAt(0)))
            {
                xReg = part.charAt(0);
                xVal =-1;
            }
            else
            {
                xReg =' ';
                xVal =Long.parseLong(part);
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
