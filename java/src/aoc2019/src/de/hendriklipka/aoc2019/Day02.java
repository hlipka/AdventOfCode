package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day02 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day02().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<Integer> code = data.getLineAsInteger(",");
        if (!isExample)
        {
            code.set(1, 12);
            code.set(2, 2);
        }
        IntCode intCode = new IntCode(code);
        intCode.execute();
        return intCode.get(0);
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        if (isExample)
            return -1;
        final List<Integer> program = data.getLineAsInteger(",");
        for (int noun=0;noun<100;noun++)
        {
            for (int verb=0;verb<100;verb++)
            {
                List<Integer> code= new ArrayList<>(program);
                code.set(1, noun);
                code.set(2, verb);
                IntCode intCode = new IntCode(code);
                intCode.execute();
                int result=intCode.get(0);
                if (result==19690720)
                {
                    return 100*noun+verb;
                }
            }
        }
        return -1;
    }
}
