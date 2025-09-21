package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Day09 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day09().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> code=data.getFirstLineWords(",");
        IntCode intCode=IntCode.fromStringList(code);
        intCode.setDoInput(() -> new BigInteger("1"));
        final List<BigInteger> result = new ArrayList<>();
        intCode.setDoOutput(result::add);
        intCode.execute();
        return result.getLast();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<String> code = data.getFirstLineWords(",");
        IntCode intCode = IntCode.fromStringList(code);
        intCode.setDoInput(() -> new BigInteger("2"));
        final List<BigInteger> result = new ArrayList<>();
        intCode.setDoOutput(result::add);
        intCode.execute();
        return result.getLast();
    }
}
