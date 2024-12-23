package de.hendriklipka.aoc.search;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Implements a depth-first search
 * using a given 'world' model and tracking search states
 */
public class DepthFirstSearch<W extends SearchWorld<S>,S extends SearchState>
{
    private final W world;

    Map<String, Object> memoize = new HashMap<>(100000);
    private Comparator<S> comparator;

    public DepthFirstSearch(W world)
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
        // memoize positions which we have seen before, and skip them if the other one was better
        String key = currentState.calculateStateKey();
        Object currentCost = currentState.getCurrentCost();
        // this is an atomic set - if it returns some other than null, there was a mapping before
        final Object existingCost = memoize.putIfAbsent(key, currentCost);
        if (null != existingCost)
        {
            if(currentState.betterThan(existingCost))
            {
                memoize.put(key, currentCost);
            }
            else
            {
                return;
            }
        }
        List<S> newStates = world.calculateNextStates(currentState);
        // if we can sort next states by preference
        if (null!=comparator && newStates.size()>1)
        {
            newStates.sort(comparator);
        }
        for (S nextState: newStates)
        {
            doSearch(nextState, world);
        }
    }
}
