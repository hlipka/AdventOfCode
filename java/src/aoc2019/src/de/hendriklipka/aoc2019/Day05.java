package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
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
        intCode.setDoInput(()-> 1);
        final List<Integer> result=new ArrayList<>();
        intCode.setDoOutput(result::add);
        intCode.execute();

        return result.getLast();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<Integer> code = data.getLineAsInteger(",");
        IntCode intCode = IntCode.fromIntList(code);
        intCode.setDoInput(() -> 5);
        final List<Integer> result = new ArrayList<>();
        intCode.setDoOutput(result::add);
        intCode.execute();

        return result.getLast();
    }
}
