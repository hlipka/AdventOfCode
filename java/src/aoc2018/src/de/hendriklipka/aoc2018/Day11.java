package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.IntMatrix;

import java.io.IOException;

public class Day11 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day11().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        int serial=Integer.parseInt(data.getLines().get(0));
        final var grid = createGrid(serial);
        Position bestPos=new Position(-2,-2);
        int bestLevel=0;
        for (Position pos : grid.allPositions())
        {
            if (grid.in(pos.updated(2,2)))
            {
                int level=
                        grid.at(pos.updated(0, 0))
                        +grid.at(pos.updated(1,0))
                        +grid.at(pos.updated(2,0))
                        +grid.at(pos.updated(0,1))
                        +grid.at(pos.updated(1,1))
                        +grid.at(pos.updated(2,1))
                        +grid.at(pos.updated(0,2))
                        +grid.at(pos.updated(1,2))
                        +grid.at(pos.updated(2,2));
                if (level>bestLevel)
                {
                    bestLevel=level;
                    bestPos=pos.updated(1, 1);
                }
            }
        }
        System.out.println("best level="+bestLevel);
        return bestPos.col+","+bestPos.row; // update to use grid coords
    }

    private static IntMatrix createGrid(final int serial)
    {
        IntMatrix grid=new IntMatrix(300, 300, 0);
        for (Position pos: grid.allPositions())
        {
            int x=pos.col+1;
            int y=pos.row+1;
            int rackID=x+10;
            int powerLevel=rackID*y;
            powerLevel+= serial;
            powerLevel*=rackID;
            if (powerLevel<100)
            {
                powerLevel=0;
            }
            else
            {
                powerLevel=(powerLevel%1000)/100;
            }
            powerLevel-=5;
            grid.set(pos, powerLevel);
        }
        return grid;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        int serial = Integer.parseInt(data.getLines().get(0));
        final var grid = createGrid(serial);
        Position bestPos = new Position(-2, -2);
        int bestLevel = 0;
        int bestSize=0;
        // brute-force all square sizes
        for (int squareSize=1; squareSize<=300; squareSize++)
        {
            for (Position pos : grid.allPositions())
            {
                if (grid.in(pos.updated(squareSize-1, squareSize - 1)))
                {
                    int level=0;
                    for (int r=0;r<squareSize;r++)
                        for (int c=0;c<squareSize;c++)
                            level+=grid.at(pos.updated(r, c));
                    if (level > bestLevel)
                    {
                        bestLevel = level;
                        bestPos = pos.updated(1, 1);
                        bestSize=squareSize;
                    }
                }
            }
        }
        System.out.println("best level=" + bestLevel);
        return bestPos.col + "," + bestPos.row+","+bestSize;
    }
}
