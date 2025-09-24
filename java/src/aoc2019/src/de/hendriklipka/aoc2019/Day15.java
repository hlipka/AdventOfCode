package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

public class Day15 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day15().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> code = data.getFirstLineWords(",");
        IntCode intCode = IntCode.fromStringList(code);
        System.out.println(StringUtils.join(intCode.decompile(253),"\n"));
        return -1;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return null;
    }
}
