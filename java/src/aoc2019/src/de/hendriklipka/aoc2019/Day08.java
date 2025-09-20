package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day08 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day08().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<Integer> pixels = data.getLinesAsDigits().get(0);
        final List<List<Integer>> layers = ListUtils.partition(pixels, 25 * 6);
        final List<Integer> layer = layers.stream().sorted(Comparator.comparingInt(l -> getCount(l, 0))).findFirst().orElseThrow();

        return getCount(layer,1) * getCount(layer,2);
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<Integer> pixels = data.getLinesAsDigits().get(0);
        final List<List<Integer>> layers = new ArrayList<>(ListUtils.partition(pixels, 25 * 6));
        final List<Integer> image=layers.get(0);
        layers.remove(0);
        for (List<Integer> layer:layers)
        {
            for (int i=0;i<layer.size();i++)
            {
                if (2==image.get(i))
                {
                    image.set(i, layer.get(i));
                }
            }
        }
        final List<List<Integer>> rows = ListUtils.partition(image, 25);
        for (List<Integer> row:rows)
        {
            System.out.println(StringUtils.join(row.stream().map(i->0==i?' ':'#').toList(),""));
        }

        return null;
    }

    private int getCount(List<Integer> pixels, int pixel)
    {
        return (int)(pixels.stream().filter(p -> p == pixel).count());
    }
}
