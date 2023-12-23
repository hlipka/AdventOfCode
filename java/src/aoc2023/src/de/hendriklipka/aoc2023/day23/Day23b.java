package de.hendriklipka.aoc2023.day23;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

import static de.hendriklipka.aoc.Direction.*;

/**
 * Make this a graph problem
 * - add start and end as graph nodes
 * - from the start, find the first junction
 * - store junction as graph node, store length at edge
 * - use junction as a new start node
 * - find next junctions from there
 * - when the junction is the end, stop this path there
 * now we have a graph of paths between junctions, which we can use to find the longest path
 */
public class Day23b
{
    public static void main(String[] args)
    {
        try
        {
            CharMatrix trail = AocParseUtils.getLinesAsCharMatrix("2023", "day23", '#');
            StopWatch watch = new StopWatch();
            watch.start();
            Position start = new Position(0, 1);
            Position end = new Position(trail.rows() - 1, trail.cols() - 2);

            MultiValuedMap<String, Pair<String, Integer>> paths = new ArrayListValuedHashMap<>();
            LinkedList<Position> junctionsToCheck = new LinkedList<>();
            junctionsToCheck.add(start);
            Set<Position> visited = new HashSet<>();
            while (!junctionsToCheck.isEmpty())
            {
                Position startJ = junctionsToCheck.poll();
                // find open directions.
                List<Direction> dirs = new ArrayList<>();
                if (trail.at(startJ.updated(UP)) != '#' && !visited.contains(startJ.updated(UP))) dirs.add(UP);
                if (trail.at(startJ.updated(DOWN)) != '#' && !visited.contains(startJ.updated(DOWN))) dirs.add(DOWN);
                if (trail.at(startJ.updated(LEFT)) != '#' && !visited.contains(startJ.updated(LEFT))) dirs.add(LEFT);
                if (trail.at(startJ.updated(RIGHT)) != '#' && !visited.contains(startJ.updated(RIGHT))) dirs.add(RIGHT);
                for (Direction dir : dirs)
                {
                    Pair<Position, Integer> path = tracePath(startJ, dir, trail, end, visited);
                    Position nextJunction = path.getLeft();
                    if (!nextJunction.equals(end))
                        junctionsToCheck.add(nextJunction);
                    // store path in both directions
                    paths.put(getName(startJ), Pair.of(getName(nextJunction), path.getRight()));
                    paths.put(getName(nextJunction), Pair.of(getName(startJ), path.getRight()));
                }
            }
            final HashSet<String> visitedJunctions = new HashSet<>();
            visitedJunctions.add(getName(start));
            long path = findPath(paths, getName(start), getName(end), visitedJunctions);
            System.out.println(path);
            System.out.println(watch.getTime() + "ms");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * brute force the path calculation
     * the GraphSearch class (using Dijkstra's algorithm) does not work here, it finds a path that is too short
     * (see <a href="https://en.wikipedia.org/wiki/Longest_path_problem">Wikipedia</a> for why)
     */
    private static int findPath(MultiValuedMap<String, Pair<String, Integer>> allPaths, String current, String end, Set<String> visited)
    {
        int length = -1;
        if (current.equals(end))
            return 0;

        // where can we go from here?
        Collection<Pair<String, Integer>> paths = allPaths.get(current);
        for (Pair<String, Integer> path : paths)
        {
            final String target = path.getLeft();
            // and how long is the current segment we are looking at
            final Integer currentSegmentLength = path.getRight();

            // we were already at the target, so we cannot visit this path
            if (visited.contains(target))
                continue;

            // add the new target to the list of visited places for this variation
            visited.add(target);
            // find the longest path from here
            final int subPathLength = findPath(allPaths, target, end, visited);
            visited.remove(target); // clean up the visited list
            // when the path did not find the end, do not use it
            if (subPathLength<0)
                continue;
            // if it is longer than the best we have so far, store it
            if (currentSegmentLength + subPathLength > length)
                length = currentSegmentLength + subPathLength;
        }
        return length;
    }

    static Pair<Position, Integer> tracePath(Position start, Direction dir, CharMatrix trail, Position end, Set<Position> visited)
    {
        int len = 0;
        Position pos = start;
        // find a path until the next junction.
        // return the junction, length until there, and the dir we reached it through
        while (true)
        {
            pos = pos.updated(dir);
            len++;
            if (pos.equals(end))
                return Pair.of(pos, len);
            if (isJunction(trail, pos))
            {
                return Pair.of(pos, len);
            }
            visited.add(pos);
            // we now have only one open direction
            if (dir != DOWN && trail.at(pos.updated(UP)) != '#' && !visited.contains(pos.updated(UP))) dir = UP;
            else if (dir != UP && trail.at(pos.updated(DOWN)) != '#' && !visited.contains(pos.updated(DOWN))) dir = DOWN;
            else if (dir != RIGHT && trail.at(pos.updated(LEFT)) != '#' && !visited.contains(pos.updated(LEFT))) dir = LEFT;
            else if (dir != LEFT && trail.at(pos.updated(RIGHT)) != '#' && !visited.contains(pos.updated(RIGHT))) dir = RIGHT;
        }
    }

    private static boolean isJunction(CharMatrix trail, Position newPos)
    {
        int free = 0;
        free += trail.at(newPos.updated(UP)) == '#' ? 0 : 1;
        free += trail.at(newPos.updated(DOWN)) == '#' ? 0 : 1;
        free += trail.at(newPos.updated(LEFT)) == '#' ? 0 : 1;
        free += trail.at(newPos.updated(RIGHT)) == '#' ? 0 : 1;
        return free > 2;
    }

    private static String getName(Position pos)
    {
        return Integer.toString(pos.hashCode());
    }
}
