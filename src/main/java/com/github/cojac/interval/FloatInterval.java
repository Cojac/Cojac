/*
 * *
 *    Copyright 2011-2016 Frédéric Bapst & HEIA-FR students
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.cojac.interval;

import static com.github.cojac.models.FloatReplacerClasses.COJAC_STABILITY_THRESHOLD;
import static java.lang.Math.PI;

/**
 * <p>
 * Note : the mathematical operation does not treat operation with overflow. In
 * the future, maybe implement some features to frame those special event
 * Example : [-MAX_VALUE;MAX_VALUE] + [0.0;0.0] is giving [-infinity;infinity] :
 *
 * FloatInterval b = new FloatInterval(-Double.MAX_VALUE,Double.MAX_VALUE);
 * FloatInterval c = new FloatInterval(0.0);
 * System.out.println(FloatInterval.add(b,c)); // [-NEGATIVE_INFINITY,Infinity]
 * </p>
 *
 * @version 0.1
 */
public class FloatInterval implements Comparable<FloatInterval> {
    public final float inf;
    public final float sup;
    private static final float PIF = (float) PI;
    private static final float HALF_PI =  PIF / 2.0f;
    private static final float ONE_AND_HALF_PI =  PIF * 1.5f;
    private static final float TWO_PI = PIF * 2.0f;
    
    /**
     * Constructor
     *
     * @param inf
     *            need to be smaller than sup
     * @param sup
     *            need to be bigger than inf
     */
    public FloatInterval(float inf, float sup) {
            this.inf = inf;
            this.sup = sup;
    }

    /**
     * Constructor
     *
     * @param value
     *            value of the created interval, same has new
     *            FloatInterval(a,a);
     */
    public FloatInterval(float value) {
        this(value, value);
    }

    /**
     * Constructor
     *
     * @param a
     *            value of the FloatInterval to "copy"
     */
    public FloatInterval(FloatInterval a) {
        this(a.inf, a.sup);
    }

