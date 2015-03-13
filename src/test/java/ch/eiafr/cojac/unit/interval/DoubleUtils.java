package ch.eiafr.cojac.unit.interval;

import java.util.Random;

/**
 * Use to generate random value for the DoubleInterval Test
 */
public class DoubleUtils
{
    private static Random r = new Random();

    /**
     * @return a random positive double that's part of the interval [0.0;Double.MAX_VALUE]
     */
    public static double rDouble()
    {
        return ((double)r.nextInt(Integer.MAX_VALUE-1)) + r.nextDouble();
    }

    /**
     * @param value reference fot the computation
     *
     * @return a double bigger than value and positive
     *      - if value == Double.MaxValue, return Double.MaxValue
     *      - if value == NaN, return NaN
     *      - if value == POSITIVE_INFINITY, return MAX_VALUE
     *
     * For example with a value 20.0 the return could be 30.0
     */
    public static double getBiggerDouble(double value)
    {
        if(Double.isNaN(value))
        {
            return Double.NaN;
        }
        if(value == Double.POSITIVE_INFINITY)
        {
            return Double.MAX_VALUE;
        }
        if(value < 0.0)
        {
            return rDouble();
        }
        if(value == Double.MAX_VALUE)
        {
            return Double.MAX_VALUE;
        }
        return value + ((double)r.nextInt(Integer.MAX_VALUE - 1 - (int)value )) + r.nextDouble();
    }

    /**
     * @param value reference fot the computation
     *
     * @return a double smaller than value and positive
     *      - if value == 0.0, return 0.0
     *      - if value == NaN, return NaN
     *      - if value == MIN_VALUE, return 0.0;
     *      - if value < 0, return a random positive double
     *
     * For example with a value 20.0 the return could be 10.0
     */
    public static double getSmallerDouble(double value)
    {
        if(Double.isNaN(value))
        {
            return Double.NaN;
        }
        if(value == Double.MIN_VALUE)
        {
            return 0.0;
        }
        if((value) == 0.0)
        {
            return 0.0;
        }
        if(value < 0.0)
        {
            return ((double)r.nextInt((int)-value)) + r.nextDouble();
        }
        if(value < 1.0)
        {
            return r.nextDouble();
        }
        return ((double)r.nextInt((int)value)) + r.nextDouble();
    }

    /**
     * @param value reference fot the computation
     *
     * @return a double that's is bigger (in absolute) than value and negative
     *      - if value == NaN, return NaN
     *      - if value == 0.0, return a random negative double
     *      - if abs(value) == MAX_VALUE, return - MAX_VALUE
     *      - if value == NEGATIVE_INFINITY, return NEGATIVE_INFINITY
     *      - if value == POSITIVE_INFINITY, return NEGATIVE_INFINITY
     *
     * For example with a value -20.0 the return could be -30.0
     */
    public static double getBiggerNegativeDouble(double value)
    {
        if(Double.isNaN(value))
        {
            return Double.NaN;
        }
        if(value == Double.NEGATIVE_INFINITY || value == Double.POSITIVE_INFINITY)
        {
            return Double.NEGATIVE_INFINITY;
        }
        return - getBiggerDouble(Math.abs(value));
    }

    /**
     * @param value reference fot the computation
     *
     * @return a double that's is smaller (in absolute) than value and negative
     *
     * For example with a value -20.0 the return could be -10.0
     *      - if value == NaN, return NaN
     *      - if value == 0.0, return 0.0;
     *      - if abs(value) == MIN_VALUE, return 0.0
     */
    public static double getSmallerNegativeDouble(double value)
    {
        if(Double.isNaN(value))
        {
            return Double.NaN;
        }
        if(value == Double.NEGATIVE_INFINITY || value == Double.POSITIVE_INFINITY)
        {
            return -rDouble();
        }
        return - getSmallerDouble(Math.abs(value));
    }
}
