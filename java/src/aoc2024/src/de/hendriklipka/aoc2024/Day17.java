package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        final List<String> lines = data.getLines();
        Computer parsed = new Computer(lines);

        // from observing how fast we get new digits in the result, this is the first with full 16 output values
        // the digits appear at 1/8/64/512 and so on
        // (got these by printing out the results and the start values)
        // from observation we also know that the periods for each digit are 1/8/64/512 and so on
        // so we need to know when the periods start, and which digits never change
        // (the periods are how long each numbers stays the same, not when it appears again)

        // when we start at the above value, and log whenever a digit changes for the first time (and to which value)
        // we that:
        // - the start values are 7,7,7,7,7,7,7,7,7,7,7,7,7,7,3,7
        // - the values for the first digit are 7,7,5,4,3,2,1,0, 3,7,4,6,3,2,1,0,7,7,7,0,3,3,1,0
        // - we also see that the periods are, again 1/8/64/512
        //   (but they look twice as big since they see twice the same number at the start
        // - it seems that all follow the same number sequence that the first digit does

        // with the above progression of the digits, and the period of the digits, the last digit can only advance 8 times until we get another digit

        // question: does the second-to-last-digit ever change?

        // find the range where the last digit is valid
        // at the start, look at the value of the previous digit
        // if it does not match, jump forward (by its period) until we have a matching value
        //   - we need to stay in the period of the current digit
        // recursively go to the left
        // either we find a full match (then it's the first one)
        // if not, go up and jump forward until we find the next matching number

        return findStartValue(parsed, 35184372088832L, 15);

    }

    private long findStartValue(final Computer parsed, final long rangeStart, final int digit)
    {
        if (digit==-1)
            return rangeStart;
        final long programDigit = parsed.program.get(digit);
        final long period = getPeriod(digit);
        for (int i=0; i<8; i++)
        {
            final long currentRangeStart = rangeStart + (long) i * period;
            final List<Long> result = runComputer(parsed, currentRangeStart);
            final Long currentResultDigit = result.get(digit);
            if (currentResultDigit == programDigit)
            {
                long innerStartValue=findStartValue(parsed, currentRangeStart, digit-1);
                if (0!=innerStartValue)
                    return innerStartValue;
            }
        }
        return 0;
    }

    private static long getPeriod(final int digit)
    {
        long result=1;
        for (int i=0; i<digit; i++)
            result *=8;
        return result;
    }

    private static List<Long> runComputer(final Computer parsed, final long currentRegA)
    {
        Computer computer = new Computer(new ArrayList<>());
        computer.dayB=true;
        computer.program = parsed.program;
        computer.regA= currentRegA;
        computer.run();
        return computer.output;
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
                        output.add(combo(operand) % 8);
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
