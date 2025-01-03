package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.search.Graph;
import de.hendriklipka.aoc.search.GraphNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day24 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day24().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<Pair<Integer, Integer>> connectors = data.getLines().stream().map(this::parseConnector).toList();
        Graph g=new Graph();
        for (Pair<Integer, Integer> c : connectors)
        {
            g.addNode(Integer.toString(c.getLeft()));
            g.addNode(Integer.toString(c.getRight()));
            g.addEdge(Integer.toString(c.getLeft()), Integer.toString(c.getRight()), c.getLeft()+c.getRight());
        }
        GraphNode start = g.getNode("0");
        Set<String> usedConnectors = new HashSet<>();
        return getBestChain(g, start, 0, usedConnectors);
    }

    private int getBestChain(final Graph g, final GraphNode currentNode, final int currentStrength, final Set<String> currentUsedConnectors)
    {
        final List<Pair<String, Integer>> edges = currentNode.getEdges();
        int bestStrength=currentStrength;
        for (Pair<String, Integer> edge : edges)
        {
            Set<String> usedConnectors = new HashSet<>(currentUsedConnectors);
            String[] key=new String[]{currentNode.getName(), edge.getLeft()};
            Arrays.sort(key);
            String keyStr= StringUtils.join(key, "-");
            if (usedConnectors.contains(keyStr))
                continue;
            usedConnectors.add(keyStr);
            int strength=getBestChain(g, g.getNode(edge.getLeft()), edge.getRight()+currentStrength, usedConnectors);
            if (strength>bestStrength)
            {
                bestStrength=strength;
            }
        }

        return bestStrength;
    }

    // same basic logic as the strongest chain, but we also record the chain length and use that for finding the best one
    private Pair<Integer, Integer> getLongestChain(final Graph g, final GraphNode currentNode, final int currentStrength,
                                                  final Set<String> currentUsedConnectors)
    {
        final List<Pair<String, Integer>> edges = currentNode.getEdges();
        Pair<Integer, Integer> bestStrength=Pair.of(currentUsedConnectors.size(),currentStrength);
        for (Pair<String, Integer> edge : edges)
        {
            Set<String> usedConnectors = new HashSet<>(currentUsedConnectors);
            String[] key=new String[]{currentNode.getName(), edge.getLeft()};
            Arrays.sort(key);
            String keyStr= StringUtils.join(key, "-");
            if (usedConnectors.contains(keyStr))
                continue;
            usedConnectors.add(keyStr);
            Pair<Integer, Integer> strength=getLongestChain(g, g.getNode(edge.getLeft()), edge.getRight()+currentStrength, usedConnectors);
            if (strength.getLeft()>bestStrength.getLeft())
            {
                bestStrength=strength;
            }
            else if (strength.getLeft().equals(bestStrength.getLeft()) && strength.getRight() > bestStrength.getRight())
            {
                bestStrength = strength;
            }
        }

        return bestStrength;
    }

    private Pair<Integer, Integer> parseConnector(final String line)
    {
        List<Integer> parts = AocParseUtils.splitLineToInts(line, '/');
        return Pair.of(parts.get(0), parts.get(1));
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<Pair<Integer, Integer>> connectors = data.getLines().stream().map(this::parseConnector).toList();
        Graph g = new Graph();
        for (Pair<Integer, Integer> c : connectors)
        {
            g.addNode(Integer.toString(c.getLeft()));
            g.addNode(Integer.toString(c.getRight()));
            g.addEdge(Integer.toString(c.getLeft()), Integer.toString(c.getRight()), c.getLeft() + c.getRight());
        }
        GraphNode start = g.getNode("0");
        Set<String> usedConnectors = new HashSet<>();
        return getLongestChain(g, start, 0, usedConnectors).getRight();
    }
}
