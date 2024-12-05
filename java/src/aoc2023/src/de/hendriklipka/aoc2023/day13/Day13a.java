package de.hendriklipka.aoc2023.day13;

import de.hendriklipka.aoc.AocDataFileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

public class Day13a
{
    public static void main(String[] args)
    {
        try
        {
            int sum= AocDataFileUtils.getStringBlocks("2023", "day13").stream().mapToInt(Day13a::getReflectionValue).sum();
            System.out.println(sum);
            // 18259 is too low

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int getReflectionValue(List<String> block)
    {
        int i = getRowReflection(block) + getColumnReflection(block);
        if (0==i)
            System.err.println("no reflection for\n" + StringUtils.join(block,"\n"));
        return i;
    }

    private static int getRowReflection(List<String> block)
    {
        // this is the row _before_ the reflection
        for (int row=1;row<block.size();row++)
        {
            if (isRowReflected(block, row))
                return 100*row;
        }
        return 0;
    }


    private static boolean isRowReflected(List<String> block, int row)
    {
        int rowCount=Math.min(row, block.size()-row);
        for (int diff=0;diff<rowCount;diff++)
        {
            if (!block.get(row+diff).equals(block.get(row-1-diff)))
                return false;
        }
        return true;
    }

    private static int getColumnReflection(List<String> block)
    {
        int cols=block.get(0).length();
        for (int col=1;col<cols;col++)
        {
            if (isColumnReflected(col, block))
                return col;
        }
        return 0;
    }

    private static boolean isColumnReflected(int col, List<String> block)
    {
        for (String line: block)
        {
            if (!isLineReflected(line, col))
                return false;
        }
        return true;
    }

    private static boolean isLineReflected(String line, int col)
    {
        int colCount=Math.min(col, line.length()-col);
        for (int diff = 0; diff < colCount; diff++)
        {
            if (line.charAt(col+diff)!=line.charAt(col-1-diff))
                return false;
        }
        return true;
    }
}
