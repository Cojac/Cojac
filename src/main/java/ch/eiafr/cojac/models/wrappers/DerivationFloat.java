package ch.eiafr.cojac.models.wrappers;

public class DerivationFloat extends Number implements
        Comparable<DerivationFloat> {
    
    protected final DerivationDouble delegateDoubleWrapper;

    public DerivationFloat(float v) {
        delegateDoubleWrapper = new DerivationDouble((double) v);
    }

    public DerivationFloat(String v) {
        delegateDoubleWrapper = new DerivationDouble(v);
    }

    public DerivationFloat(DerivationFloat v) {
        delegateDoubleWrapper = new DerivationDouble(v.delegateDoubleWrapper);
    }

    public DerivationFloat(DerivationDouble v) {
        delegateDoubleWrapper = new DerivationDouble(v);
    }

    public DerivationFloat(double v) {
        this.delegateDoubleWrapper = new DerivationDouble(v);
    }

    public static DerivationFloat fromDouble(double v) {
        return new DerivationFloat(new DerivationDouble(v));
    }

    public static DerivationFloat fromString(String v) {
        return new DerivationFloat(v);
    }

    public static DerivationFloat fromFloat(float v) {
        return new DerivationFloat(v);
    }

    public static DerivationFloat fadd(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat(DerivationDouble.dadd(a.delegateDoubleWrapper, b.delegateDoubleWrapper));
    }

    public static DerivationFloat fdiv(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat(DerivationDouble.ddiv(a.delegateDoubleWrapper, b.delegateDoubleWrapper));
    }

    public static DerivationFloat frem(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat(DerivationDouble.drem(a.delegateDoubleWrapper, b.delegateDoubleWrapper));
    }

    public static DerivationFloat fsub(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat(DerivationDouble.dsub(a.delegateDoubleWrapper, b.delegateDoubleWrapper));
    }

    public static DerivationFloat fmul(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat(DerivationDouble.dmul(a.delegateDoubleWrapper, b.delegateDoubleWrapper));
    }

    public static int fcmpl(DerivationFloat a, DerivationFloat b) {
        return a.compareTo(b);
    }

    public static int fcmpg(DerivationFloat a, DerivationFloat b) {
        return a.compareTo(b);
    }

    public static DerivationFloat fneg(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.dneg(a.delegateDoubleWrapper));
    }

    public static int f2i(DerivationFloat a) {
        return (int) a.delegateDoubleWrapper.value;
    }

    public static long f2l(DerivationFloat a) {
        return (long) a.delegateDoubleWrapper.value;
    }

    public static DerivationDouble f2d(DerivationFloat a) {
        return new DerivationDouble((float) a.delegateDoubleWrapper.value);
    }

    public static DerivationFloat i2f(int a) {
        return new DerivationFloat((float) a);
    }

    public static DerivationFloat l2f(long a) {
        return new DerivationFloat((float) a);
    }

    public static DerivationFloat d2f(DerivationDouble a) {
        return new DerivationFloat(a.floatValue());
    }

    public static float toFloat(DerivationFloat a) {
        return (float) a.delegateDoubleWrapper.value;
    }

    public static Float toRealFloat(DerivationFloat a) {
        return new Float(a.delegateDoubleWrapper.value);
    }

    public static DerivationDouble COJAC_MAGIC_FLOAT_getDerivation(DerivationFloat a) {
        return new DerivationDouble(a.delegateDoubleWrapper.deriv);
    }

    public static DerivationDouble COJAC_MAGIC_FLOAT_specifieToDerivate(DerivationFloat a) {
        return DerivationDouble.COJAC_MAGIC_DOUBLE_specifieToDerivate(a.delegateDoubleWrapper);
    }

    public static DerivationFloat math_abs(DerivationFloat a) {
        return new DerivationFloat((DerivationDouble.math_abs(a.delegateDoubleWrapper)));
    }

    public static DerivationFloat math_min(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat((DerivationDouble.math_min(a.delegateDoubleWrapper, b.delegateDoubleWrapper)));
    }
    public static DerivationFloat math_max(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat((DerivationDouble.math_max(a.delegateDoubleWrapper, b.delegateDoubleWrapper)));
    }

    //TODO: remove those sqrt/pow/sin... functions on floats (to be checked)
    public static DerivationFloat math_sqrt(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_sqrt(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_pow(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat(DerivationDouble.math_pow(a.delegateDoubleWrapper, b.delegateDoubleWrapper));
    }

    public static DerivationFloat math_sin(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_sin(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_cos(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_cos(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_tan(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_tan(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_sinh(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_sinh(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_cosh(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_cosh(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_tanh(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_tanh(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_acos(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_acos(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_atan(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_atan(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_asin(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_asin(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_exp(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_exp(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_log(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_log(a.delegateDoubleWrapper));
    }

    public static DerivationFloat math_log10(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.math_log10(a.delegateDoubleWrapper));
    }

    @Override
    public int compareTo(DerivationFloat o) {
        return delegateDoubleWrapper.compareTo(o.delegateDoubleWrapper);
    }

    @Override
    public int intValue() {
        return (int) delegateDoubleWrapper.value;
    }

    @Override
    public long longValue() {
        return (long) delegateDoubleWrapper.value;
    }

    @Override
    public float floatValue() {
        return (float) delegateDoubleWrapper.value;
    }

    @Override
    public double doubleValue() {
        return delegateDoubleWrapper.value;
    }
}
