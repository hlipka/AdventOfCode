package de.hendriklipka.aoc2023.day10;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Position;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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
            boolean leftOutside=fillInSide(area, leftSide, 'l');
            boolean rightOutside=fillInSide(area, rightSide, 'r');
            int tiles=countArea(area, leftOutside?'r':'l');
            for (List<Character> line:area)
            {
                System.out.println(StringUtils.join(line,""));
            }
            System.out.println("outside is "+leftOutside+"/"+rightOutside);
            System.out.println(tiles); // 2481 is too high, 263 is too low
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int countArea(List<List<Character>> area, char inside)
    {
        System.out.println("count "+inside);
        return area.stream().mapToInt(line -> (int) line.stream().filter(c -> c == inside).count()).sum();
    }

    private static boolean fillInSide(List<List<Character>> area, Set<Position> side, Character sideChar)
    {
        boolean outside=false;
        while (!side.isEmpty())
        {
            Position here=side.iterator().next();
            side.remove(here);
            if (pipe.contains(here))
                continue;
            if (isOutside(here)) // fill process found an outside field, so we are connected top the outside
            {
                outside=true;
            }
            else
            {
                char c=area.get(here.row).get(here.col);
                if (c==sideChar) // already filled in, so stop here
                    continue;
                area.get(here.row).set(here.col, sideChar);
                // add the other directions for a flood fill
                side.add(here.updated(-1, 0));
                side.add(here.updated(1, 0));
                side.add(here.updated(0, 1));
                side.add(here.updated(0, -1));
            }
        }
        return outside;
    }

    private static boolean isOutside(Position here)
    {
        if (here.col<0 || here.col >= columns)
            return true;
        if (here.row<0 || here.row >= rows)
            return true;
        return false;
    }

    private static void findPipe(List<List<Character>> area, int startRow, int startColumn)
    {
        int currentRow=startRow;
        int currentColumn=startColumn;
        pipe.add(new Position(currentRow, currentColumn));
        // determine start orientation
        List<Character> rowLine = area.get(currentRow);
        char leftChar=currentColumn==0?'.':rowLine.get(currentColumn+1);
        char rightChar=currentColumn==rowLine.size()-1?'.':rowLine.get(currentColumn+1);
        Dir dir;
        if (leftChar=='-' || leftChar=='L' || leftChar=='F')
        {
            dir= Dir.LEFT;
            currentColumn--;
        }
        else if (rightChar == '-' || rightChar == '7' || rightChar == 'J')
        {
            dir= Dir.RIGHT;
            currentColumn++;
        }
        else
        {
            dir= Dir.UP;
            currentRow--;
        }
        System.out.println("start Dir="+dir);
        while (true)
        {
            pipe.add(new Position(currentRow, currentColumn));
            switch(dir)
            {
                case UP ->
                {
                    leftSide.add(new Position(currentRow, currentColumn-1));
                    rightSide.add(new Position(currentRow, currentColumn+1));
                }
                case RIGHT ->
                {
                    leftSide.add(new Position(currentRow-1, currentColumn));
                    rightSide.add(new Position(currentRow+1, currentColumn));
                }
                case DOWN ->
                {
                    leftSide.add(new Position(currentRow, currentColumn+1));
                    rightSide.add(new Position(currentRow, currentColumn-1));
                }
                case LEFT ->
                {
                    leftSide.add(new Position(currentRow+1, currentColumn));
                    rightSide.add(new Position(currentRow-1, currentColumn));
                }
            }
            rowLine = area.get(currentRow);
            char c=rowLine.get(currentColumn);
//            System.out.println("pipe at row="+currentRow+", col="+currentColumn+" is "+c);
            switch(c)
            {
                case '-':
                    currentColumn+=(dir==Dir.RIGHT?1:-1);
                    break;
                case '|':
                    currentRow+=(dir==Dir.DOWN?1:-1);
                    break;
                case 'L':
                    if (dir==Dir.LEFT)
                    {
                        currentRow--;
                        dir=Dir.UP;
                    }
                    else
                    {
                        currentColumn++; // coming from the top
                        dir=Dir.RIGHT;
                    }
                    break;
                case 'J':
                    if (dir==Dir.RIGHT) // coming from left
                    {
                        currentRow--;
                        dir=Dir.UP;
                    }
                    else
                    {
                        currentColumn--; // coming from the top
                        dir=Dir.LEFT;
                    }
                    break;
                case '7':
                    if (dir==Dir.RIGHT) // coming from left
                    {
                        currentRow++;
                        dir=Dir.DOWN;
                    }
                    else
                    {
                        currentColumn--; // coming from the bottom
                        dir=Dir.LEFT;
                    }
                    break;
                case 'F':
                    if (dir==Dir.LEFT) // coming from right
                    {
                        currentRow++;
                        dir=Dir.DOWN;
                    }
                    else
                    {
                        currentColumn++; // coming from the bottom
                        dir=Dir.RIGHT;
                    }
                    break;
                case 'S':
                    return;
            }
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
