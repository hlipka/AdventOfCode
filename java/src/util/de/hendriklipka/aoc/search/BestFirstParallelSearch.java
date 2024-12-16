package de.hendriklipka.aoc.search;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Implements a best-first search
 * using a given 'world' model and tracking search states
 */
public class BestFirstParallelSearch<W extends SearchWorld<S>,S extends SearchState>
{
    private final W world;
    private final ThreadPoolExecutor executor;

    final PriorityQueue<S> moves;
    Map<String, Object> memoize = new ConcurrentHashMap<>(100000);
    Map<S, S> currentTasks = new ConcurrentHashMap<>();

    public BestFirstParallelSearch(W world)
    {
        this.world=world;
        final var nThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("using "+nThreads+" threads");
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        moves = new PriorityQueue<>(100000, world.getComparator());    }

    public void search()
    {
        moves.add(world.getFirstState());
        while (true)
        {
            S currentState;
            synchronized(moves)
            {
                if (moves.isEmpty())
                {
                    if (currentTasks.isEmpty())
                        break;
                    continue;
                }
                currentState = moves.poll();
            }

            currentTasks.put(currentState,currentState);
            executor.submit(new SearchCallable(currentState));
        }
        executor.shutdown();
    }

    private class SearchCallable implements Runnable
    {
        private final S state;

        public SearchCallable(final S currentState)
        {
            state = currentState;
        }

        @Override
        public void run()
        {
            try
            {
                // when we reach the exit, store the current round if it is better than the best way so far
                if (world.reachedTarget(state))
                    return;
                // prune branch
                if (world.canPruneBranch(state))
                {
                    return;
                }

                // memoize positions which we have seen before, and skip them if this happens
                String key = state.calculateStateKey();
                // this is an atomic set - if it returns some other than null, there was a mapping before
                // we just store the cost here, instead of the full state - saves memory
                Object currentCost = state.getCurrentCost();
                final Object existingCost = memoize.putIfAbsent(key, currentCost);
                if (null != existingCost)
                {
                    if (state.betterThan(existingCost))
                        memoize.put(key, currentCost);
                    else
                        return;
                }
                List<S> newStates = world.calculateNextStates(state);

                synchronized (moves)
                {
                    moves.addAll(newStates);
                }
            }
            finally
            {
                currentTasks.remove(state);
            }

        }
    }
}
