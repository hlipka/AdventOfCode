package de.hendriklipka.aoc2018;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day10 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day10().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        int lineSize=isExample?7:24;
        List<Star> stars= data.getLines().stream().map(Star::new).toList();
        int time=0;
        while (true)
        {
            time++;
            Map<Integer, Integer> colPos = new HashMap<>();
            int minRow=Integer.MAX_VALUE;
            int maxRow=0;
            int minCol= Integer.MAX_VALUE;
            int maxCol=0;
            // heuristic: we look for states where we have a lot of starts aligned vertically
            for (Star star : stars)
            {
                star.move();
                colPos.put(star.pos.col, colPos.getOrDefault(star.pos.col, 0) + 1);
                minRow=Math.min(minRow, star.pos.row);
                maxRow=Math.max(maxRow, star.pos.row);
                minCol=Math.min(minCol, star.pos.col);
                maxCol=Math.max(maxCol, star.pos.col);
            }
            boolean found=colPos.values().stream().anyMatch(c->c>= lineSize);
            //  if we have something, just dump it
            if (found)
            {
                CharMatrix dump=CharMatrix.filledMatrix(maxRow-minRow+1, maxCol-minCol+1, ' ', ' ');
                for (Star star : stars)
                {
                    dump.set(star.pos.updated(-minRow, -minCol), '#');
                }                    // dump?
                dump.print();
                System.out.println(time);
                break;
            }

        }
        return null;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        // already handled in part A output
        return null;
    }

    private static class Star
    {
        Position pos;
        int vX, vY;

        public Star(final String line)
        {
            List<String> parse= AocParseUtils.getGroupsFromLine(line, "position=<\\s*([\\-0-9]+),\\s*([\\-0-9]+)> velocity=<\\s*([\\-0-9]+),\\s*([\\-0-9]+)>");
            pos= new Position(Integer.parseInt(parse.get(1)), Integer.parseInt(parse.get(0)));
            vX= Integer.parseInt(parse.get(2));
            vY= Integer.parseInt(parse.get(3));
        }

        public void move()
        {
            pos=pos.updated(vY, vX);
        }
    }
}
