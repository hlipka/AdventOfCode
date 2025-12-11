package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.vizualization.VizNode;
import de.hendriklipka.aoc.vizualization.VizUtils;
import guru.nidi.graphviz.attribute.Attributes;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.ForAll;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Day11 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day11().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<Device> devices=data.getLines().stream().map(Device::new).toList();
        Map<String, List<String>> connections=new HashMap<>();
        for (Device device : devices)
        {
            connections.put(device.id, device.outputs);
        }
        Map<String, Integer> cache=new HashMap<>();

        return countConnections("you", connections,cache, "out");
    }

    private int countConnections(final String node, final Map<String, List<String>> connections, final Map<String, Integer> cache, final String target)
    {
        Integer cached=cache.get(node);
        if (null!=cached)
            return cached;
        if (node.equals(target))
            return 1;
        int count=0;

        final var targets = connections.get(node);
        if(null==targets)
        {
            System.err.println("No targets for node "+node);
        }
        for (String n: targets)
        {
            count+=countConnections(n,connections,cache, target);
        }
        cache.put(node, count);
        return count;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<Device> devices = data.getLines().stream().map(Device::new).toList();
        //visualize(devices);
        Map<String, List<String>> connections = new HashMap<>();
        for (Device device : devices)
        {
            connections.put(device.id, device.outputs);
        }
        connections.put("out", List.of());
        Map<String, Integer> cache = new HashMap<>();

        // from srv to fft, then dac, then out (grabbed from visualization)
        int c1=countConnections("svr", connections, cache, "fft");
        cache = new HashMap<>();
        int c2=countConnections("fft", connections, cache, "dac");
        cache = new HashMap<>();
        int c3=countConnections("dac", connections, cache, "out");

        return (long)c1* (long)c2* (long)c3;
    }

    private static void visualize(final List<Device> devices)
    {
        final Map<String, DeviceNode> nodes=new  HashMap<>();
        for (Device device : devices)
        {
            nodes.put(device.id, new DeviceNode(device));
        }
        nodes.put("out", new DeviceNode(new Device("out: ")));

        VizUtils.visualizeGraph("devices", nodes, new Function<String, Attributes<ForAll>[]>()
        {
            @Override
            public Attributes<ForAll>[] apply(final String s)
            {
                if (s.equals("fft"))
                    return new Attributes[]{Color.RED};
                if (s.equals("dac"))
                    return new Attributes[]{Color.RED};
                return null;
            }
        });
    }

    private static class Device
    {
        String id;
        List<String> outputs=new ArrayList<>();

        Device(String line)
        {
            String[] parts=line.split(" ");
            this.id=parts[0].substring(0, parts[0].length()-1);
            this.outputs.addAll(Arrays.asList(parts).subList(1, parts.length));
        }
    }

    private record DeviceNode(Device _device) implements VizNode
    {
        @Override
        public String getNodeName()
        {
            return _device.id;
        }

        @Override
        public Collection<String> getNodeTargets()
        {
            return _device.outputs;
        }
    }
}
