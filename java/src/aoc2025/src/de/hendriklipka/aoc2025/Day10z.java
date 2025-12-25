package de.hendriklipka.aoc2025;

import com.microsoft.z3.*;

import java.io.IOException;
import java.util.*;

// see also https://aldosari.medium.com/a-step-by-step-guide-to-harnessing-the-power-of-z3-solver-a96c4aa1b619 for a Z3 tutorial
public class Day10z extends Day10
{
    public static void main(String[] args)
    {
        new Day10z().doPuzzle(args);
    }


    @Override
    protected Object solvePartB() throws IOException
    {
        return data.getLines().stream().parallel().map(Day10z::parseMachine).mapToLong(this::solve2).sum();
    }

    long solve2(Machine machine)
    {
        Map<String, String> cfg = new HashMap<>();
        cfg.put("model", "true");

        try (Context ctx = new Context(cfg))
        {
            Optimize opt = ctx.mkOptimize();

            // define buttons and set their constraint (gte 0)
            List<Integer[]> buttons = machine._buttons;
            IntExpr[] vars = new IntExpr[buttons.size()];
            for (int btn = 0; btn < buttons.size(); btn++)
            {
                vars[btn] = ctx.mkIntConst("x"+btn);
                opt.Add(ctx.mkGe(vars[btn], ctx.mkInt(0)));
            }

            // define the equations for each light, and add these as constraint as well
            for (int currentLight = 0; currentLight < machine._lights.length; currentLight++)
            {
                final IntNum sum = ctx.mkInt(machine._jolts[currentLight]);
                List<IntExpr> lst = new ArrayList<>();
                for (int btn = 0; btn < buttons.size(); btn++)
                {
                    final Integer[] button = buttons.get(btn);
                    for (int light : button)
                    {
                        if (light == currentLight)
                        {
                            lst.add(vars[btn]);
                        }
                    }
                }
                opt.Add(ctx.mkEq(ctx.mkAdd(lst.toArray(new IntExpr[0])), sum));
            }

            // set the 'minimum total sum' as objective
            Optimize.Handle<IntSort> total = opt.MkMinimize(ctx.mkAdd(vars));

            // solve
            opt.Check();
            return Long.parseLong(total.toString());
        }
    }
}
