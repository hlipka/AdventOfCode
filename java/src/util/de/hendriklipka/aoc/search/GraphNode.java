package de.hendriklipka.aoc.search;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class GraphNode implements Comparable<GraphNode>
{
    private final List<Pair<String, Integer>> edges = new ArrayList<>();
    private final List<String> edgeNames=new ArrayList<>();
    private final String name;
    private int distance;
    private String pre;

    public GraphNode(String name)
    {
        this.name = name;
    }

    void addEdge(String to, int cost)
    {
        edges.add(Pair.of(to, cost));
        edgeNames.add(to);
    }

    public List<Pair<String, Integer>> getEdges()
    {
        return edges;
    }

    public int getOrder()
    {
        return edges.size();
    }

    @Override
    public int compareTo(GraphNode o)
    {
        return Integer.compare(distance, o.distance);
    }

    public String getName()
    {
        return name;
    }

    public int getDistance()
    {
        return distance;
    }

    public String getPreviousNode()
    {
        return pre;
    }

    public void setDistance(final int distance)
    {
        this.distance = distance;
    }

    public void setPreviousNode(final String name)
    {
        pre = name;
    }

    public List<String> edgeNames()
    {
        return edgeNames;
    }

    @Override
    public String toString()
    {
        return "GraphNode{" +
               "name='" + name + '\'' +
               ", distance=" + distance +
               ", pre='" + pre + '\'' +
               '}';
    }
}
