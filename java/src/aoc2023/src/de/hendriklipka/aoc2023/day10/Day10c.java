package de.hendriklipka.aoc2023.day10;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.Position;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simpler version of Day10b - only tracking one side of the loop is enough
 */
public class Day10c
{
    static int startRow, startColumn;
    static int rows, columns;

    enum Dir
    {
        UP, RIGHT, DOWN, LEFT;
    }

    static Set<Position> leftSide = new HashSet<>();
    static Set<Position> pipe = new HashSet<>();

    public static void main(String[] args)
    {
        try
        {
            List<List<Character>> area = AocDataFileUtils.getLinesAsChars("2023", "day10");
            rows = area.size();
            columns = area.get(0).size();
            findStart(area);
            findPipe(area, startRow, startColumn);
            boolean leftOutside = fillInSide(area, leftSide, 'l');
            int leftCount = area.stream().mapToInt(line1 -> (int) line1.stream().filter(c1 -> c1 == 'l').count()).sum();
            int pipeCount = pipe.size();
            System.out.println("left side=" + leftCount);
            System.out.println("pipe=" + pipeCount);
            System.out.println("remaining=" + ((rows * columns) - leftCount - pipeCount));
            System.out.println(leftOutside ? (rows * columns - leftCount - pipeCount) : leftCount);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean fillInSide(List<List<Character>> area, Set<Position> side, Character sideChar)
    {
        boolean outside = false;
        while (!side.isEmpty())
        {
            Position here = side.iterator().next();
            side.remove(here);
            if (pipe.contains(here))
            {
                continue;
            }
            if (here.col < 0 || here.col >= columns || here.row < 0 || here.row >= rows) // fill process found an outside field, so we are connected top the outside
            {
                outside = true;
                continue;
            }
            char c = area.get(here.row).get(here.col);
            if (c == sideChar) // already filled in, so stop here
                continue;
            area.get(here.row).set(here.col, sideChar);
            // add the other directions for a flood fill
            side.add(here.updated(-1, -1));
            side.add(here.updated(-1, 0));
            side.add(here.updated(-1, 1));
            side.add(here.updated(0, -1));
            side.add(here.updated(0, 1));
            side.add(here.updated(1, -1));
            side.add(here.updated(1, 0));
            side.add(here.updated(1, 1));
        }
        return outside;
    }

    private static void findPipe(List<List<Character>> area, int startRow, int startColumn)
    {
        int currentRow = startRow;
        int currentColumn = startColumn;
        pipe.add(new Position(currentRow, currentColumn));
        // determine start orientation
        List<Character> rowLine = area.get(currentRow);
        char leftChar = currentColumn == 0 ? '.' : rowLine.get(currentColumn - 1);
        char rightChar = currentColumn == rowLine.size() - 1 ? '.' : rowLine.get(currentColumn + 1);
        Dir dir;
        if (leftChar == '-' || leftChar == 'L' || leftChar == 'F')
        {
            dir = Dir.LEFT;
            currentColumn--;
        }
        else if (rightChar == '-' || rightChar == '7' || rightChar == 'J')
        {
            dir = Dir.RIGHT;
            currentColumn++;
        }
        else
        {
            dir = Dir.UP;
            currentRow--;
        }
        while (true)
        {
            pipe.add(new Position(currentRow, currentColumn));
            char c = area.get(currentRow).get(currentColumn);
            switch (c)
            {
                case '-' ->
                {
                    if (dir == Dir.RIGHT)
                    {
                        leftSide.add(new Position(currentRow - 1, currentColumn));
                    }
                    else if (dir == Dir.LEFT)
                    {
                        leftSide.add(new Position(currentRow + 1, currentColumn));
                    }
                    currentColumn += (dir == Dir.RIGHT ? 1 : -1);
                }
                case '|' ->
                {
                    if (dir == Dir.UP)
                    {
                        leftSide.add(new Position(currentRow, currentColumn - 1));
                    }
                    else if (dir == Dir.DOWN)
                    {
                        leftSide.add(new Position(currentRow, currentColumn + 1));
                    }
                    currentRow += (dir == Dir.DOWN ? 1 : -1);
                }
                case 'L' ->
                {
                    if (dir == Dir.LEFT)
                    {
                        leftSide.add(new Position(currentRow + 1, currentColumn));
                        leftSide.add(new Position(currentRow + 1, currentColumn-1));
                        leftSide.add(new Position(currentRow, currentColumn-1));
                        currentRow--;
                        dir = Dir.UP;
                    }
                    else
                    {
                        currentColumn++; // coming from the top
                        dir = Dir.RIGHT;
                    }
                }
                case 'J' ->
                {
                    if (dir == Dir.RIGHT) // coming from left
                    {
                        currentRow--;
                        dir = Dir.UP;
                    }
                    else
                    {
                        leftSide.add(new Position(currentRow, currentColumn+1));
                        leftSide.add(new Position(currentRow + 1, currentColumn+1));
                        leftSide.add(new Position(currentRow + 1, currentColumn));
                        currentColumn--; // coming from the top
                        dir = Dir.LEFT;
                    }
                }
                case '7' ->
                {
                    if (dir == Dir.RIGHT) // coming from left
                    {
                        leftSide.add(new Position(currentRow - 1, currentColumn));
                        leftSide.add(new Position(currentRow - 1, currentColumn+1));
                        leftSide.add(new Position(currentRow, currentColumn+1));
                        currentRow++;
                        dir = Dir.DOWN;
                    }
                    else
                    {
                        currentColumn--; // coming from the bottom
                        dir = Dir.LEFT;
                    }
                }
                case 'F' ->
                {
                    if (dir == Dir.LEFT) // coming from right
                    {
                        currentRow++;
                        dir = Dir.DOWN;
                    }
                    else
                    {
                        leftSide.add(new Position(currentRow, currentColumn-1));
                        leftSide.add(new Position(currentRow - 1, currentColumn-1));
                        leftSide.add(new Position(currentRow - 1, currentColumn));
                        currentColumn++; // coming from the bottom
                        dir = Dir.RIGHT;
                    }
                }
                case 'S' ->
                {
                    return;
                }
            }
        }
    }

    private static void findStart(List<List<Character>> area)
    {
        for (int row = 0; row < rows; row++)
        {
            List<Character> rowLine = area.get(row);
            for (int col = 0; col < columns; col++)
            {
                if ('S' == rowLine.get(col))
                {
                    startColumn = col;
                    startRow = row;
                    return;
                }
            }
        }
    }
}
