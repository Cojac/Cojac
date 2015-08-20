package ch.eiafr.cojac.models.wrappers;

//TODO: (not related to this class...) Maybe expand COJAC as: 
//      "Climbing Over Java Arithmetic Computation" or
//      "Creating Other Judicious Arithmetic Capabilities"

public class DerivationDouble extends Number implements
        Comparable<DerivationDouble> {

    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    private final double value;
    private final double deriv;

    private DerivationDouble(double value, double dValue) {
        this.value = value;
        this.deriv = dValue;
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public DerivationDouble(double v) {
        this.value = v;
        this.deriv = 0.0;
    }

    public DerivationDouble(String v) {
        this.value = Double.parseDouble(v);
        this.deriv = 0.0;
    }

    public DerivationDouble(DerivationFloat v) {
        this.value = v.delegate.value;
        this.deriv = v.delegate.deriv;
    }

    public DerivationDouble(DerivationDouble v) {
        this.value = v.value;
        this.deriv = v.deriv;
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------

    public static DerivationDouble dadd(DerivationDouble a, DerivationDouble b) {
        return new DerivationDouble(a.value + b.value, a.deriv + b.deriv);
    }

    public static DerivationDouble dsub(DerivationDouble a, DerivationDouble b) {
        return new DerivationDouble(a.value - b.value, a.deriv - b.deriv);
    }

    public static DerivationDouble dmul(DerivationDouble a, DerivationDouble b) {
        double value = a.value * b.value;
        double dValue = a.deriv * b.value + a.value * b.deriv;
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble ddiv(DerivationDouble a, DerivationDouble b) {
        double value = a.value / b.value;
        double dValue = (a.deriv * b.value - b.deriv * a.value) /
                (b.value * b.value);
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble drem(DerivationDouble a, DerivationDouble b) {
        double value = a.value % b.value;
        if (b.deriv != 0.0) {
            double dValue = Double.NaN;
            return new DerivationDouble(value, dValue);
        } 
        double dValue = a.deriv;
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble dneg(DerivationDouble a) {
        return new DerivationDouble(-a.value, -a.deriv);
    }

    public static double toDouble(DerivationDouble a) {
        return a.value;
    }

    public static Double toRealDoubleWrapper(DerivationDouble a) {
        return new Double(a.value);
    }

    public static int dcmpl(DerivationDouble a, DerivationDouble b) {
        if (Double.isNaN(a.value)|| Double.isNaN(b.value)) return -1;
        if (a.value < b.value) return -1;
        if (a.value > b.value) return +1;
        return 0;
    }

    public static int dcmpg(DerivationDouble a, DerivationDouble b) {
        if (Double.isNaN(a.value)|| Double.isNaN(b.value)) return +1;
        if (a.value < b.value) return -1;
        if (a.value > b.value) return +1;
        return 0;
    }

    public static int d2i(DerivationDouble a) {
        return (int) a.value;
    }

    public static long d2l(DerivationDouble a) {
        return (long) a.value;
    }

    public static DerivationDouble math_sqrt(DerivationDouble a) {
        double value = Math.sqrt(a.value);
        double dValue = a.deriv / (2.0 * Math.sqrt(a.value));
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_abs(DerivationDouble a) {
        double value = Math.abs(a.value);
        double dValue = a.value < 0.0 ? -a.deriv : a.deriv; //BAP: corrected, was dValue
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_sin(DerivationDouble a) {
        double value = Math.sin(a.value);
        double dValue = Math.cos(a.value) * a.deriv;
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_cos(DerivationDouble a) {
        double value = Math.cos(a.value);
        double dValue = -Math.sin(a.value) * a.deriv;
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_tan(DerivationDouble a) {
        double value = Math.tan(a.value);
        double dValue = a.deriv / (Math.cos(a.value) * Math.cos(a.value));
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_asin(DerivationDouble a) {
        double value = Math.asin(a.value);
        double dValue = a.deriv / (Math.sqrt(1.0 - a.value * a.value));
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_acos(DerivationDouble a) {
        double value = Math.acos(a.value);
        double dValue = -a.deriv / (Math.sqrt(1.0 - a.value * a.value));
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_atan(DerivationDouble a) {
        double value = Math.atan(a.value);
        double dValue = a.deriv / (1.0 + a.value * a.value);
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_sinh(DerivationDouble a) {
        double value = Math.sinh(a.value);
        double dValue = a.deriv * Math.cosh(a.value);
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_cosh(DerivationDouble a) {
        double value = Math.cosh(a.value);
        double dValue = a.deriv * Math.sinh(a.value);
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_tanh(DerivationDouble a) {
        double value = Math.tanh(a.value);
        double dValue = a.deriv / (Math.cosh(a.value) * Math.cosh(a.value));
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_exp(DerivationDouble a) {
        double value = Math.exp(a.value);
        double dValue = a.deriv * Math.exp(a.value);
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_log(DerivationDouble a) {
        double value = Math.log(a.value);
        double dValue = a.deriv / a.value;
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_log10(DerivationDouble a) {
        double value = Math.log10(a.value);
        double dValue = a.deriv / (a.value * Math.log(10.0));
        return new DerivationDouble(value, dValue);
    }
    
    public static DerivationDouble math_toRadians(DerivationDouble a) { 
        return new DerivationDouble(Math.toRadians(a.value), a.deriv);
    }

    public static DerivationDouble math_toDegrees(DerivationDouble a) { 
        return new DerivationDouble(Math.toDegrees(a.value), a.deriv);
    }

    public static DerivationDouble math_min(DerivationDouble a, DerivationDouble b) {
        return (a.value < b.value) ? a : b;
    }

    public static DerivationDouble math_max(DerivationDouble a, DerivationDouble b) {
        return (a.value > b.value) ? a : b;
    }

    public static DerivationDouble math_pow(DerivationDouble a, DerivationDouble b) {
        double value = Math.pow(a.value, b.value);
        double dValue = Math.pow(a.value, b.value) *
                (((b.value * a.deriv) / a.value) + Math.log(a.value) *
                        b.deriv);
        return new DerivationDouble(value, dValue);
    }

    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static DerivationDouble fromDouble(double v) {
        return new DerivationDouble(v);
    }

    public static DerivationDouble fromRealDoubleWrapper(Double v) {
        return fromDouble(v);
    }

    public static DerivationDouble fromString(String v) {
        return new DerivationDouble(v);
    }

    public static DerivationDouble i2d(int a) {
        return new DerivationDouble(a);
    }

    public static DerivationDouble l2d(long a) {
        return new DerivationDouble(a);
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------

    @Override public int compareTo(DerivationDouble o) {
        return Double.compare(this.value, o.value);
    }

    @Override public int hashCode() {
        return Double.hashCode(this.value);
    }

    @Override public boolean equals(Object obj) {
        Double d=null;
        if (obj instanceof Double) d=(Double) obj;
        if (obj instanceof DerivationDouble) 
            d=new Double(((DerivationDouble) obj).value);
        return new Double(this.value).equals(d);
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
        return "AutoDiff";
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(DerivationDouble n) {
        return n.value+" (deriv="+n.deriv+")";
    }

    public static DerivationDouble COJAC_MAGIC_DOUBLE_getDerivation(DerivationDouble a) {
        return new DerivationDouble(a.deriv);
    }

    public static DerivationDouble COJAC_MAGIC_DOUBLE_asDerivationTarget(DerivationDouble a) {
        return new DerivationDouble(a.value, 1.0);
    }

    //-------------------------------------------------------------------------
    //--------------------- Auxiliary methods ---------------------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

}
