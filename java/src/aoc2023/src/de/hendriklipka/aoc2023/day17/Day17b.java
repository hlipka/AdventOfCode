package de.hendriklipka.aoc2023.day17;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.IntMatrix;
import de.hendriklipka.aoc.search.DepthFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * User: hli
 * Date: 17.12.23
 * Time: 12:26
 */
public class Day17b
{
    public static void main(String[] args)
    {
        try
        {
            IntMatrix map = AocDataFileUtils.getLinesAsIntMatrix("2023", "day17", -1);
            LavaSearchWorld world1 = new LavaSearchWorld(map, Direction.RIGHT);
            DepthFirstSearch<LavaSearchWorld, LavaSearchState> dfs1=new DepthFirstSearch<>(world1);
            dfs1.search();
            System.out.println(world1.bestCost);

            final LavaSearchWorld world2 = new LavaSearchWorld(map, Direction.DOWN);
            DepthFirstSearch<LavaSearchWorld, LavaSearchState> dfs2=new DepthFirstSearch<>(world2);
            dfs2.search();
            System.out.println(world2.bestCost);
            System.out.println("best="+Math.min(world1.bestCost,world2.bestCost));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    static class LavaSearchWorld implements SearchWorld<LavaSearchState>
    {
        private final Direction firstDir;
        IntMatrix map;
        Position target;

        int bestCost=Integer.MAX_VALUE;

        LavaSearchWorld(IntMatrix map, Direction firstDir)
        {
            this.map=map;
            target=new Position(map.rows()-1, map.cols()-1);
            this.firstDir=firstDir;
        }

        @Override
        public LavaSearchState getFirstState()
        {
            return new LavaSearchState(new Position(0,0),firstDir, 0,0);
        }

        @Override
        public List<LavaSearchState> calculateNextStates(LavaSearchState currentState)
        {
            List<LavaSearchState> nextStates = new ArrayList<>();
            if (currentState.dirCount<9)
            {
                addState(nextStates, currentState.pos.updated(currentState.dir), currentState.dir, currentState.dirCount+1, currentState.cost);
            }
            if (currentState.dirCount > 2)
                switch(currentState.dir)
            {
                case UP ->
                {
                    addState(nextStates, currentState.pos.updated(Direction.LEFT), Direction.LEFT, 0, currentState.cost);
                    addState(nextStates, currentState.pos.updated(Direction.RIGHT), Direction.RIGHT, 0, currentState.cost);
                }
                case DOWN ->
                {
                    addState(nextStates, currentState.pos.updated(Direction.LEFT), Direction.LEFT, 0, currentState.cost);
                    addState(nextStates, currentState.pos.updated(Direction.RIGHT), Direction.RIGHT, 0, currentState.cost);
                }
                case LEFT ->
                {
                    addState(nextStates, currentState.pos.updated(Direction.UP), Direction.UP, 0, currentState.cost);
                    addState(nextStates, currentState.pos.updated(Direction.DOWN), Direction.DOWN, 0, currentState.cost);
                }
                case RIGHT ->
                {
                    addState(nextStates, currentState.pos.updated(Direction.UP), Direction.UP, 0, currentState.cost);
                    addState(nextStates, currentState.pos.updated(Direction.DOWN), Direction.DOWN, 0, currentState.cost);
                }
            }
            return nextStates;
        }

        private void addState(List<LavaSearchState> nextStates, Position position, Direction dir, int count, int cost)
        {
            if (map.in(position))
            {
                nextStates.add(new LavaSearchState(position, dir, count, cost+map.at(position)));
            }
        }

        @Override
        public boolean reachedTarget(LavaSearchState currentState)
        {
            if (currentState.pos.equals(target) && currentState.dirCount > 2)
            {
                bestCost=Math.min(bestCost, currentState.cost);
                return true;
            }
            return false;
        }

        @Override
        public boolean canPruneBranch(LavaSearchState currentState)
        {
            return currentState.cost+currentState.pos.dist(target)>bestCost;
        }

        @Override
        public Comparator<LavaSearchState> getComparator()
        {
            return (o1, o2) ->
            {
                int d1=target.dist(o1.pos);
                int d2=target.dist(o2.pos);
                return Integer.compare(d1,d2);
            };
        }
    }

    static class LavaSearchState implements SearchState
    {
        int cost;
        Position pos;
        Direction dir;
        int dirCount;

        public LavaSearchState(Position pos, Direction dir, int dirCount, int cost)
        {
            this.pos = pos;
            this.dir = dir;
            this.dirCount = dirCount;
            this.cost=cost;
        }

        @Override
        public String calculateStateKey()
        {
            String sb = pos.row + "-" +
                        pos.col + "-" +
                        dir.ordinal() + "-" +
                        dirCount;
            return sb;
        }

        @Override
        public boolean betterThan(Object other)
        {
            // compare accumulated heat loss to allow re-visiting with a better cost
            return cost<(Integer)other;
        }

        @Override
        public Object getCurrentCost()
        {
            return cost;
        }

        @Override
        public String toString()
        {
            return "LavaSearchState{" +
                   "cost=" + cost +
                   ", pos=" + pos +
                   ", dir=" + dir +
                   ", dirCount=" + dirCount +
                   '}';
        }
    }
}
