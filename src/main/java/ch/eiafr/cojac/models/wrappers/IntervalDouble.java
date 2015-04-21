package ch.eiafr.cojac.models.wrappers;

import ch.eiafr.cojac.interval.DoubleInterval;

public class IntervalDouble extends Number implements Comparable<IntervalDouble>
{
    protected double value;
    protected DoubleInterval interval;

    protected boolean isNan = false;
    protected boolean isInfinite = false;
    protected boolean isPositiveInfinite = false;

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
        if(a.isNan)
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
        if(a.isNan)
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
        return new IntervalDouble(a.value + a.value, DoubleInterval.add(a.interval, b.interval));
    }

    public static IntervalDouble dsub(IntervalDouble a, IntervalDouble b)
    {
        return new IntervalDouble(a.value - a.value, DoubleInterval.sub(a.interval, b.interval));
    }

    public static IntervalDouble dmul(IntervalDouble a, IntervalDouble b)
    {
        return new IntervalDouble(a.value * a.value, DoubleInterval.mul(a.interval, b.interval));
    }

    public static IntervalDouble ddiv(IntervalDouble a, IntervalDouble b)
    {
        return new IntervalDouble(a.value / a.value, DoubleInterval.div(a.interval, b.interval));
    }

    public static IntervalDouble drem(IntervalDouble a, IntervalDouble b)
    {
        return new IntervalDouble(a.value % b.value, DoubleInterval.modulo(a.interval, b.interval));
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
        if(a.isNan)
        {
            return Double.NaN;
        }
        return a.value;
    }

    public static Double toRealDouble(IntervalDouble a)
    {
        if(a.isNan)
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

    public static IntervalDouble math_sqrt(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.sqrt(a.value), DoubleInterval.sqrt(a.interval));
    }

    public static IntervalDouble math_pow(IntervalDouble a, IntervalDouble b)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.pow(a.value, b.value), DoubleInterval.pow(a.interval, b.interval));
    }

    public static IntervalDouble math_sin(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.sin(a.value), DoubleInterval.sin(a.interval));
    }

    public static IntervalDouble math_cos(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.cos(a.value), DoubleInterval.cos(a.interval));
    }

    /* Magic Method */

    public static IntervalDouble math_tan(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.tan(a.value), DoubleInterval.tan(a.interval));
    }

    public static IntervalDouble math_sinh(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.sinh(a.value), DoubleInterval.sinh(a.interval));
    }

     /* Mathematical function */

    public static IntervalDouble math_cosh(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.cosh(a.value), DoubleInterval.cosh(a.interval));
    }

    public static IntervalDouble math_tanh(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.tanh(a.value), DoubleInterval.tanh(a.interval));
    }

    public static IntervalDouble math_acos(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.acos(a.value), DoubleInterval.acos(a.interval));
    }

    public static IntervalDouble math_atan(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.atan(a.value), DoubleInterval.atan(a.interval));
    }

    public static IntervalDouble math_asin(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.asin(a.value), DoubleInterval.asin(a.interval));
    }

    public static IntervalDouble math_exp(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.exp(a.value), DoubleInterval.exp(a.interval));
    }

    public static IntervalDouble math_log(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.log(a.value), DoubleInterval.log(a.interval));
    }

    public static IntervalDouble math_log10(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.log10(a.value), DoubleInterval.log10(a.interval));
    }

    public static IntervalDouble math_abs(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(Math.abs(a.value), DoubleInterval.abs(a.interval));
    }

    public static IntervalDouble math_neg(IntervalDouble a)
    {
        if (a.isNan)
        {
            return new IntervalDouble(Double.NaN);
        }
        return new IntervalDouble(-a.value, DoubleInterval.neg(a.interval));
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
}
