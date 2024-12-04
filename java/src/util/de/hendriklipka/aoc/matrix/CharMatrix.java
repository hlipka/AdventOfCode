package de.hendriklipka.aoc.matrix;

import de.hendriklipka.aoc.DiagonalDirections;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharMatrix
{
    private final char[][] _data;
    private final char _defaultChar;
    private final int _rows;

    private final int _cols;

    public CharMatrix(List<List<Character>> data, char defaultChar)
    {
        _rows = data.size();
        _cols = data.get(0).size();
        _data = new char[_rows][_cols];
        for (int r = 0; r < _data.length; r++)
        {
            List<Character> row = data.get(r);
            for (int c = 0; c < row.size(); c++)
            {
                _data[r][c] = row.get(c);
            }
        }
        _defaultChar = defaultChar;
    }

    private CharMatrix(char[][] data, char defaultChar)
    {
        _rows = data.length;
        _cols = data[0].length;
        _data = data;
        _defaultChar = defaultChar;
    }

    public char at(Position pos)
    {
        return at(pos.row, pos.col);
    }

    public char at(int row, int col)
    {
        if (row < 0 || row >= _rows || col < 0 || col >= _cols)
        {
            return _defaultChar;
        }

        return _data[row][col];
    }

    public char[] column(int col)
    {
        char[] chars = new char[_data.length];
        for (int i = 0; i < _data.length; i++)
        {
            chars[i] = at(i, col);
        }
        return chars;
    }

    public char[] row(int row)
    {
        return _data[row];
    }

    public void set(Position pos, char c)
    {
        _data[pos.row][pos.col] = c;
    }

    public boolean isSame(CharMatrix other)
    {
        for (int r = 0; r < _data.length; r++)
        {
            char[] row = _data[r];
            char[] otherRow = other._data[r];
            for (int c = 0; c < row.length; c++)
            {
                if (row[c] != otherRow[c])
                    return false;
            }
        }
        return true;
    }

    public CharMatrix copyOf()
    {
        char[][] copy = new char[_data.length][_data[0].length];
        for (int r = 0; r < _data.length; r++)
        {
            char[] row = _data[r];
            System.arraycopy(row, 0, copy[r], 0, row.length);
        }
        return new CharMatrix(copy, _defaultChar);
    }

    public boolean moveIfEmpty(Position pos, Direction dir)
    {
        Position newPos = pos.updated(dir);
        if (in(newPos) && at(newPos) == _defaultChar)
        {
            set(newPos, at(pos));
            set(pos, _defaultChar);
            return true;
        }
        return false;
    }

    public boolean in(Position pos)
    {
        return pos.row >= 0 && pos.row < _rows && pos.col >= 0 && pos.col < _cols;
    }

    public void moveWhileEmpty(Position pos, Direction dir)
    {
        while (true)
        {
            Position newPos = pos.updated(dir);
            if (in(newPos) && at(newPos) == _defaultChar)
            {
                set(newPos, at(pos));
                set(pos, _defaultChar);
                pos = newPos;
            }
            else
            {
                break;
            }
        }
    }

    public int countInRow(int row, char c)
    {
        int count = 0;
        char[] r = _data[row];
        for (int col = 0; col < _cols; col++)
        {
            if (r[col] == c)
                count++;
        }
        return count;
    }

    public int countInCol(int col, char c)
    {
        int count = 0;
        for (int row = 0; row < _rows; col++)
        {
            if (_data[row][col] == c)
                count++;
        }
        return count;
    }

    public int count(char c)
    {
        int count = 0;
        for (int r=0;r<_rows;r++)
            for (int col = 0; col < _cols; col++)
            {
                if (_data[r][col] == c)
                    count++;
            }
        return count;
    }

    public void print()
    {
        for (char[] row : _data)
        {
            System.out.println(StringUtils.join(row, ' '));
        }
        System.out.println("----");
    }

    public int rows()
    {
        return _rows;
    }

    public int cols()
    {
        return _cols;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        return isSame((CharMatrix) o);
    }

    @Override
    public int hashCode()
    {
        StringBuilder sb=new StringBuilder();
        for (int r=0;r<_rows;r++)
            for (int col = 0; col < _cols; col++)
            {
                sb.append(_data[r][col]);
            }

        return sb.toString().hashCode();
    }

    public Position findFirst(char c)
    {
        for (int r = 0; r < _rows; r++)
            for (int col = 0; col < _cols; col++)
            {
                if(_data[r][col]==c)
                    return new Position(r,col);
            }
        return null;
    }

    public char[] getInDirection(final Position start, final DiagonalDirections direction, final int len)
    {
        final char[] result = new char[len];
        Position current = start;
        for (int i=0;i<len;i++)
        {
            result[i]=at(current);
            current = current.updated(direction);
        }
        return result;
    }

    public char[] getInDirection(final Position start, final Direction direction, final int len)
    {
        final char[] result = new char[len];
        Position current = start;
        for (int i=0;i<len;i++)
        {
            result[i]=at(current);
            current = current.updated(direction);
        }
        return result;
    }

    public List<Position> allPositions()
    {
        final List<Position> positions = new ArrayList<>(_rows * _cols);
        for (int row = 0; row < rows(); row++)
        {
            for (int col = 0; col < cols(); col++)
            {
                positions.add(new Position(row, col));
            }
        }
        return positions;
    }
}
