package de.hendriklipka.aoc;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hli
 * Date: 25.12.22
 * Time: 18:36
 */
public class Position implements Keyable
{
    public int row, col;

    public Position(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Position pos = (Position) o;

        return row==pos.row&&col==pos.col;
    }

    public Position updated(int rowDiff, int colDiff)
    {
        return new Position(row + rowDiff, col + colDiff);
    }

    public Position updated(Direction dir)
    {
        return updated(dir, 1);
    }

    public Position updated(DiagonalDirections dir)
    {
        return updated(dir, 1);
    }

    public Position updated(Direction dir, int count)
    {
        switch (dir)
        {
            case UP ->
            {
                return new Position(row - count, col);
            }
            case DOWN ->
            {
                return new Position(row + count, col);
            }
            case LEFT ->
            {
                return new Position(row, col - count);
            }
            case RIGHT ->
            {
                return new Position(row, col + count);
            }
        }
        return null;
    }

    public Position updated(DiagonalDirections dir, int count)
    {
        switch (dir)
        {
            case UP ->
            {
                return new Position(row - count, col);
            }
            case DOWN ->
            {
                return new Position(row + count, col);
            }
            case LEFT ->
            {
                return new Position(row, col - count);
            }
            case RIGHT ->
            {
                return new Position(row, col + count);
            }
            case RIGHT_UP ->
            {
                return new Position(row - count, col + count);
            }
            case RIGHT_DOWN ->
            {
                return new Position(row + count, col + count);
            }
            case LEFT_UP ->
            {
                return new Position(row - count, col - count);
            }
            case LEFT_DOWN ->
            {
                return new Position(row + count, col - count);
            }
        }
        return null;
    }

    @Override
    public int hashCode()
    {
        // that way we can use the hash code as key directly for smaller coordinated (up to 10000)
        return 32749*row+3*col;
    }

    @Override
    public String toString()
    {
        return "Pos{" +
               "row=" + row +
               ", col=" + col +
               '}';
    }

    public int dist(Position pos)
    {
        return Math.abs(pos.row-row)+Math.abs(pos.col-col);
    }

    @Override
    public String getKey()
    {
        return row+","+col;
    }

    /**
     * Gets all positions, relative to the position, which are exactly at the provided
     * manhattan distance (no closer positions are included). No guarantee abut the order is given.
     *
     * @param dist the manhattan distance
     * @return list of all positions at this distance
     */
    public List<Position> getWithinDistance(final int dist)
    {
        final List<Position> result = new ArrayList<>();
        for (int x=0;x<dist;x++)
        {
            result.add(updated(x, dist-x));
            result.add(updated(-x, -(dist-x)));
            result.add(updated(-(dist-x), x));
            result.add(updated(dist-x, -x));
        }
        return result;
    }

    // for a hex grid, each column has only every second row set - the other rows are then in the next column
    public Position updated(HexDirection direction)
    {
        return switch (direction)
        {
            case N -> new Position(row - 2, col);
            case S -> new Position(row + 2, col);
            case NE -> new Position(row - 1, col + 1);
            case NW -> new Position(row - 1, col - 1);
            case SE -> new Position(row + 1, col + 1);
            case SW -> new Position(row + 1, col - 1);
        };
    }
}
