package de.hendriklipka.aoc.search;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * generic implementation for finding the shortest paths in a graph
 * you need to add the nodes and the edges, and then can retrieve the costs, or the actual paths
 * (once retrieved these are cached, so you can get them again for cheap)
 */
public class GraphSearch
{
    final Graph graph;
    Map<String, Integer> pathCostCache = new HashMap<>();
    Map<String, List<String>> pathCache = new HashMap<>();

    public GraphSearch(final Graph graph)
    {
        this.graph = graph;
    }


    public int getPathCost(String from, String to)
    {
        String key = from + "|" + to;
        Integer cost = pathCostCache.get(key);
        if (null != cost)
        {
            return cost;
        }

        for (GraphNode node : graph.getNodes())
        {
            node.setDistance(Integer.MAX_VALUE);
            node.setPreviousNode(null);
        }
        GraphNode start = graph.nodes.get(from);
        if (null == start)
        {
            throw new IllegalArgumentException("unknown node " + from);
        }
        start.setDistance(0);
        PriorityQueue<GraphNode> allGraphNodes = new PriorityQueue<>(graph.nodes.values());
        while (!allGraphNodes.isEmpty())
        {
            GraphNode n = allGraphNodes.poll();
            List<Pair<String, Integer>> edges = n.getEdges();
            for (Pair<String, Integer> edge : edges)
            {
                // this node is not reachable at all
                if (n.getDistance()== Integer.MAX_VALUE)
                    continue;
                int newDist = n.getDistance() + edge.getRight();
                GraphNode other = graph.nodes.get(edge.getLeft());
                if (allGraphNodes.contains(other))
                {
                    if (newDist < other.getDistance())
                    {
                        other.setDistance(newDist);
                        other.setPreviousNode(n.getName());
                        // we need to re-insert, since we update the value used for sorting
                        //TODO contains and remove are linear-time, so we need something better (for larger graphs)
                        // (potentially use a set of nodes, and maps for the values)
                        allGraphNodes.remove(other);
                        allGraphNodes.add(other);
                    }
                }
            }
        }
        GraphNode target = graph.nodes.get(to);
        cost = target.getDistance();
        // we did not find a way to the target, so skip the path calculation
        if (cost== Integer.MAX_VALUE)
            return cost;
        pathCostCache.put(key, cost);
        List<String> path = new ArrayList<>();
        while (target != start)
        {
            path.add(0, target.getName()); //TODO: this might be slow with large paths
            to= target.getPreviousNode();
            target = graph.nodes.get(to);
            if (null == target)
            {
                System.out.println("cannot find node " + to);
            }
        }
        path.add(0, from);
        pathCache.put(key, path);
        return cost;
    }

    public List<String> getPath(String from, String to)
    {
        getPathCost(from, to); // ensure we have calculated the path
        String key = from + "|" + to;
        return pathCache.get(key);
    }

}
