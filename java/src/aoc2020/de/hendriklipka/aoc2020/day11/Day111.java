package de.hendriklipka.aoc2020.day11;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: hli
 * Date: 11.12.20
 * Time: 16:40
 */
public class Day111
{

    private static final char FLOOR = '.';
    private static final char OCCUPIED = '#';
    private static final char EMPTY = 'L';

    public static void main(String[] args)
    {
        try
        {
            List<String> lines = FileUtils.readLines(new File("../data/2020/day11.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
            int rows=lines.size();
            int cols=lines.get(0).length();
            char field[]=new char[rows*cols];
            int currentRow=0;
            for (String line: lines)
            {
                parseFieldLine(line, field, currentRow*cols);
                currentRow++;
            }
            dumpField(field, rows, cols);
            int iterations=0;
            while (true)
            {
                char[] newField=new char[field.length];
                final boolean b = updateField(field, newField, rows, cols);
                field=newField;
                dumpField(field, rows, cols);
                if (!b) break;
                iterations++;
            }
            System.out.println(iterations);
            System.out.println(countOccupied(field));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static int countOccupied(final char[] field)
    {
        int count=0;
        for (int i=0;i<field.length;i++)
        {
            if (field[i]==OCCUPIED)
                count++;
        }
        return count;
    }

    private static void dumpField(final char[] field, final int rows, final int cols)
    {
        for (int row=0;row<rows;row++)
        {
            int rowStart=row*cols;
            for (int col=0;col<cols;col++)
            {
                System.out.print(field[rowStart+col]);
            }
            System.out.println();
        }
        System.out.println("--------------------");
    }

    private static boolean updateField(final char[] field, final char[] newField, final int rows, final int cols)
    {
        boolean updated=false;
        for (int i=0;i<field.length;i++)
        {
            updated|=updateCell(i, field, newField, rows, cols);
        }
        return updated;
    }

    private static boolean updateCell(final int offset, final char[] field, final char[] newField, final int rows, final int cols)
    {
        newField[offset]=field[offset];
        if (FLOOR ==field[offset])
        {
            return false;
        }
        int n=countNeighbours(offset, field, rows, cols);
        char newState=field[offset];
        if (0==n)
        {
            newState= OCCUPIED;
        }
        else if (n>=4)
        {
            newState= EMPTY;
        }
        boolean updated = newState!=field[offset];
        newField[offset]=newState;
        return updated;
    }

    private static int countNeighbours(final int offset, final char[] field, final int rows, final int cols)
    {
        //TODO handle wraparound between rows
        int neighbours=0;
        if ((offset%cols)>0)
        {
            if (isOccupied(field, offset - 1 - cols))
                neighbours++;
            if (isOccupied(field, offset - 1))
                neighbours++;
            if (isOccupied(field, offset - 1 + cols))
                neighbours++;
        }
        if (isOccupied(field, offset  - cols))
            neighbours++;

        if ((offset%cols<(cols-1)))
        {
            if (isOccupied(field, offset + 1 - cols))
                neighbours++;
            if (isOccupied(field, offset + 1))
                neighbours++;
            if (isOccupied(field, offset + 1 + cols))
                neighbours++;
        }
        if (isOccupied(field, offset + cols))
            neighbours++;
        return neighbours;
    }

    private static boolean isOccupied(final char[] field, final int offset)
    {
        if (offset<0 || offset>=field.length)
            return false;
        return field[offset]==OCCUPIED;
    }

    private static void parseFieldLine(final String line, final char[] field, final int offset)
    {
        for (int i=0;i<line.length();i++)
        {
            field[offset+i]=line.charAt(i);
        }
    }
}
