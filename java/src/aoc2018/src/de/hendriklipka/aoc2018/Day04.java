package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class Day04 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day04().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final var parsed = getParsedData();
        // look up the sum of sleeping minutes for each guard, and return the biggest sleeper
        int sleepy=
                parsed.entrySet().stream().map(r -> Pair.of(r.getKey(), Arrays.stream(r.getValue()).sum())).max(Comparator.comparingInt(Pair::getValue)).orElseThrow().getKey();

        final var minutes = parsed.get(sleepy);
        int minute = IntStream.range(0, minutes.length)
                .boxed()
                .max(Comparator.comparingInt(i -> minutes[i]))
                .orElse(-1);
        return sleepy * minute;
    }

    private Map<Integer, int[]> getParsedData() throws IOException
    {
        List<String> records=data.getLines();
        records.sort(String::compareTo);
        Map<Integer, int[]> parsed=new HashMap<>();
        int guard;
        int[] night={};
        int lastMinute=0;
        for (String record : records)
        {
            if (record.contains("begins shift"))
            {
                guard= AocParseUtils.parseIntFromString(record, "\\[.*\\] Guard #(\\d+) begins shift");
                night=parsed.computeIfAbsent(guard, g->
                {
                    int[] m= new int[60];
                    Arrays.fill(m, 0);
                    return m;
                });
            }
            else if (record.contains("falls asleep"))
            {
                lastMinute=AocParseUtils.parseIntFromString(record, "\\[.* 00:(\\d+)\\] falls asleep");
            }
            else if (record.contains("wakes up"))
            {
                int minute = AocParseUtils.parseIntFromString(record, "\\[.* 00:(\\d+)\\] wakes up");
                for (int m=lastMinute; m<minute; m++)
                {
                    night[m]++;
                }
            }
            else
            {
                System.out.println("unknown record "+record);
            }
        }
        return parsed;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final var parsed = getParsedData();
        // similar to above, but we look instead for the minute with the largest value, and return the max of that
        int sleepy = parsed.entrySet().stream().map(r -> Pair.of(r.getKey(), Arrays.stream(r.getValue()).max().orElseThrow())).max(
                        Comparator.comparingInt(Pair::getValue)).orElseThrow().getKey();

        final var minutes = parsed.get(sleepy);
        int minute = IntStream.range(0, minutes.length)
                .boxed()
                .max(Comparator.comparingInt(i -> minutes[i]))
                .orElse(-1);
        return sleepy * minute;
    }
}
