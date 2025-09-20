package de.hendriklipka.aoc2019;

import org.apache.commons.collections4.map.LRUMap;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntCode
{
    private final int[] _code;

    LRUMap<Integer, IntInstr> codeCache;

    Consumer<Integer> _doOutput;
    Supplier<Integer> _doInput;

    public IntCode(List<Integer> code)
    {
        _code=code.stream().mapToInt(Integer::intValue).toArray();
        codeCache = new LRUMap<>(code.size());
    }

    public void execute()
    {
        int pc=0;
        while (true)
        {
            int opCode = _code[pc];
            IntInstr instr=codeCache.computeIfAbsent(opCode, this::parseInstruction);
            if (instr.isHalt())
                return;
            pc=instr.execute(pc, _code);
        }
    }

    private IntInstr parseInstruction(int opCode)
    {
        int intCode=opCode%100;
        Mode mode3=getMode(opCode>9999);
        Mode mode2=getMode(opCode%9999>999);
        Mode mode1=getMode(opCode%999>99);
        return switch (intCode)
        {
            case 1 -> new Add(mode1, mode2, mode3);
            case 2 -> new Mul(mode1, mode2, mode3);
            case 3 -> new Input(mode1);
            case 4 -> new Output(mode1);
            case 5 -> new JumpIfTrue(mode1, mode2);
            case 6 -> new JumpIfFalse(mode1, mode2);
            case 7 -> new LessThan(mode1, mode2, mode3);
            case 8 -> new Equals(mode1, mode2, mode3);
            case 99 -> new Halt();
            default -> throw new IllegalStateException("cannot parse instruction " + opCode);
        };
    }

    private Mode getMode(final boolean b)
    {
        return b?Mode.IMM:Mode.POS;
    }

    public int get(int pos)
    {
        return _code[pos];
    }

    private interface IntInstr
    {
        default boolean isHalt()
        {
            return false;
        }

        int execute(int pc, int[] mem);
    }

    private class Add implements IntInstr
    {
        Get get1, get2;
        Set set;
        public Add(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1=getAccess(mode1, 1);
            get2=getAccess(mode2, 2);
            set=getWrite(mode3, 3);
        }

        @Override
        public int execute(final int pc, final int[] mem)
        {
            set.set(get1.get(pc, mem)+get2.get(pc, mem), pc, mem);
            return pc + 4;
        }
    }

    private class Mul implements IntInstr
    {
        Get get1, get2;
        Set set;
        public Mul(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1=getAccess(mode1, 1);
            get2=getAccess(mode2, 2);
            set=getWrite(mode3, 3);
        }

        @Override
        public int execute(final int pc, final int[] mem)
        {
            set.set(get1.get(pc, mem)*get2.get(pc, mem), pc, mem);
            return pc + 4;
        }
    }

    private class JumpIfTrue implements IntInstr
    {
        Get get1, get2;

        public JumpIfTrue(final Mode mode1, final Mode mode2)
        {
            get1 = getAccess(mode1, 1);
            get2 = getAccess(mode2, 2);
        }

        @Override
        public int execute(final int pc, final int[] mem)
        {
            if (get1.get(pc, mem) != 0)
            {
                return get2.get(pc, mem);
            }
            return pc+3;
        }
    }

    private class JumpIfFalse implements IntInstr
    {
        Get get1, get2;

        public JumpIfFalse(final Mode mode1, final Mode mode2)
        {
            get1 = getAccess(mode1, 1);
            get2 = getAccess(mode2, 2);

        }

        @Override
        public int execute(final int pc, final int[] mem)
        {
            if (get1.get(pc, mem) == 0)
            {
                return get2.get(pc, mem);
            }
            return pc+3;
        }
    }

    private class LessThan implements IntInstr
    {
        Get get1, get2;
        Set set;

        public LessThan(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1 = getAccess(mode1, 1);
            get2 = getAccess(mode2, 2);
            set = getWrite(mode3, 3);

        }

        @Override
        public int execute(final int pc, final int[] mem)
        {
            set.set(get1.get(pc, mem)<get2.get(pc,mem)?1:0, pc, mem);
            return pc+4;
        }
    }

    private class Equals implements IntInstr
    {
        Get get1, get2;
        Set set;

        public Equals(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1 = getAccess(mode1, 1);
            get2 = getAccess(mode2, 2);
            set = getWrite(mode3, 3);

        }

        @Override
        public int execute(final int pc, final int[] mem)
        {
            set.set(get1.get(pc, mem) == get2.get(pc, mem) ? 1 : 0, pc, mem);
            return pc+4;
        }
    }

    private static class Halt implements IntInstr
    {
        @Override
        public boolean isHalt()
        {
            return true;
        }

        @Override
        public int execute(final int pc, final int[] mem)
        {
            return -1;
        }
    }

    private class Input implements IntInstr
    {
        Set set;
        public Input(final Mode firstMode)
        {
            set=getWrite(firstMode, 1);
        }

        @Override
        public int execute(final int pc, final int[] mem)
        {
            if (null==_doInput)
            {
                throw new IllegalStateException("cannot read input, no provider available.");
            }
            set.set(_doInput.get(), pc, mem);
            return pc + 2;
        }
    }

    private class Output implements IntInstr
    {
        Get get;
        public Output(final Mode firstMode)
        {
            get=getAccess(firstMode, 1);
        }

        @Override
        public int execute(final int pc, final int[] mem)
        {
            doOutput(get.get(pc, mem));
            return pc + 2;
        }
    }

    private void doOutput(final int value)
    {
        if (null!=_doOutput)
        {
            _doOutput.accept(value);
        }
        else
        {
            System.out.println(value);
        }
    }

    public void setDoOutput(final Consumer<Integer> doOutput)
    {
        _doOutput = doOutput;
    }

    public void setDoInput(final Supplier<Integer> doInput)
    {
        _doInput = doInput;
    }

    private Set getWrite(final Mode mode, final int offset)
    {
        return new SetPos(offset);
    }

    private Get getAccess(final Mode mode, int offset)
    {
        return switch (mode)
        {
            case POS -> new GetPos(offset);
            case IMM -> new GetImm(offset);
        };
    }

    private interface Get
    {
        int get(int pc, int[] mem);
    }

    private interface Set
    {
        void set(int value, int pc, int[] mem);
    }

    private enum Mode
    {
        IMM, POS
    }

    private record GetPos(int _offset) implements Get
    {
        @Override
        public int get(int pc, final int[] mem)
        {
            return mem[mem[pc + _offset]];
        }
    }

    private record GetImm(int _offset) implements Get
    {
        @Override
        public int get(int pc, final int[] mem)
        {
            return mem[pc + _offset];
        }
    }

    private record SetPos(int _offset) implements Set
    {
        @Override
        public void set(final int value, int pc, final int[] mem)
        {
            mem[mem[pc + _offset]] = value;
        }
    }
}
