package de.hendriklipka.aoc.search;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Uses the Bron–Kerbosch algorithm to find all maximal cliques in a graph (a clique being a set of nodes where each node is directly connected to all other
 * nodes of that set).
 */
public class CliqueSearch
{
    private final Graph graph;

    // the current list of largest cliques, together with the size of the largest set
    private final List<List<String>> _cliques= new ArrayList<>();
    private int currentMax=0;

    // node names, ordered by the number of outgoing edges (descending), used to find the pivot-element
    private final List<String> orderedNodes;

    public CliqueSearch(Graph graph)
    {
        this.graph=graph;
        orderedNodes=graph.getNodes().stream().sorted(Comparator.comparingInt(GraphNode::getOrder).reversed()).map(GraphNode::getName).toList();
    }

    public void search()
    {
        bronKerboschPivoted(new ArrayList<>(), new ArrayList<>(orderedNodes), new ArrayList<>());
    }

    private void bronKerboschPivoted(final List<String> r, final List<String> p, final List<String> x)
    {
        if (p.isEmpty() && x.isEmpty())
        {
            // new largest clique
            if (r.size() > currentMax)
            {
                _cliques.clear();
                _cliques.add(r);
                currentMax = r.size();
            }
            // clique of the same size
            else if (r.size()==currentMax)
            {
                _cliques.add(r);
            }
            return;
        }
        // choose pivot element from P union X
        final Set<String> pList = new HashSet<>(ListUtils.union(p, x));
        String pivot=null;
        for (String n: orderedNodes)
        {
            if (pList.contains(n))
            {
                pivot=n;
                break;
            }
        }
        List<String> nodes=ListUtils.subtract(p, graph.getNode(pivot).edgeNames());
        for (String n: nodes)
        {
            bronKerboschPivoted(
                    ListUtils.union(r, List.of(n)),
                    ListUtils.intersection(p, graph.getNode(n).edgeNames()),
                    ListUtils.intersection(x, graph.getNode(n).edgeNames()));
            p.remove(n);
            x.add(n);
        }
    }

    public List<List<GraphNode>> getCliques()
    {
        final List<List<GraphNode>> result=new ArrayList<>();
        for (List<String> clique: _cliques)
        {
            result.add(clique.stream().map(graph::getNode).toList());
        }
        return result;
    }
}
