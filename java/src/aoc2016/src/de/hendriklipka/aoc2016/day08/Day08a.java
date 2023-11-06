package de.hendriklipka.aoc2016.day08;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 06.11.23
 * Time: 17:12
 */
public class Day08a
{
    public static void main(String[] args)
    {
        try
        {
            List<Instruction> instructions = AocParseUtils.getLines("2016", "day08").stream().map(Day08a::parseInstruction).toList();
            Boolean[][] display = new Boolean[50][6];
            for (Boolean[] row: display)
            {
                Arrays.fill(row, false);
            }
            for (Instruction inst: instructions)
            {
                inst.execute(display);
                //Note: displaying this for debug purposes also solved part b ;-)
                dumpDisplay(display);
            }
            long count=Arrays.stream(display).map(r->Arrays.stream(r).filter(f->f).count()).reduce(Long::sum).orElseThrow();
            System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void dumpDisplay(final Boolean[][] display)
    {
        System.out.println("--------------------------");
        for (int row=0;row<6;row++)
        {
            for (int col=0;col<49;col++)
            {
                System.out.print(display[col][row]?"x":" ");
            }
            System.out.println();
        }
    }

    private static Instruction parseInstruction(String instruction)
    {
        if (instruction.startsWith("rect "))
        {
            return new Fill(instruction.substring(5));
        }
        if (instruction.startsWith("rotate column "))
        {
            return new RotateCol(instruction.substring(14));
        }
        if (instruction.startsWith("rotate row "))
        {
            return new RotateRow(instruction.substring(11));
        }
        throw new IllegalArgumentException("unknown instruction in "+instruction);
    }
}
