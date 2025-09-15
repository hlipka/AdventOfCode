package de.hendriklipka.aoc;

/**
 * User: hli
 */
public record Position3D(int x, int y, int z)
{
    public int distance(final Position3D other)
    {
        return Math.abs(x-other.x)+Math.abs(y-other.y)+Math.abs(z-other.z);
    }
}
