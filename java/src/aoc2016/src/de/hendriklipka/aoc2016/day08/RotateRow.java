package de.hendriklipka.aoc2016.day08;

import de.hendriklipka.aoc.AocParseUtils;

import java.util.List;

/**
 * User: hli
 * Date: 06.11.23
 * Time: 17:14
 */
public class RotateRow implements Instruction
{
    private final int row;
    private final int count;

    public RotateRow(final String instruction)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(instruction, "y=(\\d+) by (\\d+)");
        row = Integer.parseInt(parts.get(0));
        count = Integer.parseInt(parts.get(1));
    }

    public void execute(Boolean[][] display)
    {
        for (int count = 0; count < this.count; count++)
        {
            boolean mark = display[49][row];
            for (int col = 0; col < 49; col++)
            {
                display[49-col][row] = display[48-col][row];
            }
            display[0][row] = mark;
        }
    }
}
