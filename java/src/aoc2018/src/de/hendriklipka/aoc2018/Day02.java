package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day02 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day02().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<Map<Character, Integer>> counts=data.getLines().stream().map(this::countChars).toList();
        int twoCount=0;
        int threeCount=0;
        for (Map<Character, Integer> count: counts)
        {
            if (count.containsValue(2))
                twoCount++;
            if (count.containsValue(3))
                threeCount++;
        }
        return twoCount*threeCount;
    }

    private Map<Character, Integer> countChars(final String line)
    {
        final Map<Character, Integer> counts = new HashMap<>();
        for (char c : line.toCharArray())
        {
            counts.put(c, counts.getOrDefault(c, 0) + 1);
        }
        return counts;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<String> ids = data.getLines();
        for (String id1 : ids)
        {
            for (String id2 : ids)
            {
                String match=checkForMatch(id1,id2);
                if (null!=match)
                    return match;
            }
        }
        return null;
    }

    private String checkForMatch(final String id1, final String id2)
    {
        int matchCount=0;
        int diffPos=-1;
        for (int i=0; i<id1.length(); i++)
        {
            if (id1.charAt(i)!=id2.charAt(i))
            {
                matchCount++;
                diffPos=i;
            }
        }
        if (1==matchCount)
        {
            return id1.substring(0,diffPos)+id2.substring(diffPos+1);
        }
        return null;
    }
}
