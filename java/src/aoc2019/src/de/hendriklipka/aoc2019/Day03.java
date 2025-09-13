package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.InfiniteCharMatrix;
import de.hendriklipka.aoc.matrix.InfiniteIntMatrix;

import java.io.IOException;
import java.util.List;

public class Day03 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day03().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<List<String>> lines = data.getLineWords(",");
        InfiniteCharMatrix grid = new InfiniteCharMatrix('.');
        runWire(lines.get(0), grid, '1');
        runWire(lines.get(1), grid, '2');
        return grid.allKnownTiles().stream().filter(p->grid.at(p)=='3').map(Position::distance).min(Integer::compareTo).orElseThrow();
    }

    private void runWire(final List<String> directions, final InfiniteCharMatrix grid, final char wire)
    {
        Position pos=new Position(0,0);
        for (String direction: directions)
        {
            int count=Integer.parseInt(direction.substring(1));
            Direction d=Direction.of(direction.substring(0,1));
            for (int i=0;i<count;i++)
            {
                pos=pos.updated(d);
                final char at = grid.at(pos);
                if (at=='.'||at==wire)
                {
                    grid.set(pos, wire);
                }
                else // we cross the other wire
                {
                    grid.set(pos, '3');
                }
            }
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<List<String>> lines = data.getLineWords(",");
        InfiniteCharMatrix grid = new InfiniteCharMatrix('.');
        InfiniteIntMatrix intGrid = new InfiniteIntMatrix(-1);

        Position pos=new Position(0,0);
        int length=0;
        for (String direction: lines.get(0))
        {
            int count=Integer.parseInt(direction.substring(1));
            Direction d=Direction.of(direction.substring(0,1));
            for (int i=0;i<count;i++)
            {
                pos=pos.updated(d);
                length++;
                grid.set(pos, '1');
                if (-1 == intGrid.at(pos)) // when we cross ourselves
                {
                    intGrid.set(pos,length);
                }
            }
        }

        pos=new Position(0,0);
        length=0;
        int bestLength=99999999;
        for (String direction: lines.get(1))
        {
            int count=Integer.parseInt(direction.substring(1));
            Direction d=Direction.of(direction.substring(0,1));
            for (int i=0;i<count;i++)
            {
                pos=pos.updated(d);
                length++;
                final char at = grid.at(pos);
                if (at=='.')
                {
                    grid.set(pos, '2');
                }
                else if (at=='1') // we cross the first wire
                {
                    grid.set(pos, '3');
                    int length1=intGrid.at(pos);
                    if (length1+length<bestLength)
                    {
                        bestLength=length1+length;
                    }
                }
            }
        }


        return bestLength;
    }
}
