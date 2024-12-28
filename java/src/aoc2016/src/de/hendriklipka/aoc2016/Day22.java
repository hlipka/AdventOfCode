package de.hendriklipka.aoc2016;

import de.hendriklipka.aoc.*;
import de.hendriklipka.aoc.matrix.ObjectMatrix;
import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

import static de.hendriklipka.aoc.Direction.*;

public class Day22 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day22().doPuzzle(args);
    }

    private static final Position targetPos = new Position(0, 0);

    @Override
    protected Object solvePartA() throws IOException
    {
        List<Node> nodes=data.getLines().stream().map(Node::new).toList();
        return AocCollectionUtils.getOrderedPairs(nodes).stream().filter(Day22::isViable).count();
    }

    private static boolean isViable(Pair<Node, Node> nodePair)
    {
        Node from=nodePair.getLeft();
        Node to=nodePair.getRight();
        return from._used != 0 && from._used <= to._avail;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<Node> nodes = data.getLines().stream().map(Node::new).toList();
        int cols=nodes.stream().mapToInt(n->n._x).max().orElse(0);
        int rows=nodes.stream().mapToInt(n->n._y).max().orElse(0);
        final var world = new DiskSpaceWorld(nodes, rows+1, cols+1);
        BestFirstSearch<DiskSpaceWorld, DiskSpaceState> search = new BestFirstSearch<>(world);
        search.search();
        // 206 and 207 are too low
        return world._bestMoves;
    }

    private static boolean canMoveFreeSpaceTo(final ObjectMatrix<Node> nodes, final Position from, final Position to)
    {
        if (!nodes.in(from) || !nodes.in(to))
        {
            return false;
        }
        Node fromNode = nodes.at(from);
        Node toNode = nodes.at(to);
        if (toNode._used==0) // cannot move the free disk to an already free disk
            return false;
        // the size of the new free disk must fit into the current free disk
        return toNode._used <= fromNode._size;
    }

    private static class DiskSpaceWorld implements SearchWorld<DiskSpaceState>
    {
        private final DiskSpaceState initialState;
        int _bestMoves = Integer.MAX_VALUE;

        public DiskSpaceWorld(final List<Node> nodeList, final int rows, final int cols)
        {
            ObjectMatrix<Node> nodes = new ObjectMatrix<>(rows, cols);
            Position freeDisk = null;
            for (Node node : nodeList)
            {
                nodes.set(node._y, node._x, node);
                if (node._used==0)
                {
                    freeDisk = new Position(node._y, node._x);
                }
            }

            initialState = new DiskSpaceState(nodes, new Position(0, nodes.cols() - 1), 0, freeDisk);
        }

        @Override
        public DiskSpaceState getFirstState()
        {
            return initialState;
        }

        @Override
        public List<DiskSpaceState> calculateNextStates(final DiskSpaceState currentState)
        {
            final List<DiskSpaceState> result = new ArrayList<>();

            // if the free space is adjacent to the goal data (and not at the right), we add new states to move the goal data into the free space
            // this should always work
            if (currentState._freeDisk.updated(RIGHT).equals(currentState._goalData))
            {
                result.add(currentState.copyWhenMoving(currentState._goalData));
            }
            else if (currentState._freeDisk.updated(UP).equals(currentState._goalData))
            {
                result.add(currentState.copyWhenMoving(currentState._goalData));
                result.add(currentState.copyWhenMoving(currentState._freeDisk.updated(LEFT)));
            }
            else if (currentState._freeDisk.updated(DOWN).equals(currentState._goalData))
            {
                result.add(currentState.copyWhenMoving(currentState._goalData));
                result.add(currentState.copyWhenMoving(currentState._freeDisk.updated(LEFT)));
            }
            else // try all positions and rely on the cost ordering
            {
                for (Direction dir : Direction.values())
                {
                    Position p= currentState._freeDisk.updated(dir);
                    if (!p.equals(currentState._goalData) && canMoveFreeSpaceTo(currentState._nodes, currentState._freeDisk, p))
                    {
                        result.add(currentState.copyWhenMoving(p));
                    }
                }
            }

            return result;
        }

        @Override
        public boolean reachedTarget(final DiskSpaceState currentState)
        {
            final var done = currentState._goalData.equals(targetPos);
            if (done && currentState._moves<_bestMoves)
            {
                System.out.println("new best result: "+currentState._moves);
                _bestMoves=currentState._moves;
            }
            return done;
        }

        @Override
        public boolean canPruneBranch(final DiskSpaceState currentState)
        {
            return currentState._moves>_bestMoves;
        }

        @Override
        public Comparator<DiskSpaceState> getComparator()
        {
            return Comparator.comparingInt((DiskSpaceState value) -> (Integer)(value.getCurrentCost()));
        }
    }

    private static class DiskSpaceState implements SearchState
    {
        private final ObjectMatrix<Node> _nodes;
        Position _goalData;
        Position _freeDisk;
        int _moves;

        public DiskSpaceState(final ObjectMatrix<Node> nodes, Position goalData, int moves, final Position freeDisk)
        {
            _nodes = nodes;
            _goalData = goalData;
            _moves = moves;
            _freeDisk = freeDisk;
        }

        @Override
        public String calculateStateKey()
        {
            return _goalData.row +
                   "," +
                   _goalData.col +
                   ";" +
                   _freeDisk.row +
                   "," +
                   _freeDisk.col;
        }

        @Override
        public boolean betterThan(final Object otherCost)
        {
            return (Integer)otherCost>(Integer)getCurrentCost();
        }

        @Override
        public Object getCurrentCost()
        {
            final Position left=_goalData.updated(LEFT);
            // we need to move the goal data to the target position as well, so it adds to the minimum costs
            return _moves // moves until now
                   +_goalData.dist(targetPos) // how far we need to move the target
                   + _freeDisk.dist(left); // how far away the free disk is from being left to the goal data
        }

        // move the free disk to the target position
        // (so data moves in the other direction)
        DiskSpaceState copyWhenMoving(Position freeSpaceTo)
        {
            // create copy of the state
            ObjectMatrix<Node> newNodes = _nodes.copyOf();

            // in the copy, move the data to the target node
            Node dataFromOldNode= newNodes.at(freeSpaceTo); // this currently has the data, and will be free
            Node dataToOldNode= newNodes.at(_freeDisk); // this is currently free, and will have the data
            Node dataToNewNode=new Node(dataToOldNode._size, dataFromOldNode._used);
            Node dataFromNewNode = new Node(dataFromOldNode._size, 0);
            newNodes.set(_freeDisk, dataToNewNode); // the old free disk now has the data
            newNodes.set(freeSpaceTo, dataFromNewNode); // and the free space was now moved

            Position newGoal=_goalData;
            // update data when it was moved
            if (freeSpaceTo.equals(_goalData))
            {
                newGoal=_freeDisk;
            }

            return new DiskSpaceState(newNodes, newGoal, _moves + 1, freeSpaceTo);
        }
    }

    private static class Node
    {
        int _x, _y, _size, _used, _avail;

        public Node(String line)
        {
            final List<String> parts = AocParseUtils.getGroupsFromLine(line, "/dev/grid/node-x(\\d+)-y(\\d+)\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)%");
            _x = Integer.parseInt(parts.get(0));
            _y = Integer.parseInt(parts.get(1));
            _size = Integer.parseInt(parts.get(2));
            _used = Integer.parseInt(parts.get(3));
            _avail = Integer.parseInt(parts.get(4));
        }

        public Node(int size, int used)
        {
            _size = size;
            _used = used;
            _avail = size-used;
        }

        @Override
        public String toString()
        {
            return "(" +
                   _x +
                   "," + _y +
                   ")=" + _used;
        }
    }
}
