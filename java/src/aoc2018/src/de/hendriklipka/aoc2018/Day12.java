package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class Day12 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day12().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<List<String>> config=data.getStringBlocks();
        String init= AocParseUtils.parseStringFromString(config.get(0).get(0), "initial state: (.*)");
        Map<String, Character> rules=parseRules(config.get(1));
        Set<Long> pots=new HashSet<>();
        long lowestPot=0;
        long highestPot=0;
        for (int p=0;p<init.length();p++)
        {
            if (init.charAt(p)=='#')
            {
                pots.add((long)p);
                highestPot=Math.max(p, highestPot);
            }
        }
        for (int g = 0; g < 20L; g++)
        {
            Set<Long> newPots=new HashSet<>();
            // look through all potential pots
            for (long pot=lowestPot-2; pot<=highestPot+2;pot++)
            {
                if (matchingRule(pot, pots, rules)=='#')
                {
                    lowestPot=Math.min(pot, lowestPot);
                    highestPot=Math.max(pot, highestPot);
                    newPots.add(pot);
                }
            }
            pots=newPots;

        }
        return pots.stream().mapToLong(value -> value).sum();
    }

    private char matchingRule(final long pot, final Set<Long> pots, final Map<String, Character> rules)
    {
        for (Map.Entry<String, Character> rule : rules.entrySet())
        {
            if (matchesRule(pot, pots, rule.getKey()))
            {
                return rule.getValue();
            }
        }
        return '.';
    }

    private boolean matchesRule(final long pot, final Set<Long> pots, final String rule)
    {
        for (int p=0;p<5;p++)
        {
            char c=rule.charAt(p);
            long pos=pot+p-2;
            if (c=='#' && !pots.contains(pos))
                return false;
            if (c == '.' && pots.contains(pos))
                return false;
        }
        return true;
    }

    private Map<String, Character> parseRules(final List<String> lines)
    {
        final Map<String, Character> rules=new HashMap<>();
        for (String line: lines)
        {
            List<String> parts=AocParseUtils.getGroupsFromLine(line, "([.#]+) => ([.#])");
            rules.put(parts.get(0), parts.get(1).charAt(0));
        }
        return rules;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        if (isExample)
            return -1;
        List<List<String>> config=data.getStringBlocks();
        String init= AocParseUtils.parseStringFromString(config.get(0).get(0), "initial state: (.*)");
        Map<String, Character> rules=parseRules(config.get(1));
        Set<Long> pots=new HashSet<>();
        long lowestPot=0;
        long highestPot=0;
        for (int p=0;p<init.length();p++)
        {
            if (init.charAt(p)=='#')
            {
                pots.add((long)p);
                highestPot=Math.max(p, highestPot);
            }
        }
        // find cycles
        Map<String, Pair<Long, Long>> generations=new HashMap<>();
        int matchCount=0;
        for (long g = 1; g <= 50000000000L; g++)
        {
            long maxPos=0;
            long minPos=Long.MAX_VALUE;
            Set<Long> newPots=new HashSet<>();
            // look through all potential pots
            for (long pot=lowestPot-2; pot<=highestPot+2;pot++)
            {
                if (matchingRule(pot, pots, rules)=='#')
                {
                    minPos=Math.min(pot, minPos);
                    maxPos=Math.max(pot, maxPos);
                    newPots.add(pot);
                }
            }
            String key=getKey(newPots, minPos);
            if (generations.containsKey(key))
            {
                Pair<Long, Long> lastGen=generations.get(key);
                // seems we have a period with size 1, where each generation shits one to the right
                if (g- lastGen.getLeft()==1 && minPos-lastGen.getRight()==1)
                {
                    matchCount++;
                    if (5==matchCount)
                    {
                        // once we are sure we have found the repetition, the final answer is the current value plus the shift
                        return newPots.stream().mapToLong(value -> value).sum()+ newPots.size()*(50000000000L-g);
                    }
                }
            }
            generations.put(key, Pair.of(g, minPos));
            pots=newPots;
            lowestPot=minPos;
            highestPot=maxPos;

        }
        System.out.println(pots.size());
        return pots.stream().mapToLong(value -> value).sum();
    }

    private String getKey(final Set<Long> newPots, long minPos)
    {
        // looks as if the pots move to the right every now and then
        // so normalize the key with the lowest position
        List<Long> nums=new ArrayList<>();
        for (Long l:newPots)
        {
            nums.add(l-minPos);
        }
        nums.sort(Long::compareTo);
        return StringUtils.join(nums,",");
    }
}
