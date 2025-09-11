package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.CardinalDirection;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.matrix.InfiniteCharMatrix;
import de.hendriklipka.aoc.matrix.IntMatrix;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class Day20 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day20().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        IntMatrix distances = exploreBase();
        // find the largest value
        final Integer maxValue = distances.allPositions().stream().map(distances::at).max(Integer::compare).orElseThrow();
        System.out.println(StringUtils.join(distances.allMatchingPositions(maxValue), '\n'));
        return maxValue;
    }

    private IntMatrix exploreBase() throws IOException
    {
        String line = data.getLines().get(0).substring(1); // skip the ^
        InfiniteCharMatrix base = new InfiniteCharMatrix('#');
        parseLine(line, 0, new Position(0, 0), base);
        base.set(new Position(0,0), 'X');

        // we now have a filled in base
        final CharMatrix base2 = base.getSubMatrix(new Position(base.getMinRow() - 1, base.lowestColumn() - 1), base.rows() + 2,
                base.columns() + 2);
        // base2.print();

        // move the start position around - it was at (0,0) but is now somewhere else
        Position startPos=new Position(-base.getMinRow()+1, -base.lowestColumn()+1);

        // flood-fill the base to get the shortest path (counting doors encountered on the way)
        IntMatrix distances=new IntMatrix(base2.rows(),base2.cols(), -1);
        findPath(base2, startPos, distances, 0);
        return distances;
    }

    private void findPath(final CharMatrix base, final Position position, final IntMatrix distances, final int length)
    {
        int oldLength = distances.at(position);
        if (-1==oldLength || length < oldLength)
        {
            distances.set(position, length);
        }
        else
        {
            return;
        }
        for (Direction dir: Direction.values())
        {
            if (base.at(position.updated(dir))=='d')
            {
                findPath(base, position.updated(dir, 2), distances, length+1);
            }
        }
    }

    /*
      strategy:
      - parse and execute steps until we find and opening parenthesis
        - execute means: got two steps in that direction, mark the first with 'd' for door, and the next with '.' for a room
      - there, create a list of all definitions in that group
        - we can simply look for "|", and count opening / closing parentheses
      - execute each of these subgroups recursively
        - store the positions of where each ended up, and remove duplicates
          (we still need to execute all since they can mark rooms)
      - continue with the rest of the expression
     */
    private Collection<Position> parseLine(final String line, int offset, Position position, InfiniteCharMatrix base)
    {
        while (offset < line.length())
        {
            char c=line.charAt(offset);
            offset++;
            if (c=='$')
                return List.of(position);
            if (c !='(') // the only other characters are | and ), and we can encounter them only when we are scanning for a group
            {
                CardinalDirection direction = CardinalDirection.of(c);
                position = position.updated(direction);
                base.set(position, 'd');
                position = position.updated(direction);
                base.set(position, '.');
            }
            else // find subgroups
            {
                // find subgroups
                Pair<Integer, Collection<String>> groupData=getGroups(line, offset);
                int groupLength=groupData.getLeft();
                // execute each group recursively
                Set<Position> subPositions=new HashSet<>();
                for (String group : groupData.getRight())
                {
                    Collection<Position> endPos=parseLine(group, 0, position, base);
                    subPositions.addAll(endPos);
                }
                // for each of the potential end positions, continue with the rest of the pattern
                Set<Position> result=new HashSet<>();
                for (Position p : subPositions)
                {
                    result.addAll(parseLine(line, offset+groupLength, p, base));
                }
                return result;
            }
        }

        return List.of(position);
    }

    private Pair<Integer, Collection<String>> getGroups(final String line, int offset)
    {
        int length=0;
        int lastOffset=offset;
        List<String> groups=new ArrayList<>();
        int openCount=0;
        while (openCount>=0)
        {
            char c=line.charAt(offset);
            if (c=='(')
            {
                openCount++;
            }
            else if (c==')')
            {
                openCount--;
            }
            length++;
            offset++;
            if (c=='|' && 0==openCount)
            {
                final String group = line.substring(lastOffset, offset-1);
                groups.add(group);
                lastOffset=offset;
            }
            // we cannot reach the onf of the line, since we can assume that the pattern is correct
        }
        // at the end we need to add the last group as well
        final String group = line.substring(lastOffset, offset-1);
        groups.add(group);
        return Pair.of(length, groups);
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        IntMatrix distances = exploreBase();
        // find the rooms with a distance of at least 1000
        return distances.allPositions().stream().map(distances::at).filter(d-> d >= 1000).count();
    }
}
