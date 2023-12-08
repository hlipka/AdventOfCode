package de.hendriklipka.aoc2023.day08;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hli
 * Date: 07.12.23
 * Time: 20:12
 */
public class Day08a
{
    public static void main(String[] args)
    {
        Map<String, Pair<String, String>> desert = new HashMap<>();
        try
        {
            List<List<String>> map = AocParseUtils.getStringBlocks("2023", "day08");
            String instr = map.get(0).get(0);
            map.get(1).stream().map(Day08a::parseLine).forEach(t->desert.put(t.getLeft(), new ImmutablePair<>(t.getMiddle(), t.getRight())));
            String node="AAA";
            int count=0;
            while (!node.equals("ZZZ"))
            {
                char dir=instr.charAt(count%instr.length());
                Pair<String, String> next = desert.get(node);
                if (dir=='L')
                {
                    node=next.getLeft();
                }
                else
                {
                    node=next.getRight();
                }

                count++;
            }
            System.out.println(count);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Triple<String, String, String> parseLine(String line)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(line, "(\\w+) = \\((\\w+), (\\w+)\\)");
        return new ImmutableTriple<>(parts.get(0), parts.get(1), parts.get(2));
    }
}
