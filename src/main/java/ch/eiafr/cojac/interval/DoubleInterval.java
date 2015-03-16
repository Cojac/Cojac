package ch.eiafr.cojac.interval;

/**
 * <p>
 * Note : the mathematical operation does not treat operation with overflow
 * In the future, maybe implments some features to frame those special event
 * Example : [-MAX_VALUE;MAX_VALUE] + [0.0;0.0] is giving [-infinity;infinity]
 * </p>
 *
 * @version 0.1
 */
public class DoubleInterval implements Comparable<DoubleInterval>
{
    private double inf;
    private double sup;

    private boolean isNan;

    /**
     * Constructor
     *
     * @param inf need to be smaller than sup
     * @param sup need to be bigger than inf
     */
    public DoubleInterval(double inf, double sup)
    {
        if (Double.isNaN(inf) || Double.isNaN(sup))
        {
            this.inf = Double.NaN;
            this.sup = Double.NaN;
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
     *
     * @param value value of the created interval, same has new DoubleInterval(a,a);
     */
    public DoubleInterval(double value)
    {
        if (Double.isNaN(inf) || Double.isNaN(sup) || Double.isNaN(value))
        {
            this.inf = Double.NaN;
            this.sup = Double.NaN;
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
     * @return - 1 if this is absolutely bigger than o
     * - 0 if there is some shared region
     * - -1 if this is absolutely smaller than o
     */
    @Override
    public int compareTo(DoubleInterval o)
    {
        if (this.isNan && o.isNan)
        {
            return 0;
        }
        if (this.isNan)
        {
            return -1;
        }
        if (o.isNan)
        {
            return 1;
        }
        if (o.sup < this.inf)
        {
            return 1;
        }
        if (o.inf > this.sup)
        {
            return -1;
        }
        return 0;
    }

    /**
     * <p>
     * Note : the comparaison with infinity are the same with some basic double...
     * [NEGATIVE_INFINITY;POSITIVE_INFINITY] includes NEGATIVE_INFINITY and POSITIVE_INFINITY
     * </p>
     *
     * @param value dobule that's is compared with this (see has a set)
     *
     * @return - value < inf -> 1 , the value is under the set
     * - value > inf && value < sup -> 0 , the value is in the set !
     * - value > sup -> -1 , the value is over the set
     */
    public int compareTo(double value)
    {
        if (this.isNan && Double.isNaN(value))
        {
            return 0;
        }
        if (this.isNan)
        {
            return -1;
        }
        if (Double.isNaN(value))
        {
            return 1;
        }
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
        if (o.isNan || this.isNan)
        {
            return false;
        }
        return (this.inf == o.inf && this.sup == o.sup);
    }


    @Override
    public String toString()
    {
        return String.format("[%f;%f]", this.inf, this.sup);
    }

    /**
     * Test if b is in the interval a
     *
     * @param a DoubleInterval see has a set
     * @param b double b to test
     *
     * @return true if b is in a, else false
     * if the interval isNan, return false
     */
    /* Interval operation */
    public static boolean isIn(DoubleInterval a, double b)
    {
        if (a.isNan)
        {
            return false;
        }
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
        return (b.inf >= a.inf && b.sup <= a.sup);
    }


    /**
     * @return true if the interval is bounding NaN
     */
    public boolean isNan()
    {
        return isNan;
    }

    /**
     * Used for some test... test if the Interval is Degenerated
     * <p>
     * Note : is the Interval is NaN, return true...
     * See DoubleInterval.isNan()
     * </p>
     *
     * @return true if the interval ins't degenerated : this.inf <= this.sup, else false
     */
    public boolean testBounds()
    {
        if (this.isNan)
        {
            return true;
        }
        return this.inf <= this.sup;
    }


     /* Mathematical operations */

    // Todo : complete this

    /**
     * @param a 1st operand of the addition
     * @param b 2st operand of the addition
     *
     * @return a new DoubleInterval that's the result of the a + b operation on interval
     */
    public static DoubleInterval add(DoubleInterval a, DoubleInterval b)
    {
        if (a.isNan || b.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        double v1 = a.inf + b.inf;
        double v2 = a.sup + b.sup;
        return roundedInterval(v1, v2);
    }

    public static DoubleInterval sub(DoubleInterval a, DoubleInterval b)
    {
        if (a.isNan || b.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }

        return null;
    }

    public static DoubleInterval mul(DoubleInterval a, DoubleInterval b)
    {
        if (a.isNan || b.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        return null;
    }

    public static DoubleInterval div(DoubleInterval a, DoubleInterval b)
    {
        if (a.isNan || b.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        return null;
    }

    public static DoubleInterval pow2(DoubleInterval base)
    {
        if (base.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        return null;
    }

    public static DoubleInterval pow(DoubleInterval base, double exponent)
    {
        if (base.isNan || Double.isNaN(exponent))
        {
            return new DoubleInterval(Double.NaN);
        }
        return null;
    }

    public static DoubleInterval pow(DoubleInterval base, DoubleInterval exponent)
    {
        if (base.isNan || exponent.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        return null;
    }

    public static DoubleInterval exp(DoubleInterval a)
    {
        if (a.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        return null;
    }

    public static DoubleInterval log(DoubleInterval a)
    {
        if (a.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        return null;
    }

    public static DoubleInterval log2(DoubleInterval a)
    {
        if (a.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        return null;
    }

    public static DoubleInterval log10(DoubleInterval a)
    {
        if (a.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        return null;
    }

    public static DoubleInterval abs(DoubleInterval a)
    {
        if (a.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        return null;
    }

    public static DoubleInterval neg(DoubleInterval a)
    {
        if (a.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        return null;
    }

    private static DoubleInterval roundedInterval(double v1, double v2)
    {
        return new DoubleInterval(v1 - Math.ulp(v1), v2 + Math.ulp(v2));
    }
}

