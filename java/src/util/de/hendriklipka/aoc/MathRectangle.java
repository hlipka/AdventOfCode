package de.hendriklipka.aoc;

/**
 * Rectangle class which is a mathematical one - so rectangles which share a corner are _not_ overlapping each other.
 */
public class MathRectangle
{
    int x1,y1,x2,y2;

    public MathRectangle(int x1, int y1, int x2, int y2)
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

    public boolean overlaps(MathRectangle other)
    {
        return (
                (inRange(other.x1,x1,x2)
                || inRange(other.x2, x1, x2)
                || inRange(x1, other.x1, other.x2)
                || inRange(x2, other.x1, other.x2)
                || (getStartX()==other.getStartX() && getEndX()==other.getEndX())
                )
                &&
                (inRange(other.y1, y1, y2)
                 || inRange(other.y2, y1, y2)
                 || inRange(y1, other.y1, other.y2)
                 || inRange(y2, other.y1, other.y2)
                 || (getStartY() == other.getStartY() && getEndY() == other.getEndY())
                )
        );
    }

    private boolean inRange(int i, int i1, int i2)
    {
        return i>Math.min(i1,i2) && i<Math.max(i1,i2);
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
