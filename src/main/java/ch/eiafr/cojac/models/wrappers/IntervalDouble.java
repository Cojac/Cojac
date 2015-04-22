package ch.eiafr.cojac.models.wrappers;

import ch.eiafr.cojac.CojacReferences;
import ch.eiafr.cojac.interval.DoubleInterval;

import java.io.PrintStream;

public class IntervalDouble extends Number implements Comparable<IntervalDouble>
{
    protected double value;
    protected DoubleInterval interval;

    protected boolean isNan = false;
    protected boolean isInfinite = false;
    protected boolean isPositiveInfinite = false;

    private static double threshold = 1.0;
    private static boolean checkComp = false;

    /* TODO
        infinite and NaN verification
     */

    public IntervalDouble(double value)
    {
        if (Double.isNaN(value))
        {
            this.value = Double.NaN;
            this.interval = new DoubleInterval(Double.NaN);
            this.isNan = true;
        }
        else
        {
            this.value = value;
            this.interval = new DoubleInterval(value);
            this.isNan = false;
        }
    }

    public IntervalDouble(String value)
    {
        double a = Double.parseDouble(value);
        if (Double.isNaN(a))
        {
            this.value = Double.NaN;
            this.interval = new DoubleInterval(Double.NaN);
            this.isNan = true;
        }
        else
        {
            this.value = a;
            this.interval = new DoubleInterval(a);
            this.isNan = false;
        }
    }

    public IntervalDouble(int value)
    {
        this.value = (double) value;
        this.interval = new DoubleInterval((double) value);
    }

    public IntervalDouble(long value)
    {
        this.value = (double) value;
        this.interval = new DoubleInterval((double) value);
    }

    public IntervalDouble(float value)
    {
        this.value = (double) value;
        this.interval = new DoubleInterval((double) value);
    }

    public IntervalDouble(double value, DoubleInterval interval)
    {
        this.value = value;
        this.interval = interval;
    }

    public IntervalDouble(IntervalDouble a)
    {
        if (a.isNan)
        {
            this.value = Double.NaN;
            this.interval = new DoubleInterval(Double.NaN);
            this.isNan = true;
        }
        else
        {
            this.value = a.value;
            this.interval = new DoubleInterval(a.interval);
            this.isNan = false;
        }
    }

    public IntervalDouble(IntervalFloat a)
    {
        if (a.isNan)
        {
            this.value = Double.NaN;
            this.interval = new DoubleInterval(Double.NaN);
            this.isNan = true;
        }
        else
        {
            this.value = a.value;
            this.interval = new DoubleInterval(a.interval);
            this.isNan = false;
        }
    }

    public static IntervalDouble fromDouble(double a)
    {
        return new IntervalDouble(a);
    }

    public static IntervalDouble fromString(String a)
    {
        return new IntervalDouble(a);
    }

