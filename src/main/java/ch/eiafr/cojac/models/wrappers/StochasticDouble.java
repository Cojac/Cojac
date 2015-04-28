package ch.eiafr.cojac.models.wrappers;

import java.util.Comparator;
import java.util.Random;

public class StochasticDouble extends Number implements Comparable<StochasticDouble>
{
    private static int nbrParallelNumber = 3;
    private static double threshold = 1.0;
    private static Random r = new Random();
    protected double value;
    protected double[] stochasticValue;
    protected boolean isNan = false;

    /* Constructor */

    public StochasticDouble(double v)
    {
        if (Double.isNaN(v))
        {
            this.isNan = true;
        }
        this.value = v;
        this.stochasticValue = new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            stochasticValue[i] = v;
        }
    }

    public StochasticDouble(String v)
    {
        double value = Double.parseDouble(v);
        if (Double.isNaN(value))
        {
            this.isNan = true;
        }
        this.value = value;
        this.stochasticValue = new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            stochasticValue[i] = value;
        }
    }

    public StochasticDouble(StochasticDouble v)
    {
        double value = v.value;
        if (Double.isNaN(value))
        {
            this.isNan = true;
        }
        this.value = value;
        this.stochasticValue = new double[nbrParallelNumber];
        stochasticValue = v.stochasticValue.clone();
    }

    public StochasticDouble(StochasticFloat v)
    {
        double value = (double) v.value;
        if (Double.isNaN(value))
        {
            this.isNan = true;
        }
        this.value = value;
        this.stochasticValue = new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            stochasticValue[i] = (double) v.stochasticValue[i];
        }
    }

    public static StochasticDouble fromDouble(double v)
    {
        return new StochasticDouble(v);
    }

    public static StochasticDouble fromString(String v)
    {
        return new StochasticDouble(v);
    }

    public static StochasticDouble dadd(StochasticDouble a, StochasticDouble b)
    {
        if (a.isNan || b.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = a.value + b.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(a.stochasticValue[i] + b.stochasticValue[i]);
        }
        return res;
    }

    public static StochasticDouble ddiv(StochasticDouble a, StochasticDouble b)
    {
        if (a.isNan || b.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = a.value / b.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(a.stochasticValue[i] / b.stochasticValue[i]);
        }
        return res;
    }

    public static StochasticDouble drem(StochasticDouble a, StochasticDouble b)
    {
        if (a.isNan || b.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = a.value % b.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(a.stochasticValue[i] % b.stochasticValue[i]);
        }
        return res;
    }

    public static StochasticDouble dsub(StochasticDouble a, StochasticDouble b)
    {
        if (a.isNan || b.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = a.value - b.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(a.stochasticValue[i] - b.stochasticValue[i]);
        }
        return res;
    }

    public static StochasticDouble dmul(StochasticDouble a, StochasticDouble b)
    {
        if (a.isNan || b.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = a.value * b.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(a.stochasticValue[i] * b.stochasticValue[i]);
        }
        return res;
    }

    public static int dcmpl(StochasticDouble a, StochasticDouble b)
    {
        return a.compareTo(b);
    }

    public static int dcmpg(StochasticDouble a, StochasticDouble b)
    {
        return a.compareTo(b);
    }

    public static StochasticDouble dneg(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = -res.value;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = -res.stochasticValue[i];
        }
        return res;
    }

    public static int d2i(StochasticDouble a)
    {
        return a.intValue();
    }

    public static long d2l(StochasticDouble a)
    {
        return a.longValue();
    }

    public static StochasticFloat d2f(StochasticDouble a)
    {
        return new StochasticFloat(a);
    }

    public static StochasticDouble i2d(int a)
    {
        return new StochasticDouble(a);
    }

    public static StochasticDouble l2d(long a)
    {
        return new StochasticDouble(a);
    }

    public static StochasticDouble f2d(StochasticFloat a)
    {
        return new StochasticDouble(a);
    }

    public static double toDouble(StochasticDouble a)
    {
        return a.value;
    }

    public static Double toRealDouble(StochasticDouble a)
    {
        return new Double(a.value);
    }


    private static double rndRoundDouble(double value)
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

    @Override
    public String toString()
    {
        // reference : BigDecimalDouble.java
        if (isNan) return "Nan";
        String res = "" + value + " : [%s]";
        String tmp = "";
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            tmp+=(""+stochasticValue[i]);
            if(i == nbrParallelNumber-1)
                break;
            tmp+=";";
        }
        return String.format(res,tmp);
    }

    /* Mathematical function */
    public static StochasticDouble math_sqrt(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.sqrt(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.sqrt(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_pow(StochasticDouble a, StochasticDouble b)
    {
        if (a.isNan || b.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.pow(a.value, b.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.pow(a.stochasticValue[i], b.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_sin(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.sin(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.sin(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_cos(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.cos(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.cos(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_tan(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.tan(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.tan(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_sinh(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.sinh(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.sinh(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_cosh(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.cosh(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.cosh(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_tanh(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.tanh(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.tanh(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_acos(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.acos(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.acos(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_atan(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.atan(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.atan(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_asin(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.asin(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.asin(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_exp(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.exp(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.exp(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_log(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.log(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.log(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_log10(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.log10(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = rndRoundDouble(Math.log10(a.stochasticValue[i]));
        }
        return res;
    }

    public static StochasticDouble math_abs(StochasticDouble a)
    {
        if (a.isNan)
        {
            return new StochasticDouble(Double.NaN);
        }
        StochasticDouble res = new StochasticDouble(a);
        res.value = Math.abs(res.value);
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            res.stochasticValue[i] = Math.abs(a.stochasticValue[i]);
        }
        return res;
    }

    @Override
    public int compareTo(StochasticDouble o)
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
        return (float) this.value;
    }

    @Override
    public double doubleValue()
    {
        return this.value;
    }

    @SuppressWarnings("unused")
    public static void setThreshold(double value)
    {
        threshold = value;
    }

    @SuppressWarnings("unused")
    public static void setNbrParallelNumber(int value)
    {
        nbrParallelNumber = value;
    }

    private void checkStability()
    {
        if (threshold < relativeError())
        {
            CojacStabilityException e = new CojacStabilityException();
            System.err.println("Cojac has destected a unstable operation :");
            e.printStackTrace(System.err);
        }
    }

    // compute the relative error as the value divide by the mean of all the distance from value to the stochasticValue's values
    private double relativeError()
    {
        if (this.isNan)
        {
            return Double.NaN;
        }
        double numerator = 0.0;
        for (int i = 0; i < nbrParallelNumber; i++)
        {
            numerator += this.stochasticValue[i];
        }
        numerator /= (double)nbrParallelNumber;
        if (numerator == 0.0)
        {
            return Double.NaN;
        }
        return this.value / numerator;
    }

    /* Magic method */
    public static String COJAC_MAGIC_DOUBLE_toStr(StochasticDouble n)
    {
        return n.toString();
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(StochasticFloat n)
    {
        return n.toString();
    }

    public static StochasticDouble COJAC_MAGIC_DOUBLE_relativeError(StochasticDouble n)
    {
        return new StochasticDouble(n.relativeError());
    }
}
