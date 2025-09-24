package de.hendriklipka.aoc2019;

import org.apache.commons.collections4.map.LRUMap;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntCode
{
    private final Map<Integer, Integer> memory = new HashMap<>();

    LRUMap<Integer, IntInstr> codeCache;

    int relBase = 0;

    Consumer<Integer> _doOutput;
    Supplier<Integer> _doInput;
    private boolean finished = false;

    public IntCode(List<Integer> code)
    {
        for (int i = 0; i < code.size(); i++)
        {
            memory.put(i, code.get(i));
        }
        codeCache = new LRUMap<>(code.size());
    }

    public static IntCode fromIntList(List<Integer> code)
    {
        return new IntCode(code);
    }

    public static IntCode fromStringList(List<String> code)
    {
        return new IntCode(code.stream().map(Integer::parseInt).toList());
    }

    public void execute()
    {
        int pc = 0;
        while (true)
        {
            int opCode = memory.get(pc);
            IntInstr instr = codeCache.computeIfAbsent(opCode, this::parseInstruction);
            if (instr.isHalt())
            {
                finished = true;
                return;
            }
            pc = instr.execute(pc, memory);
        }
    }

    public List<String> decompile(final int maxPC)
    {
        Map<Integer, Integer> instSizes=Map.of(1,4, 2,4, 3, 2, 4,2, 5, 3, 6, 3, 7, 4, 8, 4, 9, 2, 99, 1);
        List<String> result = new ArrayList<>();
        int pc=0;
        while(memory.containsKey(pc))
        {
            int opCode = memory.get(pc);
            if (pc<maxPC)
            {
                try
                {
                    IntInstr instr = codeCache.computeIfAbsent(opCode, this::parseInstruction);
                    result.add(pc+": "+instr.decompile(pc, memory));
                    pc += instSizes.get(opCode % 100);
                }
                catch (IllegalStateException e)
                {
                    result.add(pc + ": " + opCode);
                    pc++;
                }
            }
            else
            {
                result.add(pc + ": " + opCode);
                pc++;
            }

        }
        return result;
    }

    private IntInstr parseInstruction(int opCode)
    {
        int intCode = opCode % 100;
        if (opCode>22299)
        {
            throw new IllegalArgumentException("opcode is too large: " + opCode);
        }
        Mode mode3 = getMode(opCode / 10000);
        Mode mode2 = getMode((opCode % 9999) / 1000);
        Mode mode1 = getMode((opCode % 999) / 100);
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
            case 9 -> new BaseAdjust(mode1);
            case 99 -> new Halt();
            default -> throw new IllegalStateException("cannot parse instruction " + opCode);
        };
    }

    private Mode getMode(int mode)
    {
        return switch (mode)
        {
            case 0 -> Mode.POS;
            case 1 -> Mode.IMM;
            case 2 -> Mode.REL;
            default -> throw new IllegalStateException("cannot parse mode: " + mode);
        };
    }

    public int get(int pos)
    {
        return memory.get(pos);
    }

    public boolean isFinished()
    {
        return finished;
    }

    private interface IntInstr
    {
        default boolean isHalt()
        {
            return false;
        }

        int execute(int pc, Map<Integer, Integer> mem);

        String decompile(int pc, Map<Integer, Integer> mem);
    }

    private class Add implements IntInstr
    {
        Get get1, get2;
        Set set;

        public Add(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1 = getRead(mode1, 1);
            get2 = getRead(mode2, 2);
            set = getWrite(mode3, 3);
        }

        @Override
        public int execute(final int pc, final Map<Integer, Integer> mem)
        {
            set.set(get1.get(pc, mem) + get2.get(pc, mem), pc, mem);
            return pc + 4;
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "add "+get1.decompile(pc, mem)+" + "+get2.decompile(pc, mem)+" -> "+set.decompile(pc, mem);
        }
    }

    private class Mul implements IntInstr
    {
        Get get1, get2;
        Set set;

        public Mul(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1 = getRead(mode1, 1);
            get2 = getRead(mode2, 2);
            set = getWrite(mode3, 3);
        }

        @Override
        public int execute(final int pc, final Map<Integer, Integer> mem)
        {
            set.set(get1.get(pc, mem) * get2.get(pc, mem), pc, mem);
            return pc + 4;
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "mul " + get1.decompile(pc, mem) + " * " + get2.decompile(pc, mem) + " -> " + set.decompile(pc, mem);
        }
    }

    private class JumpIfTrue implements IntInstr
    {
        Get get1, get2;

        public JumpIfTrue(final Mode mode1, final Mode mode2)
        {
            get1 = getRead(mode1, 1);
            get2 = getRead(mode2, 2);
        }

        @Override
        public int execute(final int pc, final Map<Integer, Integer> mem)
        {
            if (get1.get(pc, mem) != 0)
            {
                return get2.get(pc, mem);
            }
            return pc + 3;
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "j_if_ne_0 " + get1.decompile(pc, mem) + "? ->" + get2.decompile(pc, mem);
        }
    }

    private class JumpIfFalse implements IntInstr
    {
        Get get1, get2;

        public JumpIfFalse(final Mode mode1, final Mode mode2)
        {
            get1 = getRead(mode1, 1);
            get2 = getRead(mode2, 2);

        }

        @Override
        public int execute(final int pc, final Map<Integer, Integer> mem)
        {
            if (get1.get(pc, mem) == 0)
            {
                return get2.get(pc, mem);
            }
            return pc + 3;
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "j_if_eq_0 " + get1.decompile(pc, mem) + "? ->" + get2.decompile(pc, mem);
        }
    }

    private class LessThan implements IntInstr
    {
        Get get1, get2;
        Set set;

        public LessThan(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1 = getRead(mode1, 1);
            get2 = getRead(mode2, 2);
            set = getWrite(mode3, 3);

        }

        @Override
        public int execute(final int pc, final Map<Integer, Integer> mem)
        {
            set.set(get1.get(pc, mem) < get2.get(pc, mem) ? 1 : 0, pc, mem);
            return pc + 4;
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "less_than " + get1.decompile(pc, mem) + "<" + get2.decompile(pc, mem) + "? ->" + set.decompile(pc, mem);
        }
    }

    private class Equals implements IntInstr
    {
        Get get1, get2;
        Set set;

        public Equals(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1 = getRead(mode1, 1);
            get2 = getRead(mode2, 2);
            set = getWrite(mode3, 3);

        }

        @Override
        public int execute(final int pc, final Map<Integer, Integer> mem)
        {
            set.set(get1.get(pc, mem) == get2.get(pc, mem) ? 1 : 0, pc, mem);
            return pc + 4;
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "equals " + get1.decompile(pc, mem) + "==" + get2.decompile(pc, mem) + "? ->" + set.decompile(pc, mem);
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
        public int execute(final int pc, final Map<Integer, Integer> mem)
        {
            return -1;
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "halt";
        }
    }

    private class Input implements IntInstr
    {
        Set set;

        public Input(final Mode firstMode)
        {
            set = getWrite(firstMode, 1);
        }

        @Override
        public int execute(final int pc, final Map<Integer, Integer> mem)
        {
            if (null == _doInput)
            {
                throw new IllegalStateException("cannot read input, no provider available.");
            }
            set.set(_doInput.get(), pc, mem);
            return pc + 2;
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "input -> " + set.decompile(pc, mem);
        }
    }

    private class Output implements IntInstr
    {
        Get get;

        public Output(final Mode firstMode)
        {
            get = getRead(firstMode, 1);
        }

        @Override
        public int execute(final int pc, final Map<Integer, Integer> mem)
        {
            doOutput(get.get(pc, mem));
            return pc + 2;
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "output <-" + get.decompile(pc, mem);
        }
    }

    private class BaseAdjust implements IntInstr
    {
        Get get;

        public BaseAdjust(final Mode mode)
        {
            get = getRead(mode, 1);
        }

        @Override
        public int execute(final int pc, final Map<Integer, Integer> mem)
        {
            relBase = relBase+get.get(pc, mem);
            return pc+2;
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "adj_base " + get.decompile(pc, mem);
        }
    }


    private void doOutput(final int value)
    {
        if (null != _doOutput)
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

    private Set getWrite(final Mode mode, final Integer offset)
    {
        return switch (mode)
        {
            case POS -> new SetPos(offset);
            case IMM -> throw new IllegalStateException("Cannot set an immediate value.");
            case REL -> new SetRel(offset);
        };
    }

    private Get getRead(final Mode mode, Integer offset)
    {
        return switch (mode)
        {
            case POS -> new GetPos(offset);
            case IMM -> new GetImm(offset);
            case REL -> new GetRel(offset);
        };
    }

    private interface Get
    {
        int get(int pc, Map<Integer, Integer> mem);

        String decompile(int pc, Map<Integer, Integer> mem);
    }

    private interface Set
    {
        void set(int value, int pc, Map<Integer, Integer> mem);

        String decompile(int pc, Map<Integer, Integer> mem);
    }

    private enum Mode
    {
        IMM, POS, REL
    }

    private record GetPos(int _offset) implements Get
    {
        @Override
        public int get(int pc, final Map<Integer, Integer> mem)
        {
            return mem.getOrDefault(mem.getOrDefault(pc+_offset, 0), 0);
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "("+ mem.getOrDefault(pc + _offset, 0)+")";
        }
    }

    private record GetImm(int _offset) implements Get
    {
        @Override
        public int get(int pc, final Map<Integer, Integer> mem)
        {
            return mem.getOrDefault(pc + _offset, 0);
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return ""+mem.getOrDefault(pc + _offset, 0);
        }
    }

    private final class GetRel implements Get
    {
        private final int _offset;

        private GetRel(int _offset)
        {
            this._offset = _offset;
        }

        @Override
        public int get(int pc, final Map<Integer, Integer> mem)
        {
            return mem.getOrDefault(relBase+mem.getOrDefault(pc+_offset, 0), 0);
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "(rel+ " + mem.getOrDefault(pc + _offset, 0) + ")";
        }
    }


    private record SetPos(int _offset) implements Set
    {
        @Override
        public void set(final int value, int pc, final Map<Integer, Integer> mem)
        {
            mem.put(mem.get(pc+_offset), value);
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "(" + mem.getOrDefault(pc + _offset, 0) + ")";
        }
    }

    private final class SetRel implements Set
    {
        private final Integer _offset;

        private SetRel(Integer _offset)
        {
            this._offset = _offset;
        }

        @Override
        public void set(final int value, int pc, final Map<Integer, Integer> mem)
        {
            mem.put(relBase+mem.getOrDefault(pc+_offset, 0), value);
        }

        @Override
        public String decompile(int pc, Map<Integer, Integer> mem)
        {
            return "(rel+ " + mem.getOrDefault(pc + _offset, 0) + ")";
        }
    }

    public static class InputProvider implements Supplier<Integer>
    {
        Queue<Integer> _values = new ArrayDeque<>();

        public InputProvider(int... values)
        {
            for (int i : values)
            {
                _values.add(i);
            }
        }

        @Override
        public Integer get()
        {
            return _values.poll();
        }
    }

    public static class OutputCollector implements Consumer<Integer>
    {
        private List<Integer> result = new ArrayList<>();

        @Override
        public void accept(final Integer integer)
        {
            result.add(integer);
        }

        public List<Integer> getResult()
        {
            return result;
        }
    }

    public static class Pipe implements Supplier<Integer>, Consumer<Integer>
    {
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        private int lastValue;

        @Override
        public void accept(final Integer value)
        {
            if (null == value)
            {
                throw new IllegalArgumentException("value is null");
            }
            if (!queue.offer(value))
            {
                throw new IllegalStateException("queue is full");
            }
            lastValue = value;
        }

        @Override
        public Integer get()
        {
            try
            {
                return queue.take();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }

        public int getLastValue()
        {
            return lastValue;
        }
    }
}
