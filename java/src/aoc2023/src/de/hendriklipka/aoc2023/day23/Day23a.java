package de.hendriklipka.aoc2023.day23;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.DepthFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.*;

/**
 * User: hli
 * Date: 23.12.23
 * Time: 11:32
 */
public class Day23a
{
    public static void main(String[] args)
    {
        try
        {
            CharMatrix trail= AocDataFileUtils.getLinesAsCharMatrix("2023", "day23", '#');
            StopWatch watch = new StopWatch();
            watch.start();
            Position start=new Position(0, 1);
            Position end=new Position(trail.rows()-1, trail.cols()-2);
            TrailWorld world=new TrailWorld(trail, start, end);
            DepthFirstSearch<TrailWorld, Trail> dfs = new DepthFirstSearch<>(world);
            dfs.search();
            System.out.println(world.longestPath);
            System.out.println(watch.getTime() + "ms");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    static class Trail implements SearchState
    {
        public Position pos;
        public int dist=0;
        public Set<Position> path=new HashSet<>();

        public Trail(Position pos)
        {
            this.pos=pos;
        }

        public Trail(Trail other, Position newPos)
        {
            dist=other.dist+1;
            path.addAll(other.path);
            path.add(other.pos);
            pos=newPos;
        }

        @Override
        public String calculateStateKey()
        {
            StringBuilder sb=new StringBuilder();
            for (Position pos: path)
                sb.append(pos.row).append('-').append(pos.col).append('/');
            sb.append(pos.row).append('-').append(pos.col);
            return sb.toString();
        }

        @Override
        public boolean betterThan(Object other)
        {
            return dist>(Integer)other;
        }

        @Override
        public Object getCurrentCost()
        {
            return dist;
        }
    }

    static class TrailWorld implements SearchWorld<Trail>
    {
        private final CharMatrix trail;
        private final Position start;
        private final Position end;
        public int longestPath=0;

        public TrailWorld(CharMatrix trail, Position start, Position end)
        {
            this.trail = trail;
            this.start = start;
            this.end = end;
        }

        @Override
        public Trail getFirstState()
        {
            return new Trail(start);
        }

        @Override
        public List<Trail> calculateNextStates(Trail currentState)
        {
            List<Trail> trails=new ArrayList<>();
            char c=trail.at(currentState.pos);
            switch(c)
            {
                case '.'->{
                    addTrail(trails, currentState, currentState.pos.updated(Direction.UP));
                    addTrail(trails, currentState, currentState.pos.updated(Direction.DOWN));
                    addTrail(trails, currentState, currentState.pos.updated(Direction.LEFT));
                    addTrail(trails, currentState, currentState.pos.updated(Direction.RIGHT));
                }
                case '>'->{
                    addTrail(trails, currentState, currentState.pos.updated(Direction.RIGHT));}
                case '<'->{
                    addTrail(trails, currentState, currentState.pos.updated(Direction.LEFT));}
                case '^'->{
                    addTrail(trails, currentState, currentState.pos.updated(Direction.UP));}
                case 'v'->{
                    addTrail(trails, currentState, currentState.pos.updated(Direction.DOWN));}
            }
            return trails;
        }

        private void addTrail(List<Trail> trails, Trail currentState, Position newPos)
        {
            if (currentState.path.contains(newPos))
                return;
            if (trail.at(newPos)=='#')
                return;
            trails.add(new Trail(currentState, newPos));
        }

        @Override
        public boolean reachedTarget(Trail currentState)
        {
            final boolean done = currentState.pos.equals(end);
            if (done)
            {
                if (currentState.dist>longestPath)
                    longestPath=currentState.dist;
            }
            return done;
        }

        @Override
        public boolean canPruneBranch(Trail currentState)
        {
            //seems we cannot prune branches
            return false;
        }

        @Override
        public Comparator<Trail> getComparator()
        {
            return (state1, state2) -> -Integer.compare(state1.dist, state2.dist);
        }
    }
}
