package de.hendriklipka.aoc2023.day23;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.DepthFirstSearchNoMemoize;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.*;

/**
 * This is a brute-force test (using the same code as for part a)
 * It seems a generic DFS is difficult:
 * - it is not optimized for a _pessimistic_ path
 * - its single 'state key' does not work to ensure we do not visit a junction twice (but it makes the state list very large)
 *
 * runs through, takes about 1300 seconds (20 minutes) - the graph solution needs 3 seconds
 */
public class Day23b2
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
            // use a DFS without memoization
            DepthFirstSearchNoMemoize<TrailWorld, Trail> dfs = new DepthFirstSearchNoMemoize<>(world);
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
        public Position lastPos;
        public int dist=0;
        public Set<Integer> visited =null;

        public Trail(Position pos)
        {
            this.pos=pos;
            this.lastPos = pos.updated(Direction.UP);
            visited = new HashSet<>();
        }

        public Trail(Trail other, Position newPos, CharMatrix trail)
        {
            dist=other.dist+1;
            lastPos=other.pos;
            pos = newPos;
            // we only store junctions, because the long path between them do not have information
            if (isJunction(trail, newPos))
            {
                visited = new HashSet<>(other.visited);
                visited.add(newPos.hashCode());
            }
            else
            {
                // if it is not a junction, we just re-use the existing list
                visited=other.visited;
            }
        }

        private boolean isJunction(CharMatrix trail, Position newPos)
        {
            int free=0;
            free+=trail.at(newPos.updated(Direction.UP))=='#'?0:1;
            free+=trail.at(newPos.updated(Direction.DOWN))=='#'?0:1;
            free+=trail.at(newPos.updated(Direction.LEFT))=='#'?0:1;
            free+=trail.at(newPos.updated(Direction.RIGHT))=='#'?0:1;
            return free>2;
        }

        @Override
        public String calculateStateKey()
        {
            return null;
        }

        @Override
        public boolean betterThan(SearchState other)
        {
            return dist>((Trail)other).dist;
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
            addTrail(trails, currentState, currentState.pos.updated(Direction.UP));
            addTrail(trails, currentState, currentState.pos.updated(Direction.DOWN));
            addTrail(trails, currentState, currentState.pos.updated(Direction.LEFT));
            addTrail(trails, currentState, currentState.pos.updated(Direction.RIGHT));
            return trails;
        }

        private void addTrail(List<Trail> trails, Trail currentState, Position newPos)
        {
            // a junction we have seen already
            if (currentState.visited.contains(newPos.hashCode()))
                return;
            // a wall
            if (trail.at(newPos)=='#')
                return;
            // do not go back (since we do not store all positions)
            if (currentState.lastPos.equals(newPos))
                return;
            trails.add(new Trail(currentState, newPos, trail));
        }

        @Override
        public boolean reachedTarget(Trail currentState)
        {
            final boolean done = currentState.pos.equals(end);
            if (done)
            {
                if (currentState.dist>longestPath)
                {
                    longestPath=currentState.dist;
                    System.out.println("new longest path: "+longestPath);
                }
            }
            return done;
        }

        @Override
        public boolean canPruneBranch(Trail currentState)
        {
            //we cannot prune branches - until we fill up the maze there could always be a longer path
            return false;
        }

        @Override
        public Comparator<Trail> getComparator()
        {
            // prefer going away from the target, to get longer paths first
            return (state1, state2) -> -Integer.compare(state1.pos.dist(end), state2.pos.dist(end));
        }
    }
}
