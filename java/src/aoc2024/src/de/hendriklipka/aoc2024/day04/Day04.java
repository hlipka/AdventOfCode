package de.hendriklipka.aoc2024.day04;

import de.hendriklipka.aoc.*;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;

public class Day04 extends AocPuzzle
{
    public Day04()
    {
        super("2024", "04");
    }

    public static void main(String[] args)
    {
        new Day04().doPuzzle();
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final CharMatrix chars = AocParseUtils.getLinesAsCharMatrix(getYear(), getDay(), '.');
        int count=0;
        for (int row=0;row<chars.rows();row++)
        {
            for (int col=0;col<chars.cols();col++)
            {
                Position p=new Position(row,col);
                if (chars.at(p)=='X')
                {
                    count +=countXmas(chars, p);
                }
            }
        }
        return count;
    }

    private int countXmas(final CharMatrix chars, final Position p)
    {
        int count=0;
        if (isXmas(chars, p, DiagonalDirections.UP))
            count++;
        if (isXmas(chars, p, DiagonalDirections.LEFT_UP))
            count++;
        if (isXmas(chars, p, DiagonalDirections.LEFT))
            count++;
        if (isXmas(chars, p, DiagonalDirections.LEFT_DOWN))
            count++;
        if (isXmas(chars, p, DiagonalDirections.DOWN))
            count++;
        if (isXmas(chars, p, DiagonalDirections.RIGHT_DOWN))
            count++;
        if (isXmas(chars, p, DiagonalDirections.RIGHT))
            count++;
        if (isXmas(chars, p, DiagonalDirections.RIGHT_UP))
            count++;
        return count;
    }
    
    private boolean isXmas(final CharMatrix chars, final Position p, final DiagonalDirections direction)
    {
        Position pM=p.updated(direction, 1);
        Position pA=p.updated(direction, 2);
        Position pS=p.updated(direction, 3);
        return chars.in(pM) && chars.in(pA) && chars.in(pS) && chars.at(pM)=='M' && chars.at(pA)=='A' && chars.at(pS)=='S';
    }

    private boolean countCrosses(final CharMatrix chars, final Position p)
    {
        // Note: crosses must be diagonal, not up/down
        if (isCross(chars, p, DiagonalDirections.LEFT_UP))
            return true;
        if (isCross(chars, p, DiagonalDirections.LEFT_DOWN))
            return true;
        if (isCross(chars, p, DiagonalDirections.RIGHT_DOWN))
            return true;
        return isCross(chars, p, DiagonalDirections.RIGHT_UP);
    }

    private boolean isCross(final CharMatrix chars, final Position posA, final DiagonalDirections direction)
    {
        Position pM1=posA.updated(direction);
        Position pS1=posA.updated(direction.opposite());
        Position pM2=posA.updated(direction.right().right());
        Position pS2=posA.updated(direction.right().right().opposite());
        return chars.at(pM1) == 'M' && chars.at(pM2) == 'M' && chars.at(pS1) == 'S' && chars.at(pS2) == 'S';
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final CharMatrix chars = AocParseUtils.getLinesAsCharMatrix(getYear(), getDay(), '.');
        int count=0;
        // make sure to only look at valid 'A's
        for (int row=1;row<chars.rows()-1;row++)
        {
            for (int col=1;col<chars.cols()-1;col++)
            {
                Position p=new Position(row,col);
                if (chars.at(p)=='A')
                {
                    if (countCrosses(chars, p))
                        count++;
                }
            }
        }
        return count;
    }
}
