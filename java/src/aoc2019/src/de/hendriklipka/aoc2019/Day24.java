package de.hendriklipka.aoc2019;

import de.hendriklipka.aoc.AocPuzzle;
import de.hendriklipka.aoc.Direction;
import de.hendriklipka.aoc.Position;
import de.hendriklipka.aoc.matrix.CharMatrix;

import java.io.IOException;
import java.util.*;

public class Day24 extends AocPuzzle
{
    public static void main(String[] args)
    {
        new Day24().doPuzzle(args);
    }

    @Override
    protected Object solvePartA() throws IOException
    {
        CharMatrix area=data.getLinesAsCharMatrix('.');
        Set<CharMatrix> seen=new HashSet<>();
        while (!seen.contains(area))
        {
            seen.add(area);
            area=doStep(area);
        }
        return getDiversity(area);
    }

    private CharMatrix doStep(final CharMatrix area)
    {
        final CharMatrix result=CharMatrix.filledMatrix(area.rows(), area.cols(), '.', '.');
        for (Position p: area.allPositions())
        {
            int nb= (int)(Arrays.stream(Direction.values()).map(d->p.updated(d)).map(p1->area.at(p1)).filter(c->c=='#').count());
            if (area.at(p)=='#')
            {
                result.set(p, nb==1?'#':'.');
            }
            else
            {
                result.set(p, (nb == 1||nb==2) ? '#' : '.');
            }
        }
        return result;
    }

    private int getDiversity(final CharMatrix area)
    {
        int sum=0;
        int factor=1;
        for (Position p: area.allPositions())
        {
            if (area.at(p)=='#')
            {
                sum+=factor;
            }
            factor*=2;
        }
        return sum;
    }

    @Override
    protected Object solvePartB() throws IOException
    {
        CharMatrix area = data.getLinesAsCharMatrix('.');

        // we start with bugs only in layer 0, and in each iteration we can infect at most one additional layer
        // so in 200 iterations we can get only up to 200 additional layers per direction
        // we also know the size - each layer is 25 fields (we could omit the middle one, but keeping it makes coordinate handling easier)
        // is useful to add a dummy layer at the start and the end - that way we can treat all layers the same with regard to their neighbours
        char[] allLayers=new char [403*25];
        Arrays.fill(allLayers, '.');

        for (Position p : area.allPositions())
        {
            allLayers[201*25+p.row*5+p.col]=area.at(p);
        }

            // store the offsets to the neighbours for each field
        @SuppressWarnings("unchecked")
        List<Integer>[] neighbours=new List[403 * 25];
        calculateNeighbours(neighbours);
        for (int round=0;round<200;round++)
        {
            if (round==10)
            {
                System.out.println("at round 10: "+getBugs(allLayers));
            }
            allLayers=doRound(allLayers, neighbours);
        }
        return getBugs(allLayers);
    }

    private char[] doRound(final char[] allLayers, final List<Integer>[] neighbourPos)
    {
        final char[] result=new char[allLayers.length];
        for (int p=0;p<allLayers.length;p++)
        {
            final var neighbours = neighbourPos[p];
            if (null==neighbours) // this excludes the dummy layers
                continue;
            int nb=(int)(neighbours.stream().map(i->allLayers[i]).filter(c-> c == '#').count());
            if (allLayers[p]=='#')
            {
                result[p]= (nb == 1 ? '#' : '.');
            }
            else
            {
                result[p] = ((nb == 1 || nb == 2) ? '#' : '.');
            }
        }
        return result;
    }

    private int getBugs(final char[] layers)
    {
        int count=0;
        for (final char layer : layers)
        {
            if (layer == '#')
            {
                count++;
            }
        }
        return count;
    }

