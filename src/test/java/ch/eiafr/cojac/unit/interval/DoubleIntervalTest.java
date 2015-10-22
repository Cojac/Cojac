/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst & HEIA-FR students
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

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
    private int nbrLoopExecution = 500;
    private final double MAX_SUP = 20.0;
    private final Random rnd = new Random();
    private final double step = 0.1; // increment between two tested values for trigonometric operations

    @Test
    public void testIsIn() throws Exception {
        double v1 = rndDouble();
        double v2 = getBiggerRndDouble(v1);
        DoubleInterval a = new DoubleInterval(v1,v2);

        assertTrue(String.format("Test v1 E a : %f E %s",v1,a), a.contains(v1));
        assertTrue(String.format("Test v2 E a : %f E %s",v2,a), a.contains(v2));

        double v3 = getBiggerRndDouble(v2);
        a = new DoubleInterval(v1,v3);
        assertTrue(String.format("Test v2 E a : %f E %s",v2,a), a.contains(v2));

        // Test special number
        // NaN
        DoubleInterval b = new DoubleInterval(0.0, NaN);
        assertTrue(String.format("Test nan interval : %s", b), b.isNan());

        // positive and negative infinity
        b = new DoubleInterval(NEGATIVE_INFINITY, POSITIVE_INFINITY);
        for(int i=0; i<nbrLoopExecution; i++)
        {
            DoubleInterval c = new DoubleInterval(-rndDouble(), rndDouble());
            assertTrue(String.format("c is in b : %s isIn %s",c,b),b.contains(c));
        }

        // max value... same has up (positive and negative infinity)
        b = new DoubleInterval(-MAX_VALUE,MAX_VALUE);
        for(int i=0; i<nbrLoopExecution; i++)
        {
            DoubleInterval c = new DoubleInterval(-rndDouble(), rndDouble());
            assertTrue(String.format("c is in b : %s isIn %s",c,b), b.contains(c));
        }

        // nothing special with min value
    }

    @Test
    public void testCompareTo() throws Exception {
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
    public void testStrictCompareTo() throws Exception {
        DoubleInterval a = new DoubleInterval(0.0);
        DoubleInterval b = new DoubleInterval(0.0,0.0);
        assertTrue(String.format("Test a == b : %s == %s",a,b),a.strictlyEquals(b));
        assertTrue(String.format("Test b == a : %s == %s",b,a),b.strictlyEquals(a));

        // Some special operation with the interval !
        DoubleInterval c = DoubleInterval.add(a,a);
        assertFalse(String.format("Test c != a : %s != %s (??? : [0.0;0.0] + [0.0;0.0] != [0.0;0.0])", c, a), c.strictlyEquals(a));

        // NaN
        a = new DoubleInterval(NaN,NaN);
        double v1 = rndDouble();
        double v2 = getBiggerRndDouble(v1);
        b = new DoubleInterval(v1,v2);
        assertFalse(String.format("Test NaN : %s <=> %s",a,b),a.strictlyEquals(b));
        assertFalse(String.format("Test NaN : %s <=> %s", b, a), b.strictlyEquals(a));
    }

    @Test
    public void testCompareToDouble() throws Exception {
        // Simple compare with a double...
        double v1 = rndDouble();
        double v2 = getBiggerRndDouble(v1);
        DoubleInterval a = new DoubleInterval(v1,v2);

        double v3 = DoubleUtils.getSmallerRndDouble(v1);
        assertTrue(String.format("Test v3 > a : %f > %s",v3,a),a.compareTo(v3) == 1);
        v3 = getBiggerRndDouble(v2);
        assertTrue(String.format("Test v3 < a : %f < %s",v3,a),a.compareTo(v3) == -1);
        v3 = (v1/2.0 + v2/2.0); // if we do (v1 + v2) / 2, there could be overflow...
        assertTrue(String.format("Test v3 == a : %f == %s", v3, a),a.compareTo(v3) == 0);

        // NaN
        a = new DoubleInterval(NaN, rndDouble());
        v1 = rndDouble();
        assertTrue(String.format("Test a <=> v1 : %s == %f", a, v1),a.compareTo(v1) == -1);
        v1 = NaN;
        assertTrue(String.format("Test a <=> v1 : %s == %f",a,v1),a.compareTo(v1) == 0);
        a = new DoubleInterval(-rndDouble(), rndDouble());
        assertTrue(String.format("Test a <=> v1 : %s == %f",a,v1),a.compareTo(v1) == 1);

        // Positive infinity and negative infinity
        a = new DoubleInterval(NEGATIVE_INFINITY,POSITIVE_INFINITY);
        assertTrue(String.format("Test neg <=> [neg,pos]  : %s == %s", NEGATIVE_INFINITY, a), a.compareTo(NEGATIVE_INFINITY) == 0);
    }

    @Test
    public void testAdd() throws Exception {
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
        assertTrue(String.format("Test %s E %s",abCorrect,ab), ab.contains(abCorrect));

        DoubleInterval e = new DoubleInterval(0.0);
        e = DoubleInterval.add(e,e); testIntervalBounds(e);
        assertTrue(String.format("Test 0.0 E e : %f isiN %s", 0.0, e), e.contains(0.0));
        assertTrue(String.format("Test e E e : %s isiN %s",e,e), e.contains(e));

        // Test special number
        // NaN
        a = new DoubleInterval(NaN, rndDouble());
        b = new DoubleInterval(-rndDouble(), rndDouble());
        ab = DoubleInterval.add(a,b); testIntervalBounds(ab);
        assertTrue(String.format("Test isNan ab : %s)", ab), ab.isNan());
        ba = DoubleInterval.add(b, a); testIntervalBounds(ba);
        assertTrue(String.format("Test isNan ba : %s)",ba),ba.isNan());
    }

    @Test
    public void testSub() throws Exception {
        DoubleInterval a = new DoubleInterval(3.0,5.9);
        DoubleInterval b = new DoubleInterval(0.0,12.0);
        DoubleInterval c = new DoubleInterval(-23.5,-7.8);
        DoubleInterval d = new DoubleInterval(-10.0,10.0);

        DoubleInterval aa = DoubleInterval.sub(a,a); testIntervalBounds(aa);
        DoubleInterval bb = DoubleInterval.sub(b,b); testIntervalBounds(bb);
        DoubleInterval cc = DoubleInterval.sub(c,c); testIntervalBounds(cc);
        DoubleInterval dd = DoubleInterval.sub(d,d); testIntervalBounds(dd);

        assertTrue(String.format("Test 0 E (a - a) : %f E (%s - %s)",0.0,a,a), aa.contains(0));
        assertTrue(String.format("Test 0 E (b - b) : %f E (%s - %s)", 0.0, b, b), bb.contains(0));
        assertTrue(String.format("Test 0 E (c - c) : %f E (%s - %s)", 0.0, c, c), cc.contains(0));
        assertTrue(String.format("Test 0 E (d - d) : %f E (%s - %s)",0.0,d,d), dd.contains(0));

        DoubleInterval ab = DoubleInterval.sub(a,b);
        DoubleInterval ba = DoubleInterval.sub(b,a);

        // a - b = [-9.0;-5.9]
        DoubleInterval abRes = new DoubleInterval(-9.0,-5.9);
        assertTrue(String.format("Test [-9.0;-5.9] E (a - b) : %s E (%s - %s)",abRes,a,b), ab.contains(abRes));

        // b - a = [-5.9;9.0]
        DoubleInterval baRes = new DoubleInterval(-5.9,9.0);
        assertTrue(String.format("Test [-9.0;-5.9] E (a - b) : %s E (%s - %s)",baRes,b,a), ba.contains(baRes));

        // Test ab == -ba
        assertTrue(String.format("Test ab == ba : %s == %s", abRes, baRes), ab.strictlyEquals(DoubleInterval.neg(ba)));

        // Test NaN
        DoubleInterval nan = new DoubleInterval(NaN);
        DoubleInterval something = new DoubleInterval(-rndDouble());
        DoubleInterval res = DoubleInterval.sub(nan, something);
        assertTrue(String.format("Test anything - NaN (or inverse) : %s - %s",nan,something),res.isNan());
    }

    @Test
    public void testMul() throws Exception {
        DoubleInterval a = new DoubleInterval(6.0,15.0);
        DoubleInterval b = new DoubleInterval(-13.5,12.0);
        DoubleInterval c = new DoubleInterval(-20.0,-8.0);

        // a * b = [-81.0;180.0]
        DoubleInterval abRes = new DoubleInterval(-81.0,180.0);
        DoubleInterval ab = DoubleInterval.mul(a, b); testIntervalBounds(ab);
        assertTrue(String.format("Test abRes E (a * b) : %s E (%s * %s)",abRes,a,b), ab.contains(abRes));

        // a * c = [-300.0;-48.0]
        DoubleInterval acRes = new DoubleInterval(-300.0,-48.0);
        DoubleInterval ac = DoubleInterval.mul(a,c); testIntervalBounds(ac);
        assertTrue(String.format("Test abRes E (a * c) : %s E (%s * %s)",acRes,a,c), ac.contains(acRes));

        // b * c = [-240;270.0]
        DoubleInterval bcRes = new DoubleInterval(-240.0,270.0);
        DoubleInterval bc = DoubleInterval.mul(b,c); testIntervalBounds(bc);
        assertTrue(String.format("Test abRes E (b * c) : %s E (%s * %s)",bcRes,b,c), bc.contains(bcRes));

        // test a * b == b * a
        DoubleInterval ba = DoubleInterval.mul(b,a);
        assertTrue(String.format("Test a*b == b*a : %s == %s",ab,ba),ab.strictlyEquals(ba));

        // test NaN
        DoubleInterval nan = new DoubleInterval(NaN);
        DoubleInterval something = new DoubleInterval(-rndDouble());
        DoubleInterval res = DoubleInterval.mul(nan, something);
        assertTrue(String.format("Test anything - NaN (or inverse) : %s - %s",nan,something),res.isNan());
    }

    @Test
    public void testDiv() throws Exception {
        DoubleInterval a = new DoubleInterval(10.0); testIntervalBounds(a);
        DoubleInterval b = new DoubleInterval(3.0); testIntervalBounds(b);

        // Res of a / b = [3.33;3.33]
        DoubleInterval ab = DoubleInterval.div(a,b);
        DoubleInterval abRes = new DoubleInterval(10.0/3.0);

        assertTrue(String.format("Test [3.33;3.33] E a/b : %s E %s",abRes,ab), ab.contains(abRes));

        // Test that's a/b * b/a == 1 --> 1 E a/b * b/a
        DoubleInterval abba = DoubleInterval.mul(DoubleInterval.div(a, b), DoubleInterval.div(b, a));
        assertTrue(String.format("Test 1.0 E a/b : %f E %s", 1.0, abba),  abba.contains(1.0));

        // Test 0 interval
        double v1;
        a = new DoubleInterval(-rndDouble(), rndDouble()); testIntervalBounds(a);
        b = new DoubleInterval(v1 = rndDouble(), getBiggerRndDouble(v1)); testIntervalBounds(b);
        ab = DoubleInterval.div(a,b);
        DoubleInterval ba = DoubleInterval.div(b, a);
        assertTrue(String.format("Test a/b !is NaN : %s", ab),!ab.isNan());
        assertTrue(String.format("Test b/a is NaN : %s", ba), ba.isNan());
    }

    @Test
    public void testPow2() throws Exception {
        DoubleInterval a = new DoubleInterval(2.0,3.0);
        DoubleInterval b = new DoubleInterval(-2.0,5.0);
        DoubleInterval c = new DoubleInterval(-10.0,-6.0);

        DoubleInterval aa = DoubleInterval.pow2(a);
        DoubleInterval bb = DoubleInterval.pow2(b);
        DoubleInterval cc = DoubleInterval.pow2(c);

        DoubleInterval aRes = new DoubleInterval(4.0,9.0);
        DoubleInterval bRes = new DoubleInterval(0.0,25.0);
        DoubleInterval cRes = new DoubleInterval(36.0,100.0);

        assertTrue(String.format("Test a^2 : %s^2 = %s", a, aRes), aa.contains(aRes));
        assertTrue(String.format("Test b^2 : %s^2 = %s", b, bRes), bb.contains(bRes));
        assertTrue(String.format("Test c^2 : %s^2 = %s", c, cRes), cc.contains(cRes));

        // Test a*a ~= a^2
        // The width of the interval with the mul operation instead of
        // using the pow2 is bigger w(aa) <= w(aaMul)
        DoubleInterval aaMul = DoubleInterval.mul(a,a);
        DoubleInterval bbMul = DoubleInterval.mul(b,b);
        DoubleInterval ccMul = DoubleInterval.mul(c, c);

        assertTrue(String.format("Test a^2 isIn a*a : %s inIn %s",aa,aaMul), aaMul.contains(aa));
        assertTrue(String.format("Test b^2 isIn b*b : %s inIn %s",bb,bbMul), bbMul.contains(bb));
        assertTrue(String.format("Test c^2 isIn c*c : %s inIn %s",cc,ccMul), ccMul.contains(cc));
    }

    @Test
    public void testPow() throws Exception {
        // Simple pow usage
        DoubleInterval a = new DoubleInterval(4.0,5.0);
        DoubleInterval b = new DoubleInterval(0.0,9.0);

        DoubleInterval a3 = DoubleInterval.pow(a, 3.0);
        DoubleInterval b6 = DoubleInterval.pow(b, 6.0);

        DoubleInterval aRes = new DoubleInterval(64.0,125.0);
        DoubleInterval bRes = new DoubleInterval(0.0,531441.0);

        assertTrue(String.format("Test a^3 : %s isIn %s", a3, aRes), a3.contains(aRes));
        assertTrue(String.format("Test b^6 : %s isIn %s", b6, bRes), b6.contains(bRes));

        // Test relation between pow2(a) and pow(a,2.0)
        DoubleInterval aPow2 = DoubleInterval.pow2(a);
        DoubleInterval aPow  = DoubleInterval.pow(a,2.0);

        assertTrue(String.format("Test pow2(a) == pow(a,2.0) : %s == %s", aPow2, aPow), aPow2.strictlyEquals(aPow));
    }

    @Test
    public void testPowWithInterval() throws Exception {
        // Simple pow usage
        DoubleInterval a = new DoubleInterval(0.0,5.0);
        DoubleInterval b = new DoubleInterval(3.0,7.0);

        DoubleInterval ab = DoubleInterval.pow(a, b);
        DoubleInterval ba = DoubleInterval.pow(b, a);

        DoubleInterval abRes = new DoubleInterval(0.0,78125.0);
        DoubleInterval baRes = new DoubleInterval(1.0,16807.0);

        assertTrue(String.format("Test a^b : %s^%s isIn %s", a,b, abRes), ab.contains(abRes));
        assertTrue(String.format("Test b^a : %s^%s isIn %s", b,a, baRes), ba.contains(baRes));
    }

    @Test
    public void testExp() throws Exception {
        DoubleInterval a = new DoubleInterval(0.0,4.5);
        DoubleInterval b = new DoubleInterval(-4.5,9.0);

        DoubleInterval aExp = DoubleInterval.exp(a);
        DoubleInterval bExp = DoubleInterval.exp(b);

        DoubleInterval aRes = new DoubleInterval(1.0,90.01713130052181);
        DoubleInterval bRes = new DoubleInterval(0.011108996538242306,8103.083927575384);

        assertTrue(String.format("Test exp(a) : %s isIn %s", aRes,aExp), aExp.contains(aRes));
        assertTrue(String.format("Test exp(b) : %s isIn %s", bRes,bExp), bExp.contains(bRes));
    }

    @Test
    public void testLog() throws Exception {
        DoubleInterval a = new DoubleInterval(4.0,9.0);
        DoubleInterval b = new DoubleInterval(0.00004,0.00005);

        DoubleInterval aLog = DoubleInterval.log(a);
        DoubleInterval bLog = DoubleInterval.log(b);

        DoubleInterval aRes = new DoubleInterval(1.3862943611198906,2.1972245773362196);
        DoubleInterval bRes = new DoubleInterval(-10.126631103850338,-9.903487552536127);

        assertTrue(String.format("Test log(a) : %s isIn %s", aRes,aLog), aLog.contains(aRes));
        assertTrue(String.format("Test log(b) : %s isIn %s", bRes,bLog), bLog.contains(bRes));
    }

    @Test
    public void testLog10() throws Exception {
        DoubleInterval a = new DoubleInterval(2.0,10.0);
        DoubleInterval b = new DoubleInterval(0.00004,0.00005);

        DoubleInterval aLog = DoubleInterval.log10(a);
        DoubleInterval bLog = DoubleInterval.log10(b);

        DoubleInterval aRes = new DoubleInterval(0.3010299956639812,1.0);
        DoubleInterval bRes = new DoubleInterval(-4.3979400086720375,-4.301029995663981);

        assertTrue(String.format("Test log(a) : %s isIn %s", aRes,aLog), aLog.contains(aRes));
        assertTrue(String.format("Test log(b) : %s isIn %s", bRes,bLog), bLog.contains(bRes));
    }

    @Test
    public void testSin() throws Exception {
        double v1,v2,min = 10.0,max = -10.0;
        double itr,itrSin;
        DoubleInterval a,aRes;

        for(int i=0; i<nbrLoopExecution; i++) {
            v1 = rnd.nextDouble() * MAX_SUP; v1 = rnd.nextBoolean() ? v1:-v1;
            v2 = rnd.nextDouble() * MAX_SUP; v2 = rnd.nextBoolean() ? v2:-v2;
            if(v1 < v2) {
                a = new DoubleInterval(v1,v2);
            } else {
                a = new DoubleInterval(v2,v1);
            }
            aRes = DoubleInterval.sin(a);
            itr = a.inf + step; // if we start at itr = a.inf, it's failing...
            while(itr < a.sup) {
                itrSin = Math.sin(itr);
                if(itrSin < min) {
                    min = itrSin;
                }
                if(itrSin > max)  {
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
    public void testCos() throws Exception {
        double v1,v2,min = 20.0,max = -20.0;
        double itr,itrCos;
        DoubleInterval a,aRes;

        for(int i=0; i<nbrLoopExecution; i++) {
            v1 = rnd.nextDouble() * MAX_SUP; v1 = rnd.nextBoolean() ? v1:-v1;
            v2 = rnd.nextDouble() * MAX_SUP; v2 = rnd.nextBoolean() ? v2:-v2;
            if(v1 < v2) {
                a = new DoubleInterval(v1,v2);
            } else {
                a = new DoubleInterval(v2,v1);
            }
            aRes = DoubleInterval.cos(a);
            itr = a.inf + step; // if we start at itr = a.inf, it's failing...
            while(itr < a.sup) {
                itrCos = Math.cos(itr);
                if(itrCos < min) {
                    min = itrCos;
                }
                if(itrCos > max) {
                    max = itrCos;
                }
                assertTrue(String.format("cos(%s) is in %s", itr, aRes),itrCos <= aRes.sup && itrCos >= aRes.inf);
                itr+=step;
            }
            assertTrue("Test min : ",min + step > aRes.inf || min - step < aRes.inf);
            assertTrue("Test max : ",max + step > aRes.sup || max - step < aRes.sup);
        }
    }

    @Test
    public void testTan() throws Exception {
        double v1,v2,min = 20.0,max = -20.0;
        double itr,itrTan;
        DoubleInterval a,aRes;

        for(int i=0; i<nbrLoopExecution; i++) {
            v1 = rnd.nextDouble() * MAX_SUP; v1 = rnd.nextBoolean() ? v1:-v1;
            v2 = rnd.nextDouble() * MAX_SUP; v2 = rnd.nextBoolean() ? v2:-v2;
            if(v1 < v2) {
                a = new DoubleInterval(v1,v2);
            } else {
                a = new DoubleInterval(v2,v1);
            }
            aRes = DoubleInterval.tan(a);
            itr = a.inf + step; // if we start at itr = a.inf, it's failing...
            while(itr < a.sup) {
                itrTan = Math.tan(itr);
                if(itrTan < min) {
                    min = itrTan;
                }
                if(itrTan > max) {
                    max = itrTan;
                }
                assertTrue(String.format("tan(%s) is in %s : tan(%s) = %s", itr, aRes,itr,itrTan),itrTan <= aRes.sup && itrTan >= aRes.inf);
                itr+=step;
            }
            assertTrue("Test min : ",min + step > aRes.inf || min - step < aRes.inf);
            assertTrue("Test max : ",max + step > aRes.sup || max - step < aRes.sup);
        }
    }

    @Test
    public void testWidth() throws Exception {
        DoubleInterval a;
        double v1,v2;
        for (int i = 0; i < nbrLoopExecution; i++) {
            v1 = rnd.nextDouble() * MAX_SUP; v1 = rnd.nextBoolean() ? v1:-v1;
            v2 = rnd.nextDouble() * MAX_SUP; v2 = rnd.nextBoolean() ? v2:-v2;
            a = v1 < v2 ? new DoubleInterval(v1,v2) : new DoubleInterval(v2,v1);
            assertTrue(DoubleInterval.width(a) == a.sup - a.inf);
        }
    }

    @Test
    public void testSqrt() throws Exception {
        double v1,v2;
        DoubleInterval a,aRes;
        double itr,itrSqrt; 
        
        for (int i = 0; i < nbrLoopExecution; i++) {
            v1 = rnd.nextDouble() * MAX_SUP;
            v2 = rnd.nextDouble() * MAX_SUP;
            a = v1 < v2 ? new DoubleInterval(v1, v2) : new DoubleInterval(v2, v1);
            testIntervalBounds(a);

            itr = a.inf + step;

            aRes = DoubleInterval.sqrt(a);
            while (itr < a.sup) {
                itrSqrt = Math.sqrt(itr);
                assertTrue(String.format("sqrt(%s) is in %s : sqrt(%s) = %s\nInterval used is : %s\n", itr, aRes, itr, itrSqrt, a),
                        itrSqrt <= aRes.sup && itrSqrt >= aRes.inf);
                itr += step;
            }
        }
    }

    @Test
    public void testAbs() throws Exception  {
        DoubleInterval a = new DoubleInterval(3.0,4.0);
        DoubleInterval b = new DoubleInterval(-2.0,5.0);
        DoubleInterval c = new DoubleInterval(-7.0,-5.0);

        DoubleInterval aRes = new DoubleInterval(3.0,4.0);
        DoubleInterval bRes = new DoubleInterval(0.0,5.0);
        DoubleInterval cRes = new DoubleInterval(5.0,7.0);

        assertTrue(DoubleInterval.abs(a).strictlyEquals(aRes));
        assertTrue(DoubleInterval.abs(b).strictlyEquals(bRes));
        assertTrue(DoubleInterval.abs(c).strictlyEquals(cRes));
    }

    @Test
    public void testNeg() throws Exception {
        DoubleInterval a = new DoubleInterval(3.0,4.0);
        DoubleInterval b = new DoubleInterval(-2.0,5.0);
        DoubleInterval c = new DoubleInterval(-7.0,-5.0);

        DoubleInterval aRes = new DoubleInterval(-4.0,-3.0);
        DoubleInterval bRes = new DoubleInterval(-5.0,2.0);
        DoubleInterval cRes = new DoubleInterval(5.0,7.0);

        assertTrue(DoubleInterval.neg(a).strictlyEquals(aRes));
        assertTrue(DoubleInterval.neg(b).strictlyEquals(bRes));
        assertTrue(DoubleInterval.neg(c).strictlyEquals(cRes));
    }

    @Test
    public void testArcFunction() throws Exception {
        double v1,v2;
        DoubleInterval a,aArcSin,aArcCos,aArcTan;
        double itr,itrFuncValue;

        // Test cosh and sinh, restricted domain
        for (int i = 0; i < nbrLoopExecution; i++) {
            v1 = rnd.nextDouble(); v1 = rnd.nextBoolean()?v1:-v1;
            v2 = rnd.nextDouble(); v2 = rnd.nextBoolean()?v2:-v2;
            a = v1 < v2 ? new DoubleInterval(v1,v2) : new DoubleInterval(v2,v1);
            testIntervalBounds(a);

            itr = a.inf + step;

            aArcCos = DoubleInterval.acos(a);
            aArcSin = DoubleInterval.asin(a);
            while(itr < a.sup) {
                itrFuncValue = Math.acos(itr);
                assertTrue(String.format("acos(%s) is in %s : acos(%s) = %s\nInterval used is : %s\n", itr, aArcCos,itr,itrFuncValue,a),
                        itrFuncValue <= aArcCos.sup && itrFuncValue >= aArcCos.inf);
                itrFuncValue = Math.asin(itr);
                assertTrue(String.format("asin(%s) is in %s : asin(%s) = %s\nInterval used is : %s\n", itr, aArcSin,itr,itrFuncValue,a),
                        itrFuncValue <= aArcSin.sup && itrFuncValue >= aArcSin.inf);
                itr += step;
            }
        }

        // Test tanh
        for (int i = 0; i < nbrLoopExecution; i++) {
            v1 = rnd.nextDouble() * MAX_SUP; v1 = rnd.nextBoolean()?v1:-v1;
            v2 = rnd.nextDouble() * MAX_SUP; v2 = rnd.nextBoolean()?v2:-v2;
            a = v1 < v2 ? new DoubleInterval(v1,v2) : new DoubleInterval(v2,v1);
            testIntervalBounds(a);

            itr = a.inf + step;

            aArcTan = DoubleInterval.atan(a);
            while(itr < a.sup) {
                itrFuncValue = Math.atan(itr);
                assertTrue(String.format("atan(%s) is in %s : atan(%s) = %s\nInterval used is : %s\n", itr, aArcTan,itr,itrFuncValue,a),
                        itrFuncValue <= aArcTan.sup && itrFuncValue >= aArcTan.inf);
                itr += step;
            }
        }
    }

    @Test
    public void testHyperbolic() throws Exception {
        double v1,v2;
        DoubleInterval a,aSinh,aCosh,aTanh;
        double itr,itrFuncValue;

        // Test cosh and sinh, restricted domain
        for (int i = 0; i < nbrLoopExecution; i++) {
            v1 = rnd.nextDouble(); v1 = rnd.nextBoolean()?v1:-v1;
            v2 = rnd.nextDouble(); v2 = rnd.nextBoolean()?v2:-v2;
            a = v1 < v2 ? new DoubleInterval(v1,v2) : new DoubleInterval(v2,v1);
            testIntervalBounds(a);

            itr = a.inf + step;

            aCosh = DoubleInterval.cosh(a);
            aSinh = DoubleInterval.sinh(a);
            aTanh = DoubleInterval.tanh(a);

            while(itr < a.sup) {
                itrFuncValue = Math.cosh(itr);
                assertTrue(String.format("cosh(%s) is in %s : cosh(%s) = %s\nInterval used is : %s\n", itr, aCosh,itr,itrFuncValue,a),
                        itrFuncValue <= aCosh.sup && itrFuncValue >= aCosh.inf);
                itrFuncValue = Math.sinh(itr);
                assertTrue(String.format("sinh(%s) is in %s : sinh(%s) = %s\nInterval used is : %s\n", itr, aSinh,itr,itrFuncValue,a),
                        itrFuncValue <= aSinh.sup && itrFuncValue >= aSinh.inf);
                itrFuncValue = Math.tanh(itr);
                assertTrue(String.format("tanh(%s) is in %s : tanh(%s) = %s\nInterval used is : %s\n", itr, aTanh,itr,itrFuncValue,a),
                        itrFuncValue <= aTanh.sup && itrFuncValue >= aTanh.inf);
                itr += step;
            }
        }
    }

    @Test
    public void testModulo() throws Exception {
        DoubleInterval a = new DoubleInterval(4.0,5.0);
        DoubleInterval b = new DoubleInterval(-3.0,5.0);
        DoubleInterval c = new DoubleInterval(-6.0,-2.0);
        DoubleInterval d = new DoubleInterval(0.0,5.0);

        DoubleInterval ab = DoubleInterval.modulo(a,b);
        DoubleInterval ba = DoubleInterval.modulo(b,a);
        DoubleInterval ac = DoubleInterval.modulo(a,c);
        DoubleInterval ca = DoubleInterval.modulo(c,a);
        DoubleInterval ad = DoubleInterval.modulo(a,d);

        DoubleInterval baRes = new DoubleInterval(-3.0,5.0);
        DoubleInterval acRes = new DoubleInterval(0.0,5.0);
        DoubleInterval caRes = new DoubleInterval(-5.0,0.0);

        assertTrue(String.format("Test a %% b : NaN",a,b),ab.isNan());
        assertTrue(String.format("Test a %% d : NaN",a,d),ad.isNan());
        assertTrue(String.format("Test b %% a : %s %% %s = %s ; res : %s",b,a,baRes,ba), ba.contains(baRes));
        assertTrue(String.format("Test a %% c : %s %% %s = %s ; res : %s",a,c,acRes,ac), ac.contains(acRes));
        assertTrue(String.format("Test c %% a : %s %% %s = %s ; res : %s",c,a,caRes,ca), ca.contains(caRes));
    }

    @Test
    public void testSpecialCasesToBeDiscussed() throws Exception {
        DoubleInterval a = new DoubleInterval(-Double.MAX_VALUE,Double.MAX_VALUE);
        DoubleInterval b = new DoubleInterval(0.0);
        DoubleInterval aPlusZero=DoubleInterval.add(a,b);
        assertTrue(aPlusZero.inf==Double.NEGATIVE_INFINITY || 
                   aPlusZero.inf==-Double.MAX_VALUE);
        assertTrue(aPlusZero.sup==Double.POSITIVE_INFINITY || 
                   aPlusZero.sup==Double.MAX_VALUE);
        DoubleInterval zeroMinusZero=DoubleInterval.sub(b,b);
        assertTrue(zeroMinusZero.inf==-Double.MIN_VALUE || 
                   zeroMinusZero.inf==0);
        assertTrue(zeroMinusZero.sup==+Double.MIN_VALUE || 
                   zeroMinusZero.sup==0);
    }

    private void testIntervalBounds(DoubleInterval a) throws Exception {
        assertTrue(String.format("DoubleInterval %s is degenerated",a),a.hasValidBounds());
    }
}
