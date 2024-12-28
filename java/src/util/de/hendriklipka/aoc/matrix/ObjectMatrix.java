package de.hendriklipka.aoc.matrix;

import de.hendriklipka.aoc.DiagonalDirections;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiPredicate;

@SuppressWarnings("unchecked")
public class ObjectMatrix<T>
{
    private final T[][] _data;
    private final int _rows;

    private final int _cols;

    public ObjectMatrix(List<List<T>> data)
    {
        _rows = data.size();
        _cols = data.get(0).size();
        _data = (T[][]) new Object[_rows][_cols];
        for (int r = 0; r < _data.length; r++)
        {
            List<T> row = data.get(r);
            for (int c = 0; c < row.size(); c++)
            {
                _data[r][c] = row.get(c);
            }
        }
    }

    public ObjectMatrix(T[][] data)
    {
        _rows = data.length;
        _cols = data[0].length;
        _data = data;
    }

    public ObjectMatrix(int rows, int cols)
    {
        _rows=rows;
        _cols=cols;
        _data = (T[][]) new Object[rows][cols];
    }


    public T at(Position pos)
    {
        return at(pos.row, pos.col);
    }

    public T at(int row, int col)
    {
        if (row < 0 || row >= _rows || col < 0 || col >= _cols)
        {
            return null;
        }

        return _data[row][col];
    }

    public T[] column(int col)
    {
        T[] chars = (T[]) new Object[_data.length];
        for (int i = 0; i < _data.length; i++)
        {
            chars[i] = at(i, col);
        }
        return chars;
    }

    public T[] row(int row)
    {
        return _data[row];
    }

    public void set(Position pos, T c)
    {
        _data[pos.row][pos.col] = c;
    }

    public void set(int row, int col, T c)
    {
        _data[row][col] = c;
    }

    public boolean isSame(ObjectMatrix<T> other)
    {
        for (int r = 0; r < _data.length; r++)
        {
            T[] row = _data[r];
            T[] otherRow = other._data[r];
            for (int c = 0; c < row.length; c++)
            {
                if (row[c] != otherRow[c])
                    return false;
            }
        }
        return true;
    }

    public ObjectMatrix<T> copyOf()
    {
        T[][] copy = (T[][]) new Object[_data.length][_data[0].length];
        for (int r = 0; r < _data.length; r++)
        {
            System.arraycopy(_data[r], 0, copy[r], 0, _data[r].length);
        }
        return new ObjectMatrix<>(copy);
    }

    public boolean moveIfEmpty(Position pos, Direction dir)
    {
        Position newPos = pos.updated(dir);
        if (in(newPos) && at(newPos) == null)
        {
            set(newPos, at(pos));
            set(pos, null);
            return true;
        }
        return false;
    }

    public boolean in(Position pos)
    {
        return pos.row >= 0 && pos.row < _rows && pos.col >= 0 && pos.col < _cols;
    }

    public boolean in(int row, int col)
    {
        return row >= 0 && row < _rows && col >= 0 && col < _cols;
    }

    public void moveWhileEmpty(Position pos, Direction dir)
    {
        while (true)
        {
            Position newPos = pos.updated(dir);
            if (in(newPos) && at(newPos) == null)
            {
                set(newPos, at(pos));
                set(pos, null);
                pos = newPos;
            }
            else
            {
                break;
            }
        }
    }

    public int countInRow(int row, T c)
    {
        int count = 0;
        T[] r = _data[row];
        for (int col = 0; col < _cols; col++)
        {
            if (Objects.equals(r[col], c))
                count++;
        }
        return count;
    }

    public int countInCol(int col, T c)
    {
        int count = 0;
        for (int row = 0; row < _rows; row++)
        {
            if (Objects.equals(_data[row][col], c))
                count++;
        }
        return count;
    }

    public int count(T c)
    {
        int count = 0;
        for (int r=0;r<_rows;r++)
            for (int col = 0; col < _cols; col++)
            {
                if (Objects.equals(_data[r][col], c))
                    count++;
            }
        return count;
    }

    public void print()
    {
        for (T[] row : _data)
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

        return isSame((ObjectMatrix<T>) o);
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

    public Position findFirst(T c)
    {
        for (int r = 0; r < _rows; r++)
            for (int col = 0; col < _cols; col++)
            {
                if(Objects.equals(_data[r][col],c))
                    return new Position(r,col);
            }
        return null;
    }

    public T[] getInDirection(final Position start, final DiagonalDirections direction, final int len)
    {
        final T[] result = (T[]) new Object[len];
        Position current = start;
        for (int i=0;i<len;i++)
        {
            result[i]=at(current);
            current = current.updated(direction);
        }
        return result;
    }

    public T[] getInDirection(final Position start, final Direction direction, final int len)
    {
        final T[] result = (T[]) new Object[len];
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

    public List<Position> allMatchingPositions(T x)
    {
        final List<Position> positions = new ArrayList<>(_rows * _cols);
        for (int row = 0; row < rows(); row++)
        {
            for (int col = 0; col < cols(); col++)
            {
                    if (Objects.equals(_data[row][col], x))
                {
                    positions.add(new Position(row, col));
                }
            }
        }
        return positions;
    }

    public Set<Position> floodFill(Position start, BiPredicate<Position, Position> canMove)
    {
        final Set<Position> positions = new HashSet<>(_rows * _cols);
        final List<Position> toVisit = new ArrayList<>();
        toVisit.add(start);
        while (!toVisit.isEmpty())
        {
            Position currentPosition = toVisit.remove(0);
            positions.add(currentPosition);
            for (Direction d: Direction.values())
            {
                Position nextPos=currentPosition.updated(d);
                if (canMove.test(currentPosition, nextPos))
                    toVisit.add(nextPos);
            }
        }
        return positions;
    }
}
