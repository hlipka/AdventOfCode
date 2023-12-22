package de.hendriklipka.aoc;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * User: hli
 * Date: 22.12.23
 * Time: 22:00
 */
public class _GridRectangle
{
    @Test
    public void testCoordinates1()
    {
        GridRectangle r = new GridRectangle(2, 2, 1, 1);
        assertThat(r.getStartX(), is(1));
        assertThat(r.getStartY(), is(1));
        assertThat(r.getEndX(), is(2));
        assertThat(r.getEndY(), is(2));
    }

    @Test
    public void testCoordinates2()
    {
        GridRectangle r = new GridRectangle(3, 2, 5, 1);
        assertThat(r.getStartX(), is(3));
        assertThat(r.getStartY(), is(1));
        assertThat(r.getEndX(), is(5));
        assertThat(r.getEndY(), is(2));
    }

    @Test
    public void testOverlaps()
    {
        GridRectangle r = new GridRectangle(1, 2, 8, 9);
        assertTrue(r.overlaps(new GridRectangle(1, 2, 8, 9)));
        assertTrue(r.overlaps(new GridRectangle(8, 9, 1, 2)));
        assertTrue(r.overlaps(new GridRectangle(1, 9, 8, 2)));

        assertTrue(r.overlaps(new GridRectangle(2, 3, 7, 8)));

        assertTrue(r.overlaps(new GridRectangle(0, 0, 10, 10)));

        assertTrue(r.overlaps(new GridRectangle(0, 0, 1, 2)));
        assertTrue(r.overlaps(new GridRectangle(1, 2, 0, 0)));

        assertTrue(r.overlaps(new GridRectangle(1, 3, 8, 7)));

        assertTrue(r.overlaps(new GridRectangle(0, 2, 10, 2)));

        assertTrue(r.overlaps(new GridRectangle(0, 4, 10, 4)));
        assertTrue(r.overlaps(new GridRectangle(5, 5, 10, 10)));
        assertTrue(r.overlaps(new GridRectangle(5, 5, 6, 10)));

        assertFalse(r.overlaps(new GridRectangle(10, 10, 10, 10)));
        assertFalse(r.overlaps(new GridRectangle(-1, -2, -8, -9)));
        assertFalse(r.overlaps(new GridRectangle(0, 0, 0, 0)));
        assertFalse(r.overlaps(new GridRectangle(0, 0, 0, 1)));
        assertFalse(r.overlaps(new GridRectangle(0, 2, 0, 4)));
        assertFalse(r.overlaps(new GridRectangle(2, 0, 5, 0)));
        assertFalse(r.overlaps(new GridRectangle(0, 10, 1, 11)));
    }
}
