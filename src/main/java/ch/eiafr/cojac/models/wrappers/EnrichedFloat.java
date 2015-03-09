package ch.eiafr.cojac.models.wrappers;

/**
 * Created by Snipy on 07.03.15.
 */
public class EnrichedFloat extends Number
{
    private float value;
    //private RealInterval interval;

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
        return value;
    }

    @Override
    public double doubleValue()
    {
        return (double)value;
    }
}
