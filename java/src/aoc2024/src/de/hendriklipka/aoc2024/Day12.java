package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class Day12 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day12().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        CharMatrix map = data.getLinesAsCharMatrix('.');
        List<Region> knownRegions=new ArrayList<>();
        map.allPositions().forEach(p->handlePosition(map, p, knownRegions));
        return knownRegions.stream().mapToInt(Region::price).sum();
    }

    private void handlePosition(final CharMatrix map, final Position p, final List<Region> knownRegions)
    {
        // we already have this position in a region
        if (knownRegions.stream().anyMatch(r->r.contains(p)))
            return;
        Region r=new Region(map.at(p));
        knownRegions.add(r);
        fillInRegion(map, r, p);
    }

    private void fillInRegion(final CharMatrix map, final Region r, final Position p)
    {
        // do a flood fill from the current starting point to get the full region
        List<Position> toBeVisited=new ArrayList<>();
        toBeVisited.add(p);
        while (!toBeVisited.isEmpty())
        {
            Position currentPos=toBeVisited.remove(0);
            if (!r.contains(currentPos)) // we did not visit before
            {
                r.area.add(currentPos); // add this to the regions area
                for (Direction d: Direction.values())
                {
                    Position nextPos=currentPos.updated(d);
                    if (map.at(nextPos)==r.name)
                    {
                        // when the next position belongs to the current region, remember to also visit it
                        toBeVisited.add(nextPos);
                    }
                    else
                    {
                        // otherwise this is a fence, so we remember the fence segment
                        // an outer position might have multiple fences, so we also remember the direction we are looking at it
                        r.fence.put(nextPos.getKey()+";"+d.getKey(), Pair.of(nextPos, d));
                    }
                }
            }
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CharMatrix map = data.getLinesAsCharMatrix('.');
        List<Region> knownRegions=new ArrayList<>();
        map.allPositions().forEach(p->handlePosition(map, p, knownRegions));
        return knownRegions.stream().mapToInt(Region::discounted).sum();
    }

    private static class Region
    {
        Set<Position> area=new HashSet<>();
        // for part 1 a set would be sufficient, but part 2 is easier when we also remember position and direction
        Map<String, Pair<Position, Direction>> fence = new HashMap<>();
        char name;

        Region(char c)
        {
            name=c;
        }
        int price()
        {
            return area.size()*fence.size();
        }

        int sides()
        {
            // while determining the area, we also calculated all fences (as a pair of the inner position and the direction the fence is facing)
            int sides=0;
            // remember which of these fences we already handled
            Set<String> visitedFence=new HashSet<>();

            for (Map.Entry<String, Pair<Position, Direction>> currentFence: fence.entrySet() )
            {
                // we only must look at each fence once
                if (!visitedFence.contains(currentFence.getKey()))
                {
                    // we know the current fence will form a new side
                    sides++;
                    Position p=currentFence.getValue().getLeft();
                    Direction d=currentFence.getValue().getRight();
                    // when we have the position and the direction, we look to each of the side of the fence
                    for (Direction side:d.perpendicular())
                    {
                        Position nextPos=p;
                        // look whether we have a fence segment which is part of the current side
                        // segments being in the right position but facing in the wrong direction will be skipped here (because the direction is wrong)
                        while (true)
                        {
                            nextPos=nextPos.updated(side);
                            final String key = nextPos.getKey() + ";" + d.getKey();
                            if (fence.containsKey(key))
                            {
                                // when we have segment, mark it (as belonging to the current side)
                                visitedFence.add(key);
                            }
                            else
                            {
                                // otherwise we stop, the side cannot be extended further into this direction
                                break;
                            }
                        }
                    }
                }
            }
            return sides;
        }

        public boolean contains(Position p)
        {
            return area.contains(p);
        }

        public int discounted()
        {
            return area.size()*sides();
        }
    }
}
