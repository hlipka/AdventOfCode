package de.hendriklipka.aoc2025;


import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.RangeLong;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day05 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day05().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<List<String>> blocks = data.getStringBlocks();
        List<RangeLong> ranges = blocks.getFirst().stream().map(RangeLong::new).toList();
        return blocks.get(1).stream().mapToLong(Long::parseLong).filter(i->isFresh(i, ranges)).count();
    }

    private boolean isFresh(final long id, final List<RangeLong> ranges)
    {
        return ranges.stream().anyMatch(range->isInRange(id, range));
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<List<String>> blocks = data.getStringBlocks();
        List<RangeLong> ranges = blocks.getFirst().stream().map(RangeLong::new).toList();
        return mergeRanges(new ArrayList<>(ranges)).stream().mapToLong(r-> r.getTo() - r.getFrom() + 1).sum();
    }

    private List<RangeLong> mergeRanges(final List<RangeLong> ranges)
    {
        final List<RangeLong> result=new ArrayList<>();
        while (!ranges.isEmpty())
        {
            // take the first remaining range
            RangeLong range = ranges.removeFirst();
            // look if there is _any_ range we can merge this with
            while (true)
            {
                boolean merged = false;
                // check all other ranges
                // when we found something to merge, we always stop and check from the start - there might now be new candidates to also merge
                for (RangeLong other : ranges)
                {
                    // the other range is completely inside this range
                    if (isInRange(other.getFrom(), range) && isInRange(other.getTo(), range))
                    {
                        // just remove it from the list, nothing needs to be done
                        ranges.remove(other);
                        merged=true;
                        break;
                    }
                    // the start of the other range is inside this range
                    else if (isInRange(other.getFrom(), range))
                    {
                        // so we extend our range to the right
                        range=new RangeLong(range.getFrom(), other.getTo());
                        ranges.remove(other);
                        merged=true;
                        break;
                    }
                    // the end of the other range is inside this range
                    else if (isInRange(other.getTo(), range))
                    {
                        // so we extend our range to the left
                        range=new RangeLong(other.getFrom(), range.getTo());
                        ranges.remove(other);
                        merged=true;
                        break;
                    }
                    // the current range is completely inside the other range
                    if (isInRange(range.getFrom(), other) && isInRange(range.getTo(), other))
                    {
                        // so swap them
                        range=other;
                        ranges.remove(other);
                        merged=true;
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

    private boolean isInRange(final Long id, final RangeLong range)
    {
        return range.getFrom() <= id && id <= range.getTo();
    }
}
