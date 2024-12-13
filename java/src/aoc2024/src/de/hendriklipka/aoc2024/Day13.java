package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.MathUtils;

import java.io.IOException;
import java.util.List;

public class Day13 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day13().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        return data.getStringBlocks().stream().map(this::createMachine).mapToLong(Machine::solve).sum();
    }

    private Machine createMachine(final List<String> b)
    {
        return new Machine(b.get(0), b.get(1), b.get(2));
    }

    private Machine createRealMachine(final List<String> b)
    {
        final Machine machine = new Machine(b.get(0), b.get(1), b.get(2));
        machine.x+=10000000000000L;
        machine.y+=10000000000000L;
        return machine;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return data.getStringBlocks().stream().map(this::createRealMachine).mapToLong(Machine::solve).sum();
    }

    private static class Machine
    {
        long xa,xb,ya,yb,x,y;

        Machine(String lineA, String lineB, String lineResult)
        {
            xa=AocParseUtils.parseLongFromString(lineA, "Button A: X\\+(\\d+), Y\\+\\d+");
            ya=AocParseUtils.parseLongFromString(lineA, "Button A: X\\+\\d+, Y\\+(\\d+)");
            xb=AocParseUtils.parseLongFromString(lineB, "Button B: X\\+(\\d+), Y\\+\\d+");
            yb=AocParseUtils.parseLongFromString(lineB, "Button B: X\\+\\d+, Y\\+(\\d+)");
            x=AocParseUtils.parseLongFromString(lineResult, "Prize: X=(\\d+), Y=\\d+");
            y=AocParseUtils.parseLongFromString(lineResult, "Prize: X=\\d+, Y=(\\d+)");
        }

        public long solve()
        {
            long[] result= MathUtils.solveLongDualEquation(xa, xb, x, ya, yb, y);
            if (null!=result)
            {
                return result[0]*3+result[1];
            }
            return 0;
        }
    }
}
