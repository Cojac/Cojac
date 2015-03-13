package ch.eiafr.cojac.interval;

/**
 * Created by Snipy on 11.03.15.
 */
public class DoubleInterval implements Comparable<DoubleInterval>
{
    private double inf;
    private double sup;
    private int precision;

    public DoubleInterval(double inf, double sup)
    {
        this.inf = inf;
        this.sup = sup;
    }

    // Todo
    @Override
    public int compareTo(DoubleInterval o)
    {
        return 0;
    }

    /* Mathematical operations */

    public static DoubleInterval add (DoubleInterval valueA, DoubleInterval valueB)
    {
        return null;
    }

    public static DoubleInterval sub (DoubleInterval valueA, DoubleInterval valueB)
    {
        return null;
    }

    public static DoubleInterval mul (DoubleInterval valueA, DoubleInterval valueB)
    {
        return null;
    }

    public static DoubleInterval div (DoubleInterval valueA, DoubleInterval valueB)
    {
        return null;
    }

    public static DoubleInterval pow2 (DoubleInterval base)
    {
        return null;
    }

    public static DoubleInterval pow (DoubleInterval base,double exponent)
    {
        return null;
    }

    public static DoubleInterval pow (DoubleInterval base,DoubleInterval exponent)
    {
        return null;
    }

    public static DoubleInterval exp (DoubleInterval value)
    {
        return null;
    }

    public static DoubleInterval log (DoubleInterval value)
    {
        return null;
    }
    public static DoubleInterval log2 (DoubleInterval value)
    {
        return null;
    }
    public static DoubleInterval log10 (DoubleInterval value)
    {
        return null;
    }
}
