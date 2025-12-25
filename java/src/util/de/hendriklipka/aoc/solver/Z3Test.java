package de.hendriklipka.aoc.solver;

import com.microsoft.z3.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class Z3Test
{
    @Test
    public void z3Demo()
    {
        // this is the 'optimizeExample' from https://github.com/Z3Prover/z3/blob/master/examples/java/JavaExample.java#L2216
        Map<String, String> cfg = new HashMap<>();
        cfg.put("model", "true");
        try (Context ctx = new Context(cfg))
        {

            System.out.println("Opt");

            Optimize opt = ctx.mkOptimize();

            // Set constraints.
            IntExpr xExp = ctx.mkIntConst("x");
            IntExpr yExp = ctx.mkIntConst("y");

            opt.Add(ctx.mkEq(ctx.mkAdd(xExp, yExp), ctx.mkInt(10)),
                    ctx.mkGe(xExp, ctx.mkInt(0)),
                    ctx.mkGe(yExp, ctx.mkInt(0)));

            // Set objectives.
            Optimize.Handle<IntSort> mx = opt.MkMaximize(xExp);
            Optimize.Handle<IntSort> my = opt.MkMaximize(yExp);

            System.out.println(opt.Check());
            System.out.println(mx);
            System.out.println(my);
        }
    }

    @Test
    public void z3Test1()
    {
        // the minimum test example used for the linear solver
        Map<String, String> cfg = new HashMap<>();
        cfg.put("model", "true");
        try (Context ctx = new Context(cfg))
        {

            System.out.println("Opt");

            Optimize opt = ctx.mkOptimize();

            // Set constraints.
            IntExpr xExp = ctx.mkIntConst("a");
            IntExpr yExp = ctx.mkIntConst("b");

            opt.Add(ctx.mkEq(ctx.mkAdd(xExp, yExp), ctx.mkInt(3)),
                    ctx.mkGe(xExp, ctx.mkInt(0)),
                    ctx.mkGe(yExp, ctx.mkInt(0)));

            // Set objectives.
            Optimize.Handle<IntSort> total = opt.MkMinimize(ctx.mkAdd(xExp, yExp));

            System.out.println(opt.Check());
            System.out.println(total);
        }
    }

    @Test
    public void z3Test2()
    {
        // this is 2025 day 10b, first example
        Map<String, String> cfg = new HashMap<>();
        cfg.put("model", "true");
        try (Context ctx = new Context(cfg))
        {
            System.out.println("Opt");

            Optimize opt = ctx.mkOptimize();

            // Set constraints.
            IntExpr x0Exp = ctx.mkIntConst("x0");
            IntExpr x1Exp = ctx.mkIntConst("x1");
            IntExpr x2Exp = ctx.mkIntConst("x2");
            IntExpr x3Exp = ctx.mkIntConst("x3");
            IntExpr x4Exp = ctx.mkIntConst("x4");
            IntExpr x5Exp = ctx.mkIntConst("x5");

            opt.Add(
                    ctx.mkEq(ctx.mkAdd(x4Exp, x5Exp), ctx.mkInt(3)),
                    ctx.mkEq(ctx.mkAdd(x1Exp, x5Exp), ctx.mkInt(5)),
                    ctx.mkEq(ctx.mkAdd(x2Exp, x3Exp, x4Exp), ctx.mkInt(4)),
                    ctx.mkEq(ctx.mkAdd(x0Exp, x1Exp, x3Exp), ctx.mkInt(7)),
                    ctx.mkGe(x0Exp, ctx.mkInt(0)),
                    ctx.mkGe(x1Exp, ctx.mkInt(0)),
                    ctx.mkGe(x2Exp, ctx.mkInt(0)),
                    ctx.mkGe(x3Exp, ctx.mkInt(0)),
                    ctx.mkGe(x4Exp, ctx.mkInt(0)),
                    ctx.mkGe(x5Exp, ctx.mkInt(0))
            );

            // Set objectives.
            Optimize.Handle<IntSort> total = opt.MkMinimize(ctx.mkAdd(x0Exp,x1Exp,x2Exp,x3Exp,x4Exp,x5Exp));

            System.out.println(opt.Check());
            System.out.println(total); // this should be 10
        }

    }
}
