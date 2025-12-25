package de.hendriklipka.aoc2025;


import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Range;

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
        List<Range> ranges = blocks.getFirst().stream().map(Range::new).toList();
        return blocks.get(1).stream().mapToLong(Long::parseLong).filter(i->isFresh(i, ranges)).count();
    }

    private boolean isFresh(final long id, final List<Range> ranges)
    {
        return ranges.stream().anyMatch(range->isInRange(id, range));
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<List<String>> blocks = data.getStringBlocks();
        List<Range> ranges = blocks.getFirst().stream().map(Range::new).toList();
        return Range.mergeRanges(new ArrayList<>(ranges)).stream().mapToLong(r-> r.getTo() - r.getFrom() + 1).sum();
    }

    private boolean isInRange(final Long id, final Range range)
    {
        return range.getFrom() <= id && id <= range.getTo();
    }
}
