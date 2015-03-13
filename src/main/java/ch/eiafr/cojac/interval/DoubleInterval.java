package ch.eiafr.cojac.interval;

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

    /*
        value < inf -> 1 , the value is under the set
        value > inf && value < sup -> 0 , the value is in the set !
        value > sup -> -1 , the value is over the set
     */
    public int compareTo(double value)
    {
        if (value < inf)
        {
            return 1;
        }
        if (value > sup)
        {
            return -1;
        }
        return 0;
    }


    @Override
    public String toString()
    {
        return String.format("[%f;%f]", this.inf, this.sup);
    }

     /* Mathematical operations */
    // Todo : complete this


    public static DoubleInterval add(DoubleInterval a, DoubleInterval b)
    {
        double v1 = a.inf + b.inf;
        double v2 = a.sup + b.sup;
        return new DoubleInterval(v1 - Math.ulp(v1), v2 + Math.ulp(v2));
    }

    public static DoubleInterval sub(DoubleInterval a, DoubleInterval b)
    {
        return null;
    }

    public static DoubleInterval mul(DoubleInterval a, DoubleInterval b)
    {
        return null;
    }

    public static DoubleInterval div(DoubleInterval a, DoubleInterval b)
    {
        return null;
    }

    public static DoubleInterval pow2(DoubleInterval base)
    {
        return null;
    }

    public static DoubleInterval pow(DoubleInterval base, double exponent)
    {
        return null;
    }

    public static DoubleInterval pow(DoubleInterval base, DoubleInterval exponent)
    {
        return null;
    }

    public static DoubleInterval exp(DoubleInterval a)
    {
        return null;
    }

    public static DoubleInterval log(DoubleInterval a)
    {
        return null;
    }

    public static DoubleInterval log2(DoubleInterval a)
    {
        return null;
    }

    public static DoubleInterval log10(DoubleInterval a)
    {
        return null;
    }
}

