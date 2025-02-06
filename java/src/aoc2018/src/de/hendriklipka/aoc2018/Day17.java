package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.InfiniteCharMatrix;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.hendriklipka.aoc.DiagonalDirections.*;

public class Day17 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day17().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        InfiniteCharMatrix tiles=new InfiniteCharMatrix('.');
        data.getLines().stream().map(l-> AocParseUtils.parsePartsFromString(l, "(.)=(\\d+), .=(\\d+)..(\\d+)")).forEach(c->handleCoords(c, tiles));
        int minY=tiles.getMinRow();
        int maxY=tiles.getMaxRow();
        System.out.println(maxY);
        final Position well = new Position(0, 500);
        // we keep track of where water is flowing, but does not rest
        final Set<Position> running=new HashSet<>();
        while(true)
        {
            // simulate the next square of water - return true when it fully fell through
            if (simulateWater(well, tiles, running, maxY))
            {
                break;
            }
        }
        final List<Position> fixedWater = tiles.allKnownTiles().stream().filter(t -> t.row <= maxY && t.row >= minY).filter(t -> tiles.at(t) == 'w').toList();
        final List<Position> runningWater = running.stream().filter(t -> t.row <= maxY && t.row >= minY).toList();
        // we need a union here - some of the running water places later contain water at rest
        return CollectionUtils.union(fixedWater, runningWater).size();
    }

    // true when all water fell through
    private boolean simulateWater(final Position well, final InfiniteCharMatrix tiles, Set<Position> running, final int maxRow)
    {
        boolean allFellThrough=true;
        Set<Position> water=new HashSet<>();
        water.add(well);
        /*
            Simulate a splash of water
            We record where it just flows downwards (since we need this later)
            when we hit something (either clay or water at rest - records in the tile map)
            we check to both sides whether the water would stay here (clay walls found) or could flow further down
            if it stays, record the water in the map as stationary and we are done with that splash
            when it can fall down, we create on or two new splashes for the sides, and continue with them separately
            we are done with a splash when it reaches to bottom ofn the map
            we record whether all water did fall through or we got some new stationary water
         */
        while (!water.isEmpty())
        {
            Set<Position> newWater=new HashSet<>();
            // simulate one of the current splashes
            for (Position p : water)
            {
                // when the water gets too deep, we stop if flowing
                if (p.row>maxRow)
                    continue;
                final char downTile = tiles.at(p.updated(DOWN));
                if (downTile == '.')
                {
                    running.add(p);
                    newWater.add(p.updated(DOWN));
                }
                else
                {
                    // we hit something, so lets look at both sides
                    int leftCol=p.col;
                    boolean leftWall=false;
                    while (true)
                    {
                        leftCol--;
                        // check for a wall
                        if (tiles.at(new Position(p.row, leftCol))=='#')
                        {
                            leftWall=true;
                            break;
                        }
                        // check for a place to further fall down
                        if (tiles.at(new Position(p.row+1, leftCol))=='.')
                        {
                            break;
                        }
                    }
                    int rightCol=p.col;
                    boolean rightWall=false;
                    while (true)
                    {
                        rightCol++;
                        // check for a wall
                        if (tiles.at(new Position(p.row, rightCol))=='#')
                        {
                            rightWall=true;
                            break;
                        }
                        // check for a place to further fall down
                        if (tiles.at(new Position(p.row+1, rightCol))=='.')
                        {
                            break;
                        }
                    }
                    // water will stay here
                    if (leftWall&&rightWall)
                    {
                        allFellThrough=false;
                        for (int col=leftCol+1; col<rightCol; col++)
                        {
                            tiles.set(new Position(p.row, col), 'w');
                        }
                    }
                    else
                    {
                        // water can flow down, so remember its way
                        for (int col=leftCol+1; col<rightCol; col++)
                        {
                            running.add(new Position(p.row, col));
                        }
                        // add new splashes where we found a way down
                        if (!leftWall)
                        {
                            running.add(new Position(p.row, leftCol));
                            newWater.add(new Position(p.row+1, leftCol));
                        }
                        if (!rightWall)
                        {
                            running.add(new Position(p.row, rightCol));
                            newWater.add(new Position(p.row+1, rightCol));
                        }
                    }
                }
            }
            water=newWater;
        }
        return allFellThrough;
    }

    private void handleCoords(final List<String> coords, final InfiniteCharMatrix tiles)
    {
        if (coords.get(0).equals("x"))
        {
            int x=Integer.parseInt(coords.get(1));
            int yFrom=Integer.parseInt(coords.get(2));
            int yTo=Integer.parseInt(coords.get(3));
            for (int y=yFrom;y<=yTo;y++)
            {
                tiles.set(new Position(y, x), '#');
            }
        }
        else
        {
            int y=Integer.parseInt(coords.get(1));
            int xFrom=Integer.parseInt(coords.get(2));
            int xTo=Integer.parseInt(coords.get(3));
            for (int x=xFrom;x<=xTo;x++)
            {
                tiles.set(new Position(y, x), '#');
            }
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // this is the same as part A, but we just need to know the amount of water at rest (which we recorded in the tile map)
        InfiniteCharMatrix tiles=new InfiniteCharMatrix('.');
        data.getLines().stream().map(l-> AocParseUtils.parsePartsFromString(l, "(.)=(\\d+), .=(\\d+)..(\\d+)")).forEach(c->handleCoords(c, tiles));
        int minY=tiles.getMinRow();
        int maxY=tiles.getMaxRow();
        System.out.println(maxY);
        final Position well = new Position(0, 500);
        // we keep track of where water is flowing, but does not rest
        final Set<Position> running=new HashSet<>();
        while(true)
        {
            // simulate the next square of water - return true when it fully fell through
            if (simulateWater(well, tiles, running, maxY))
            {
                break;
            }
        }
        final List<Position> fixedWater = tiles.allKnownTiles().stream().filter(t -> t.row <= maxY && t.row >= minY).filter(t -> tiles.at(t) == 'w').toList();
        return fixedWater.size();
    }
}
