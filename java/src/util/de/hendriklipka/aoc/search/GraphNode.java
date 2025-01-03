package de.hendriklipka.aoc.search;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class GraphNode implements Comparable<GraphNode>
{
    private final List<Pair<String, Integer>> egdes = new ArrayList<>();
    private final String name;
    private int distance;
    private String pre;

    public GraphNode(String name)
    {
        this.name = name;
    }

    void addEdge(String to, int cost)
    {
        egdes.add(Pair.of(to, cost));
    }

    public List<Pair<String, Integer>> getEdges()
    {
        return egdes;
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

    public void setPre(final String pre)
    {
        this.pre = pre;
    }

    public void setPreviousNode(final String name)
    {
        pre = name;
    }
}
