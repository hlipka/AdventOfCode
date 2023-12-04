package de.hendriklipka.aoc2016.day15;

import de.hendriklipka.aoc.AocParseUtils;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 04.12.23
 * Time: 22:17
 */
public class Day15b
{
    public static void main(String[] args)
    {
        try
        {
            final List<Disc> discs = AocParseUtils.getLines("2016", "day15").stream().map(Day15b::parseDisc).toList();

            int time=0;
            while (true)
            {
                boolean found = true;
                for (Disc disc: discs)
                {
                    if (0!=((disc._at+time)%disc._size))
                    {
                        found=false;
                    }
                }
                if (found)
                {
                    break;
                }
                time++;
            }
            System.out.println(time);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static Disc parseDisc(String line)
    {
        final int[] nums = AocParseUtils.parsePartsFromString(line, "Disc #(\\d) has (\\d+) positions; at time=0, it is at position (\\d+).").stream().mapToInt(Integer::parseInt).toArray();
        int discNum=nums[0];
        int size=nums[1];
        int start=nums[2];
        int openAt = (discNum+start)%size;
        return new Disc(size, openAt);
    }

    private static class Disc
    {
        private final int _size;
        private final int _at;

        public Disc(final int size, final int openAt)
        {
            _size = size;
            _at = openAt;
        }

        @Override
        public String toString()
        {
            return "Disc{" +
                    "_size=" + _size +
                    ", _at=" + _at +
                    '}';
        }
    }
}
