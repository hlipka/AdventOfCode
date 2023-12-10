package de.hendriklipka.aoc2023.day10;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Position;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: hli
 * Date: 10.12.23
 * Time: 09:04
 */
public class Day10b
{
    static int startRow, startColumn;
    static int rows, columns;

    enum Dir
    {
        UP, RIGHT, DOWN, LEFT;
    }

    static Set<Position> leftSide = new HashSet<>();
    static Set<Position> rightSide = new HashSet<>();
    static Set<Position> pipe = new HashSet<>();

    public static void main(String[] args)
    {
        try
        {
            List<List<Character>> area = AocParseUtils.getLinesAsChars("2023", "day10");
            rows = area.size();
            columns = area.get(0).size();
            findStart(area);
            findPipe(area, startRow, startColumn);
            Collection<Position> dupes = CollectionUtils.intersection(leftSide, rightSide);
            System.out.println("found "+dupes.size()+" dupes");
//            System.out.println("dupes="+StringUtils.join(dupes,"\n"));
//            System.out.println("left side="+StringUtils.join(leftSide,"\n"));
//            System.out.println("right side="+StringUtils.join(rightSide,"\n"));
            boolean leftOutside = fillInSide(area, leftSide, 'l');
            boolean rightOutside = fillInSide(area, rightSide, 'r');
            int leftCount = countArea(area, 'l');
            int rightCount = countArea(area, 'r');
            for (Position p : pipe)
            {
                area.get(p.row).set(p.col, 'p');
            }
            int pipeCount = countArea(area, 'p');
//            findUnmarked(area);
//            for (List<Character> line : area)
//            {
//                System.out.println(StringUtils.join(line, ""));
//            }
            System.out.println("left side=" + leftCount);
            System.out.println("right side=" + rightCount);
            System.out.println("pipe=" + pipeCount);
            System.out.println("remaining=" + ((rows * columns) - leftCount - rightCount - pipeCount));
            System.out.println("outside is " + leftOutside + "/" + rightOutside);
            int tiles = leftOutside ? rightCount : leftCount;
            System.out.println(tiles); // 281 is too high, 263 is too low
            // 273 is correct, though we still have dupes
            System.out.println((rows * columns) - (leftOutside ? leftCount : rightCount) - pipeCount);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int countArea(List<List<Character>> area, char inside)
    {
        return area.stream().mapToInt(line -> (int) line.stream().filter(c -> c == inside).count()).sum();
    }

    private static boolean fillInSide(List<List<Character>> area, Set<Position> side, Character sideChar)
    {
        boolean outside = false;
        while (!side.isEmpty())
        {
            Position here = side.iterator().next();
            side.remove(here);
            if (pipe.contains(here))
                continue;
            if (here.col < 0 || here.col >= columns || here.row < 0 || here.row >= rows) // fill process found an outside field, so we are connected top the outside
            {
                outside = true;
            }
            else
            {
                char c = area.get(here.row).get(here.col);
                if (c == sideChar) // already filled in, so stop here
                    continue;
                if (c== (sideChar=='l'?'r':'l'))
                {
                    System.err.println("found conflict at "+here);
                }
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
        System.out.println("start Dir=" + dir);
        while (true)
        {
            pipe.add(new Position(currentRow, currentColumn));
            char c = area.get(currentRow).get(currentColumn);
//            System.out.println("at row="+currentRow+", col="+currentColumn+", char="+c+", dir="+dir);
            switch (c)
            {
                case '-' ->
                {
                    if (dir == Dir.RIGHT)
                    {
                        leftSide.add(new Position(currentRow - 1, currentColumn));
                        rightSide.add(new Position(currentRow + 1, currentColumn));
                    }
                    else if (dir == Dir.LEFT)
                    {
                        leftSide.add(new Position(currentRow + 1, currentColumn));
                        rightSide.add(new Position(currentRow - 1, currentColumn));
                    }
                    currentColumn += (dir == Dir.RIGHT ? 1 : -1);
                }
                case '|' ->
                {
                    if (dir == Dir.UP)
                    {
                        leftSide.add(new Position(currentRow, currentColumn - 1));
                        rightSide.add(new Position(currentRow, currentColumn + 1));
                    }
                    else if (dir == Dir.DOWN)
                    {
                        leftSide.add(new Position(currentRow, currentColumn + 1));
                        rightSide.add(new Position(currentRow, currentColumn - 1));
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
//                        rightSide.add(new Position(currentRow + 1, currentColumn));
                        currentRow--;
                        dir = Dir.UP;
                    }
                    else
                    {
//                        leftSide.add(new Position(currentRow, currentColumn+1));
                        rightSide.add(new Position(currentRow, currentColumn-1));
                        rightSide.add(new Position(currentRow + 1, currentColumn-1));
                        rightSide.add(new Position(currentRow + 1, currentColumn));
                        currentColumn++; // coming from the top
                        dir = Dir.RIGHT;
                    }
                }
                case 'J' ->
                {
                    if (dir == Dir.RIGHT) // coming from left
                    {
//                        leftSide.add(new Position(currentRow - 1, currentColumn));
                        rightSide.add(new Position(currentRow + 1, currentColumn));
                        rightSide.add(new Position(currentRow + 1, currentColumn+1));
                        rightSide.add(new Position(currentRow, currentColumn+1));
                        currentRow--;
                        dir = Dir.UP;
                    }
                    else
                    {
                        leftSide.add(new Position(currentRow, currentColumn+1));
                        leftSide.add(new Position(currentRow + 1, currentColumn+1));
                        leftSide.add(new Position(currentRow + 1, currentColumn));
//                        rightSide.add(new Position(currentRow, currentColumn-1));
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
//                        rightSide.add(new Position(currentRow + 1, currentColumn));
                        currentRow++;
                        dir = Dir.DOWN;
                    }
                    else
                    {
//                        leftSide.add(new Position(currentRow, currentColumn-1));
                        rightSide.add(new Position(currentRow, currentColumn+1));
                        rightSide.add(new Position(currentRow - 1, currentColumn+1));
                        rightSide.add(new Position(currentRow - 1, currentColumn));
                        currentColumn--; // coming from the bottom
                        dir = Dir.LEFT;
                    }
                }
                case 'F' ->
                {
                    if (dir == Dir.LEFT) // coming from right
                    {
//                        leftSide.add(new Position(currentRow + 1, currentColumn));
                        rightSide.add(new Position(currentRow - 1, currentColumn));
                        rightSide.add(new Position(currentRow - 1, currentColumn-1));
                        rightSide.add(new Position(currentRow , currentColumn-1));
                        currentRow++;
                        dir = Dir.DOWN;
                    }
                    else
                    {
                        leftSide.add(new Position(currentRow, currentColumn-1));
                        leftSide.add(new Position(currentRow - 1, currentColumn-1));
                        leftSide.add(new Position(currentRow - 1, currentColumn));
//                        rightSide.add(new Position(currentRow, currentColumn+1));
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

    private static void findUnmarked(List<List<Character>> area)
    {
        for (int row = 0; row < rows; row++)
        {
            List<Character> rowLine = area.get(row);
            for (int col = 0; col < columns; col++)
            {
                final Character c = rowLine.get(col);
                if (c!='l' && c!='r'&&c!='p')
                {
                    System.out.println("unmarked: row="+row+", col="+col+", ->"+c);
                    area.get(row).set(col, 'X');
                }
            }
        }
    }
}