    public static IntervalDouble dadd(IntervalDouble a, IntervalDouble b)
    {
        if(a.isNan || b.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res = new IntervalDouble(a.value + b.value, DoubleInterval.add(a.interval, b.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble dsub(IntervalDouble a, IntervalDouble b)
    {
        if(a.isNan || b.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(a.value - b.value, DoubleInterval.sub(a.interval, b.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble dmul(IntervalDouble a, IntervalDouble b)
    {
        if(a.isNan || b.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res = new IntervalDouble(a.value * b.value, DoubleInterval.mul(a.interval, b.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble ddiv(IntervalDouble a, IntervalDouble b)
    {
        if(a.isNan || b.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res = new IntervalDouble(a.value / b.value, DoubleInterval.div(a.interval, b.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble drem(IntervalDouble a, IntervalDouble b)
    {
        if(a.isNan || b.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res = new IntervalDouble(a.value % b.value, DoubleInterval.modulo(a.interval, b.interval));
        res.checkStability();
        return res;
    }

    // Todo : is this correct ?
    public static int dcmpl(IntervalDouble a, IntervalDouble b)
    {
        return a.compareTo(b);
    }

    public static int dcmpg(IntervalDouble a, IntervalDouble b)
    {
        return a.compareTo(b);
    }

    // it's just a neg operation, dont need to check the stability...
    public static IntervalDouble dneg(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(-a.value, DoubleInterval.neg(a.interval));
    }

    public static long d2l(IntervalDouble a)
    {
        return a.longValue();
    }

    public static int d2i(IntervalDouble a)
    {
        return a.intValue();
    }

    public static float d2f(IntervalDouble a)
    {
        return a.floatValue();
    }

    public static IntervalDouble i2d(int a)
    {
        return new IntervalDouble(a);
    }

    public static IntervalDouble f2d(float a)
    {
        return new IntervalDouble(a);
    }

    public static IntervalDouble l2d(long a)
    {
        return new IntervalDouble(a);
    }

    public static double toDouble(IntervalDouble a)
    {
        if (a.isNan)
        {
            return Double.NaN;
        }
        return a.value;
    }

    public static Double toRealDouble(IntervalDouble a)
    {
        if (a.isNan)
        {
            return Double.NaN;
        }
        return new Double(a.value);
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(IntervalDouble n)
    {
        return n.toString();
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(IntervalFloat n)
    {
        return n.toString();
    }

    public static boolean COJAC_MAGIC_DOUBLE_isIn(IntervalDouble n)
    {
        return DoubleInterval.isIn(n.interval, n.value);
    }

    public static int COJAC_MAGIC_DOUBLE_IntervalcompareTo(IntervalDouble a, IntervalDouble b)
    {
        return a.compareTo(b);
    }

    public static IntervalDouble COJAC_MAGIC_DOUBLE_width(IntervalDouble a)
    {
        return new IntervalDouble(DoubleInterval.width(a.interval));
    }

    public static IntervalDouble math_sqrt(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res = new IntervalDouble(Math.sqrt(a.value), DoubleInterval.sqrt(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_pow(IntervalDouble a, IntervalDouble b)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.pow(a.value, b.value), DoubleInterval.pow(a.interval, b.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_sin(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.sin(a.value), DoubleInterval.sin(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_cos(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.cos(a.value), DoubleInterval.cos(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_tan(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.tan(a.value), DoubleInterval.tan(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_sinh(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.sinh(a.value), DoubleInterval.sinh(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_cosh(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.cosh(a.value), DoubleInterval.cosh(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_tanh(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.tanh(a.value), DoubleInterval.tanh(a.interval));
        res.checkStability();
        return res;
    }

    /* Magic Method */

    public static IntervalDouble math_acos(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.acos(a.value), DoubleInterval.acos(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_atan(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.atan(a.value), DoubleInterval.atan(a.interval));
        res.checkStability();
        return res;
    }

     /* Mathematical function */

    public static IntervalDouble math_asin(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.asin(a.value), DoubleInterval.asin(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_exp(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.exp(a.value), DoubleInterval.exp(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_log(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.log(a.value), DoubleInterval.log(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_log10(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.log10(a.value), DoubleInterval.log10(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_abs(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(Math.abs(a.value), DoubleInterval.abs(a.interval));
        res.checkStability();
        return res;
    }

    public static IntervalDouble math_neg(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        IntervalDouble res =  new IntervalDouble(-a.value, DoubleInterval.neg(a.interval));
        res.checkStability();
        return res;
    }


    @SuppressWarnings("unused")
    public static void setThreshold(double value)
    {
        threshold = value;
    }

    @SuppressWarnings("unused")
    public static void setCheckComp(boolean value)
    {
        checkComp = value;
    }

    @Override
    public int intValue()
    {
        if (isNan)
        {
            return 0;
        }
        if (isInfinite)
        {
            return isPositiveInfinite ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        return (int) value;
    }

    @Override
    public long longValue()
    {
        return (long) value;
    }

    @Override
    public float floatValue()
    {
        return (float) value;
    }

    @Override
    public double doubleValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        // reference : BigDecimalDouble.java
        if (isNan) return "Nan";
        if (isInfinite) return (isPositiveInfinite ? '+' : '-') + "Infinity";
        return String.format("%s:%s", value, interval);
    }

    @Override
    public int compareTo(IntervalDouble o)
    {
        int compResult = this.interval.compareTo(o.interval);
        if(checkComp)
        {
            checkComp(compResult);
        }
        if (compResult != 0)
        {
            return compResult;
        }
        if (this.value < o.value)
        {
            return -1;
        }
        if (this.value > o.value)
        {
            return 1;
        }
        return 0;
    }

    private void checkStability()
    {
        try
        {
            if(threshold < relativeError())
            {
                throw new CojacStabilityException();
            }
        }
        catch (CojacStabilityException e)
        {
            System.err.println("Cojac has destected a unstable operation :");
            e.printStackTrace(System.err);
        }
    }

    private void checkComp(int compResult)
    {
        try
        {
            if(compResult == 0)
            {
                throw new CojacStabilityComparaisonException();
            }
        }
        catch (CojacStabilityComparaisonException e)
        {
            System.err.println("Cojac has destected a unstable comparaison :");
            e.printStackTrace(System.err);
        }
    }

    private double relativeError()
    {
        if(this.isNan)
        {
            return Double.NaN;
        }
        double numerator = Math.min(Math.abs(this.interval.inf),Math.abs(this.interval.sup));
        if(numerator == 0.0)
        {
            return Double.NaN;
        }
        return DoubleInterval.width(this.interval) / numerator;
    }
}
