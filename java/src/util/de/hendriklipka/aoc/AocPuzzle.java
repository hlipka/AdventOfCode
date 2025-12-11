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
        boolean doReal=true;
        boolean doPartA=true;
        for (String arg : args)
        {
            if (arg.equals("skipA"))
                doPartA=false;
            if (arg.equals("skipX"))
                doExamples=false;
            if (arg.equals("skipR"))
                doReal=false;
        }
        try
        {
            long startTime, endTime;
            if (doPartA)
            {
                if (doExamples)
                {
                    isExample=true;
                    if (AocDataFileUtils.getDataFileName(_year, "ex" + _day).exists())
                    {
                        handleExampleFilePartA(_day);
                    }
                    else if (AocDataFileUtils.getDataFileName(_year, "exA" + _day).exists())
                    {
                        handleExampleFilePartA(_day);
                    }
                    else
                    {
                        int count=1;
                        while (AocDataFileUtils.getDataFileName(_year, "ex" + _day+"_"+count).exists())
                        {
                            handleExampleFilePartA(_day + "_" + count);
                            System.out.println("--------------------------------");
                            System.out.println();
                            count++;
                        }
                    }
                }
                if(doReal)
                {
                    isExample = false;
                    data = new AocDataFileUtils(_year, "day" + _day);
                    startTime = System.currentTimeMillis();
                    final Object resultA = solvePartA();
                    endTime = System.currentTimeMillis();
                    System.out.println("result A:\n" + resultA);
                    System.out.println("======================");
                    System.out.println("took " + (endTime - startTime) + "ms");
                    System.out.println();
                }
                System.out.println();
            }
            if (doExamples)
            {
                isExample=true;
                if (AocDataFileUtils.getDataFileName(_year, "ex" + _day).exists())
                {
                    handleExampleFilePartB(_day);
                }
                else if (AocDataFileUtils.getDataFileName(_year, "exB" + _day).exists())
                {
                    handleExampleFilePartB(_day);
                }
                else
                {
                    int count = 1;
                    while (AocDataFileUtils.getDataFileName(_year, "ex" + _day + "_" + count).exists())
                    {
                        handleExampleFilePartB(_day + "_" + count);
                        System.out.println("--------------------------------");
                        System.out.println();
                        count++;
                    }
                }
            }
            if (doReal)
            {
                isExample = false;
                data = new AocDataFileUtils(_year, "day" + _day);
                startTime = System.currentTimeMillis();
                final Object resultB = solvePartB();
                endTime = System.currentTimeMillis();
                System.out.println("result B:\n" + resultB);
                System.out.println("======================");
                System.out.println("took " + (endTime - startTime) + "ms");
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void handleExampleFilePartA(final String day) throws IOException
    {
        long startTime;
        long endTime;
        data = new AocDataFileUtils(_year, "ex" + day);
        startTime = System.currentTimeMillis();
        final Object resultExampleA = solvePartA();
        endTime = System.currentTimeMillis();
        System.out.println("example A ("+day+"):\n" + resultExampleA);
        System.out.println("took " + (endTime - startTime) + "ms");
    }

    private void handleExampleFilePartB(final String day) throws IOException
    {
        long startTime;
        long endTime;
        data = new AocDataFileUtils(_year, "ex" + day);
        startTime = System.currentTimeMillis();
        final Object resultExampleA = solvePartB();
        endTime = System.currentTimeMillis();
        System.out.println("example B ("+day+"):\n" + resultExampleA);
        System.out.println("took " + (endTime - startTime) + "ms");
    }

    protected abstract Object solvePartA() throws IOException;
    protected abstract Object solvePartB() throws IOException;
}
