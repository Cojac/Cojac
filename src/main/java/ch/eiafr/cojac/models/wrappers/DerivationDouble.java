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
        this.value = v.value.value;
        this.dValue = v.value.dValue;
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
        return new DerivationDouble(a.value+b.value,a.dValue+b.dValue);
    }

    public static DerivationDouble ddiv(DerivationDouble a, DerivationDouble b)
    {
        double value = a.value / b.value;
        double dValue = (a.dValue * b.value - b.dValue * a.value) / (b.value * b.value);
        return new DerivationDouble(value,dValue);
    }

    // Todo Correct ?
    public static DerivationDouble drem(DerivationDouble a, DerivationDouble b)
    {
        return new DerivationDouble(a.value % b.value, a.dValue % b.dValue);
    }

    public static DerivationDouble dsub(DerivationDouble a, DerivationDouble b)
    {
        return new DerivationDouble(a.value - b.value,a.dValue - b.dValue);
    }

    public static DerivationDouble dmul(DerivationDouble a, DerivationDouble b)
    {
        double value = a.value * b.value;
        double dValue = a.dValue * b.value + a.value * b.dValue;
        return new DerivationDouble(value,dValue);
    }

    public static int dcmpl(DerivationDouble a, DerivationDouble b)
    {
     return    a.compareTo(b);
    }

    public static int dcmpg(DerivationDouble a, DerivationDouble b)
    {
        return a.compareTo(b);
    }

    public static DerivationDouble dneg(DerivationDouble a)
    {
        return new DerivationDouble(-a.value,-a.dValue);
    }

    public static int d2i(DerivationDouble a)
    {
        return (int)a.value;
    }

    public static long d2l(DerivationDouble a)
    {
        return (long)a.value;
    }

    public static DerivationFloat d2f(DerivationDouble a)
    {
        return new DerivationFloat(a);
    }

    public static DerivationDouble i2d(int a)
    {
        return new DerivationDouble((double)a);
    }

    public static DerivationDouble l2d(long a)
    {
        return new DerivationDouble((double)a);
    }

    public static DerivationDouble f2d(DerivationFloat a)
    {
        return new DerivationDouble(a.value);
    }

    public static double toDouble(DerivationDouble a)
    {
        return a.value;
    }

    public static Double toRealDouble(DerivationDouble a)
    {
        return new Double(a.value);
    }

    @Override
    public int compareTo(DerivationDouble o)
    {
        if(o.isNaN && this.isNaN)
        {
            return 0;
        }
        if(o.isNaN)
        {
            return 1;
        }
        if(this.isNaN)
        {
            return -1;
        }
        if(o.value < this.value)
        {
            return 1;
        }
        if(o.value > this.value)
        {
            return -1;
        }
        return 0;
    }

    @Override
    public int intValue()
    {
        return (int)value;
    }

    @Override
    public long longValue()
    {
        return (long)value;
    }

    @Override
    public float floatValue()
    {
        return (float)value;
    }

    @Override
    public double doubleValue()
    {
        return value;
    }

    public static DerivationDouble COJAC_MAGIC_DOUBLE_getDerivation(DerivationDouble a)
    {
        return new DerivationDouble(a.dValue);
    }

    public static void COJAC_MAGIC_DOUBLE_specifieToDerivate(DerivationDouble a)
    {
        a.dValue = 1.0;
    }
}
