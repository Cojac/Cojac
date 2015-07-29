package ch.eiafr.cojac.models.wrappers;

import java.util.Random;

public class StochasticDouble extends Number implements
        Comparable<StochasticDouble> {
    private static int nbrParallelNumber = 3;
    private static double threshold = 0.1;
    private static boolean checkComparisons = false; //TODO: activate that feature

    private final static Random r = new Random();
    
    private final static double Tb = 4.303; // see chenaux 1988

    protected final double value;
    protected final double[] stochasticValue;
    protected final boolean isUnStable;

    /* Constructor */

    StochasticDouble(double v) {
        this.value = v;
        this.stochasticValue = new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++) {
            stochasticValue[i] = v;
        }
        this.isUnStable=false;
    }
    
    private StochasticDouble(double v, double[] tab, boolean unstable) {
        this.value = v;
        this.stochasticValue=tab;
        this.isUnStable=checkedStability(unstable);
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

    private StochasticDouble(String v) {
        this(Double.parseDouble(v));
    }

    StochasticDouble(StochasticDouble v) {
        double value = v.value;
        this.value = value;
        this.isUnStable = v.isUnStable;
        this.stochasticValue = v.stochasticValue.clone();
    }

    StochasticDouble(StochasticFloat v) {
        double value = v.value;
        this.value = value;
        this.isUnStable = v.isUnStable;
        this.stochasticValue = new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++) {
            stochasticValue[i] = v.stochasticValue[i];
        }
    }

    public static StochasticDouble fromDouble(double v) {
        return new StochasticDouble(v);
    }

    public static StochasticDouble fromString(String v) {
        return new StochasticDouble(v);
    }

    public static StochasticDouble dadd(StochasticDouble a, StochasticDouble b) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(a.stochasticValue[i] + b.stochasticValue[i]);
        StochasticDouble res = new StochasticDouble(a.value + b.value, t, a.isUnStable || b.isUnStable);
        return res;
    }

    public static StochasticDouble ddiv(StochasticDouble a, StochasticDouble b) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(a.stochasticValue[i] / b.stochasticValue[i]);
        StochasticDouble res = new StochasticDouble(a.value / b.value, t, a.isUnStable || b.isUnStable);
        return res;
    }

    public static StochasticDouble drem(StochasticDouble a, StochasticDouble b) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(a.stochasticValue[i] % b.stochasticValue[i]);
        StochasticDouble res = new StochasticDouble(a.value % b.value, t, a.isUnStable || b.isUnStable);
        return res;
  }

    public static StochasticDouble dsub(StochasticDouble a, StochasticDouble b) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(a.stochasticValue[i] - b.stochasticValue[i]);
        StochasticDouble res = new StochasticDouble(a.value - b.value, t, a.isUnStable || b.isUnStable);
        return res;
    }

    public static StochasticDouble dmul(StochasticDouble a, StochasticDouble b) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(a.stochasticValue[i] * b.stochasticValue[i]);
        StochasticDouble res = new StochasticDouble(a.value * b.value, t, a.isUnStable || b.isUnStable);
        return res;
    }

    public static int dcmpl(StochasticDouble a, StochasticDouble b) {
        return a.compareTo(b);
    }

    public static int dcmpg(StochasticDouble a, StochasticDouble b) {
        return a.compareTo(b);
    }

    public static StochasticDouble dneg(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(-a.stochasticValue[i]);
        StochasticDouble res = new StochasticDouble(-a.value, t, a.isUnStable);
        return res;
    }

    public static int d2i(StochasticDouble a) {
        return a.intValue();
    }

    public static long d2l(StochasticDouble a) {
        return a.longValue();
    }

    public static StochasticFloat d2f(StochasticDouble a) {
        return new StochasticFloat(a);
    }

    public static StochasticDouble i2d(int a) {
        return new StochasticDouble(a);
    }

    public static StochasticDouble l2d(long a) {
        return new StochasticDouble(a);
    }

    public static StochasticDouble f2d(StochasticFloat a) {
        return new StochasticDouble(a);
    }

    public static double toDouble(StochasticDouble a) {
        return a.value;
    }

    public static Double toRealDouble(StochasticDouble a) {
        return new Double(a.value);
    }

    // TODO remove "round to zero"
    private static double rndRound(double value) {
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

    /* Mathematical function */
    public static StochasticDouble math_sqrt(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.sqrt(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.sqrt(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_pow(StochasticDouble a, StochasticDouble b) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.pow(a.stochasticValue[i], b.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(
                Math.pow(a.value, b.value), t, a.isUnStable||b.isUnStable);
        return res;
    }

    public static StochasticDouble math_sin(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.sin(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.sin(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_cos(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.cos(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.cos(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_tan(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.tan(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.tan(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_sinh(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.sinh(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.sinh(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_cosh(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.cosh(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.cosh(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_tanh(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.tanh(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.tanh(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_acos(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.acos(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.acos(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_atan(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.atan(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.atan(a.value), t, a.isUnStable);
        return res;
   }

    public static StochasticDouble math_asin(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.asin(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.asin(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_exp(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.exp(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.exp(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_log(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.log(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.log(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_log10(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.log10(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.log10(a.value), t, a.isUnStable);
        return res;
    }

    public static StochasticDouble math_abs(StochasticDouble a) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.abs(a.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(Math.abs(a.value), t, a.isUnStable);
        return res;
    }
    
    public static StochasticDouble math_min(StochasticDouble a, StochasticDouble b) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.min(a.stochasticValue[i], b.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(
                Math.min(a.value, b.value), t, a.isUnStable||b.isUnStable);
        return res;
    }

    public static StochasticDouble math_max(StochasticDouble a, StochasticDouble b) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(Math.max(a.stochasticValue[i], b.stochasticValue[i]));
        StochasticDouble res = new StochasticDouble(
                Math.max(a.value, b.value), t, a.isUnStable||b.isUnStable);
        return res;
    }


    @SuppressWarnings("unused") // used by reflection (from cmd line option)
    public static void setThreshold(double value) {
        threshold = value;
    }

    @SuppressWarnings("unused") // used by reflection (from cmd line option)
    public static void setNbrParallelNumber(int value) {
        nbrParallelNumber = value;
    }

    /* Magic method */
    public static String COJAC_MAGIC_DOUBLE_toStr(StochasticDouble n) {
        return n.toString();
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(StochasticFloat n) {
        return n.toString();
    }

    public static StochasticDouble COJAC_MAGIC_DOUBLE_relativeError(StochasticDouble n) {
        return new StochasticDouble(n.relativeError());
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

    @Override
    public int compareTo(StochasticDouble o) {
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
        return (float) this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    // TODO: check whether it is a "relativeError", or the "numberOfStableDigits"
    // compute the relative error as the value divide by the mean of all the
    // distance from value to the stochasticValue's values
    /*
     * private double relativeError() { if (this.isNan) { return Double.NaN; }
     * 
     * double numerator = 0.0; for (int i = 0; i < nbrParallelNumber; i++) {
     * numerator += (Math.abs(value - this.stochasticValue[i])); } numerator =
     * numerator / (double) nbrParallelNumber; return numerator / value; }
     */

    private double relativeError() {
        double mean = 0.0F;
        for (int i = 0; i < nbrParallelNumber; i++) {
            mean += this.stochasticValue[i];
        }
        mean = mean / nbrParallelNumber;

        double squareSum = 0.0;
        for (int i = 0; i < nbrParallelNumber; i++) {
            double delta = this.stochasticValue[i] - mean;
            squareSum += delta*delta;
        }

        double sigmaSquare = (1.0 / (nbrParallelNumber - 1)) *
                squareSum;

        double Cr = (Math.sqrt(nbrParallelNumber) * Math.abs(mean)) /
                (Math.sqrt(sigmaSquare) * Tb);
        return Math.pow(10.0, -Cr);
    }
    
    private boolean overlaps(StochasticFloat o) {
        double min=value, max=value, omin=o.value, omax=o.value;
        for(double f:stochasticValue) {
            if(f<min) min=f;
            if(f>max) max=f;
        }
        for(double f:o.stochasticValue) {
            if(f<omin) omin=f;
            if(f>omax) omax=f;
        }
        return Math.max(min, omin) <= Math.min(max, omax);
    }

}
