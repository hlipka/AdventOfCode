package de.hendriklipka.aoc2022.day04;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.RangeLong;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;

/**
 * User: hli
 * Date: 04.12.22
 * Time: 20:03
 */
public class Day041
{
    public static void main(String[] args)
    {
        try
        {
            final List<String> lines = AocDataFileUtils.getLines("2022", "day04");
            long count = lines.stream().map(Day041::getRanges).filter(Day041::isOverlap).count();
            System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static boolean isOverlap(final Pair<RangeLong, RangeLong> r)
    {
        final RangeLong left = r.getLeft();
        final RangeLong right = r.getRight();
        return left.isInsideOf(right) || right.isInsideOf(left);
    }

    private static Pair<RangeLong, RangeLong> getRanges(final String s)
    {
        String[] ranges = s.split(",");
        final ImmutablePair<RangeLong, RangeLong> pair = new ImmutablePair<>(new RangeLong(ranges[0]), new RangeLong(ranges[1]));
        System.out.println(pair);
        return pair;
    }
}
