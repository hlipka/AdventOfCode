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
        new Day04().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final CharMatrix chars = data.getLinesAsCharMatrix( '.');
        return chars.allPositions().stream().mapToLong(p->{
            if (chars.at(p) == 'X')
            {
                return DiagonalDirections.directions().stream().map(d -> new String(
                        chars.getInDirection(p, d, 4)).equals("XMAS")).filter(x -> x == true).count();
            }
            return 0L;
        }).sum();
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
        final CharMatrix chars = data.getLinesAsCharMatrix( '.');
        return chars.allPositions().stream().filter(p ->
                chars.at(p) == 'A' && countCrosses(chars, p)).count();
    }
}
