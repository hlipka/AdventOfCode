package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Day13 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day13().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<Pair<Integer, Integer>> layerData = data.getLines().stream().map(this::parseLayer).toList();
        int size=layerData.stream().mapToInt(Pair::getLeft).max().orElseThrow();
        int[] layers = new int[size + 1];
        Arrays.fill(layers, -1);
        for (Pair<Integer, Integer> layer: layerData)
        {
            layers[layer.getLeft()]=layer.getRight();
        }
        int cost=0;
        for (int l=0;l<layers.length;l++)
        {
            final var range = layers[l];
            if (-1 == range)
                continue;
            // this is the period where this scanner is right at the top (and can hit us)
            int period= range * 2 - 2;
            if (0==l%period)
            {
                cost+=range*l;
            }
        }
        return cost;
    }

    private Pair<Integer, Integer> parseLayer(final String layer)
    {
        final List<String> parts = AocParseUtils.getGroupsFromLine(layer, "(\\d+): (\\d+)");
        return Pair.of(Integer.parseInt(parts.get(0)), Integer.parseInt(parts.get(1)));
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<Pair<Integer, Integer>> layerData = data.getLines().stream().map(this::parseLayer).toList();
        int size = layerData.stream().mapToInt(Pair::getLeft).max().orElseThrow();
        int[] periods = new int[size + 1];
        Arrays.fill(periods, -1);
        for (Pair<Integer, Integer> layer : layerData)
        {
            // directly calculate the periods, we don't need the range
            periods[layer.getLeft()] = layer.getRight()*2-2;
        }
        // just brute force all possible delays, our calculation is fast enough
        int delay=0;
        while (true)
        {
            boolean hit=false;
            for (int l = 0; l < periods.length; l++)
            {
                int period=periods[l];
                if (-1 == period)
                    continue;
                if (0 == (l+delay) % period)
                {
                    hit=true;
                    break;
                }
            }
            if (!hit)
                break;
            delay++;
        }
        return delay;
    }
}
