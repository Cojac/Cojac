package ch.eiafr.cojac.unit.interval;

import ch.eiafr.cojac.interval.DoubleInterval;
import org.junit.Test;

import static ch.eiafr.cojac.unit.interval.DoubleUtils.getBiggerDouble;
import static ch.eiafr.cojac.unit.interval.DoubleUtils.rDouble;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *  @version 0.1
 *  <p>
 *      Note : maybe implements test with some special value like MAX_VALUE...
 *  </p>
 */
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
        double v1 = rDouble();
        double v2 = getBiggerDouble(v1);
        DoubleInterval a = new DoubleInterval(v1,v2);

        assertTrue(String.format("Test v1 E a : %f E %s",v1,a),DoubleInterval.isIn(a,v1));
        assertTrue(String.format("Test v2 E a : %f E %s",v2,a),DoubleInterval.isIn(a,v2));

        double v3 = getBiggerDouble(v2);
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
            DoubleInterval c = new DoubleInterval(-rDouble(), rDouble());
            assertTrue(String.format("c is in b : %s isIn %s",c,b),DoubleInterval.isIn(b,c));
        }

        // max value... same has up (positive and negative infinity)
        b = new DoubleInterval(-max,max);
        for(int i=0; i<nbrLoopExecution; i++)
        {
            DoubleInterval c = new DoubleInterval(-rDouble(), rDouble());
            assertTrue(String.format("c is in b : %s isIn %s",c,b),DoubleInterval.isIn(b,c));
        }

        // nothing special with min value
    }

    @Test
    public void testCompareTo() throws Exception
    {
        // Simple compare with non inclusive interval
        double v1 = rDouble();
        double v2 = getBiggerDouble(v1);
        DoubleInterval a = new DoubleInterval(v1, v2);
        v1 = getBiggerDouble(v2);
        v2 = getBiggerDouble(v1);
        DoubleInterval b = new DoubleInterval(v1, v2);

        assertTrue(String.format("a > b  : %s > %s", a, b), a.compareTo(b) < 0);
        assertTrue(String.format("b < a  : %s < %s", b, a), b.compareTo(a) > 0);
        assertTrue(String.format("b == b : %s == %s", b, b), b.compareTo(b) == 0);

        // Same but negative
        v1 = - rDouble();
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
        a = new DoubleInterval(rDouble());
        assertTrue(String.format("Test a == a : %s == %s",a,a),a.compareTo(a) == 0);

        // Test special number
        a = new DoubleInterval(Double.NaN, rDouble());
        v1 = rDouble();
        v2 = getBiggerDouble(v1);
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
        double v1 = rDouble();
        double v2 = getBiggerDouble(v1);
        b = new DoubleInterval(v1,v2);
        assertFalse(String.format("Test NaN : %s <=> %s",a,b),a.strictCompareTo(b));
        assertFalse(String.format("Test NaN : %s <=> %s",b,a),b.strictCompareTo(a));
    }

    @Test
    public void testCompareToDouble() throws Exception
    {
        // Simple compare with a double...
        double v1 = rDouble();
        double v2 = getBiggerDouble(v1);
        DoubleInterval a = new DoubleInterval(v1,v2);

        double v3 = DoubleUtils.getSmallerDouble(v1);
        assertTrue(String.format("Test v3 > a : %f > %s",v3,a),a.compareTo(v3) == 1);
        v3 = getBiggerDouble(v2);
        assertTrue(String.format("Test v3 < a : %f < %s",v3,a),a.compareTo(v3) == -1);
        v3 = (v1 + v2) / 2.0;
        assertTrue(String.format("Test v3 == a : %f == %s",v3,a),a.compareTo(v3) == 0);

        // NaN
        a = new DoubleInterval(nan, rDouble());
        v1 = rDouble();
        assertTrue(String.format("Test a <=> v1 : %s == %f",a,v1),a.compareTo(v1) == -1);
        v1 = nan;
        assertTrue(String.format("Test a <=> v1 : %s == %f",a,v1),a.compareTo(v1) == 0);
        a = new DoubleInterval(-rDouble(), rDouble());
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

        // Test bounds
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

        DoubleInterval abCorrect = new DoubleInterval(5.5,12.0);
        assertTrue(String.format("Test %s E %s",abCorrect,ab),DoubleInterval.isIn(ab,abCorrect));

        DoubleInterval e = new DoubleInterval(0.0);
        e = DoubleInterval.add(e,e); testIntervalBounds(e);
        assertTrue(String.format("Test 0.0 E e : %f isiN %s", 0.0, e), DoubleInterval.isIn(e, 0.0));
        assertTrue(String.format("Test e E e : %s isiN %s",e,e),DoubleInterval.isIn(e,e));

        // Test special number
        // NaN
        a = new DoubleInterval(nan, rDouble());
        b = new DoubleInterval(-rDouble(),rDouble());
        ab = DoubleInterval.add(a,b); testIntervalBounds(ab);
        assertTrue(String.format("Test isNan ab : %s)", ab), ab.isNan());
        ba = DoubleInterval.add(b,a); testIntervalBounds(ba);
        assertTrue(String.format("Test isNan ba : %s)",ba),ba.isNan());

        // Todo : test with max min neg ans pos if needed
        // 0.0 not interresting
    }

    @Test
    public void testSub() throws Exception
    {
        DoubleInterval a = new DoubleInterval(3.0,5.9);
        DoubleInterval b = new DoubleInterval(0.0,12.0);
        DoubleInterval c = new DoubleInterval(-23.5,-7.8);
        DoubleInterval d = new DoubleInterval(-10.0,10.0);

        DoubleInterval aa = DoubleInterval.sub(a,a);
        DoubleInterval bb = DoubleInterval.sub(b,b);
        DoubleInterval cc = DoubleInterval.sub(c,c);
        DoubleInterval dd = DoubleInterval.sub(d,d);

        assertTrue(String.format("Test 0 E (a - a) : %f E (%s - %s)",0.0,a,a),DoubleInterval.isIn(aa,0));
        assertTrue(String.format("Test 0 E (b - b) : %f E (%s - %s)", 0.0, b, b), DoubleInterval.isIn(bb, 0));
        assertTrue(String.format("Test 0 E (c - c) : %f E (%s - %s)",0.0,c,c),DoubleInterval.isIn(cc,0));
        assertTrue(String.format("Test 0 E (d - d) : %f E (%s - %s)",0.0,d,d),DoubleInterval.isIn(dd,0));

        DoubleInterval ab = DoubleInterval.sub(a,b);
        DoubleInterval ba = DoubleInterval.sub(b,a);

        // a - b = [-9.0;-5.9]
        DoubleInterval abRes = new DoubleInterval(-9.0,-5.9);
        assertTrue(String.format("Test [-9.0;-5.9] E (a - b) : %s E (%s - %s)",abRes,a,b),DoubleInterval.isIn(ab,abRes));

        // b - a = [-5.9;9.0]
        DoubleInterval baRes = new DoubleInterval(-5.9,9.0);
        assertTrue(String.format("Test [-9.0;-5.9] E (a - b) : %s E (%s - %s)",baRes,b,a),DoubleInterval.isIn(ba,baRes));

        // Test ab == -ba
        assertTrue(String.format("Test ab == ba : %s == %s",abRes,baRes),ab.strictCompareTo(DoubleInterval.neg(ba)));

        // Test NaN
        DoubleInterval nan = new DoubleInterval(this.nan);
        DoubleInterval something = new DoubleInterval(-rDouble());
        DoubleInterval res = DoubleInterval.sub(nan,something);
        assertTrue(String.format("Test anything - NaN (or inverse) : %s - %s",nan,something),res.isNan());
    }

    @Test
    public void testMul() throws Exception
    {
        DoubleInterval a = new DoubleInterval(6.0,15.0);
        DoubleInterval b = new DoubleInterval(-13.5,12.0);
        DoubleInterval c = new DoubleInterval(-20.0,-8.0);
        DoubleInterval d = new DoubleInterval(nul,nul);

        // a * b = [-81.0;180.0]
        DoubleInterval abRes = new DoubleInterval(-81.0,180.0);
        DoubleInterval ab = DoubleInterval.mul(a,b);
        assertTrue(String.format("Test abRes E (a * b) : %s E (%s * %s)",abRes,a,b),DoubleInterval.isIn(ab,abRes));

        // a * c = [-300.0;-48.0]
        DoubleInterval acRes = new DoubleInterval(-300.0,-48.0);
        DoubleInterval ac = DoubleInterval.mul(a,c);
        assertTrue(String.format("Test abRes E (a * c) : %s E (%s * %s)",acRes,a,c),DoubleInterval.isIn(ac,acRes));

        // b * c = [-240;270.0]
        DoubleInterval bcRes = new DoubleInterval(-240.0,270.0);
        DoubleInterval bc = DoubleInterval.mul(b,c);
        assertTrue(String.format("Test abRes E (b * c) : %s E (%s * %s)",bcRes,b,c),DoubleInterval.isIn(bc,bcRes));

        // test a * b == b * a
        DoubleInterval ba = DoubleInterval.mul(b,a);
        assertTrue(String.format("Test a*b == b*a : %s == %s",ab,ba),ab.strictCompareTo(ba));

        // test NaN
        DoubleInterval nan = new DoubleInterval(this.nan);
        DoubleInterval something = new DoubleInterval(-rDouble());
        DoubleInterval res = DoubleInterval.mul(nan,something);
        assertTrue(String.format("Test anything - NaN (or inverse) : %s - %s",nan,something),res.isNan());
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
        assertTrue(String.format("DoubleInterval %s is degenerated",a),a.testBounds());
    }
}
