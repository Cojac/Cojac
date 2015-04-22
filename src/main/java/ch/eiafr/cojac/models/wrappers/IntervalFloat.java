package ch.eiafr.cojac.models.wrappers;

import ch.eiafr.cojac.interval.DoubleInterval;

public class IntervalFloat extends Number implements Comparable<IntervalFloat>
{
    protected double value;
    protected DoubleInterval interval;

    protected boolean isNan = false;
    protected boolean isInfinite = false;
    protected boolean isPositiveInfinite = false;

    /* TODO
        infinite and NaN verification
     */

    public IntervalFloat(double value)
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

    public IntervalFloat(String value)
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

    public IntervalFloat(int value)
    {
        this.value = (double) value;
        this.interval = new DoubleInterval((double) value);
    }

    public IntervalFloat(long value)
    {
        this.value = (double) value;
        this.interval = new DoubleInterval((double) value);
    }

    public IntervalFloat(float value)
    {
        this.value = (double) value;
        this.interval = new DoubleInterval((double) value);
    }

    public IntervalFloat(double value, DoubleInterval interval)
    {
        this.value = value;
        this.interval = interval;
    }

    public IntervalFloat(IntervalDouble a)
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

    public IntervalFloat(IntervalFloat a)
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
        return new IntervalFloat(a.value + b.value, DoubleInterval.add(a.interval, b.interval));
    }

    public static IntervalFloat fsub(IntervalFloat a, IntervalFloat b)
    {
        return new IntervalFloat(a.value - b.value, DoubleInterval.sub(a.interval, b.interval));
    }

    public static IntervalFloat fmul(IntervalFloat a, IntervalFloat b)
    {
        return new IntervalFloat(a.value * b.value, DoubleInterval.mul(a.interval, b.interval));
    }

    public static IntervalFloat fdiv(IntervalFloat a, IntervalFloat b)
    {
        return new IntervalFloat(a.value / b.value, DoubleInterval.div(a.interval, b.interval));
    }

    public static IntervalFloat frem(IntervalFloat a, IntervalFloat b)
    {
        return new IntervalFloat(a.value % b.value, DoubleInterval.modulo(a.interval, b.interval));
    }

    public static IntervalFloat fneg(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(-a.value, DoubleInterval.neg(a.interval));
    }

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

    public static double f2d(IntervalFloat a)
    {
        return a.doubleValue();
    }

    public static IntervalFloat i2f(int a)
    {
        return new IntervalFloat(a);
    }

    public static IntervalFloat d2f(double a)
    {
        return new IntervalFloat(a);
    }

    public static IntervalFloat l2f(long a)
    {
        return new IntervalFloat(a);
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(IntervalFloat n)
    {
        return n.toString();
    }

    public static float toFloat(IntervalFloat a)
    {
        return (float)a.value;
    }

    public static Float toRealFloat(IntervalFloat a)
    {
        return new Float(a.value);
    }

    public static IntervalFloat math_sqrt(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.sqrt(a.value), DoubleInterval.sqrt(a.interval));
    }

    public static IntervalFloat math_pow(IntervalFloat a, IntervalFloat b)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.pow(a.value, b.value), DoubleInterval.pow(a.interval, b.interval));
    }

    public static IntervalFloat math_sin(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.sin(a.value), DoubleInterval.sin(a.interval));
    }

    public static IntervalFloat math_cos(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.cos(a.value), DoubleInterval.cos(a.interval));
    }

    /* Magic Method */

    public static IntervalFloat math_tan(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.tan(a.value), DoubleInterval.tan(a.interval));
    }

    public static IntervalFloat math_sinh(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.sinh(a.value), DoubleInterval.sinh(a.interval));
    }

     /* Mathematical function */

    public static IntervalFloat math_cosh(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.cosh(a.value), DoubleInterval.cosh(a.interval));
    }

    public static IntervalFloat math_tanh(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.tanh(a.value), DoubleInterval.tanh(a.interval));
    }

    public static IntervalFloat math_acos(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.acos(a.value), DoubleInterval.acos(a.interval));
    }

    public static IntervalFloat math_atan(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.atan(a.value), DoubleInterval.atan(a.interval));
    }

    public static IntervalFloat math_asin(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.asin(a.value), DoubleInterval.asin(a.interval));
    }

    public static IntervalFloat math_exp(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.exp(a.value), DoubleInterval.exp(a.interval));
    }

    public static IntervalFloat math_log(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.log(a.value), DoubleInterval.log(a.interval));
    }

    public static IntervalFloat math_log10(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.log10(a.value), DoubleInterval.log10(a.interval));
    }

    public static IntervalFloat math_abs(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(Math.abs(a.value), DoubleInterval.abs(a.interval));
    }

    public static IntervalFloat math_neg(IntervalFloat a)
    {
        if (a.isNan)
        {
            return new IntervalFloat(Double.NaN);
        }
        return new IntervalFloat(-a.value, DoubleInterval.neg(a.interval));
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
