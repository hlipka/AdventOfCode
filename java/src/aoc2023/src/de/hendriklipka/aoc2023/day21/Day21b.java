package de.hendriklipka.aoc2023.day21;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.matrix.IntMatrix;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Predicate;

/**
 * idea for the solution: calculate 3 additional fields outwards in the 4 directions
 * from there we can calculate the offset (or steps cycle) for each reachable tile
 * then we can calculate how often each tile repeats, and is part of the result:
 * for that we work with the repetitions of the garden field
 * walk the center up (or down), and for each repeated field we can branch out left and right
 * since we know the offsets, we can calculate the number of tiles which are part of the result
 * (the cycle here is 2x the offset, because of the odd/even jump)
 * BUG: we cannot walk from the center column, since the steps stored for it are from the center line
 * so we need to do the center on its own, and start from the columns next to it
 * this is still TBD
 */
public class Day21b
{
//    private final static int STEPS = 26501365;
    private final static int STEPS = 20;
    private final static int GOAL = 441;

    @SuppressWarnings("PointlessArithmeticExpression")
    private static final int PARITY= STEPS % 2;

    // Note to self: we have 'odd' parity now :(
    private static final Predicate<Integer> FIELD_CONDITION = i -> i <= STEPS && PARITY == (i % 2);

    public static void main(String[] args) throws IOException
    {
        CharMatrix field = AocParseUtils.getLinesAsCharMatrix("2023", "ex21c", '#');
        int width = field.cols();
        int height = field.rows();
        Position start = field.findFirst('S');
        field.set(start, '.');
        IntMatrix center = new IntMatrix(height, width, Integer.MAX_VALUE);
        LinkedList<Position> queue = new LinkedList<>();
        queue.add(start);
        center.set(start, 0);
        walk(queue, center, field);
        // mark non-reachable places
        for (int row=0;row<height;row++)
            for (int col=0;col<width;col++)
            {
                if (field.at(row,col)!='#' && center.at(row,col)>1000)
                {
                    field.set(new Position(row, col),'#');
                }
            }

        // borders of the field are all empty, so any adjacent field (when repeating the garden) is a direct step of length 1
        // go upwards
        IntMatrix fieldUp1 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillUpwards(width, height, new LinkedList<>(), fieldUp1, center, field);

        IntMatrix fieldUp2 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillUpwards(width, height, new LinkedList<>(), fieldUp2, fieldUp1, field);

        IntMatrix fieldUp3 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillUpwards(width, height, new LinkedList<>(), fieldUp3, fieldUp2, field);

        IntMatrix upOffset = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillInOffsets(width, height, upOffset, fieldUp3, fieldUp2, field);

        //row+1
        IntMatrix fieldDown1 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillDownwards(width, height, new LinkedList<>(), fieldDown1, center, field);

        IntMatrix fieldDown2 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillDownwards(width, height, new LinkedList<>(), fieldDown2, fieldDown1, field);

        IntMatrix fieldDown3 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillDownwards(width, height, new LinkedList<>(), fieldDown3, fieldDown2, field);

        IntMatrix downOffset = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillInOffsets(width, height, downOffset, fieldDown3, fieldDown2, field);

        // col-1
        IntMatrix fieldLeft1 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillLeft(width, height, new LinkedList<>(), fieldLeft1, center, field);

        IntMatrix fieldLeft2 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillLeft(width, height, new LinkedList<>(), fieldLeft2, fieldLeft1, field);

        IntMatrix fieldLeft3 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillLeft(width, height, new LinkedList<>(), fieldLeft3, fieldLeft2, field);

        IntMatrix leftOffset = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillInOffsets(width, height, leftOffset, fieldLeft3, fieldLeft2, field);

        // col-1
        IntMatrix fieldRight1 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillRight(width, height, new LinkedList<>(), fieldRight1, center, field);

        IntMatrix fieldRight2 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillRight(width, height, new LinkedList<>(), fieldRight2, fieldRight1, field);

        IntMatrix fieldRight3 = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillRight(width, height, new LinkedList<>(), fieldRight3, fieldRight2, field);

        IntMatrix rightOffset = new IntMatrix(height, width, Integer.MAX_VALUE);
        fillInOffsets(width, height, rightOffset, fieldRight3, fieldRight2, field);

        long result = calculateFields(field, center,
                fieldUp1, upOffset,
                fieldDown1, downOffset,
                fieldLeft1, leftOffset,
                fieldRight1, rightOffset);

        System.out.println(result);
        System.out.println(GOAL+" (goal)");
        System.out.println("diff=" + (result - GOAL));
//        System.out.println("637085539055808 is too low");
        // 637085539055808 is too low
    }

