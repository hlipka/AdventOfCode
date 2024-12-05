package de.hendriklipka.aoc2023.day25;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.search.GraphSearch;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

/**
 * User: hli
 * Date: 25.12.23
 * Time: 16:54
 */
public class Day25
{
    static MultiValuedMap<String, String> wires = new ArrayListValuedHashMap<>();
    public static void main(String[] args) throws IOException
    {
        AocDataFileUtils.getLines("2023", "day25").forEach(Day25::parseLine);
        List<String> allNodes=new ArrayList<>(wires.keySet()); // ordered list so we can access by index

        final GraphSearch gs = createGraph(new ArrayList<String>(wires.keySet()));

        // for 1000 random paths, determine which edge gets used how often - the most common ones are probably the ones we need to cut
        Map<String, Integer> edgeCount = new HashMap<>();
        Random r=new Random();
        for (int i=0;i<1000;i++)
        {
            String n1=allNodes.get(r.nextInt(allNodes.size()));
            String n2=allNodes.get(r.nextInt(allNodes.size()));
            if (n1.equals(n2))
                continue;
            List<String> path = findPath(gs, n1, n2);
            for (int p=0;p<path.size()-1;p++)
            {
                // sort the node names so we do not get duplicates
                String name= path.get(p).hashCode() < path.get(p + 1).hashCode()?path.get(p) + "-" + path.get(p + 1): path.get(p+1) + "-" + path.get(p);
                Integer c=edgeCount.getOrDefault(name, 0);
                edgeCount.put(name, c+1);
            }
        }
        // get the 3 most common edges
        List<Pair<String, String>> first = edgeCount.entrySet().stream().sorted(Comparator.comparingInt(e -> -e.getValue())).limit(3).map(
                Map.Entry::getKey).map(e-> {String[] p=e.split("-"); return Pair.of(p[0], p[1]);}).toList();

        // remove these wires (determined by looking at the output from above)
        wires.removeMapping(first.get(0).getLeft(), first.get(0).getRight());
        wires.removeMapping(first.get(0).getRight(), first.get(0).getLeft());
        wires.removeMapping(first.get(1).getLeft(), first.get(1).getRight());
        wires.removeMapping(first.get(1).getRight(), first.get(1).getLeft());
        wires.removeMapping(first.get(2).getLeft(), first.get(2).getRight());
        wires.removeMapping(first.get(2).getRight(), first.get(2).getLeft());

        // we can use the separated nodes from above as starting points to count the groups
        int count1=reachableFrom(first.get(0).getLeft());
        int count2=reachableFrom(first.get(0).getRight());
        // sanity check
        System.out.println(allNodes.size()+"<=>"+(count1+count2));
        System.out.println(count1*count2);

    }

    private static GraphSearch createGraph(List<String> allNodes)
    {
        GraphSearch gs=new GraphSearch();
        for (String n: allNodes)
        {
            gs.addNode(n);
        }
        for (String n : wires.keys())
        {
            for(String w: wires.get(n))
                gs.addEdge(n, w, 1);
        }
        return gs;
    }

    private static int reachableFrom(String startNode)
    {
        Set<String> nodes=new HashSet<>();
        LinkedList<String> toVisit=new LinkedList<>();
        toVisit.add(startNode);
        while(!toVisit.isEmpty())
        {
            String next=toVisit.poll();
            if (nodes.contains(next))
                continue;
            nodes.add(next);
            toVisit.addAll(wires.get(next));
        }
        return nodes.size();
    }

    private static List<String> findPath(GraphSearch gs, String n1, String n2)
    {
        return gs.getPath(n1, n2);
    }

    private static void parseLine(String line)
    {
        String from=AocParseUtils.parseStringFromString(line, "(\\w+): .*");
        String[] to= StringUtils.split(AocParseUtils.parseStringFromString(line, "\\w+: (.*)"),' ');
        for (String w: to)
        {
            // add both wires
            wires.put(from, w);
            wires.put(w, from);
        }
    }
}
