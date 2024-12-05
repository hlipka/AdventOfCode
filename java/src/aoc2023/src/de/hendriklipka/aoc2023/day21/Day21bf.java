package de.hendriklipka.aoc2023.day21;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.matrix.IntMatrix;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;

/**
 * this is to test the brute-force mechanism and to explore the data
 */
public class Day21bf
{
    private final static int STEPS = 20;
    private static final Predicate<Integer> FIELD_CONDITION = i -> i <= STEPS && 0 == (i % 2);

    public static void main(String[] args) throws IOException
    {
        // ex21c is a 11x11 field, with everything being a '.' (apart from the S in the middle)
        // I used this to test my proposed solution, because it misses fields
        CharMatrix field = AocDataFileUtils.getLinesAsCharMatrix("2023", "ex21c", '#');
        int width = field.cols();
        int height = field.rows();
        Position start = field.findFirst('S');
        field.set(start, '.');

        IntMatrix center = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        LinkedList<Position> queue = new LinkedList<>();
        queue.add(start);
        center.set(start, 0);
        walk(queue, center, field);

        // borders of the field are all empty, so any adjacent field (when repeating the garden) is a direct step of length 1
        IntMatrix fieldUp = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillUpwards(width, height, queue, fieldUp, center, field);

        IntMatrix fieldDown = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillDownwards(width, height, queue, fieldDown, center, field);

        IntMatrix fieldLeft = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillLeft(width, height, queue, fieldLeft, center, field);

        IntMatrix fieldRight = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillRight(width, height, queue, fieldRight, center, field);

        long result = center.count(FIELD_CONDITION);
        result += fieldUp.count(FIELD_CONDITION);
        result += fieldDown.count(FIELD_CONDITION);
        result += fieldLeft.count(FIELD_CONDITION);
        result += fieldRight.count(FIELD_CONDITION);

        result += walkRowsUp(width, height, fieldUp, field);
        result += walkRowsDown(width, height, fieldDown, field);

        result += walkRowLeft(width, height, fieldLeft, field, 0, -1);
        result += walkRowRight(width, height, fieldRight, field, 0, 1);


        System.out.println(result+" targets found");
    }

    private static long walkRowsUp(int width, int height, IntMatrix startField, CharMatrix field)
    {
        int rowUp=-1;
        // we walk the center line upwards, count the fields we could hit
        // and also branch out left and right to count what we could hit there

        long result=0;

        // we first walk the start row to the sides
        // we counted the start field already, but not its sides
        result += walkRowLeft(width, height, startField, field, rowUp, 0);
        result += walkRowRight(width, height, startField, field, rowUp, 0);

        while (true)
        {
            // walk to the next field upwards
            IntMatrix newField = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
            LinkedList<Position> queue = new LinkedList<>();
            // and fill in the new distances
            fillUpwards(width, height, queue, newField, startField, field);

            rowUp--;

            int count= newField.count(FIELD_CONDITION);
            // when this has nothing that matches anymore, we are done, and we also can skip the sides (because they cannot hit anything either
            if (0==count)
                break;
            result +=count;
            // we also go the sides from where we are
            result += walkRowLeft(width, height, newField, field, rowUp, 0);
            result += walkRowRight(width, height, newField, field, rowUp, 0);
            startField=newField;
        }
        return result;
    }

    private static long walkRowsDown(int width, int height, IntMatrix startField, CharMatrix field)
    {
        int rowDown=1;
        long result = 0;
        // walk the start row to the sides
        result += walkRowLeft(width, height, startField, field, rowDown, 0);
        result += walkRowRight(width, height, startField, field, rowDown, 0);

        while (true)
        {
            IntMatrix newField = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
            LinkedList<Position> queue = new LinkedList<>();
            fillDownwards(width, height, queue, newField, startField, field);

            rowDown++;

            int count = newField.count(FIELD_CONDITION);
            // when this has nothing that matches anymore, we are done, and we also can skip the sides
            if (0 == count)
                break;
            result += count;
            // we also go the sides from where we are
            result += walkRowLeft(width, height, newField, field, rowDown, 0);
            result += walkRowRight(width, height, newField, field, rowDown, 0);
            startField = newField;
        }
        return result;
    }


