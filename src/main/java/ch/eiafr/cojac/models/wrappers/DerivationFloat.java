package ch.eiafr.cojac.models.wrappers;

public class DerivationFloat extends Number implements Comparable<DerivationFloat>
{
    protected DerivationDouble value;

    public DerivationFloat(float v)
    {
        value = new DerivationDouble((double)v);
    }

    public DerivationFloat(String v)
    {
        value = new DerivationDouble(v);
    }

    public DerivationFloat(DerivationFloat v)
    {
        value = new DerivationDouble(v.value);
    }

    public DerivationFloat(DerivationDouble v)
    {
        value = new DerivationDouble(v);
    }

    public DerivationFloat(double v)
    {
        this.value = new DerivationDouble(v);
    }

    public static DerivationFloat fromDouble(double v)
    {
        return new DerivationFloat(new DerivationDouble(v));
    }

    public static DerivationFloat fromString(String v)
    {
        return new DerivationFloat(v);
    }

    public static DerivationFloat fromFloat(float v)
    {
        return new DerivationFloat(v);
    }

    public static DerivationFloat fadd(DerivationFloat a, DerivationFloat b)
    {
        return new DerivationFloat(DerivationDouble.dadd(a.value,b.value));
    }

    public static DerivationFloat fdiv(DerivationFloat a, DerivationFloat b)
    {
        return new DerivationFloat(DerivationDouble.ddiv(a.value, b.value));
    }

    public static DerivationFloat frem(DerivationFloat a, DerivationFloat b)
    {
        return new DerivationFloat(DerivationDouble.drem(a.value, b.value));
    }

    public static DerivationFloat fsub(DerivationFloat a, DerivationFloat b)
    {
        return new DerivationFloat(DerivationDouble.dsub(a.value, b.value));
    }

    public static DerivationFloat fmul(DerivationFloat a, DerivationFloat b)
    {
        return new DerivationFloat(DerivationDouble.dmul(a.value, b.value));
    }

    public static int fcmpl(DerivationFloat a, DerivationFloat b)
    {
        return a.compareTo(b);
    }

    public static int fcmpg(DerivationFloat a, DerivationFloat b)
    {
        return a.compareTo(b);
    }

    public static DerivationFloat fneg(DerivationFloat a)
    {
        return new DerivationFloat(DerivationDouble.dneg(a.value));
    }

    public static int f2i(DerivationFloat a)
    {
        return (int)a.value.value;
    }

    public static long f2l(DerivationFloat a)
    {
        return (long)a.value.value;
    }

    public static DerivationDouble f2d(DerivationFloat a)
    {
        return new DerivationDouble((float)a.value.value);
    }

    public static DerivationFloat i2f(int a)
    {
        return new DerivationFloat((float)a);
    }

    public static DerivationFloat l2f(long a)
    {
        return new DerivationFloat((float)a);
    }

    public static DerivationFloat d2f(DerivationDouble a)
    {
        return new DerivationFloat(a.floatValue());
    }

    public static float toFloat(DerivationFloat a)
    {
        return (float)a.value.value;
    }

    public static Float toRealFloat(DerivationFloat a)
    {
        return new Float(a.value.value);
    }

    @Override
    public int compareTo(DerivationFloat o)
    {
        return value.compareTo(o.value);
    }

    @Override
    public int intValue()
    {
        return (int)value.value;
    }

    @Override
    public long longValue()
    {
        return (long)value.value;
    }

    @Override
    public float floatValue()
    {
        return (float)value.value;
    }

    @Override
    public double doubleValue()
    {
        return value.value;
    }

    public static DerivationDouble COJAC_MAGIC_FLOAT_getDerivation(DerivationFloat a)
    {
        return new DerivationDouble(a.value.dValue);
    }

    public static void COJAC_MAGIC_FLOAT_specifieToDerivate(DerivationFloat a)
    {
        a.value.dValue = 1.0;
    }
}
