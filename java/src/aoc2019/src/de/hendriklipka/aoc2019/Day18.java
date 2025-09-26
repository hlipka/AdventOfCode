package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.DiagonalDirections;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class Day18 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day18().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        CharMatrix vault = CharMatrix.fromStringList(data.getLines(), '#');
        final var world = new VaultWorld(vault, vault.allMatchingPositions('@').getFirst());
        BestFirstSearch<VaultWorld, VaultState> search = new BestFirstSearch<>(world);
        search.search();
        return world.moves;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CharMatrix vault = CharMatrix.fromStringList(data.getLines(), '#');
        final Position oldStartPos = vault.allMatchingPositions('@').getFirst();
        vault.set(oldStartPos, '#');
        vault.set(oldStartPos.updated(Direction.UP), '#');
        vault.set(oldStartPos.updated(Direction.DOWN), '#');
        vault.set(oldStartPos.updated(Direction.LEFT), '#');
        vault.set(oldStartPos.updated(Direction.RIGHT), '#');
        vault.set(oldStartPos.updated(DiagonalDirections.LEFT_UP), '@');
        vault.set(oldStartPos.updated(DiagonalDirections.RIGHT_UP), '@');
        vault.set(oldStartPos.updated(DiagonalDirections.LEFT_DOWN), '@');
        vault.set(oldStartPos.updated(DiagonalDirections.RIGHT_DOWN), '@');

        final var world = new Vault4World(vault, vault.allMatchingPositions('@'));
        BestFirstSearch<Vault4World, Vault4State> search = new BestFirstSearch<>(world);
        search.search();
        return world.moves;
    }

    private static class VaultWorld implements SearchWorld<VaultState>
    {
        private final CharMatrix _vault;
        private final Position _first;
        private final ArrayList<Character> allKeys;
        public int moves=1000000;
        Map<Character, Position> _keys=new HashMap<>();
        Map<Character, Position> _doors=new HashMap<>();

        Map<String, Integer> pathCache=new HashMap<>();

        public VaultWorld(final CharMatrix vault, final Position first)
        {
            _vault=vault;
            _first = first;
            vault.allPositions().forEach(p->
            {
                char c=vault.at(p);
                if (c>='a' && c<='z')
                {
                    _keys.put(c, p);
                    _vault.set(p, '.'); // keys are open to move through
                }
                else if (c>='A' && c<='Z')
                {
                    _doors.put(c, p);
                    _vault.set(p, '#'); // doors count as blocks
                }

            });
            allKeys= new ArrayList<>(_keys.keySet());
            allKeys.sort(Character::compareTo);
        }

        @Override
        public VaultState getFirstState()
        {
            return new  VaultState(0, _first, allKeys, _vault);
        }

        @Override
        public List<VaultState> calculateNextStates(final VaultState currentState)
        {
            // take the current vault, and flood-fill from the current position
            AStarSearch search = null;

            final List<VaultState> newStates = new ArrayList<>();

            // from there, find all reachable keys
            for (char k: currentState._missingKeys)
            {
                Position keyPosition = _keys.get(k);
                String key = getKeyForRobot(currentState._position, currentState._missingKeys)+"_"+k;
                Integer length= pathCache.get(key);
                if (length==null)
                {
                    if (null==search)
                    {
                        search = new AStarSearch(new CharArrayWorld(currentState._vault, currentState._position, new Position(0, 0), '#'));
                        search.findPath();
                    }
                    length = search.getPathLength(keyPosition);
                }
                if (length<100000)
                {
                    pathCache.put(key, length);
                    // for each, create a new world with the door for that key removed
                    CharMatrix v=currentState._vault.copyOf();
                    // remove the door
                    final var door = Character.toUpperCase(k);
                    if (_doors.containsKey(door))
                    {
                        v.set(_doors.get(door), ' ');
                    }
                    final ArrayList<Character> newKeys = new ArrayList<>(currentState._missingKeys);
                    newKeys.remove((Character)k);
                    newStates.add(new VaultState(currentState._moves+length, keyPosition, newKeys, v));
                }
            }

            // and run from there
            return newStates;
        }

        @Override
        public boolean reachedTarget(final VaultState currentState)
        {
            // we are done when we have all keys
            boolean done=currentState._missingKeys.isEmpty();
            if (done && currentState._moves<moves)
            {
                moves=currentState._moves;
            }
            return done;
        }

        @Override
        public boolean canPruneBranch(final VaultState currentState)
        {
            return currentState._moves>=moves;
        }

        @Override
        public Comparator<VaultState> getComparator()
        {
            return Comparator.comparingInt(VaultState::getMoves);
        }
    }

    private record VaultState(int _moves, Position _position, List<Character> _missingKeys, CharMatrix _vault) implements SearchState
    {
        @Override
        public String calculateStateKey()
        {
            // state is position plus remaining keys (sorted)
            return getKeyForRobot(_position, _missingKeys);
        }

        @Override
        public boolean betterThan(final Object otherCost)
        {
            return _moves < (Integer)otherCost;
        }

        @Override
        public Object getCurrentCost()
        {
            return _moves;
        }

        public int getMoves()
        {
            return _moves;
        }

    }

    private static class Vault4World implements SearchWorld<Vault4State>
    {
        private final CharMatrix _vault;
        private final List<Position> _first;
        private final ArrayList<Character> allKeys;
        public int moves=1000000;
        Map<Character, Position> _keys=new HashMap<>();
        Map<Character, Position> _doors=new HashMap<>();

        Map<String, Integer> pathCache = new HashMap<>();

        public Vault4World(final CharMatrix vault, final List<Position> first)
        {
            _vault=vault;
            _first = first;
            vault.allPositions().forEach(p->
            {
                char c=vault.at(p);
                if (c>='a' && c<='z')
                {
                    _keys.put(c, p);
                    _vault.set(p, '.'); // keys are open to move through
                }
                else if (c>='A' && c<='Z')
                {
                    _doors.put(c, p);
                    _vault.set(p, '#'); // doors count as blocks
                }

            });
            allKeys= new ArrayList<>(_keys.keySet());
            allKeys.sort(Character::compareTo);
        }

        @Override
        public Vault4State getFirstState()
        {
            return new  Vault4State(0, _first, allKeys, _vault);
        }

        @Override
        public List<Vault4State> calculateNextStates(final Vault4State currentState)
        {
            // take the current vault, and flood-fill from all the current positions
            List<AStarSearch> search = new ArrayList<>(10);
            search.add(null);
            search.add(null);
            search.add(null);
            search.add(null);

            final List<Vault4State> newStates = new ArrayList<>();

            // from there, find all reachable keys
            for (char k: currentState._missingKeys)
            {
                Position keyPosition = _keys.get(k);
                for (int i=0;i<currentState._positions.size();i++)
                {
                    String key = getKeyForRobot(currentState._positions.get(i), currentState._missingKeys)+"_"+k;
                    // check whether we have a cached value for that robot
                    Integer length = pathCache.get(key);
                    if (length == null)
                    {
                        AStarSearch s = search.size()>i?search.get(i):null;
                        if (null == s)
                        {
                            s = new AStarSearch(new CharArrayWorld(currentState._vault, currentState._positions.get(i), new Position(0, 0), '#'));
                            search.set(i, s);
                            s.findPath();
                        }
                        length = s.getPathLength(keyPosition);
                    }

                    if (length < 100000)
                    {
                        pathCache.put(key, length);
                        // for each, create a new world with the door for that key removed
                        CharMatrix v = currentState._vault.copyOf();
                        // remove the door
                        final var door = Character.toUpperCase(k);
                        if (_doors.containsKey(door))
                        {
                            v.set(_doors.get(door), ' ');
                        }
                        // found a key
                        final ArrayList<Character> newKeys = new ArrayList<>(currentState._missingKeys);
                        newKeys.remove((Character) k);
                        // change the one moved bot
                        final ArrayList<Position> newPos=new ArrayList<>(currentState._positions);
                        newPos.set(i, keyPosition);
                        newStates.add(new Vault4State(currentState._moves + length, newPos, newKeys, v));
                    }
                }
            }

            // and run from there
            return newStates;
        }

        @Override
        public boolean reachedTarget(final Vault4State currentState)
        {
            // we are done when we have all keys
            boolean done=currentState._missingKeys.isEmpty();
            if (done && currentState._moves<moves)
            {
                moves=currentState._moves;
            }
            return done;
        }

        @Override
        public boolean canPruneBranch(final Vault4State currentState)
        {
            return currentState._moves>=moves;
        }

        @Override
        public Comparator<Vault4State> getComparator()
        {
            return Comparator.comparingInt(Vault4State::getMoves);
        }
    }

    private record Vault4State(int _moves, List<Position> _positions, List<Character> _missingKeys, CharMatrix _vault) implements SearchState
    {
        @Override
        public String calculateStateKey()
        {
            // state is positions plus remaining keys (sorted)
            return StringUtils.join(_positions.stream().map(p-> p.row + "," + p.col).toList(),',') + ";" + StringUtils.join(_missingKeys);
        }

        @Override
        public boolean betterThan(final Object otherCost)
        {
            return _moves < (Integer)otherCost;
        }

        @Override
        public Object getCurrentCost()
        {
            return _moves;
        }

        public int getMoves()
        {
            return _moves;
        }

    }

    private static String getKeyForRobot(final Position position, final List<Character> missingKeys)
    {
        return position.row + "," + position.col + "," + StringUtils.join(missingKeys);
    }
}
