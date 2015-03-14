package ch.eiafr.cojac.unit.interval;

import ch.eiafr.cojac.interval.DoubleInterval;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DoubleIntervalTest
{
    private double nul = 0.0;
    private double nan = Double.NaN;
    private double max = Double.MAX_VALUE;
    private double min = Double.MIN_VALUE;
    private double pos = Double.POSITIVE_INFINITY;
    private double neg = Double.NEGATIVE_INFINITY;

    private int nbrLoopExecution = 1000;

    @Test
    public void testIsIn() throws Exception
    {
        double v1 = DoubleUtils.rDouble();
        double v2 = DoubleUtils.getBiggerDouble(v1);
        DoubleInterval a = new DoubleInterval(v1,v2);

        assertTrue(String.format("Test v1 E a : %f E %s",v1,a),DoubleInterval.isIn(a,v1));
        assertTrue(String.format("Test v2 E a : %f E %s",v2,a),DoubleInterval.isIn(a,v2));

        double v3 = DoubleUtils.getBiggerDouble(v2);
        a = new DoubleInterval(v1,v3);
        assertTrue(String.format("Test v2 E a : %f E %s",v2,a),DoubleInterval.isIn(a,v2));

        // Test special number
        // NaN
        DoubleInterval b = new DoubleInterval(0.0,nan);
        assertTrue(String.format("Test nan interval : %s", b), b.isNan());

        // positive and negative infinity
        b = new DoubleInterval(neg,pos);
        for(int i=0; i<nbrLoopExecution; i++)
        {
            DoubleInterval c = new DoubleInterval(-DoubleUtils.rDouble(),DoubleUtils.rDouble());
            assertTrue(String.format("c is in b : %s isIn %s",c,b),DoubleInterval.isIn(b,c));
        }

        // max value... same has up (positive and negative infinity)
        b = new DoubleInterval(-max,max);
        for(int i=0; i<nbrLoopExecution; i++)
        {
            DoubleInterval c = new DoubleInterval(-DoubleUtils.rDouble(),DoubleUtils.rDouble());
            assertTrue(String.format("c is in b : %s isIn %s",c,b),DoubleInterval.isIn(b,c));
        }

        // nothing special with min value
    }

    @Test
    public void testCompareTo() throws Exception
    {
        // Simple compare with non inclusive interval
        double v1 = DoubleUtils.rDouble();
        double v2 = DoubleUtils.getBiggerDouble(v1);
        DoubleInterval a = new DoubleInterval(v1, v2);
        v1 = DoubleUtils.getBiggerDouble(v2);
        v2 = DoubleUtils.getBiggerDouble(v1);
        DoubleInterval b = new DoubleInterval(v1, v2);

        assertTrue(String.format("a > b  : %s > %s", a, b), a.compareTo(b) < 0);
        assertTrue(String.format("b < a  : %s < %s", b, a), b.compareTo(a) > 0);
        assertTrue(String.format("b == b : %s == %s", b, b), b.compareTo(b) == 0);

        // Same but negative
        v1 = - DoubleUtils.rDouble();
        v2 = DoubleUtils.getSmallerNegativeDouble(v1);
        a = new DoubleInterval(v1,v2);
        v1 = DoubleUtils.getSmallerNegativeDouble(v2);
        v2 = DoubleUtils.getSmallerNegativeDouble(v1);
        b = new DoubleInterval(v1,v2);

        assertTrue(String.format("a < b  : %s < %s", a, b), a.compareTo(b) < 0);
        assertTrue(String.format("b > a  : %s > %s", b, a), b.compareTo(a) > 0);
        assertTrue(String.format("b == b : %s == %s", b, b), b.compareTo(b) == 0);

        // Compare some interval with shared region
        a = new DoubleInterval(3.0,6.0);
        b = new DoubleInterval(2.0,8.0);

        assertTrue(String.format("Test a > b : %s > %s",a,b),a.compareTo(b) == 0);
        assertTrue(String.format("Test a < b : %s < %s",a,b),b.compareTo(a) == 0);

        // Same interval...
        a = new DoubleInterval(DoubleUtils.rDouble());
        assertTrue(String.format("Test a == a : %s == %s",a,a),a.compareTo(a) == 0);

        // Test special number
        a = new DoubleInterval(Double.NaN,DoubleUtils.rDouble());
        v1 = DoubleUtils.rDouble();
        v2 = DoubleUtils.getBiggerDouble(v1);
        b = new DoubleInterval(v1,v2);
        assertTrue(String.format("Test a <=> b : %s <=> %s",a,b),a.compareTo(b) == -1);

        // nothing special with infinity, min or max value
    }

    @Test
    public void testStrictCompareTo() throws Exception
    {
        DoubleInterval a = new DoubleInterval(0.0);
        DoubleInterval b = new DoubleInterval(0.0,0.0);
        assertTrue(String.format("Test a == b : %s == %s",a,b),a.strictCompareTo(b));
        assertTrue(String.format("Test b == a : %s == %s",b,a),b.strictCompareTo(a));

        // Some special operation with the interval !
        DoubleInterval c = DoubleInterval.add(a,a);
        assertFalse(String.format("Test c != a : %s != %s (??? : [0.0;0.0] + [0.0;0.0] != [0.0;0.0])", c, a), c.strictCompareTo(a));

        // NaN
        a = new DoubleInterval(nan,nan);
        double v1 = DoubleUtils.rDouble();
        double v2 = DoubleUtils.getBiggerDouble(v1);
        b = new DoubleInterval(v1,v2);
        assertFalse(String.format("Test NaN : %s <=> %s",a,b),a.strictCompareTo(b));
        assertFalse(String.format("Test NaN : %s <=> %s",b,a),b.strictCompareTo(a));
    }

    @Test
    public void testCompareToDouble() throws Exception
    {
        // Simple compare with a double...
        double v1 = DoubleUtils.rDouble();
        double v2 = DoubleUtils.getBiggerDouble(v1);
        DoubleInterval a = new DoubleInterval(v1,v2);

        double v3 = DoubleUtils.getSmallerDouble(v1);
        assertTrue(String.format("Test v3 > a : %f > %s",v3,a),a.compareTo(v3) == 1);
        v3 = DoubleUtils.getBiggerDouble(v2);
        assertTrue(String.format("Test v3 < a : %f < %s",v3,a),a.compareTo(v3) == -1);
        v3 = (v1 + v2) / 2.0;
        assertTrue(String.format("Test v3 == a : %f == %s",v3,a),a.compareTo(v3) == 0);

        // NaN
        a = new DoubleInterval(nan,DoubleUtils.rDouble());
        v1 = DoubleUtils.rDouble();
        assertTrue(String.format("Test a <=> v1 : %s == %f",a,v1),a.compareTo(v1) == -1);
        v1 = nan;
        assertTrue(String.format("Test a <=> v1 : %s == %f",a,v1),a.compareTo(v1) == 0);
        a = new DoubleInterval(-DoubleUtils.rDouble(),DoubleUtils.rDouble());
        assertTrue(String.format("Test a <=> v1 : %s == %f",a,v1),a.compareTo(v1) == 1);

        // Positive infinity and negative infinity
        a = new DoubleInterval(neg,pos);
        assertTrue(String.format("Test neg <=> [neg,pos]  : %s == %s",neg,a),a.compareTo(neg) == 0);
    }

    @Test
    public void testAdd() throws Exception
    {
        // Test some simple interval addition (positive, negative, both...)
        DoubleInterval a = new DoubleInterval(0.0,4.0);
        DoubleInterval b = new DoubleInterval(5.5,8.0);
        DoubleInterval c = new DoubleInterval(-3.0,-2.0);
        DoubleInterval d = new DoubleInterval(-8.0, 16.0);

        DoubleInterval ab = DoubleInterval.add(a,b); testIntervalBounds(ab);
        DoubleInterval ba = DoubleInterval.add(a,b); testIntervalBounds(ba);
        DoubleInterval ac = DoubleInterval.add(a,c); testIntervalBounds(ac);
        DoubleInterval ad = DoubleInterval.add(a,c); testIntervalBounds(ad);
        DoubleInterval cd = DoubleInterval.add(a,c); testIntervalBounds(cd);
        DoubleInterval dc = DoubleInterval.add(a,c); testIntervalBounds(dc);
        assertTrue(String.format("Test a+b >= a : %s >= %s",ab,a),ab.compareTo(a) >= 0);
        assertTrue(String.format("Test b+a >= b : %s >= %s",ba,b),ba.compareTo(b) >= 0);
        assertTrue(String.format("Test a+c >= c : %s >= %s",ac,c),ac.compareTo(c) >= 0);
        assertFalse(String.format("Test ! a+d < a : %s < %s", ad, a), ad.compareTo(a) < 0);
        assertTrue(String.format("Test c + d == d + c : %s == %s", cd, dc), cd.compareTo(dc) == 0);
        assertTrue(String.format("Test d + c == c + d : %s == %s",dc,cd),dc.compareTo(cd) == 0);

        DoubleInterval e = new DoubleInterval(0.0);
        e = DoubleInterval.add(e,e); testIntervalBounds(e);
        assertTrue(String.format("Test 0.0 E e : %f isiN %s", 0.0, e), DoubleInterval.isIn(e, 0.0));
        assertTrue(String.format("Test e E e : %s isiN %s",e,e),DoubleInterval.isIn(e,e));
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

    private void testIntervalBounds(DoubleInterval a) throws Exception
    {
        if(!a.testBounds())
        {
            throw new Exception(String.format("DoubleInterval %s is degenerated",a));
        }
    }
}
