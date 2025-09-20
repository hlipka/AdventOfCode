package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
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
        return null;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return null;
    }
}
