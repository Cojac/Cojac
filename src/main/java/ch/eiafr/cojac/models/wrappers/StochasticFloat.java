package ch.eiafr.cojac.models.wrappers;

import java.util.Random;

public class StochasticFloat extends Number implements
        Comparable<StochasticFloat> {
    private static int nbrParallelNumber = 3;
    private static float threshold = 0.1F;
    private double Tb = 4.303; // see chenaux 1988
    private static Random r = new Random();
    
    protected final float value;
    protected final float[] stochasticValue;
    protected boolean isUnStable = false;

    public StochasticFloat(float v) {
        this.value = v;
        this.stochasticValue = new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++) {
            this.stochasticValue[i] = v;
        }
        isUnStable=false;
    }

    public StochasticFloat(float v, float[] tab, boolean unstable) {
        this.value = v;
        this.stochasticValue=tab;
        this.isUnStable=checkedStability(unstable);
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
    }

    public StochasticFloat(StochasticFloat v) {
        this.value = v.value;
        this.stochasticValue = new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++) {
            this.stochasticValue[i] = v.stochasticValue[i];
        }
    }

    private boolean checkedStability(boolean wasUnstable) {
        if (wasUnstable) return wasUnstable;
        if (threshold < relativeError()) {
            RuntimeException e = new RuntimeException("COJAC: the computation becomes unstable (the interval grows too much)"
                    +"(relative error: "+relativeError()+")");
            // TODO: reconsider this reporting mechanism (merge with the original Cojac
            // mechanisms (console, logfile, exception, callback)
            System.err.println("Cojac has destected a unstable operation :");
            e.printStackTrace(System.err);
            System.err.println("interval value :" + this.toString());
            System.err.println("relative error :" + relativeError());
            return true;
        }
        return false;
    }

    
    public static StochasticFloat fromDouble(double v) {
        return new StochasticFloat((float) v);
    }

    public static StochasticFloat fromString(String v) {
        return new StochasticFloat(v);
    }

    public static StochasticFloat fromFloat(float v) {
        return new StochasticFloat(v);
    }

    public static StochasticFloat fadd(StochasticFloat a, StochasticFloat b) {
        float[] t=new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(a.stochasticValue[i] + b.stochasticValue[i]);
        StochasticFloat res = new StochasticFloat(a.value + b.value, t, a.isUnStable || b.isUnStable);
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

    public static int fcmpl(StochasticFloat a, StochasticFloat b) {
        return a.compareTo(b);
    }

    public static int fcmpg(StochasticFloat a, StochasticFloat b) {
        return a.compareTo(b);
    }

    public static StochasticFloat fneg(StochasticFloat a) {
        float[] t=new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(-a.stochasticValue[i]);
        StochasticFloat res = new StochasticFloat(-a.value, t, a.isUnStable);
        return res;
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

    public static StochasticFloat i2f(int a) {
        return new StochasticFloat((float) a);
    }

    public static StochasticFloat l2f(long a) {
        return new StochasticFloat((float) a);
    }

    public static StochasticFloat d2f(StochasticDouble a) {
        return new StochasticFloat(a);
    }

    public static float toFloat(StochasticFloat a) {
        return a.value;
    }

    public static Float toRealFloat(StochasticFloat a) {
        return new Float(a.value);
    }

    // Todo : maybe use float in parameter ?
    @SuppressWarnings("unused")
    public static void setThreshold(double value) {
        threshold = (float) value;
    }

    @SuppressWarnings("unused")
    public static void setNbrParallelNumber(int value) {
        nbrParallelNumber = value;
    }

    private static float rndRound(float value) {
        switch (r.nextInt(3)) {
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

    public static String COJAC_MAGIC_FLOAT_toStr(StochasticFloat n) {
        return n.toString();
    }

    public static StochasticFloat COJAC_MAGIC_FLOAT_relativeError(StochasticFloat n) {
        return new StochasticFloat(n.relativeError());
    }

    public static float toFloat(IntervalFloat a) {
        return (float) a.value;
    }

    @Override
    public int compareTo(StochasticFloat o) {
        if (this.value > o.value) {
            return 1;
        }
        if (this.value < o.value) {
            return -1;
        }
        return 0;
    }

    @Override
    public int intValue() {
        return (int) this.value;
    }

    @Override
    public long longValue() {
        return (long) this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
        return (double) this.value;
    }

    @Override
    public String toString() {
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

    private void checkStability() {
        if (isUnStable) {
            return;
        }
        if (Math.abs(threshold) < relativeError()) {
            RuntimeException e = new RuntimeException();
            System.err.println("Cojac has destected a unstable operation");
            System.err.println("Relative error is : " + relativeError());
            System.err.println(this.toString());
            e.printStackTrace(System.err);
            this.isUnStable = true;
        }
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
        mean = mean / (float) nbrParallelNumber;

        double squareSum = 0.0;
        for (int i = 0; i < nbrParallelNumber; i++) {
            float delta = this.stochasticValue[i] - mean;
            squareSum += delta*delta;
        }

        double sigmaSquare = (1.0 / (double) (nbrParallelNumber - 1)) *
                squareSum;

        double Cr = (Math.sqrt((double) nbrParallelNumber) * Math.abs(mean)) /
                (Math.sqrt(sigmaSquare) * Tb);
        return (float) Math.pow(10.0, -Cr);
    }
}
