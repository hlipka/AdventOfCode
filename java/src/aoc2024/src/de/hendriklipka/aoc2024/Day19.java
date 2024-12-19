package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Trie;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class Day19 extends AocPuzzle
{
    Trie knownPatterns;
    Set<String> allPatterns;
    Map<String, Long> countCache;

    public static void main(String[] args)
    {
        new Day19().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        knownPatterns = new Trie();
        allPatterns = new HashSet<>();

        final List<List<String>> spec = data.getStringBlocks();
        String[] towels = StringUtils.split(spec.get(0).get(0), ", ");
        // reverse both the towels and the patterns
        // this avoids some pathological patterns
        for (String t: towels)
        {
            addPattern(StringUtils.reverse(t));
        }
        List<String> patterns = spec.get(1);
        return patterns.stream().filter(p->doCheck(StringUtils.reverse(p), "")).count();
    }

    private boolean doCheck(final String pattern, final String currentPrefix)
    {
        if (allPatterns.contains(pattern))
            return true;
        List<String> prefixes = knownPatterns.getAllPrefixes(pattern);
        if (null==prefixes || prefixes.isEmpty())
            return false;
        for (String p: prefixes)
        {
            int len=p.length();
            String remaining= pattern.substring(len);
            final String newPrefix = currentPrefix + pattern.substring(0, len);
            if (doCheck(remaining, newPrefix))
            {
                return true;
            }
        }
        return false;
    }

    private void addPattern(final String pattern)
    {
        knownPatterns.insert(pattern);
        allPatterns.add(pattern);
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        knownPatterns = new Trie();
        allPatterns = new HashSet<>();
        countCache=new HashMap<>(1000);

        final List<List<String>> spec = data.getStringBlocks();
        String[] towels = StringUtils.split(spec.get(0).get(0), ", ");
        // reverse both the towels and the patterns
        // this avoids some pathological patterns
        for (String t: towels)
        {
            addPattern(StringUtils.reverse(t));
        }
        List<String> patterns = spec.get(1);
        return patterns.stream().map(StringUtils::reverse).filter(p->doCheck(p, "")).mapToLong(this::countArrangements).sum();
    }

    private long countArrangements(final String pattern)
    {
        return doCount(pattern);
    }

    private long doCount(final String pattern)
    {
        if (pattern.isEmpty())
            return 1;
        // we cache the number of arrangements for the remaining pattern
        long cached=countCache.getOrDefault(pattern,-1L);
        if (-1!=cached)
            return cached;
        long count=0;
        List<String> prefixes = knownPatterns.getAllPrefixes(pattern);
        // we have no match at all
        if (null==prefixes || prefixes.isEmpty())
            return 0;
        for (String p: prefixes)
        {
            String remaining= pattern.substring(p.length());
            count+=doCount(remaining);
        }
        countCache.put(pattern,count);
        return count;
    }

}
