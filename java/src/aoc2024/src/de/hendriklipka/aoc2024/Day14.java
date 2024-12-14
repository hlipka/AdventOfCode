package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class Day14 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day14().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<String> config=data.getLines();
        List<Long> room = AocParseUtils.getAllNumbersFromLine(config.remove(0));
        long width=room.get(0);
        long height=room.get(1);
        final Map<Integer, Integer> groups = config.stream().map(AocParseUtils::getAllNumbersFromLine).map(Robot::new).
                map(r -> r.simulate(100, width, height)).
                map(position -> getQuadrant(position, width, height)).
                filter(q -> q > 0).
                collect(groupingBy(q -> q, summingInt(q -> 1)));
        return groups.getOrDefault(1, 0)*groups.getOrDefault(2, 0)*groups.getOrDefault(3, 0)*groups.getOrDefault(4, 0);
    }

    private static int getQuadrant(Position position, final long width, final long height)
    {
        final int middleRow = (int)Math.floor((double) height / 2);
        final int middleCol = (int)Math.floor((double) width / 2);
        if (position.row==middleRow || position.col==middleCol)
        {
            return 0;
        }

        if (position.row < middleRow)
        {
            return position.col < middleCol ? 1 : 2;
        }
        else
        {
            return position.col < middleCol ? 3 : 4;
        }
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<String> config = data.getLines();
        List<Long> size = AocParseUtils.getAllNumbersFromLine(config.remove(0));
        long width = size.get(0);
        long height = size.get(1);
        final List<Robot> robots = config.stream().map(AocParseUtils::getAllNumbersFromLine).map(Robot::new).toList();

        for (int time=1;time<10000; time++)
        {
            CharMatrix room = CharMatrix.filledMatrix((int)height, (int)width, ' ', '.');
            final int finalTime = time;
            final Set<Position> positions = robots.stream().map(r -> r.simulate(finalTime, width, height)).collect(Collectors.toSet());
            /*
                Lucky assumption: when the robots form a picture, they probably should all be in different places
                Interestingly only some of the robots form the tree, and the other ones are in random locations.
                For the first 10k seconds we get about 10 matching states, and one of them is the right one
                (First idea was to look for a symmetric output, but the picture isn't)
                (Another idea could be to take each robot, do a flood-fill from there, and when a region of e.g. at least 10 robots are found print the image)
             */
            if (positions.size()>=robots.size()-3)
            {
                positions.forEach(position -> {room.set(position, 'x');});
                System.out.println("time="+time);
                room.print();
                System.out.println("========================================");
            }

        }

        return null;
    }

    private static class Robot
    {
        private final Position pos;
        private final Position direction;

        public Robot(List<Long> config)
        {
            pos = new Position((int)config.get(1).longValue(), (int)config.get(0).longValue());
            direction = new Position((int)config.get(3).longValue(), (int)config.get(2).longValue());
        }

        Position simulate(long cycles, final long width, final long height)
        {
            long newRow=(pos.row+direction.row*cycles)%height;
            if (newRow<0)
                newRow+=height;
            long newCol=(pos.col+direction.col*cycles)%width;
            if (newCol<0)
                newCol+=width;
            return new Position((int)newRow, (int)newCol);
        }

        @Override
        public String toString()
        {
            return "Robot{" +
                   "pos=" + pos +
                   ", direction=" + direction +
                   '}';
        }
    }
}
