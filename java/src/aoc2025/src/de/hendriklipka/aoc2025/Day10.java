package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class Day10 extends AocPuzzle
{
    static int count=0;

    public static void main(String[] args)
    {
        new Day10().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        return data.getLines().stream().parallel().map(Day10::parseMachine).mapToInt(Day10::solveMachine).sum();
    }

    private static int solveMachine(Machine machine)
    {
        final MachineWorld world = new MachineWorld(machine);
        BestFirstSearch<MachineWorld, MachineState> search = new BestFirstSearch<>(world);
        search.search();
        return world.getToggles();
    }

    private static Machine parseMachine(String line)
    {
        String[] parts = line.split(" ");
        Machine machine = new Machine();
        for (String p : parts)
        {
            if (p.startsWith("["))
            {
                machine.setLights(p.substring(1, p.length() - 1));
            }
            else if (p.startsWith("("))
            {
                machine.addButton(p.substring(1, p.length() - 1));
            }
            else if (p.startsWith("{"))
            {
                machine.setJolts(p.substring(1, p.length() - 1));
            }
            else
            {
                throw new IllegalArgumentException("Invalid machine part: " + p);
            }
        }
        return machine;
    }

    static class Machine
    {
        private int[] _lights;
        private int[] _jolts;
        private final List<Integer[]> _buttons = new ArrayList<>();
        private final List<Integer> _presses = new ArrayList<>();

        public void setLights(final String s)
        {
            _lights = new int[s.length()];
            _jolts = new int[s.length()];
            for (int i = 0; i < s.length(); i++)
            {
                _lights[i] = s.charAt(i) == '.' ? 0 : 1;
            }
        }

        public void addButton(final String s)
        {
            String[] btns = s.split(",");
            Integer[] btn = new Integer[btns.length];
            for (int i = 0; i < btns.length; i++)
            {
                final String b = btns[i];
                btn[i] = (Integer.parseInt(b));
            }
            _buttons.add(btn);
            _presses.add(1);
        }

        public void setJolts(final String s)
        {
            String[] js = s.split(",");
            for (int i = 0; i < js.length; i++)
            {
                _jolts[i] = (Integer.parseInt(js[i]));
            }
        }
    }

    static class MachineWorld implements SearchWorld<MachineState>
    {
        private final Machine _machine;
        private final int buttonCount;
        private final int lightCount;
        private int _bestToggles = Integer.MAX_VALUE;

        public MachineWorld(final Machine machine)
        {
            _machine = machine;
            buttonCount = _machine._buttons.size();
            lightCount = _machine._lights.length;
        }

        @Override
        public MachineState getFirstState()
        {
            return new MachineState(_machine, buttonCount, lightCount);
        }

        @Override
        public List<MachineState> calculateNextStates(final MachineState currentState)
        {
            final List<MachineState> presses = new ArrayList<>(buttonCount);
            for (int i = 0; i < buttonCount; i++)
            {
                presses.add(currentState.pressButton(i));
            }
            return presses;
        }

        @Override
        public boolean reachedTarget(final MachineState currentState)
        {
            for (int i = 0; i < lightCount; i++)
            {
                if (_machine._lights[i] != currentState._state[i])
                    return false;
            }
            if (currentState._toggles < _bestToggles)
                _bestToggles = currentState._toggles;
            return true;
        }

        @Override
        public boolean canPruneBranch(final MachineState currentState)
        {
            return currentState._toggles > _bestToggles;
        }

        @Override
        public Comparator<MachineState> getComparator()
        {
            return Comparator.comparingInt(machineState -> machineState.getToggles());
        }

        public int getToggles()
        {
            return _bestToggles;
        }
    }

    static class MachineState implements SearchState
    {
        private final int[] _state;
        private final Machine _machine;
        int _toggles;
        int[] _pressed;

        public MachineState(final Machine machine, final int buttonCount, final int lightCount)
        {
            _machine = machine;
            _pressed = new int[buttonCount];
            _state = new int[lightCount];
            Arrays.fill(_pressed, 0);
            Arrays.fill(_state, 0);
        }

        @Override
        public String calculateStateKey()
        {
            return StringUtils.join(_pressed, ',');
        }

        @Override
        public boolean betterThan(final Object otherCost)
        {
            return _toggles < (Integer) otherCost;
        }

        @Override
        public Object getCurrentCost()
        {
            return _toggles;
        }

        MachineState pressButton(int num)
        {
            MachineState newState = new MachineState(_machine, _pressed.length, _state.length);
            System.arraycopy(_pressed, 0, newState._pressed, 0, _pressed.length);
            System.arraycopy(_state, 0, newState._state, 0, _state.length);
            newState._pressed[num]++;
            newState._toggles = _toggles + _machine._presses.get(num);
            final Integer[] button = _machine._buttons.get(num);
            for (int light : button)
                newState._state[light] = 1 - newState._state[light];
            return newState;
        }

        public int getToggles()
        {
            return _toggles;
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        count=0;
        return data.getLines().stream().map(Day10::parseMachine).mapToInt(this::solveMachine2).sum();
    }

    private int solveMachine2(Machine machine)
    {
        count++;
        System.out.println("looking at " + count);
        List<Pair<List<Integer>, Integer>> buttonsForLights = new ArrayList<>();
        for (int i = 0; i < machine._lights.length; i++)
        {
            List<Integer> buttons = new ArrayList<>();
            List<Integer[]> integers = machine._buttons;
            for (int j = 0; j < integers.size(); j++)
            {
                final Integer[] button = integers.get(j);
                for (int l : button)
                {
                    if (l == i)
                        buttons.add(j);
                }
            }
            buttonsForLights.add(Pair.of(buttons, machine._jolts[i]));
            System.out.println("Buttons for light " + i + ": " + StringUtils.join(buttons, ',') + " = " + machine._jolts[i]);
        }
        ReduceResult result = solveButtons(buttonsForLights);
        if (result.fullySolved)
        {
            System.out.println("fully solved machine " + count + " with " + result.presses);
            return result.presses;
        }
        System.out.println("reduced machine " + count + ", needs final solving for "+result.buttons);
        return -1;
    }

    /*
        Idea:
        - we now have a set of equations, each a sum of buttons which must be a certain number of jolts (a 'light')
        - try each light, whether it is fully part of another equation
        - if it is, we can replace this other equation by one comprised of the remaining buttons, and the remaining jolts
        - so e.g. with 'b0+b1=4' and 'b0+b1+b2+b3=10', the second equation _must_ then be 'b2+b3=10'
        - we can do this recursively (so we can check whether 'b2+b3' is part of yet another equation
        - track equations with a single button as solved
        - once we cannot replace anything anymore, solve the rest by brute force
        - in the above example, the second light also contains e.g. 'b1+b3=8', so this needs to be tried as well

        this does not help with the most complex equations - they cannot be reduced further by this process
        for that we would need further handling:
        - there can be two equations which differ by only a single buttons, so we can create a connection between these buttons ('b1=b2+3')
        - also, when we define/test a value for one button we can reduce the current equation by this button, and then again try the replacements as above

        - when there are more buttons than equations, we have 'free' buttons - we can try to define values for these buttons, then do the reduce and
        calculate from there
     */
    private ReduceResult solveButtons(final List<Pair<List<Integer>, Integer>> state)
    {
        if (isFullySolved(state))
        {
            return new ReduceResult(true, getPresses(state), null);
        }
        // all equations
        ReduceResult bestResult=null;
        for (Pair<List<Integer>, Integer> current: state)
        {
            // which of the lights contain the current one?
            List<Pair<List<Integer>, Integer>> replace=getReplaceableLights(state, current);
            if (!replace.isEmpty())
            {
                List<Pair<List<Integer>, Integer>> nextState = new ArrayList<>(state);
                // now replace each of the lights with its reduced variant
                for (Pair<List<Integer>, Integer> next: replace)
                {
                    nextState.remove(next);
                    nextState.add(doReplace(next, current));
                }
                ReduceResult result=solveButtons(nextState);
                if (result.fullySolved)
                    return result;
                if (bestResult==null || result.isBetterThan(bestResult))
                    bestResult = result;
            }
        }
        // no replacements possible, so we need to brute-force the rest
        if (bestResult == null)
        {
            return new ReduceResult(false, 0, state);
            // first sum up the fixed values
//            System.out.println("final solution found as "+state);
            // check whether all equations are of length 1 -> then there is only one solution

            // otherwise throw this into a brute-force solver

        }

        return new ReduceResult(false, 0, bestResult.buttons);
    }

    private boolean isFullySolved(final List<Pair<List<Integer>, Integer>> state)
    {
        return state.stream().map(e->e.getLeft().size()).allMatch(s->s==1);
    }

    private int getPresses(final List<Pair<List<Integer>, Integer>> state)
    {
        Map<Integer, Integer> buttons = new HashMap<>();
        for (Pair<List<Integer>, Integer> current: state)
        {
            buttons.put(current.getLeft().getFirst(), current.getRight());
        }
        return buttons.values().stream().mapToInt(Integer::intValue).sum();
    }

    record ReduceResult(boolean fullySolved, int presses, List<Pair<List<Integer>, Integer>> buttons)
    {

        public boolean isBetterThan(final ReduceResult other)
        {
            return getFoundButtons(this.buttons)<getFoundButtons(other.buttons);
        }

        private int getFoundButtons(final List<Pair<List<Integer>, Integer>> buttons)
        {
            return (int)(buttons.stream().filter(e->e.getLeft().size()==1).count());
        }
    }

    private static Pair<List<Integer>, Integer> doReplace(final Pair<List<Integer>, Integer> nextState, final Pair<List<Integer>, Integer> current)
    {
        return Pair.of(ListUtils.removeAll(nextState.getLeft(),current.getLeft()), nextState.getRight() - current.getRight());
    }

    private static List<Pair<List<Integer>, Integer>> getReplaceableLights(final List<Pair<List<Integer>, Integer>> nextState,
                                                                           final Pair<List<Integer>, Integer> current)
    {
        final List<Pair<List<Integer>, Integer>> result=new ArrayList<>();
        for (Pair<List<Integer>, Integer> next: nextState)
        {
            //noinspection SlowListContainsAll
            if (!next.equals(current) && next.getLeft().containsAll(current.getLeft()))
                result.add(next);
        }
        return result;
    }
}
