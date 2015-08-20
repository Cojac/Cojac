package ch.eiafr.cojac.interval;

import static java.lang.Math.PI;

/**
 * <p>
 * Note : the mathematical operation does not treat operation with overflow. In
 * the future, maybe implement some features to frame those special event
 * Example : [-MAX_VALUE;MAX_VALUE] + [0.0;0.0] is giving [-infinity;infinity] :
 *
 * DoubleInterval b = new DoubleInterval(-Double.MAX_VALUE,Double.MAX_VALUE);
 * DoubleInterval c = new DoubleInterval(0.0);
 * System.out.println(DoubleInterval.add(b,c)); // [-NEGATIVE_INFINITY,Infinity]
 * </p>
 *
 * @version 0.1
 */
public class DoubleInterval implements Comparable<DoubleInterval> {
    public final double inf;
    public final double sup;

    //private final boolean isNan; // TODO: remove that field

    private static final double HALF_PI = PI / 2.0;
    private static final double ONE_AND_HALF_PI = PI * 1.5;
    private static final double TWO_PI = PI * 2.0;
    
    /**
     * Constructor
     *
     * @param inf
     *            need to be smaller than sup
     * @param sup
     *            need to be bigger than inf
     */
    public DoubleInterval(double inf, double sup) {
            this.inf = inf;
            this.sup = sup;
    }

    /**
     * Constructor
     *
     * @param value
     *            value of the created interval, same has new
     *            DoubleInterval(a,a);
     */
    public DoubleInterval(double value) {
        this(value, value);
    }

    /**
     * Constructor
     *
     * @param a
     *            value of the DoubleInterval to "copy"
     */
    public DoubleInterval(DoubleInterval a) {
        this(a.inf, a.sup);
    }

    /**
     * @param o
     *            another DoubleInterval to be compared with this
     *
     * @return - 1 if this is absolutely bigger than o - 0 if there is some
     *         shared region - -1 if this is absolutely smaller than o
     */
    @Override
    public int compareTo(DoubleInterval o) {
        if (this.isNan() && o.isNan()) {
            return 0;
        }
        if (this.isNan()) {
            return -1;
        }
        if (o.isNan()) {
            return 1;
        }
        if (o.sup < this.inf) {
            return 1;
        }
        if (o.inf > this.sup) {
            return -1;
        }
        return 0;
    }

    /**
     * <p>
     * Note : the comparison with infinity are the same with some basic
     * double... [NEGATIVE_INFINITY;POSITIVE_INFINITY] includes
     * NEGATIVE_INFINITY and POSITIVE_INFINITY
     * </p>
     *
     * @param value
     *            double that's is compared with this (see has a set)
     *
     * @return - value < inf -> 1, the value is under the set 
     *         - value > inf && value < sup -> 0, the value is in the set ! 
     *         - value > sup -> -1, the value is over the set
     */
    public int compareTo(double value) {
        if (this.isNan() && Double.isNaN(value)) {
            return 0;
        }
        if (this.isNan()) {
            return -1;
        }
        if (Double.isNaN(value)) {
            return 1;
        }
        if (value < inf) {
            return 1;
        }
        if (value > sup) {
            return -1;
        }
        return 0;
    }

    /**
     * @param o
     *            DoubleInterval to be compared with this
     *
     * @return true only if the Interval are strictly equal
     */
    public boolean strictlyEquals(DoubleInterval o) {
        return !(o.isNan() || this.isNan()) &&
                (this.inf == o.inf && this.sup == o.sup);
    }
    
    public boolean overlaps(DoubleInterval a) {
        return Math.max(this.inf, a.inf) <= Math.min(this.sup, a.sup);
    }

    @Override
    public String toString() {
        if (isNan()) {
            return "[NaN;NaN]";
        }
        return "[" + this.inf + ";" + this.sup + "]";
    }

    /**
     * @param a
     *            DoubleInterval to use
     *
     * @return the width of the interval
     */
    public static double width(DoubleInterval a) {
        assert a.hasValidBounds();
        return a.sup - a.inf;
    }

    /**
     * Test if b is in the interval
     *
     * @param b
     *            double b to test
     *
     * @return true if b is inside, else false
     */
    /* Interval operation */
    public boolean contains(double b) {
        return  b >= this.inf && b <= this.sup;
    }

    /** @return true if b is completely in the Interval, false otherwise */
    public boolean contains(DoubleInterval b) {
        return  b.inf >= this.inf && b.sup <= this.sup;
    }

