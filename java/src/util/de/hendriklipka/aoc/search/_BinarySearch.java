package de.hendriklipka.aoc.search;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class _BinarySearch
{
    int[] values = new int[]{1,3,4,5,6,8,9,11,12,13,14,16,17,18,19,20,21};

    @Test
    public void testSimpleArraySearch1()
    {
        final int value=5;
        BinarySearch<int[]> search = new BinarySearch<>(0, values.length - 1, values, (ints, index) -> Integer.compare(value, ints[index]));
        int idx=search.search();
        assertThat(idx, is(3));
    }

    @Test
    public void testSimpleArraySearch2()
    {
        final int value=17;
        BinarySearch<int[]> search = new BinarySearch<>(0, values.length - 1, values, (ints, index) -> Integer.compare(value, ints[index]));
        int idx=search.search();
        assertThat(idx, is(12));
    }

    @Test
    public void testSimpleArraySearch3()
    {
        final int value=12;
        BinarySearch<int[]> search = new BinarySearch<>(0, values.length - 1, values, (ints, index) -> Integer.compare(value, ints[index]));
        int idx=search.search();
        assertThat(idx, is(8));
    }

    @Test
    public void testSimpleArraySearchFirst()
    {
        final int value=1;
        BinarySearch<int[]> search = new BinarySearch<>(0, values.length - 1, values, (ints, index) -> Integer.compare(value, ints[index]));
        int idx=search.search();
        assertThat(idx, is(0));
    }

    @Test
    public void testSimpleArraySearchLast()
    {
        final int value=20;
        BinarySearch<int[]> search = new BinarySearch<>(0, values.length - 1, values, (ints, index) -> Integer.compare(value, ints[index]));
        int idx=search.search();
        assertThat(idx, is(15));
    }

    @Test
    public void testSimpleArraySearchNotFound()
    {
        final int value=22;
        BinarySearch<int[]> search = new BinarySearch<>(0, values.length - 1, values, (ints, index) -> Integer.compare(value, ints[index]));
        int idx=search.search();
        assertThat(idx, is(Integer.MAX_VALUE));
    }

    @Test
    public void testSimpleArraySearchRange()
    {
        final int value=12;
        BinarySearch<int[]> search = new BinarySearch<>(2, values.length - 2, values, (ints, index) -> Integer.compare(value, ints[index]));
        int idx=search.search();
        assertThat(idx, is(8)); // the index is still the same as when we search in the full range
    }

    @Test
    public void testSimpleArraySearchRangeNotFound()
    {
        final int value=1;
        BinarySearch<int[]> search = new BinarySearch<>(2, values.length - 1, values, (ints, index) -> Integer.compare(value, ints[index]));
        int idx=search.search();
        assertThat(idx, is(Integer.MAX_VALUE));
    }
}
