package de.hendriklipka.aoc2023.day24;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * idea
 * find pairs of hailstones with same dx, dy or dz - they move parallel in the respective plane
 * from their x,y or z distances we can determine possible rock-velocities
 * with multiple pairs we should be able to reduce the candidates
 */
public class Day24b2
{
    public static final double TOLERANCE = 0.00001;
    public static final int DISTANCE = 1000000; // distance between two points, when we need them from a single line

    public static void main(String[] args)
    {
        try
        {
            List<HailStone> stones= AocDataFileUtils.getLines("2023", "day24").stream().map(Day24b2::parseHailStone).toList();
            // find pairs for X
            for (int i = 0; i < stones.size() - 1; i++)
            {
                for (int j =i+1; j < stones.size(); j++)
                {
                    if (stones.get(i).dx == stones.get(j).dx)
                    {
                        final long dx = stones.get(i).x - stones.get(j).x;

                        System.out.println("common dx=" + stones.get(i).dx + " with a x diff of " + dx);
                    }
                }
            }
            System.out.println();
            // find pairs for Y
            for (int i = 0; i < stones.size() - 1; i++)
            {
                for (int j = i + 1; j < stones.size(); j++)
                {
                    if (stones.get(i).dy == stones.get(j).dy)
                    {
                        System.out.println("common dy=" + stones.get(i).dy + " with a y diff of " + (stones.get(i).y - stones.get(j).y));
                    }
                }
            }
            System.out.println();
            // find pairs for Z
            for (int i = 0; i < stones.size() - 1; i++)
            {
                for (int j = i + 1; j < stones.size(); j++)
                {
                    if (stones.get(i).dz == stones.get(j).dz)
                    {
                        System.out.println("common dz=" + stones.get(i).dz + " with a z diff of " + (stones.get(i).z - stones.get(j).z));
                    }
                }
            }

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static HailStone parseHailStone(String line)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(line, "(\\d+),\\s+(\\d+),\\s+(\\d+)\\s+@\\s+(.+),\\s+(.+),\\s+(.+)");

        return new HailStone(Long.parseLong(parts.get(0)),Long.parseLong(parts.get(1)),Long.parseLong(parts.get(2)),
                Long.parseLong(parts.get(3)),Long.parseLong(parts.get(4)),Long.parseLong(parts.get(5)));
    }

    public static class HailStone
    {
        public long x;
        public long y;
        public long z;
        public long dx;
        public long dy;
        public long dz;

        public HailStone(long x, long y, long z, long dx, long dy, long dz)
        {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        @Override
        public String toString()
        {
            return "HailStone{" +
                   "x=" + x +
                   ", y=" + y +
                   ", z=" + z +
                   ", dx=" + dx +
                   ", dy=" + dy +
                   ", dz=" + dz +
                   '}';
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HailStone hailStone = (HailStone) o;
            return x == hailStone.x && y == hailStone.y && z == hailStone.z && dx == hailStone.dx && dy == hailStone.dy && dz == hailStone.dz;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(x, y, z, dx, dy, dz);
        }
    }

}
