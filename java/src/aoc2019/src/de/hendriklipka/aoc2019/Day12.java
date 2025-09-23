package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

public class Day12 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day12().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<Body> bodies = data.getLines().stream().map(this::parseBody).toList();
        for (int i=0;i<1000;i++)
        {
            simulateMovement(bodies);
        }

        return getEnergy(bodies);
    }

    private void simulateMovement(final List<Body> bodies)
    {
        for (Pair<Body, Body> pair: AocCollectionUtils.getPairs(bodies))
        {
            Body b1 = pair.getLeft();
            Body b2 = pair.getRight();
            b1._vx+=Integer.signum(b2._x-b1._x);
            b2._vx+=Integer.signum(b1._x-b2._x);
            b1._vy+=Integer.signum(b2._y-b1._y);
            b2._vy+=Integer.signum(b1._y-b2._y);
            b1._vz+=Integer.signum(b2._z-b1._z);
            b2._vz+=Integer.signum(b1._z-b2._z);
        }
        for  (Body b: bodies)
        {
            b._x+=b._vx;
            b._y+=b._vy;
            b._z+=b._vz;
        }
    }

    private int getEnergy(final List<Body> bodies)
    {
        return bodies.stream().mapToInt(Body::getEnergy).sum();
    }

    private Body parseBody(String line)
    {
        final List<String> parts = AocParseUtils.parsePartsFromString(line, "<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)>");
        return new Body(Integer.parseInt(parts.get(0)), Integer.parseInt(parts.get(1)), Integer.parseInt(parts.get(2)), 0,0,0);
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // since the axes are independent of each other, we find the cycle for each axis, and then do the LCM over the results
        List<Body> bodies = data.getLines().stream().map(this::parseBody).toList();
        int xLoop=findLoop(bodies, b->b._x,b->b._vx);
        System.out.println("xLoop="+xLoop);
        int yLoop=findLoop(bodies, b->b._y,b->b._vy);
        System.out.println("yLoop="+yLoop);
        int zLoop=findLoop(bodies, b->b._z,b->b._vz);
        System.out.println("zLoop="+zLoop);

        return MathUtils.lcm(xLoop, yLoop, zLoop);
    }

    private int findLoop(final List<Body> bodies, final Function<Body, Integer> posGetter, final Function<Body, Integer> velGetter)
    {
        HashSet<String> visited = new HashSet<>();
        Body b1=bodies.get(0);
        Body b2=bodies.get(1);
        Body b3=bodies.get(2);
        Body b4=bodies.get(3);

        var p1 = posGetter.apply(b1);
        var p2 = posGetter.apply(b2);
        var p3 = posGetter.apply(b3);
        var p4 = posGetter.apply(b4);
        var v1 = velGetter.apply(b1);
        var v2 = velGetter.apply(b2);
        var v3 = velGetter.apply(b3);
        var v4 = velGetter.apply(b4);

        int loops=0;
        while (true)
        {
            v1 += Integer.signum(p2 - p1);
            v1 += Integer.signum(p3 - p1);
            v1 += Integer.signum(p4 - p1);

            v2 += Integer.signum(p1 - p2);
            v2 += Integer.signum(p3 - p2);
            v2 += Integer.signum(p4 - p2);

            v3 += Integer.signum(p1 - p3);
            v3 += Integer.signum(p2 - p3);
            v3 += Integer.signum(p4 - p3);

            v4 += Integer.signum(p1 - p4);
            v4 += Integer.signum(p2 - p4);
            v4 += Integer.signum(p3 - p4);

            p1+=v1;
            p2+=v2;
            p3+=v3;
            p4+=v4;

            String key= p1 + "," + p2 + "," + p3 + "," + p4 + ";" + v1 + "," + v2 + "," + v3 + "," + v4;
            // check whether we know this state already, leave when we do, and add otherwise
            if (!visited.add(key))
                break;
            loops++;
        }
        return loops;
    }

    private static class Body
    {
        int _x;
        int _y;
        int _z;
        int _vx;
        int _vy;
        int _vz;

        public Body(final int x, final int y, final int z, final int vx, final int vy, final int vz)
        {
            _x = x;
            _y = y;
            _z = z;
            _vx = vx;
            _vy = vy;
            _vz = vz;
        }

        public int getEnergy()
        {
            return (Math.abs(_x)+Math.abs(_y)+Math.abs(_z))*(Math.abs(_vx)+Math.abs(_vy)+Math.abs(_vz));
        }

        @Override
        public String toString()
        {
            return "Body{" +
                   "_x=" + _x +
                   ", _y=" + _y +
                   ", _z=" + _z +
                   ", _vx=" + _vx +
                   ", _vy=" + _vy +
                   ", _vz=" + _vz +
                   ", en=" + getEnergy() +
                   '}';
        }
    }
}
