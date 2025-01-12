package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class    Day09 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day09().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        data.getLines().forEach(Day09::simulate);
        return null;
    }

    private static void simulate(String line)
    {
        List<String> game= AocParseUtils.getGroupsFromLine(line, "(\\d+) players; last marble is worth (\\d+) points");
        long players=Long.parseLong(game.get(0));
        long player=0;
        long highestMarble=Long.parseLong(game.get(1));
        final var scores = runGame(highestMarble, player, players);
        System.out.println(line+" - highScore="+scores.values().stream().mapToLong(Long::longValue).max().orElseThrow());
    }

    private static Map<Long, Long> runGame(final long highestMarble, long player, final long players)
    {
        Marble current=new Marble(0);
        current.next=current;
        current.prev=current;
        Map<Long, Long> scores=new HashMap<>();
        for (int round = 1; round <= highestMarble; round++)
        {
            if (0==(round%23))
            {
                // remove the marble 7 counterclockwise
                Marble remove=current.prev.prev.prev.prev.prev.prev.prev;
                remove.prev.next=remove.next;
                remove.next.prev=remove.prev;
                current=remove.next;
                int roundScore=round+remove.num;
                scores.put(player, roundScore + scores.getOrDefault(player, 0L));
            }
            else
            {
                // insert marble at the right place
                Marble next=new Marble(round);
                Marble oneStep=current.next;
                Marble twoStep=oneStep.next;
                next.next=twoStep;
                next.prev=oneStep;
                oneStep.next=next;
                twoStep.prev=next;
                current=next;
            }
            player = (player + 1) % players;
        }
        return scores;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        data.getLines().forEach(Day09::simulate2);
        return null;
    }

    private static void simulate2(String line)
    {
        List<String> game = AocParseUtils.getGroupsFromLine(line, "(\\d+) players; last marble is worth (\\d+) points");
        long players = Long.parseLong(game.get(0));
        long player = 0;
        long highestMarble = Long.parseLong(game.get(1));
        final var scores = runGame(highestMarble*100, player, players);
        System.out.println(line + " - highScore=" + scores.values().stream().mapToLong(Long::longValue).max().orElseThrow());
    }

    private static class Marble
    {
        Marble prev, next;
        int num;
        public Marble(final int num)
        {
            this.num=num;
        }
    }
}
