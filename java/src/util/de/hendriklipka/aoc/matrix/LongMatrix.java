package de.hendriklipka.aoc.matrix;

import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class LongMatrix
{
    long[][] _data;
    long _defaultInt;
    int _rows;
    int _cols;
    public LongMatrix(List<List<Integer>> data, long defaultInt)
    {
        _rows = data.size();
        _cols = data.get(0).size();
        _data = new long[_rows][_cols];
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

    private LongMatrix(long[][] data, long defaultValue)
    {
        _rows = data.length;
        _cols = data[0].length;
        _data = data;
        _defaultInt = defaultValue;
    }

    public LongMatrix(int rows, int cols, long defaultValue)
    {
        _rows = rows;
        _cols = cols;
        _data = new long[_rows][_cols];
        for (int r = 0; r < _data.length; r++)
        {
            for (int c = 0; c < cols; c++)
            {
                _data[r][c] = defaultValue;
            }
        }
        _defaultInt = defaultValue;
    }

    public static LongMatrix filledMatrix(int rows, int cols, long fillChar, long defaultChar)
    {
        final long[][] data = new long[rows][cols];
        for (long[] currentRow : data)
        {
            Arrays.fill(currentRow, fillChar);
        }
        return new LongMatrix(data, defaultChar);
    }

    public long at(Position pos)
    {
        return at(pos.row, pos.col);
    }

    public long at(int row, int col)
    {
        if (row < 0 || row >= _rows || col < 0 || col >= _cols)
        {
            return _defaultInt;
        }

        return _data[row][col];
    }

    public long[] column(int col)
    {
        long[] ints = new long[_data.length];
        for (int i = 0; i < _data.length; i++)
        {
            ints[i] = at(i, col);
        }
        return ints;
    }

    public long[] row(int row)
    {
        return _data[row];
    }

    public void set(Position pos, long i)
    {
        _data[pos.row][pos.col] = i;
    }

    public void set(int row, int col, long i)
    {
        _data[row][col] = i;
    }

    public boolean isSame(LongMatrix other)
    {
        for (int r = 0; r < _data.length; r++)
        {
            long[] row = _data[r];
            long[] otherRow = other._data[r];
            for (int c = 0; c < row.length; c++)
            {
                if (row[c] != otherRow[c])
                    return false;
            }
        }
        return true;
    }

    public LongMatrix copyOf()
    {
        long[][] copy = new long[_data.length][_data[0].length];
        for (int r = 0; r < _data.length; r++)
        {
            long[] row = _data[r];
            System.arraycopy(row, 0, copy[r], 0, row.length);
        }
        return new LongMatrix(copy, _defaultInt);
    }

    public boolean in(Position pos)
    {
        return pos.row >= 0 && pos.row < _rows && pos.col >= 0 && pos.col < _cols;
    }

    public int countInRow(int row, long i)
    {
        int count = 0;
        long[] r = _data[row];
        for (int col = 0; col < _cols; col++)
        {
            if (r[col] == i)
                count++;
        }
        return count;
    }

    public int countInCol(int col, long i)
    {
        int count = 0;
        for (int row = 0; row < _rows; col++)
        {
            if (_data[row][col] == i)
                count++;
        }
        return count;
    }

    public int count(long i)
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

    public int count(Predicate<Long> condition)
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

    public List<Position> allMatchingPositions(long x)
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
        for (long[] row : _data)
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

        return isSame((LongMatrix) o);
    }

    @Override
    public int hashCode()
    {
        StringBuilder sb=new StringBuilder();
        for (int r=0;r<_rows;r++)
            sb.append(StringUtils.join(_data[r],",")).append(",");

        return sb.toString().hashCode();
    }

    public void add(LongMatrix other)
    {
        for (int r = 0; r < _rows; r++)
            for (int col = 0; col < _cols; col++)
                _data[r][col]=_data[r][col]+other._data[r][col];
    }

    /**
     * Flood-fills the matrix starting at the given position, to find all reachable places.
     *
     * @param start where to start the fill
     * @param canMove predicate whether a move (from one position to another) is allowed
     * @return set of positions that are reachable from the start position
     */
    public Set<Position> floodFill(Position start, BiPredicate<Position, Position> canMove)
    {
        final Set<Position> positions = new HashSet<>(_rows * _cols);
        final List<Position> toVisit = new ArrayList<>();
        toVisit.add(start);
        while (!toVisit.isEmpty())
        {
            Position currentPosition = toVisit.remove(0);
            positions.add(currentPosition);
            for (Direction d : Direction.values())
            {
                Position nextPos = currentPosition.updated(d);
                if (canMove.test(currentPosition, nextPos))
                    toVisit.add(nextPos);
            }
        }
        return positions;
    }
}
