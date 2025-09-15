package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position3D;
import de.hendriklipka.aoc.search.BestFirstParallelSearch;
import de.hendriklipka.aoc.search.BestFirstSearch;
import de.hendriklipka.aoc.search.SearchState;
import de.hendriklipka.aoc.search.SearchWorld;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class Day23 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day23().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<Bot> bots = data.getLines().stream().map(this::parseBot).toList();
        Bot largest= bots.stream().max(Comparator.comparingInt(Bot::range)).orElseThrow();

        return botsInrange(largest, bots);
    }

    private int botsInrange(final Bot bot, final List<Bot> bots)
    {
        return (int)bots.stream().filter(b->inRange(bot, b)).count();
    }

    private boolean inRange(final Bot bot, final Bot otherBot)
    {
        return (
                Math.abs(bot.x-otherBot.x) +
                Math.abs(bot.y-otherBot.y) +
                Math.abs(bot.z-otherBot.z))
               <= bot.range();
    }

    private Bot parseBot(final String line)
    {
        final List<String> parts = AocParseUtils.parsePartsFromString(line, "pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(-?\\d+)");
        return new Bot(Integer.parseInt(parts.get(0)), Integer.parseInt(parts.get(1)), Integer.parseInt(parts.get(2)), Integer.parseInt(parts.get(3)));
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // so the strategy is probably a partitioned search:
        // find the bounding box over all bots and ranges, as 2^n value
        // split this in half for each coordinate - this gives 8 cubes
        // for each cube, calculate with how many pots it intersects - this gives an upper bound of the best place that could be in that cube
        // put the cubes into a priority queue: first sort by the upper bound, then the size (larger boxes first), then closest distance to the center
        // take the first cube from the list, do the same again
        // when the cube taken out has a size of 1, we are done
        // a cube intersects with a bot when either:
        // - the center of the bot is in the cube
        // - the distance from an edge of the cube to the center of the bot less than or equal to the range of the bot
        // - for each direction of each coordinate (x, y or z)
        //   - the center of the bot is within the bounds of the other two coordinates
        //   - and on the one coordinate, the center of the bot + its range is within the cube (these are the 'tips' of the octahedron)
        // the priority queue ensures that the final cube is the best - any other cube has a lower upper bound, and the sorting ensures there is none closer
        // to the center

        List<Bot> bots = data.getLines().stream().map(this::parseBot).toList();

        int maxCoord=bots.stream().map(bot->
                List.of(bot.x-bot.range(), bot.y-bot.range(), bot.z-bot.range(), bot.x+bot.range(), bot.y+bot.range(), bot.z+bot.range())).flatMap(List::stream).max(Integer::compareTo).orElseThrow();
        int startSize=1;
        while (startSize<maxCoord)
        {
            startSize*=2;
        }

        final NanoBotWorld world=new NanoBotWorld(startSize, bots);

        // I'm not sure what the parallel implementation is doing differently compared to the normal one, but it finished _way_ faster
        BestFirstParallelSearch<NanoBotWorld, BotRange> bfs = new BestFirstParallelSearch<>(world);
        bfs.search();
        return world.getBestDistance();
    }

    private record Bot(int x, int y, int z, int range)
    {
    }

    private static class NanoBotWorld implements SearchWorld<BotRange>
    {
        private final int _startSize;
        private final List<Bot> _bots;

        int bestBotCount=0;
        private int bestDistance;

        public NanoBotWorld(final int startSize, final List<Bot> bots)
        {
            _startSize = startSize;
            _bots = bots;
        }

        @Override
        public BotRange getFirstState()
        {
            return new BotRange(-_startSize, _startSize-1, -_startSize, _startSize-1, -_startSize, _startSize-1, _bots);
        }

        @Override
        public List<BotRange> calculateNextStates(final BotRange r)
        {
            int newSize=r.getSize()/2;

            // assuming that we have a size of 8, so a range would be -4..3
            // the new ranges are then -4..-1 and 0..3
            // r.?1 is always the smaller value and r.?2 is always the bigger value

            return List.of(
                    new BotRange(r.x1, r.x1+newSize-1, r.y1, r.y1+newSize-1, r.z1, r.z1+newSize-1, _bots),
                    new BotRange(r.x1, r.x1+newSize-1, r.y1, r.y1+newSize-1, r.z1+newSize, r.z2, _bots),
                    new BotRange(r.x1, r.x1+newSize-1, r.y1+newSize, r.y2, r.z1, r.z1 + newSize - 1, _bots),
                    new BotRange(r.x1, r.x1+newSize-1, r.y1+newSize, r.y2, r.z1 + newSize, r.z2, _bots) ,
                    new BotRange(r.x1+newSize, r.x2, r.y1, r.y1 + newSize - 1, r.z1, r.z1 + newSize - 1, _bots),
                    new BotRange(r.x1+newSize, r.x2, r.y1, r.y1 + newSize - 1, r.z1 + newSize, r.z2, _bots) ,
                    new BotRange(r.x1+newSize, r.x2, r.y1+newSize, r.y2, r.z1, r.z1 + newSize - 1, _bots) ,
                    new BotRange(r.x1+newSize, r.x2, r.y1+newSize, r.y2, r.z1 + newSize, r.z2, _bots)
            );
        }

        @Override
        public boolean reachedTarget(final BotRange currentState)
        {
            // we have a range with a size of 1
            final var b = currentState.x1 == currentState.x2;
            if (b && currentState.intersects > bestBotCount)
            {
                bestBotCount = currentState.intersects;
                System.out.println("new best state: " + bestBotCount);
                bestDistance = currentState.getBestDistance();
            }
            return b;
        }

        @Override
        public boolean canPruneBranch(final BotRange currentState)
        {
            return currentState.intersects<bestBotCount;
        }

        @Override
        public Comparator<BotRange> getComparator()
        {
            // first, we check for more intersects since this mean in the range of more bots
            // when two ranges are the same, prefer the larger one since it might split further into a smaller one with the same intersect count but be closer
            // last, prefer the closer one
            // so finally we end up with a range of size 1
            return Comparator.comparing(BotRange::getIntersects, Comparator.reverseOrder()).thenComparing(BotRange::getSize, Comparator.reverseOrder()).thenComparingInt(BotRange::getBestDistance);
        }

        public int getBestDistance()
        {
            // not used for BFS
            return bestDistance;
        }
    }

    private static class BotRange implements SearchState
    {
        int x1,x2,y1,y2,z1,z2;
        int intersects;
        int bestDistance;

        public BotRange(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2, List<Bot> bots)
        {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.z1 = z1;
            this.z2 = z2;

            intersects = (int)(bots.stream().filter(this::intersects).count());
//            System.out.println("intersects=" + intersects);

            bestDistance = Math.min(Math.abs(x1), Math.abs(x2))+Math.min(Math.abs(y1), Math.abs(y2))+Math.min(Math.abs(z1), Math.abs(z2));
        }

        private boolean intersects(Bot bot)
        {
            // bot center is inside the range
            if (bot.x>x1 && bot.x<x2 && bot.y>y1 && bot.y<y2 && bot.z>z1 && bot.z<z2)
            {
                return true;
            }
            // bot is in range of one of the corners (when its in the corner quadrants)
            if (inRange(bot, x1,y1, z1)) return true;
            if (inRange(bot, x2,y1, z1)) return true;
            if (inRange(bot, x1,y2, z1)) return true;
            if (inRange(bot, x2,y2, z1)) return true;
            if (inRange(bot, x1,y1, z2)) return true;
            if (inRange(bot, x2,y1, z2)) return true;
            if (inRange(bot, x1,y2, z2)) return true;
            if (inRange(bot, x2,y2, z2)) return true;

            // bot touches the range via one of the sides
            if (bot.x>=x1&&bot.x<=x2&&bot.y>=y1&&bot.y<=y2 && ( (bot.z-bot.range>=z1 && bot.z-bot.range<=z1) | (bot.z + bot.range >= z1 && bot.z + bot.range <= z1))) return true;
            if (bot.x>=x1&&bot.x<=x2&&bot.z>=z1&&bot.z<=z2 && ( (bot.y-bot.range>=y1 && bot.y-bot.range<=y1) | (bot.y + bot.range >= y1 && bot.y + bot.range <= y1))) return true;
            if (bot.y>=y1&&bot.y<=y2&&bot.z>=z1&&bot.z<=z2 && ( (bot.x-bot.range>=x1 && bot.x-bot.range<=x1) | (bot.x + bot.range >= x1 && bot.x + bot.range <= x1))) return true;

            return false;
        }

        private boolean inRange(final Bot bot, final int x1, final int y1, final int z1)
        {
            Position3D center = new Position3D(bot.x, bot.y, bot.z);
            return center.distance(new Position3D(x1, y1, z1)) <= bot.range;
        }

        @Override
        public String calculateStateKey()
        {
            return x1+"-"+x2+"-"+y1+"-"+y2+"-"+z1+"-"+z2;
        }

        @Override
        public boolean betterThan(final Object otherCost)
        {
            BotRange other=(BotRange)otherCost;
            if (intersects>other.intersects)
            {
                return true;
            }
            if (getSize()>other.getSize())
            {
                return true;
            }
            return bestDistance<other.bestDistance;
        }

        @Override
        public Object getCurrentCost()
        {
            return this;
        }

        public int getIntersects()
        {
            return  intersects;
        }

        public int getBestDistance()
        {
            return  bestDistance;
        }

        public int getSize()
        {
            return Math.abs(x1-x2)+1;
        }
    }
}
