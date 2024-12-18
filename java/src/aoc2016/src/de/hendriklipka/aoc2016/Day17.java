package de.hendriklipka.aoc2016;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day17 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day17().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        String code=data.getLines().get(0);
        final var doorWorld = new DoorWorld(code);
        BestFirstSearch<DoorWorld, DoorState> search = new BestFirstSearch<>(doorWorld);
        search.search();

        return doorWorld.bestPath;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        String code = data.getLines().get(0);
        final var doorWorld = new DoorWorld(code);
        BestFirstSearch<DoorWorld, DoorState> search = new BestFirstSearch<>(doorWorld);
        search.search();

        return doorWorld.longestPath.length();
    }

    private static class DoorWorld implements SearchWorld<DoorState>
    {
        private final String _code;
        private String bestPath="";
        private String longestPath="";

        public DoorWorld(final String code)
        {
            _code=code;
        }

        @Override
        public DoorState getFirstState()
        {
            final var doorState = new DoorState("", new Position(0, 0));
            doorState.setDoorState(_code);
            return doorState;
        }

        @Override
        public List<DoorState> calculateNextStates(final DoorState currentState)
        {
            final List<DoorState> states = new ArrayList<>();
            for (Direction dir : Direction.values())
            {
                Position next=currentState._position.updated(dir);
                if (isRoom(next) && currentState.isOpen(dir))
                {
                    DoorState nextState=new DoorState(currentState._path+dir.name().charAt(0), next);
                    nextState.setDoorState(_code);
                    states.add(nextState);
                }
            }
            return states;
        }

        private boolean isRoom(final Position next)
        {
            return next.row>=0 && next.col>=0 && next.row<4 && next.col<4;
        }

        @Override
        public boolean reachedTarget(final DoorState currentState)
        {
            final var done = currentState._position.row == 3 && currentState._position.col == 3;
            if (done)
            {
                if (bestPath.isEmpty() || currentState._path.length() < bestPath.length())
                {
                    bestPath = currentState._path;
                }
                if (currentState._path.length() > longestPath.length())
                {
                    longestPath = currentState._path;
                }
            }
            return done;
        }

        @Override
        public boolean canPruneBranch(final DoorState currentState)
        {
            // we never prune so we also get to see the longest path (this is still fast enough)
            return false;
        }

        @Override
        public Comparator<DoorState> getComparator()
        {
            return Comparator.comparingInt(DoorState::pathLength);
        }
    }

    private static class DoorState implements SearchState
    {
        private final String _path;
        private final Position _position;
        char[] doors=new char[4];
        private String hash;

        public DoorState(final String path, final Position position)
        {
            _path = path;
            _position = position;
        }

        public void setDoorState(String code)
        {
            hash = DigestUtils.md5Hex(code+_path).toLowerCase();
            for (int i=0;i<4;i++)
            {
                char c=hash.charAt(i);
                if (c== 'b' || c== 'c'  || c=='d' || c=='e' || c =='f')
                    doors[i]='o';
                else
                    doors[i]='x';
            }
        }

        public boolean isOpen(Direction dir)
        {
            return switch (dir)
            {
                case UP -> doors[0] == 'o';
                case DOWN -> doors[1] == 'o';
                case LEFT -> doors[2] == 'o';
                case RIGHT -> doors[3] == 'o';
            };
        }

        @Override
        public String calculateStateKey()
        {
            // we cannot really use a hash to rule out duplicates - we might need to revisit rooms
            // even with the same combination of open doors, since due to the hashing the next step might be different
            return hash+","+_position.row+","+_position.col;
        }

        @Override
        public boolean betterThan(final Object otherCost)
        {
            return _path.length()<(Integer)otherCost;
        }

        @Override
        public Object getCurrentCost()
        {
            return _path.length();
        }

        public int pathLength()
        {
            return _path.length();
        }
    }
}
