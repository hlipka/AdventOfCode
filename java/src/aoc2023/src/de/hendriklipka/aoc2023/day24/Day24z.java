package de.hendriklipka.aoc2023.day24;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.microsoft.z3.*;

/*
use Z3 to solve that task
 */
public class Day24z
{
    public static void main(String[] args)
    {
        try
        {
            List<HailStone> stones= AocDataFileUtils.getLines("2023", "day24").stream().map(Day24z::parseHailStone).toList();
            HailStone s1 = stones.get(0);
            HailStone s2 = stones.get(10);
            HailStone s3 = stones.get(200);
            HailStone s4 = stones.get(3);

            // this is the 'optimizeExample' from https://github.com/Z3Prover/z3/blob/master/examples/java/JavaExample.java#L2216
            Map<String, String> cfg = new HashMap<>();
            cfg.put("model", "true");
            try (Context ctx = new Context(cfg))
            {
                Solver solver=ctx.mkSolver();

                // what we need to solve: pos(sx@tx)==pos(r@tx) for all x(0..3)

                // define the coordinates and velocity of the rock
                IntExpr rx = ctx.mkIntConst("rx");
                IntExpr ry = ctx.mkIntConst("ry");
                IntExpr rz = ctx.mkIntConst("rz");
                IntExpr rdx = ctx.mkIntConst("rdx");
                IntExpr rdy = ctx.mkIntConst("rdy");
                IntExpr rdz = ctx.mkIntConst("rdz");

                // add the conditions themselves to the solver
                addPoint("0", 0, ctx, stones, rx, ry, rz, rdx, rdy, rdz, solver);
                addPoint("1", 10, ctx, stones, rx, ry, rz, rdx, rdy, rdz, solver);
                addPoint("2", 200, ctx, stones, rx, ry, rz, rdx, rdy, rdz, solver);
                addPoint("3", 3, ctx, stones, rx, ry, rz, rdx, rdy, rdz, solver);

                // add the result as the sum of the rock start coordinates
                IntExpr result=ctx.mkIntConst("result");
                solver.add(ctx.mkEq(ctx.mkAdd(rx, ry, rz), result));

                System.out.println(solver.check());
                Model m=solver.getModel();
                System.out.println(m.evaluate(result, false));
            }

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void addPoint(final String number, final int i, final Context ctx, final List<HailStone> stones, final IntExpr rx, final IntExpr ry,
                                 final IntExpr rz, final IntExpr rdx, final IntExpr rdy, final IntExpr rdz, final Solver solver)
    {
        HailStone st=stones.get(i);
        IntExpr t = ctx.mkIntConst("t"+number);
        solver.add(ctx.mkEq(ctx.mkAdd(ctx.mkMul(t, ctx.mkInt(st.dx)), ctx.mkInt(st.x)), ctx.mkAdd(ctx.mkMul(t, rdx), rx)));
        solver.add(ctx.mkEq(ctx.mkAdd(ctx.mkMul(t, ctx.mkInt(st.dy)), ctx.mkInt(st.y)), ctx.mkAdd(ctx.mkMul(t, rdy), ry)));
        solver.add(ctx.mkEq(ctx.mkAdd(ctx.mkMul(t, ctx.mkInt(st.dz)), ctx.mkInt(st.z)), ctx.mkAdd(ctx.mkMul(t, rdz), rz)));
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