    private static long calculateFields(CharMatrix field, IntMatrix center,
                                        IntMatrix fieldUp, IntMatrix upOffset,
                                        IntMatrix fieldDown, IntMatrix downOffset,
                                        IntMatrix fieldLeft, IntMatrix leftOffset,
                                        IntMatrix fieldRight, IntMatrix rightOffset)
    {
        long result = center.count(FIELD_CONDITION);

        for (int row = 0; row < field.rows(); row++)
        {
            for (int col = 0; col < field.cols(); col++)
            {
                if (field.at(row, col) != '#')
                {
                    result += calculateVertical(row, col, fieldUp, upOffset, leftOffset, rightOffset);
                    result += calculateVertical(row, col, fieldDown, downOffset, leftOffset, rightOffset);

                    // these are just for the middle row, the others were handled when going up/down
                    result += calculateHorizontal(row, col, fieldLeft, leftOffset);
                    result += calculateHorizontal(row, col, fieldRight, rightOffset);
                }
            }
        }

        return result;
    }

    private static long calculateVertical(int row, int col, IntMatrix startField, IntMatrix offsets, IntMatrix leftOffset, IntMatrix rightOffset)
    {
        long result = 0;
        final int leftOffsetValue = leftOffset.at(row, col);
        final int rightOffsetValue = rightOffset.at(row, col);

        // step upwards for each repeated field
        // the step distance advances by the calculated offset
        // we start at the first field away from the center, so we need to handle that one, and the respective row
        // the position is calculated as 'steps away from the center'
        final int rowOffset = offsets.at(row, col);
        for (int currentSteps = startField.at(row, col); currentSteps <= STEPS; currentSteps += rowOffset)
        {
            boolean isParity = PARITY == (currentSteps % 2);
            // for each row, we calculate the remaining steps, which we can use for a side-wards walk
            // we can calculate how often we can go sideways

            if (isParity)
            {
                result++;
                // when parity matches, we can start right here, so we encounter one field for each (2*offset)
                int remaining = STEPS - currentSteps;
                result += remaining / (2L * leftOffsetValue);
                result += remaining / (2L * rightOffsetValue);
            }
            else
            {
                // when parity does not match, the first field we hit is one side-ways. So we check that we can go there, and calculate from there
                int remainingLeft = STEPS - currentSteps - leftOffsetValue;
                if (remainingLeft >= 0)
                {
                    result++; // that's the field we reached via the above step calculation
                    result += remainingLeft / (2L * leftOffsetValue); // and this calculates the remaining ones
                }
                // FIXME this is wrong
                // when we are e.g. in the top left corner, we have a distance of 'side length' to the next top left corner
                // so the new steps would be 'top_left+offset'
                // but due to the free column in the middle of the input, the top right has the same steps as the top left
                // so the result needs to be 'top_left+1', which means we skip fields at the borders
                // the same issue appears with the top right when going to the left side
                // so we need to start actually one field to the sides instead of the middle
                int remainingRight = STEPS - currentSteps - rightOffsetValue;
                if (remainingRight >= 0)
                {
                    result++;
                    result += remainingRight / (2L * rightOffsetValue);
                }
            }
        }
        return result;
    }

    private static long calculateHorizontal(int row, int col, IntMatrix field, IntMatrix offsets)
    {
        long result = 0;
        int currentSteps = field.at(row, col);
        boolean isParity = PARITY == (currentSteps % 2);
        final int sideOffset = offsets.at(row, col);
        if (isParity)
        {
            result++;
            int remaining = STEPS - currentSteps;
            result += remaining / (2L * sideOffset);
        }
        else
        {
            int remaining = STEPS - currentSteps - sideOffset;
            if (remaining >= 0)
            {
                result++;
                result += remaining / (2L * sideOffset);
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
                else
                    target.set(p, 0);
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
