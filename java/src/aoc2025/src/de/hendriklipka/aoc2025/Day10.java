package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class Day10 extends AocPuzzle
{
    static boolean[] _done=new boolean[0];
    static long _start=0;

    public static void main(String[] args)
    {
        new Day10().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        return data.getLines().stream().parallel().map(Day10::parseMachine).mapToInt(this::solveMachine).sum();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final int[] count = {1};
        _done=new boolean[data.getLines().size()+1];
        Arrays.fill(_done,false);
        final List<MachineSolver> machines = data.getLines().stream().map(Day10::parseMachine).map(m->new MachineSolver(m, count[0]++)).toList();
        _start=System.currentTimeMillis();
        return machines.stream().parallel().mapToInt(MachineSolver::solve).sum();
    }

    private int solveMachine(Machine machine)
    {
        final MachineWorld world = new MachineWorld(machine);
        BestFirstSearch<MachineWorld, MachineState> search=new BestFirstSearch<>(world);
        search.search();
        return world.getToggles();
    }

    static void checkSolved()
    {
        System.out.println("took "+(System.currentTimeMillis()-_start)/1000+"s");
        List<Integer> open=new ArrayList<>();
        for (int i=1;i<_done.length;i++)
        {
            if (!_done[i])
                open.add(i);
        }
        System.out.println(open.size()+" problems left");
        if (open.size()<10)
        {
            System.out.println("open lines: "+StringUtils.join(open,","));
        }
    }

    private static class MachineSolver
    {
        private final Machine machine;
        int currentBestPresses = Integer.MAX_VALUE;
        private int _count;

        public MachineSolver(final Machine machine, final int count)
        {
            this.machine = machine;
            _count = count;
        }

        private int solve()
        {
            // group together all buttons which toggle one light, and what they need to sum up to (the jolts for that light)
            // so we can transform this into a problem where the toggles for the button affecting a light must sum up to the jolts we need
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
            }
            // shortest list first
            buttonsForLights.sort(Comparator.comparingInt(t -> t.getLeft().size()));

            // start with the first light
            Pair<List<Integer>, Integer> pair = buttonsForLights.removeFirst();
            int bestPresses = handleNextList(pair, new HashMap<>(), buttonsForLights, 0);
            System.out.println("solved machine " + _count + " with " + bestPresses);
            _done[_count] = true;
            checkSolved();
            return bestPresses;
        }

        private int handleNextList(final Pair<List<Integer>, Integer> pair, final Map<Integer, Integer> buttonsPressed,
                                   final List<Pair<List<Integer>, Integer>> buttonsForLights, final int currentPresses)
        {
            if (currentPresses>currentBestPresses)
                return currentPresses+1;
            // determine which of the button have no value yet - the pressed ones already have a fixed value
            Collection<Integer> buttonsLeft = getRemainingButtons(pair, buttonsPressed);
            // we are done when there are no buttons left to handle, in that case we go to the next list
            if (buttonsLeft.isEmpty())
            {
                // verify that the buttons sum up to the needed jolts
                int jolts = 0;
                for (Map.Entry<Integer, Integer> entry : buttonsPressed.entrySet())
                {
                    if (pair.getLeft().contains(entry.getKey()))
                        jolts += entry.getValue();
                }
                if (jolts != pair.getRight())
                    return Integer.MAX_VALUE;
                // if there is no list, we are done
                if (buttonsForLights.isEmpty())
                {
                    if (currentPresses<currentBestPresses)
                        currentBestPresses=currentPresses;
                    return currentPresses;
                }
                return gotoNextLight(buttonsPressed, buttonsForLights, currentPresses);
            }
            int currentLightJolts = pair.getRight();
            // we need to remove anything that was pressed already
            for (Map.Entry<Integer, Integer> entry : buttonsPressed.entrySet())
            {
                // remove any presses
                if (pair.getLeft().contains(entry.getKey()))
                    currentLightJolts -= entry.getValue();
            }
            // we need at least 'currentLightJolts' presses to reach our goal, so we can check for that
            if (currentLightJolts+currentPresses>currentBestPresses)
            {
                return Integer.MAX_VALUE;
            }
            // when we have all jolts, we can set all remaining buttons to 0 and go to the next list
            if (0 == currentLightJolts)
            {
                if (buttonsForLights.isEmpty())
                {
                    if (currentPresses<currentBestPresses)
                        currentBestPresses=currentPresses;
                    return currentPresses;
                }
                final Map<Integer, Integer> currentButtonsPressed = new HashMap<>(buttonsPressed);
                for (int btn : buttonsLeft)
                {
                    currentButtonsPressed.put(btn, 0);
                }
                return gotoNextLight(currentButtonsPressed, buttonsForLights, currentPresses);
            }
            if (currentLightJolts < 0)
                return Integer.MAX_VALUE;
            return handleNextButton(buttonsLeft, buttonsPressed, buttonsForLights, 0, currentLightJolts, currentPresses);
        }

        private int handleNextButton(final Collection<Integer> buttonsLeft, final Map<Integer, Integer> buttonsPressed,
                                     final List<Pair<List<Integer>, Integer>> buttonsForLights, final int joltsProvided, final int currentLightJolts,
                                     final int currentPresses)
        {
            if (currentPresses>currentBestPresses)
                return currentPresses+1;
            final var button = buttonsLeft.iterator().next();
            // this is the only button left, so we know its value
            if (buttonsLeft.size() == 1)
            {
                if (buttonsForLights.isEmpty())
                {
                    final int presses = currentPresses + (currentLightJolts - joltsProvided);
                    if (presses<currentBestPresses)
                        currentBestPresses=presses;
                    return presses;
                }
                final Map<Integer, Integer> nextButtonsPressed = new HashMap<>(buttonsPressed);
                nextButtonsPressed.put(button, currentLightJolts - joltsProvided);
                return gotoNextLight(nextButtonsPressed, buttonsForLights, currentPresses + (currentLightJolts - joltsProvided));
            }
            final Map<Integer, Integer> currentButtonsPressed = new HashMap<>(buttonsPressed);
            int bestPresses = Integer.MAX_VALUE;
            Collection<Integer> nextButtonsLeft = new ArrayList<>(buttonsLeft);
            nextButtonsLeft.remove(button);
            for (int i = currentLightJolts - joltsProvided; i > -1; i--)
            {
                currentButtonsPressed.put(button, i);
                int presses = handleNextButton(nextButtonsLeft, currentButtonsPressed, buttonsForLights, i + joltsProvided, currentLightJolts,
                        currentPresses + i);
                if (presses < bestPresses)
                {
                    bestPresses = presses;
                }
            }
            if (bestPresses<currentBestPresses)
                currentBestPresses=bestPresses;
            return bestPresses;
        }

        private int gotoNextLight(final Map<Integer, Integer> buttonsPressed, final List<Pair<List<Integer>, Integer>> buttonsForLights,
                                  final int globalBestPresses)
        {
            List<Pair<List<Integer>, Integer>> nextButtonsForLights = new ArrayList<>(buttonsForLights);
            // sort so the light with the fewest unknown buttons comes first
            nextButtonsForLights.sort((pair1, pair2) ->
            {
                int c1 = getRemainingButtons(pair1, buttonsPressed).size();
                int c2 = getRemainingButtons(pair2, buttonsPressed).size();
                final var compared = Integer.compare(c1, c2);
                if (c1==1||c2==1)
                {
                    // choose the formula with one buttons left - when both are short use up the most jolts
                    if (0==compared)
                    {
                        return -Integer.compare(pair1.getValue(), pair2.getValue());
                    }
                }
                // when both formulas are equal length, take the one with less jolts (faster to loop through)
                if (0 == compared)
                {
                    return Integer.compare(pair1.getValue(), pair2.getValue());
                }
                // prefer the one with fewer free buttons
                return compared;
            });
            Pair<List<Integer>, Integer> nextPair = nextButtonsForLights.removeFirst();
            return handleNextList(nextPair, buttonsPressed, nextButtonsForLights, globalBestPresses);
        }

        private static Collection<Integer> getRemainingButtons(final Pair<List<Integer>, Integer> pair, final Map<Integer, Integer> buttonsPressed)
        {
            return CollectionUtils.removeAll(pair.getLeft(), buttonsPressed.keySet());
        }

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
}
