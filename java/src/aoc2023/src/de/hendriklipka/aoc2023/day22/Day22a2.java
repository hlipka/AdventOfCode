package de.hendriklipka.aoc2023.day22;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.GridRectangle;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.io.IOException;
import java.util.*;

/**
 * A more simplified (and probably even faster) version
 */
public class Day22a2
{
    public static void main(String[] args)
    {
        try
        {
            // we sort the brick by their bottom Z
            // two bricks with the same Z must be next to each other and don't interfere ever
            List<Brick> bricks =
                    AocParseUtils.getLines("2023", "day22").stream().map(Day22a2::parseBrick).sorted(Comparator.comparingInt(Brick::getZBottom)).toList();
            // let the bricks fall down from bottom to top
            MultiValuedMap<Integer, Brick> brickTops = new ArrayListValuedHashMap<>();
            for (Brick brick : bricks)
            {
                moveBrick(brick, brickTops);
            }

            // sanity check: there should be no bricks floating around
            for (Brick brick : bricks)
            {
                if (brick.getZBottom()>1 && brick.getBricksBelow().isEmpty())
                {
                    System.out.println("floating brick: "+brick);
                }
            }

            // count which can be removed
            int count=countRemoveableBrick(bricks);
            System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static int countRemoveableBrick(List<Brick> bricks)
    {
        int count=0;
        // a brick can be removed if there is nothing on top or each brick on top of it is supported by more than 1 brick,
        // so we check if there is a brick supported by it which has only one supporting brick
        // (when nothing is on top, this also counts)
        for (Brick brick : bricks)
        {
            boolean canBeRemoved=true;
            Collection<Brick> bricksAbove = brick.getBricksAbove();
            for (Brick brickAbove: bricksAbove)
            {
                if (brickAbove.getBricksBelow().size() == 1)
                {
                    canBeRemoved=false;
                    break;
                }
            }
            if (canBeRemoved)
            {
                count++;
            }
        }

        return count;
    }

    private static void moveBrick(Brick brick, MultiValuedMap<Integer, Brick> settledBricks)
    {
        GridRectangle bottom=brick.getArea();
        int bottomZ=brick.getZBottom();
        while (bottomZ>1) // at bottom=1 we already touch the ground
        {
            Collection<Brick> bricks = settledBricks.get(bottomZ - 1); // are there any bricks directly below us?
            boolean touchedOne=false;
            // check _all_ bricks ending at this height whether we touch them or not
            for (Brick brickBelow: bricks)
            {
                // do we really touch this brick?
                if (bottom.overlaps(brickBelow.getArea()))
                {
                    // since they touch, they have a 'support' relation
                    brick.addBrickBelow(brickBelow);
                    brickBelow.addBrickAbove(brick);
                    touchedOne=true;
                }
            }
            if (touchedOne)
                break;
            bottomZ--;
        }

        // the highest Z is how deep we can fall, so move the brick
        brick.moveDownTo(bottomZ);
        // mark the brick as settled down
        settledBricks.put(brick.getZTop(), brick);
    }

    private static Brick parseBrick(String line)
    {
        List<String> parts = AocParseUtils.parsePartsFromString(line, "(\\d+),(\\d+),(\\d+)~(\\d+),(\\d+),(\\d+)");
        return new Brick(
                Integer.parseInt(parts.get(0))
                ,Integer.parseInt(parts.get(1))
                ,Integer.parseInt(parts.get(2))
                ,Integer.parseInt(parts.get(3))
                ,Integer.parseInt(parts.get(4))
                ,Integer.parseInt(parts.get(5))
        );
    }

    private static class Brick
    {
        public int xBottom,yBottom,zBottom,xTop,yTop,zTop;

        Set<Brick> bricksAbove = new HashSet<>();
        Set<Brick> bricksBelow = new HashSet<>();

        public Brick(int x1, int y1, int z1, int x2, int y2, int z2)
        {
            this.xBottom = Math.min(x1, x2);
            this.yBottom = Math.min(y1, y2);
            this.zBottom = Math.min(z1, z2);
            this.xTop = Math.max(x1, x2);
            this.yTop = Math.max(y1, y2);
            this.zTop = Math.max(z1, z2);
        }

        public int getZBottom()
        {
            return zBottom;
        }

        public int getZTop()
        {
            return zTop;
        }

        public GridRectangle getArea()
        {
            return new GridRectangle(xBottom, yBottom, xTop, yTop);
        }

        public void moveDownTo(int topZ)
        {
            if (topZ!=zBottom)
            {
                int diff=zBottom-topZ;
                zBottom-=diff;
                zTop-=diff;
            }
        }

        public void addBrickAbove(Brick other)
        {
            bricksAbove.add(other);
        }

        public void addBrickBelow(Brick other)
        {
            bricksBelow.add(other);
        }

        public Set<Brick> getBricksAbove()
        {
            return bricksAbove;
        }

        public Set<Brick> getBricksBelow()
        {
            return bricksBelow;
        }

        @Override
        public String toString()
        {
            return "Brick{" +
                   "x1=" + xBottom +
                   ", y1=" + yBottom +
                   ", x2=" + xTop +
                   ", y2=" + yTop +
                   ", zBottom=" + zBottom +
                   ", zTop=" + zTop +
                   '}';
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Brick brick = (Brick) o;
            return xBottom == brick.xBottom && yBottom == brick.yBottom && zBottom == brick.zBottom && xTop == brick.xTop && yTop == brick.yTop && zTop == brick.zTop;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(xBottom, yBottom, zBottom, xTop, yTop, zTop);
        }
    }
}
