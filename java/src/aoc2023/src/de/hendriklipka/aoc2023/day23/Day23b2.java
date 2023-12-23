package de.hendriklipka.aoc2023.day23;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.DepthFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;

import java.io.IOException;
import java.util.*;

/**
 * Make this a graph problem
 * - add start and end as graph nodes
 * - from the start, find the first junction
 * - store junction as graph node, store length at edge
 * - use junction as a new start node
 * - find next junctions from there
 * - when the junction is the end, stop this path there
 * now we have a graph of paths between junctions, we just fund the path
 * store the lengths as negative values, so we can look for the shortest path...
 */
public class Day23b
{
    public static void main(String[] args)
    {
        try
        {
            CharMatrix trail= AocParseUtils.getLinesAsCharMatrix("2023", "day23", '#');
            Position start=new Position(0, 1);
            Position end=new Position(trail.rows()-1, trail.cols()-2);
            TrailWorld world=new TrailWorld(trail, start, end);
            DepthFirstSearch<TrailWorld, Trail> dfs = new DepthFirstSearch<>(world);
            dfs.search();
            System.out.println("6194 is too low");
            System.out.println(world.longestPath);
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
        public Set<Position> visited =new HashSet<>();

        public Trail(Position pos)
        {
            this.pos=pos;
            this.lastPos = pos.updated(Direction.UP);
        }

        public Trail(Trail other, Position newPos, CharMatrix trail)
        {
            dist=other.dist+1;
            lastPos=other.pos;
            // we only store junctions, because the long path between them do not have information
            if (isJunction(trail, newPos))
            {
                visited.add(other.pos);
                visited.addAll(other.visited);
            }
            else
            {
                // if its not a junction, we just re-use the existing list
                visited=other.visited;
            }
            pos=newPos;
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
            StringBuilder sb=new StringBuilder();
            for (Position pos: visited)
                sb.append(pos.row).append('-').append(pos.col).append('/');
            sb.append(pos.row).append('-').append(pos.col);
            return sb.toString();
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
            if (currentState.visited.contains(newPos))
                return;
            if (trail.at(newPos)=='#')
                return;
            // do not go back
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
