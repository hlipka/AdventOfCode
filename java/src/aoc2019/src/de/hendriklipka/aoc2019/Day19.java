package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day19 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day19().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final List<Integer> code = data.getLineAsInteger(",");
        IntCode drone = IntCode.fromIntList(code);

        CharMatrix points=CharMatrix.filledMatrix(50, 50, ' ', ' ');
        for (Position p : points.allPositions())
        {
            final var r = getGravity(p, drone);
            points.set(p, r==0?'.':'#');
        }
        return points.allMatchingPositions('#').size();
    }

    private static int getGravity(final Position p, final IntCode drone)
    {
        IntCode theDrone= drone.createClone();
        theDrone.setDoInput(new IntCode.InputProvider(p.col, p.row));
        final var out = new IntCode.OutputCollector();
        theDrone.setDoOutput(out);
        theDrone.execute();
        return out.getResult().getFirst();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final List<Integer> code = data.getLineAsInteger(",");
        IntCode drone = IntCode.fromIntList(code);

        // strategy: we can implement an edge-follower, one for the left and one for the right edge (just start with the previous line and go right)
        // when we know for each line where the beam starts and ends, we can determine where santas ship fits:
        // starting at line L, the top right corner is at the right end
        // the top left corner is at TR-100 - if now at line L+99 the line start is before-or-at TR-100 the ship fits
        // the check should be from the current line upwards - that way we know when we can stop scanning
        // we can even speed that up: just trace the left edge, and then see whether (-99,-99) of that is in the beam or not
        List<Pair<Integer, Integer>> edges=new ArrayList<>(10000);
        // add 9 empty lines (up to line 8) so the indexes are correct
        edges.add(Pair.of(0,0));
        edges.add(Pair.of(0,0));
        edges.add(Pair.of(0,0));
        edges.add(Pair.of(0,0));
        edges.add(Pair.of(0,0));
        edges.add(Pair.of(0,0));
        edges.add(Pair.of(0,0));
        edges.add(Pair.of(0,0));
        edges.add(Pair.of(0,0));
        // scan line 9 from left to right to get a start for the edge detector
        int currentLine = 9;
        int start=0;
        start = findStart(currentLine, start, drone);
        int end=start;
        end = findEnd(currentLine, end, drone);
        // we store the first and the last position of the beam
        edges.add(Pair.of(start,end));
        while (true)
        {
            currentLine++;
            final Pair<Integer, Integer> lastEdge = edges.getLast();
            start=findStart(currentLine, lastEdge.getLeft(), drone);
            end=findEnd(currentLine, Math.max(start, lastEdge.getRight()), drone);
            edges.add(Pair.of(start,end));
            if (end-start>=99) // technically the bottom edge of the ship now fits
            {
                int topLine=currentLine-99;
                final Pair<Integer, Integer> topEdges = edges.get(topLine);
                if (topEdges.getRight()>=start+99) // does the ship fit?
                {
                    return start* 10000+topLine;
                }
            }
        }
    }

    private static int findEnd(final int currentLine, int end, final IntCode drone)
    {
        while (true)
        {
            final var r = getGravity(new Position(currentLine, end), drone);
            if (r == 0)
                return end-1;
            end++;
        }
    }

    private static int findStart(final int currentLine, int start, final IntCode drone)
    {
        while (true)
        {
            final var r = getGravity(new Position(currentLine, start), drone);
            if (r>0)
                return start;
            start++;
        }
    }
}