    /**
     * @param o
     *            another FloatInterval to be compared with this
     *
     * @return - 1 if this is absolutely bigger than o - 0 if there is some
     *         shared region - -1 if this is absolutely smaller than o
     */
    @Override
    public int compareTo(FloatInterval o) {
        if (this.isNaN() && o.isNaN()) {
            return 0;
        }
        if (this.isNaN()) {
            return -1;
        }
        if (o.isNaN()) {
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
        if (this.isNaN() && Double.isNaN(value)) {
            return 0;
        }
        if (this.isNaN()) {
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
     *            FloatInterval to be compared with this
     *
     * @return true only if the Interval are strictly equal
     */
    public boolean strictlyEquals(FloatInterval o) {
        return !(o.isNaN() || this.isNaN()) &&
                (this.inf == o.inf && this.sup == o.sup);
    }
    
    public boolean overlaps(FloatInterval a) {
        return Math.max(this.inf, a.inf) <= Math.min(this.sup, a.sup);
    }

    @Override
    public String toString() {
        if (isNaN()) {
            return "[NaN;NaN]";
        }
        return "[" + this.inf + ";" + this.sup + "]";
    }

    /**
     * @param a
     *            FloatInterval to use
     *
     * @return the width of the interval
     */
    public static float width(FloatInterval a) {
        assert a.hasValidBounds();
        return a.sup - a.inf;
    }

    /**
     * Test if b is in the interval
     *
     * @param b
     *            float b to test
     *
     * @return true if b is inside, else false
     */
    /* Interval operation */
    public boolean contains(double b) {
        return  b >= this.inf && b <= this.sup;
    }

    /** @return true if b is completely in the Interval, false otherwise */
    public boolean contains(FloatInterval b) {
        return  b.inf >= this.inf && b.sup <= this.sup;
    }

    /**
     * @return true if the interval is bounding NaN
     */
    public boolean isNaN() {
        return Double.isNaN(inf) || Double.isNaN(sup);
    }

    /**
     * Used for some test... test if the Interval is Degenerated
     * <p>
     * Note : is the Interval is NaN, return true... See FloatInterval.isNan()
     * </p>
     *
     * @return true if the interval ins't degenerated : this.inf <= this.sup,
     *         else false
     */
    public boolean hasValidBounds() {
        return this.isNaN() || this.inf <= this.sup;
    }

    /* Mathematical operations */

    /**
     * @param a
     *            1st operand of the addition
     * @param b
     *            2st operand of the addition
     *
     * @return a new FloatInterval that's the result of the a + b operation on
     *         interval
     */
    public static FloatInterval add(FloatInterval a, FloatInterval b) {
        float v1 = a.inf + b.inf;
        float v2 = a.sup + b.sup;
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            1st operand of the subtraction
     * @param b
     *            2st operand of the subtraction
     *
     * @return a new FloatInterval that's the result of the a - b operation on
     *         interval
     */
    public static FloatInterval sub(FloatInterval a, FloatInterval b) {
        float v1 = a.inf - b.sup;
        float v2 = a.sup - b.inf;
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            1st operand of the multiplication
     * @param b
     *            2st operand of the multiplication
     *
     * @return a new FloatInterval that's the result of the a * b operation on
     *         interval
     */
    public static FloatInterval mul(FloatInterval a, FloatInterval b) {
        float v1 = Math.min(Math.min(a.inf * b.inf, a.inf * b.sup), Math.min(a.sup *
                b.inf, a.sup * b.sup));
        float v2 = Math.max(Math.max(a.inf * b.inf, a.inf * b.sup), Math.max(a.sup *
                b.inf, a.sup * b.sup));
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            1st operand of the division
     * @param b
     *            2st operand of the division
     *
     * @return a FloatInterval that's the result of the a/b operation 
     *         - if the b interval contains 0.0, the result interval is NaN
     */
    public static FloatInterval div(FloatInterval a, FloatInterval b) {
        if (b.contains(0.0)) {
            return new FloatInterval(Float.NaN);
        }
        float v1 = Math.min(Math.min(a.inf / b.inf, a.inf / b.sup), Math.min(a.sup /
                b.inf, a.sup / b.sup));
        float v2 = Math.max(Math.max(a.inf / b.inf, a.inf / b.sup), Math.max(a.sup /
                b.inf, a.sup / b.sup));
        return roundedInterval(v1, v2);
    }

    /**
     * @param base
     *            1st operand of the power 2 operation
     *
     * @return a new FloatInterval that's the result of the pow operation on an
     *         interval
     */
    public static FloatInterval pow2(FloatInterval base) {
        float v1 = base.inf*base.inf;
        float v2 = base.sup*base.sup;
        if (base.inf > 0.0) {
            return roundedInterval(v1, v2);
        } else if (base.sup < 0.0) {
            return roundedInterval(v2, v1);
        } else { // 0 is in the base interval
            return new FloatInterval(0.0f, Math.max(v1, v2));
        }
    }

    /**
     * @param base
     *            1st operand of the power exponent operation PRE : base.inf >=
     *            0.0
     * @param exponent
     *            2st operand of the operation
     *
     * @return a new FloatInterval that's the result of the pow operation on an
     *         interval
     */
    public static FloatInterval pow(FloatInterval base, float exponent) {
        assert (base.isNaN() || base.inf >= 0.0);
        
        float v1 =  pow(base.inf, exponent);
        float v2 =  pow(base.sup, exponent);
        return roundedInterval(v1, v2);
    }

    /**
     * @param base
     *            1st operand of the power exponent operation PRE : base.inf >=
     *            0.0
     * @param exponent
     *            2st operand of the operation
     *
     * @return a new FloatInterval that's the result of the base^exponent
     *         operation because the pow function is monotone, the result is
     *         easy to compute
     */
    public static FloatInterval pow(FloatInterval base, FloatInterval exponent) {
        assert (base.isNaN() || base.inf >= 0.0);
        float v1 = Math.min(Math.min(pow(base.inf, exponent.inf), pow(base.inf, exponent.sup)), Math.min(pow(base.sup, exponent.inf), pow(base.sup, exponent.sup)));
        float v2 = Math.max(Math.max(pow(base.inf, exponent.inf), pow(base.inf, exponent.sup)), Math.max(pow(base.sup, exponent.inf), pow(base.sup, exponent.sup)));
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            argument for the exponential function
     *
     * @return a new FloatInterval that's the result of the exponential
     *         function
     */
    public static FloatInterval exp(FloatInterval a) {
        float v1 = (float) Math.exp(a.inf);
        float v2 = (float) Math.exp(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            argument for the logarithmic function PRE : param a must be >
     *            0
     *
     * @return a new FloatInterval that's the result of the logarithmic
     *         function (ln)
     */
    public static FloatInterval log(FloatInterval a) {
        if (a.inf < 0) {
            return new FloatInterval(Float.NaN);
        }
        assert (a.isNaN() || a.inf > 0.0);
        float v1 = (float)Math.log(a.inf);
        float v2 = (float)Math.log(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            argument for the logarithmic base 10 function PRE : param a
     *            must be > 0
     *
     * @return a new FloatInterval that's the result of the logarithmic
     *         function
     */
    public static FloatInterval log10(FloatInterval a) {
        if (a.inf < 0) {
            return new FloatInterval(Float.NaN);
        }
        assert (a.isNaN() || a.inf > 0.0);
        float v1 = (float)Math.log10(a.inf);
        float v2 = (float)Math.log10(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the square 2 operation PRE : the interval must be
     *            positive (a.inf >= 0.0)
     *
     * @return a new FloatInterval that's the result of the sqrt operation
     */
    public static FloatInterval sqrt(FloatInterval a) {
        assert (a.isNaN() || a.inf > 0.0);
        float v1 = (float)Math.sqrt(a.inf);
        float v2 = (float)Math.sqrt(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the absolute operation
     *
     * @return a new FloatInterval that's the absolute interval of the operand
     */
    public static FloatInterval abs(FloatInterval a) {
        if (a.contains(0.0f)) {
            float v1 = 0.0f;
            float v2 = Math.max(-a.inf, a.sup);
            return new FloatInterval(v1, v2);
        } else if (a.sup < 0) {
            return new FloatInterval(-a.sup, -a.inf);
        } else { // (a.inf > 0)
            return new FloatInterval(a.inf, a.sup); // Doesn't need rounding, the
                                                     // result is already known
        }
    }

    /**
     * @param a
     *            operand of the negative operation
     *
     * @return a new FloatInterval that's the negative the operand
     */
    public static FloatInterval neg(FloatInterval a) {
        return new FloatInterval(-a.sup, -a.inf);
    }

    /** @return a new FloatInterval that's the result of the cosine operation on a*/
    public static FloatInterval cos(FloatInterval a) {
        if (a.isNaN()) return new FloatInterval(Float.NaN);
        FloatInterval wholeDomain = new FloatInterval(-1,+1);
        if (width(a) >= TWO_PI) return wholeDomain;
        float inf = (a.inf % TWO_PI + TWO_PI) % TWO_PI; // now inf is in [0..2PI[
        float sup = inf + width(a);
        if (inf <= PIF) {
            if (sup >= TWO_PI) return wholeDomain;
            if (sup <= PIF) // monotone decreasing...
                return roundedInterval((float)Math.cos(sup),(float) Math.cos(inf));
            float max=Math.max((float)Math.cos(inf),(float) Math.cos(sup));
            FloatInterval x= roundedInterval(-1, max);
            return new FloatInterval(-1, x.sup); // "unround" x.inf
        }
        if (sup >= TWO_PI+PIF) return wholeDomain;
        if (sup < TWO_PI) // monotone increasing...
            return roundedInterval((float)Math.cos(inf), (float)Math.cos(sup));
        float min=Math.min((float)Math.cos(inf), (float)Math.cos(sup));
        FloatInterval x= roundedInterval(min, +1);
        return new FloatInterval(x.inf, +1); // "unround" x.sup
    }

    /** @return a new FloatInterval that's the result of the sine operation on a*/
    public static FloatInterval sin(FloatInterval a) {
        // using sin(x + pi/2) = cos(x)
        return cos(new FloatInterval(a.inf - HALF_PI, a.sup - HALF_PI));
    }

    /** @return a new FloatInterval that's the result of the tangent operation on a*/
    public static FloatInterval tan(FloatInterval a) {
        // (see [Julmy15], p. 26)
        if (a.isNaN()) 
            return new FloatInterval(Float.NaN);
        FloatInterval wholeDomain = new FloatInterval(Float.NEGATIVE_INFINITY, 
                                                        Float.POSITIVE_INFINITY);
        if (width(a) >= PIF)
            return wholeDomain;
        float inf = (a.inf % PIF + PIF) % PIF; // now inf is in [0..PI[
        float sup = inf + width(a);
        if (inf <= HALF_PI && sup >= HALF_PI)
            return wholeDomain;
        if (sup >= ONE_AND_HALF_PI)
            return wholeDomain;
        float v1 = (float)Math.tan(inf);
        float v2 = (float)Math.tan(sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the sinh operation
     *
     * @return a new FloatInterval that's the negative the operand
     */
    public static FloatInterval sinh(FloatInterval a) {
        float v1 = (float)Math.sinh(a.inf);
        float v2 = (float)Math.sinh(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the cosh operation
     *
     * @return a new FloatInterval that's the negative the operand
     */
    public static FloatInterval cosh(FloatInterval a) {
        float sup = Math.max((float)Math.cosh(a.inf), (float)Math.cosh(a.sup));
        return roundedInterval(0.0f, sup);
    }

    /**
     * @param a
     *            operand of the tanh operation
     *
     * @return a new FloatInterval that's the negative the operand
     */
    public static FloatInterval tanh(FloatInterval a) {
        float v1 = (float)Math.tanh(a.inf);
        float v2 = (float)Math.tanh(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the asin operation PRE : the interval must be in
     *            [-1;1]
     *
     * @return a new FloatInterval that's the negative the operand
     */
    public static FloatInterval asin(FloatInterval a) {
        assert a.isNaN() || (a.inf >= -1.0 && a.sup <= 1.0);
        float v1 = (float)Math.asin(a.inf);
        float v2 = (float)Math.asin(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the acos operation PRE : the interval must be in
     *            [-1;1]
     *
     * @return a new FloatInterval that's the negative the operand
     */
    public static FloatInterval acos(FloatInterval a) {
        assert a.isNaN() || (a.inf >= -1.0 && a.sup <= 1.0);
        float v1 = (float)Math.acos(a.sup);
        float v2 = (float)Math.acos(a.inf);
        return roundedInterval(v1, v2);
    }

    /**
     * @param a
     *            operand of the atan operation
     *
     * @return a new FloatInterval that's the negative the operand
     */
    public static FloatInterval atan(FloatInterval a) {
        float v1 = (float)Math.atan(a.inf);
        float v2 = (float)Math.atan(a.sup);
        return roundedInterval(v1, v2);
    }

    /**
     * Notes : the returned Interval is pessimistic
     * @return a new FloatInterval that's the result of the a%b operation
     */
    public static FloatInterval modulo(FloatInterval a, FloatInterval b) {
        if (a.isNaN() || b.isNaN()) return new FloatInterval(Float.NaN);
        if (b.contains(0.0f))        return new FloatInterval(Float.NaN);

        int q1=(int)(a.inf/b.inf), q2=(int)(a.sup/b.inf);
        int q3=(int)(a.inf/b.sup), q4=(int)(a.sup/b.sup);
        float r1=a.inf%b.inf, r2=a.sup%b.inf;
        float r3=a.inf%b.sup, r4=a.sup%b.sup;
        if(q1==q2 && q1==q3 && q1==q4) { // same quotient
            float rmin=Math.min(r1, Math.min(r2, Math.min(r3,r4)));
            float rmax=Math.max(r1, Math.max(r2, Math.max(r3,r4)));
            return new FloatInterval(rmin, rmax);
        }
        
        float max = Math.max(Math.abs(b.inf), Math.abs(b.sup));
        if (a.contains(0.0f)) {
            return new FloatInterval(-max, max);
        }
        return a.inf > 0 ? new FloatInterval(0.0f, max)
                         : new FloatInterval(-max, 0.0f);
    }

    /**
     * @param inf
     *            inferior bound of the interval
     * @param sup
     *            superior bound of the interval
     *
     * @return a new FloatInterval with the inf minus ulp and the sup bonus ulp
     */
    private static FloatInterval roundedInterval(float inf, float sup) {
        return new FloatInterval(inf - Math.ulp(inf), sup + Math.ulp(sup));
    }

    public static FloatInterval toRadians(FloatInterval a) {
        float v1 = (float)Math.toRadians(a.inf);
        float v2 = (float)Math.toRadians(a.sup);
        return roundedInterval(v1, v2);
    }

    public static FloatInterval toDegrees(FloatInterval a) {
        float v1 = (float)Math.toDegrees(a.inf);
        float v2 = (float)Math.toDegrees(a.sup);
        return roundedInterval(v1, v2);
    }
    private static float pow(float a,float b){
        return (float) Math.pow(a, b);
    }
    /*
     * (Copied from wrapper/intervalDouble)
     * We don't see a clean way to define the relative error when the value is 
     * (very close to) zero, which is yet quite common. Here we use a very
     * dirty trick to handle that. 
     */
    public double relativeError() {
        float inf = this.inf, sup = this.sup;
        boolean containsZero = (inf<=0 && sup >= 0);
        double numerator=sup-inf;
        inf=Math.abs(inf);
        sup=Math.abs(sup);
        double denominator = Math.min(inf, sup);
        if(containsZero && Math.max(inf, sup) < COJAC_STABILITY_THRESHOLD)
            return numerator;  // here we "bet" that the true value is zero...
        if (denominator * COJAC_STABILITY_THRESHOLD < Double.MIN_NORMAL) {
            denominator = Math.max(inf,  sup);
            if (denominator * COJAC_STABILITY_THRESHOLD < Double.MIN_NORMAL)
                return numerator;  // here we "bet" that the true value is zero...
        }
        return numerator / denominator;
    }
}
