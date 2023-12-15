package de.hendriklipka.aoc2023.day14;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.CycleFinder;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;

public class Day14b2
{

    public static final char ROUND_ROCK = 'O';

    public static void main(String[] args)
    {
        try
        {
            CharMatrix field = AocParseUtils.getLinesAsCharMatrix("2023", "day14", '.');
            field.print();
            CycleFinder<CharMatrix> cf = new CycleFinder<>(Day14b2::doCycle, 1000000000);

            field=cf.getFinalState(field);

            long sum=0;
            for (int r = 0; r < field.rows(); r++)
            {
                int weight = field.rows() - r;
                int rocks = field.countInRow(r, ROUND_ROCK);
                long lineWeight = weight * (long) rocks;
                sum+= lineWeight;
            }
            field.print();
            System.out.println(sum);

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void tiltNorth(CharMatrix field)
    {
        for (int r = 1; r < field.rows(); r++)
        {
            for (int c = 0; c < field.cols(); c++)
                if (field.at(r,c) == ROUND_ROCK)
                    field.moveWhileEmpty(new Position(r, c), Direction.UP);
        }
    }
    private static void tiltSouth(CharMatrix field)
    {
        for (int r = field.rows()-1; r >= 0; r--)
        {
            for (int c = 0; c < field.cols(); c++)
                if (field.at(r,c) == ROUND_ROCK)
                    field.moveWhileEmpty(new Position(r, c), Direction.DOWN);
        }
    }
    private static void tiltWest(CharMatrix field)
    {
        for (int c = 1; c < field.cols(); c++)
            for (int r = 0; r < field.rows(); r++)
            {
                if (field.at(r,c) == ROUND_ROCK)
                    field.moveWhileEmpty(new Position(r, c), Direction.LEFT);
            }
    }
    private static void tiltEast(CharMatrix field)
    {
        for (int c = field.cols()-1; c >=0; c--)
            for (int r = 0; r < field.rows(); r++)
            {
                if (field.at(r,c) == ROUND_ROCK)
                    field.moveWhileEmpty(new Position(r, c), Direction.RIGHT);
            }
    }

    private static CharMatrix doCycle(CharMatrix field)
    {
        CharMatrix newField=field.copyOf();
        tiltNorth(newField);
        tiltWest(newField);
        tiltSouth(newField);
        tiltEast(newField);
        return newField;
    }
}
