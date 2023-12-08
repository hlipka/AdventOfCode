package de.hendriklipka.aoc;

import org.apache.commons.math3.util.ArithmeticUtils;

/**
 * User: hli
 * Date: 08.12.23
 * Time: 07:55
 */
public class MathUtils
{
    public static long lcm(long... numbers)
    {
        int pos=2;
        long lcm= ArithmeticUtils.lcm(numbers[0], numbers[1]);
        while(pos<numbers.length)
        {
            long next=numbers[pos];
            lcm = ArithmeticUtils.lcm(lcm, next);
            pos++;
        }
        return lcm;
    }
}
