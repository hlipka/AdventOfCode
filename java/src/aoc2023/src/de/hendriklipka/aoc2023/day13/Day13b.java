package de.hendriklipka.aoc2023.day13;

import de.hendriklipka.aoc.AocDataFileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day13b
{
    public static void main(String[] args)
    {
        try
        {
            int sum= AocDataFileUtils.getStringBlocks("2023", "day13").stream().mapToInt(Day13b::getReflectionValue).sum();
            System.out.println(sum);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int getReflectionValue(List<String> block)
    {
        int oldRowValue=getRowReflection(block, -1);
        int oldColValue=getColumnReflection(block, -1);
        for (int row=0;row<block.size();row++)
        {
            for (int col=0;col<block.get(0).length();col++)
            {
                List<String> newBlock=fixBlock(block, row, col);
                int rowReflection = getRowReflection(newBlock, oldRowValue/100);
                int columnReflection = getColumnReflection(newBlock, oldColValue);
                if (0!=rowReflection && rowReflection!=oldRowValue)
                {
                    return rowReflection;
                }
                if (0!=columnReflection && columnReflection!=oldColValue)
                {
                    return columnReflection;
                }
            }
        }
        System.err.println("no reflection found for\n" + StringUtils.join(block,"\n")+"\nold value was "+(oldRowValue+oldColValue));
        return 0;
    }

    private static List<String> fixBlock(List<String> block, int rowFix, int colFix)
    {
        List<String> newBlock = new ArrayList<>();
        for (int row=0;row<block.size();row++)
        {
            String s=block.get(row);
            if (row==rowFix)
            {
                char c=s.charAt(colFix);
                s=s.substring(0, colFix)+('.'==c?'#':'.')+s.substring(colFix+1);
            }
            newBlock.add(s);
        }
        return newBlock;
    }

    private static int getRowReflection(List<String> block, int skipRow)
    {
        // this is the row _before_ the reflection
        for (int row=1;row<block.size();row++)
        {
            if (row!=skipRow && isRowReflected(block, row))
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

    private static int getColumnReflection(List<String> block, int skipCol)
    {
        int cols=block.get(0).length();
        // this is the column _before_ the reflection
        for (int col=1;col<cols;col++)
        {
            if (col!=skipCol && isColumnReflected(col, block))
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
