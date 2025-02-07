package de.hendriklipka.aoc2018;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.DiagonalDirections;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.LoopFinder;

import java.io.IOException;

public class Day18 extends AocPuzzle {
    public static void main(String[] args)
    {
        new Day18().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        CharMatrix area=data.getLinesAsCharMatrix('.');
        for (int i=0;i<10;i++)
        {
            area=simulateStep(area);
        }
        return area.allMatchingPositions('|').size()*area.allMatchingPositions('#').size();
    }

    private CharMatrix simulateStep(final CharMatrix area)
    {
        final CharMatrix newArea = CharMatrix.filledMatrix(area.rows(), area.cols(), '.', '.');
        for (Position p: area.allPositions())
        {
            char c=area.at(p);
            if (c=='.')
            {
                if (countNeighbours(area, p, '|') >= 3)
                {
                    newArea.set(p, '|');
                }
            }
            else if (c=='|')
            {
                newArea.set(p, countNeighbours(area, p, '#') >= 3 ? '#' : '|');
            }
            else // lumberyard
            {
                newArea.set(p, countNeighbours(area, p, '|') > 0 && countNeighbours(area, p, '#') > 0 ? '#' : '.');
            }
        }
        return newArea;
    }

    private int countNeighbours(final CharMatrix area, final Position p, final char c)
    {
        return (int) DiagonalDirections.directions().stream().filter(d -> area.at(p.updated(d)) == c).count();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CharMatrix area=data.getLinesAsCharMatrix('.');
        CharMatrix lastRound= new LoopFinder<>(this::simulateStep).findLastValue(area, 1000000000);
        return lastRound.allMatchingPositions('|').size() * lastRound.allMatchingPositions('#').size();
    }
}
