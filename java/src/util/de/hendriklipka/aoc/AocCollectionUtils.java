package de.hendriklipka.aoc;

import java.util.List;

/**
 * User: hli
 */
public class AocCollectionUtils
{
    public static int findLargestElement(List<Integer> list)
    {
        if (list.isEmpty())
        {
            return 0;
        }
        int pos=-1;
        int maxValue=-1;
        for (int i=0;i<list.size();i++)
        {
            int value = list.get(i);
            if (value>maxValue)
            {
                maxValue=value;
                pos=i;
            }
        }
        return pos;
    }
}
