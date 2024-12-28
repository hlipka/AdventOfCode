package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
        Map<String, String[]> pipes=new HashMap<>();
        data.getLines().forEach(l->parseLine(l, pipes));
        String prgId="0";
        final var group = getGroupSet(prgId, pipes);
        return group.size();
    }

    private static Set<String> getGroupSet(final String prgId, final Map<String, String[]> pipes)
    {
        Set<String> group = new HashSet<>();
        Set<String> toVisit=new HashSet<>();
        // start with the root programs
        toVisit.add(prgId);
        while(!toVisit.isEmpty()) // when nothing is left to look at, we are done
        {
            // take the next program to look at, and remove it from the visit-list
            String prg=toVisit.iterator().next();
            toVisit.remove(prg);
            // when we have no seen it before
            if (!group.contains(prg))
            {
                // add it to the group
                group.add(prg);
                // and then add its targets to the visit list
                Collections.addAll(toVisit, pipes.get(prg));
            }
        }
        return group;
    }

    private void parseLine(final String line, final Map<String, String[]> pipes)
    {
        String prg= AocParseUtils.parseStringFromString(line, "(.*) <-> .*");
        String targetStr = AocParseUtils.parseStringFromString(line, ".* <-> (.*)");
        String[] targets = StringUtils.split(targetStr, ", ");
        pipes.put(prg, targets);
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        int groupCount=0;
        Map<String, String[]> pipes = new HashMap<>();
        data.getLines().forEach(l -> parseLine(l, pipes));
        Set<String> seenSoFar=new HashSet<>();
        // we start with the root program
        String prgId = "0";
        while (true)
        {
            // we determine the programs in this group
            final var group = getGroupSet(prgId, pipes);
            groupCount++;
            seenSoFar.addAll(group);
            // find all programs we have never looked at so far (which is 'all programs - the ones seen so far')
            final Collection<String> remaining = CollectionUtils.subtract(pipes.keySet(), seenSoFar);
            // when the is nothing left, we are done
            if (remaining.isEmpty())
                break;
            // otherwise we just take one the programs and determine the next group from there
            prgId = remaining.iterator().next();
        }
        return groupCount;
    }
}
