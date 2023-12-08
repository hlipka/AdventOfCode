package de.hendriklipka.aoc2023.day08;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.MathUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.util.*;

/**
 * User: hli
 * Date: 07.12.23
 * Time: 20:12
 */
public class Day08b
{
    public static void main(String[] args)
    {
        Map<String, Pair<String, String>> desert = new HashMap<>();
        try
        {
            List<List<String>> map = AocParseUtils.getStringBlocks("2023", "day08");
            String instr = map.get(0).get(0);
            map.get(1).stream().map(Day08b::parseLine).forEach(t -> desert.put(t.getLeft(), new ImmutablePair<>(t.getMiddle(), t.getRight())));
            List<String> nodes = new ArrayList<>();
            for (String n : desert.keySet())
            {
                if (n.endsWith("A"))
                {
                    nodes.add(n);
                }
            }
            long count = 0;
            long[] cycles = new long[nodes.size()];
            Arrays.fill(cycles, 0);
            while (!done(cycles))
            {
                final int pos = (int) count % instr.length();
                char dir = instr.charAt(pos);
                for (int i = 0; i < nodes.size(); i++)
                {
                    String node = nodes.get(i);
                    // detect cycles at the end of the instruction list
                    // only there we can have repeatable cycles
                    if (0 == pos && 0 == cycles[i] && node.endsWith("Z"))
                    {
                        cycles[i] = count;
                    }
                    Pair<String, String> next = desert.get(node);
                    if (dir == 'L')
                    {
                        nodes.set(i, next.getLeft());
                    } else
                    {
                        nodes.set(i, next.getRight());
                    }
                }
                count++;
            }
            System.out.println(MathUtils.lcm(cycles));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean done(long[] cycles)
    {
        for (long cycle: cycles)
        {
            if (0==cycle)
                return false;
        }
        return true;
    }

    private static Triple<String, String, String> parseLine(String line)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(line, "(\\w+) = \\((\\w+), (\\w+)\\)");
        return new ImmutableTriple<>(parts.get(0), parts.get(1), parts.get(2));
    }
}