    /**
     * @return true if the interval is bounding NaN
     */
    public boolean isNan() {
        return Double.isNaN(inf) || Double.isNaN(sup);
    }

    /**
     * Used for some test... test if the Interval is Degenerated
     * <p>
     * Note : is the Interval is NaN, return true... See DoubleInterval.isNan()
     * </p>
     *
     * @return true if the interval ins't degenerated : this.inf <= this.sup,
     *         else false
     */
    public boolean hasValidBounds() {
        return this.isNan() || this.inf <= this.sup;
    }

    /* Mathematical operations */

    /**
     * @param a
     *            1st operand of the addition
     * @param b
     *            2st operand of the addition
     *
     * @return a new DoubleInterval that's the result of the a + b operation on
     *         interval
     */
    public static DoubleInterval add(DoubleInterval a, DoubleInterval b) {
        double v1 = a.inf + b.inf;
        double v2 = a.sup + b.sup;
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            1st operand of the subtraction
     * @param b
     *            2st operand of the subtraction
     *
     * @return a new DoubleInterval that's the result of the a - b operation on
     *         interval
     */
    public static DoubleInterval sub(DoubleInterval a, DoubleInterval b) {
        double v1 = a.inf - b.sup;
        double v2 = a.sup - b.inf;
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            1st operand of the multiplication
     * @param b
     *            2st operand of the multiplication
     *
     * @return a new DoubleInterval that's the result of the a * b operation on
     *         interval
     */
    public static DoubleInterval mul(DoubleInterval a, DoubleInterval b) {
        double v1 = Math.min(Math.min(a.inf * b.inf, a.inf * b.sup), Math.min(a.sup *
                b.inf, a.sup * b.sup));
        double v2 = Math.max(Math.max(a.inf * b.inf, a.inf * b.sup), Math.max(a.sup *
                b.inf, a.sup * b.sup));
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            1st operand of the division
     * @param b
     *            2st operand of the division
     *
     * @return a DoubleInterval that's the result of the a/b operation 
     *         - if the b interval contains 0.0, the result interval is NaN
     */
    public static DoubleInterval div(DoubleInterval a, DoubleInterval b) {
        if (b.contains(0.0)) {
            return new DoubleInterval(Double.NaN);
        }
        double v1 = Math.min(Math.min(a.inf / b.inf, a.inf / b.sup), Math.min(a.sup /
                b.inf, a.sup / b.sup));
        double v2 = Math.max(Math.max(a.inf / b.inf, a.inf / b.sup), Math.max(a.sup /
                b.inf, a.sup / b.sup));
        return roundedInterval(v1, v2);
    }

    /**
     * @param base
     *            1st operand of the power 2 operation
     *
     * @return a new DoubleInterval that's the result of the pow operation on an
     *         interval
     */
    public static DoubleInterval pow2(DoubleInterval base) {
        double v1 = base.inf*base.inf;
        double v2 = base.sup*base.sup;
        if (base.inf > 0.0) {
            return roundedInterval(v1, v2);
        } else if (base.sup < 0.0) {
            return roundedInterval(v2, v1);
        } else { // 0 is in the base interval
            return new DoubleInterval(0.0, Math.max(v1, v2));
        }
    }

    /**
     * @param base
     *            1st operand of the power exponent operation PRE : base.inf >=
     *            0.0
     * @param exponent
     *            2st operand of the operation
     *
     * @return a new DoubleInterval that's the result of the pow operation on an
     *         interval
     */
    public static DoubleInterval pow(DoubleInterval base, double exponent) {
        assert (base.isNan() || base.inf >= 0.0);
        double v1 = Math.pow(base.inf, exponent);
        double v2 = Math.pow(base.sup, exponent);
        return roundedInterval(v1, v2);
    }

    /**
     * @param base
     *            1st operand of the power exponent operation PRE : base.inf >=
     *            0.0
     * @param exponent
     *            2st operand of the operation
     *
     * @return a new DoubleInterval that's the result of the base^exponent
     *         operation because the pow function is monotone, the result is
     *         esay to compute
     */
    public static DoubleInterval pow(DoubleInterval base, DoubleInterval exponent) {
        assert (base.isNan() || base.inf >= 0.0);
        double v1 = Math.min(Math.min(Math.pow(base.inf, exponent.inf), Math.pow(base.inf, exponent.sup)), Math.min(Math.pow(base.sup, exponent.inf), Math.pow(base.sup, exponent.sup)));
        double v2 = Math.max(Math.max(Math.pow(base.inf, exponent.inf), Math.pow(base.inf, exponent.sup)), Math.max(Math.pow(base.sup, exponent.inf), Math.pow(base.sup, exponent.sup)));
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            argument for the exponential function
     *
     * @return a new DoubleInterval that's the result of the exponential
     *         function
     */
    public static DoubleInterval exp(DoubleInterval a) {
        double v1 = Math.exp(a.inf);
        double v2 = Math.exp(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            argument for the logarithmic function PRE : param a must be >
     *            0
     *
     * @return a new DoubleInterval that's the result of the logarithmic
     *         function (ln)
     */
    public static DoubleInterval log(DoubleInterval a) {
        if (a.inf < 0) {
            return new DoubleInterval(Double.NaN);
        }
        assert (a.isNan() || a.inf > 0.0);
        double v1 = Math.log(a.inf);
        double v2 = Math.log(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            argument for the logarithmic base 10 function PRE : param a
     *            must be > 0
     *
     * @return a new DoubleInterval that's the result of the logarithmic
     *         function
     */
    public static DoubleInterval log10(DoubleInterval a) {
        if (a.inf < 0) {
            return new DoubleInterval(Double.NaN);
        }
        assert (a.isNan() || a.inf > 0.0);
        double v1 = Math.log10(a.inf);
        double v2 = Math.log10(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the square 2 operation PRE : the interval must be
     *            positive (a.inf >= 0.0)
     *
     * @return a new DoubleInterval that's the result of the sqrt operation
     */
    public static DoubleInterval sqrt(DoubleInterval a) {
        assert (a.isNan() || a.inf > 0.0);
        double v1 = Math.sqrt(a.inf);
        double v2 = Math.sqrt(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the absolute operation
     *
     * @return a new DoubleInterval that's the absolute interval of the operand
     */
    public static DoubleInterval abs(DoubleInterval a) {
        if (a.contains(0.0)) {
            double v1 = 0.0;
            double v2 = Math.max(-a.inf, a.sup);
            return new DoubleInterval(v1, v2);
        } else if (a.sup < 0) {
            return new DoubleInterval(-a.sup, -a.inf);
        } else { // (a.inf > 0)
            return new DoubleInterval(a.inf, a.sup); // Doesn't need rounding, the
                                                     // result is already known
        }
    }

    /**
     * @param a
     *            operand of the negative operation
     *
     * @return a new DoubleInterval that's the negative the operand
     */
    public static DoubleInterval neg(DoubleInterval a) {
        return new DoubleInterval(-a.sup, -a.inf);
    }

    /** @return a new DoubleInterval that's the result of the cosine operation on a*/
    public static DoubleInterval cos(DoubleInterval a) {
        if (a.isNan()) return new DoubleInterval(Double.NaN);
        DoubleInterval wholeDomain = new DoubleInterval(-1,+1);
        if (width(a) >= TWO_PI) return wholeDomain;
        double inf = (a.inf % TWO_PI + TWO_PI) % TWO_PI; // now inf is in [0..2PI[
        double sup = inf + width(a);
        if (inf <= PI) {
            if (sup >= TWO_PI) return wholeDomain;
            if (sup <= PI) // monotone decreasing...
                return roundedInterval(Math.cos(sup), Math.cos(inf));
            double max=Math.max(Math.cos(inf), Math.cos(sup));
            DoubleInterval x= roundedInterval(-1, max);
            return new DoubleInterval(-1, x.sup); // "unround" x.inf
        }
        if (sup >= TWO_PI+PI) return wholeDomain;
        if (sup < TWO_PI) // monotone increasing...
            return roundedInterval(Math.cos(inf), Math.cos(sup));
        double min=Math.min(Math.cos(inf), Math.cos(sup));
        DoubleInterval x= roundedInterval(min, +1);
        return new DoubleInterval(x.inf, +1); // "unround" x.sup
    }

    /** @return a new DoubleInterval that's the result of the sine operation on a*/
    public static DoubleInterval sin(DoubleInterval a) {
        // using sin(x + pi/2) = cos(x)
        return cos(new DoubleInterval(a.inf - HALF_PI, a.sup - HALF_PI));
    }

    /** @return a new DoubleInterval that's the result of the tangent operation on a*/
    public static DoubleInterval tan(DoubleInterval a) {
        // (see [Julmy15], p. 26)
        if (a.isNan()) 
            return new DoubleInterval(Double.NaN);
        DoubleInterval wholeDomain = new DoubleInterval(Double.NEGATIVE_INFINITY, 
                                                        Double.POSITIVE_INFINITY);
        if (width(a) >= PI)
            return wholeDomain;
        double inf = (a.inf % PI + PI) % PI; // now inf is in [0..PI[
        double sup = inf + width(a);
        if (inf <= HALF_PI && sup >= HALF_PI)
            return wholeDomain;
        if (sup >= ONE_AND_HALF_PI)
            return wholeDomain;
        double v1 = Math.tan(inf);
        double v2 = Math.tan(sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the sinh operation
     *
     * @return a new DoubleInterval that's the negative the operand
     */
    public static DoubleInterval sinh(DoubleInterval a) {
        double v1 = Math.sinh(a.inf);
        double v2 = Math.sinh(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the cosh operation
     *
     * @return a new DoubleInterval that's the negative the operand
     */
    public static DoubleInterval cosh(DoubleInterval a) {
        double sup = Math.max(Math.cosh(a.inf), Math.cosh(a.sup));
        return roundedInterval(0.0, sup);
    }

    /**
     * @param a
     *            operand of the tanh operation
     *
     * @return a new DoubleInterval that's the negative the operand
     */
    public static DoubleInterval tanh(DoubleInterval a) {
        double v1 = Math.tanh(a.inf);
        double v2 = Math.tanh(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the asin operation PRE : the interval must be in
     *            [-1;1]
     *
     * @return a new DoubleInterval that's the negative the operand
     */
    public static DoubleInterval asin(DoubleInterval a) {
        assert a.isNan() || (a.inf >= -1.0 && a.sup <= 1.0);
        double v1 = Math.asin(a.inf);
        double v2 = Math.asin(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the acos operation PRE : the interval must be in
     *            [-1;1]
     *
     * @return a new DoubleInterval that's the negative the operand
     */
    public static DoubleInterval acos(DoubleInterval a) {
        assert a.isNan() || (a.inf >= -1.0 && a.sup <= 1.0);
        double v1 = Math.acos(a.sup);
        double v2 = Math.acos(a.inf);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the atan operation
     *
     * @return a new DoubleInterval that's the negative the operand
     */
    public static DoubleInterval atan(DoubleInterval a) {
        double v1 = Math.atan(a.inf);
        double v2 = Math.atan(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * Notes : the returned Interval is pessimistic
     * @return a new DoubleInterval that's the result of the a%b operation
     */
    public static DoubleInterval modulo(DoubleInterval a, DoubleInterval b) {
        if (a.isNan() || b.isNan()) return new DoubleInterval(Double.NaN);
        if (b.contains(0.0))        return new DoubleInterval(Double.NaN);

        int q1=(int)(a.inf/b.inf), q2=(int)(a.sup/b.inf);
        int q3=(int)(a.inf/b.sup), q4=(int)(a.sup/b.sup);
        double r1=a.inf%b.inf, r2=a.sup%b.inf;
        double r3=a.inf%b.sup, r4=a.sup%b.sup;
        if(q1==q2 && q1==q3 && q1==q4) { // same quotient
            double rmin=Math.min(r1, Math.min(r2, Math.min(r3,r4)));
            double rmax=Math.max(r1, Math.max(r2, Math.max(r3,r4)));
            return new DoubleInterval(rmin, rmax);
        }
        
        double max = Math.max(Math.abs(b.inf), Math.abs(b.sup));
        if (a.contains(0.0)) {
            return new DoubleInterval(-max, max);
        }
        return a.inf > 0 ? new DoubleInterval(0.0, max)
                         : new DoubleInterval(-max, 0.0);
    }

    /**
     * @param inf
     *            inferior bound of the interval
     * @param sup
     *            superior bound of the interval
     *
     * @return a new DoubleInterval with the inf minus ulp and the sup bonus ulp
     */
    private static DoubleInterval roundedInterval(double inf, double sup) {
        return new DoubleInterval(inf - Math.ulp(inf), sup + Math.ulp(sup));
    }

    public static DoubleInterval toRadians(DoubleInterval a) {
        double v1 = Math.toRadians(a.inf);
        double v2 = Math.toRadians(a.sup);
        return roundedInterval(v1, v2);
    }

    public static DoubleInterval toDegrees(DoubleInterval a) {
        double v1 = Math.toDegrees(a.inf);
        double v2 = Math.toDegrees(a.sup);
        return roundedInterval(v1, v2);
    }

}
