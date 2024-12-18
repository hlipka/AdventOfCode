package de.hendriklipka.aoc;

import java.io.IOException;

public abstract class AocPuzzle
{
    private final String _year;
    private final String _day;
    protected boolean isExample=false;

    protected AocDataFileUtils data;

    public AocPuzzle()
    {
        String cName=getClass().getSimpleName();
        String pName=getClass().getPackageName();
        _day=cName.toLowerCase().replace("day","");
        final var packages = pName.split("\\.");
        _year= packages[packages.length-1].replace("aoc", "");
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
                    isExample=true;
                    data = new AocDataFileUtils(_year, "ex"+_day);
                    startTime = System.currentTimeMillis();
                    final Object resultExampleA = solvePartA();
                    endTime = System.currentTimeMillis();
                    System.out.println("example A:\n" + resultExampleA);
                    System.out.println("took " + (endTime - startTime) + "ms");
                }
                isExample=false;
                data = new AocDataFileUtils(_year, "day" +_day);
                startTime = System.currentTimeMillis();
                final Object resultA = solvePartA();
                endTime = System.currentTimeMillis();
                System.out.println("result A:\n" + resultA);
                System.out.println("======================");
                System.out.println("took " + (endTime - startTime) + "ms");
                System.out.println();
                System.out.println();
            }
            if (doExamples)
            {
                isExample=true;
                data = new AocDataFileUtils(_year, "ex" +_day);
                startTime = System.currentTimeMillis();
                final Object resultExampleB = solvePartB();
                endTime = System.currentTimeMillis();
                System.out.println("example B:\n" + resultExampleB);
                System.out.println("took " + (endTime - startTime) + "ms");
            }
            isExample=false;
            data = new AocDataFileUtils(_year, "day" +_day);
            startTime = System.currentTimeMillis();
            final Object resultB = solvePartB();
            endTime = System.currentTimeMillis();
            System.out.println("result B:\n" + resultB);
            System.out.println("======================");
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
