package de.hendriklipka.aoc;

import org.apache.commons.math3.util.ArithmeticUtils;

import java.math.BigInteger;

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

    /**
     * Solve a system of linear equations, where everything must be integer values
     * The equations are xa*a+xb*b=x ; ya*a+yb*b=y
     * The result is an array [a,b], or null when no integer solution can be found
     */
    public static long[] solveLongDualEquation(long xa, long xb, long x, long ya, long yb, long y)
    {
        BigInteger a1=BigInteger.valueOf(x).multiply(BigInteger.valueOf(yb)).subtract(BigInteger.valueOf(xb).multiply(BigInteger.valueOf(y)));
        BigInteger b1=BigInteger.valueOf(y).multiply(BigInteger.valueOf(xa)).subtract(BigInteger.valueOf(ya).multiply(BigInteger.valueOf(x)));
        BigInteger div=BigInteger.valueOf(xa).multiply(BigInteger.valueOf(yb)).subtract(BigInteger.valueOf(xb).multiply(BigInteger.valueOf(ya)));
        if (div.signum()==0)
            return null;
        if (div.signum()==-1)
        {
            a1=a1.negate();
            b1=b1.negate();
            div=div.negate();
        }
        if (a1.mod(div).equals(BigInteger.ZERO) && b1.mod(div).equals(BigInteger.ZERO))
        {
            long a=a1.divide(div).longValue();
            long b=b1.divide(div).longValue();
            return new long[]{a,b};
        }
        else
        {
            return null;
        }
    }
}