    // 0: dummy
    // 1..200 - upwards (previous)
    // 201: start layer
    // 202..401: downwards (next)
    private void calculateNeighbours(final List<Integer>[] neighbours)
    {
        for (int layer=1;layer<402;layer++)
        {
            int currentField = layer * 25;
            int nextCenter = currentField + 25 + 12; // the layer downwards (into the center field)
            int previousCenter = currentField -13; // the layer upwards (to the outside)

            // first row
            neighbours[currentField]=List.of(currentField + 1, currentField + 5, previousCenter-5, previousCenter - 1);
            currentField++;
            neighbours[currentField]=List.of(currentField - 1, currentField + 1, currentField + 5, previousCenter - 5);
            currentField++;
            neighbours[currentField]=List.of(currentField - 1, currentField + 1, currentField + 5, previousCenter - 5);
            currentField++;
            neighbours[currentField]=List.of(currentField - 1, currentField + 1, currentField + 5, previousCenter - 5);
            currentField++;
            neighbours[currentField]=List.of(currentField - 1, currentField + 5, previousCenter - 5, previousCenter + 1);
            currentField++;

            // second row
            neighbours[currentField]=List.of(currentField-5, currentField + 5, currentField + 1, previousCenter - 1);
            currentField++;
            neighbours[currentField]=List.of(currentField - 5, currentField + 5, currentField + 1, currentField-1);
            currentField++;

            // right above the middle
            neighbours[currentField]=List.of(currentField - 5, currentField + 1, currentField - 1,
                    nextCenter-12, nextCenter - 11, nextCenter - 10, nextCenter - 9, nextCenter - 8);
            currentField++;

            neighbours[currentField]=List.of(currentField - 5, currentField + 5, currentField + 1, currentField - 1);
            currentField++;
            neighbours[currentField]=List.of(currentField - 5, currentField + 5, currentField - 1, previousCenter + 1);
            currentField++;

            // middle row
            neighbours[currentField]=List.of(currentField - 5, currentField + 5, currentField+1, previousCenter - 1);
            currentField++;
            neighbours[currentField]=List.of(currentField - 5, currentField + 5, currentField - 1, nextCenter - 12
                    , nextCenter - 7, nextCenter - 2, nextCenter + 3, nextCenter +8);
            currentField++;
            neighbours[currentField]=List.of(); // the middle field is treated has having no neighbours, so it always ends up empty
            currentField++;
            neighbours[currentField]=List.of(currentField - 5, currentField + 5, currentField + 1, nextCenter -8, nextCenter -3, nextCenter +2,
                    nextCenter + 7, nextCenter + 12);
            currentField++;
            neighbours[currentField]=List.of(currentField - 5, currentField + 5, currentField - 1, previousCenter + 1);
            currentField++;

            // fourth row
            neighbours[currentField]=List.of(currentField - 5, currentField + 5, currentField + 1, previousCenter - 1);
            currentField++;
            neighbours[currentField]=List.of(currentField - 5, currentField + 5, currentField - 1, currentField + 1);
            currentField++;
            // right below the middle
            neighbours[currentField]=List.of(currentField - 1, currentField + 1, currentField + 5, nextCenter + 8, nextCenter + 9, nextCenter + 10,
                    nextCenter + 11, nextCenter + 12);
            currentField++;
            neighbours[currentField]=List.of(currentField - 5, currentField + 5, currentField + 1, currentField - 1);
            currentField++;
            neighbours[currentField]=List.of(currentField - 5, currentField + 5, currentField - 1, previousCenter + 1);
            currentField++;

            // fifth row
            neighbours[currentField]=List.of(currentField -5, currentField + 1, previousCenter + 5, previousCenter - 1);
            currentField++;
            neighbours[currentField]=List.of(currentField - 1, currentField + 1, currentField - 5, previousCenter + 5);
            currentField++;
            neighbours[currentField]=List.of(currentField - 1, currentField + 1, currentField - 5, previousCenter + 5);
            currentField++;
            neighbours[currentField]=List.of(currentField - 1, currentField + 1, currentField - 5, previousCenter + 5);
            currentField++;
            neighbours[currentField]=List.of(currentField - 1, currentField - 5, previousCenter + 5, previousCenter + 1);

        }
    }
}
