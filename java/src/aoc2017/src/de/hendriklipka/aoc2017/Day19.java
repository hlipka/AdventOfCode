package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;

import static de.hendriklipka.aoc.Direction.DOWN;

public class Day19 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day19().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        CharMatrix path=data.getLinesAsCharMatrix(' ');
        Position current=path.findFirst('|');
        Direction dir=DOWN;
        StringBuilder letters=new StringBuilder();
        while(true)
        {
            Position next=current.updated(dir);
            final var c = path.at(next);
            if (c==' ')
                break;
            if (Character.isAlphabetic(c))
            {
                letters.append(c);
            }
            if (c=='+')
            {
                // if a turn to the left gives a used field, turn to the left
                Direction left=dir.left();
                if (path.at(next.updated(left))!=' ')
                    dir=left;
                else // otherwise turn to the right
                    dir=dir.right();
            }
            current=next;
        }
        return letters;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // same as above, but we just count steps
        CharMatrix path = data.getLinesAsCharMatrix(' ');
        Position current = path.findFirst('|');
        Direction dir = DOWN;
        int steps=0;
        while (true)
        {
            Position next = current.updated(dir);
            final var c = path.at(next);
            steps++;
            if (c == ' ')
                break;
            if (c == '+')
            {
                dir = path.at(next.updated(dir.left())) != ' ' ? dir.left() : dir.right();
            }
            current = next;
        }
        return steps;
    }
}
