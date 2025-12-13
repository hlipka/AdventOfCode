package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import de.hendriklipka.aoc2025.LinearSystemOfOnes.Equation;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class Day10 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day10().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        return data.getLines().stream().parallel().map(Day10::parseMachine).mapToInt(this::solveMachine).sum();
    }

    private int solveMachine(Machine machine)
    {
        final MachineWorld world = new MachineWorld(machine);
        BestFirstSearch<MachineWorld, MachineState> search = new BestFirstSearch<>(world);
        search.search();
        return world.getToggles();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return data.getLines().stream().parallel().map(Day10::parseMachine).mapToInt(this::solve2).sum();
    }

    private int solve2(Machine machine)
    {
        LinearSystemOfOnes system = new LinearSystemOfOnes();
        // group together all buttons which toggle one light, and what they need to sum up to (the jolts for that light)
        // so we can transform this into a problem where the toggles for the button affecting a light must sum up to the jolts we need
        for (int i = 0; i < machine._lights.length; i++)
        {
            Equation eq = new Equation();
            eq.setValue(machine._jolts[i]);
            List<Integer[]> integers = machine._buttons;
            for (int j = 0; j < integers.size(); j++)
            {
                final Integer[] button = integers.get(j);
                for (int l : button)
                {
                    if (l == i)
                    {
                        eq.addVariable("b" + j);
                    }
                }
            }
            system.addEquation(eq);
        }
        final var result = system.solve();
        System.out.println("solved "+machine+" as "+result);
        return result;
    }

    static Machine parseMachine(String line)
    {
        String[] parts=line.split(" ");
        Machine machine=new Machine(parts[0]);
        for (String p: parts)
        {
            if (p.startsWith("["))
            {
                machine.setLights(p.substring(1, p.length()-1));
            }
            else if (p.startsWith("("))
            {
                machine.addButton(p.substring(1, p.length()-1));
            }
            else if (p.startsWith("{"))
            {
                machine.setJolts(p.substring(1, p.length()-1));
            }
            else
            {
                throw new IllegalArgumentException("Invalid machine part: "+p);
            }
        }
        return machine;
    }

    static class Machine
    {
        private final String _id;
        int[] _lights;
        int[] _jolts;
        final List<Integer[]> _buttons=new ArrayList<>();

        public Machine(final String id)
        {
            _id=id;
        }

        public void setLights(final String s)
        {
            _lights=new int[s.length()];
            _jolts=new int[s.length()];
            for (int i=0;i<s.length();i++)
            {
                _lights[i]=s.charAt(i)=='.'?0:1;
            }
        }

        public void addButton(final String s)
        {
            String[] buttons=s.split(",");
            Integer[] btn=new Integer[buttons.length];
            for (int i = 0; i < buttons.length; i++)
            {
                final String b = buttons[i];
                btn[i]=(Integer.parseInt(b));
            }
            _buttons.add(btn);
        }

        public void setJolts(final String s)
        {
            String[] js=s.split(",");
            for (int i = 0; i < js.length; i++)
            {
                _jolts[i]=(Integer.parseInt(js[i]));
            }
        }

        @Override
        public String toString()
        {
            return "Machine{" +
                   "_id='" + _id + '\'' +
                   '}';
        }
    }

    static class MachineWorld implements SearchWorld<MachineState>
    {
        private final Machine _machine;
        private final int buttonCount;
        private final int lightCount;
        private int _bestToggles=Integer.MAX_VALUE;

        public MachineWorld(final Machine machine)
        {
            _machine=machine;
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
            final List<MachineState> presses=new ArrayList<>(buttonCount);
            for (int i = 0; i < buttonCount; i++)
            {
                presses.add(currentState.pressButton(i));
            }
            return presses;
        }

        @Override
        public boolean reachedTarget(final MachineState currentState)
        {
            for (int i=0;i<lightCount;i++)
            {
                if (_machine._lights[i]!=currentState._state[i])
                    return false;
            }
            if (currentState._toggles<_bestToggles)
                _bestToggles=currentState._toggles;
            return true;
        }

        @Override
        public boolean canPruneBranch(final MachineState currentState)
        {
            return currentState._toggles>_bestToggles;
        }

        @Override
        public Comparator<MachineState> getComparator()
        {
            return Comparator.comparingInt(MachineState::getToggles);
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
            _machine=machine;
            _pressed=new int[buttonCount];
            _state=new int[lightCount];
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
            return _toggles<(Integer)otherCost;
        }

        @Override
        public Object getCurrentCost()
        {
            return _toggles;
        }

        MachineState pressButton(int num)
        {
            MachineState newState=new MachineState(_machine, _pressed.length, _state.length);
            System.arraycopy(_pressed, 0, newState._pressed, 0, _pressed.length);
            System.arraycopy(_state, 0, newState._state, 0, _state.length);
            newState._pressed[num]++;
            newState._toggles=_toggles+1;
            final Integer[] button = _machine._buttons.get(num);
            for (int light: button)
            {
                newState._state[light]=1-newState._state[light];
            }
            return newState;
        }

        public int getToggles()
        {
            return _toggles;
        }
    }
}
