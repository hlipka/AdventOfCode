package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.List;

public class Day07 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day07().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> calibs = data.getLines();
        return calibs.stream().map(Calibration::new).filter(Calibration::isValid).mapToLong(c->c.result).sum();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<String> calibs = data.getLines();
        return calibs.stream().map(Calibration::new).filter(Calibration::isValid2).mapToLong(c -> c.result).sum();
    }

    private static class Calibration
    {
        long result;
        List<Integer> operators;

        public Calibration(String line)
        {
            String[] parts1=line.split(":");
            result = Long.parseLong(parts1[0]);
            operators = AocParseUtils.splitLineToInts(parts1[1], ' ');
        }

        public boolean isValid()
        {
            long current = operators.get(0);

            return checkCalibration(current, 1);
        }

        private boolean checkCalibration(final long current, final int pos)
        {
            if (pos== operators.size())
            {
                return current == result;
            }
            long next=operators.get(pos);

            return checkCalibration(current+next, pos+1) || checkCalibration(current*next, pos+1);
        }
        public boolean isValid2()
        {
            long current = operators.get(0);

            return checkCalibration2(current, 1);
        }

        private boolean checkCalibration2(final long current, final int pos)
        {
            // early exit - numbers cannot get smaller
            if (current>result) return false;
            if (pos== operators.size())
            {
                return current == result;
            }
            long next=operators.get(pos);

            // try the operations first which make the result much larger so we can exit earlier
            return checkCalibration2(current*next, pos+1)
                   || checkCalibration2(Long.parseLong(Long.toString(current) + next), pos + 1)
                   || checkCalibration2(current+next, pos+1)
                    ;
        }
    }
}
