package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class Day16 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day16().doPuzzle(args);
    }

    ForkJoinPool exec = (ForkJoinPool) Executors.newWorkStealingPool();

    @Override
    protected Object solvePartA() throws IOException
    {
        int[] digits = data.getLinesAsDigits().getFirst().stream().mapToInt(Integer::intValue).toArray();
        for (int i = 0; i < 100; i++)
        {
            digits = doFFT(digits);
        }
        int[] result = new int[8];
        System.arraycopy(digits, 0, result, 0, 8);
        return StringUtils.join(result, ' ').replace(" ", "");
    }

    // note: for the real data set, this takes about 230s per iteration
    private int[] doFFT(final int[] digits)
    {
        final int[] result = new int[digits.length];
        for (int i = 0; i < digits.length; i++)
        {
            exec.submit(new FFTDigit(result, i, digits));
        }
        // wait until all digits are finished
        while (0 != exec.getActiveThreadCount()) ;
        return result;
    }

    private record FFTDigit(int[] _result, int _i, int[] _digits) implements Runnable
        {

            @Override
            public void run()
            {
                _result[_i] = getDigit(_i, _digits);
            }
        }

    // our full data has 650 chars, so around 6.5Mio for the big set. So the sum is at most 65Mio, which fits an int
    private static int getDigit(final int pos, final int[] digits)
    {
        int sum = 0;
        // we track the phase angle to know how long each phase takes
        int phaseAngle = pos;
        int i = 0;
        while (i < digits.length)
        {
            // first, skip ahead for the zeroes
            i += Math.min(phaseAngle, pos + 1);
            phaseAngle = 0;
            // now to +1
            while (phaseAngle <= pos && i < digits.length)
            {
                sum += digits[i];
                i++;
                phaseAngle++;
            }
            // skip ahead for the zeroes
            i += (pos + 1);
            phaseAngle = 0;
            // now do -1
             while (phaseAngle <= pos && i < digits.length)
            {
                sum -= digits[i];
                i++;
                phaseAngle++;
            }
        }
        return Math.abs(sum) % 10;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<Integer> origDigits = data.getLinesAsDigits().getFirst();
        int ofs = Integer.parseInt(data.getLines().getFirst().substring(0, 7));
        int[] digits = new int[10000 * origDigits.size()];
        for (int i = 0; i < 10000; i++)
        {
            for (int j = 0; j < origDigits.size(); j++)
            {
                digits[i * origDigits.size() + j] = origDigits.get(j);
            }
        }

        // observation: to get the n-th digit in the final iteration (with 'n' being the first digit of the result we need)
        // we will skip all previous one - due to the phase expansion they will be multiplied with 0 anyway
        // so we only ever need the digits starting at the final offset
        // also, since the offset is quite close to the total length, all these values will get a phase of '1' so we can just sum them
        // we probably can speed this up when we calculate the digits backwards, then we should be able to re-use the existing sums

        int[] finalDigits = new int[digits.length - ofs];
        System.arraycopy(digits, ofs, finalDigits, 0, digits.length - ofs);

        for (int i = 0; i < 100; i++)
        {
            finalDigits = doFFT2(finalDigits, ofs);
        }
        int[] result = new int[8];
        System.arraycopy(finalDigits, 0, result, 0, 8);
        return StringUtils.join(result, ' ').replace(" ", "");
    }

    /*
        Observation
     */
    private int[] doFFT2(final int[] digits, int ofs)
    {
        final int[] result = new int[digits.length];
        for (int i = 0; i < digits.length; i++)
        {
            exec.submit(new FFTDigit2(result, i, digits));
        }
        // wait until all digits are finished
        while (0 != exec.getActiveThreadCount()) ;
        return result;
    }

    private record FFTDigit2(int[] _result, int _i, int[] _digits) implements Runnable
    {
        @Override
        public void run()
        {
            int sum=0;
            for (int j = _i; j < _digits.length; j++)
                sum+= _digits[j];
            _result[_i] = sum % 10;
        }
    }

}


