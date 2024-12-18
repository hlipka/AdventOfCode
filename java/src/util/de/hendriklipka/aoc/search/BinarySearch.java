package de.hendriklipka.aoc.search;

import java.util.function.BiFunction;

/**
 * Implements a generic binary search over arbitrary conditions and ranges
 *
 * @param <T> the type of the context object to use for the search
 */
public class BinarySearch<T>
{
    private final int _start;
    private final int _end;
    private final T _context;
    private final BiFunction<T, Integer, Integer> _verifyFunction;

    /**
     * the verification function must return a value < 0 when the index is too large (search nearer to the start),
     * a value > 0 when the index is too small (search closer to the end) and 0 when the index is the correct one.
     *
     * @param start start value of whatever the search range is
     * @param end end value of whatever the search range is
     * @param context the context object used for the search
     * @param verifyFunction the function used for verification of the current index
     */
    public BinarySearch(int start, int end, T context, BiFunction<T, Integer, Integer> verifyFunction)
    {

        _start = start;
        _end = end;
        _context = context;
        _verifyFunction = verifyFunction;
    }

    public int search()
    {
        int index = Integer.MAX_VALUE;
        int low=_start;
        int high=_end;

        while (low <= high) {
            int mid = low  + ((high - low) / 2);
            int key = _verifyFunction.apply(_context, mid);
            if (key>0)
            {
                low = mid + 1;
            }
            else if (key<0)
            {
                high = mid - 1;
            }
            else if (key==0)
            {
                index = mid;
                break;
            }
        }
        return index;
    }
}
