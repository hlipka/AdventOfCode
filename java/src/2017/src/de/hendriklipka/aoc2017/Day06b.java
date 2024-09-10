package de.hendriklipka.aoc2017;

import de.hendriklipka.aoc.AocCollectionUtils;
import de.hendriklipka.aoc.AocParseUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * User: hli
 */
public class Day06b
{
    public static void main(String[] args)
    {
        try
        {
            List<Integer> blocks = AocParseUtils.getLineAsInteger("2017", "day06","\t ");
            Map<String, Integer> knownBlocks = new HashMap<>();
            knownBlocks.put(blockID(blocks), 0);
            int count=0;
            while (true)
            {
                redistribute(blocks);
                count++;
                String id = blockID(blocks);
                if (knownBlocks.containsKey(id))
                {
                    System.out.println(count - knownBlocks.get(id));
                    break;
                }
                knownBlocks.put(id, count);
            }
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
