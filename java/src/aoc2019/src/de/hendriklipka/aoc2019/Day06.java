package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day06 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day06().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        // keys are the bodies, values are their center of orbit
        Map<String, String> centers=new HashMap<>();
        data.getLines().forEach(l->addOrbit(l, centers));
        return centers.keySet().stream().mapToInt(b->countParents(b, centers)).sum();
    }

    private int countParents(final String b, final Map<String, String> centers)
    {
        String parent=centers.get(b);
        if ("COM".equals(parent))
            return 1;
        return 1+countParents(parent, centers);
    }

    private void addOrbit(final String line, final Map<String, String> centers)
    {
        String[] parts = line.split("\\)");
        centers.put(parts[1], parts[0]);
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // keys are the bodies, values are their center of orbit
        Map<String, String> centers = new HashMap<>();
        data.getLines().forEach(l -> addOrbit(l, centers));
        List<String> you=new ArrayList<>();
        findPath(you, "YOU", centers);
        List<String> san=new ArrayList<>();
        findPath(san, "SAN", centers);
        for (int i=0;i<you.size();i++)
        {
            int s=san.indexOf(you.get(i));
            if (s!=-1)
            {
                return s+i;
            }
        }

        return null;
    }

    private void findPath(final List<String> path, final String start, final Map<String, String> centers)
    {
        String parent = centers.get(start);
        if ("COM".equals(parent))
            return;
        path.add(parent);
        findPath(path, parent, centers);
    }
}
