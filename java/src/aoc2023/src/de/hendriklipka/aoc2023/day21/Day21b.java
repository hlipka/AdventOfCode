package de.hendriklipka.aoc2023.day21;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.matrix.IntMatrix;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * User: hli
 * Date: 21.12.23
 * Time: 06:13
 */
public class Day21b
{
    private final static int STEPS = 50;

    public static void main(String[] args) throws IOException
    {
        CharMatrix field = AocParseUtils.getLinesAsCharMatrix("2023", "ex21", '#');
        int width = field.cols();
        int height = field.rows();
        Position start = field.findFirst('S');
        field.set(start, '.');
        IntMatrix dists = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        LinkedList<Position> queue = new LinkedList<>();
        queue.add(start);
        dists.set(start, 0);
        walk(queue, dists, field);
        // queue is always empty now, so we can re-use it

        // borders of the field are all empty, so any adjacent field (when repeating the garden) is a direct step of length 1
        // go upwards
        IntMatrix upDists = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillUpwards(width, height, queue, upDists, dists, field);

        IntMatrix upDists2 = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillUpwards(width, height, queue, upDists2, upDists, field);

        IntMatrix upOffset = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillInOffsets(width, height, upOffset, upDists2, upDists, field);

        //row+1
        IntMatrix downDists = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillDownwards(width, height, queue, downDists, dists, field);

        // row+2
        IntMatrix downDists2 = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillDownwards(width, height, queue, downDists2, downDists, field);

        IntMatrix downOffset = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillInOffsets(width, height, downOffset, downDists2, downDists, field);

        // col-1
        IntMatrix leftDists = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillLeft(width, height, queue, leftDists, dists, field);

        // col-2
        IntMatrix leftDists2 = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillLeft(width, height, queue, leftDists2, leftDists, field);

        IntMatrix leftOffset = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillInOffsets(width, height, leftOffset, leftDists2, leftDists, field);

        // col-1
        IntMatrix rightDists = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillRight(width, height, queue, rightDists, dists, field);

        // col-2
        IntMatrix rightDists2 = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillRight(width, height, queue, rightDists2, rightDists, field);

        IntMatrix rightOffset = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        fillInOffsets(width, height, rightOffset, rightDists2, rightDists, field);

        long result = calculateFields(field, dists, upDists, upOffset, downDists, downOffset, leftDists, leftOffset, rightDists, rightOffset);

        System.out.println(result);
        System.out.println("1594 (goal)");
        System.out.println("diff=" + (result - 1594));
    }

    private static long calculateFields(CharMatrix field, IntMatrix dists, IntMatrix upDists, IntMatrix upOffset, IntMatrix downDists, IntMatrix downOffset,
                                        IntMatrix leftDists, IntMatrix leftOffset,
                                        IntMatrix rightDists, IntMatrix rightOffset)
    {
        long result = dists.count(i -> i <= STEPS && 0 == (i % 2));

        for (int row = 0; row < field.rows(); row++)
        {
            for (int col = 0; col < field.cols(); col++)
            {
                if (field.at(row, col) != '#')
                {
                    result += calculateUp(row, col, upDists, upOffset, leftOffset, rightOffset);
                    result += calculateUp(row, col, downDists, downOffset, leftOffset, rightOffset);

                    // these are just for the middle row, the others were handled when going up/down
                    result += calculateSide(row, col, leftDists, leftOffset);
                    result += calculateSide(row, col, rightDists, rightOffset);
                }
            }
        }

        return result;
    }

    private static long calculateSide(int row, int col, IntMatrix dists, IntMatrix offsets)
    {
        long result = 0;
        boolean isEven = 0 == (dists.at(row, col) % 2);
        int currentSteps = dists.at(row, col);
        final int sideOffset = offsets.at(row, col);
        if (isEven)
        {
            result++;
            // for an even field, we can start right here, so we encounter one field for each (2*offset)
            int remaining = STEPS - currentSteps;
            result += remaining / (2L * sideOffset);
        }
        else
        {
            // for an even position, the first field we hit is one sideways. So we check that we can go there, and calculate from there
            int remaining = STEPS - currentSteps - sideOffset;
            if (remaining >= 0)
            {
                result++;
                result += remaining / (2L * sideOffset);
            }
        }

        return result;
    }

    private static long calculateUp(int row, int col, IntMatrix startDists, IntMatrix rowOffsets, IntMatrix leftOffset, IntMatrix rightOffset)
    {
        long result = 0;
        // step upwards for each repeated field
        // the step distance advances by the calculated offset
        final int leftOffsetValue = leftOffset.at(row, col);
        final int rightOffsetValue = rightOffset.at(row, col);
        for (int upPos = startDists.at(row, col); upPos <= STEPS; upPos += rowOffsets.at(row, col))
        {
            boolean isEven = 0 == (upPos % 2);
            // for each row, we calculate the remaining steps, which we can use for a side-wards walk
            // we can calculate how often we can go sideways

            // are we at an even repetition (only every second one ist)?
            if (isEven)
            {
                // for 'even' repetition, the current one counts already
                result++;
                // for an even field, we can start right here, so we encounter one field for each (2*offset)
                int remaining = STEPS - upPos;
                result += remaining / (2L * leftOffsetValue);
                result += remaining / (2L * rightOffsetValue);
            }
            else
            {
                // for an even position, the first field we hit is one sideways. So we check that we can go there, and calculate from there
                int remainingLeft = STEPS - upPos - leftOffsetValue;
                if (remainingLeft >= 0)
                {
                    result++;
                    result += remainingLeft / (2L * leftOffsetValue);
                }
                int remainingRight = STEPS - upPos - rightOffsetValue;
                if (remainingRight >= 0)
                {
                    result++;
                    result += remainingRight / (2L * rightOffsetValue);
                }
            }
        }
        return result;
    }

    private static void fillInOffsets(int width, int height, IntMatrix target, IntMatrix higher, IntMatrix lower, CharMatrix field)
    {
        for (int row = 0; row < height; row++)
        {
            for (int col = 0; col < width; col++)
            {
                Position p = new Position(row, col);
                if (field.at(row, col) != '#')
                    target.set(p, higher.at(p) - lower.at(p));
            }
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
