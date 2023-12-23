package de.hendriklipka.aoc.search;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements a depth-first search
 * using a given 'world' model and tracking search states
 */
public class DepthFirstSearch<W extends SearchWorld<S>,S extends SearchState>
{
    private final W world;

    ConcurrentHashMap<String, S> memoize = new ConcurrentHashMap<>(100000);
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
        // memoize positions which we have seen before, and skip them if this happens
        String key = currentState.calculateStateKey();
        // this is an atomic set - if it returns some other than null, there was a mapping before
        final S other = memoize.putIfAbsent(key, currentState);
        if (null != other)
        {
            if(currentState.betterThan(other))
                memoize.put(key, currentState);
            else
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
