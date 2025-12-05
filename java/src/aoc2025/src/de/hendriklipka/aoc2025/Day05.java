package de.hendriklipka.aoc2025;


import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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
        List<Pair<Long, Long>> ranges = blocks.getFirst().stream().map(AocParseUtils::parseLongRange).toList();
        return blocks.get(1).stream().mapToLong(Long::parseLong).filter(i->isFresh(i, ranges)).count();
    }

    private boolean isFresh(final long id, final List<Pair<Long, Long>> ranges)
    {
        return ranges.stream().anyMatch(range->isInRange(id, range));
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<List<String>> blocks = data.getStringBlocks();
        List<Pair<Long, Long>> ranges = blocks.getFirst().stream().map(AocParseUtils::parseLongRange).toList();
        return mergeRanges(new ArrayList<>(ranges)).stream().mapToLong(r-> r.getRight() - r.getLeft() + 1).sum();
    }

    private List<Pair<Long, Long>> mergeRanges(final List<Pair<Long, Long>> ranges)
    {
        final List<Pair<Long, Long>> result=new ArrayList<>();
        while (!ranges.isEmpty())
        {
            // take the first remaining range
            Pair<Long, Long> range = ranges.removeFirst();
            // look if there is _any_ range we can merge this with
            while (true)
            {
                boolean merged = false;
                // check all other ranges
                // when we found something to merge, we always stop and check from the start - there might now be new candidates to also merge
                for (Pair<Long, Long> other : ranges)
                {
                    // the other range is completely inside this range
                    if (isInRange(other.getLeft(), range) && isInRange(other.getRight(), range))
                    {
                        // just remove it from the list, nothing needs to be done
                        ranges.remove(other);
                        merged=true;
                        break;
                    }
                    // the start of the other range is inside this range
                    else if (isInRange(other.getLeft(), range))
                    {
                        // so we extend our range to the right
                        range=Pair.of(range.getLeft(), other.getRight());
                        ranges.remove(other);
                        merged=true;
                        break;
                    }
                    // the end of the other range is inside this range
                    else if (isInRange(other.getRight(), range))
                    {
                        // so we extend our range to the left
                        range=Pair.of(other.getLeft(), range.getRight());
                        ranges.remove(other);
                        merged=true;
                        break;
                    }
                    // the current range is completely inside the other range
                    if (isInRange(range.getLeft(), other) && isInRange(range.getRight(), other))
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

    private boolean isInRange(final Long id, final Pair<Long, Long> range)
    {
        return range.getLeft() <= id && id <= range.getRight();
    }
}
