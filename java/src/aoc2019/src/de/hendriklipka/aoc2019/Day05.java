package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Day05 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day05().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<Integer> code = data.getLineAsInteger(",");
        IntCode intCode = IntCode.fromIntList(code);
        intCode.setDoInput(()-> BigInteger.ONE);
        final List<BigInteger> result=new ArrayList<>();
        intCode.setDoOutput(result::add);
        intCode.execute();

        return result.get(0);
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<Integer> code = data.getLineAsInteger(",");
        IntCode intCode = IntCode.fromIntList(code);
        intCode.setDoInput(() -> new BigInteger("5"));
        final List<BigInteger> result = new ArrayList<>();
        intCode.setDoOutput(result::add);
        intCode.execute();

        return result.get(0);
    }
}
