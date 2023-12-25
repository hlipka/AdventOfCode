package de.hendriklipka.aoc2023.day24;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Day24a
{
    private static final long MIN_BOUND=200000000000000L;
    private static final long MAX_BOUND=400000000000000L;
//    private static final long MIN_BOUND=7L;
//    private static final long MAX_BOUND=27L;
    private static final BigDecimal MIN_BOUND_D=new BigDecimal(MIN_BOUND);
    private static final BigDecimal MAX_BOUND_D=new BigDecimal(MAX_BOUND);

    public static void main(String[] args)
    {
        try
        {
            int count=0;
            List<HailStone> stones= AocParseUtils.getLines("2023", "day24").stream().map(Day24a::parseHailStone).filter(HailStone::hitsBox).toList();
            for (int i=0;i<stones.size()-1;i++)
            {
                for (int j=i+1;j<stones.size();j++)
                {
                    if (hailStonesMeet(stones.get(i), stones.get(j)))
                    {
                        count++;
                    }
                }
            }
        System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    private static boolean hailStonesMeet(HailStone stone1, HailStone stone2)
    {
        long a11=stone1.dx;
        long a12=-stone2.dx;
        long a21=stone1.dy;
        long a22=-stone2.dy;
        long b1=stone2.x-stone1.x;
        long b2=stone2.y-stone1.y;


        BigDecimal det = getDet(a11, a12, a21, a22);
        if (0==det.signum())
        {
            // lines are parallel - but are they the same?
            // the input only has one, and they are not the same line (checked manually)
            System.out.println("testing: "+stone1+", "+stone2);
            System.out.println("parallel ones...");
            System.out.println("x_diff="+(stone1.x-stone2.x));
            System.out.println("y_diff="+(stone1.y-stone2.y));
            System.out.println("gradient="+(double)(stone1.x-stone2.x)/(stone1.y-stone2.y)+", "+((double)stone1.dx/stone1.dy)+" ,"+((double)stone2.dx/stone2.dy));
            return false;
        }
        BigDecimal det1 = getDet(b1, a12, b2, a22);
        BigDecimal det2 = getDet(a11, b1, a21, b2);

        if (det.signum()==0)
        {
            // should not happen, since we excluded parallel lines already, and we also know that no velocities are zero in the input
            System.err.println("zero?"+stone1+", "+stone2);
        }

        BigDecimal t1 = det1.divide(det, 10, RoundingMode.HALF_UP);
        BigDecimal t2 = det2.divide(det, 10, RoundingMode.HALF_UP);
        if (t1.signum()==-1 || t2.signum() == -1)
        {
            return false;
        }

        if (!inRange(stone1.x, stone1.dx, t1) || !inRange(stone1.y, stone1.dy, t1) || !inRange(stone2.x, stone2.dx, t2) || !inRange(stone2.y, stone2.dy, t2))
        {
            return false;
        }

        return true;
    }

    @SuppressWarnings("IfStatementWithIdenticalBranches")
    private static boolean inRange(long pos, long dx, BigDecimal time)
    {
        BigDecimal target = new BigDecimal(pos).add(new BigDecimal(dx).multiply(time));
        if (MIN_BOUND_D.subtract(target).signum()==1)
        {
            return false;
        }
        if (MAX_BOUND_D.subtract(target).signum()==-1)
        {
            return false;
        }

        return true;
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

        @SuppressWarnings("RedundantIfStatement")
        public boolean hitsBox()
        {
            if (x<MIN_BOUND && dx<0)
            {
                return false;
            }
            if (y<MIN_BOUND && dy<0)
            {
                return false;
            }
            if (x>MAX_BOUND && dx>0)
            {
                return false;
            }
            if (y>MAX_BOUND && dy>0)
            {
                return false;
            }
            //TODO we can also check if we miss any of the corners, when its too slow
            return true;
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
