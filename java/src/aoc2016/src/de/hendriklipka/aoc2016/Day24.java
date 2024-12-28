package de.hendriklipka.aoc2016;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.AStarSearch;
import de.hendriklipka.aoc.search.CharArrayWorld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day24 extends AocPuzzle
{

    private CharMatrix _ducts;
    private List<Position> _targets;
    private Map<String, Integer> _distances;
    private int _highestTarget;

    public static void main(String[] args)
    {
        new Day24().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        _ducts = data.getLinesAsCharMatrix('#');
        _targets = new ArrayList<>();
        _highestTarget = -1;
        getTargets();
        // we store all pairs of distances
        _distances = new HashMap<>();
        getDistances();

        return findShortestPath(List.of(0), 0);
    }

    // since there are only 7 points to visit, and we have the distances already, we can brute-force the calculation
    private int findShortestPath(final List<Integer> points, final int len)
    {
        if (points.size()-1==_highestTarget)
            return len;
        int bestPath=Integer.MAX_VALUE;
        int currentPoint = points.get(points.size()-1);
        for (int i=0;i<=_highestTarget;i++)
        {
            if (!points.contains(i))
            {
                List<Integer> newPoints = new ArrayList<>(points);
                newPoints.add(i);
                int path=findShortestPath(newPoints, len+_distances.get(currentPoint+"-"+i));
                if (path<bestPath)
                    bestPath=path;
            }
        }
        return bestPath;
    }

    private int findShortestPathReturn(final List<Integer> points, final int len)
    {
        // same algorithm as before, but we ned to add the return path at the end
        if (points.size()-1==_highestTarget)
        {
            int currentPoint = points.get(points.size() - 1);
            return len+ _distances.get(currentPoint + "-0");
        }
        int bestPath=Integer.MAX_VALUE;
        int currentPoint = points.get(points.size()-1);
        for (int i=0;i<=_highestTarget;i++)
        {
            if (!points.contains(i))
            {
                List<Integer> newPoints = new ArrayList<>(points);
                newPoints.add(i);
                int path= findShortestPathReturn(newPoints, len + _distances.get(currentPoint + "-" + i));
                if (path<bestPath)
                    bestPath=path;
            }
        }
        return bestPath;
    }

    private void getDistances()
    {
        for (int i = 0; i <= _highestTarget; i++)
        {
            final var world = new CharArrayWorld(_ducts, _targets.get(i), new Position(_ducts.rows() - 1, _ducts.cols() - 1), '#');
            AStarSearch search = new AStarSearch(world);
            search.findPath();
            // we ignore whether we found a path, but read the results of the flood-fill
            for (int t = 0; t <= _highestTarget; t++)
            {
                int len = search.getPathLength(_targets.get(t));
                _distances.put(i + "-" + t,len);
            }
        }
    }

    private void getTargets()
    {
        for (int i=0;i<10;i++)
        {
            Position p = _ducts.findFirst((char)('0' + i));
            if (null!=p)
            {
                _ducts.set(p, '.');
                _targets.add(p);
                _highestTarget =i;
            }
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        _ducts = data.getLinesAsCharMatrix('#');
        _targets = new ArrayList<>();
        _highestTarget = -1;
        getTargets();
        // we store all pairs of distances
        _distances = new HashMap<>();
        getDistances();

        return findShortestPathReturn(List.of(0), 0);
    }
}
