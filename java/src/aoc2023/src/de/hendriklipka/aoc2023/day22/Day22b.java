package de.hendriklipka.aoc2023.day22;

import de.hendriklipka.aoc.AocDataFileUtils;
import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.GridRectangle;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.io.IOException;
import java.util.*;

/**
 * User: hli
 * Date: 22.12.23
 * Time: 14:15
 */
public class Day22b
{
    public static void main(String[] args)
    {
        try
        {
            // we sort the brick by their bottom Z
            // two bricks with the same Z must be next to each other and don't interfere ever
            List<Brick> bricks =
                    AocDataFileUtils.getLines("2023", "day22").stream().map(Day22b::parseBrick).sorted(Comparator.comparingInt(Brick::getZBottom)).toList();
            // let the bricks fall down from bottom to top
            for (int i=0;i<bricks.size();i++)
            {
                Brick brick=bricks.get(i);
                // get the bricks below (or next to) the current brick
                final List<Brick> foundation = new ArrayList<>(bricks.subList(0, i));
                // the list is sorted by the top Z, so the highest brick comes first
                foundation.sort(Comparator.comparingInt(Brick::getZTop));
                Collections.reverse(foundation);
                moveBrick(brick, foundation);
            }
            // we now have the stack of brick in rest

            // calculate which brick is supported by which other brick, and what rests on what else
            MultiValuedMap<Brick, Brick> supports=new ArrayListValuedHashMap<>();
            MultiValuedMap<Brick, Brick> supportedBy=new ArrayListValuedHashMap<>();
            calculateSupports(bricks, supports, supportedBy);

            int totalCount=0;
            // remove each brick from the pile
            for (Brick brick : bricks)
            {
                // since we know the support 'tree', we can calculate the chain reaction, using a queue
                // mark a brick as removed, and check all bricks supported by it

                // bricks which did already fall down
                Set<Brick> fallenDown = new HashSet<>();
                fallenDown.add(brick);
                // bricks we need to check - might have duplicates (when they are supported by multiple bricks who all fall down)
                LinkedList<Brick> mightFallDown = new LinkedList<>(supports.get(brick));
                while (!mightFallDown.isEmpty())
                {
                    Brick check=mightFallDown.poll();
                    // get the list of bricks this is supported by
                    Collection<Brick> mySupports=supportedBy.get(check);
                    // when all of these are already removed, this brick will also fall down
                    if (fallenDown.containsAll(mySupports))
                    {
                        fallenDown.add(check);
                        mightFallDown.addAll(supports.get(check));
                    }
                }
                totalCount+=fallenDown.size()-1; // one less because we also added the removed brick here
            }
            System.out.println(totalCount);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void calculateSupports(List<Brick> bricks, MultiValuedMap<Brick, Brick> supports, MultiValuedMap<Brick, Brick> supportedBy)
    {
        // remember which brick ends where (on the top side)
        MultiValuedMap<Integer, Brick> brickTops=new ArrayListValuedHashMap<>();
        for (Brick brick: bricks)
        {
            brickTops.put(brick.getZTop(), brick);
        }
        for (Brick brick : bricks)
        {
            int bottom=brick.getZBottom(); // where does it end at the bottom?
            Collection<Brick> directlyBelow=brickTops.get(bottom-1); // all brick ending directly below
            for (Brick other: directlyBelow)
            {
                if (brick.getArea().overlaps(other.getArea()))
                {
                    supports.put(other, brick); // the other brick supports us
                    supportedBy.put(brick, other); // and we are supported by the other brick
                }
            }
        }
    }

    private static void moveBrick(Brick brick, List<Brick> foundation)
    {
        GridRectangle bottom=brick.getArea();
        // calculate the area the brick covers downwards
        // for each field, cast a ray downwards and see what it hits (we cannot hit anything higher than the current bottom Z)
        int topZ=0;
        // note: coordinates are _inclusive_
        for (int x=bottom.getStartX();x<=bottom.getEndX();x++)
        {
            for (int y=bottom.getStartY(); y<=bottom.getEndY();y++)
            {
                for (Brick other: foundation)
                {
                    // do we hit something?
                    if (other.getArea().contains(x,y) && other.getZTop()<brick.getZBottom())
                    {
                        // if so, check whether this is higher than any previous hit
                        if (other.getZTop()>topZ)
                        {
                            topZ= other.getZTop();
                        }
                        // we can stop here - any other tile we could hit must be lower than the current one
                        break;
                    }
                }
            }
        }
        // the highest Z is how deep we can fall, so move the brick (1 higher than whatever we did hit)
        brick.moveDownTo(topZ+1);
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

        @Override
        public String toString()
        {
            return "Brick{" +
                   "xBottom=" + xBottom +
                   ", yBottom=" + yBottom +
                   ", zBottom=" + zBottom +
                   ", xTop=" + xTop +
                   ", yTop=" + yTop +
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
