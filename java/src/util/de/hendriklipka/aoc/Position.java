package de.hendriklipka.aoc;

/**
 * User: hli
 * Date: 25.12.22
 * Time: 18:36
 */
public class Position
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
}
