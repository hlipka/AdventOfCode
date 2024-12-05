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

public class Day16a
{
    public static void main(String[] args)
    {
        try
        {
            CharMatrix testGrid = AocDataFileUtils.getLinesAsCharMatrix("2023", "ex16", '.');
            CharMatrix grid = AocDataFileUtils.getLinesAsCharMatrix("2023", "day16", '.');
            System.out.println(simulate(testGrid));
            System.out.println(simulate(grid));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static long simulate(CharMatrix grid)
    {
        LinkedList<Pair<Position, Direction>> beams=new LinkedList<>();
        Map<Position, List<Direction>> fields = new HashMap<>();
        beams.add(new ImmutablePair<>(new Position(0, -0), RIGHT));
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
