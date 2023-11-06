package de.hendriklipka.aoc2016.day08;

import de.hendriklipka.aoc.AocParseUtils;

import java.util.List;

/**
 * User: hli
 * Date: 06.11.23
 * Time: 17:21
 */
public class RotateCol implements Instruction
{
    private final int col;
    private final int count;

    public RotateCol(final String instruction)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(instruction, "x=(\\d+) by (\\d+)");
        col = Integer.parseInt(parts.get(0));
        count = Integer.parseInt(parts.get(1));
    }

    @Override
    public void execute(final Boolean[][] display)
    {
        for (int count=0;count<this.count;count++)
        {
            boolean mark = display[col][5];
            for (int row=0;row<5;row++)
            {
                display[col][5-row]=display[col][4-row];
            }
            display[col][0]=mark;
        }
    }
}
