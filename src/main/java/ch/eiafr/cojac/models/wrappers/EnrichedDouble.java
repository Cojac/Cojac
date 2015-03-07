package ch.eiafr.cojac.models.wrappers;

import com.kenai.jinterval.rational_bounds.RealInterval;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 * Created by Snipy on 07.03.15.
 */
public class EnrichedDouble extends Number implements Comparable<EnrichedDouble>
{
    private double value;
    private RealInterval interval;
    private boolean isNan;
    private boolean isInfinite;
    private boolean isPositiveInfinite;

    public EnrichedDouble(double value)
    {
        this.value = value;
        this.interval = RealInterval.valueOf(value,value);
    }

    public EnrichedDouble(int value)
    {
        this.value = (double)value;
        this.interval = RealInterval.valueOf((double)value,(double)value);
    }

    public EnrichedDouble(long value)
    {
        this.value = (double)value;
        this.interval = RealInterval.valueOf((double)value,(double)value);
    }

    public EnrichedDouble(float value)
    {
        this.value = (double)value;
        this.interval = RealInterval.valueOf((double)value,(double)value);
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

    public static EnrichedDouble dadd(EnrichedDouble a, EnrichedDouble b)
    {
        return null;
    }

    public static EnrichedDouble dsub(EnrichedDouble a, EnrichedDouble b)
    {
        return null;
    }

    public static EnrichedDouble dmul(EnrichedDouble a, EnrichedDouble b)
    {
        return null;
    }

    public static EnrichedDouble ddiv(EnrichedDouble a, EnrichedDouble b)
    {
        return null;
    }

    public static EnrichedDouble drem(EnrichedDouble a, EnrichedDouble b)
    {
        return null;
    }

    public static EnrichedDouble dneg(EnrichedDouble a)
    {
        return null;
    }

    public static long d2l(EnrichedDouble a)
    {
        return a.longValue();
    }

    public static int d2i(EnrichedDouble a)
    {
        return a.intValue();
    }

    public static float d2f(EnrichedDouble a)
    {
        return a.floatValue();
    }

    public static EnrichedDouble i2d(int a)
    {
        return new EnrichedDouble(a);
    }

    public static EnrichedDouble f2d(float a)
    {
        return new EnrichedDouble(a);
    }

    public static EnrichedDouble l2d(long a)
    {
        return new EnrichedDouble(a);
    }

    @Override
    public String toString()
    {
        // reference : BigDecimalDouble.java
        if(isNan) return "Nan";
        if(isInfinite) return (isPositiveInfinite?'+':'-')+"Infinity";
        return String.format("%f ; [%f,%f]",value,interval.doubleInf(),interval.doubleSup());
    }

    /* Mathematical function */

    public static EnrichedDouble math_sqrt(EnrichedDouble a)
    {
        a.value = Math.sqrt(a.value);
        a.interval.sqrt();
        return a;
    }

    // TODO
    @Override
    public int compareTo(EnrichedDouble o)
    {
        return 0;
    }
}
