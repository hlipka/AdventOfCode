package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class Day22 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day22().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        return data.getLinesAsLong().stream().mapToLong(this::getLastSecret).sum();
    }

    private long getLastSecret(final Long num)
    {
        long secret=num;
        for (int i = 0; i < 2000; i++)
            secret=evolve(secret);
        return secret;
    }

    // calculate the next secret
    private long evolve(long secret)
    {
        long num=secret*64;
        secret = secret^num;
        secret = secret% 16777216;

        num = secret / 32;
        secret = secret^num;
        secret = secret % 16777216;

        num = secret * 2048;
        secret = secret ^ num;
        secret = secret % 16777216;

        return secret;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // for each monkey, get a list of potential instructions paired with the number of bananas they would buy
        final List<List<Pair<String, Integer>>> sequences =
                data.getLinesAsLong().stream().map(this::getPricesList).map(this::getSequenceList).toList();
        // from we can get
        // - all potential instructions
        // - a map for each monkey showing what it would buy
        // what would be faster: while walking through all instructions for a monkey, just update a map with the sum values
        Set<String> allInstructions = new HashSet<>();
        List<Map<String, Integer>> mapOfInstructionResults = new ArrayList<>();
        for (List<Pair<String, Integer>> instructions : sequences)
        {
            // just add all the instructions
            allInstructions.addAll(instructions.stream().map(Pair::getLeft).toList());
            final Map<String, Integer> mapOfInstructions = new HashMap<>();
            // map each instruction to the banana count of when it first appears
            for (Pair<String, Integer> instruction : instructions)
            {
                // we only add the first instruction and skip further duplicates
                mapOfInstructions.putIfAbsent(instruction.getLeft(), instruction.getRight());
            }
            mapOfInstructionResults.add(mapOfInstructions);
        }

        // then we just look through all instructions, and sum the values over all monkeys and find the best one
        int bestSum = 0;
        for (String instr: allInstructions)
        {
            int result = mapOfInstructionResults.stream().mapToInt(m->m.getOrDefault(instr, 0)).sum();
            if (result>bestSum)
            {
                bestSum = result;
            }
        }

        return bestSum;
    }

    private List<Integer> getPricesList(final Long secret)
    {
        final List<Integer> prices = new ArrayList<>();
        long currentSecret = secret;
        while (prices.size()<2000)
        {
            prices.add((int)(currentSecret%10));
            currentSecret = evolve(currentSecret);
        }
        return prices;
    }


    private List<Pair<String, Integer>> getSequenceList(final List<Integer> priceList)
    {
        final List<Pair<String, Integer>> result = new ArrayList<>();
        for (int i=4;i<priceList.size();i++)
        {
            final String sb = (priceList.get(i - 3) - priceList.get(i - 4)) +
                              "," +
                              (priceList.get(i - 2) - priceList.get(i - 3)) +
                              "," +
                              (priceList.get(i - 1) - priceList.get(i - 2)) +
                              "," +
                              (priceList.get(i) - priceList.get(i - 1));
            result.add(Pair.of(sb, priceList.get(i)));
        }
        return result;
    }

}
