package de.hendriklipka.aoc.search;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: hli
 */
public class _CliqueSearch
{
    @Test
    public void testSimpleGraph()
    {
        // this example is from https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm
        Graph g=new Graph();
        g.addNode("1");
        g.addNode("2");
        g.addNode("3");
        g.addNode("4");
        g.addNode("5");
        g.addNode("6");
        g.addEdge("1","2", 1);
        g.addEdge("1","5", 1);
        g.addEdge("2","5", 1);
        g.addEdge("2","3", 1);
        g.addEdge("3","4", 1);
        g.addEdge("4","5", 1);
        g.addEdge("4","6", 1);

        CliqueSearch cliqueSearch=new CliqueSearch(g);
        cliqueSearch.search();
        final List<List<GraphNode>> result = cliqueSearch.getCliques();
        assertThat(result.size(), is(1));
        final List<GraphNode> clique = result.get(0);
        assertThat(clique.size(), is(3));
        final List<String> names = clique.stream().map(GraphNode::getName).sorted().toList();
        assertThat(names.get(0), is("1"));
        assertThat(names.get(1), is("2"));
        assertThat(names.get(2), is("5"));
    }
}
