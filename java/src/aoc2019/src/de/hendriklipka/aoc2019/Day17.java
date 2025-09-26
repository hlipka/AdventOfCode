package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Day17 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day17().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<Integer> code = data.getLineAsInteger(",");
        IntCode robot = IntCode.fromIntList(code);
        final var ship = getShip(robot);
        ship.print();
        return ship.allMatchingPositions('#').stream().filter(p->isCrossing(ship,p)).mapToInt(p->getAlign(p)).sum();
    }

    private static CharMatrix getShip(final IntCode robot)
    {
        List<StringBuilder> data=new ArrayList<>();
        data.add(new StringBuilder());
        robot.setDoOutput(value ->
        {
            if (10 == value)
            {
                data.add(new StringBuilder());
            }
            else
            {
                data.getLast().append((char) value.intValue());
            }
        });
        robot.execute();
        CharMatrix ship=CharMatrix.fromStringList(data.stream().map(s->s.toString().trim()).filter(s->!s.isEmpty()).toList(), ' ');
        return ship;
    }

    private int getAlign(final Position p)
    {
        return p.row*p.col;
    }

    private boolean isCrossing(final CharMatrix ship, final Position p)
    {
        return ship.at(p.updated(Direction.UP))=='#'
                && ship.at(p.updated(Direction.DOWN))=='#'
                && ship.at(p.updated(Direction.RIGHT))=='#'
                && ship.at(p.updated(Direction.LEFT))=='#';
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<Integer> code = data.getLineAsInteger(",");
        IntCode robot = IntCode.fromIntList(code);
        final var ship = getShip(robot);
        Position robotPos=ship.allMatchingPositions('^').getFirst();
        Direction dir=Direction.UP;
        List<String> path=new ArrayList<>();
        findPath(ship, robotPos, dir, path);
        ship.print();

        System.out.println(StringUtils.join(path,','));
        // look at the path and compress manually
        List<String> program = List.of("A,B,A,B,A,C,B,C,A,C\n",
                "R,4,L,10,L,10\n",
                "L,8,R,12,R,10,R,4\n",
                "L,8,L,8,R,10,R,4\n",
                "n\n");

        // reload, and set to program mode
        robot = IntCode.fromIntList(code);
        robot.set(0, 2);

        // convert to a char list
        StringBuilder sb=new StringBuilder();
        for (String line:program)
            sb.append(line);
        char[] instructions=sb.toString().toCharArray();
        robot.setDoInput(new Supplier<>()
        {
            int ofs = 0;

            @Override
            public Integer get()
            {
                return (int) instructions[ofs++];
            }
        });
        final int[] lastValue=new int[1];
        robot.setDoOutput(value ->
        {
            if (value>128)
            {
                lastValue[0]=value;
            }
            else
            {
                System.out.println((char)value.intValue());
            }
        });
        robot.execute();
        return lastValue[0];
    }

    private static void findPath(final CharMatrix ship, Position robotPos, Direction dir, final List<String> path)
    {
        int moveCount=0;
        while(true)
        {
            // go forward if we can
            if (ship.at(robotPos.updated(dir)) == '#')
            {
                moveCount++;
                robotPos = robotPos.updated(dir);
            }
            else // if not find out how to turn
            {
                if (0!=moveCount)
                    path.add(Integer.toString(moveCount));
                moveCount=0;
                if (ship.at(robotPos.updated(dir.left())) == '#')
                {
                    path.add("L");
                    dir = dir.left();
                }
                else if (ship.at(robotPos.updated(dir.right())) == '#')
                {
                    path.add("R");
                    dir = dir.right();
                }
                else
                {
                    System.out.println("don't know how to turn at " + robotPos + "/" + dir);
                    ship.set(robotPos, 'X');
                    break;
                }
            }
        }
    }
}
