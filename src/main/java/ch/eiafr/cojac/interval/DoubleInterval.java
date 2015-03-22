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
        if (isNan)
        {
            return "[NaN;NaN]";
        }
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

    /**
     * @param a 1st operand of the subtraction
     * @param b 2st operand of the subtraction
     *
     * @return a new DoubleInterval that's the result of the a - b operation on interval
     */
    public static DoubleInterval sub(DoubleInterval a, DoubleInterval b)
    {
        if (a.isNan || b.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        double v1 = a.inf - b.sup;
        double v2 = a.sup - b.inf;
        return roundedInterval(v1, v2);
    }

    /**
     * @param a 1st operand of the multiplication
     * @param b 2st operand of the multiplication
     *
     * @return a new DoubleInterval that's the result of the a * b operation on interval
     */
    public static DoubleInterval mul(DoubleInterval a, DoubleInterval b)
    {
        if (a.isNan || b.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        double v1 = Math.min(Math.min(a.inf * b.inf, a.inf * b.sup), Math.min(a.sup * b.inf, a.sup * b.sup));
        double v2 = Math.max(Math.max(a.inf * b.inf, a.inf * b.sup), Math.max(a.sup * b.inf, a.sup * b.sup));
        return roundedInterval(v1, v2);
    }

    /**
     * @param a 1st operand of the division
     * @param b 2st operand of the division
     *
     * @return a DoubleInterval that's the result of the a/b operation
     * - if the b interval contains 0.0, the result interval is NaN
     */
    public static DoubleInterval div(DoubleInterval a, DoubleInterval b)
    {
        if (a.isNan || b.isNan || isIn(b, 0.0))
        {
            return new DoubleInterval(Double.NaN);
        }
        double v1 = Math.min(Math.min(a.inf / b.inf, a.inf / b.sup), Math.min(a.sup / b.inf, a.sup / b.sup));
        double v2 = Math.max(Math.max(a.inf / b.inf, a.inf / b.sup), Math.max(a.sup / b.inf, a.sup / b.sup));
        return roundedInterval(v1, v2);
    }

    /**
     * @param base 1st operand of the power 2 operation
     *
     * @return a new DoubleInterval that's the result of the pow operation on an interval
     */
    public static DoubleInterval pow2(DoubleInterval base)
    {
        if (base.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        if(base.inf > 0.0)
        {
            double v1 = Math.pow(base.inf,2.0);
            double v2 = Math.pow(base.sup,2.0);
            return roundedInterval(v1,v2);
        }
        else if(base.sup < 0.0)
        {
            double v1 = Math.pow(base.sup,2.0);
            double v2 = Math.pow(base.inf,2.0);
            return roundedInterval(v1,v2);
        }
        else // 0 is in the base interval
        {
            return new DoubleInterval(0.0,Math.max(Math.pow(base.inf,2.0),Math.pow(base.sup,2.0)));
        }
    }

    /**
     * @param base     1st operand of the power exponent operation
     *                 PRE : base.inf >= 0.0
     * @param exponent 2st operand of the operation
     *
     * @return a new DoubleInterval that's the result of the pow operation on an interval
     */
    public static DoubleInterval pow(DoubleInterval base, double exponent)
    {
        if (base.isNan || Double.isNaN(exponent))
        {
            return new DoubleInterval(Double.NaN);
        }
        assert (base.inf >= 0.0);
        double v1 = Math.pow(base.inf, exponent);
        double v2 = Math.pow(base.sup, exponent);
        return roundedInterval(v1, v2);
    }

    /**
     * @param base     1st operand of the power exponent operation
     *                 PRE : base.inf >= 0.0
     * @param exponent 2st operand of the operation
     *
     * @return a new DoubleInterval that's the result of the base^exponent operation
     * because the pow function is monotone, the result is esay to compute
     */
    public static DoubleInterval pow(DoubleInterval base, DoubleInterval exponent)
    {
        if (base.isNan || exponent.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        assert (base.inf >= 0.0);
        double v1 = Math.min(Math.min(Math.pow(base.inf, exponent.inf), Math.pow(base.inf, exponent.sup)),
                Math.min(Math.pow(base.sup, exponent.inf), Math.pow(base.sup, exponent.sup)));
        double v2 = Math.max(Math.max(Math.pow(base.inf, exponent.inf), Math.pow(base.inf, exponent.sup)),
                Math.max(Math.pow(base.sup, exponent.inf), Math.pow(base.sup, exponent.sup)));
        return roundedInterval(v1,v2);
    }

    /**
     * @param a argument for the exponential function
     *
     * @return a new DoubleInterval that's the result of the exponential function
     */
    public static DoubleInterval exp(DoubleInterval a)
    {
        if (a.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        double v1 = Math.exp(a.inf);
        double v2 = Math.exp(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a argument for the logarithmic function
     *
     * @return a new DoubleInterval that's the result of the logarithmic function (ln)
     */
    public static DoubleInterval log(DoubleInterval a)
    {
        if (a.isNan || a.inf < 0)
        {
            return new DoubleInterval(Double.NaN);
        }
        double v1 = Math.log(a.inf);
        double v2 = Math.log(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a argument for the logarithmic base 10 function
     *
     * @return a new DoubleInterval that's the result of the logarithmic function
     */
    public static DoubleInterval log10(DoubleInterval a)
    {
        if (a.isNan || a.inf < 0)
        {
            return new DoubleInterval(Double.NaN);
        }
        double v1 = Math.log10(a.inf);
        double v2 = Math.log10(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a operand of the square 2 operation
     *          PRE : the interval must be positive (a.inf >= 0.0)
     *
     * @return a new DoubleInterval that's the result of the sqrt operation
     */
    public static DoubleInterval sqrt(DoubleInterval a)
    {
        if (a.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        assert (a.inf >= 0.0);
        double v1 = Math.sqrt(a.inf);
        double v2 = Math.sqrt(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a operand of the absolute operation
     *
     * @return a new DoubleInterval that's the absolute interval of the operand
     */
    public static DoubleInterval abs(DoubleInterval a)
    {
        if (a.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        if (isIn(a, 0.0))
        {
            double v1 = 0.0;
            double v2 = Math.max(-a.inf, a.sup);
            v2 = v2 + Math.ulp(v2);
            return new DoubleInterval(v1, v2);
        }
        else if (a.sup < 0)
        {
            return new DoubleInterval(-a.sup, -a.inf);
        }
        else //(a.inf > 0)
        {
            return new DoubleInterval(a.inf, a.sup); // No need rounded, the result is already know
        }
    }

    /**
     * @param a operand of the negative operation
     *
     * @return a new DoubleInterval that's the negative the operand
     */
    public static DoubleInterval neg(DoubleInterval a)
    {
        if (a.isNan)
        {
            return new DoubleInterval(Double.NaN);
        }
        return new DoubleInterval(-a.sup, -a.inf);
    }

    /**
     * @param inf inferior bound of the interval
     * @param sup superior bound of the interval
     *
     * @return a new DoubleInterval with the inf minus ulp and the sup bonus ulp
     */
    private static DoubleInterval roundedInterval(double inf, double sup)
    {
        if (Double.isNaN(inf) || Double.isNaN(sup))
        {
            return new DoubleInterval(Double.NaN);
        }
        return new DoubleInterval(inf - Math.ulp(inf), sup + Math.ulp(sup));
    }
}

