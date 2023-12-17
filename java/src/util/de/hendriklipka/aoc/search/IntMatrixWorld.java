package de.hendriklipka.aoc.search;

import de.hendriklipka.aoc.matrix.IntMatrix;

/**
 * User: hli
 * Date: 17.12.23
 * Time: 15:17
 */
public abstract class IntMatrixWorld implements ArrayWorld
{
    IntMatrix map;
    @Override
    public int getWidth()
    {
        return map.cols();
    }

    @Override
    public int getHeight()
    {
        return map.rows();
    }
}
