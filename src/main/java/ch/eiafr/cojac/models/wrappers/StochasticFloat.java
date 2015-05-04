package ch.eiafr.cojac.models.wrappers;

import java.util.Random;

public class StochasticFloat extends Number implements Comparable<StochasticFloat>
{
    private static int nbrParallelNumber = 3;
    private static float threshold = 1.0F;
    private static Random r = new Random();
    protected float value;
    protected float[] stochasticValue;
    protected boolean isNan = false;
    protected boolean isUnStable = false;

    public StochasticFloat(float v)
    {
        if (Float.isNaN(v))
        {
            this.isNan = true;
        }
        this.value = v;
        this.stochasticValue = new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            this.stochasticValue[i] = v;
        }
    }

    public StochasticFloat(String v)
    {
        float value = Float.parseFloat(v);
        if (Float.isNaN(value))
        {
            this.isNan = true;
        }
        this.value = value;
        this.stochasticValue = new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            this.stochasticValue[i] = value;
        }
    }

    public StochasticFloat(StochasticDouble v)
    {
        if (v.isNan)
        {
            this.isNan = true;
        }
        this.value = (float) v.value;
        this.stochasticValue = new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            this.stochasticValue[i] = (float) v.stochasticValue[i];
        }
    }

    public StochasticFloat(StochasticFloat v)
    {
        if (v.isNan)
        {
            this.isNan = true;
        }
        this.value = v.value;
        this.stochasticValue = new float[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            this.stochasticValue[i] = v.stochasticValue[i];
        }
    }

    public static StochasticFloat fromDouble(double v)
    {
        return new StochasticFloat((float) v);
    }

    public static StochasticFloat fromString(String v)
    {
        return new StochasticFloat(v);
    }

    public static StochasticFloat fromFloat(float v)
    {
        return new StochasticFloat(v);
    }

    public static StochasticFloat fadd(StochasticFloat a, StochasticFloat b)
    {
        if (a.isNan || b.isNan)
        {
            return new StochasticFloat(Float.NaN);
        }
        StochasticFloat res = new StochasticFloat(a);
        res.value = a.value + b.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(a.stochasticValue[i] + b.stochasticValue[i]);
        }
        res.isUnStable = a.isUnStable || b.isUnStable;
        res.checkStability();
        return res;
    }

    public static StochasticFloat fdiv(StochasticFloat a, StochasticFloat b)
    {
        if (a.isNan || b.isNan)
        {
            return new StochasticFloat(Float.NaN);
        }
        StochasticFloat res = new StochasticFloat(a);
        res.value = a.value / b.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(a.stochasticValue[i] / b.stochasticValue[i]);
        }
        res.isUnStable = a.isUnStable || b.isUnStable;
        res.checkStability();
        return res;
    }

    public static StochasticFloat frem(StochasticFloat a, StochasticFloat b)
    {
        if (a.isNan || b.isNan)
        {
            return new StochasticFloat(Float.NaN);
        }
        StochasticFloat res = new StochasticFloat(a);
        res.value = a.value % b.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(a.stochasticValue[i] % b.stochasticValue[i]);
        }
        res.isUnStable = a.isUnStable || b.isUnStable;
        res.checkStability();
        return res;
    }

    public static StochasticFloat fsub(StochasticFloat a, StochasticFloat b)
    {
        if (a.isNan || b.isNan)
        {
            return new StochasticFloat(Float.NaN);
        }
        StochasticFloat res = new StochasticFloat(a);
        res.value = a.value - b.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(a.stochasticValue[i] - b.stochasticValue[i]);
        }
        res.isUnStable = a.isUnStable || b.isUnStable;
        res.checkStability();
        return res;
    }

    public static StochasticFloat fmul(StochasticFloat a, StochasticFloat b)
    {
        if (a.isNan || b.isNan)
        {
            return new StochasticFloat(Float.NaN);
        }
        StochasticFloat res = new StochasticFloat(a);
        res.value = a.value * b.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(a.stochasticValue[i] * b.stochasticValue[i]);
        }
        res.isUnStable = a.isUnStable || b.isUnStable;
        res.checkStability();
        return res;
    }

    public static int fcmpl(StochasticFloat a, StochasticFloat b)
    {
        return a.compareTo(b);
    }

    public static int fcmpg(StochasticFloat a, StochasticFloat b)
    {
        return a.compareTo(b);
    }

    public static StochasticFloat fneg(StochasticFloat a)
    {
        if (a.isNan)
        {
            return new StochasticFloat(Float.NaN);
        }
        StochasticFloat res = new StochasticFloat(a);
        res.value = -a.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = -a.stochasticValue[i];
        }
        res.isUnStable = a.isUnStable;
        return res;
    }

    public static int f2i(StochasticFloat a)
    {
        return a.intValue();
    }

    public static long f2l(StochasticFloat a)
    {
        return a.longValue();
    }

    public static StochasticDouble f2d(StochasticFloat a)
    {
        return new StochasticDouble(a.doubleValue());
    }

    public static StochasticFloat i2f(int a)
    {
        return new StochasticFloat((float) a);
    }

    public static StochasticFloat l2f(long a)
    {
        return new StochasticFloat((float) a);
    }

    public static StochasticFloat d2f(StochasticDouble a)
    {
        return new StochasticFloat(a);
    }

    public static float toFloat(StochasticFloat a)
    {
        return a.value;
    }

    public static Float toRealFloat(StochasticFloat a)
    {
        return new Float(a.value);
    }

    // Todo : maybe use float in parameter ?
    @SuppressWarnings("unused")
    public static void setThreshold(double value)
    {
        threshold = (float) value;
    }

    @SuppressWarnings("unused")
    public static void setNbrParallelNumber(int value)
    {
        nbrParallelNumber = value;
    }

    private static float rndRoundDouble(float value)
    {
        switch (r.nextInt(4))
        {
            case 0:
                // round to the smalles number (2.4 -> 2, 2.6 -> 3)
                if ((int) value - ((int) (value + 0.5)) == 0)
                {
                    return value - Math.ulp(value);
                }
                else
                {
                    return value + Math.ulp(value);
                }
            case 1:
                // round to 0
                if (value < 0.0)
                {
                    return value + Math.ulp(value);
                }
                else
                {
                    return value - Math.ulp(value);
                }
            case 2:
                // round to negative infinity
                return value - Math.ulp(value);
            case 3:
                // round to positive infinity
                return value + Math.ulp(value);
            default:
                return value;
        }
    }

    public static String COJAC_MAGIC_FLOAT_toStr(StochasticFloat n)
    {
        return n.toString();
    }

    public static StochasticFloat COJAC_MAGIC_FLOAT_relativeError(StochasticFloat n)
    {
        return new StochasticFloat(n.relativeError());
    }

    public static float toFloat(IntervalFloat a)
    {
        return (float) a.value;
    }

    @Override
    public int compareTo(StochasticFloat o)
    {
        if (this.isNan && o.isNan)
        {
            return 0;
        }
        if (o.isNan)
        {
            return 1;
        }
        if (this.isNan)
        {
            return -1;
        }
        if (this.value > o.value)
        {
            return 1;
        }
        if (this.value < o.value)
        {
            return -1;
        }
        return 0;
    }

    @Override
    public int intValue()
    {
        return (int) this.value;
    }

    @Override
    public long longValue()
    {
        return (long) this.value;
    }

    @Override
    public float floatValue()
    {
        return this.value;
    }

    @Override
    public double doubleValue()
    {
        return (double) this.value;
    }

    @Override
    public String toString()
    {
        // reference : BigDecimalDouble.java
        if (isNan) return "Nan";
        String res = "" + value + " : [%s]";
        String tmp = "";
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            tmp += ("" + stochasticValue[i]);
            if (i == nbrParallelNumber - 1)
                break;
            tmp += ";";
        }
        return String.format(res, tmp);
    }

    private void checkStability()
    {
        if (isUnStable)
        {
            return;
        }
        if (threshold < relativeError())
        {
            CojacStabilityException e = new CojacStabilityException();
            System.err.println("Cojac has destected a unstable operation");
            System.err.println("Relative error is : " + relativeError());
            System.err.println(this.toString());
            e.printStackTrace(System.err);
            this.isUnStable = true;
        }
    }

    // compute the relative error as the value divide by the mean of all the distance from value to the stochasticValue's values
    private float relativeError()
    {
        if (this.isNan)
        {
            return Float.NaN;
        }
        float numerator = 0.0F;
        float tmp;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            tmp = value - this.stochasticValue[i];
            numerator += (tmp >= 0.0F ? tmp : -tmp);
        }
        numerator = numerator / (float) nbrParallelNumber;
        return numerator / value;
    }
}
