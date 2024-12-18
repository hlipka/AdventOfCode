package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;

import java.io.IOException;
import java.util.*;

public class Day16 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day16().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        CharMatrix forest = data.getLinesAsCharMatrix('#');
        ForestWorld world = new ForestWorld(forest, forest.findFirst('S'), forest.findFirst('E'));
        BestFirstSearch<ForestWorld, ForestState> search = new BestFirstSearch<>(world);
        search.search();
        return world.bestScore;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CharMatrix forest = data.getLinesAsCharMatrix('#');
        ForestWorld world = new ForestWorld(forest, forest.findFirst('S'), forest.findFirst('E'));
        BestFirstSearch<ForestWorld, ForestState> search = new BestFirstSearch<>(world);
        search.search();
        int bestScore= world.bestScore;

        // once we know the best score, we search again, but this time enforce looking at all paths
        // we prune by the known best score so only the exact best paths should finish
        // the prune function in 'ForestWorld' will keep all paths which are as good as the current best path
        // (instead of insisting on being better) so we finish all best path until the end (and collect them there)
        // potentially we can even skip this second search, and filter all the collected paths by the ones with the best score
        forest = data.getLinesAsCharMatrix('#');
        world = new ForestWorld(forest, forest.findFirst('S'), forest.findFirst('E'));
        world.bestScore = bestScore;
        BestFirstSearch<ForestWorld, ForestState> search2 = new BestFirstSearch<>(world);
        search2.search();
        // go through all the best paths, and collect unique positions
        Set<Position> paths = new HashSet<>();
        for (ForestState state : world.bestPaths)
        {
            ForestState currentState=state;
            while (currentState!=null)
            {
                paths.add(currentState.pos);
                currentState = currentState.previous;
            }
        }
        return paths.size();
    }

    private static class ForestWorld implements SearchWorld<ForestState>
    {
        private final CharMatrix _forest;
        Position start;
        Position end;
        int bestScore=Integer.MAX_VALUE;
        private List<ForestState> bestPaths = new ArrayList<>();

        public ForestWorld(final CharMatrix forest, final Position start, final Position end)
        {
            _forest = forest;
            _forest.set(end,'.');
            _forest.set(start,'.');
            this.start = start;
            this.end = end;
        }

        @Override
        public ForestState getFirstState()
        {
            return new ForestState(start, Direction.RIGHT);
        }

        @Override
        public List<ForestState> calculateNextStates(final ForestState currentState)
        {
            final List<ForestState> states = new ArrayList<>();
            ForestState moved=currentState.move();
            if (_forest.at(moved.pos)=='.')
                states.add(moved);
            // for the turns - check whether we could go into the new direction
            // otherwise skip it - turning twice does not make sense
            final var left = currentState.left();
            if (_forest.at(left.pos.updated(left.dir))=='.')
                states.add(left);
            final var right = currentState.right();
            if (_forest.at(right.pos.updated(right.dir)) == '.')
                states.add(right);
            return states;
        }

        @Override
        public boolean reachedTarget(final ForestState currentState)
        {
            final var done = currentState.pos.equals(end);
            if (done)
            {
                if(currentState.score<bestScore)
                {
                    bestScore=currentState.score;
                }
                else if (currentState.score==bestScore)
                    bestPaths.add(currentState);
            }
            return done;
        }

        @Override
        public boolean canPruneBranch(final ForestState currentState)
        {
            return currentState.score>bestScore;
        }

        @Override
        public Comparator<ForestState> getComparator()
        {
            return Comparator.comparingInt(ForestState::getScore);
        }
    }

    private static class ForestState implements SearchState
    {
        private int score=0;
        private final Position pos;
        private final Direction dir;
        private ForestState previous=null;

        public ForestState(final Position pos, final Direction dir)
        {
            this.pos = pos;
            this.dir=dir;
        }

        @Override
        public String calculateStateKey()
        {
            return pos.row+","+pos.col+","+dir.ordinal();
        }

        @Override
        public boolean betterThan(final Object otherCost)
        {
            return score<=(Integer)otherCost;
        }

        @Override
        public Object getCurrentCost()
        {
            return score;
        }

        public int getScore()
        {
            return score;
        }

        public ForestState move()
        {
            ForestState newState = new ForestState(pos.updated(dir), dir);
            newState.score=score+1;
            newState.previous=this;
            return newState;
        }

        public ForestState left()
        {
            ForestState newState = new ForestState(pos, dir.left());
            newState.score = score+1000;
            newState.previous=this;
            return newState;
        }

        public ForestState right()
        {
            ForestState newState = new ForestState(pos, dir.right());
            newState.score = score+1000;
            newState.previous=this;
            return newState;
        }
    }
}
