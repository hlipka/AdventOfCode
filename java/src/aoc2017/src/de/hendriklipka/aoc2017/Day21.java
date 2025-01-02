package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocParseUtils;
import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;
import de.hendriklipka.aoc.matrix.InfiniteCharMatrix;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day21 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day21().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        final var rounds = isExample ? 2 : 5;
        return simulateRounds(rounds);
    }

    private int simulateRounds(final int rounds) throws IOException
    {
        Map<CharMatrix, CharMatrix> expand2=new HashMap<>();
        Map<CharMatrix, CharMatrix> expand3=new HashMap<>();
        data.getLines().stream().map(this::parseLine).forEach(p->
        {
            if (p.getLeft().rows()==2)
            {
                for (CharMatrix cm: p.getLeft().getTransformations())
                    expand2.put(cm, p.getRight());
            }
            else
            {
                for (CharMatrix cm : p.getLeft().getTransformations())
                    expand3.put(cm, p.getRight());
            }
        });

        InfiniteCharMatrix pixels = new InfiniteCharMatrix('.');
        pixels.set(new Position(0,1), '#');
        pixels.set(new Position(1,2), '#');
        pixels.set(new Position(2,0), '#');
        pixels.set(new Position(2,1), '#');
        pixels.set(new Position(2,2), '#');

        for (int round = 0; round < rounds; round ++)
        {
            int factor = (0 == pixels.rows() % 2) ? 2 : 3;
            pixels = expand(pixels, factor, factor==2?expand2:expand3);
        }
        int count=0;
        for (int row=pixels.lowestRow(); row<=pixels.highestRow(); row++)
        {
            for (int col=pixels.lowestColumn(); col<=pixels.highestColumn(); col++)
            {
                if (pixels.at(new Position(row, col))=='#')
                {
                    count++;
                }
            }
        }
        return count;
    }

    private InfiniteCharMatrix expand(final InfiniteCharMatrix pixels, final int factor, final Map<CharMatrix, CharMatrix> expand)
    {
        // Note: we make sure the lowest row / col count is always 0
        final InfiniteCharMatrix result= new InfiniteCharMatrix('.');
        int count=pixels.rows()/factor;
        for (int row=0; row<count;row++)
        {
            for (int col=0; col<count; col++)
            {
                Position pos = new Position(row*factor, col*factor);
                CharMatrix cm=pixels.getSubMatrix(pos, factor, factor);
                CharMatrix expanded=expand.get(cm);
                result.set(new Position(row*(factor+1), col*(factor+1)), expanded);
            }
        }

        return result;
    }

    private Pair<CharMatrix, CharMatrix> parseLine(final String line)
    {
        String from= AocParseUtils.parseStringFromString(line, "([.#/]+)\\s+=>.*");
        String to= AocParseUtils.parseStringFromString(line, ".*=>\\s+([.#/]+)");
        List<String> fromP= Arrays.asList(StringUtils.split(from,"/"));
        List<String> toP= Arrays.asList(StringUtils.split(to,"/"));
        return Pair.of(CharMatrix.fromStringList(fromP,'.'), CharMatrix.fromStringList(toP,'.'));
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        final var rounds = isExample ? 2 : 18;
        return simulateRounds(rounds);
    }
}
