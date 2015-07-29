package ch.eiafr.cojac.models.wrappers;

import ch.eiafr.cojac.interval.DoubleInterval;

public class IntervalDouble extends Number implements
        Comparable<IntervalDouble> {
    
    private static double threshold = 1.0;
    private static boolean checkComparisons = false;

    protected final double value;
    protected final DoubleInterval interval;
    
    protected final boolean isUnStable;

    private IntervalDouble(double value) {
        this(value, new DoubleInterval(value), false);
    }

    private IntervalDouble(double value, DoubleInterval interval, boolean unstable) {
        this.value = value;
        this.interval = interval;
        this.isUnStable=checkedStability(unstable);
    }

    IntervalDouble(IntervalFloat a) {
        this.value = a.value;
        this.interval = new DoubleInterval(a.interval);
        this.isUnStable=a.isUnStable;
    }

    public static IntervalDouble fromDouble(double a) {
        return new IntervalDouble(a);
    }

    public static IntervalDouble fromString(String a) {
        double d = Double.parseDouble(a);
        return fromDouble(d);
    }

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

    // Todo : is this correct ?
    public static int dcmpl(IntervalDouble a, IntervalDouble b) {
        return a.compareTo(b);
    }

    public static int dcmpg(IntervalDouble a, IntervalDouble b) {
        return a.compareTo(b);
    }

    // it's just a neg operation, dont need to check the stability...
    public static IntervalDouble dneg(IntervalDouble a) {
        return new IntervalDouble(
                -a.value, 
                DoubleInterval.neg(a.interval),
                a.isUnStable);
    }

    public static long d2l(IntervalDouble a) {
        return a.longValue();
    }

    public static int d2i(IntervalDouble a) {
        return a.intValue();
    }

    public static IntervalFloat d2f(IntervalDouble a) {
        return new IntervalFloat(a);
    }

    public static IntervalDouble i2d(int a) {
        return fromDouble(a);
    }

    public static IntervalDouble f2d(IntervalFloat a) {
        return new IntervalDouble(a);
    }

    public static IntervalDouble l2d(long a) {
        return fromDouble(a);
    }

    public static double toDouble(IntervalDouble a) {
        return a.value;
    }

    public static Double toRealDouble(IntervalDouble a) {
        return new Double(a.value);
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(IntervalDouble n) {
        return n.toString();
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(IntervalFloat n) {
        return n.toString();
    }

    public static boolean COJAC_MAGIC_DOUBLE_isIn(IntervalDouble n) {
        return n.interval.contains(n.value);
    }

    public static int COJAC_MAGIC_DOUBLE_IntervalcompareTo(IntervalDouble a, IntervalDouble b) {
        return a.compareTo(b);
    }

    public static IntervalDouble COJAC_MAGIC_DOUBLE_width(IntervalDouble a) {
        return new IntervalDouble(DoubleInterval.width(a.interval));
    }

    public static IntervalDouble COJAC_MAGIC_DOUBLE_relativeError(IntervalDouble n) {
        return new IntervalDouble(n.relativeError());
    }

    public static IntervalDouble math_sqrt(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.sqrt(a.value), 
                DoubleInterval.sqrt(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_pow(IntervalDouble a, IntervalDouble b) {
        IntervalDouble res = new IntervalDouble(
                Math.pow(a.value, b.value), 
                DoubleInterval.pow(a.interval, b.interval),
                a.isUnStable || b.isUnStable);
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

    /* Magic Method */

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

    /* Mathematical function */

    public static IntervalDouble math_asin(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.asin(a.value), 
                DoubleInterval.asin(a.interval),
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

    public static IntervalDouble math_abs(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                Math.abs(a.value), 
                DoubleInterval.abs(a.interval),
                a.isUnStable);
        return res;
    }

    public static IntervalDouble math_neg(IntervalDouble a) {
        IntervalDouble res = new IntervalDouble(
                -a.value, 
                DoubleInterval.neg(a.interval),
                a.isUnStable);
        return res;
    }

    @SuppressWarnings("unused")
    public static void setThreshold(double value) {
        threshold = value;
    }

    @SuppressWarnings("unused")
    public static void setCheckComp(boolean value) {
        checkComparisons = value;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", value, interval);
    }

    @Override
    public int compareTo(IntervalDouble o) {
        int compResult = this.interval.compareTo(o.interval);
        if (checkComparisons) {
            checkComp(compResult);
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

    private boolean checkedStability(boolean wasUnstable) {
        if (wasUnstable) return wasUnstable;
        if (threshold < relativeError()) {
            CojacStabilityException e = new CojacStabilityException();
            System.err.println("Cojac has destected a unstable operation :");
            e.printStackTrace(System.err);
            System.err.println("interval value :" + this.toString());
            System.err.println("relative error :" + relativeError());
            return true;
        }
        return false;
    }

    private void checkComp(int compResult) {
        if (compResult == 0) {
            CojacStabilityComparaisonException e = new CojacStabilityComparaisonException();
            System.err.println("Cojac has detected an unstable comparison :");
            e.printStackTrace(System.err);
        }
    }

    private double relativeError() {
        double numerator = Math.min(Math.abs(this.interval.inf), Math.abs(this.interval.sup));
        if (numerator == 0.0) {
            return Double.NaN;
        }
        return DoubleInterval.width(this.interval) / numerator;
    }
}
