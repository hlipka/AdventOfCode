package de.hendriklipka.aoc2023.day24;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/*
thanks to https://www.reddit.com/r/adventofcode/comments/18qexvu/2023_day_24_part_2_3d_vector_interpretation_and/
(I had nearly all the code in place, since I thought we have two parallel hail stones. This was wrong, but using the 'moving frame of reference' introduces
exactly that, so I just changed the code to get the 4 needed hail stones)
 */
public class Day24b
{
    public static final double TOLERANCE = 0.000001;
    public static final int DISTANCE = 1000000; // distance between two points, when we need them from a single line
    private static final int SCALE = 10;

    public static void main(String[] args)
    {
        try
        {
            List<HailStone> stones= AocParseUtils.getLines("2023", "day24").stream().map(Day24b::parseHailStone).toList();
            // take the first stone
            HailStone first = stones.get(0);
            // move all stones into a reference frame using the first stone (so the first stone stays at 0,0,0)
            HailStone hailStone1 = first.subtract(first);
            HailStone hailStone2 = stones.get(SCALE).subtract(first);
            HailStone otherHailStone1 = stones.get(200).subtract(first);
            HailStone otherHailStone2 = stones.get(3).subtract(first);

            // we know that the rock passes through 0,0,0 then (to hit that first stone)
            // we use the second stone to get a plane - the rock needs to hit its path in the new reference plans

            // when have this plane, we can use the next two stones to find their intersection with the plane
            // and from there we can get the origin

            // calculate plane from these two hailstones
            Vector3D p1 = new Vector3D(hailStone1.x, hailStone1.y, hailStone1.z);
            Vector3D p2 = new Vector3D(hailStone2.x+ hailStone2.dx * DISTANCE, hailStone2.y + hailStone2.dy * DISTANCE, hailStone2.z + hailStone2.dz * DISTANCE);
            Vector3D p3 = new Vector3D(hailStone2.x, hailStone2.y, hailStone2.z);
            Plane plane=new Plane(p1, p2, p3, TOLERANCE);

            // we swap them so this stone is hit before the other one
            // find intersection of the plane with this hailstone
            Line line1 = new Line(
                    new Vector3D(otherHailStone1.x, otherHailStone1.y, otherHailStone1.z),
                    new Vector3D(otherHailStone1.x + otherHailStone1.dx * DISTANCE, otherHailStone1.y + otherHailStone1.dy * DISTANCE, otherHailStone1.z + otherHailStone1.dz * DISTANCE),
                    TOLERANCE
            );
            Vector3D intersect1 = plane.intersection(line1);

            // find timestamp of this intersection
            BigDecimal t1= BigDecimal.valueOf(intersect1.getX()).subtract(new BigDecimal(otherHailStone1.x))
                    .divide(BigDecimal.valueOf(otherHailStone1.dx), SCALE, RoundingMode.HALF_DOWN);

            // find intersection of the plane with this hailstone as well
            Line line2 = new Line(
                    new Vector3D(otherHailStone2.x, otherHailStone2.y, otherHailStone2.z),
                    new Vector3D(otherHailStone2.x + otherHailStone2.dx * DISTANCE, otherHailStone2.y + otherHailStone2.dy * DISTANCE, otherHailStone2.z + otherHailStone2.dz * DISTANCE),
                    TOLERANCE
            );
            Vector3D intersect2 = plane.intersection(line2);

            // find timestamp of this intersection
            BigDecimal t2= BigDecimal.valueOf(intersect2.getX()).subtract(new BigDecimal(otherHailStone2.x))
                    .divide(BigDecimal.valueOf(otherHailStone2.dx), SCALE, RoundingMode.HALF_DOWN);

            // sanity check: the time stamps need to be positive
            if (t1.signum()!=1 || t2.signum()!=1)
            {
                System.err.println("one of the time is negative");
            }

            // time the rock needs from intersect 1 to intersect 2 (which actually is time-forward)
            BigDecimal td = t2.subtract(t1);

            // from the two points we get the coordinate-diffs, and together with the time-diff we can get the origin
            BigDecimal dx = BigDecimal.valueOf(intersect2.getX()).subtract(BigDecimal.valueOf(intersect1.getX()));
            BigDecimal dy = BigDecimal.valueOf(intersect2.getY()).subtract(BigDecimal.valueOf(intersect1.getY()));
            BigDecimal dz = BigDecimal.valueOf(intersect2.getZ()).subtract(BigDecimal.valueOf(intersect1.getZ()));
            BigDecimal xOrig =
                    BigDecimal.valueOf(intersect1.getX()).subtract(t1.multiply(dx).divide(td, SCALE, RoundingMode.HALF_DOWN)).add(BigDecimal.valueOf(first.x));
            BigDecimal yOrig = BigDecimal.valueOf(intersect1.getY()).subtract(t1.multiply(dy).divide(td, SCALE, RoundingMode.HALF_DOWN)).add(
                    BigDecimal.valueOf(first.y));
            BigDecimal zOrig = BigDecimal.valueOf(intersect1.getZ()).subtract(t1.multiply(dz).divide(td, SCALE, RoundingMode.HALF_DOWN)).add(
                    BigDecimal.valueOf(first.z));

            System.out.println(xOrig.add(yOrig).add(zOrig));
            // 660876089572247 too high
            // 618930628393750
            // 606772018765657
            // 606772018765658
            // 606772018765659

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

        public HailStone subtract(HailStone first)
        {
            return new HailStone(x-first.x, y-first.y, z-first.z, dx-first.dx, dy-first.dy, dz-first.dz);
        }
    }

}
