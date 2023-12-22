package de.hendriklipka.aoc;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * User: hli
 * Date: 22.12.23
 * Time: 22:00
 */
public class _MathRectangle
{
    @Test
    public void testCoordinates1()
    {
        MathRectangle r = new MathRectangle(2, 2, 1, 1);
        assertThat(r.getStartX(), is(1));
        assertThat(r.getStartY(), is(1));
        assertThat(r.getEndX(), is(2));
        assertThat(r.getEndY(), is(2));
    }

    @Test
    public void testCoordinates2()
    {
        MathRectangle r = new MathRectangle(3, 2, 5, 1);
        assertThat(r.getStartX(), is(3));
        assertThat(r.getStartY(), is(1));
        assertThat(r.getEndX(), is(5));
        assertThat(r.getEndY(), is(2));
    }

    @Test
    public void testOverlap()
    {
        MathRectangle r = new MathRectangle(1, 2, 8, 9);

        assertTrue(r.overlaps(new MathRectangle(1, 2, 8, 9)));
        assertTrue(r.overlaps(new MathRectangle(9, 8, 1, 2)));

        assertTrue(r.overlaps(new MathRectangle(2, 3, 7, 8)));

        assertTrue(r.overlaps(new MathRectangle(2, 3, 8, 9)));

        assertTrue(r.overlaps(new MathRectangle(1, 3, 7, 8)));

        assertTrue(r.overlaps(new MathRectangle(0, 3, 9, 4)));
        assertTrue(r.overlaps(new MathRectangle(5, 5, 10, 10)));

        assertTrue(r.overlaps(new MathRectangle(2, 3, 8, 9)));
        assertTrue(r.overlaps(new MathRectangle(2, 3, 8, 9)));
        assertTrue(r.overlaps(new MathRectangle(2, 3, 8, 9)));

        assertFalse(r.overlaps(new MathRectangle(1, 2, 8, 0)));
        assertFalse(r.overlaps(new MathRectangle(0, 2, 9, 0)));
        assertFalse(r.overlaps(new MathRectangle(1, 0, 0, 10)));

        assertFalse(r.overlaps(new MathRectangle(0, 0, 1, 2)));
        assertFalse(r.overlaps(new MathRectangle(0, 9, 1, 10)));
        assertFalse(r.overlaps(new MathRectangle(0, 8, 1, 10)));

    }
}
