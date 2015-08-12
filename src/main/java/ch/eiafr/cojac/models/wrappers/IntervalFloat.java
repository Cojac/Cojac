package ch.eiafr.cojac.models.wrappers;

//This float wrapper is done by delegation to IntervalDouble
public class IntervalFloat extends Number implements Comparable<IntervalFloat> {
    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    protected final IntervalDouble delegate;

    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public IntervalFloat(float v) {
        delegate = new IntervalDouble(v);
    }

    public IntervalFloat(String v) {
        delegate = new IntervalDouble(v);
    }

    public IntervalFloat(IntervalFloat v) {
        delegate = new IntervalDouble(v.delegate);
    }

    public IntervalFloat(IntervalDouble v) {
        delegate = new IntervalDouble(v);
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------

    public static IntervalFloat fadd(IntervalFloat a, IntervalFloat b) {
        return new IntervalFloat(IntervalDouble.dadd(a.delegate, b.delegate));
    }

    public static IntervalFloat fsub(IntervalFloat a, IntervalFloat b) {
        return new IntervalFloat(IntervalDouble.dsub(a.delegate, b.delegate));
    }

    public static IntervalFloat fmul(IntervalFloat a, IntervalFloat b) {
        return new IntervalFloat(IntervalDouble.dmul(a.delegate, b.delegate));
    }

    public static IntervalFloat fdiv(IntervalFloat a, IntervalFloat b) {
        return new IntervalFloat(IntervalDouble.ddiv(a.delegate, b.delegate));
    }

    public static IntervalFloat frem(IntervalFloat a, IntervalFloat b) {
        return new IntervalFloat(IntervalDouble.drem(a.delegate, b.delegate));
    }

    public static IntervalFloat fneg(IntervalFloat a) {
        return new IntervalFloat(IntervalDouble.dneg(a.delegate));
    }

    public static float toFloat(IntervalFloat a) {
        return (float) IntervalDouble.toDouble(a.delegate);
    }

    public static Float toRealFloatWrapper(IntervalFloat a) {
        return toFloat(a);
    }

    public static int fcmpl(IntervalFloat a, IntervalFloat b) {
        return IntervalDouble.dcmpl(a.delegate, b.delegate);
    }

    public static int fcmpg(IntervalFloat a, IntervalFloat b) {
        return IntervalDouble.dcmpg(a.delegate, b.delegate);
    }

    public static IntervalFloat math_abs(IntervalFloat a) {
        return new IntervalFloat((IntervalDouble.math_abs(a.delegate)));
    }

    public static IntervalFloat math_min(IntervalFloat a, IntervalFloat b) {
        return new IntervalFloat((IntervalDouble.math_min(a.delegate, b.delegate)));
    }
    public static IntervalFloat math_max(IntervalFloat a, IntervalFloat b) {
        return new IntervalFloat((IntervalDouble.math_max(a.delegate, b.delegate)));
    }

    public static int f2i(IntervalFloat a) {
        return IntervalDouble.d2i(a.delegate);
    }

    public static long f2l(IntervalFloat a) {
        return IntervalDouble.d2l(a.delegate);
    }

    public static IntervalDouble f2d(IntervalFloat a) {
        return new IntervalDouble(a.delegate);
    }

    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static IntervalFloat fromString(String v) {
        return new IntervalFloat(v);
    }

    public static IntervalFloat fromFloat(float v) {
        return new IntervalFloat(v);
    }

    public static IntervalFloat fromRealFloatWrapper(Float v) {
        return fromFloat(v);
    }
    
    public static IntervalFloat d2f(IntervalDouble a) {
        return new IntervalFloat(a);
    }

    public static IntervalFloat i2f(int a) {
        return new IntervalFloat(IntervalDouble.i2d(a));
    }

    public static IntervalFloat l2f(long a) {
        return new IntervalFloat(IntervalDouble.l2d(a));
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------

    @Override public int compareTo(IntervalFloat o) {
        return delegate.compareTo(o.delegate);
    }

    @Override public boolean equals(Object obj) {
        if (obj==null || !(obj instanceof IntervalFloat)) return false;
        return delegate.equals(((IntervalFloat)obj).delegate);
    }
    
    @Override public int hashCode() {
        return delegate.hashCode();
    }
    
    @Override public String toString() {
        return delegate.toString();
    }
    
    @Override public int intValue() {
        return delegate.intValue();
    }

    @Override public long longValue() {
        return delegate.longValue();
    }

    @Override public float floatValue() {
        return delegate.floatValue();
    }

    @Override public double doubleValue() {
        return delegate.doubleValue();
    }
    
    //-------------------------------------------------------------------------
    //----------------- "Magic" methods ---------------------------------------
    //-------------------------------------------------------------------------

    public static String COJAC_MAGIC_FLOAT_wrapper() {
        return "Interval";
    }

    public static String COJAC_MAGIC_FLOAT_toStr(IntervalFloat n) {
        return IntervalDouble.COJAC_MAGIC_DOUBLE_toStr(n.delegate);
    }
    
    //TODO: add magic methods from delegate
}
