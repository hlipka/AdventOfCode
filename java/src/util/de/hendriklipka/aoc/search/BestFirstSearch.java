package de.hendriklipka.aoc.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Implements a best-first search
 * using a given 'world' model and tracking search states
 */
public class BestFirstSearch<W extends SearchWorld<S>,S extends SearchState>
{
    private final W world;

    public BestFirstSearch(W world)
    {
        this.world=world;
    }

    public void search()
    {
        PriorityQueue<S> moves = new PriorityQueue<>(100000, world.getComparator());
        Map<String, Object> memoize = new HashMap<>(100000);

        moves.add(world.getFirstState());
        while (!moves.isEmpty())
        {
            S currentState = moves.poll();

            // when we reach the exit, store the current round if it is better than the best way so far
            if (world.reachedTarget(currentState))
                continue;
            // prune branch
            if (world.canPruneBranch(currentState))
            {
                continue;
            }

            // memoize positions which we have seen before, and skip them if this happens
            String key = currentState.calculateStateKey();
            // this is an atomic set - if it returns some other than null, there was a mapping before
            // we just store the cost here, instead of the full state - saves memory
            Object currentCost = currentState.getCurrentCost();
            final Object existingCost = memoize.putIfAbsent(key, currentCost);
            if (null != existingCost)
            {
                if (currentState.betterThan(existingCost))
                    memoize.put(key, currentCost);
                else
                    continue;
            }
            List<S> newStates = world.calculateNextStates(currentState);

            moves.addAll(newStates);
        }
    }

}
