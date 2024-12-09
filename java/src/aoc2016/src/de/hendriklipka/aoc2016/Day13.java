package de.hendriklipka.aoc2016;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.AStarSearch;
import de.hendriklipka.aoc.search.ArrayWorld;

import java.io.IOException;
import java.util.List;

public class Day13 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day13().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        // get all settings from the file so we can easily handle the example as well (which has a different target)
        final List<Integer> setting = data.getLineAsInteger(",");
        int num=setting.get(0);
        int col=setting.get(1);
        int row=setting.get(2);

        // this places all the cubicles - assume that twice the size of the target position is enough
        final var floor = createFloor(row*2, col*2, num);
        // create a simple A* search
        final var world = createWorld(floor, col, row, col * 2, row * 2);
        AStarSearch search = new AStarSearch(world);
        // and run it
        return search.findPath();
    }

    private static ArrayWorld createWorld(final char[][] floor, final int col, final int row, final int cols, final int rows)
    {
        CharMatrix cubes = new CharMatrix(floor, '#');
        final ArrayWorld world = new ArrayWorld()
        {
            @Override
            public int getStartX()
            {
                return 1;
            }

            @Override
            public int getStartY()
            {
                return 1;
            }

            @Override
            public int getEndX()
            {
                return col;
            }

            @Override
            public int getEndY()
            {
                return row;
            }

            @Override
            public int getWidth()
            {
                return cols;
            }

            @Override
            public int getHeight()
            {
                return rows;
            }

            @Override
            public boolean canMoveTo(final int oldX, final int oldY, final int x, final int y)
            {
                return cubes.in(y,x) && cubes.at(y,x)!='#';
            }
        };
        return world;
    }

    private char[][] createFloor(final int rows, final int cols, final int num)
    {
        final char[][] floor = new char[rows][cols];
        for (int r = 0; r < floor.length; r++)
        {
            for (int c = 0; c < floor[r].length; c++)
            {
                floor[r][c] = getCube(c,r, num);
            }
        }
        return floor;
    }

    private char getCube(final int x, final int y, final int num)
    {
        int n= x * x + 3 * x + 2 * x * y + y + y * y+num;
        return (0==Integer.bitCount(n)%2)?' ':'#';
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // same setup as before
        final List<Integer> setting = data.getLineAsInteger(",");
        int num = setting.get(0);
        final var floor = createFloor(60, 60, num);
        int count=0;
        // just run through all positions which could be reached in theory (manhattan distance)
        for (int row = 0; row<52; row++)
        {
            for (int col = 0; col<(53-row); col++)
            {
                if (floor[row][col]=='#')
                    continue;
                // if this is not a cubicle, just run the search again and check the steps
                final var world = createWorld(floor, col, row, 60, 60);
                AStarSearch search = new AStarSearch(world);
                if (search.findPath()<=50)
                    count++;

            }
        }
        return count;
    }
}
