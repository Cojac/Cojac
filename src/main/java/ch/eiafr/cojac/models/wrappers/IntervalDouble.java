package ch.eiafr.cojac.models.wrappers;

import ch.eiafr.cojac.interval.DoubleInterval;
import ch.eiafr.cojac.models.Reactions;
import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_STABILITY_THRESHOLD;
import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_CHECK_UNSTABLE_COMPARISONS;

public class IntervalDouble extends Number implements
        Comparable<IntervalDouble> {
    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------
    
//    private static double COJAC_STABILITY_THRESHOLD = 1.0;
//    private static boolean COJAC_CHECK_UNSTABLE_COMPARISONS = false;

    protected final double value;
    protected final DoubleInterval interval;
    
    protected final boolean isUnStable;

    private IntervalDouble(double value, DoubleInterval interval, boolean unstable) {
        this.value = value;
        this.interval = interval;
        this.isUnStable=checkedStability(unstable);
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public IntervalDouble(double value) {
        this(value, new DoubleInterval(value), false);
    }

    public IntervalDouble(String s) {
        this(Double.valueOf(s), new DoubleInterval(Double.valueOf(s)), false);
    }

    public IntervalDouble(IntervalFloat a) {
        this.value = a.delegate.value;
        this.interval = new DoubleInterval(a.delegate.interval);
        this.isUnStable=a.delegate.isUnStable;
    }

    public IntervalDouble(IntervalDouble a) {
        this(a.value, a.interval, a.isUnStable);
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------
    
    public static IntervalDouble dadd(IntervalDouble a, IntervalDouble b) {
        IntervalDouble res = new IntervalDouble(
                a.value + b.value, 
                DoubleInterval.add(a.interval, b.interval), 
                a.isUnStable || b.isUnStable);
        return res;
    }

    public static IntervalDouble dsub(IntervalDouble a, IntervalDouble b) {
        IntervalDouble res = new IntervalDouble(
                a.value - b.value, 
                DoubleInterval.sub(a.interval, b.interval),
                a.isUnStable || b.isUnStable);
        return res;
    }

    public static IntervalDouble dmul(IntervalDouble a, IntervalDouble b) {
        IntervalDouble res = new IntervalDouble(
                a.value * b.value, 
                DoubleInterval.mul(a.interval, b.interval),
                a.isUnStable || b.isUnStable);
        return res;
    }

    public static IntervalDouble ddiv(IntervalDouble a, IntervalDouble b) {
        IntervalDouble res = new IntervalDouble(
                a.value / b.value, 
                DoubleInterval.div(a.interval, b.interval),
                a.isUnStable || b.isUnStable);
        return res;
    }

    public static IntervalDouble drem(IntervalDouble a, IntervalDouble b) {
        IntervalDouble res = new IntervalDouble(
                a.value % b.value, 
                DoubleInterval.modulo(a.interval, b.interval),
                a.isUnStable || b.isUnStable);
        return res;
    }

    // it's just a neg operation, dont need to check the stability...
    public static IntervalDouble dneg(IntervalDouble a) {
        return new IntervalDouble(
                -a.value, 
                DoubleInterval.neg(a.interval),
                a.isUnStable);
    }

    public static double toDouble(IntervalDouble a) {
        return a.value;
    }

    public static Double toRealDoubleWrapper(IntervalDouble a) {
        return new Double(a.value);
    }

    public static int dcmpl(IntervalDouble a, IntervalDouble b) {
        if (Double.isNaN(a.value) || Double.isNaN(b.value)) return -1;
        return a.compareTo(b);
    }

    public static int dcmpg(IntervalDouble a, IntervalDouble b) {
        if (Double.isNaN(a.value) || Double.isNaN(b.value)) return +1;
        return a.compareTo(b);
    }

    public static int d2i(IntervalDouble a) {
        return a.intValue();
    }

    public static long d2l(IntervalDouble a) {
        return a.longValue();
    }

    /* Mathematical function */

    public static IntervalDouble math_sqrt(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.sqrt(a.value), 
                DoubleInterval.sqrt(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_abs(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.abs(a.value), 
                DoubleInterval.abs(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_sin(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.sin(a.value), 
                DoubleInterval.sin(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_cos(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.cos(a.value), 
                DoubleInterval.cos(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_tan(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.tan(a.value), 
                DoubleInterval.tan(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_asin(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.asin(a.value), 
                DoubleInterval.asin(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_acos(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.acos(a.value), 
                DoubleInterval.acos(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_atan(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.atan(a.value), 
                DoubleInterval.atan(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_sinh(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.sinh(a.value), 
                DoubleInterval.sinh(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_cosh(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.cosh(a.value), 
                DoubleInterval.cosh(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_tanh(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.tanh(a.value), 
                DoubleInterval.tanh(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_exp(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.exp(a.value), 
                DoubleInterval.exp(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_log(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.log(a.value), 
                DoubleInterval.log(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_log10(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.log10(a.value), 
                DoubleInterval.log10(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_toRadians(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.toRadians(a.value), 
                DoubleInterval.toRadians(a.interval),
                a.isUnStable);
        return res;
    }
    
    public static IntervalDouble math_toDegrees(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.toDegrees(a.value), 
                DoubleInterval.toDegrees(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_min(IntervalDouble a, IntervalDouble b) {
        if (a.compareTo(b)<0) return a;
        return b;
    }

    public static IntervalDouble math_max(IntervalDouble a, IntervalDouble b) {
        if (a.compareTo(b)>0) return a;
        return b;
    }

    public static IntervalDouble math_pow(IntervalDouble a, IntervalDouble b) {
        IntervalDouble res = new IntervalDouble(
                Math.pow(a.value, b.value), 
                DoubleInterval.pow(a.interval, b.interval),
                a.isUnStable || b.isUnStable);
        return res;
    }

    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static IntervalDouble fromDouble(double a) {
        return new IntervalDouble(a);
    }

    public static IntervalDouble fromRealDoubleWrapper(Double a) {
        return fromDouble(a);
    }

    public static IntervalDouble fromString(String a) {
        double d = Double.parseDouble(a);
        return fromDouble(d);
    }

    public static IntervalDouble i2d(int a) {
        return fromDouble(a);
    }

    public static IntervalDouble l2d(long a) {
        return fromDouble(a);
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------

    @Override public int compareTo(IntervalDouble o) {
        int compResult = this.interval.compareTo(o.interval);
        if (COJAC_CHECK_UNSTABLE_COMPARISONS) {
            if (this.interval.strictlyEquals(o.interval)) return 0;
            if (this.interval.overlaps(o.interval))
                reportBadComparison();
        }
        if (compResult != 0) {
            return compResult;
        }
        if (this.value < o.value) {
            return -1;
        }
        if (this.value > o.value) {
            return 1;
        }
        return 0;
    }

    @Override public boolean equals(Object obj) {
        Double d=null;
        if (obj instanceof Double) d=(Double) obj;
        if (obj instanceof IntervalDouble) 
            d=new Double(((IntervalDouble) obj).value);
        return new Double(this.value).equals(d);
    }
    
    @Override public int hashCode() {
        return Double.hashCode(this.value);
    }
    
    @Override public String toString() {
        return String.format("%s:%s", value, interval);
    }

    @Override public int intValue() {
        return (int) value;
    }

    @Override public long longValue() {
        return (long) value;
    }

    @Override public float floatValue() {
        return (float) value;
    }

    @Override public double doubleValue() {
        return value;
    }

    //-------------------------------------------------------------------------
    //----------------- "Magic" methods ---------------------------------------
    //-------------------------------------------------------------------------

    public static String COJAC_MAGIC_DOUBLE_wrapper() {
        return "Interval";
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(IntervalDouble n) {
        return n.asInternalString();
    }

    public static IntervalDouble COJAC_MAGIC_DOUBLE_width(IntervalDouble a) {
        return new IntervalDouble(DoubleInterval.width(a.interval));
    }

    public static IntervalDouble COJAC_MAGIC_DOUBLE_relativeError(IntervalDouble n) {
        return new IntervalDouble(n.relativeError());
    }
    
    //-------------------------------------------------------------------------
    //--------------------- Auxiliary methods ---------------------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    //TODO consider the number of significant digits instead of the relative error
    
    // used by reflection (from cmd line option)
    public static void setThreshold(double value) {
        COJAC_STABILITY_THRESHOLD = value;
    }

    // used by reflection (from cmd line option)
    public static void setCheckComp(boolean value) {
        COJAC_CHECK_UNSTABLE_COMPARISONS = value;
    }

    public String asInternalString() {
        return String.format("%s:%s", value, interval);
    }

    private boolean checkedStability(boolean wasUnstable) {
        if (wasUnstable) return wasUnstable;
        if (COJAC_STABILITY_THRESHOLD < relativeError()) {
            Reactions.react("BigDecimal wrapper detects unstability... "+asInternalString()+" ");
            return true;
        }
        return false;
    }

    private void reportBadComparison() {
        Reactions.react("BigDecimal wrapper detects dangerous comparison (overlap)... ");
    }

    private double relativeError() {
        double threshold=1E-100; // arbitrarily
        double denominator = Math.min(Math.abs(this.interval.inf), Math.abs(this.interval.sup));
        if (denominator < threshold) {
            denominator = Math.max(Math.abs(this.interval.inf), Math.abs(this.interval.sup));
            if (denominator < threshold) denominator=1.0;  // arbitrarily too
        }
        return DoubleInterval.width(this.interval) / denominator;
    }
}
