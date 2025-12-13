package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.math.LinearIntegerEquationSolver;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class Day10b extends Day10
{
    public static void main(String[] args)
    {
        new Day10b().doPuzzle(args);
    }


    @Override
    protected Object solvePartB() throws IOException
    {
        return data.getLines().stream().parallel().map(Day10b::parseMachine).mapToLong(this::solve2).sum();
    }

    long solve2(Machine machine)
    {
        LinearIntegerEquationSolver system = new LinearIntegerEquationSolver();
        final var context = new Day10Context();
        // group together all buttons which toggle one light, and what they need to sum up to (the jolts for that light)
        // so we can transform this into a problem where the toggles for the button affecting a light must sum up to the jolts we need
        for (int i = 0; i < machine._lights.length; i++)
        {
            LinearIntegerEquationSolver.Equation eq = new LinearIntegerEquationSolver.Equation();
            eq.setValue(machine._jolts[i]);
            List<Integer[]> integers = machine._buttons;
            for (int j = 0; j < integers.size(); j++)
            {
                final Integer[] button = integers.get(j);
                for (int l : button)
                {
                    if (l == i)
                    {
                        eq.addVariable(1, "b" + j);
                    }
                }
            }
            system.addEquation(eq);
            context.addEquation(eq);
        }
        final var result = system.solveForBestResult(context);
        System.out.println("solved "+machine+" as "+result.getValue());
        return result.getValue();
    }

    private static class Day10Context implements LinearIntegerEquationSolver.SolveContext
    {
        Map<String, Pair<Long, Long>> _max = new HashMap<>();
        private long _value = Long.MAX_VALUE;

        public Day10Context(final LinearIntegerEquationSolver.Equation... equations)
        {
            for (LinearIntegerEquationSolver.Equation eq : equations)
            {
                addEquation(eq);
            }
        }

        private Day10Context(Day10Context other)
        {
            _max.putAll(other._max);
            _value = other._value;
        }

        @Override
        public boolean variableIsValid(final long varValue)
        {
            return varValue >= 0;
        }

        @Override
        public Pair<Long, Long> getVariableRange(final String varName)
        {
            return _max.get(varName);
        }

        @Override
        public LinearIntegerEquationSolver.SolveContext copy(final long[] values)
        {
            final var copy = new Day10Context(this);
            copy._value = Arrays.stream(values).sum();
            return copy;
        }

        @Override
        public boolean isBetterThan(final long otherValue)
        {
            return _value < otherValue;
        }

        @Override
        public long getValue()
        {
            return _value;
        }

        public void addEquation(final LinearIntegerEquationSolver.Equation eq)
        {
            for (String varName : eq.getVariables().keySet())
            {
                Pair<Long, Long> ex = _max.get(varName);
                if (ex == null)
                {
                    _max.put(varName, Pair.of(0L, eq.getValue()));
                }
                else if (ex.getRight() > eq.getValue())
                {
                    _max.put(varName, Pair.of(0L, eq.getValue()));
                }
            }
        }
    }
}
