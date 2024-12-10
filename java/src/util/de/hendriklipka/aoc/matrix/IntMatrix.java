package de.hendriklipka.aoc.matrix;

import de.hendriklipka.aoc.Position;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * User: hli
 * Date: 14.12.23
 * Time: 18:46
 */
public class IntMatrix
{
    int[][] _data;
    int _defaultInt;
    int _rows;
    int _cols;
    public IntMatrix(List<List<Integer>> data, int defaultInt)
    {
        _rows = data.size();
        _cols = data.get(0).size();
        _data = new int[_rows][_cols];
        for (int r = 0; r < _data.length; r++)
        {
            List<Integer> row = data.get(r);
            for (int c = 0; c < row.size(); c++)
            {
                _data[r][c] = row.get(c);
            }
        }
        _defaultInt = defaultInt;
    }

    private IntMatrix(int[][] data, int defaultValue)
    {
        _rows = data.length;
        _cols = data[0].length;
        _data = data;
        _defaultInt = defaultValue;
    }

    public IntMatrix(int rows, int cols, int defaultValue)
    {
        _rows = rows;
        _cols = cols;
        _data = new int[_rows][_cols];
        for (int r = 0; r < _data.length; r++)
        {
            for (int c = 0; c < cols; c++)
            {
                _data[r][c] = defaultValue;
            }
        }
        _defaultInt = defaultValue;
    }

    public int at(Position pos)
    {
        return at(pos.row, pos.col);
    }

    public int at(int row, int col)
    {
        if (row < 0 || row >= _rows || col < 0 || col >= _cols)
        {
            return _defaultInt;
        }

        return _data[row][col];
    }

    public int[] column(int col)
    {
        int[] ints = new int[_data.length];
        for (int i = 0; i < _data.length; i++)
        {
            ints[i] = at(i, col);
        }
        return ints;
    }

    public int[] row(int row)
    {
        return _data[row];
    }

    public void set(Position pos, int i)
    {
        _data[pos.row][pos.col] = i;
    }

    public boolean isSame(IntMatrix other)
    {
        for (int r = 0; r < _data.length; r++)
        {
            int[] row = _data[r];
            int[] otherRow = other._data[r];
            for (int c = 0; c < row.length; c++)
            {
                if (row[c] != otherRow[c])
                    return false;
            }
        }
        return true;
    }

    public IntMatrix copyOf()
    {
        int[][] copy = new int[_data.length][_data[0].length];
        for (int r = 0; r < _data.length; r++)
        {
            int[] row = _data[r];
            System.arraycopy(row, 0, copy[r], 0, row.length);
        }
        return new IntMatrix(copy, _defaultInt);
    }

    public boolean in(Position pos)
    {
        return pos.row >= 0 && pos.row < _rows && pos.col >= 0 && pos.col < _cols;
    }

    public int countInRow(int row, int i)
    {
        int count = 0;
        int[] r = _data[row];
        for (int col = 0; col < _cols; col++)
        {
            if (r[col] == i)
                count++;
        }
        return count;
    }

    public int countInCol(int col, int i)
    {
        int count = 0;
        for (int row = 0; row < _rows; col++)
        {
            if (_data[row][col] == i)
                count++;
        }
        return count;
    }

    public int count(int i)
    {
        int count = 0;
        for (int r = 0; r < _rows; r++)
            for (int col = 0; col < _cols; col++)
            {
                if (_data[r][col] == i)
                    count++;
            }
        return count;
    }

    public int count(Predicate<Integer> condition)
    {
        int count = 0;
        for (int r = 0; r < _rows; r++)
            for (int col = 0; col < _cols; col++)
            {
                if (condition.test(_data[r][col]))
                    count++;
            }
        return count;
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

    public List<Position> allMatchingPositions(int x)
    {
        final List<Position> positions = new ArrayList<>(_rows * _cols);
        for (int row = 0; row < rows(); row++)
        {
            for (int col = 0; col < cols(); col++)
            {
                if (_data[row][col] == x)
                {
                    positions.add(new Position(row, col));
                }
            }
        }
        return positions;
    }
    public void print()
    {
        for (int[] row : _data)
        {
            System.out.println(StringUtils.join(row, ';'));
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

        return isSame((IntMatrix) o);
    }

    @Override
    public int hashCode()
    {
        StringBuilder sb=new StringBuilder();
        for (int r=0;r<_rows;r++)
            sb.append(StringUtils.join(_data[r],",")).append(",");

        return sb.toString().hashCode();
    }

    public void add(IntMatrix other)
    {
        for (int r = 0; r < _rows; r++)
            for (int col = 0; col < _cols; col++)
                _data[r][col]=_data[r][col]+other._data[r][col];
    }
}
