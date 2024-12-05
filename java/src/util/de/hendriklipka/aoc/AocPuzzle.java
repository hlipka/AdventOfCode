package de.hendriklipka.aoc;

import java.io.IOException;

public abstract class AocPuzzle
{
    private final String _year;
    private final String _day;

    protected AocDataFileUtils data;

    public AocPuzzle(String year, String day)
    {
        _year = year;
        _day = day;
    }

    protected void doPuzzle(final String[] args)
    {
        boolean doExamples=true;
        boolean doPartA=true;
        for (String arg : args)
        {
            if (arg.equals("skipA"))
                doPartA=false;
            if (arg.equals("skipX"))
                doExamples=false;
        }
        try
        {
            long startTime, endTime;
            if (doPartA)
            {
                if (doExamples)
                {
                    data = new AocDataFileUtils(_year + "s", _day);
                    startTime = System.currentTimeMillis();
                    final Object resultExampleA = solvePartA();
                    endTime = System.currentTimeMillis();
                    System.out.println("example A:\n" + resultExampleA);
                    System.out.println("took " + (endTime - startTime) + "ms");
                }
                data = new AocDataFileUtils(_year, _day);
                startTime = System.currentTimeMillis();
                final Object resultA = solvePartA();
                endTime = System.currentTimeMillis();
                System.out.println("result A:\n" + resultA);
                System.out.println("took " + (endTime - startTime) + "ms");
            }
            if (doExamples)
            {
                data = new AocDataFileUtils(_year + "s", _day);
                startTime = System.currentTimeMillis();
                final Object resultExampleB = solvePartB();
                endTime = System.currentTimeMillis();
                System.out.println("example B:\n" + resultExampleB);
                System.out.println("took " + (endTime - startTime) + "ms");
            }
            data = new AocDataFileUtils(_year, _day);
            startTime = System.currentTimeMillis();
            final Object resultB = solvePartB();
            endTime = System.currentTimeMillis();
            System.out.println("result B:\n" + resultB);
            System.out.println("took " + (endTime - startTime) + "ms");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected abstract Object solvePartA() throws IOException;
    protected abstract Object solvePartB() throws IOException;
}
