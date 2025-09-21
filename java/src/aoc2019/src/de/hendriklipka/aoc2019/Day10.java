package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day10 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day10().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final CharMatrix asteroids = data.getLinesAsCharMatrix('.');

        return asteroids.allMatchingPositions('#').stream().mapToInt(pos->getVisibleAsteroids(pos, asteroids)).max().orElseThrow();
    }

    private int getVisibleAsteroids(final Position pos, final CharMatrix asteroids)
    {
        // start with all asteroids visible, we remove the ones which are hidden
        CharMatrix visible=asteroids.copyOf();
        visible.set(pos,'X'); // the asteroid cannot see itself
        for (Position other: visible.allMatchingPositions('#'))
        {
            // find the offset of this asteroid so we can mark anything hidden behind it
            Pair<Integer, Integer> offset = pos.diff(other);
            // reduce the offset to the minimal value: dx==dy->1,1, 6,3->2,1, -5,0->-1,0
            int gcd=ArithmeticUtils.gcd(Math.abs(offset.getLeft()), Math.abs(offset.getRight()));
            offset=Pair.of(offset.getLeft()/gcd,  offset.getRight()/gcd);
            // now go and hide the other asteroids
            other = other.updated(offset.getLeft(), offset.getRight());
            while (asteroids.in(other))
            {
                visible.set(other, '.'); // make anything behind that one asteroid invisible
                other = other.updated(offset.getLeft(), offset.getRight());
            }
        }
        return visible.count('#');
    }

    @Override
    protected Object solvePartB() throws IOException
    {
//        System.out.println(comparePos(new Position(0,0), new Position(-2, 2), new Position(-2, 3)));
        /*
         Strategy:
         - get the observing position as in part A (call it 'center')
         - from there, get lists of asteroids which are in line with ech other
           - similar to above, loop over all asteroids, get their offsets
           - GCD down to the smallest one
           - with that offset, start from the center and create a list of all asteroids we find
          - this gives us a list of lists, each with the closest asteroid in front
          - we order these lists by the angle of their first element (it's the same angle for the other ones)
            - in the top right quadrant, this is simply dy/dx, ascending (note: dx is 0 right at the start)
              (we could calculate the angle via tan/arctan, but its strictly monotone anyway)
            - in the bottom right, it's still dy/dx, descending, since the values are now negative (dx is 0 right at the end)
            - in the right half (dx<0), it's still dx/dy - we start with the most positive values again)
            so: we first sort by dx pos/neg, and then sort by dx/dy, and take care of dx==0
          - from there, we can simply shoot down in the prescribed order, and remove the destroyed asteroids from the start of their list
         */

        final CharMatrix asteroids = data.getLinesAsCharMatrix('.');
        Position startPos =
                asteroids.allMatchingPositions('#').stream().max(Comparator.comparingInt(
                        position -> getVisibleAsteroids(position, asteroids))).orElseThrow();
        List<List<Position>> allRays = getRays(asteroids, startPos);
        allRays.sort((positions, other) -> comparePos(startPos, positions.getFirst(), other.getFirst()));
        // now let's destroy some asteroids
        List<Position> destroyed=new ArrayList<>();
        while (allRays.stream().anyMatch(r->!r.isEmpty()))
        {
            for (List<Position> ray: allRays)
            {
                if (ray.isEmpty())
                    continue;
                Position dest = ray.getFirst();
                ray.removeFirst();
                destroyed.add(dest);
            }
        }

        final var bet = destroyed.get(199);
        return bet.col*100+bet.row;
    }

    // Returns a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
    // less than means: a smaller angle, with 0 upwards and then to the right
    private int comparePos(final Position center, final Position first, final Position other)
    {
        final Pair<Integer, Integer> ofs1 = center.diff(first);
        final Pair<Integer, Integer> ofs2 = center.diff(other);
        int q1=getQuadrant(ofs1);
        int q2=getQuadrant(ofs2);
        if (q1<q2)
            return -1;
        if (q2<q1)
            return 1;

        double r1=(double)ofs1.getRight()/(double)ofs1.getLeft();
        double r2=(double)ofs2.getRight()/(double)ofs2.getLeft();
        return -Double.compare(r1,r2);
    }

    // left is dy, right is dx
    // dy negative if first quadrant
    private int getQuadrant(final Pair<Integer, Integer> p)
    {
        if (p.getRight()>=0)
        {
            if (p.getLeft()<0)
            {
                return 1;
            }
            else
            {
                return 2;
            }
        }
        if (p.getLeft() < 0)
        {
            return 4;
        }
        else
        {
            return 3;
        }
    }

    private List<List<Position>> getRays(final CharMatrix asteroids, final Position startPos)
    {
        asteroids.set(startPos, 'X');
        final List<List<Position>> rays = new ArrayList<>();
        for (Position other: asteroids.allMatchingPositions('#'))
        {
            // we handled this by some asteroids in the line of sight already
            if (asteroids.at(other)=='.')
                continue;
            List<Position> ray=new ArrayList<>();
            Pair<Integer, Integer> offset = startPos.diff(other);
            // reduce the offset to the minimal value: dx==dy->1,1, 6,3->2,1, -5,0->-1,0
            int gcd = ArithmeticUtils.gcd(Math.abs(offset.getLeft()), Math.abs(offset.getRight()));
            if (gcd==0)
                System.out.println("huh?");
            offset = Pair.of(offset.getLeft() / gcd, offset.getRight() / gcd);
            // we now start at the center
            other = startPos.updated(offset.getLeft(), offset.getRight());
            while (asteroids.in(other))
            {
                if (asteroids.at(other)=='#')
                {
                    ray.add(other);
                    asteroids.set(other, '.');
                }
                other = other.updated(offset.getLeft(), offset.getRight());
            }
            if (!ray.isEmpty())
            {
                rays.add(ray);
            }

        }
        return rays;
    }
}
