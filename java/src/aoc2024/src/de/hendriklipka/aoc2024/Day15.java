package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day15 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day15().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<List<String>> config = data.getStringBlocks();
        CharMatrix warehouse=CharMatrix.fromStringList(config.get(0), '#');
        List<String> instructions = config.get(1);
        Position robot=warehouse.findFirst('@');
        warehouse.set(robot, '.'); // free up this space
        for (String inst: instructions)
        {
            for (Character c: inst.toCharArray())
            {
                robot = move(warehouse, c, robot);
            }
        }
        return warehouse.allMatchingPositions('O').stream().mapToInt(Day15::getGPS).sum();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<List<String>> config = data.getStringBlocks();
        CharMatrix warehouse=CharMatrix.fromStringList(config.get(0).stream().map(Day15::makeWide).toList(), '#');
        List<String> instructions = config.get(1);
        Position robot=warehouse.findFirst('@');
        warehouse.set(robot, '.'); // free up this space
        for (String inst: instructions)
        {
            for (Character c: inst.toCharArray())
            {
                robot = moveWide(warehouse, c, robot);
            }
        }
        return warehouse.allMatchingPositions('[').stream().mapToInt(Day15::getGPS).sum();
    }

    private static String makeWide(String line)
    {
        return line.replace("#","##").replace(".","..").replace("@","@.").replace("O","[]");
    }


    private Position moveWide(final CharMatrix warehouse, final Character c, Position robot)
    {
        Direction dir = switch (c)
        {
            case '<' -> Direction.LEFT;
            case '>' -> Direction.RIGHT;
            case '^' -> Direction.UP;
            case 'v' -> Direction.DOWN;
            default -> throw new IllegalArgumentException("invalid instruction: " + c);
        };
        Position target=robot.updated(dir);
        if (warehouse.at(target)=='.')
        {
            return target; // free
        }
        if (warehouse.at(target)=='#')
        {
            return robot; // blocked
        }
        if (dir==Direction.LEFT||dir==Direction.RIGHT)
        {
            // find end of the boxes
            while (warehouse.at(target)=='['||warehouse.at(target)==']')
            {
                target=target.updated(dir);
            }
            if (warehouse.at(target) == '#')
            {
                return robot; // whole chain is blocked
            }
            while(!target.equals(robot))
            {
                // move everything to the side, going backwards through the boxes
                warehouse.set(target, warehouse.at(target.updated(dir.opposite())));
                target=target.updated(dir.opposite());
            }
            robot=robot.updated(dir);
            warehouse.set(robot,'.');
        }
        else // up and down
        {
            Set<Position> boxes=new HashSet<>();
            boxes.add(robot);
            if (moveBoxes(warehouse, boxes, dir))
            {
                // we can move the set of boxes
                robot=robot.updated(dir);
                warehouse.set(robot,'.');
                return robot;
            }
        }
        return robot;
    }

    private boolean moveBoxes(final CharMatrix warehouse, final Set<Position> boxes, final Direction dir)
    {
        Set<Position> nextBoxes=new HashSet<>();
        boolean allFree=true;
        boolean blocked=false;
        for (Position box: boxes)
        {
            Position pushTarget=box.updated(dir);
            final char c = warehouse.at(pushTarget);
            if (c=='#')
                blocked=true;
            if (c!='.')
                allFree=false;
            if (c=='['||c==']')
            {
                nextBoxes.add(pushTarget);
                if (c == '[')
                {
                    nextBoxes.add(pushTarget.updated(Direction.RIGHT));
                }
                else
                {
                    nextBoxes.add(pushTarget.updated(Direction.LEFT));
                }
            }
        }
        if (allFree)
            return true;
        if (blocked)
            return false;
        boolean canMove= moveBoxes(warehouse, nextBoxes, dir);
        if (canMove)
        {
            for (Position box: nextBoxes)
            {
                warehouse.set(box.updated(dir), warehouse.at(box));
                warehouse.set(box, '.');
            }
        }
        return canMove;
    }

    private Position move(final CharMatrix warehouse, final Character c, Position robot)
    {
        Direction dir = switch (c)
        {
            case '<' -> Direction.LEFT;
            case '>' -> Direction.RIGHT;
            case '^' -> Direction.UP;
            case 'v' -> Direction.DOWN;
            default -> throw new IllegalArgumentException("invalid instruction: " + c);
        };
        Position target=robot.updated(dir);
        if (warehouse.at(target)=='.')
        {
            return target; // free
        }
        if (warehouse.at(target)=='#')
        {
            return robot; // blocked
        }
        while (warehouse.at(target)=='O')
        {
            target=target.updated(dir);
        }
        if (warehouse.at(target) == '#')
        {
            return robot; // whole chain is blocked
        }
        // move the line of boxes
        warehouse.set(target, 'O');
        robot=robot.updated(dir);
        warehouse.set(robot,'.');
        return robot;
    }

    private static int getGPS(Position position)
    {
        return position.row * 100 + position.col;
    }
}
