package de.hendriklipka.aoc2024;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class Day23 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day23().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        Map<String, Set<String>> connections = getAllConnections();
        // store the known triplets
        Set<String> triplets = new HashSet<>();
        // loop over all already known computer pairs
        connections.keySet().forEach(c1->{
            // gets the connections of the first computer, loop over it
            connections.get(c1).forEach(c2->{
                // get the intersection between these two connections lists
                final Set<String> co1 = connections.get(c1);
                final Set<String> co2 = connections.get(c2);
                Collection<String> co3= CollectionUtils.intersection(co1, co2);
                // and store the resulting triplet(s) (if at least one computer starts with 't')
                co3.forEach(c3->
                {
                    if (c1.startsWith("t") || c2.startsWith("t") || c3.startsWith("t"))
                        triplets.add(getTriplet(c1, c2, c3));
                });
            });
        });
        return triplets.size();
    }

    private Map<String, Set<String>> getAllConnections() throws IOException
    {
        List<Pair<String, String>> lines = data.getLines().stream().map(l->parseLine(l)).toList();
        // build a list of all computers, and all connections each computer has
        Map<String, Set<String>> connections = new HashMap<>();
        lines.forEach((l)->
        {
            Set<String> s1=connections.computeIfAbsent(l.getLeft(), k->new HashSet<>());
            s1.add(l.getRight());
            Set<String> s2=connections.computeIfAbsent(l.getRight(), k->new HashSet<>());
            s2.add(l.getLeft());
        });
        return connections;
    }

    private String getTriplet(final String c1, final String c2, final String c3)
    {
        String[] tr = new String[]{c1, c2, c3};
        Arrays.sort(tr);
        return StringUtils.join(tr,",");
    }

    private Pair<String, String> parseLine(final String line)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(line, "(\\w+)-(\\w+)");
        return Pair.of(parts.get(0), parts.get(1));
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // We can do the same as for part A
        final Map<String, Set<String>> connections = getAllConnections();
        // store the known triplets
        final Map<String, String[]> nLets = new HashMap<>();
        // now store the triplets also as list (we call the n-lets with n=3 now)
        connections.keySet().forEach(c1->{
            // gets the connections of the first computer, loop over it
            connections.get(c1).forEach(c2->{
                // get the intersection between these two connections lists
                final Set<String> co1 = connections.get(c1);
                final Set<String> co2 = connections.get(c2);
                Collection<String> co3= CollectionUtils.intersection(co1, co2);
                // and store the resulting triplet(s) (if at least one computer starts with 't')
                co3.forEach(c3->
                {
                        nLets.put(getTriplet(c1, c2, c3), new String[]{c1, c2, c3});
                });
            });
        });

        Map<String, String[]> nLetsGrowing = nLets;
        // try to expand the n-Lets with additional noes connected to all current nodes
        while (true)
        {
            // take all nodes, and get the list of other nodes which all have in common
            Map<String, String[]> newNLets = new HashMap<>();

            for (String[] nLet : nLetsGrowing.values())
            {
                // reduce the set of common connection over all elements of the nLet
                Set<String> newOnes = connections.get(nLet[0]);
                for (int i = 1; i < nLet.length; i++)
                    newOnes = new HashSet<>(CollectionUtils.intersection(newOnes, connections.get(nLet[i])));
                // when there are no common nodes, we are done with this n-let
                if (!newOnes.isEmpty())
                {
                    // otherwise, we create new n+1-lets (one for each new common node)
                    for (String newOne : newOnes)
                    {
                        String[] newNLet = new String[nLet.length+1];
                        System.arraycopy(nLet, 0, newNLet, 0, nLet.length);
                        newNLet[nLet.length] = newOne;
                        Arrays.sort(newNLet);
                        newNLets.put(StringUtils.join(newNLet, ','), newNLet);
                    }
                }
            }

            // once we have no n-lets left, the last one before os the one we search
            if (newNLets.isEmpty())
                break;
            nLetsGrowing = newNLets;
        }
        if (nLetsGrowing.size()!=1)
        {
            throw new IllegalStateException("found more than one result, they are "+nLetsGrowing);
        }
        return StringUtils.join(nLetsGrowing.values().iterator().next(), ',');
    }
}
