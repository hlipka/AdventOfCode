package de.hendriklipka.aoc2023.day14;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;

public class Day14a
{

    public static final char ROUND_ROCK = 'O';
    public static final char FREE = '.';

    public static void main(String[] args)
    {
        try
        {
            CharMatrix field = AocParseUtils.getLinesAsCharMatrix("2023", "day14", '.');
            field.print();
            for (int r = 1; r < field.rows(); r++)
            {
                for (int c=0;c<field.cols();c++)
                    if (field.at(r,c)==ROUND_ROCK)
                        field.moveWhileEmpty(new Position(r, c), Direction.UP);
            }
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
}
