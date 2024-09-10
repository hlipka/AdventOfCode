package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocCollectionUtils;
import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: hli
 */
public class Day06a
{
    public static void main(String[] args)
    {
        try
        {
            List<Integer> blocks = AocParseUtils.getLineAsInteger("2017", "day06","\t");
            Set<String> knownBlocks = new HashSet<>();
            knownBlocks.add(blockID(blocks));
            int count=0;
            while (true)
            {
                redistribute(blocks);
                count++;
                String id = blockID(blocks);
                if (knownBlocks.contains(id))
                    break;
                knownBlocks.add(id);
                System.out.println(id);
            }
            System.out.println(count);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void redistribute(final List<Integer> blocks)
    {
        int largest = AocCollectionUtils.findLargestElement(blocks);
        int blockCount=blocks.get(largest);
        int next=(largest+1)%blocks.size();
        blocks.set(largest, 0);
        while (blockCount>0)
        {
            blocks.set(next, blocks.get(next)+1);
            blockCount--;
            next = (next + 1) % blocks.size();

        }
    }

    private static String blockID(final List<Integer> blocks)
    {
        return StringUtils.join(blocks,";");
    }
}
