package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.matrix.IntMatrix;
import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day22 extends AocPuzzle
{

    private static final int MOD = 20183;

    private static final char ROCK = '.';
    private static final char WET = '=';
    private static final char NARROW = '|';
    private static final char BLOCKED = '#';

    private static final char TOOL_NEITHER = 'N';
    private static final char TOOL_TORCH = 'T';
    private static final char TOOL_GEAR = 'C';

    public static void main(String[] args)
    {
        new Day22().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> input=data.getLines();
        int depth= AocParseUtils.parseIntFromString(input.get(0), "depth: (\\d+)");
        int xTarget= AocParseUtils.parseIntFromString(input.get(1), "target: (\\d+),\\d+");
        int yTarget= AocParseUtils.parseIntFromString(input.get(1), "target: \\d+,(\\d+)");
        final var cave = createCave(yTarget, xTarget, 0, depth);

        return cave.allPositions().stream().mapToInt(p-> cave.at(p) % 3).sum();
    }

    private CharMatrix createRegions(final IntMatrix cave)
    {
        CharMatrix regions=CharMatrix.filledMatrix(cave.rows(), cave.cols(), BLOCKED, BLOCKED);
        cave.allPositions().forEach(p-> regions.set(p, switch(cave.at(p) % 3)
        {
            case 0 -> ROCK;
            case 1 -> WET;
            case 2 -> NARROW;
            default -> BLOCKED;
        }));
        return regions;
    }

    private static IntMatrix createCave(final int yTarget, final int xTarget, final int offset, final int depth)
    {
        IntMatrix cave=new IntMatrix(yTarget + 1 + offset, xTarget + 1 + offset, -1);
        cave.set(0, 0, 0); // rule 1
        for (int col = 1; col < cave.cols(); col++) // rule 3
        {
            cave.set(0, col, (col * 16807 + depth) % MOD);
        }
        for (int row = 1; row < cave.rows(); row++) // rule 4
        {
            cave.set(row, 0, (row* 48271 + depth) % MOD);
        }
        for (int col = 1; col < cave.cols(); col++) // rul 5
        {
            for (int row = 1; row < cave.rows() ; row++)
            {
                if (col==xTarget && row==yTarget)
                {
                    cave.set(yTarget, xTarget, 0); // rule 2
                }
                else
                {
                    int gi = cave.at(row, col - 1) * cave.at(row - 1, col);
                    cave.set(row, col, (gi + depth) % MOD);
                }
            }
        }
        return cave;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<String> input = data.getLines();
        int depth = AocParseUtils.parseIntFromString(input.get(0), "depth: (\\d+)");
        int xTarget = AocParseUtils.parseIntFromString(input.get(1), "target: (\\d+),\\d+");
        int yTarget = AocParseUtils.parseIntFromString(input.get(1), "target: \\d+,(\\d+)");
        // we need moe data than just the rectangle to the target, so let's add some additional offset around the target
        final var cave = createCave(yTarget, xTarget, 100, depth);

        final CharMatrix regions = createRegions(cave);

        final var world = new CaveWorld(regions, new Position(yTarget, xTarget));
        BestFirstSearch<CaveWorld, CaveState> search = new BestFirstSearch<>(world);
        search.search();

        return world._bestTime;
    }

    private static class CaveWorld implements SearchWorld<CaveState>
    {
        private final CharMatrix _regions;
        private final Position _target;
        private int _bestTime =1000000;

        public CaveWorld(final CharMatrix regions, final Position target)
        {
            _regions = regions;
            _target = target;
        }

        @Override
        public CaveState getFirstState()
        {
            return new CaveState(new Position(0,0), TOOL_TORCH, 0);
        }

        @Override
        public List<CaveState> calculateNextStates(final CaveState currentState)
        {
            final List<CaveState> states =new ArrayList<>();
            char currentTool = currentState._tool;
            char currentRegion=_regions.at(currentState._position);
            // find out how we can switch our tool
            switch (currentTool)
            {
                case TOOL_TORCH:
                    if (currentRegion==ROCK)
                        states.add(currentState.changeTool(TOOL_GEAR));
                    if (currentRegion == NARROW)
                        states.add(currentState.changeTool(TOOL_NEITHER));
                    break;
                case TOOL_GEAR:
                    if (currentRegion == ROCK)
                        states.add(currentState.changeTool(TOOL_TORCH));
                    if (currentRegion == WET)
                        states.add(currentState.changeTool(TOOL_NEITHER));
                    break;
                case TOOL_NEITHER:
                    if (currentRegion == WET)
                        states.add(currentState.changeTool(TOOL_GEAR));
                    if (currentRegion == NARROW)
                        states.add(currentState.changeTool(TOOL_TORCH));
                    break;
            }
            // also, check where we can go with the current tool
            for (Direction direction : Direction.values())
            {
                Position to=currentState._position.updated(direction);
                char targetRegion=_regions.at(to);
                if (targetRegion==BLOCKED)
                    continue;
                if (targetRegion==ROCK && currentTool == TOOL_NEITHER)
                    continue;
                if (targetRegion==WET && currentTool == TOOL_TORCH)
                    continue;
                if (targetRegion==NARROW && currentTool == TOOL_GEAR)
                    continue;
                states.add(currentState.moveTo(to));
            }
            return states;
        }

        @Override
        public boolean reachedTarget(final CaveState currentState)
        {
            final var there = currentState._position.equals(_target) && currentState._tool == TOOL_TORCH;
            if (there && currentState._time < _bestTime)
            {
                _bestTime =currentState._time;
            }
            return there;
        }

        @Override
        public boolean canPruneBranch(final CaveState currentState)
        {
            return currentState._time > _bestTime;
        }

        @Override
        public Comparator<CaveState> getComparator()
        {
            return Comparator.comparingInt(CaveState::getTime);
        }
    }

    private record CaveState(Position _position, char _tool, int _time) implements SearchState
        {
            @Override
            public String calculateStateKey()
            {
                return _position.getKey() + "-" + _tool;
            }

            @Override
            public boolean betterThan(final Object otherCost)
            {
                return _time < (Integer) otherCost;
            }

            @Override
            public Object getCurrentCost()
            {
                return _time;
            }

            public CaveState changeTool(final char tool)
            {
                return new CaveState(_position, tool, _time + 7);
            }

            public CaveState moveTo(final Position position)
            {
                return new CaveState(position, _tool, _time + 1);
            }

            public int getTime()
            {
                return _time;
            }
        }
}
