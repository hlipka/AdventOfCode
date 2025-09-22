package de.hendriklipka.aoc2019;

import org.apache.commons.collections4.map.LRUMap;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntCode
{
    private final Map<BigInteger, BigInteger> memory = new HashMap<>();

    LRUMap<BigInteger, IntInstr> codeCache;

    Consumer<BigInteger> _doOutput;
    Supplier<BigInteger> _doInput;
    private boolean finished = false;

    BigInteger relBase = BigInteger.ZERO;

    private static final BigInteger MAX_OPCODE = new BigInteger("22299");
    private static final BigInteger FOUR = new BigInteger("4");
    private static final BigInteger THREE = new BigInteger("3");

    public static IntCode fromIntList(List<Integer> code)
    {
        return new IntCode(code.stream().map(i ->
        {
            return new BigInteger(Integer.toString(i));
        }).toList());
    }

    public static IntCode fromStringList(List<String> code)
    {
        return new IntCode(code.stream().map(BigInteger::new).toList());
    }

    private IntCode(List<BigInteger> code)
    {
        for (int i = 0; i < code.size(); i++)
        {
            memory.put(new BigInteger(Integer.toString(i)), code.get(i));
        }
        codeCache = new LRUMap<>(code.size());
    }

    public void execute()
    {
        BigInteger pc = BigInteger.ZERO;
        while (true)
        {
            BigInteger opCode = memory.get(pc);
            IntInstr instr = codeCache.computeIfAbsent(opCode, this::parseInstruction);
            if (instr.isHalt())
            {
                finished = true;
                return;
            }
            pc = instr.execute(pc, memory);
        }
    }

    private IntInstr parseInstruction(BigInteger opCode)
    {
        if (opCode.compareTo(MAX_OPCODE) > 0)
        {
            throw new IllegalArgumentException("opcode is too large: " + opCode);
        }
        final var opCodeInt = opCode.intValue();
        int intCode = opCodeInt % 100;
        Mode mode3 = getMode(opCodeInt / 10000);
        Mode mode2 = getMode((opCodeInt % 9999)/1000);
        Mode mode1 = getMode((opCodeInt % 999)/100);
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

    public int getMemValue(int pos)
    {
        return memory.get(new BigInteger(Integer.toString(pos))).intValue();
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

        BigInteger execute(BigInteger pc, Map<BigInteger, BigInteger> mem);
    }

    public static class Pipe implements Supplier<BigInteger>, Consumer<BigInteger>
    {
        BlockingQueue<BigInteger> queue=new LinkedBlockingQueue<>();
        private BigInteger lastValue;

        @Override
        public void accept(final BigInteger value)
        {
            if (null==value)
            {
                throw new  IllegalArgumentException("value is null");
            }
            if (!queue.offer(value))
            {
                throw new IllegalStateException("queue is full");
            }
            lastValue=value;
        }

        @Override
        public BigInteger get()
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
            return lastValue.intValue();
        }
    }

    private class Add implements IntInstr
    {
        Get get1, get2;
        Set set;

        public Add(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1 = getRead(mode1, BigInteger.ONE);
            get2 = getRead(mode2, BigInteger.TWO);
            set = getWrite(mode3, THREE);
        }

        @Override
        public BigInteger execute(final BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            set.set(get1.get(pc, mem).add(get2.get(pc, mem)), pc, mem);
            return pc.add(FOUR);
        }
    }

    private class Mul implements IntInstr
    {
        Get get1, get2;
        Set set;

        public Mul(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1 = getRead(mode1, BigInteger.ONE);
            get2 = getRead(mode2, BigInteger.TWO);
            set = getWrite(mode3, THREE);
        }

        @Override
        public BigInteger execute(final BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            set.set(get1.get(pc, mem).multiply(get2.get(pc, mem)), pc, mem);
            return pc.add(FOUR);
        }
    }

    private class JumpIfTrue implements IntInstr
    {
        Get get1, get2;

        public JumpIfTrue(final Mode mode1, final Mode mode2)
        {
            get1 = getRead(mode1, BigInteger.ONE);
            get2 = getRead(mode2, BigInteger.TWO);
        }

        @Override
        public BigInteger execute(final BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            if (!get1.get(pc, mem).equals(BigInteger.ZERO))
            {
                return get2.get(pc, mem);
            }
            return pc.add(THREE);
        }
    }

    private class JumpIfFalse implements IntInstr
    {
        Get get1, get2;

        public JumpIfFalse(final Mode mode1, final Mode mode2)
        {
            get1 = getRead(mode1, BigInteger.ONE);
            get2 = getRead(mode2, BigInteger.TWO);

        }

        @Override
        public BigInteger execute(final BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            if (get1.get(pc, mem).equals(BigInteger.ZERO))
            {
                return get2.get(pc, mem);
            }
            return pc.add(THREE);
        }
    }

    private class LessThan implements IntInstr
    {
        Get get1, get2;
        Set set;

        public LessThan(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1 = getRead(mode1, BigInteger.ONE);
            get2 = getRead(mode2, BigInteger.TWO);
            set = getWrite(mode3, THREE);

        }

        @Override
        public BigInteger execute(final BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            set.set((get1.get(pc, mem).compareTo(get2.get(pc, mem))<0) ? BigInteger.ONE : BigInteger.ZERO, pc, mem);
            return pc.add(FOUR);
        }
    }

    private class Equals implements IntInstr
    {
        Get get1, get2;
        Set set;

        public Equals(final Mode mode1, final Mode mode2, final Mode mode3)
        {
            get1 = getRead(mode1, BigInteger.ONE);
            get2 = getRead(mode2, BigInteger.TWO);
            set = getWrite(mode3, THREE);

        }

        @Override
        public BigInteger execute(final BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            set.set(get1.get(pc, mem).equals(get2.get(pc, mem)) ? BigInteger.ONE : BigInteger.ZERO, pc, mem);
            return pc.add(FOUR);
        }
    }

    private class BaseAdjust implements IntInstr
    {
        Get get;

        public BaseAdjust(final Mode mode)
        {
            get = getRead(mode, BigInteger.ONE);
        }

        @Override
        public BigInteger execute(final BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            relBase = relBase.add(get.get(pc, mem));
            return pc.add(BigInteger.TWO);
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
        public BigInteger execute(final BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            return BigInteger.ZERO;
        }
    }

    private class Input implements IntInstr
    {
        Set set;

        public Input(final Mode firstMode)
        {
            set = getWrite(firstMode, BigInteger.ONE);
        }

        @Override
        public BigInteger execute(final BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            if (null == _doInput)
            {
                throw new IllegalStateException("cannot read input, no provider available.");
            }
            set.set(_doInput.get(), pc, mem);
            return pc.add(BigInteger.TWO);
        }
    }

    private class Output implements IntInstr
    {
        Get get;

        public Output(final Mode firstMode)
        {
            get = getRead(firstMode, BigInteger.ONE);
        }

        @Override
        public BigInteger execute(final BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            doOutput(get.get(pc, mem));
            return pc.add(BigInteger.TWO);
        }
    }

    private void doOutput(final BigInteger value)
    {
        if (null != _doOutput)
        {
            _doOutput.accept(value);
        }
        else
        {
            System.out.println("output: "+value);
        }
    }

    public void setDoOutput(final Consumer<BigInteger> doOutput)
    {
        _doOutput = doOutput;
    }

    public void setDoInput(final Supplier<BigInteger> doInput)
    {
        _doInput = doInput;
    }

    private Set getWrite(final Mode mode, final BigInteger offset)
    {
        return switch (mode)
        {
            case POS -> new SetPos(offset);
            case IMM -> throw new IllegalStateException("Cannot set an immediate value.");
            case REL -> new SetRel(offset);
        };
    }

    private Get getRead(final Mode mode, BigInteger offset)
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
        BigInteger get(BigInteger pc, Map<BigInteger, BigInteger> mem);
    }

    private interface Set
    {
        void set(BigInteger value, BigInteger pc, Map<BigInteger, BigInteger> mem);
    }

    private enum Mode
    {
        IMM, POS, REL
    }

    private record GetPos(BigInteger _offset) implements Get
    {
        @Override
        public BigInteger get(BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            return mem.getOrDefault(mem.getOrDefault(pc.add(_offset), BigInteger.ZERO), BigInteger.ZERO);
        }
    }

    private record GetImm(BigInteger _offset) implements Get
    {
        @Override
        public BigInteger get(BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            return mem.getOrDefault(pc.add(_offset), BigInteger.ZERO);
        }
    }

    private final class GetRel implements Get
    {
        private final BigInteger _offset;

        private GetRel(BigInteger _offset)
        {
            this._offset = _offset;
        }

        @Override
        public BigInteger get(BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            return mem.getOrDefault(relBase.add(mem.getOrDefault(pc.add(_offset), BigInteger.ZERO)), BigInteger.ZERO);
        }
    }

    private record SetPos(BigInteger _offset) implements Set
    {
        @Override
        public void set(final BigInteger value, BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            mem.put(mem.get(pc.add(_offset)), value);
        }
    }

    private final class SetRel implements Set
    {
        private final BigInteger _offset;

        private SetRel(BigInteger _offset)
        {
            this._offset = _offset;
        }

        @Override
        public void set(final BigInteger value, BigInteger pc, final Map<BigInteger, BigInteger> mem)
        {
            mem.put(relBase.add(mem.getOrDefault(pc.add(_offset), BigInteger.ZERO)), value);
        }
    }

    public static class InputProvider implements Supplier<BigInteger>
    {
        Queue<BigInteger> _values = new ArrayDeque<>();

        public InputProvider(BigInteger... values)
        {
            Collections.addAll(_values, values);
        }

        @Override
        public BigInteger get()
        {
            return _values.poll();
        }
    }

    public static class OutputCollector implements Consumer<BigInteger>
    {
        private final List<BigInteger> result = new ArrayList<>();

        @Override
        public void accept(final BigInteger integer)
        {
            result.add(integer);
        }

        public List<BigInteger> getResult()
        {
            return result;
        }
    }
}
