package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.AStarPrioritizedSearch;
import de.hendriklipka.aoc.search.AStarSearch;
import de.hendriklipka.aoc.search.CharArrayWorld;

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
        //AStarSearch search=new AStarSearch(new CharArrayWorld(memory, start, end, '#'));
        AStarPrioritizedSearch search = new AStarPrioritizedSearch(new CharArrayWorld(memory, start, end, '#')); // simple manhattan distance as heuristic
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
        List<String> addresses=data.getLines();
        final CharArrayWorld world = new CharArrayWorld(memory, start, end, '#');
        // lets just brute-force all paths
        // we should start after the first 1024 lines, but this still runs in under 1 minute
        // optimization: check whether the next byte would land on the current shortest path, if not we can skip it
        // (but the current AStarSearch cannot return the path)
        // we also could do a binary search, which would be even faster
        for (String address : addresses)
        {
            setMemory(memory, address);
            AStarPrioritizedSearch search = new AStarPrioritizedSearch(world); // simple manhattan distance as heuristic
            search.findPath();
            if (!search.didFoundTarget())
            {
                return address;
            }
        }
        return "none";
    }

}
