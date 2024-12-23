package de.hendriklipka.aoc.search;

/**
 * Base interface to track the current search state
 */
public interface SearchState
{
    /**
     * @return a key used to check duplicates states (which can be pruned)
     */
    String calculateStateKey();

    /**
     * Determine whether the current state has a better score than the other one. This is needed when a state is re-visited - it only can be ignored
     * when the current state is worse than the existing one.
     *
     * @param otherCost the cost of the already visited state
     * @return true when the current state is better
     */
    default boolean betterThan(Object otherCost)
    {
        return false;
    }

    /**
     * @return the cost value of the state (used for betterThan() comparison)
     */
    default Object getCurrentCost()
    {
        return null;
    }
}
