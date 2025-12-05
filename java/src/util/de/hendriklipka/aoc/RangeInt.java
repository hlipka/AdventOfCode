package de.hendriklipka.aoc;

public class RangeInt
{
    int from;
    int to;


    public RangeInt(String range)
    {
        String[] ranges = range.split("\\-");
        from = Integer.parseInt(ranges[0]);
        to = Integer.parseInt(ranges[1]);
    }

    public RangeInt(int from, int to)
    {
        this.from = from;
        this.to = to;
    }

    public int getFrom()
    {
        return from;
    }

    public int getTo()
    {
        return to;
    }

    public boolean isInsideOf(final RangeInt other)
    {
        return from>=other.from && to<=other.to;
    }

    public boolean insideThisRange(final int value)
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

    public boolean overlapsWith(final RangeInt other)
    {
        return insideThisRange(other.from) || insideThisRange(other.to);
    }
}
