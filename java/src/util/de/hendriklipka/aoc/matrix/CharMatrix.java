package de.hendriklipka.aoc.matrix;

import de.hendriklipka.aoc.DiagonalDirections;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiPredicate;

public class CharMatrix
{
    private final char[][] _data;
    private final char _defaultChar;
    private final int _rows;

    private final int _cols;

    public CharMatrix(List<List<Character>> data, char defaultChar)
    {
        _rows = data.size();
        _cols = data.stream().mapToInt(List::size).max().orElse(0);
        _data = new char[_rows][_cols];
        for (int r = 0; r < _data.length; r++)
        {
            List<Character> row = data.get(r);
            for (int c = 0; c < _cols; c++)
            {
                if (c< row.size())
                    _data[r][c] = row.get(c);
                else
                    _data[r][c]=defaultChar;
            }
        }
        _defaultChar = defaultChar;
    }

    public CharMatrix(char[][] data, char defaultChar)
    {
        _rows = data.length;
        _cols = data[0].length;
        _data = data.clone();
        for (int r=0; r < _rows; r++)
            _data[r]=data[r].clone();
        _defaultChar = defaultChar;
    }

    public static CharMatrix fromStringList(final List<String> strings, char defaultChar)
    {
        final char[][] data = new char[strings.size()][strings.get(0).length()];
        for (int r = 0; r < data.length; r++)
        {
            String row = strings.get(r);
            for (int c = 0; c < row.length(); c++)
            {
                data[r][c] = row.charAt(c);
            }
        }
        return new CharMatrix(data, defaultChar);
    }

    public static CharMatrix filledMatrix(int rows, int cols, char fillChar, char defaultChar)
    {
        final char[][] data = new char[rows][cols];
        for (char[] currentRow : data)
        {
            Arrays.fill(currentRow, fillChar);
        }
        return new CharMatrix(data, defaultChar);
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

    public boolean in(int row, int col)
    {
        return row >= 0 && row < _rows && col >= 0 && col < _cols;
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
        for (int row = 0; row < _rows; row++)
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

    public List<Position> allMatchingPositions(char x)
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

    public Set<Position> floodFill(Position start, BiPredicate<Position, Position> canMove)
    {
        final Set<Position> positions = new HashSet<>(_rows * _cols);
        final List<Position> toVisit = new ArrayList<>();
        toVisit.add(start);
        while (!toVisit.isEmpty())
        {
            Position currentPosition = toVisit.remove(0);
            if (positions.contains(currentPosition))
                continue;
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

    /**
     * get all 8 transformations of this character matrix:
     * - get original matrix
     * - in all 4 rotations
     * - flipped vertically and horizontally
     * - and the first rotation of each flip
     * When the numbers of rows and columns do not match, an IllegalStateException is thrown.
     *
     * @return the list of transformed matrices
     */
    public List<CharMatrix> getTransformations()
    {
        if (_rows!=_cols)
            throw new IllegalStateException();

        CharMatrix r1, r2, r3, fv,fh,fvr,fhr;

        fh=new CharMatrix(_data, _defaultChar);
        for (int r=0;r<_rows;r++)
            ArrayUtils.reverse(fh._data[r]);

        fv=new CharMatrix(_data, _defaultChar);
        for (int r = 0; r < _rows; r++)
        {
            for (int c=0;c<_cols;c++)
            {
                fv._data[r][c] = _data[_rows-r-1][c];
                fv._data[_rows-r-1][c] = _data[r][c];
            }
        }

        r1=rotate();
        r2=r1.rotate();
        r3=r2.rotate();
        fvr=fv.rotate();
        fhr=fh.rotate();

        return List.of(this, r1, r2, r3, fv, fh, fvr, fhr);
    }

    public CharMatrix rotate()
    {
        CharMatrix rot = new CharMatrix(_data, _defaultChar);
        for (int r = 0; r < _rows; r++)
        {
            for (int c = 0; c < _cols; c++)
            {
                rot._data[r][c] = _data[_cols-c-1][r];
            }
        }
        return rot;
    }
}
