package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day14 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day14().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        String input=data.getLines().get(0);
        int blocks=0;
        for (int row=0;row<128; row++)
        {
            KnotHash hash = new KnotHash(input+"-"+row, 256);
            hash.hash();
            String rowHash=hash.getBinaryHash();
            blocks+=StringUtils.countMatches(rowHash, '1');

        }
        return blocks;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        String input = data.getLines().get(0);
        List<String> rows=new ArrayList<>();
        for (int row = 0; row < 128; row++)
        {
            KnotHash hash = new KnotHash(input + "-" + row, 256);
            hash.hash();
            rows.add(hash.getBinaryHash());
        }
        CharMatrix disk=CharMatrix.fromStringList(rows, '0');
        Set<Position> visited=new HashSet<>();
        int regionCount=0;
        // we just brute-force all used positions
        for (Position pos: disk.allMatchingPositions('1'))
        {
            // we have not looked at it before
            if (!visited.contains(pos))
            {
                // when we did not look at it before, this is a new region
                regionCount++;
                visited.add(pos);
                // get the flood fill of the region
                final Set<Position> region = disk.floodFill(pos, (from, to) ->
                        disk.at(to) == '1');
                // and mark all the positions as already known
                visited.addAll(region);
            }
        }
        return regionCount;
    }
}
