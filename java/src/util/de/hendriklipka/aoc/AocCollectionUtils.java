package de.hendriklipka.aoc;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
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

    /**
     * Get a list of all pairs of elements in the list. Pairs will be unique (so when [A,B] is in the list, [B, A] will not be in the list)
     * Pairs [A, A] will not be in the list
     *
     * @param list the list
     * @return the list of all pairs
     * @param <T> the type of the elements
     */
    public static <T> List<Pair<T, T>> getPairs(List<T> list)
    {
        final List<Pair<T, T>> result = new ArrayList<>();
        for (int i=0;i<list.size();i++)
            for (int j=i+1;j<list.size();j++)
                result.add(new ImmutablePair<>(list.get(i), list.get(j)));
        return result;
    }

    /**
     * Get a list of all pairs of elements in the list. Pairs will be ordered (so both [A,B] and [B, A] will be in the list)
     * Pairs [A, A] will not be in the list
     *
     * @param list the list
     * @return the list of all pairs
     * @param <T> the type of the elements
     */
    public static <T> List<Pair<T, T>> getOrderedPairs(List<T> list)
    {
        final List<Pair<T, T>> result = new ArrayList<>();
        for (int i=0;i<list.size();i++)
            for (int j=0;j<list.size();j++)
                if (i!=j)
                    result.add(new ImmutablePair<>(list.get(i), list.get(j)));
        return result;
    }
}
