package de.hendriklipka.aoc2016.day08;

import de.hendriklipka.aoc.AocParseUtils;

import java.util.List;

/**
 * User: hli
 * Date: 06.11.23
 * Time: 17:20
 */
public class Fill implements Instruction
{
    private final int cols;
    private final int rows;

    public Fill(final String s)
    {
        List<String> parts= AocParseUtils.parsePartsFromString(s, "(\\d+)x(\\d+)");
        cols=Integer.parseInt(parts.get(0));
        rows=Integer.parseInt(parts.get(1));
    }

    @Override
    public void execute(final Boolean[][] display)
    {
        for (int r=0;r<rows;r++)
            for (int c=0;c<cols;c++)
                display[c][r]=true;
    }
}
