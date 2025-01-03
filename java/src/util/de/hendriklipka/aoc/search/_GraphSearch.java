package de.hendriklipka.aoc.search;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

/**
 * User: hli
 * Date: 29.12.22
 * Time: 23:26
 */
public class _GraphSearch
{
    @Test
    public void testSimpleGraph()
    {
        Graph graph = new Graph();
        GraphSearch gs = new GraphSearch(graph);
        graph.addNode("a");
        graph.addNode("b");
        graph.addNode("c");
        graph.addNode("d");
        graph.addEdge("a", "b", 1);
        graph.addEdge("a", "c", 2);
        graph.addEdge("b", "d", 1);
        graph.addEdge("b", "d", 1);

        assertThat(gs.getPathCost("a", "b"), is(1));
        assertThat(gs.getPathCost("a", "c"), is(2));
        assertThat(gs.getPathCost("a", "d"), is(2));

        assertThat(gs.getPath("a", "b"), contains("a", "b"));
        assertThat(gs.getPath("a", "c"), contains("a", "c"));
        assertThat(gs.getPath("a", "d"), contains("a", "b", "d"));
    }

    @Test
    public void testLargerGraph()
    {
        Graph graph = new Graph();
        GraphSearch gs = new GraphSearch(graph);
        graph.addNode("a");
        graph.addNode("b");
        graph.addNode("c");
        graph.addNode("d");
        graph.addNode("e");
        graph.addNode("f");
        graph.addNode("g");
        graph.addNode("h");
        graph.addNode("i");
        graph.addNode("j");
        graph.addEdge("a", "b", 1);
        graph.addEdge("b", "c", 3);
        graph.addEdge("c", "d", 5);
        graph.addEdge("c", "h", 6);
        graph.addEdge("a", "i", 2);
        graph.addEdge("i", "h", 4);
        graph.addEdge("d", "e", 10);
        graph.addEdge("e", "f", 11);
        graph.addEdge("h", "f", 7);
        graph.addEdge("h", "g", 8);
        graph.addEdge("g", "j", 9);

        assertThat(gs.getPathCost("a", "b"), is(1));
        assertThat(gs.getPathCost("a", "c"), is(4));
        assertThat(gs.getPathCost("a", "e"), is(19));
        assertThat(gs.getPathCost("a", "f"), is(13));
        assertThat(gs.getPathCost("f", "a"), is(13)); // reverse path must be the same
        assertThat(gs.getPathCost("f", "a"), is(13)); // get from the cache
        assertThat(gs.getPathCost("a", "j"), is(23));
        assertThat(gs.getPathCost("j", "d"), is(28));

        assertThat(gs.getPath("a", "b"), contains("a", "b"));
        assertThat(gs.getPath("a", "c"), contains("a", "b", "c")); // calculates a new path
        assertThat(gs.getPath("a", "f"), contains("a", "i", "h", "f"));
        assertThat(gs.getPath("a", "e"), contains("a", "b", "c", "d", "e"));
        assertThat(gs.getPath("a", "j"), contains("a", "i", "h", "g", "j"));
    }
}
