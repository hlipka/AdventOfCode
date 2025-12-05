package de.hendriklipka.aoc;

/**
 * User: hli
 * Date: 04.12.22
 * Time: 20:04
 */
public class RangeLong
{
    long from;
    long to;


    public RangeLong(String range)
    {
        String[] ranges = range.split("\\-");
        from = Long.parseLong(ranges[0]);
        to = Long.parseLong(ranges[1]);
    }

    public RangeLong(long from, long to)
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

    public boolean isInsideOf(final RangeLong other)
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

    public boolean overlapsWith(final RangeLong other)
    {
        return insideThisRange(other.from) || insideThisRange(other.to);
    }
}
