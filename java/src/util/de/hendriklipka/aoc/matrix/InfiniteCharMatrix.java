package de.hendriklipka.aoc.matrix;

import de.hendriklipka.aoc.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: hli
 */
public class InfiniteCharMatrix
{
    private int lowestRow=0;
    private int highestRow=0;
    private int lowestColumn=0;
    private int highestColumn=0;

    Map<Position, Character> data= new HashMap<>();
    private final Character defaultChar;

    public InfiniteCharMatrix(final Character defaultChar)
    {
        this.defaultChar = defaultChar;
    }

    public void set(Position pos, char c)
    {
        if (pos.row > highestRow)
            highestRow = pos.row;
        if (pos.row < lowestRow)
            lowestRow = pos.row;
        if (pos.col > highestColumn)
            highestColumn = pos.col;
        if (pos.col < lowestColumn)
            lowestColumn = pos.col;
        data.put(pos, c);
    }

    public char at(Position pos)
    {
        return data.getOrDefault(pos, defaultChar);
    }

    public boolean inside(Position pos)
    {
        return pos.row>=lowestRow && pos.row<=highestRow && pos.col>=lowestColumn && pos.col<=highestColumn;
    }

    public boolean in(Position pos)
    {
        return data.containsKey(pos);
    }

    public CharMatrix getSubMatrix(Position topLeft, int rows, int columns)
    {
        CharMatrix result = CharMatrix.filledMatrix(rows, columns, defaultChar, defaultChar);
        for (int r=0;r<rows;r++)
        {
            for (int c=0;c<columns;c++)
            {
                Position pos=topLeft.updated(r, c);
                result.set(new Position(r,c), at(pos));
            }
        }
        return result;
    }

    public int lowestRow()
    {
        return lowestRow;
    }

    public int highestRow()
    {
        return highestRow;
    }

    public int lowestColumn()
    {
        return lowestColumn;
    }

    public int highestColumn()
    {
        return highestColumn;
    }

    public int rows()
    {
        return highestRow-lowestRow+1;
    }

    public void set(final Position pos, final CharMatrix expanded)
    {
        for (Position p: expanded.allPositions())
        {
            if (expanded.at(p)!=defaultChar)
                set(pos.updated(p), expanded.at(p));
        }
    }

    public int getMinRow()
    {
        return data.keySet().stream().mapToInt(p->p.row).min().orElseThrow();
    }

    public int getMaxRow()
    {
        return data.keySet().stream().mapToInt(p->p.row).max().orElseThrow();
    }

    public Set<Position> allKnownTiles()
    {
        return data.keySet();
    }
}
