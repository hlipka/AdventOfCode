package de.hendriklipka.aoc.search;

import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.IntMatrix;

import java.util.*;

/**
 * Class for a generic A* search in an array-based world
 */
public class AStarPrioritizedSearch
{
    private final ArrayWorld world;
    private final Position end;
    private final IntMatrix path;

    PriorityQueue<State> queue;

    State lastState;

    boolean foundTarget=false;

    public AStarPrioritizedSearch(ArrayWorld world)
    {
        this.world = world;
        end = new Position(world.getEndY(), world.getEndX());
        path = new IntMatrix(world.getHeight(), world.getWidth(), Integer.MAX_VALUE);
        queue = new PriorityQueue<>(10000, new StateComparator());
    }

    public int findPath()
    {
        Position start = new Position(world.getStartY(), world.getStartX());
        queue.add(new State(start, 0, null));

        doFindPath();
        return path.at(end);
    }

    public List<Position> getPath()
    {
        final List<Position> pathPos = new ArrayList<>(path.at(end));
        State currentState = lastState;
        while (currentState != null)
        {
            pathPos.add(currentState.pos);
            currentState = currentState.parent;
        }
        // the list started from the last element
        Collections.reverse(pathPos);
        return pathPos;
    }

    private void doFindPath()
    {
        while (!queue.isEmpty())
        {
            State state = queue.poll();
            Position current = state.pos;
            int oldLength = path.at(current);
            if (state.cost < oldLength)
            {
                path.set(current, state.cost);
            }
            else
            {
                continue; // prune this branch
            }
            if (current.equals(end))
            {
                foundTarget=true;
                return; // stop at the first path
            }

            for (Direction dir: Direction.values())
            {
                Position target = current.updated(dir);
                if (world.canMoveTo(current.col, current.row, target.col, target.row))
                {
                    queue.add(new State(target, state.cost + 1, state));
                }
            }
        }
    }

    public boolean didFoundTarget()
    {
        return foundTarget;
    }

    private class State
    {
        private final State parent;
        public Position pos;
        public int cost;

        public State(final Position position, final int cost, State parent)
        {
            this.pos=position;
            this.cost=cost;
            this.parent=parent;
        }
    }

    private class StateComparator implements Comparator<State>
    {
        @Override
        public int compare(final State o1, final State o2)
        {
            return Integer.compare(o1.cost+ world.remainingCost(o1.pos.col, o1.pos.row), o2.cost+ world.remainingCost(o2.pos.col, o2.pos.row));
        }
    }
}
