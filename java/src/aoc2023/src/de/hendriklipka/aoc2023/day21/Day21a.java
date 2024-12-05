package de.hendriklipka.aoc2023.day21;

import de.hendriklipka.aoc.AocDataFileUtils;
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
public class Day21a
{
    private final static int STEPS=64;
    public static void main(String[] args) throws IOException
    {
        CharMatrix field= AocDataFileUtils.getLinesAsCharMatrix("2023", "day21", '#');
        Position start=field.findFirst('S');
        IntMatrix dists=new IntMatrix(field.rows(), field.cols(), Integer.MAX_VALUE);
        LinkedList<Position> queue=new LinkedList<>();
        queue.add(start);
        dists.set(start,0);
        while(!queue.isEmpty())
        {
            Position here=queue.poll();
            queue.remove(here);
            final int dist = dists.at(here);
            addPos(field, dists, queue, here, Direction.UP, dist);
            addPos(field, dists, queue, here, Direction.DOWN, dist);
            addPos(field, dists, queue, here, Direction.LEFT, dist);
            addPos(field, dists, queue, here, Direction.RIGHT, dist);
        }
        System.out.println(dists.count(i->i<=STEPS&&0==(i%2)));
    }

    private static void addPos(CharMatrix field, IntMatrix dists, Collection<Position> queue, Position here, Direction dir, int dist)
    {
        Position next=here.updated(dir);
        if (field.at(next)=='#') // cannot walk here
            return;
        if (dists.at(next)<=dist+1) // we have been there, and it was cheaper as well
            return;
        dists.set(next, dist+1);
        queue.add(next);
    }
}
