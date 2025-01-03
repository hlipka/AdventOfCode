package de.hendriklipka.aoc.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: hli
 */
public class Graph
{
    Map<String, GraphNode> nodes = new HashMap<>();

    public void addNode(String name)
    {
        nodes.putIfAbsent(name, new GraphNode(name));
    }

    public void addEdge(String from, String to, int cost)
    {
        GraphNode fromNode = nodes.get(from);
        if (null == fromNode)
        {
            throw new IllegalArgumentException("unknown node " + from);
        }
        GraphNode toNode = nodes.get(to);
        if (null == toNode)
        {
            throw new IllegalArgumentException("unknown node " + to);
        }
        fromNode.addEdge(to, cost);
        toNode.addEdge(from, cost);
    }

    public GraphNode getNode(String name)
    {
        return nodes.get(name);
    }

    public List<GraphNode> getNodes()
    {
        return new ArrayList<>(nodes.values());
    }
}
