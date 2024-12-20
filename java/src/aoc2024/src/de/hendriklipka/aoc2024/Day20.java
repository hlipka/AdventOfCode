package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.AStarPrioritizedSearch;
import de.hendriklipka.aoc.search.CharArrayWorld;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day20 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day20().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        CharMatrix track = data.getLinesAsCharMatrix('#');
        final Position start = track.findFirst('S');
        final Position end = track.findFirst('E');
        track.set(start,'.');
        track.set(end,'.');
        // find the path through the track
        AStarPrioritizedSearch search = new AStarPrioritizedSearch(new CharArrayWorld(track, start, end, '#'));
        int len=search.findPath();
        System.out.println("track length="+len);
        // collect the path, and the length at each position
        final List<Position> path = search.getPath();
        Map<Position, Integer> pathData = new HashMap<>();
        path.forEach(p->pathData.put(p, search.getPathLength(p)));

        Map<Integer, Integer> cheats = new HashMap<>();
        path.forEach(p-> findShortCheats(p, pathData, track, cheats));
        if (isExample)
        {
            cheats.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).forEach(e-> System.out.println(e.getKey() + "->" + e.getValue()));
        }
        return cheats.entrySet().stream().filter(e->e.getKey()>=100).mapToInt(Map.Entry::getValue).sum();
    }

    private static void findShortCheats(final Position p, final Map<Position, Integer> pathData, final CharMatrix track, final Map<Integer, Integer> cheats)
    {
        int current = pathData.get(p);
        for (Direction d: Direction.values())
        {
            Position c1= p.updated(d);
            final Position c2 = c1.updated(d);
            if (track.at(c1) == '#' && track.at(c2) == '.')
            {
                int next= pathData.get(c2);
                int save=current-next-2;
                if (save>0)
                {
                    int count = cheats.getOrDefault(save, 0);
                    cheats.put(save, count + 1);
                }
            }
        }
    }

    private static void findLongCheats(final Position p, final Map<Position, Integer> pathData, final CharMatrix track, final Map<Integer, Integer> cheats)
    {
        int current = pathData.get(p);
        // find all positions within manhattan distances of 2 up to 20
        // (a cheat of 1 is just the next field, so it does not save time)
        for (int dist=2; dist<=20; dist++)
        {
            List<Position> targets = p.getWithinDistance(dist);
            for (Position target: targets)
            {
                if (track.in(target) && track.at(target) == '.')
                {
                    int next= pathData.get(target);
                    int save=current-next-dist;
                    if (save>0)
                    {
                        int count = cheats.getOrDefault(save, 0);
                        cheats.put(save, count + 1);
                    }

                }
            }
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CharMatrix track = data.getLinesAsCharMatrix('#');
        final Position start = track.findFirst('S');
        final Position end = track.findFirst('E');
        track.set(start,'.');
        track.set(end,'.');
        // find the path through the track
        AStarPrioritizedSearch search = new AStarPrioritizedSearch(new CharArrayWorld(track, start, end, '#'));
        int len=search.findPath();
        System.out.println("track length="+len);
        // collect the path, and the length at each position
        final List<Position> path = search.getPath();
        Map<Position, Integer> pathData = new HashMap<>();
        path.forEach(p->pathData.put(p, search.getPathLength(p)));

        Map<Integer, Integer> cheats = new HashMap<>();
        path.forEach(p-> findLongCheats(p, pathData, track, cheats));
        if (isExample)
        {
            cheats.entrySet().stream().filter(e->e.getKey()>=50).sorted(Comparator.comparingInt(Map.Entry::getKey)).forEach(e-> System.out.println(e.getKey() + "->" + e.getValue()));
        }
        return cheats.entrySet().stream().filter(e->e.getKey()>=100).mapToInt(Map.Entry::getValue).sum();    }
}
