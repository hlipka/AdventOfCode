package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.math3.linear.*;

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
        return data.getStringBlocks().stream().map(this::createRealMachine).parallel().mapToLong(Machine::solveLarge).sum();
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
            long minCoins=Long.MAX_VALUE;
            for (int aSteps=0; aSteps<101; aSteps++)
            {
                long xRemaining=x-aSteps*xa;
                if (xRemaining<0)
                    break;
                if (xRemaining%xb==0)
                {
                    final long bSteps = xRemaining / xb;
                    if (aSteps*ya+bSteps*yb!=y)
                    {
                        continue;
                    }
                    long coins= aSteps * 3 + bSteps;
                    if (coins<minCoins)
                    {
                        minCoins=coins;
                    }
                }
            }
            if (Long.MAX_VALUE==minCoins)
                return 0;
            return minCoins;
        }

        @Override
        public String toString()
        {
            return "Machine{" +
                   "xa=" + xa +
                   ", xb=" + xb +
                   ", ya=" + ya +
                   ", yb=" + yb +
                   ", x=" + x +
                   ", y=" + y +
                   '}';
        }

        // we actually need to solve a linear equation system
        // lets cheat a bit and use Commons Math
        public long solveLarge()
        {
            RealMatrix coefficients =
                    new Array2DRowRealMatrix(new double[][] { { xa, xb }, { ya, yb } },
                            false);
            DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
            RealVector constants = new ArrayRealVector(new double[] { x, y }, false);
            RealVector solution = solver.solve(constants);
            double aStepsD = solution.getEntry(0);
            double bStepsD = solution.getEntry(1);
            long aSteps=Math.round(aStepsD);
            long bSteps=Math.round(bStepsD);
            // verify that we have an integer solution
            if (aSteps*xa+bSteps*xb==x && aSteps*ya+bSteps*yb==y)
            {
                return aSteps*3+bSteps;
            }
            return 0;
        }
    }
}
