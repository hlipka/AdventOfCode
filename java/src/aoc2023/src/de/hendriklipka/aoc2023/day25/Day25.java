package de.hendriklipka.aoc2023.day25;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.search.GraphSearch;
import de.hendriklipka.aoc.vizualization.VizNode;
import de.hendriklipka.aoc.vizualization.VizUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;

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
    static Set<String> devices=new HashSet<>();
    public static void main(String[] args) throws IOException
    {
        AocParseUtils.getLines("2023", "day25").forEach(Day25::parseLine);
        List<String> allNodes=new ArrayList<>(devices);
        Map<String, Integer> edgeCount=new HashMap<>();
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
        // for 1000 random path, determine which edge gets used how often - the most common ones are probably the ones we need to cut
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
                // this actually adds the edges in both directions, so maybe we should sort them
                // then we could to get the first 3 automatically
                String name=path.get(p)+"-"+path.get(p+1);
                Integer c=edgeCount.getOrDefault(name, 0);
                edgeCount.put(name, c+1);
            }
        }
        List<String> first = edgeCount.entrySet().stream().sorted(Comparator.comparingInt(e -> -e.getValue())).limit(10).map(
                e -> e.getKey()+":"+e.getValue()).toList();
        System.out.println(StringUtils.join(first," - "));

        // remove these wires (determined by looking at the output from above)
        wires.removeMapping("vfs", "dhl");
        wires.removeMapping("dhl", "vfs");
        wires.removeMapping("nzn", "pbq");
        wires.removeMapping("pbq", "nzn");
        wires.removeMapping("zpc", "xvp");
        wires.removeMapping("xvp", "zpc");

        // we can use the separated nodes from above as starting points to count the groups
        int count1=reachableFrom("vfs");
        int count2=reachableFrom("dhl");
        System.out.println(count1);
        System.out.println(count2);
        System.out.println(allNodes.size()+"/"+(count1+count2));
        System.out.println(count1*count2);

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
            devices.add(w);
        }
        devices.add(from);
    }

    static class Device implements VizNode
    {
        List<String> wires=new ArrayList<>();
        private String name;

        Device(String name)
        {
            this.name = name;
        }

        @Override
        public String getNodeName()
        {
            return name;
        }

        public void addWire(String wire)
        {
            wires.add(wire);
        }

        @Override
        public Collection<String> getNodeTargets()
        {
            return wires;
        }
    }
}
