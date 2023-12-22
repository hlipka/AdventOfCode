package de.hendriklipka.aoc;

/**
 * User: hli
 * Date: 22.12.23
 * Time: 15:01
 */
public class Rectangle
{
    int x1,y1,x2,y2;

    public Rectangle(int x1, int y1, int x2, int y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public boolean contains(int x, int y)
    {
        return (x>=Math.min(x1,x2) &&x<=Math.max(x1,x2) && y >= Math.min(y1, y2) && y <= Math.max(y1, y2));
    }

    public boolean overlaps(Rectangle other)
    {
        return (
                (inRange(other.x1,x1,x2)
                || inRange(other.x2, x1, x2)
                || inRange(x1, other.x1, other.x2)
                || inRange(x2, other.x1, other.x2))
                &&
                (inRange(other.y1, y1, y2)
                 || inRange(other.y2, y1, y2)
                 || inRange(y1, other.y1, other.y2)
                 || inRange(y2, other.y1, other.y2))
        );
    }

    private boolean inRange(int i, int i1, int i2)
    {
        return i>=Math.min(i1,i2) && i<=Math.max(i1,i2);
    }

    public int getStartX()
    {
        return Math.min(x1,x2);
    }

    public int getEndX()
    {
        return Math.max(x1,x2);
    }

    public int getStartY()
    {
        return Math.min(y1, y2);
    }

    public int getEndY()
    {
        return Math.max(y1, y2);
    }
}
