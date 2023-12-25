package de.hendriklipka.aoc2023.day24;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * we have 2 parallel lines, so we can calculate a plane from it
 * we then can choose 2 other lines, and calculate their intersection with the plane
 * from these two intersection points we can calculate the trajectory of the stone
 * also, since we know he times for these intersection points, we can calculate where the stone needs to start
 */
public class Day24b
{
    public static final double TOLERANCE = 0.00001;
    public static final int DISTANCE = 100000; // distance between two points, when we need them from a single line

    public static void main(String[] args)
    {
        try
        {
            List<HailStone> stones= AocParseUtils.getLines("2023", "day24").stream().map(Day24b::parseHailStone).toList();
            Pair<HailStone, HailStone> pair = findPair(stones);
            HailStone hailStone1=pair.getLeft();
            HailStone hailStone2=pair.getRight();
            List<HailStone> otherHailStones=stones.stream().filter(s->!s.equals(hailStone1)).filter(s->!s.equals(hailStone2)).limit(2).toList();

            // calculate plane from these two hailstones
            Vector3D p1 = new Vector3D(hailStone1.x, hailStone1.y, hailStone1.z);
            Vector3D p2 = new Vector3D(hailStone1.x+ hailStone1.dx * DISTANCE, hailStone1.y + hailStone1.dy * DISTANCE, hailStone1.z + hailStone1.dz * DISTANCE);
            Vector3D p3 = new Vector3D(hailStone2.x, hailStone2.y, hailStone2.z);
            Plane plane=new Plane(p1, p2, p3, TOLERANCE);

            HailStone otherHailStone1=otherHailStones.get(0);
            // find intersection of the plane with this hailstone
            Line line1 = new Line(
                    new Vector3D(otherHailStone1.x, otherHailStone1.y, otherHailStone1.z),
                    new Vector3D(otherHailStone1.x + otherHailStone1.dx * DISTANCE, otherHailStone1.y + otherHailStone1.dy * DISTANCE, otherHailStone1.z + otherHailStone1.dz * DISTANCE),
                    TOLERANCE
            );
            Vector3D intersect1 = plane.intersection(line1);

            // find timestamp of this intersection
            BigDecimal t1= BigDecimal.valueOf(intersect1.getX()).subtract(new BigDecimal(otherHailStone1.x))
                    .divide(BigDecimal.valueOf(otherHailStone1.dx), 5, RoundingMode.HALF_DOWN);

            // find intersection of the plane with this hailstone as well
            HailStone otherHailStone2=otherHailStones.get(1);
            Line line2 = new Line(
                    new Vector3D(otherHailStone2.x, otherHailStone2.y, otherHailStone2.z),
                    new Vector3D(otherHailStone2.x + otherHailStone2.dx * DISTANCE, otherHailStone2.y + otherHailStone2.dy * DISTANCE, otherHailStone2.z + otherHailStone2.dz * DISTANCE),
                    TOLERANCE
            );
            Vector3D intersect2 = plane.intersection(line2);

            // find timestamp of this intersection
            BigDecimal t2= BigDecimal.valueOf(intersect2.getX()).subtract(new BigDecimal(otherHailStone2.x))
                    .divide(BigDecimal.valueOf(otherHailStone2.dx), 5, RoundingMode.HALF_DOWN);

            // sanity check: the time stamps need to be positive
            if (t1.signum()!=1 || t2.signum()!=1)
            {
                System.err.println("one of the time is negative");
            }

            // find line through these two intersection points
            // from the points we can directly get the velocity
            Line rockPath=new Line(intersect1, intersect2, TOLERANCE);
            // hmm - actually this velocity should be the one of the rockPath line?
            BigDecimal velocityX = BigDecimal.valueOf(intersect1.getX()).subtract(BigDecimal.valueOf(intersect2.getX()))
                    .divide(t1.subtract(t2), 5, RoundingMode.HALF_DOWN);
            System.out.println(velocityX);

            // with the velocity, and the timestamps, we can get the origin
            // x0=x1-vx*t1

            System.out.println(-1);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Pair<HailStone, HailStone> findPair(List<HailStone> stones)
    {
        for (int i=0;i<stones.size()-1;i++)
        {
            for (int j=i+1;j<stones.size();j++)
            {
                if (areParallel(stones.get(i), stones.get(j)))
                {
                    return Pair.of(stones.get(i), stones.get(j));
                }
            }
        }
        throw new IllegalArgumentException("no pair found");
    }

    private static boolean areParallel(HailStone stone1, HailStone stone2)
    {
        long a11=stone1.dx;
        long a12=-stone2.dx;
        long a21=stone1.dy;
        long a22=-stone2.dy;

        BigDecimal det = getDet(a11, a12, a21, a22);
        return 0 == det.signum();
    }

    private static BigDecimal getDet(long a, long c, long b, long d)
    {
        BigDecimal ad=new BigDecimal(a).multiply(new BigDecimal(d));
        BigDecimal bc=new BigDecimal(b).multiply(new BigDecimal(c));
        return ad.subtract(bc);
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
    }
}
