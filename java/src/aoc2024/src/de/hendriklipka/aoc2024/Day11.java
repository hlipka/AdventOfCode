package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day11 extends AocPuzzle
{
    private Map<String, Long> memo;
    public static void main(String[] args)
    {
        new Day11().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<Integer> stones = data.getLineIntegers(" ").get(0);
        memo = new HashMap<>();
        return stones.stream().mapToLong(s->simulate(s,25)).sum();
    }

    private long simulate(final long stone, final int blinks)
    {
        if (0==blinks)
        {
            return 1;
        }
        String num=Long.toString(stone);
        String key = num+"-"+blinks;
        if (memo.containsKey(key))
        {
            return memo.get(key);
        }
        if (0==stone)
        {
            final long simulate = simulate(1, blinks - 1);
            memo.put(key,simulate);
            return simulate;
        }
        final int numLen = num.length();
        if (0 == numLen % 2)
        {
            final String leftNum = num.substring(0, numLen / 2);
            final String rightNum = num.substring(numLen / 2);
            final long simulateLeft = simulate(Long.parseLong(leftNum), blinks - 1);
            final long simulateRight = simulate(Long.parseLong(rightNum), blinks - 1);
            memo.put(key,simulateLeft+simulateRight);
            return simulateLeft + simulateRight;
        }
        final long simulate = simulate(stone * 2024, blinks - 1);
        memo.put(key,simulate);
        return simulate;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<Integer> stones = data.getLineIntegers(" ").get(0);
        memo = new HashMap<>();
        return stones.stream().mapToLong(s->simulate(s,75)).sum();
    }
}
