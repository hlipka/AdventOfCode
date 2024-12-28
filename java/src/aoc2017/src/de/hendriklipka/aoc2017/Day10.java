package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.List;

public class Day10 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day10().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<List<Integer>> config=data.getLineIntegers(",");
        int size=config.get(0).get(0);
        List<Integer> lengths=config.get(1);
        KnotHash hash=new KnotHash(lengths, size);
        hash.singleRound();
        return hash.getHash();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        if (isExample)
            return null;
        List<String> config = data.getLines();
        int size=Integer.parseInt(config.get(0));

        System.out.println(calculateHash("", size));
        System.out.println(calculateHash("AoC 2017", size));
        System.out.println(calculateHash("1,2,3", size));
        System.out.println(calculateHash("1,2,4", size));
        return calculateHash(config.get(1), size);
    }

    private String calculateHash(String s, final int size)
    {
        KnotHash hash = new KnotHash(s, size);
        hash.hash();
        return hash.getHexHash();
    }

}
