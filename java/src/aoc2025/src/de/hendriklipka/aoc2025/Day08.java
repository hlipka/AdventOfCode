package de.hendriklipka.aoc2025;


import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class Day08 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day08().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final int count=isExample?10:1000;
        final List<String> lines = data.getLines();
        final Set<Box> boxes=new HashSet<>();
        final List<Set<Box>> circuits = createEmptyCircuits(lines, boxes);
        final List<Pair<Box, Box>> distances = calculateDistances(boxes);
        for (int i=0;i<count;i++)
        {
            connect(distances.getFirst().getLeft(), distances.getFirst().getRight(), circuits);
            distances.removeFirst();
        }
        circuits.sort(Comparator.comparingInt(boxes1 -> -boxes1.size()));
        return circuits.stream().mapToInt(Set::size).limit(3).reduce(1, (a, b)-> a * b);
    }

    private List<Pair<Box, Box>> calculateDistances(final Set<Box> boxes)
    {
        final List<Pair<Box, Box>> distances=new ArrayList<>();
        for (Box box : boxes)
        {
            for (Box other : boxes)
            {
                if (box.num < other.num)
                {
                    distances.add(Pair.of(box, other));
                }
            }
        }
        distances.sort((p1, p2) -> Long.compare(dist(p1), dist(p2)));
        return distances;
    }

    private static List<Set<Box>> createEmptyCircuits(final List<String> lines, final Set<Box> boxes)
    {
        final List<Set<Box>> circuits=new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++)
        {
            Set<Box> circuit=new HashSet<>();
            final Box box = new Box(lines.get(i), i, circuit);
            boxes.add(box);
            circuit.add(box);
            circuits.add(circuit);
        }
        return circuits;
    }

    private void connect(final Box left, final Box right, final List<Set<Box>> circuits)
    {
        final Set<Box> leftCircuit = left.circuit;
        final Set<Box> rightCircuit = right.circuit;

        // already in the same circuit?
        if (leftCircuit.contains(right))
            return;

        // connect both circuits together
        leftCircuit.addAll(rightCircuit);
        for (Box box: leftCircuit)
            box.circuit= leftCircuit;

        // make sure only one circuit is left in the list
        circuits.remove(rightCircuit);
        circuits.remove(leftCircuit);
        circuits.add(leftCircuit);
    }

    private long dist(final Pair<Box, Box> p1)
    {
        return (long)(p1.getLeft().x-p1.getRight().x)*(p1.getLeft().x-p1.getRight().x)
               + (long)(p1.getLeft().y-p1.getRight().y)*(p1.getLeft().y-p1.getRight().y)
               + (long)(p1.getLeft().z-p1.getRight().z)*(p1.getLeft().z-p1.getRight().z);
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<String> lines = data.getLines();
        final Set<Box> boxes = new HashSet<>();
        final List<Set<Box>> circuits = createEmptyCircuits(lines, boxes);
        final List<Pair<Box, Box>> distances = calculateDistances(boxes);
        // connect circuits until we have one circuit for the first time
        while (true)
        {
            final Pair<Box, Box> pair = distances.getFirst();
            connect(pair.getLeft(), pair.getRight(), circuits);
            distances.removeFirst();
            if (circuits.size() == 1)
            {
                return pair.getLeft().x*pair.getRight().x;
            }
        }
    }

    private static class Box
    {
        int x, y, z;
        int num;
        Set<Box> circuit;

        Box(String line, int num, final Set<Box> circuit)
        {
            String[] parts=line.split(",");
            x=Integer.parseInt(parts[0]);
            y=Integer.parseInt(parts[1]);
            z=Integer.parseInt(parts[2]);
            this.num=num;
            this.circuit=circuit;
        }

        @Override
        public String toString()
        {
            return "Box{" +
                   "x=" + x +
                   ", y=" + y +
                   ", z=" + z +
                   '}';
        }
    }
}
