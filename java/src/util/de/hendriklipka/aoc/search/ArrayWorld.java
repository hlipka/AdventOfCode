package de.hendriklipka.aoc.search;

/**
 * Abstracts an array-based world for doing a A*-search.
 */
public interface ArrayWorld
{
    /**
     * @return width of the world
     */
    int getWidth();

    /**
     * @return height of the world
     */
    int getHeight();

    /**
     * @return where to start, x-coordinate
     */
    int getStartX();

    /**
     * @return where to start, y-coordinate
     */
    int getStartY();

    /**
     * @return where the target is, x-coordinate
     */
    int getEndX();

    /**
     * @return where the target is, y-coordinate
     */
    int getEndY();

    /**
     * whether we are allowed to move from the old position to the new one
     * @param oldX old X
     * @param oldY old Y
     * @param x new X
     * @param y new Y
     * @return is the move allowed?
     */
    boolean canMoveTo(int oldX, int oldY, int x, int y);

    /**
     * Returns the projected cost to get from the provided position to the end. Lower position means better.
     * The default implementation uses manhattan distance
     *
     * @param x x of the position
     * @param y y of the position
     * @return cost (lower is better)
     */
    default int remainingCost(int x, int y)
    {
        return Math.abs(getEndX() - x) + Math.abs(getEndY() - y);
    }
}
