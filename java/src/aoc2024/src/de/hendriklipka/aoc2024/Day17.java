package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day17 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day17().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<String> lines = data.getLines();
        Computer computer = new Computer(lines);
        computer.run();
        return StringUtils.join(computer.output, ",");
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final long startTime=System.currentTimeMillis();
        final List<String> lines = data.getLines();
        Computer parsed = new Computer(lines);
        List<String> empty=new ArrayList<>();
        return Stream.iterate(0L, i -> i + 1L).parallel().filter(i->{
            if (0==(i%100000000))
            {
                long time=System.currentTimeMillis()-startTime;
                if (time>0)
                System.out.println(i+" / "+time+"ms / "+(i/time)+" iter/ms");
            }
            Computer c = new Computer(empty);
            c.dayB=true;
            c.program = parsed.program;
            c.regA= i;
            c.run();
            return c.output.equals(c.program);

        }).findFirst().orElse(-1L);
    }

    private static class Computer
    {
        List<Long> output = new ArrayList<>(20);
        long regA=0, regB=0, regC=0;
        int instr=0;
        List<Long> program;
        boolean dayB=false;

        public Computer(final List<String> lines)
        {
            for (String line: lines)
            {
                if (line.startsWith("Register A:"))
                    regA = AocParseUtils.getAllNumbersFromLine(line).get(0);
                if (line.startsWith("Register B:"))
                    regB = AocParseUtils.getAllNumbersFromLine(line).get(0);
                if (line.startsWith("Register C:"))
                    regC = AocParseUtils.getAllNumbersFromLine(line).get(0);
                if (line.startsWith("Program:"))
                    program=AocParseUtils.splitLineToLongs((line.substring(9)),',');
            }
        }

        public void run()
        {
            while(instr>=0 && instr<program.size())
            {
                long instruction = program.get(instr);
                long operand  = program.get(instr+1);
                switch ((int)instruction)
                {
                    case 0: regA=divide(combo(operand));
                            break;
                    case 1: regB=regB^operand;
                        break;
                    case 2: regB= combo(operand)%8;
                        break;
                    case 3: if (regA!=0)
                            {
                                instr=(int)(operand-2); // account for the increment we always do
                            }
                        break;
                    case 4: regB=regB^regC;
                        break;
                    case 5:
                        final long value = combo(operand) % 8;
                        output.add(value);
                        if (dayB) // for partB we check whether we can exit already
                        {
                            final int size = output.size();
                            if (program.get(size-1)!=value) // wrote a wrong value
                                return;
                            if (size == program.size()) // output size reached
                                return;
                        }
                        break;
                    case 6: regB=divide(combo(operand));
                        break;
                    case 7: regC=divide(combo(operand));
                        break;
                }
                instr+=2;
            }
        }

        private long divide(final long operand)
        {
            long denom=(int)Math.pow(2, operand);
            return regA/denom;
        }

        private long combo(long operand)
        {
            return switch ((int)operand)
            {
                case 0, 1, 2, 3 -> operand;
                case 4 -> regA;
                case 5 -> regB;
                case 6 -> regC;
                default -> throw new IllegalStateException("Operand " + operand + " not allowed");
            };
        }
    }
}
