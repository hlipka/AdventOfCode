package de.hendriklipka.aoc2025;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.GridRectangle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.vizualization.SwingViewer;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day09 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day09().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        List<Position> tiles=data.getLineLongs(",").stream().map(l->new Position(l.getFirst().intValue(), l.get(1).intValue())).toList();
        List<GridRectangle> rects=new ArrayList<>();
        for (int i=0;i<tiles.size();i++)
        {
            Position start=tiles.get(i);
            for (int j=i+1;j<tiles.size();j++)
            {
                Position end=tiles.get(j);
                rects.add(new GridRectangle(start.col, start.row, end.col, end.row));
            }
        }
        rects.sort(Comparator.comparingLong(r->-r.getArea()));
        return rects.getFirst().getArea();
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        List<Position> tiles = data.getLineLongs(",").stream().map(l -> new Position(l.get(1).intValue(), l.getFirst().intValue())).toList();
        // create a list of lines - the start point will be the left-most column, and they also point upwards
        List<Pair<Position, Position>> lines=new ArrayList<>();
        for (int i=0;i<tiles.size()-1;i++)
        {
            final Position start = tiles.get(i);
            final Position end = tiles.get(i + 1);
            createLine(start, end, lines);
        }
        createLine(tiles.getFirst(), tiles.getLast(), lines);

        new SwingViewer(g ->
        {
            for (Pair<Position, Position> line: lines)
            {
                g.drawLine(line.getLeft().col/100,  line.getLeft().row / 100, line.getRight().col / 100, line.getRight().row / 100);
            }
        });

        List<GridRectangle> rects = new ArrayList<>();
        for (int i = 0; i < tiles.size(); i++)
        {
            Position start = tiles.get(i);
            for (int j = i + 1; j < tiles.size(); j++)
            {
                Position end = tiles.get(j);
                // we know that start and end are part of the polygon already, so we need to check only the other two corners of the rectangle
                Position other1=new Position(start.row, end.col);
                Position other2=new Position(end.row, start.col);
                // need to check whether both edges are enclosed within the polygon
                final boolean in1 = isInPolygon(other1, lines);
                final boolean in2 = isInPolygon(other2, lines);
                if (in1 && in2)
                {
                    final GridRectangle rect = new GridRectangle(start.col, start.row, end.col, end.row);
                    // the rectangle must not cross any line of the polygon (crossing means anything apart from the edges)
                    if (isCrossing(start, other1, lines )
                        || isCrossing(other1, end, lines)
                        || isCrossing(end, other2, lines)
                        || isCrossing(other2, start, lines))
                    {
                        continue;
                    }

                    // the center of the rectangle also must be part of the polygon - otherwise the rectangle might be in an outer pocket of the polygon
                    // the case of width/height==1 will be handled automatically
                    Position middle=new Position((start.row+end.row)/2, (start.col+end.col)/2);
                    if (!isInPolygon(middle, lines))
                    {
                        continue;
                    }
                    rects.add(rect);
                }
            }
        }

        rects.sort(Comparator.comparingLong(r -> -r.getArea()));
        return rects.getFirst().getArea();
    }

    // check for real crossings - end points are not considered
    private boolean isCrossing(final Position edgeStart, final Position egdeEnd, final List<Pair<Position, Position>> lines)
    {
        if (edgeStart.col==egdeEnd.col) // vertical edge
        {
            int edgeCol=edgeStart.col;
            for (Pair<Position, Position> line: lines)
            {
                // we can skip vertical lines here, we care  only about horizontal ones
                if (line.getLeft().col==line.getRight().col)
                    continue;
                int startCol=Math.min(line.getLeft().col, line.getRight().col);
                int endCol=Math.max(line.getLeft().col, line.getRight().col);
                int startRow=Math.min(edgeStart.row, egdeEnd.row);
                int endRow=Math.max(edgeStart.row, egdeEnd.row);
                int lineRow=line.getLeft().row;
                if (edgeCol>startCol && edgeCol<endCol && lineRow>startRow && lineRow<endRow)
                {
                    return true;
                }
            }
        }
        else // horizontal edge
        {
            int edgeRow=edgeStart.row;
            for (Pair<Position, Position> line: lines)
            {
                if (line.getLeft().row==line.getRight().row)
                    continue;
                int startRow=Math.min(line.getLeft().row, line.getRight().row);
                int endRow=Math.max(line.getLeft().row, line.getRight().row);
                int startCol=Math.min(edgeStart.col, egdeEnd.col);
                int endCol=Math.max(edgeStart.col, egdeEnd.col);
                int lineCol=line.getLeft().col;
                if (edgeRow>startRow&& edgeRow<endRow && lineCol>startCol && lineCol<endCol)
                {
                    return true;
                }
            }
        }
        return false;
    }

    private static void createLine(final Position start, final Position end, final List<Pair<Position, Position>> lines)
    {
        if (start.col < end.col)
            lines.add(Pair.of(start, end));
        else if (start.col > end.col)
            lines.add(Pair.of(end, start));
        else if (start.row<end.row)
            lines.add(Pair.of(end, start));
        else
            lines.add(Pair.of(end, start));
    }

    private boolean isInPolygon(final Position position, final List<Pair<Position, Position>> lines)
    {
        // first check: when the position is directly on a line, it is inside the polygon
        if (isOnALine(position, lines))
        {
            return true;
        }
        // cast a horizontal ray from the left side until we reach our point
        // count how often we cross a line
        // when it is an odd number, we are inside the polygon
        // we know that we are not directly on a line (checked this above)
        int crossings=0;
        for (int i = 0; i < lines.size(); i++)
        {
            final Pair<Position, Position> line = lines.get(i);
            Position start = line.getLeft();
            Position end = line.getRight();
            // the line is fully to the right, so will never cross it
            if (start.col > position.col && end.col > position.col)
                continue;
            if (start.row == end.row) // horizontal line
            {
                if (start.row == position.row)
                {
                    // check whether the next and previous lines go into the same direction (up or down)
                    // when to both point into the same direction (seen from the current row) we don't change our state
                    Pair<Position, Position> prevLine = 0 == i ? lines.getLast() : lines.get(i - 1);
                    int prevRow=prevLine.getLeft().row==position.row?prevLine.getRight().row:prevLine.getLeft().row;
                    final boolean prevUpwards = prevRow < start.row;

                    Pair<Position, Position> nextLine = lines.size() - 1 == i ? lines.getFirst() : lines.get(i + 1);
                    int nextRow=nextLine.getLeft().row==position.row?nextLine.getRight().row:nextLine.getLeft().row;
                    final boolean nextUpwards = nextRow < start.row;

                    if (prevUpwards != nextUpwards)
                        crossings++;
                }
            }
            else // vertical
            {
                int min = Math.min(start.row, end.row);
                int max = Math.max(start.row, end.row);
                // does the ray cross the line?
                // we can skip situations where we would hit the start or end of the line - these will be handled by the horizontal check above
                if (min < position.row && position.row < max)
                    crossings++;
            }
        }
        return 1 == (crossings % 2);
    }

    private boolean isOnALine(final Position pos, final List<Pair<Position, Position>> lines)
    {
        for (Pair<Position, Position> line:lines)
        {
            Position start=line.getLeft();
            Position end=line.getRight();
            if (start.row==end.row) // horizontal
            {
                if (pos.row!=start.row)
                    continue;
                int min=Math.min(start.col, end.col);
                int max=Math.max(start.col, end.col);
                if (min <= pos.col && pos.col <= max)
                    return true;
            }
            else // vertical
            {
                if (pos.col != start.col)
                    continue;
                int min = Math.min(start.row, end.row);
                int max = Math.max(start.row, end.row);
                if (min <= pos.row && pos.row <= max)
                    return true;
            }
        }
        return false;
    }
}
