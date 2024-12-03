package de.hendriklipka.aoc;

import java.io.IOException;

public abstract class AocPuzzle
{
    private final String _year;
    private final String _day;
    private String folder;

    public AocPuzzle(String year, String day)
    {
        _year = year;
        _day = day;
    }

    protected void doPuzzle()
    {
        try
        {
            folder=_year+"s";
            System.out.println("example A:\n"+solvePartA());
            folder=_year;
            System.out.println("result A:\n"+solvePartA());
            folder=_year+"s";
            System.out.println("example B:\n"+solvePartB());
            folder=_year;
            System.out.println("result B:\n"+solvePartB());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected String getDay()
    {
        return "day"+_day;
    }

    protected String getYear()
    {
        return folder;
    }

    protected abstract Object solvePartA() throws IOException;
    protected abstract Object solvePartB() throws IOException;
}
