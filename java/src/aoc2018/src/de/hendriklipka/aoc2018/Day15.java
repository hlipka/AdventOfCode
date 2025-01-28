package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.AStarSearch;
import de.hendriklipka.aoc.search.ReversedCharArrayWorld;

import java.io.IOException;
import java.util.*;

import static de.hendriklipka.aoc.Direction.*;

public class Day15 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day15().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final var attackPowerElves = 3;

        CharMatrix cave=data.getLinesAsCharMatrix('.');
        Map<Position, Integer> goblins=new HashMap<>();
        Map<Position, Integer> elves=new HashMap<>();
        cave.allMatchingPositions('G').forEach(p-> goblins.put(p, 200));
        cave.allMatchingPositions('E').forEach(p-> elves.put(p, 200));

        final var rounds = runCombatSimulation(cave, elves, goblins, attackPowerElves);

        final var goblinsSum = goblins.values().stream().mapToInt(v -> v).sum();
        final var elvesSum = elves.values().stream().mapToInt(v -> v).sum();
        return rounds * (goblinsSum + elvesSum);
    }

    private int runCombatSimulation(final CharMatrix cave, final Map<Position, Integer> elves, final Map<Position, Integer> goblins, final int attackPowerElves)
    {
        int rounds=0;
        boolean finished=false;
        while (!finished)
        {
            Set<Position> handledThisRound=new HashSet<>();
            // 'reading' order
            for (int row = 0; row < cave.rows(); row++)
            {
                for (int col = 0; col < cave.cols(); col++)
                {
                    final var position = new Position(row, col);
                    // skip unit we handled in this round (which moved down or to the right)
                    if (handledThisRound.contains(position))
                        continue;
                    // when we have a persons, handle its turn
                    char c= cave.at(row, col);
                    // check whether the current unit would find a target
                    // we do this here so we know that there is yet another person to be handled
                    // (because this makes the current round incomplete)
                    // when this is the last person, and the fight would be over afterward,
                    // we complete this round and end the fight at the start of the next round
                    if (c=='G')
                    {
                        if (elves.isEmpty())
                        {
                            finished = true;
                            break;
                        }
                        handledThisRound.add(moveGoblin(cave, position, goblins, elves));
                    }
                    else if (c=='E')
                    {
                        if (goblins.isEmpty())
                        {
                            finished = true;
                            break;
                        }
                        handledThisRound.add(moveElf(cave, position, elves, goblins, attackPowerElves));
                    }
                } // cols
                if (finished)
                {
                    break;
                }
            } // rows
            if (!finished)
            {
                rounds++;
            }
        }
        return rounds;
    }

    private Position moveElf(CharMatrix cave, final Position position, final Map<Position, Integer> elves, final Map<Position, Integer> goblins,
                             final int attackPower)
    {
        Position newPos= moveUnit(cave, position, 'G');
        if (!newPos.equals(position))
        {
            cave.set(position, '.');
            cave.set(newPos, 'E');
            int hp=elves.get(position);
            elves.remove(position);
            elves.put(newPos, hp);
        }
        doAttack(cave, newPos, 'G', goblins, attackPower);
        return newPos;
    }

    private Position moveGoblin(CharMatrix cave, final Position position, final Map<Position, Integer> goblins, final Map<Position, Integer> elves)
    {
        Position newPos = moveUnit(cave, position, 'E');
        if (!newPos.equals(position))
        {
            cave.set(position,'.');
            cave.set(newPos, 'G');
            int hp = goblins.get(position);
            goblins.remove(position);
            goblins.put(newPos, hp);
        }
        doAttack(cave, newPos, 'E', elves, 3);
        return newPos;
    }

    private Position moveUnit(final CharMatrix cave, final Position pos, final char targetType)
    {
        // do not move if we can attack right away
        for (Direction d : Direction.values())
        {
            if (cave.at(pos.updated(d))==targetType)
                return pos;
        }

        // first, find out which targets the current unit could actually reach
        // we treat anything not being '.' as 'cannot reach'
        AStarSearch search=new AStarSearch(new ReversedCharArrayWorld(cave, pos, new Position(0, 0), '.'));
        search.findPath();
        final List<Position> potentialTargetPlaces=new ArrayList<>();
        // look at all free fields, in reading order - these could be our targets
        for (Position targetSquare: cave.allMatchingPositions('.'))
        {
            // check whether the square has a target unit next to it (making it a valid target square)
            for (Direction d: Direction.values())
            {
                if (cave.at(targetSquare.updated(d)) == targetType)
                {
                    // if it has - is it closer than the previous one?
                    int l = search.getPathLength(targetSquare);
                    if (l != Integer.MAX_VALUE)
                    {
                        potentialTargetPlaces.add(targetSquare);
                    }
                    // in any case, we are done
                    break;
                }
            }
        }
        // no reachable targets
        if (potentialTargetPlaces.isEmpty())
        {
            return pos;
        }
        // find the closest targets, select the one to move to
        // potentialTargetPlaces are already in reading order
        Position chosenTarget=null;
        int bestDist=10000;
        for (Position targetPlace: potentialTargetPlaces)
        {
            int dist=search.getPathLength(targetPlace);
            // when costs are the same, keep the existing one since its preferred (first in reading order)
            if (dist<bestDist)
            {
                bestDist=dist;
                chosenTarget=targetPlace;
            }
        }

        // from there, do a reverse search to find the cheapest path to our target
        search = new AStarSearch(new ReversedCharArrayWorld(cave, chosenTarget, pos, '.'));
        search.findPath();

        Direction d=UP;
        int bestPath= search.getPathLength(pos.updated(UP));
        if (search.getPathLength(pos.updated(LEFT))<bestPath)
        {
            d = LEFT;
            bestPath= search.getPathLength(pos.updated(LEFT));
        }
        if (search.getPathLength(pos.updated(RIGHT))<bestPath)
        {
            d = RIGHT;
            bestPath= search.getPathLength(pos.updated(RIGHT));
        }
        if (search.getPathLength(pos.updated(DOWN))<bestPath)
        {
            d = DOWN;
        }

        return pos.updated(d);
    }

    private void doAttack(final CharMatrix cave, final Position currentUnit, final char targetType, final Map<Position, Integer> targetUnits,
                          final int attackPower)
    {
        int hp=201;
        Position chosenTarget=null;
        if (cave.at(currentUnit.updated(UP))==targetType)
        {
            chosenTarget=currentUnit.updated(UP);
            hp=targetUnits.get(chosenTarget);
        }
        if (cave.at(currentUnit.updated(LEFT))==targetType && targetUnits.get(currentUnit.updated(LEFT))<hp)
        {
            chosenTarget=currentUnit.updated(LEFT);
            hp = targetUnits.get(chosenTarget);
        }
        if (cave.at(currentUnit.updated(RIGHT))==targetType && targetUnits.get(currentUnit.updated(RIGHT)) < hp)
        {
            chosenTarget=currentUnit.updated(RIGHT);
            hp = targetUnits.get(chosenTarget);
        }
        if (cave.at(currentUnit.updated(DOWN))==targetType && targetUnits.get(currentUnit.updated(DOWN)) < hp)
        {
            chosenTarget=currentUnit.updated(DOWN);
        }
        if (null!=chosenTarget)
        {
            int level=targetUnits.get(chosenTarget);
            level-= attackPower;
            if (level>0)
            {
                targetUnits.put(chosenTarget, level);
            }
            else
            {
                targetUnits.remove(chosenTarget);
                cave.set(chosenTarget, '.');
            }
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        var attackPowerElves = 3;
        boolean elvesWon=false;
        int result=0;
        // just brute force increasing strength
        while (!elvesWon)
        {
            CharMatrix cave = data.getLinesAsCharMatrix('.');
            Map<Position, Integer> goblins = new HashMap<>();
            Map<Position, Integer> elves = new HashMap<>();
            cave.allMatchingPositions('G').forEach(p -> goblins.put(p, 200));
            cave.allMatchingPositions('E').forEach(p -> elves.put(p, 200));
            int elfCount=elves.size();

            final var rounds = runCombatSimulation(cave, elves, goblins, attackPowerElves);

            if (elfCount!=elves.size())
            {
                attackPowerElves++;
                continue;
            }

            final var goblinsSum = goblins.values().stream().mapToInt(v -> v).sum();
            final var elvesSum = elves.values().stream().mapToInt(v -> v).sum();

            result = rounds * (goblinsSum + elvesSum);
            elvesWon=true;
        }
        return result;
    }
}
