package de.hendriklipka.aoc.search;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements a depth-first search
 * using a given 'world' model and tracking search states
 */
public class DepthFirstSearchNoState<W extends SearchWorld<S>,S extends SearchState>
{
    private final W world;

    private Comparator<S> comparator;

    public DepthFirstSearchNoState(W world)
    {
        this.world=world;
    }

    public void search()
    {
        S state = world.getFirstState();
        comparator = world.getComparator();
        doSearch(state, world);
    }

    private void doSearch(S currentState, W world)
    {
        // when we reach the exit, store the current round if it is better than the best way so far
        if (world.reachedTarget(currentState))
        {
            return;
        }
        // prune branch
        if (world.canPruneBranch(currentState))
        {
            return;
        }
        List<S> newStates = world.calculateNextStates(currentState);
        if (newStates.size()>1 && null!=comparator)
        {
            newStates.sort(comparator);
        }
        for (S nextState: newStates)
        {
            doSearch(nextState, world);
        }
    }
}