    private static long walkRowLeft(int width, int height, IntMatrix startField, CharMatrix field, int row, int col)
    {
        long result=0;
        while (true)
        {
            // ignore the current field, walk right away
            IntMatrix newField = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
            LinkedList<Position> queue = new LinkedList<>();
            fillLeft(width, height, queue, newField, startField, field);

            col--;

            // first walk to the next field to the side, the start field was already handled
            int count = newField.count(FIELD_CONDITION);
            // when this has nothing that matches anymore, we are done
            if (0 == count)
                return result;
            result += count;
            startField=newField;
        }
    }

    private static long walkRowRight(int width, int height, IntMatrix startField, CharMatrix field, int row, int col)
    {
        long result = 0;
        while (true)
        {
            // ignore the current field, walk right away
            IntMatrix newField = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
            LinkedList<Position> queue = new LinkedList<>();
            fillRight(width, height, queue, newField, startField, field);

            col++;
            // first walk to the next field to the side, the start field was already handled
            int count = newField.count(FIELD_CONDITION);
            // when this has nothing that matches anymore, we are done
            if (0 == count)
                return result;
            result += count;
            startField = newField;
        }
    }


    private static void fillUpwards(int width, int height, LinkedList<Position> queue, IntMatrix target, IntMatrix source, CharMatrix field)
    {
        for (int col = 0; col < width; col++)
        {
            final Position pos = new Position(height - 1, col);
            queue.add(pos);
            target.set(pos, source.at(0, col) + 1);
        }
        walk(queue, target, field);
    }

    private static void fillDownwards(int width, int height, LinkedList<Position> queue, IntMatrix target, IntMatrix source, CharMatrix field)
    {
        for (int col = 0; col < width; col++)
        {
            final Position pos = new Position(0, col);
            queue.add(pos);
            target.set(pos, source.at(height - 1, col) + 1);
        }
        walk(queue, target, field);
    }

    private static void fillLeft(int width, int height, LinkedList<Position> queue, IntMatrix target, IntMatrix source, CharMatrix field)
    {
        for (int row = 0; row < height; row++)
        {
            final Position pos = new Position(row, width - 1);
            queue.add(pos);
            target.set(pos, source.at(row, 0) + 1);
        }
        walk(queue, target, field);
    }

    private static void fillRight(int width, int height, LinkedList<Position> queue, IntMatrix target, IntMatrix source, CharMatrix field)
    {
        for (int row = 0; row < height; row++)
        {
            final Position pos = new Position(row, 0);
            queue.add(pos);
            target.set(pos, source.at(row, width - 1) + 1);
        }
        walk(queue, target, field);
    }


    private static void walk(LinkedList<Position> queue, IntMatrix dists, CharMatrix field)
    {
        while (!queue.isEmpty())
        {
            Position here = queue.poll();
            queue.remove(here);
            final int dist = dists.at(here);
            addPos(field, dists, queue, here, Direction.UP, dist);
            addPos(field, dists, queue, here, Direction.DOWN, dist);
            addPos(field, dists, queue, here, Direction.LEFT, dist);
            addPos(field, dists, queue, here, Direction.RIGHT, dist);
        }
    }

    private static void addPos(CharMatrix field, IntMatrix dists, Collection<Position> queue, Position here, Direction dir, int dist)
    {
        Position next = here.updated(dir);
        if (field.at(next) == '#') // cannot walk here
            return;
        if (dists.at(next) <= dist + 1) // we have been there, and it was cheaper as well
            return;
        dists.set(next, dist + 1);
        queue.add(next);
    }
}
