package de.hendriklipka.aoc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Range
{
    long from;
    long to;


    public Range(String range)
    {
        String[] ranges = range.split("\\-");
        from = Long.parseLong(ranges[0]);
        to = Long.parseLong(ranges[1]);
    }

    public Range(long from, long to)
    {
        this.from = from;
        this.to = to;
    }

    public long getFrom()
    {
        return from;
    }

    public long getTo()
    {
        return to;
    }

    public boolean isInsideOf(final Range other)
    {
        return from>=other.from && to<=other.to;
    }

    public boolean insideThisRange(final long value)
    {
        return from<=value && value<=to;
    }

    @Override
    public String toString()
    {
        return "Range{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }

    public boolean overlapsWith(final Range other)
    {
        return insideThisRange(other.from) || insideThisRange(other.to);
    }

    public boolean canBeMergedWith(final Range other)
    {
        return overlapsWith(other) || isInsideOf(other) || other.isInsideOf(this);
    }

    public void merge(final Range other)
    {
        from=Math.min(from, other.from);
        to=Math.max(to, other.to);
    }

    public static List<Range> mergeRanges(final List<Range> ranges)
    {
        ranges.sort(Comparator.comparing(Range::getFrom));
        final List<Range> result = new ArrayList<>();
        while (!ranges.isEmpty())
        {
            // take the first remaining range
            Range range = ranges.removeFirst();
            // look if there is _any_ range we can merge this with
            while (true)
            {
                boolean merged = false;
                // check all other ranges
                // when we found something to merge we always stop and check from the start - there might now be new candidates to also merge
                for (Range other : ranges)
                {
                    if (range.canBeMergedWith(other))
                    {
                        range.merge(other);
                        ranges.remove(other);
                        merged = true;
                        break;
                    }
                }
                // when no other ranges overlap, we are done and the current range goes into the result
                if (!merged)
                {
                    result.add(range);
                    break;
                }
            }
        }
        return result;
    }
}
