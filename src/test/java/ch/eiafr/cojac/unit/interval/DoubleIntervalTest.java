package ch.eiafr.cojac.unit.interval;

import ch.eiafr.cojac.interval.DoubleInterval;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DoubleIntervalTest
{

    @Test
    public void testCompareTo() throws Exception
    {
        double v1 = DoubleUtils.rDouble();
        double v2 = DoubleUtils.getBiggerDouble(v1);
        DoubleInterval a = new DoubleInterval(v1, v2);
        v1 = DoubleUtils.getBiggerDouble(v2);
        v2 = DoubleUtils.getBiggerDouble(v1);
        DoubleInterval b = new DoubleInterval(v1, v2);

        // Simple compare
        assertTrue(String.format("a > b  : %s > %s", a, b), a.compareTo(b) > 0);
        assertTrue(String.format("b < a  : %s < %s", b, a), b.compareTo(a) < 0);
        assertTrue(String.format("b == b : %s == %s", b, b), b.compareTo(b) == 0);
    }

    @Test
    public void testCompareTo1() throws Exception
    {

    }

    @Test
    public void testAdd() throws Exception
    {

    }

    @Test
    public void testSub() throws Exception
    {

    }

    @Test
    public void testMul() throws Exception
    {

    }

    @Test
    public void testDiv() throws Exception
    {

    }

    @Test
    public void testPow2() throws Exception
    {

    }

    @Test
    public void testPow() throws Exception
    {

    }

    @Test
    public void testPow1() throws Exception
    {

    }

    @Test
    public void testExp() throws Exception
    {

    }

    @Test
    public void testLog() throws Exception
    {

    }

    @Test
    public void testLog2() throws Exception
    {

    }

    @Test
    public void testLog10() throws Exception
    {

    }
}
