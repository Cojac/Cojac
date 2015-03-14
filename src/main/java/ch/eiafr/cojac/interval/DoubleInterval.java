package ch.eiafr.cojac.interval;

import ch.eiafr.cojac.Methods;

public class DoubleInterval implements Comparable<DoubleInterval>
{
    private double inf;
    private double sup;

    private boolean isNan;

    /**
     * Constructor
     * @param inf need to be smaller than sup
     * @param sup need to be bigger than inf
     */
    public DoubleInterval(double inf, double sup)
    {
        if(Double.isNaN(inf) || Double.isNaN(sup))
        {
            this.isNan = true;
        }
        else
        {
            this.inf = inf;
            this.sup = sup;
            this.isNan = false;
        }
    }

    /**
     * Constructor
     * @param value value of the created interval, same has new DoubleInterval(a,a);
     */
    public DoubleInterval(double value)
    {
        if(Double.isNaN(inf) || Double.isNaN(sup))
        {
            this.isNan = true;
        }
        else
        {
            this.inf = this.sup = value;
            this.isNan = false;
        }
    }

    /**
     * @param o another DoubleInterval to be compared with this
     *
     * @return
     *      - 1 if this is absolutely bigger than o
     *      - 0 if there is some shared region
     *      - -1 if this is absolutely smaller than o
     */
    @Override
    public int compareTo(DoubleInterval o)
    {
        if(this.isNan && o.isNan)
        {
            return 0;
        }
        if(this.isNan)
        {
            return -1;
        }
        if(o.isNan)
        {
            return 1;
        }
        if(o.sup < this.inf)
        {
            return 1;
        }
        if(o.inf > this.sup)
        {
            return -1;
        }
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

    /**
     * @param o DoubleInterval to be compared with this
     *
     * @return true only if the Interval are strictly equals
     */
    public boolean strictCompareTo(DoubleInterval o)
    {
        return (this.inf == o.inf && this.sup == o.sup);
    }


    @Override
    public String toString()
    {
        return String.format("[%f;%f]", this.inf, this.sup);
    }

    /**
     * Test if b is in the interval a
     * @param a DoubleInterval see has a set
     * @param b double b to test
     *
     * @return true if b is in a, else false
     */
    /* Interval operation */
    public static boolean isIn(DoubleInterval a, double b)
    {
        return (b >= a.inf && b <= a.sup);
    }

    /**
     * @param a DoubleInterval supposed to bee the encompassing one
     * @param b DoubleInterval supposed to be inside the DoubleInterval a
     *
     * @return true if b is completely in the Interval a, false otherwise
     */
    public static boolean isIn(DoubleInterval a, DoubleInterval b)
    {
        return (b.inf > a.inf && b.sup < a.sup);
    }


    /**
     * Used for some test... test if the Interval is Degenerated
     * <p>
     *     Note : is the Interval is NaN, return true...
     * </p>
     * @return true if the interval ins't degenerated : this.inf <= this.sup, else false
     */
    public boolean testBounds()
    {
        if(this.isNan)
        {
            return true;
        }
        return this.inf > this.sup;
    }

     /* Mathematical operations */
    // Todo : complete this

    /**
     * @param a 1st operand of the addition
     * @param b 2st operand of the addition
     *
     * @return a new DoubleInterval that's the result of the a + b operation
     */
    public static DoubleInterval add(DoubleInterval a, DoubleInterval b)
    {
        double v1 = a.inf + b.inf;
        double v2 = a.sup + b.sup;
        return roundedInterval(v1,v2);
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

    private static DoubleInterval roundedInterval(double v1, double v2)
    {
        return new DoubleInterval(v1 - Math.ulp(v1),v2 + Math.ulp(v2));
    }
}

