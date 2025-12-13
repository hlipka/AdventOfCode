package de.hendriklipka.aoc.math;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LinearIntegerEquationSolverTest
{
    @Test
    void solveExactly1()
    {
        LinearIntegerEquationSolver solver = new LinearIntegerEquationSolver();
        LinearIntegerEquationSolver.Equation eq1 = new LinearIntegerEquationSolver.Equation();
        eq1.addVariable(1, "a");
        eq1.addVariable(2, "b");
        eq1.setValue(3);
        LinearIntegerEquationSolver.Equation eq2 = new LinearIntegerEquationSolver.Equation();
        eq2.addVariable(2, "a");
        eq2.addVariable(1, "b");
        eq2.setValue(3);
        solver.addEquation(eq1);
        solver.addEquation(eq2);
        solver.solveExactly();
        assertEquals(1, solver.getVariable("a"));
        assertEquals(1, solver.getVariable("b"));
    }

    @Test
    void solveExactly2()
    {
        // this is the example from https://en.wikipedia.org/wiki/Gaussian_elimination
        LinearIntegerEquationSolver solver = new LinearIntegerEquationSolver();
        LinearIntegerEquationSolver.Equation eq1 = new LinearIntegerEquationSolver.Equation();
        eq1.addVariable(2, "x");
        eq1.addVariable(1, "y");
        eq1.addVariable(-1, "z");
        eq1.setValue(8);
        LinearIntegerEquationSolver.Equation eq2 = new LinearIntegerEquationSolver.Equation();
        eq2.addVariable(-3, "x");
        eq2.addVariable(-1, "y");
        eq2.addVariable(2, "z");
        eq2.setValue(-11);
        LinearIntegerEquationSolver.Equation eq3 = new LinearIntegerEquationSolver.Equation();
        eq3.addVariable(-2, "x");
        eq3.addVariable(1, "y");
        eq3.addVariable(2, "z");
        eq3.setValue(-3);
        solver.addEquation(eq1);
        solver.addEquation(eq2);
        solver.addEquation(eq3);
        solver.solveExactly();
        assertEquals(2, solver.getVariable("x"));
        assertEquals(3, solver.getVariable("y"));
        assertEquals(-1, solver.getVariable("z"));
    }

    @Test
    void solveExactly3()
    {
        // this is the example from https://de.wikipedia.org/wiki/Gau%C3%9Fsches_Eliminationsverfahren
        LinearIntegerEquationSolver solver = new LinearIntegerEquationSolver();
        LinearIntegerEquationSolver.Equation eq1 = new LinearIntegerEquationSolver.Equation();
        eq1.addVariable(1, "x1");
        eq1.addVariable(2, "x2");
        eq1.addVariable(3, "x3");
        eq1.setValue(2);

        LinearIntegerEquationSolver.Equation eq2 = new LinearIntegerEquationSolver.Equation();
        eq2.addVariable(1, "x1");
        eq2.addVariable(1, "x2");
        eq2.addVariable(1, "x3");
        eq2.setValue(2);

        LinearIntegerEquationSolver.Equation eq3 = new LinearIntegerEquationSolver.Equation();
        eq3.addVariable(3, "x1");
        eq3.addVariable(3, "x2");
        eq3.addVariable(1, "x3");
        eq3.setValue(0);

        solver.addEquation(eq1);
        solver.addEquation(eq2);
        solver.addEquation(eq3);
        solver.solveExactly();
        assertEquals(5, solver.getVariable("x1"));
        assertEquals(-6, solver.getVariable("x2"));
        assertEquals(3, solver.getVariable("x3"));
    }

    @Test
    public void testSolveWithMinimum0()
    {
        // very simply system with just two variables
        LinearIntegerEquationSolver solver = new LinearIntegerEquationSolver();

        LinearIntegerEquationSolver.Equation eq0 = new LinearIntegerEquationSolver.Equation();
        eq0.addVariable(1, "x0");
        eq0.addVariable(1, "x1");
        eq0.setValue(3);
        solver.addEquation(eq0);

        final var context = solver.solveForBestResult(new Day10Context(eq0));
        assertEquals(3, context.getValue());
    }


    @Test
    public void testSolveWithMinimum1()
    {
        // this is 2025 day 10b, first example
        LinearIntegerEquationSolver solver = new LinearIntegerEquationSolver();

        LinearIntegerEquationSolver.Equation eq0 = new LinearIntegerEquationSolver.Equation();
        eq0.addVariable(1, "x4");
        eq0.addVariable(1, "x5");
        eq0.setValue(3);

        LinearIntegerEquationSolver.Equation eq1 = new LinearIntegerEquationSolver.Equation();
        eq1.addVariable(1, "x1");
        eq1.addVariable(1, "x5");
        eq1.setValue(5);

        LinearIntegerEquationSolver.Equation eq2 = new LinearIntegerEquationSolver.Equation();
        eq2.addVariable(1, "x2");
        eq2.addVariable(1, "x3");
        eq2.addVariable(1, "x4");
        eq2.setValue(4);

        LinearIntegerEquationSolver.Equation eq3 = new LinearIntegerEquationSolver.Equation();
        eq3.addVariable(1, "x0");
        eq3.addVariable(1, "x1");
        eq3.addVariable(1, "x3");
        eq3.setValue(7);

        solver.addEquation(eq0);
        solver.addEquation(eq1);
        solver.addEquation(eq2);
        solver.addEquation(eq3);

        final var context = solver.solveForBestResult(new Day10Context(eq0, eq1, eq2, eq3));
        assertEquals(10, context.getValue());

    }

    @Test
    public void testSolveWithMinimum2()
    {
        // this is 2025 day 10b, second example
        LinearIntegerEquationSolver solver = new LinearIntegerEquationSolver();

        LinearIntegerEquationSolver.Equation eq0 = new LinearIntegerEquationSolver.Equation();
        eq0.addVariable(1, "x0");
        eq0.addVariable(1, "x2");
        eq0.addVariable(1, "x3");
        eq0.setValue(7);

        LinearIntegerEquationSolver.Equation eq1 = new LinearIntegerEquationSolver.Equation();
        eq1.addVariable(1, "x3");
        eq1.addVariable(1, "x4");
        eq1.setValue(5);

        LinearIntegerEquationSolver.Equation eq2 = new LinearIntegerEquationSolver.Equation();
        eq2.addVariable(1, "x0");
        eq2.addVariable(1, "x1");
        eq2.addVariable(1, "x3");
        eq2.addVariable(1, "x4");
        eq2.setValue(12);

        LinearIntegerEquationSolver.Equation eq3 = new LinearIntegerEquationSolver.Equation();
        eq3.addVariable(1, "x0");
        eq3.addVariable(1, "x1");
        eq3.addVariable(1, "x4");
        eq3.setValue(7);

        LinearIntegerEquationSolver.Equation eq4 = new LinearIntegerEquationSolver.Equation();
        eq4.addVariable(1, "x0");
        eq4.addVariable(1, "x2");
        eq4.addVariable(1, "x4");
        eq4.setValue(2);

        solver.addEquation(eq4);
        solver.addEquation(eq1);
        solver.addEquation(eq2);
        solver.addEquation(eq3);
        solver.addEquation(eq4);

        final var context = solver.solveForBestResult(new Day10Context(eq0, eq1, eq2, eq3, eq4));
        assertEquals(12, context.getValue());

    }

    private static class Day10Context implements LinearIntegerEquationSolver.SolveContext
    {
        Map<String, Long> _max = new HashMap<>();
        private long _value=Long.MAX_VALUE;

        public Day10Context(final LinearIntegerEquationSolver.Equation...equations)
        {
            for (LinearIntegerEquationSolver.Equation eq : equations)
            {
                for (String varName: eq._variables.keySet())
                {
                    Long ex = _max.get(varName);
                    if (ex == null)
                    {
                        _max.put(varName, eq._value);
                    }
                    else if (ex > eq._value)
                    {
                        _max.put(varName, eq._value);
                    }
                }
            }
        }

        private Day10Context(Day10Context other)
        {
            _max.putAll(other._max);
            _value=other._value;
        }

        @Override
        public boolean variableIsValid(final long varValue)
        {
            return varValue>=0;
        }

        @Override
        public Pair<Long, Long> getVariableRange(final String varName)
        {
            return Pair.of(0L, _max.get(varName));
        }

        @Override
        public LinearIntegerEquationSolver.SolveContext copy(final long[] values)
        {
            final var copy = getDay10Context();
            copy._value= Arrays.stream(values).sum();
            return copy;
        }

        private Day10Context getDay10Context()
        {
            return new Day10Context(this);
        }

        @Override
        public boolean isBetterThan(final long otherValue)
        {
            return _value<otherValue;
        }

        @Override
        public long getValue()
        {
            return _value;
        }
    }
}