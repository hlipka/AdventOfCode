package de.hendriklipka.aoc2023.day16;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

import static de.hendriklipka.aoc.Direction.*;

public class Day16b
{
    public static void main(String[] args)
    {
        try
        {
            CharMatrix testGrid = AocDataFileUtils.getLinesAsCharMatrix("2023", "ex16", '.');
            CharMatrix grid = AocDataFileUtils.getLinesAsCharMatrix("2023", "day16", '.');
            System.out.println(countMax(testGrid));
            System.out.println(countMax(grid));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static long countMax(CharMatrix grid)
    {
        long max=0;
        for (int col=0;col<grid.cols();col++)
        {
            long count=simulate(grid, ImmutablePair.of(new Position(0, col), DOWN));
            max=Math.max(max, count);
            count=simulate(grid, ImmutablePair.of(new Position(grid.rows()-1, col), UP));
            max=Math.max(max, count);
        }
        for (int row=0;row<grid.rows();row++)
        {
            long count=simulate(grid, ImmutablePair.of(new Position(row, 0), RIGHT));
            max=Math.max(max, count);
            count=simulate(grid, ImmutablePair.of(new Position(row, grid.cols()-1), LEFT));
            max=Math.max(max, count);
        }
        return max;
    }

    private static long simulate(CharMatrix grid, ImmutablePair<Position, Direction> start)
    {
        LinkedList<Pair<Position, Direction>> beams=new LinkedList<>();
        Map<Position, List<Direction>> fields = new HashMap<>();
        beams.add(start);
        while(!beams.isEmpty())
        {
            Pair<Position, Direction> beam = beams.pollFirst();
            traceBeam(beam, grid, beams, fields);
        }
        return fields.size();
    }

    private static void traceBeam(Pair<Position, Direction> beam, CharMatrix grid, LinkedList<Pair<Position, Direction>> beams,
                                  Map<Position, List<Direction>> fields)
    {
        Position currentPos = beam.getLeft();
        if (!grid.in(currentPos))
            return;
        List<Direction> dirs = fields.computeIfAbsent(currentPos, k -> new ArrayList<>());
        Direction currentDir = beam.getRight();
        if (dirs.contains(currentDir))
            return;
        dirs.add(currentDir);
        char field=grid.at(currentPos);
        switch(field)
        {
            case '.'->{
                Position newPos=currentPos.updated(currentDir);
                beams.add(new ImmutablePair<>(newPos, currentDir));
            }
            case '-'->{
                if (currentDir == LEFT || currentDir == RIGHT)
                {
                    beams.add(new ImmutablePair<>(currentPos.updated(currentDir), currentDir));
                }
                else
                {
                    beams.add(new ImmutablePair<>(currentPos.updated(LEFT), LEFT));
                    beams.add(new ImmutablePair<>(currentPos.updated(RIGHT), RIGHT));
                }
            }
            case '|'->{
                if (currentDir==Direction.UP || currentDir == DOWN)
                {
                    beams.add(new ImmutablePair<>(currentPos.updated(currentDir), currentDir));
                }
                else
                {
                    beams.add(new ImmutablePair<>(currentPos.updated(UP), UP));
                    beams.add(new ImmutablePair<>(currentPos.updated(DOWN), DOWN));
                }
            }
            case '/'->{
                switch(currentDir)
                {
                    case UP ->
                    {
                        beams.add(new ImmutablePair<>(currentPos.updated(RIGHT), RIGHT));
                    }
                    case DOWN ->
                    {
                        beams.add(new ImmutablePair<>(currentPos.updated(LEFT), LEFT));
                    }
                    case LEFT ->
                    {
                        beams.add(new ImmutablePair<>(currentPos.updated(DOWN), DOWN));
                    }
                    case RIGHT ->
                    {
                        beams.add(new ImmutablePair<>(currentPos.updated(UP), UP));
                    }
                }
            }
            case '\\'->{
                switch(currentDir)
                {
                    case UP ->
                    {
                        beams.add(new ImmutablePair<>(currentPos.updated(LEFT), LEFT));
                    }
                    case DOWN ->
                    {
                        beams.add(new ImmutablePair<>(currentPos.updated(RIGHT), RIGHT));
                    }
                    case LEFT ->
                    {
                        beams.add(new ImmutablePair<>(currentPos.updated(UP), UP));
                    }
                    case RIGHT ->
                    {
                        beams.add(new ImmutablePair<>(currentPos.updated(DOWN), DOWN));
                    }
                }
            }
        }
    }
}
