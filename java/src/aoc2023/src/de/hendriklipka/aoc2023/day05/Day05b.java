package de.hendriklipka.aoc2023.day05;

import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * User: hli
 * Date: 01.12.23
 * Time: 00:00
 */
public class Day05b
{
    static Map<String, Rule> rules = new HashMap<>();
    static long[] seeds;
    public static void main(String[] args)
    {
        long min = Long.MAX_VALUE;
        try
        {
            List<List<String>> blocks = AocParseUtils.getStringBlocks("2023", "day05");
            parseSeeds(blocks.get(0));
            blocks.remove(0);
            blocks.stream().map(Day05b::parseBlock).forEach(m-> rules.put(m.from, m));

            for (int i=0;i<seeds.length;i+=2)
            {
                long start = seeds[i];
                long count=seeds[i+1];
                System.out.println("new range from "+start);
                for (long value=start;value<start+count;value++) {
                    long location = calculateLocation(value);
                    if (location<min) {
                        min=location;
                        System.out.println(min);
                    }
                }
            }
            System.out.println(min);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static long calculateLocation(long value) {
        String currentMapping = "seed";
        while(!currentMapping.equals("location"))
        {
            Rule rule = rules.get(currentMapping);
            value = rule.getMappedValue(value);
            currentMapping = rule.to;
        }
        return value;
    }

    private static Rule parseBlock(List<String> block) {
        Rule rule = new Rule();
        rule.from=AocParseUtils.parseStringFromString(block.get(0),"(\\w+)-to-\\w+ map:");
        rule.to=AocParseUtils.parseStringFromString(block.get(0),"\\w+-to-(\\w+) map:");
        block.remove(0);
        for (String line: block)
        {
            long destStart = AocParseUtils.parseLongFromString(line, "(\\d+) \\d+ \\d+");
            long sourceStart = AocParseUtils.parseLongFromString(line, "\\d+ (\\d+) \\d+");
            long length = AocParseUtils.parseLongFromString(line, "\\d+ \\d+ (\\d+)");
            rule.addMapping(sourceStart, destStart, length);
        }

        return rule;
    }

    private static void parseSeeds(List<String> block) {
        String seedList=AocParseUtils.parseStringFromString(block.get(0), "seeds: (.*)");
        seeds = Arrays.stream(StringUtils.split(seedList, ' ')).mapToLong(Long::parseLong).toArray();
    }

    private static class Rule
    {
        String from;
        String to;
        private List<Mapping> mappings = new ArrayList<>();

        public void addMapping(long sourceStart, long destStart, long length) {
            mappings.add(new Mapping(sourceStart, destStart, length));
        }

        public long getMappedValue(long value)
        {
            for (Mapping map: mappings)
            {
                long target = map.getMappedValue(value);
                if (-1!=target)
                    return target;
            }
            return value;
        }

        private static class Mapping {
            private final long sourceStart;
            private final long destStart;
            private final long length;

            public Mapping(long sourceStart, long destStart, long length) {
                this.sourceStart = sourceStart;
                this.destStart = destStart;
                this.length = length;
            }

            public long getMappedValue(long value)
            {
                if (value >= sourceStart && value < sourceStart+length )
                {
                    return value-sourceStart+destStart;
                }
                return -1;
            }
        }
    }
}
