package ch.eiafr.cojac.models.wrappers;

import ch.eiafr.cojac.interval.DoubleInterval;

public class IntervalFloat extends Number implements Comparable<IntervalFloat>
{
    private static float threshold;
    private static boolean checkComp;

    protected float value;
    protected DoubleInterval interval;
    protected boolean isNan = false;
    protected boolean isInfinite = false;
    protected boolean isPositiveInfinite = false;
    protected boolean isUnStable = false;

    /* TODO
        infinite and NaN verification
     */

    public IntervalFloat(double value)
    {
        if (Double.isNaN(value))
        {
            this.value = (float) Double.NaN;
            this.interval = new DoubleInterval(Double.NaN);
            this.isNan = true;
        }
        else
        {
            this.value = (float) value;
            this.interval = new DoubleInterval(value);
            this.isNan = false;
        }
    }

    public IntervalFloat(String value)
    {
        double a = Double.parseDouble(value);
        if (Double.isNaN(a))
        {
            this.value = (float) Double.NaN;
            this.interval = new DoubleInterval(Double.NaN);
            this.isNan = true;
        }
        else
        {
            this.value = (float) a;
            this.interval = new DoubleInterval(a);
            this.isNan = false;
        }
    }

    public IntervalFloat(int value)
    {
        this.value = (float) value;
        this.interval = new DoubleInterval((double) value);
    }

    public IntervalFloat(long value)
    {
        this.value = (float) value;
        this.interval = new DoubleInterval((double) value);
    }

    public IntervalFloat(float value)
    {
        this.value = value;
        this.interval = new DoubleInterval((double) value);
    }

    public IntervalFloat(float value, DoubleInterval interval)
    {
        this.value = value;
        this.interval = interval;
    }

    public IntervalFloat(IntervalDouble a)
    {
        if (a.isNan)
        {
            this.value = (float) Double.NaN;
            this.interval = new DoubleInterval(Double.NaN);
            this.isNan = true;
        }
        else
        {
            this.value = (float) a.value;
            this.interval = new DoubleInterval(a.interval);
            this.isNan = false;
        }
    }

    public IntervalFloat(IntervalFloat a)
    {
        if (a.isNan)
        {
            this.value = (float) Double.NaN;
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

    @SuppressWarnings("unused")
    public static void setThreshold(float value)
    {
        threshold = value;
    }

    @SuppressWarnings("unused")
    public static void setCheckComp(boolean value)
    {
        checkComp = value;
    }

    public static IntervalFloat fromDouble(double a)
    {
        return new IntervalFloat(a);
    }

    public static IntervalFloat fromString(String a)
    {
        return new IntervalFloat(a);
    }

    public static IntervalFloat fromFloat(float a)
    {
        return new IntervalFloat(a);
    }

    public static IntervalFloat fadd(IntervalFloat a, IntervalFloat b)
    {
        if (a.isNan || b.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        IntervalFloat res = new IntervalFloat(a.value + b.value, DoubleInterval.add(a.interval, b.interval));
        res.isUnStable = a.isUnStable || b.isUnStable;
        res.checkStability();
        return res;
    }

    public static IntervalFloat fsub(IntervalFloat a, IntervalFloat b)
    {
        if (a.isNan || b.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        IntervalFloat res = new IntervalFloat(a.value - b.value, DoubleInterval.sub(a.interval, b.interval));
        res.isUnStable = a.isUnStable || b.isUnStable;
        res.checkStability();
        return res;
    }

    public static IntervalFloat fmul(IntervalFloat a, IntervalFloat b)
    {
        if (a.isNan || b.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        IntervalFloat res = new IntervalFloat(a.value * b.value, DoubleInterval.mul(a.interval, b.interval));
        res.isUnStable = a.isUnStable || b.isUnStable;
        res.checkStability();
        return res;
    }

    public static IntervalFloat fdiv(IntervalFloat a, IntervalFloat b)
    {
        if (a.isNan || b.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        IntervalFloat res = new IntervalFloat(a.value / b.value, DoubleInterval.div(a.interval, b.interval));
        res.isUnStable = a.isUnStable || b.isUnStable;
        res.checkStability();
        return res;
    }

    public static IntervalFloat frem(IntervalFloat a, IntervalFloat b)
    {
        if (a.isNan || b.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        IntervalFloat res = new IntervalFloat(a.value % b.value, DoubleInterval.modulo(a.interval, b.interval));
        res.isUnStable = a.isUnStable || b.isUnStable;
        res.checkStability();
        return res;
    }

    public static IntervalFloat fneg(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        IntervalFloat res = new IntervalFloat(-a.value, DoubleInterval.neg(a.interval));
        return res;
    }

    // Todo : is this correct ?
    public static int fcmpl(IntervalFloat a, IntervalFloat b)
    {
        return a.compareTo(b);
    }

    public static int fcmpg(IntervalFloat a, IntervalFloat b)
    {
        return a.compareTo(b);
    }

    public static long f2l(IntervalFloat a)
    {
        return a.longValue();
    }

    public static int f2i(IntervalFloat a)
    {
        return a.intValue();
    }

    public static IntervalDouble f2d(IntervalFloat a)
    {
        return new IntervalDouble(a);
    }

    public static IntervalFloat i2f(int a)
    {
        return new IntervalFloat(a);
    }

    public static IntervalFloat d2f(IntervalDouble a)
    {
        return new IntervalFloat(a);
    }

    public static IntervalFloat l2f(long a)
    {
        return new IntervalFloat(a);
    }

    public static String COJAC_MAGIC_FLOAT_toStr(IntervalFloat n)
    {
        return n.toString();
    }

    // Todo correct ?
    public static IntervalFloat COJAC_MAGIC_FLOAT_relativeError(IntervalFloat n)
    {
        return new IntervalFloat(n.relativeError());
    }

    public static float toFloat(IntervalFloat a)
    {
        return (float) a.value;
    }

    public static Float toRealFloat(IntervalFloat a)
    {
        return new Float(a.value);
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
    public int compareTo(IntervalFloat o)
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
        // Todo
        /*
        if(this.interval.inf > this.value || this.interval.sup < this.value)
        {
            CojacStabilityException e = new CojacStabilityException("The value is out of the interval !");
            System.err.println("Cojac has destected a unstable operation :");
            e.printStackTrace(System.err);
        }
        */
        if(this.isUnStable)
        {
            return;
        }
        if (threshold < relativeError())
        {
            CojacStabilityException e = new CojacStabilityException();
            System.err.println("Cojac has destected a unstable operation :");
            e.printStackTrace(System.err);
            this.isUnStable = true;
        }
    }

    private void checkComp(int compResult)
    {
        if (compResult == 0)
        {
            CojacStabilityComparaisonException e = new CojacStabilityComparaisonException();
            System.err.println("Cojac has destected a unstable comparaison :");
            e.printStackTrace(System.err);
        }
    }

    private double relativeError()
    {
        if (this.isNan)
        {
            return Double.NaN;
        }
        double numerator = Math.min(Math.abs(this.interval.inf), Math.abs(this.interval.sup));
        if (numerator == 0.0)
        {
            return Double.NaN;
        }
        return DoubleInterval.width(this.interval) / numerator;
    }
}
