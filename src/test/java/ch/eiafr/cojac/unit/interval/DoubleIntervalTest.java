package ch.eiafr.cojac.unit.interval;

import ch.eiafr.cojac.interval.DoubleInterval;
import java.util.Random;
import org.junit.Test;

import static ch.eiafr.cojac.unit.interval.DoubleUtils.getBiggerRndDouble;
import static ch.eiafr.cojac.unit.interval.DoubleUtils.rndDouble;
import static java.lang.Double.*;
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
    private int nbrLoopExecution = 1000;

    @Test
    public void testIsIn() throws Exception
    {
        double v1 = rndDouble();
        double v2 = getBiggerRndDouble(v1);
        DoubleInterval a = new DoubleInterval(v1,v2);

        assertTrue(String.format("Test v1 E a : %f E %s",v1,a),DoubleInterval.isIn(a,v1));
        assertTrue(String.format("Test v2 E a : %f E %s",v2,a),DoubleInterval.isIn(a,v2));

        double v3 = getBiggerRndDouble(v2);
        a = new DoubleInterval(v1,v3);
        assertTrue(String.format("Test v2 E a : %f E %s",v2,a),DoubleInterval.isIn(a,v2));

        // Test special number
        // NaN
        DoubleInterval b = new DoubleInterval(0.0, NaN);
        assertTrue(String.format("Test nan interval : %s", b), b.isNan());

        // positive and negative infinity
        b = new DoubleInterval(NEGATIVE_INFINITY, POSITIVE_INFINITY);
        for(int i=0; i<nbrLoopExecution; i++)
        {
            DoubleInterval c = new DoubleInterval(-rndDouble(), rndDouble());
            assertTrue(String.format("c is in b : %s isIn %s",c,b),DoubleInterval.isIn(b,c));
        }

        // max value... same has up (positive and negative infinity)
        b = new DoubleInterval(-MAX_VALUE,MAX_VALUE);
        for(int i=0; i<nbrLoopExecution; i++)
        {
            DoubleInterval c = new DoubleInterval(-rndDouble(), rndDouble());
            assertTrue(String.format("c is in b : %s isIn %s",c,b),DoubleInterval.isIn(b,c));
        }

        // nothing special with min value
    }

    @Test
    public void testCompareTo() throws Exception
    {
        // Simple compare with non inclusive interval
        double v1 = rndDouble();
        double v2 = getBiggerRndDouble(v1);
        DoubleInterval a = new DoubleInterval(v1, v2);
        v1 = getBiggerRndDouble(v2);
        v2 = getBiggerRndDouble(v1);
        DoubleInterval b = new DoubleInterval(v1, v2);

        assertTrue(String.format("a > b  : %s > %s", a, b), a.compareTo(b) < 0);
        assertTrue(String.format("b < a  : %s < %s", b, a), b.compareTo(a) > 0);
        assertTrue(String.format("b == b : %s == %s", b, b), b.compareTo(b) == 0);

        // Same but negative
        v1 = - rndDouble();
        v2 = DoubleUtils.getSmallerRndNegativeDouble(v1);
        a = new DoubleInterval(v1,v2);
        v1 = DoubleUtils.getSmallerRndNegativeDouble(v2);
        v2 = DoubleUtils.getSmallerRndNegativeDouble(v1);
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
        a = new DoubleInterval(rndDouble());
        assertTrue(String.format("Test a == a : %s == %s",a,a),a.compareTo(a) == 0);

        // Test special number
        a = new DoubleInterval(NaN, rndDouble());
        v1 = rndDouble();
        v2 = getBiggerRndDouble(v1);
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
        a = new DoubleInterval(NaN,NaN);
        double v1 = rndDouble();
        double v2 = getBiggerRndDouble(v1);
        b = new DoubleInterval(v1,v2);
        assertFalse(String.format("Test NaN : %s <=> %s",a,b),a.strictCompareTo(b));
        assertFalse(String.format("Test NaN : %s <=> %s",b,a),b.strictCompareTo(a));
    }

    @Test
    public void testCompareToDouble() throws Exception
    {
        // Simple compare with a double...
        double v1 = rndDouble();
        double v2 = getBiggerRndDouble(v1);
        DoubleInterval a = new DoubleInterval(v1,v2);

        double v3 = DoubleUtils.getSmallerRndDouble(v1);
        assertTrue(String.format("Test v3 > a : %f > %s",v3,a),a.compareTo(v3) == 1);
        v3 = getBiggerRndDouble(v2);
        assertTrue(String.format("Test v3 < a : %f < %s",v3,a),a.compareTo(v3) == -1);
        v3 = (v1/2.0 + v2/2.0); // if we do (v1 + v2) / 2, there could be overflow...
        assertTrue(String.format("Test v3 == a : %f == %s",v3,a),a.compareTo(v3) == 0);

        // NaN
        a = new DoubleInterval(NaN, rndDouble());
        v1 = rndDouble();
        assertTrue(String.format("Test a <=> v1 : %s == %f",a,v1),a.compareTo(v1) == -1);
        v1 = NaN;
        assertTrue(String.format("Test a <=> v1 : %s == %f",a,v1),a.compareTo(v1) == 0);
        a = new DoubleInterval(-rndDouble(), rndDouble());
        assertTrue(String.format("Test a <=> v1 : %s == %f",a,v1),a.compareTo(v1) == 1);

        // Positive infinity and negative infinity
        a = new DoubleInterval(NEGATIVE_INFINITY,POSITIVE_INFINITY);
        assertTrue(String.format("Test neg <=> [neg,pos]  : %s == %s",NEGATIVE_INFINITY,a),a.compareTo(NEGATIVE_INFINITY) == 0);
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
        DoubleInterval cd = DoubleInterval.add(a,d); testIntervalBounds(cd);
        DoubleInterval dc = DoubleInterval.add(d,c); testIntervalBounds(dc);

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
        a = new DoubleInterval(NaN, rndDouble());
        b = new DoubleInterval(-rndDouble(), rndDouble());
        ab = DoubleInterval.add(a,b); testIntervalBounds(ab);
        assertTrue(String.format("Test isNan ab : %s)", ab), ab.isNan());
        ba = DoubleInterval.add(b,a); testIntervalBounds(ba);
        assertTrue(String.format("Test isNan ba : %s)",ba),ba.isNan());

        // Todo : test with max min neg ans pos if needed
        // 0.0 not interesting
    }

    @Test
    public void testSub() throws Exception
    {
        DoubleInterval a = new DoubleInterval(3.0,5.9);
        DoubleInterval b = new DoubleInterval(0.0,12.0);
        DoubleInterval c = new DoubleInterval(-23.5,-7.8);
        DoubleInterval d = new DoubleInterval(-10.0,10.0);

        DoubleInterval aa = DoubleInterval.sub(a,a); testIntervalBounds(aa);
        DoubleInterval bb = DoubleInterval.sub(b,b); testIntervalBounds(bb);
        DoubleInterval cc = DoubleInterval.sub(c,c); testIntervalBounds(cc);
        DoubleInterval dd = DoubleInterval.sub(d,d); testIntervalBounds(dd);

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
        DoubleInterval nan = new DoubleInterval(NaN);
        DoubleInterval something = new DoubleInterval(-rndDouble());
        DoubleInterval res = DoubleInterval.sub(nan,something);
        assertTrue(String.format("Test anything - NaN (or inverse) : %s - %s",nan,something),res.isNan());
    }

    @Test
    public void testMul() throws Exception
    {
        DoubleInterval a = new DoubleInterval(6.0,15.0);
        DoubleInterval b = new DoubleInterval(-13.5,12.0);
        DoubleInterval c = new DoubleInterval(-20.0,-8.0);

        // a * b = [-81.0;180.0]
        DoubleInterval abRes = new DoubleInterval(-81.0,180.0);
        DoubleInterval ab = DoubleInterval.mul(a,b); testIntervalBounds(ab);
        assertTrue(String.format("Test abRes E (a * b) : %s E (%s * %s)",abRes,a,b),DoubleInterval.isIn(ab,abRes));

        // a * c = [-300.0;-48.0]
        DoubleInterval acRes = new DoubleInterval(-300.0,-48.0);
        DoubleInterval ac = DoubleInterval.mul(a,c); testIntervalBounds(ac);
        assertTrue(String.format("Test abRes E (a * c) : %s E (%s * %s)",acRes,a,c),DoubleInterval.isIn(ac,acRes));

        // b * c = [-240;270.0]
        DoubleInterval bcRes = new DoubleInterval(-240.0,270.0);
        DoubleInterval bc = DoubleInterval.mul(b,c); testIntervalBounds(bc);
        assertTrue(String.format("Test abRes E (b * c) : %s E (%s * %s)",bcRes,b,c),DoubleInterval.isIn(bc,bcRes));

        // test a * b == b * a
        DoubleInterval ba = DoubleInterval.mul(b,a);
        assertTrue(String.format("Test a*b == b*a : %s == %s",ab,ba),ab.strictCompareTo(ba));

        // test NaN
        DoubleInterval nan = new DoubleInterval(NaN);
        DoubleInterval something = new DoubleInterval(-rndDouble());
        DoubleInterval res = DoubleInterval.mul(nan, something);
        assertTrue(String.format("Test anything - NaN (or inverse) : %s - %s",nan,something),res.isNan());
    }

    @Test
    public void testDiv() throws Exception
    {
        DoubleInterval a = new DoubleInterval(10.0); testIntervalBounds(a);
        DoubleInterval b = new DoubleInterval(3.0); testIntervalBounds(b);

        // Res of a / b = [3.33;3.33]
        DoubleInterval ab = DoubleInterval.div(a,b);
        DoubleInterval abRes = new DoubleInterval(10.0/3.0);

        assertTrue(String.format("Test [3.33;3.33] E a/b : %s E %s",abRes,ab),DoubleInterval.isIn(ab,abRes));

        // Test that's a/b * b/a == 1 --> 1 E a/b * b/a
        DoubleInterval abba = DoubleInterval.mul(DoubleInterval.div(a, b), DoubleInterval.div(b, a));
        assertTrue(String.format("Test 1.0 E a/b : %f E %s", 1.0, abba), DoubleInterval.isIn(abba, 1.0));

        // Test 0 interval
        double v1;
        a = new DoubleInterval(-rndDouble(), rndDouble()); testIntervalBounds(a);
        b = new DoubleInterval(v1 = rndDouble(), getBiggerRndDouble(v1)); testIntervalBounds(b);
        ab = DoubleInterval.div(a,b);
        DoubleInterval ba = DoubleInterval.div(b,a);
        assertTrue(String.format("Test a/b !is NaN : %s", ab),!ab.isNan());
        assertTrue(String.format("Test b/a is NaN : %s", ba), ba.isNan());
    }

    @Test
    public void testPow2() throws Exception
    {
        DoubleInterval a = new DoubleInterval(2.0,3.0);
        DoubleInterval b = new DoubleInterval(-2.0,5.0);
        DoubleInterval c = new DoubleInterval(-10.0,-6.0);

        DoubleInterval aa = DoubleInterval.pow2(a);
        DoubleInterval bb = DoubleInterval.pow2(b);
        DoubleInterval cc = DoubleInterval.pow2(c);

        DoubleInterval aRes = new DoubleInterval(4.0,9.0);
        DoubleInterval bRes = new DoubleInterval(0.0,25.0);
        DoubleInterval cRes = new DoubleInterval(36.0,100.0);

        assertTrue(String.format("Test a^2 : %s^2 = %s", a, aRes), DoubleInterval.isIn(aa, aRes));
        assertTrue(String.format("Test b^2 : %s^2 = %s", b, bRes), DoubleInterval.isIn(bb, bRes));
        assertTrue(String.format("Test c^2 : %s^2 = %s", c, cRes), DoubleInterval.isIn(cc, cRes));

        // Test a*a ~= a^2
        // The width of the interval with the mul operation instead of
        // using the pow2 is bigger w(aa) <= w(aaMul)
        DoubleInterval aaMul = DoubleInterval.mul(a,a);
        DoubleInterval bbMul = DoubleInterval.mul(b,b);
        DoubleInterval ccMul = DoubleInterval.mul(c,c);

        assertTrue(String.format("Test a^2 isIn a*a : %s inIn %s",aa,aaMul),DoubleInterval.isIn(aaMul,aa));
        assertTrue(String.format("Test b^2 isIn b*b : %s inIn %s",bb,bbMul),DoubleInterval.isIn(bbMul,bb));
        assertTrue(String.format("Test c^2 isIn c*c : %s inIn %s",cc,ccMul),DoubleInterval.isIn(ccMul,cc));
    }

    @Test
    public void testPow() throws Exception
    {
        // Simple pow usage
        DoubleInterval a = new DoubleInterval(4.0,5.0);
        DoubleInterval b = new DoubleInterval(0.0,9.0);

        DoubleInterval a3 = DoubleInterval.pow(a,3.0);
        DoubleInterval b6 = DoubleInterval.pow(b,6.0);

        DoubleInterval aRes = new DoubleInterval(64.0,125.0);
        DoubleInterval bRes = new DoubleInterval(0.0,531441.0);

        assertTrue(String.format("Test a^3 : %s isIn %s", a3, aRes), DoubleInterval.isIn(a3, aRes));
        assertTrue(String.format("Test b^6 : %s isIn %s", b6, bRes), DoubleInterval.isIn(b6, bRes));

        // Test relation between pow2(a) and pow(a,2.0)
        DoubleInterval aPow2 = DoubleInterval.pow2(a);
        DoubleInterval aPow  = DoubleInterval.pow(a,2.0);

        assertTrue(String.format("Test pow2(a) == pow(a,2.0) : %s == %s", aPow2, aPow), aPow2.strictCompareTo(aPow));
    }

    @Test
    public void testPowWithInterval() throws Exception
    {
        // Simple pow usage
        DoubleInterval a = new DoubleInterval(0.0,5.0);
        DoubleInterval b = new DoubleInterval(3.0,7.0);

        DoubleInterval ab = DoubleInterval.pow(a,b);
        DoubleInterval ba = DoubleInterval.pow(b,a);

        DoubleInterval abRes = new DoubleInterval(0.0,78125.0);
        DoubleInterval baRes = new DoubleInterval(1.0,16807.0);


        assertTrue(String.format("Test a^b : %s^%s isIn %s", a,b, abRes), DoubleInterval.isIn(ab, abRes));
        assertTrue(String.format("Test b^a : %s^%s isIn %s", b,a, baRes), DoubleInterval.isIn(ba, baRes));
    }

    @Test
    public void testExp() throws Exception
    {
        DoubleInterval a = new DoubleInterval(0.0,4.5);
        DoubleInterval b = new DoubleInterval(-4.5,9.0);

        DoubleInterval aExp = DoubleInterval.exp(a);
        DoubleInterval bExp = DoubleInterval.exp(b);

        DoubleInterval aRes = new DoubleInterval(1.0,90.01713130052181);
        DoubleInterval bRes = new DoubleInterval(0.011108996538242306,8103.083927575384);

        assertTrue(String.format("Test exp(a) : %s isIn %s", aRes,aExp), DoubleInterval.isIn(aExp,aRes));
        assertTrue(String.format("Test exp(b) : %s isIn %s", bRes,bExp), DoubleInterval.isIn(bExp,bRes));
    }

    @Test
    public void testLog() throws Exception
    {
        DoubleInterval a = new DoubleInterval(4.0,9.0);
        DoubleInterval b = new DoubleInterval(0.00004,0.00005);

        DoubleInterval aLog = DoubleInterval.log(a);
        DoubleInterval bLog = DoubleInterval.log(b);

        DoubleInterval aRes = new DoubleInterval(1.3862943611198906,2.1972245773362196);
        DoubleInterval bRes = new DoubleInterval(-10.126631103850338,-9.903487552536127);

        assertTrue(String.format("Test log(a) : %s isIn %s", aRes,aLog), DoubleInterval.isIn(aLog,aRes));
        assertTrue(String.format("Test log(b) : %s isIn %s", bRes,bLog), DoubleInterval.isIn(bLog,bRes));
    }

    @Test
    public void testLog10() throws Exception
    {
        DoubleInterval a = new DoubleInterval(2.0,10.0);
        DoubleInterval b = new DoubleInterval(0.00004,0.00005);

        DoubleInterval aLog = DoubleInterval.log10(a);
        DoubleInterval bLog = DoubleInterval.log10(b);

        DoubleInterval aRes = new DoubleInterval(0.3010299956639812,1.0);
        DoubleInterval bRes = new DoubleInterval(-4.3979400086720375,-4.301029995663981);

        assertTrue(String.format("Test log(a) : %s isIn %s", aRes,aLog), DoubleInterval.isIn(aLog,aRes));
        assertTrue(String.format("Test log(b) : %s isIn %s", bRes,bLog), DoubleInterval.isIn(bLog,bRes));
    }

    @Test
    public void testSin() throws Exception
    {
        double v1,v2,min = 10.0,max = -10.0;
        double itr,itrSin,step = 0.001;
        DoubleInterval a,aRes;
        Random r = new Random();

        for(int i=0; i<nbrLoopExecution; i++)
        {
            v1 = r.nextDouble() * 20.0; v1 = r.nextBoolean() ? v1:-v1;
            v2 = r.nextDouble() * 20.0; v2 = r.nextBoolean() ? v2:-v2;
            if(v1 < v2)
            {
                a = new DoubleInterval(v1,v2);
            }
            else
            {
                a = new DoubleInterval(v2,v1);
            }
            aRes = DoubleInterval.sin(a);
            itr = a.inf + step; // if we start at itr = a.inf, it's failing...
            while(itr < a.sup)
            {
                itrSin = Math.sin(itr);
                if(itrSin < min)
                {
                    min = itrSin;
                }
                if(itrSin > max)
                {
                    max = itrSin;
                }
                assertTrue(String.format("sin(%s) is in %s", itr, aRes),itrSin <= aRes.sup && itrSin >= aRes.inf);
                itr+=step;
            }
            assertTrue("Test min : ",min + step > aRes.inf || min - step < aRes.inf);
            assertTrue("Test max : ",max + step > aRes.sup || max - step < aRes.sup);
        }
    }

    @Test
    public void testCos() throws Exception
    {
        double v1,v2,min = 20.0,max = -20.0;
        double itr,itrCos,step = 0.001;
        DoubleInterval a,aRes;
        Random r = new Random();

        for(int i=0; i<nbrLoopExecution; i++)
        {
            v1 = r.nextDouble() * 20.0; v1 = r.nextBoolean() ? v1:-v1;
            v2 = r.nextDouble() * 20.0; v2 = r.nextBoolean() ? v2:-v2;
            if(v1 < v2)
            {
                a = new DoubleInterval(v1,v2);
            }
            else
            {
                a = new DoubleInterval(v2,v1);
            }
            aRes = DoubleInterval.cos(a);
            itr = a.inf + step; // if we start at itr = a.inf, it's failing...
            while(itr < a.sup)
            {
                itrCos = Math.cos(itr);
                if(itrCos < min)
                {
                    min = itrCos;
                }
                if(itrCos > max)
                {
                    max = itrCos;
                }
                assertTrue(String.format("cos(%s) is in %s", itr, aRes),itrCos <= aRes.sup && itrCos >= aRes.inf);
                itr+=step;
            }
            assertTrue("Test min : ",min + step > aRes.inf || min - step < aRes.inf);
            assertTrue("Test max : ",max + step > aRes.sup || max - step < aRes.sup);
        }
    }

    private void testIntervalBounds(DoubleInterval a) throws Exception
    {
        assertTrue(String.format("DoubleInterval %s is degenerated",a),a.testBounds());
    }
}
