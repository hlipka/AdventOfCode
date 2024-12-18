package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.AStarSearch;
import de.hendriklipka.aoc.search.ArrayWorld;

import java.io.IOException;
import java.util.List;

public class Day18 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day18().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        Position start = new Position(0, 0);
        CharMatrix memory;
        Position end;
        int lines;
        if (isExample)
        {
            memory = CharMatrix.filledMatrix(7,7,'.', '#');
            end=new Position(6,6);
            lines=12;
        }
        else
        {
            memory = CharMatrix.filledMatrix(71,71,'.', '#');
            end=new Position(70,70);
            lines=1024;
        }

        data.getLines().stream().limit(lines).forEach(l->setMemory(memory, l));
        AStarSearch search=new AStarSearch(new MemoryWorld(memory, start, end));
        return search.findPath();
    }

    private void setMemory(final CharMatrix memory, final String line)
    {
        List<Integer> pos= AocParseUtils.splitLineToInts(line);
        memory.set(new Position(pos.get(0), pos.get(1)), '#');
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        Position start = new Position(0, 0);
        CharMatrix memory;
        Position end;
        if (isExample)
        {
            memory = CharMatrix.filledMatrix(7,7,'.', '#');
            end=new Position(6,6);
        }
        else
        {
            memory = CharMatrix.filledMatrix(71,71,'.', '#');
            end=new Position(70,70);
        }
        List<String> bytes=data.getLines();
        final MemoryWorld world = new MemoryWorld(memory, start, end);
        for (int i=0;i<bytes.size();i++)
        {
            setMemory(memory, bytes.get(i));
            AStarSearch search=new AStarSearch(world);
            if (search.findPath()==Integer.MAX_VALUE)
            {
                return bytes.get(i);
            }
        }
        return -1;
    }

    private static class MemoryWorld implements ArrayWorld
    {
        private final CharMatrix _memory;
        private final Position _start;
        private final Position _end;

        public MemoryWorld(final CharMatrix memory, Position start, Position end)
        {
            _memory=memory;
            _start=start;
            _end=end;
        }

        @Override
        public int getWidth()
        {
            return _memory.cols();
        }

        @Override
        public int getHeight()
        {
            return _memory.rows();
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
            return _memory.at(y,x)!='#';
        }
    }
}
