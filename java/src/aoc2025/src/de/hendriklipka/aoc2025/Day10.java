package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Day10 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day10().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        return data.getLines().stream().parallel().map(Day10::parseMachine).mapToInt(Day10::solveMachine).sum();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        return data.getLines().stream().map(Day10::parseMachine).mapToInt(Day10::solveMachine2).sum();
    }

    private static int solveMachine(Machine machine)
    {
        final MachineWorld world = new MachineWorld(machine);
        BestFirstSearch<MachineWorld, MachineState> search=new BestFirstSearch<>(world);
        search.search();
        return world.getToggles();
    }

    private static int solveMachine2(Machine machine)
    {
        final MachineWorld2 world = new MachineWorld2(machine);
        BestFirstSearch<MachineWorld2, MachineState2> search=new BestFirstSearch<>(world);
        search.search();
        System.out.println("solved a machine");
        return world.getToggles();
    }

    private static Machine parseMachine(String line)
    {
        String[] parts=line.split(" ");
        Machine machine=new Machine();
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
        private int[] _lights;
        private int[] _jolts;
        private final List<Integer[]> _buttons=new ArrayList<>();

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

    static class MachineWorld2 implements SearchWorld<MachineState2>
    {
        private final Machine _machine;
        private final int buttonCount;
        private final int lightCount;
        private int _bestToggles=Integer.MAX_VALUE;

        public MachineWorld2(final Machine machine)
        {
            _machine=machine;
            buttonCount = _machine._buttons.size();
            lightCount = _machine._lights.length;
        }

        @Override
        public MachineState2 getFirstState()
        {
            return new MachineState2(_machine, lightCount);
        }

        @Override
        public List<MachineState2> calculateNextStates(final MachineState2 currentState)
        {
            final List<MachineState2> presses=new ArrayList<>(buttonCount);
            for (int i = 0; i < buttonCount; i++)
            {
                presses.addAll(currentState.pressButton(i));
            }
            return presses;
        }

        @Override
        public boolean reachedTarget(final MachineState2 currentState)
        {
            for (int i=0;i<lightCount;i++)
            {
                if (_machine._jolts[i]!=currentState._jolts[i])
                    return false;
            }
            if (currentState._toggles<_bestToggles)
                _bestToggles=currentState._toggles;
            return true;
        }

        @Override
        public boolean canPruneBranch(final MachineState2 currentState)
        {
            return currentState._toggles>=_bestToggles-1;
        }

        @Override
        public Comparator<MachineState2> getComparator()
        {
            return Comparator.comparingInt(s->s._goodness);
        }

        public int getToggles()
        {
            return _bestToggles;
        }
    }

    static class MachineState2 implements SearchState
    {
        private final int[] _jolts;
        private final Machine _machine;
        int _toggles;
        private int _goodness;

        public MachineState2(final Machine machine, final int lightCount)
        {
            _machine=machine;
            _jolts =new int[lightCount];
            Arrays.fill(_jolts, 0);
            setGoodness();
        }

        @Override
        public String calculateStateKey()
        {
            return StringUtils.join(_jolts, ',');
        }

        @Override
        public boolean betterThan(final Object otherCost)
        {
            return (Integer)getCurrentCost()<(Integer)otherCost;
        }

        @Override
        public Object getCurrentCost()
        {
            return _toggles;
        }

        // the current state is better when it has (fewer toggles done)+(fewer toggles to do)
        public void setGoodness()
        {
            int sum=_toggles;
            for (int i=0;i<_jolts.length;i++)
            {
                sum+=_machine._jolts[i]-_jolts[i];
            }
            _goodness=sum;
        }

        List<MachineState2> pressButton(int num)
        {
            final List<MachineState2> result=new ArrayList<>();
            int maxPresses=1000;
            final Integer[] button = _machine._buttons.get(num);
            for (int light: button)
            {
                int diff=_machine._jolts[light]-_jolts[light];
                if (diff<maxPresses)
                    maxPresses=diff;
            }
            for (int i=maxPresses; i>0; i--)
            {
                MachineState2 newState=new MachineState2(_machine, _jolts.length);
                System.arraycopy(_jolts, 0, newState._jolts, 0, _jolts.length);
                newState._toggles=_toggles+i;
                for (int light: button)
                {
                    newState._jolts[light]+=i;
                }
                newState.setGoodness();
                result.add(newState);
            }

            return result;
        }

        @Override
        public String toString()
        {
            return "MachineState2{" +
                   "_jolts=" + Arrays.toString(_jolts) +
                   ", _toggles=" + _toggles +
                   ", goodness=" + _goodness +
                   '}';
        }
    }
}
