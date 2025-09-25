package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.CardinalDirection;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.search.AStarPrioritizedSearch;
import de.hendriklipka.aoc.search.AStarSearch;
import de.hendriklipka.aoc.search.CharArrayWorld;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Day15 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day15().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final var ship = getShip();
        ship.print();
        Position target=ship.allMatchingPositions('D').getFirst();
        ship.set(target, ' ');
        final var start = ship.allMatchingPositions('R').getFirst();;
        ship.set(start, ' ');
        AStarPrioritizedSearch search=new AStarPrioritizedSearch(new CharArrayWorld(ship, start, target, '#'));
        return search.findPath();
    }

    private CharMatrix getShip() throws IOException
    {
        List<String> code = new ArrayList<>(data.getFirstLineWords(","));
        // change the 'jump if true' to read in immediate mode to force it to never jump
        // this makes the robot go through walls, so we can easily map the whole ship
        code.set(224, "1106");
        IntCode intCode = IntCode.fromStringList(code);
        // we also read the start position from the program memory
        final var startPos = new Position(intCode.get(1035), intCode.get(1034));

        CharMatrix ship=CharMatrix.filledMatrix(41, 41, '*', '#');
        Mapper mapper = new Mapper(ship, startPos);
        intCode.setDoOutput(mapper);
        intCode.setDoInput(mapper);
        intCode.execute();
        ship.set(startPos, 'R');
        return ship;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CharMatrix ship = getShip();
        Position target = ship.allMatchingPositions('D').getFirst();
        ship.set(target, ' ');
        final var start = ship.allMatchingPositions('R').getFirst();
        ;
        ship.set(start, ' ');
        // we now need to start at the target location, since the oxygen spreads from there
        final var world = new CharArrayWorld(ship, target, start, '#');
        // this does essentially a flood-fill
        AStarSearch search = new AStarSearch(world);
        search.findPath();
        // so we can search for the largest non-default value and get our result
        return search.getLargestValue();
    }

    private static class Mapper implements Consumer<Integer>, Supplier<Integer>
    {
        private enum MapMode
        {
            HOME, MAP_RIGHT, MAP_LEFT, DONE
        }


        private final CharMatrix _ship;
        // from the code we know that we start at 21,21
        Position pos;
        MapMode mode=MapMode.HOME;

        Position MAP_END=new Position(40, 40);

        public Mapper(final CharMatrix ship, final Position startPos)
        {
            _ship=ship;
            pos = startPos;
        }

        @Override
        public void accept(final Integer value)
        {
            // just store everything
            _ship.set(pos, switch(value)
            {
                case 0->'#';
                case 1->' ';
                case 2->'D';
                default->throw new RuntimeException();
            });
        }

        @Override
        public Integer get()
        {
            if (mode==MapMode.HOME)
            {
                if (pos.row!=0)
                {
                    pos=pos.updated(CardinalDirection.N);
                    return 1;
                }
                if (pos.col!=0)
                {
                    pos=pos.updated(CardinalDirection.W);
                    return 3;
                }
                System.out.println("finished homing");
                mode=MapMode.MAP_RIGHT;
            }
            if (pos.equals(MAP_END))
                return 0;
            if (mode==MapMode.MAP_RIGHT)
            {
                if (pos.col!=40)
                {
                    pos=pos.updated(CardinalDirection.E);
                    return 4;
                }
                else
                {
                    pos = pos.updated(CardinalDirection.S);
                    mode=MapMode.MAP_LEFT;
                    return 2;
                }
            }
            else  if (mode==MapMode.MAP_LEFT)
            {
                if (pos.col != 0)
                {
                    pos = pos.updated(CardinalDirection.W);
                    return 3;
                }
                else
                {
                    pos = pos.updated(CardinalDirection.S);
                    mode = MapMode.MAP_RIGHT;
                    return 2;
                }
            }
            return 0;
        }
    }
}
