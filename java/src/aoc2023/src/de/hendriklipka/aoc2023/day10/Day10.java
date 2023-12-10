package de.hendriklipka.aoc2023.day10;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 10.12.23
 * Time: 09:04
 */
public class Day10
{
    static int startRow, startColumn;
    static int rows, columns;
    public static void main(String[] args)
    {
        try
        {
            List<List<Character>> area = AocParseUtils.getLinesAsChars("2023", "day10");
            rows = area.size();
            columns = area.get(0).size();
            findStart(area);
            int pipeLength=findPipeLength(area, startRow, startColumn);
            System.out.println(pipeLength);
            System.out.println((pipeLength)/2);

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int findPipeLength(List<List<Character>> area, int startRow, int startColumn)
    {
        int len=0;
        int lastRow=startRow;
        int lastColumn=startColumn;
        int currentRow=startRow;
        int currentColumn=startColumn+1;
        while (true)
        {
            List<Character> rowLine = area.get(currentRow);
            char c=rowLine.get(currentColumn);
            System.out.println("pipe at row="+currentRow+", col="+currentColumn+" is "+c);
            int dir;
            switch(c)
            {
                case '-':
                    dir=(currentColumn-lastColumn);
                    lastColumn=currentColumn;
                    currentColumn+=dir;
                    break;
                case '|':
                    dir=currentRow-lastRow;
                    lastRow=currentRow;
                    currentRow+=dir;
                    break;
                case 'L':
                    if (lastRow==currentRow) // coming from right
                    {
                        lastColumn=currentColumn;
                        currentRow--;
                    }
                    else
                    {
                        lastRow=currentRow;
                        currentColumn++; // coming from the top
                    }
                    break;
                case 'J':
                    if (lastRow == currentRow) // coming from left
                    {
                        lastColumn = currentColumn;
                        currentRow--;
                    }
                    else
                    {
                        lastRow = currentRow;
                        currentColumn--; // coming from the top
                    }
                    break;
                case '7':
                    if (lastRow == currentRow) // coming from left
                    {
                        lastColumn = currentColumn;
                        currentRow++;
                    }
                    else
                    {
                        lastRow = currentRow;
                        currentColumn--; // coming from the bottom
                    }
                    break;
                case 'F':
                    if (lastRow == currentRow) // coming from right
                    {
                        lastColumn = currentColumn;
                        currentRow++;
                    }
                    else
                    {
                        lastRow = currentRow;
                        currentColumn++; // coming from the bottom
                    }
                    break;
                case 'S':
                    return len+1;
            }
            len++;
        }
    }

    private static void findStart(List<List<Character>> area)
    {
        for (int row=0;row<rows;row++)
        {
            List<Character> rowLine = area.get(row);
            for (int col=0;col<columns;col++)
            {
                if ('S'==rowLine.get(col))
                {
                    startColumn=col;
                    startRow=row;
                    return;
                }
            }
        }
    }
}
