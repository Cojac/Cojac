package ch.eiafr.cojac.models.wrappers;

/**
 * Created by Snipy on 07.03.15.
 */
public class IntervalDouble extends Number implements Comparable<IntervalDouble>
{
    private double value;
    private boolean isNan;
    private boolean isInfinite;
    private boolean isPositiveInfinite;

    public IntervalDouble(double value)
    {
        this.value = value;
        //this.interval = Utils.kaucher(value,value);
    }

    public IntervalDouble(int value)
    {
        this.value = (double)value;
        //this.interval = RealInterval.valueOf((double)value,(double)value);
    }

    public IntervalDouble(long value)
    {
        this.value = (double)value;
        //this.interval = RealInterval.valueOf((double)value,(double)value);
    }

    public IntervalDouble(float value)
    {
        this.value = (double)value;
        //this.interval = RealInterval.valueOf((double)value,(double)value);
    }


    @Override
    public int intValue()
    {
        return (int)value;
    }

    @Override
    public long longValue()
    {
        return (long)value;
    }

    @Override
    public float floatValue()
    {
        return (float)value;
    }

    @Override
    public double doubleValue()
    {
        return value;
    }

    public static IntervalDouble dadd(IntervalDouble a, IntervalDouble b)
    {
        return null;
    }

    public static IntervalDouble dsub(IntervalDouble a, IntervalDouble b)
    {
        return null;
    }

    public static IntervalDouble dmul(IntervalDouble a, IntervalDouble b)
    {
        return null;
    }

    public static IntervalDouble ddiv(IntervalDouble a, IntervalDouble b)
    {
        return null;
    }

    public static IntervalDouble drem(IntervalDouble a, IntervalDouble b)
    {
        return null;
    }

    public static IntervalDouble dneg(IntervalDouble a)
    {
        return null;
    }

    public static long d2l(IntervalDouble a)
    {
        return a.longValue();
    }

    public static int d2i(IntervalDouble a)
    {
        return a.intValue();
    }

    public static float d2f(IntervalDouble a)
    {
        return a.floatValue();
    }

    public static IntervalDouble i2d(int a)
    {
        return new IntervalDouble(a);
    }

    public static IntervalDouble f2d(float a)
    {
        return new IntervalDouble(a);
    }

    public static IntervalDouble l2d(long a)
    {
        return new IntervalDouble(a);
    }

    @Override
    public String toString()
    {
        // reference : BigDecimalDouble.java
        if(isNan) return "Nan";
        if(isInfinite) return (isPositiveInfinite?'+':'-')+"Infinity";
        return String.format("%f",value);
    }

    /* Mathematical function */

    public static IntervalDouble math_sqrt(IntervalDouble a)
    {
        a.value = Math.sqrt(a.value);
        //a.interval.sqrt();
        return a;
    }

    // TODO
    @Override
    public int compareTo(IntervalDouble o)
    {
        return 0;
    }
}
