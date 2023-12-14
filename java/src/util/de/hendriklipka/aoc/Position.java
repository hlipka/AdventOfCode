package de.hendriklipka.aoc;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

        return new EqualsBuilder().append(row, pos.row).append(col, pos.col).isEquals();
    }

    public Position updated(int rowDiff, int colDiff)
    {
        return new Position(row + rowDiff, col + colDiff);
    }

    public Position updated(Direction dir)
    {
        switch (dir)
        {
            case UP ->
            {
                return new Position(row - 1, col);
            }
            case DOWN ->
            {
                return new Position(row + 1, col);
            }
            case LEFT ->
            {
                return new Position(row, col - 1);
            }
            case RIGHT ->
            {
                return new Position(row, col + 1);
            }
        }
        return null;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(row).append(col).toHashCode();
    }

    @Override
    public String toString()
    {
        return "Pos{" +
               "row=" + row +
               ", col=" + col +
               '}';
    }
}
