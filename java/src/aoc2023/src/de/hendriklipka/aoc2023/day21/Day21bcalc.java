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
 * thanks to the explanation at https://github.com/villuna/aoc23/wiki/A-Geometric-solution-to-advent-of-code-2023,-day-21
 */
public class Day21bcalc
{
    private final static int STEPS = 26501365;
    @SuppressWarnings("PointlessArithmeticExpression")

    // Note to self: we have 'odd' parity now :(
    private static final Predicate<Integer> ODD_CONDITION = i -> i <= STEPS && 1 == (i % 2);
    private static final Predicate<Integer> EVEN_CONDITION = i -> i <= STEPS && 0 == (i % 2);

    private static final Predicate<Integer> ODD_CORNERS = i -> i <= STEPS && 1 == (i % 2) && i > 65;
    private static final Predicate<Integer> EVEN_CORNERS = i -> i <= STEPS && 0 == (i % 2) && i > 65;


    public static void main(String[] args) throws IOException
    {
        CharMatrix field = AocDataFileUtils.getLinesAsCharMatrix("2023", "day21", '#');
        Position start = field.findFirst('S');
        field.set(start, '.');
        IntMatrix dists = new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        LinkedList<Position> queue = new LinkedList<>();
        queue.add(start);
        dists.set(start, 0);
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
        final int rocks = field.count('#');
        System.out.println("rocks: " + rocks);
        final long oddTiles = dists.count(ODD_CONDITION);
        final long evenTiles = dists.count(EVEN_CONDITION);
        final long oddCorners = dists.count(ODD_CORNERS);
        final long evenCorners = dists.count(EVEN_CORNERS);
        long size = field.rows();
        long fieldsPerDir = (STEPS - size / 2) / size;
        System.out.println(fieldsPerDir + " fields in each direction");
        System.out.println("o=" + oddTiles);
        System.out.println("e=" + evenTiles);
        System.out.println(131 * 131 + "->" + (oddTiles + evenTiles + rocks));
        long total =
                (fieldsPerDir + 1) * (fieldsPerDir + 1) * oddTiles
                + fieldsPerDir * fieldsPerDir * evenTiles
                - (fieldsPerDir + 1) * oddCorners
                + fieldsPerDir * evenCorners;
        System.out.println(total);
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
