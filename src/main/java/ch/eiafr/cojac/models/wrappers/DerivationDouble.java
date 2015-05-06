package ch.eiafr.cojac.models.wrappers;

public class DerivationDouble extends Number implements Comparable<DerivationDouble>
{
    protected boolean isNaN; // Todo

    protected double value;
    protected double dValue;

    public DerivationDouble(double v)
    {
        this.value = v;
        this.dValue = 0.0;
    }

    public DerivationDouble(double value, double dValue)
    {
        this.value = value;
        this.dValue = dValue;
    }

    public DerivationDouble(String v)
    {
        this.value = Double.parseDouble(v);
        this.dValue = 0.0;
    }

    public DerivationDouble(DerivationFloat v)
    {
        this.value = v.delegateDoubleWrapper.value;
        this.dValue = v.delegateDoubleWrapper.dValue;
    }

    public DerivationDouble(DerivationDouble v)
    {
        this.value = v.value;
        this.dValue = v.dValue;
    }

    public static DerivationDouble fromDouble(double v)
    {
        return new DerivationDouble(v);
    }

    public static DerivationDouble fromString(String v)
    {
        return new DerivationDouble(v);
    }

    public static DerivationDouble dadd(DerivationDouble a, DerivationDouble b)
    {
        return new DerivationDouble(a.value + b.value, a.dValue + b.dValue);
    }

    public static DerivationDouble ddiv(DerivationDouble a, DerivationDouble b)
    {
        double value = a.value / b.value;
        double dValue = (a.dValue * b.value - b.dValue * a.value) / (b.value * b.value);
        return new DerivationDouble(value, dValue);
    }

    // Todo Correct ?
    public static DerivationDouble drem(DerivationDouble a, DerivationDouble b)
    {
        return new DerivationDouble(a.value % b.value, a.dValue % b.dValue);
    }

    public static DerivationDouble dsub(DerivationDouble a, DerivationDouble b)
    {
        return new DerivationDouble(a.value - b.value, a.dValue - b.dValue);
    }

    public static DerivationDouble dmul(DerivationDouble a, DerivationDouble b)
    {
        double value = a.value * b.value;
        double dValue = a.dValue * b.value + a.value * b.dValue;
        return new DerivationDouble(value, dValue);
    }

    public static int dcmpl(DerivationDouble a, DerivationDouble b)
    {
        return a.compareTo(b);
    }

    public static int dcmpg(DerivationDouble a, DerivationDouble b)
    {
        return a.compareTo(b);
    }

    public static DerivationDouble dneg(DerivationDouble a)
    {
        return new DerivationDouble(-a.value, -a.dValue);
    }

    public static int d2i(DerivationDouble a)
    {
        return (int) a.value;
    }

    public static long d2l(DerivationDouble a)
    {
        return (long) a.value;
    }

    public static DerivationFloat d2f(DerivationDouble a)
    {
        return new DerivationFloat(a);
    }

    public static DerivationDouble i2d(int a)
    {
        return new DerivationDouble((double) a);
    }

    public static DerivationDouble l2d(long a)
    {
        return new DerivationDouble((double) a);
    }

    public static DerivationDouble f2d(DerivationFloat a)
    {
        return new DerivationDouble(a.delegateDoubleWrapper);
    }

    public static double toDouble(DerivationDouble a)
    {
        return a.value;
    }

    public static Double toRealDouble(DerivationDouble a)
    {
        return new Double(a.value);
    }

    public static DerivationDouble COJAC_MAGIC_DOUBLE_getDerivation(DerivationDouble a)
    {
        return new DerivationDouble(a.dValue);
    }

    public static void COJAC_MAGIC_DOUBLE_specifieToDerivate(DerivationDouble a)
    {
        a.dValue = 1.0;
    }

    public static DerivationDouble math_sqrt(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.sqrt(a.value);
        double dValue = a.dValue / (2.0 * Math.sqrt(a.value));
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_pow(DerivationDouble a, DerivationDouble b)
    {
        if (a.isNaN || b.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.pow(a.value, b.value);
        double dValue = Math.pow(a.value,b.value) * (((b.value*a.dValue)/a.value) + Math.log(a.value) * b.dValue);
        return new DerivationDouble(value,dValue);
    }

    public static DerivationDouble math_sin(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.sin(a.value);
        double dValue = Math.cos(a.value) * a.dValue;
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_cos(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.cos(a.value);
        double dValue = -Math.sin(a.value) * a.dValue;
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_tan(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.tan(a.value);
        double dValue = a.dValue / (Math.cos(a.value) * Math.cos(a.value));
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_sinh(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.sinh(a.value);
        double dValue = a.dValue * Math.cosh(a.value);
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_cosh(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.cosh(a.value);
        double dValue = a.dValue * Math.sinh(a.value);
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_tanh(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.tanh(a.value);
        double dValue = a.dValue / (Math.cosh(a.value) * Math.cosh(a.value));
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_acos(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.acos(a.value);
        double dValue = -a.dValue / (Math.sqrt(1.0 - a.value * a.value));
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_atan(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.atan(a.value);
        double dValue = a.dValue / (1.0 + a.value * a.value);
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_asin(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.asin(a.value);
        double dValue = a.dValue / (Math.sqrt(1.0 - a.value * a.value));
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_exp(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.exp(a.value);
        double dValue = a.dValue * Math.exp(a.value);
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_log(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.log(a.value);
        double dValue = a.dValue / a.value;
        return new DerivationDouble(value, dValue);
    }

    // todo correct ?
    public static DerivationDouble math_log10(DerivationDouble a)
    {
        if (a.isNaN)
        {
            return new DerivationDouble(Double.NaN);
        }
        double value = Math.log10(a.value);
        double dValue = a.dValue / (a.value * Math.log(10.0));
        return new DerivationDouble(value, dValue);
    }

    public static DerivationDouble math_abs(DerivationDouble a)
    {
        RuntimeException exception = new RuntimeException("abs'(x) : not derivable !");
        exception.printStackTrace(System.err);
        return null;
    }

    @Override
    public int compareTo(DerivationDouble o)
    {
        if (o.isNaN && this.isNaN)
        {
            return 0;
        }
        if (o.isNaN)
        {
            return 1;
        }
        if (this.isNaN)
        {
            return -1;
        }
        if (o.value < this.value)
        {
            return 1;
        }
        if (o.value > this.value)
        {
            return -1;
        }
        return 0;
    }

    @Override
    public int intValue()
    {
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
}
