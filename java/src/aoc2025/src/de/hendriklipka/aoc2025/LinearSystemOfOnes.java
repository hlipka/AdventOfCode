package de.hendriklipka.aoc2025;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;

/*
  Solves a linear equation where all factors are either 0 or 1. Find the minimal solution,
 */
public class LinearSystemOfOnes
{
    private final Set<Equation>  _equations=new HashSet<>();
    private final Set<Equation> _solvedVariables=new HashSet<>();

    public boolean isInValid()
    {
        return !isValid;
    }

    boolean isValid=true;

    /*
        Strategy;
        - first try to reduce the equations with replacements (poor man's Gaussian replacement)
            - this moves all equations with just one variable to the 'solved' set
        - when its solved, we are done
        - choose one of the equations
            - the one with the lowest target value sum
            - choose one variable there, and loop over all possible values (see below)
            - take the minimal result from whatever we get
            - this means adding this to the solved set
        - so we can to replace it everywhere, and also further reduce all equations
    */
    public int solve()
    {
        int bestScore=Integer.MAX_VALUE;

        // do a first reduction
        reduce();
        if (!isValid)
        {
            return Integer.MAX_VALUE;
        }
        if (isSolved())
        {
            return _solvedVariables.stream().mapToInt(Equation::getValue).sum();
        }
        LinearSystemOfOnes.Equation eq = getEquations().stream().min(Comparator.comparingInt(LinearSystemOfOnes.Equation::getValue)).orElseThrow();
        int targetValue = eq.getValue();
        String varName = eq.getVariables().iterator().next();
        for (int i = 0; i <= targetValue; i++)
        {
            LinearSystemOfOnes newSystem = setVariable(varName, i);
            // variable is already set with a different value (so we technically should not have selected it here)
            if (null == newSystem)
                continue;
            if (newSystem.isInValid())
                continue;
            int score=newSystem.solve();
            if (newSystem.isInValid())
                continue;
            if (score<bestScore)
            {
                bestScore=score;
            }
        }

        return bestScore;
    }

    // we are done when all equations are removed, because then all variables have known values
    private boolean isSolved()
    {
        return _equations.isEmpty();
    }

    private void reduce()
    {
        List<Equation> singles=_equations.stream().filter(eq->eq._variables.size()==1&&eq._value>=0).toList();
        _solvedVariables.addAll(singles);
        singles.forEach(_equations::remove);
        while (true)
        {
            boolean didReplacements=false;
            final var reduceWith = new ArrayList<>(_equations);
            reduceWith.addAll(_solvedVariables);
            for (Equation eq : reduceWith)
            {
                // which other equations contain 'eq'?
                List<Equation> reducible = _equations.stream().filter(e -> e.isReducibleBy(eq)).toList();
                if (!reducible.isEmpty())
                {
                    didReplacements=true;
                    for (Equation reducableEquation : reducible)
                    {
                        _equations.remove(reducableEquation);
                        Equation reduced=reducableEquation.reduceBy(eq);
                        // check for conflicts - an equation with the same variables (no matter what the value is)
                        if (hasSimilarEquation(reduced))
                        {
                            isValid = false;
                            return;
                        }
                        // add as variable
                        if (reduced._variables.size()==1)
                        {
                            if (reduced._value < 0)
                            {
                                isValid=false;
                                return;
                            }
                            for (Equation v: _solvedVariables)
                            {
                                if (v._variables.iterator().next().equals(reduced._variables.iterator().next()) && v._value!=reduced._value)
                                {
                                    isValid = false;
                                    return;
                                }
                            }
                            _solvedVariables.add(reduced);
                        }
                        else
                        {
                            _equations.add(reduced);
                        }
                    }
                }
            }
            if (!didReplacements)
            {
                break;
            }
        }
    }

    private boolean hasSimilarEquation(final Equation eq)
    {
        return _equations.stream().anyMatch(equation -> equation.isSimilar(eq));
    }

    public Collection<Equation> getEquations()
    {
        return _equations;
    }

    public LinearSystemOfOnes setVariable(final String varName, final int value)
    {
        // conflict detection
        if (hasVariable(varName) && value!=getVarValue(varName))
        {
            return null;
        }
        if (value<0)
        {
            isValid=false;
            return null;
        }

        Equation eq=new Equation();
        eq.setValue(value);
        eq.addVariable(varName);
        final LinearSystemOfOnes next = new LinearSystemOfOnes();
        next._solvedVariables.addAll(_solvedVariables);
        for (Equation equation : _equations)
        {
            if (equation.isReducibleBy(eq))
            {
                final var reduced = equation.reduceBy(eq);
                if (reduced._variables.size()==1)
                    next._solvedVariables.add(reduced);
                else
                    next._equations.add(reduced);
            }
            else
                next._equations.add(equation);
        }
        next._solvedVariables.add(eq);
        next.reduce();

        return next;
    }

    private int getVarValue(final String varName)
    {
        return _solvedVariables.stream().filter(v->varName.equals(v.getVariables().iterator().next())).findFirst().orElseThrow().getValue();
    }

    private boolean hasVariable(final String varName)
    {
        return _solvedVariables.stream().anyMatch(e -> e._variables.contains(varName));
    }

    public void addEquation(final Equation eq)
    {
        _equations.add(eq);
    }

    public static class Equation
    {
        private int _value;
        private final Set<String> _variables=new HashSet<>();

        public int getValue()
        {
            return _value;
        }

        public Collection<String> getVariables()
        {
            return _variables;
        }

        public void addVariable(final String varName)
        {
            _variables.add(varName);
        }

        public void setValue(final int value)
        {
            _value = value;
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            final Equation equation = (Equation) o;

            return new EqualsBuilder().append(_value, equation._value).append(_variables, equation._variables).isEquals();
        }

        @Override
        public int hashCode()
        {
            return new HashCodeBuilder(17, 37).append(_value).append(_variables).toHashCode();
        }

        @Override
        public String toString()
        {
            return "Eq{" + StringUtils.join(_variables, '+') + '='+_value + '}';
        }

        public boolean containsAllVariables(final Collection<String> variables)
        {
            return _variables.containsAll(variables);
        }
        public boolean isReducibleBy(Equation eq)
        {
            return !this.equals(eq) && containsAllVariables(eq._variables);
        }

        public Equation reduceBy(final Equation eq)
        {
            final Equation result=new Equation();
            result.setValue(_value-eq.getValue());
            result._variables.addAll(ListUtils.removeAll(_variables, eq._variables));
            return result;
        }

        public boolean isSimilar(final Equation eq)
        {
            return _variables.containsAll(eq._variables) && eq._variables.containsAll(_variables) && eq._value!=_value;
        }
    }
}
