package ch.eiafr.cojac.models.wrappers;

import java.util.Arrays;
import java.util.Random;
import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_STABILITY_THRESHOLD;
import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_CHECK_UNSTABLE_COMPARISONS;

import ch.eiafr.cojac.models.Reactions;

public class StochasticFloat extends Number implements
        Comparable<StochasticFloat> {
    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    private final static int nbrParallelNumber = 3;  // was originally tunable as an option
    //private static float threshold = 0.1F;
    //private static boolean checkComparisons = false; 

    private final static double Tb = 4.303; // see chenaux 1988
    private final static Random random = new Random();
    
    protected final float value;
    protected final float[] stochasticValue;
    protected final boolean isUnStable;

    private StochasticFloat(float v, float[] tab, boolean unstable) {
        this.value = v;
        this.stochasticValue=tab;
        this.isUnStable=checkedStability(unstable);
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public StochasticFloat(float v) {
        this.value = v;
        this.stochasticValue = new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++) {
            this.stochasticValue[i] = v;
        }
        isUnStable=false;
    }

    public StochasticFloat(String v) {
        this(Float.parseFloat(v));
    }

    public StochasticFloat(StochasticDouble v) {
        this.value = (float) v.value;
        this.stochasticValue = new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++) {
            this.stochasticValue[i] = (float) v.stochasticValue[i];
        }
        isUnStable=false;
    }

    public StochasticFloat(StochasticFloat v) {
        this.value = v.value;
        this.stochasticValue = new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++) {
            this.stochasticValue[i] = v.stochasticValue[i];
        }
        isUnStable=false;
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------

    public static StochasticFloat fadd(StochasticFloat a, StochasticFloat b) {
        float[] t=new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(a.stochasticValue[i] + b.stochasticValue[i]);
        StochasticFloat res = new StochasticFloat(a.value + b.value, t, a.isUnStable || b.isUnStable);
        return res;
    }

    public static StochasticFloat fsub(StochasticFloat a, StochasticFloat b) {
        float[] t=new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(a.stochasticValue[i] - b.stochasticValue[i]);
        StochasticFloat res = new StochasticFloat(a.value - b.value, t, a.isUnStable || b.isUnStable);
        return res;
    }

    public static StochasticFloat fmul(StochasticFloat a, StochasticFloat b) {
        float[] t=new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(a.stochasticValue[i] * b.stochasticValue[i]);
        StochasticFloat res = new StochasticFloat(a.value * b.value, t, a.isUnStable || b.isUnStable);
        return res;
    }

    public static StochasticFloat fdiv(StochasticFloat a, StochasticFloat b) {
        float[] t=new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(a.stochasticValue[i] / b.stochasticValue[i]);
        StochasticFloat res = new StochasticFloat(a.value / b.value, t, a.isUnStable || b.isUnStable);
        return res;
    }

    public static StochasticFloat frem(StochasticFloat a, StochasticFloat b) {
        float[] t=new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(a.stochasticValue[i] % b.stochasticValue[i]);
        StochasticFloat res = new StochasticFloat(a.value % b.value, t, a.isUnStable || b.isUnStable);
        return res;
    }

    public static StochasticFloat fneg(StochasticFloat a) {
        float[] t=new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(-a.stochasticValue[i]);
        StochasticFloat res = new StochasticFloat(-a.value, t, a.isUnStable);
        return res;
    }
    
    public static float toFloat(StochasticFloat a) {
        return a.value;
    }

    public static Float toRealFloatWrapper(StochasticFloat a) {
        return new Float(a.value);
    }

    public static int fcmpl(StochasticFloat a, StochasticFloat b) {
        if (Float.isNaN(a.value)|| Float.isNaN(b.value)) return -1;
        return a.compareTo(b);
    }

    public static int fcmpg(StochasticFloat a, StochasticFloat b) {
        if (Float.isNaN(a.value)|| Float.isNaN(b.value)) return +1;
        return a.compareTo(b);
    }

    public static StochasticFloat math_abs(StochasticFloat a) {
        return a.value>=0 ? a: fneg(a);
    }

    public static StochasticFloat math_min(StochasticFloat a, StochasticFloat b) {
        return a.value <= b.value ? a: b;
    }

    public static StochasticFloat math_max(StochasticFloat a, StochasticFloat b) {
        return a.value >= b.value ? a: b;
    }

    public static int f2i(StochasticFloat a) {
        return a.intValue();
    }

    public static long f2l(StochasticFloat a) {
        return a.longValue();
    }

    public static StochasticDouble f2d(StochasticFloat a) {
        return new StochasticDouble(a.doubleValue());
    }

    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------
    
    public static StochasticFloat fromString(String v) {
        return new StochasticFloat(v);
    }

    public static StochasticFloat fromFloat(float v) {
        return new StochasticFloat(v);
    }

    public static StochasticFloat fromRealFloatWrapper(Float v) {
        return fromFloat(v);
    }

    public static StochasticFloat d2f(StochasticDouble a) {
        return new StochasticFloat(a);
    }

    public static StochasticFloat i2f(int a) {
        return new StochasticFloat(a);
    }

    public static StochasticFloat l2f(long a) {
        return new StochasticFloat(a);
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------

    @Override public int compareTo(StochasticFloat o) {
        if (COJAC_CHECK_UNSTABLE_COMPARISONS) {
            if (value==o.value && Arrays.equals(stochasticValue, o.stochasticValue)) return 0;
            if (this.overlaps(o))
                reportBadComparison();
        }
        if (this.value > o.value)  return +1;
        if (this.value < o.value)  return -1;
        return 0;
    }

    @Override public boolean equals(Object obj) {
        Float d=null;
        if (obj instanceof Double) d=(Float) obj;
        if (obj instanceof StochasticFloat) 
            d=new Float(((StochasticFloat) obj).value);
        return new Float(this.value).equals(d);
//      return new Float(this.val).equals(obj);
//        return (obj instanceof BasicFloat) && (((BasicFloat)obj).val == val);
    }

    @Override public int hashCode() {
        return Float.hashCode(this.value);
    }

    @Override public String toString() {
        // reference : BigDecimalDouble.java
        String res = "" + value + " : [%s]";
        String tmp = "";
        for (int i = 0; i < nbrParallelNumber; i++) {
            tmp += ("" + stochasticValue[i]);
            if (i == nbrParallelNumber - 1)
                break;
            tmp += ";";
        }
        return String.format(res, tmp);
    }

    @Override public int intValue() {
        return (int) this.value;
    }

    @Override public long longValue() {
        return (long) this.value;
    }

    @Override public float floatValue() {
        return this.value;
    }

    @Override public double doubleValue() {
        return this.value;
    }

    //-------------------------------------------------------------------------
    //----------------- "Magic" methods ---------------------------------------
    //-------------------------------------------------------------------------

    public static String COJAC_MAGIC_FLOAT_wrapper() {
        return "Stochastic";
    }

    public static String COJAC_MAGIC_FLOAT_toStr(StochasticFloat n) {
        return n.toString();
    }

    public static StochasticFloat COJAC_MAGIC_FLOAT_relativeError(StochasticFloat n) {
        return new StochasticFloat(n.relativeError());
    }

    //-------------------------------------------------------------------------
    //--------------------- Auxiliary methods ---------------------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

//    // Todo : maybe use float in parameter ?
//    public static void setThreshold(double value) {
//        threshold = (float) value;
//    }
//
//    public static void setNbrParallelNumber(int value) {
//        nbrParallelNumber = value;
//    }

    private static float rndRound(float value) {
        switch (random.nextInt(3)) {
        case 0:
            // round to negative infinity
            return value - Math.ulp(value);
        case 1:
            // round to positive infinity
            return value + Math.ulp(value);
        default:
            // default rounding mode in Java
            return value;
        }
    }

    public String asInternalString() {
        String res = "" + value + " : [%s]";
        String tmp = "";
        for (int i = 0; i < nbrParallelNumber; i++) {
            tmp += ("" + stochasticValue[i]);
            if (i == nbrParallelNumber - 1)
                break;
            tmp += ";";
        }
        return String.format(res, tmp);
    }
    
    private boolean checkedStability(boolean wasUnstable) {
        if (wasUnstable) return wasUnstable;
        if (COJAC_STABILITY_THRESHOLD < relativeError()) {
            Reactions.react("Stochastic wrapper detects unstability... "+asInternalString()+" ");
            return true;
        }
        return false;
    }

    private boolean overlaps(StochasticFloat o) {
        float min=value, max=value, omin=o.value, omax=o.value;
        for(float f:stochasticValue) {
            if(f<min) min=f;
            if(f>max) max=f;
        }
        for(float f:o.stochasticValue) {
            if(f<omin) omin=f;
            if(f>omax) omax=f;
        }
        return Math.max(min, omin) <= Math.min(max, omax);
    }


    // compute the relative error as the value divide by the mean of all the
    // distance from value to the stochasticValue's values
    private float relativeError() {
        /*
         * if (this.isNan) { return Float.NaN; } float numerator = 0.0F; float
         * tmp; for (int i = 0; i < nbrParallelNumber; i++) { tmp = value -
         * this.stochasticValue[i]; numerator += (tmp >= 0.0F ? tmp : -tmp); }
         * numerator = numerator / (float) nbrParallelNumber; return numerator /
         * value;
         */

        float mean = 0.0F;
        for (int i = 0; i < nbrParallelNumber; i++) {
            mean += this.stochasticValue[i];
        }
        mean = mean / nbrParallelNumber;

        double squareSum = 0.0;
        for (int i = 0; i < nbrParallelNumber; i++) {
            float delta = this.stochasticValue[i] - mean;
            squareSum += delta*delta;
        }

        double sigmaSquare = (1.0 / (nbrParallelNumber - 1)) *
                squareSum;

        double Cr = (Math.sqrt(nbrParallelNumber) * Math.abs(mean)) /
                (Math.sqrt(sigmaSquare) * Tb);
        return (float) Math.pow(10.0, -Cr);
    }
    
    private void reportBadComparison() {
        Reactions.react("Stochastic wrapper detects dangerous comparison (overlap)... ");
    }

}
