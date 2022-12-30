package de.hendriklipka.aoc2020.day24;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * User: hli
 * Date: 24.12.20
 * Time: 11:56
 */
public class Day242
{
    private static final Set<Pos> blackTiles=new HashSet<>();

    public static void main(String[] args)
    {
        try
        {
            FileUtils.readLines(new File("data/day24.txt"), StandardCharsets.UTF_8).stream().filter(StringUtils::isNotBlank).forEach(Day242::flipTile);
            Set<Pos> currentBlackTiles = blackTiles;
            for (int i=0;i<100;i++)
            {
                currentBlackTiles = flipFloor(currentBlackTiles);
                System.out.println("round "+i+", "+currentBlackTiles.size()+" black tiles");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static Set<Pos> flipFloor(final Set<Pos> tiles)
    {
        final Set<Pos> result = new HashSet<>();
        tiles.forEach(t->
        {
            checkBlackTile(tiles, result, t);
            checkWhiteTile(tiles, result, t.newPos(Direction.E));
            checkWhiteTile(tiles, result, t.newPos(Direction.W));
            checkWhiteTile(tiles, result, t.newPos(Direction.SE));
            checkWhiteTile(tiles, result, t.newPos(Direction.NE));
            checkWhiteTile(tiles, result, t.newPos(Direction.SW));
            checkWhiteTile(tiles, result, t.newPos(Direction.NW));
        }
        );
        return result;
    }

    private static void checkBlackTile(final Set<Pos> tiles, final Set<Pos> result, final Pos t)
    {
        int n = getNeighbours(t, tiles);
        if (n>0 && n<=2)
        {
            result.add(t);
        }
    }

    private static void checkWhiteTile(final Set<Pos> tiles, final Set<Pos> result, final Pos t)
    {
        int n = getNeighbours(t, tiles);
        if (n == 2)
        {
            result.add(t);
        }
    }


    private static int getNeighbours(final Pos t, final Set<Pos> tiles)
    {
        int count=0;
        if (tiles.contains(t.newPos(Direction.E))) count++;
        if (tiles.contains(t.newPos(Direction.W))) count++;
        if (tiles.contains(t.newPos(Direction.SE))) count++;
        if (tiles.contains(t.newPos(Direction.NE))) count++;
        if (tiles.contains(t.newPos(Direction.SW))) count++;
        if (tiles.contains(t.newPos(Direction.NW))) count++;
        return count;
    }

    private static void flipTile(final String line)
    {
        int pos=0;
        Pos current=new Pos(0,0);
        while (pos<line.length())
        {
            switch (line.charAt(pos))
            {
                case 'e':
                    current=current.newPos(Direction.E);
                    break;
                case 'w':
                    current = current.newPos(Direction.W);
                    break;
                case 'n':
                    switch (line.charAt(pos + 1))
                    {
                        case 'w':
                            current = current.newPos(Direction.NW);
                            break;
                        case 'e':
                            current = current.newPos(Direction.NE);
                            break;
                    }
                    pos++;
                    break;
                case 's':
                    switch (line.charAt(pos + 1))
                    {
                        case 'w':
                            current = current.newPos(Direction.SW);
                            break;
                        case 'e':
                            current = current.newPos(Direction.SE);
                            break;
                    }
                    pos++;
                    break;
            }
            pos++;
        }
        if (blackTiles.contains(current))
        {
            System.out.println("Flipping " + current+" to white");
            blackTiles.remove(current);
        }
        else
        {
            System.out.println("Flipping " + current+" to black");
            blackTiles.add(current);
        }
    }

    private static class Pos
    {
        private final int x;
        private final int y;

        public Pos(final int x, final int y)
        {
            this.x = x;
            this.y = y;
        }

        public Pos newPos(Direction dir)
        {
            if (dir.getDy() == 0)
            {
                return new Pos(x + dir.getDx(), y);
            }
            else if (dir.getDy() == -1)
            {
                if (0 == (y % 2))
                {
                    return new Pos(x + dir.getDx() - dir.getDy(), y + dir.getDy());
                }
                else
                {
                    return new Pos(x + dir.getDx(), y + dir.getDy());
                }
            }
            else
            {
                if (0 == (y % 2))
                {
                    return new Pos(x + dir.getDx() + dir.getDy(), y + dir.getDy());
                }
                else
                {
                    return new Pos(x + dir.getDx(), y + dir.getDy());
                }
            }
        }

        @Override
        public boolean equals(final Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Pos pos = (Pos) o;
            return x == pos.x && y == pos.y;
        }

        @Override
        public int hashCode()
        {
            return x*1000+y;
        }

        @Override
        public String toString()
        {
            return "Pos{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    private enum Direction
    {
        W(-1,0), NW(-1,1), NE(0,1), E(1,0), SE(0,-1), SW(-1,-1);

        private final int _dx;
        private final int _dy;

        Direction(final int dx, final int dy)
        {
            _dx = dx;
            _dy = dy;
        }

        public int getDx()
        {
            return _dx;
        }

        public int getDy()
        {
            return _dy;
        }

        public static Direction fromString(String s)
        {
            switch (s)
            {
                case "e": return E;
                case "w": return W;
                case "ne": return NE;
                case "nw": return NW;
                case "se": return SE;
                case "sw": return SW;
            }
            throw new IllegalArgumentException("unknown direction "+s);
        }
    }
}
