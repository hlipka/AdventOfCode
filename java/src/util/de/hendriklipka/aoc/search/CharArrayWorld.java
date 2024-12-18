package de.hendriklipka.aoc.search;

import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;

public class CharArrayWorld implements ArrayWorld
{
    private final CharMatrix grid;
    private final Position _start;
    private final Position _end;
    private final char _wallChar;

    public CharArrayWorld(final CharMatrix grid, Position start, Position end, final char wallChar)
    {
        this.grid = grid;
        _start = start;
        _end = end;
        _wallChar = wallChar;
    }

    @Override
    public int getWidth()
    {
        return grid.cols();
    }

    @Override
    public int getHeight()
    {
        return grid.rows();
    }

    @Override
    public int getStartX()
    {
        return _start.col;
    }

    @Override
    public int getStartY()
    {
        return _start.row;
    }

    @Override
    public int getEndX()
    {
        return _end.col;
    }

    @Override
    public int getEndY()
    {
        return _end.row;
    }

    @Override
    public boolean canMoveTo(final int oldX, final int oldY, final int x, final int y)
    {
        return grid.at(y, x) != _wallChar;
    }
}
