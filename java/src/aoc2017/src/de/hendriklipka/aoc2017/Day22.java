package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.matrix.InfiniteCharMatrix;

import java.io.IOException;

import static de.hendriklipka.aoc.Direction.UP;

public class Day22 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day22().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final var world = createWorld();
        Position current=new Position(0,0);
        Direction dir=UP;
        int infected=0;
        for (int i=0;i<10000;i++)
        {
            char c=world.at(current);
            if (c=='#') // infected
            {
                dir=dir.right();
                world.set(current,'.');
            }
            else
            {
                dir=dir.left();
                world.set(current,'#');
                infected++;
            }
            current = current.updated(dir);
        }
        return infected;
    }

    private InfiniteCharMatrix createWorld() throws IOException
    {
        CharMatrix input=data.getLinesAsCharMatrix('.');
        int rowOfs=input.rows()/2;
        int colOfs=input.cols()/2;
        InfiniteCharMatrix world = new InfiniteCharMatrix('.');
        for (Position p: input.allMatchingPositions('#'))
        {
            world.set(p.updated(-rowOfs, -colOfs), '#');
        }
        return world;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        //clean=., weakened=w, infected=#, flagged=f
        final var world = createWorld();
        Position current = new Position(0, 0);
        Direction dir = UP;
        int infected = 0;
        // same simulation, but with different states and rules
        for (int i = 0; i < 10000000; i++)
        {
            char c = world.at(current);
            if (c == '.') // clean
            {
                dir = dir.left();
                world.set(current, 'w'); // clean get weakened
            }
            else if (c == 'w') // weakened
            {
                world.set(current, '#'); // weakened gets infected
                infected++;
            }
            else if (c=='#')
            {
                dir = dir.right();
                world.set(current, 'f');
            }
            else if (c=='f') // flagged
            {
                dir = dir.opposite();
                world.set(current, '.'); // flagged gets cleaned
            }
            current = current.updated(dir);
        }
        return infected;
    }
}
