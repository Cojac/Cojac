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

package com.github.cojac.unit.interval;

import org.junit.Test;

import static com.github.cojac.unit.interval.DoubleUtils.*;
import static java.lang.Double.MAX_VALUE;
import static org.junit.Assert.assertTrue;

public class DoubleUtilsTest
{
    private final int repeat_times = 1000;

    @Test
    public void testRndDouble() {
        for (int counter = 0; counter< repeat_times; counter++)
        {
            double a = rndDouble();
            assertTrue(String.format("%f > 0", a), a >= 0.0);
            assertTrue(String.format("%f < %f", a, Double.MAX_VALUE), a <= Double.MAX_VALUE);
        }
    }

    @Test
    public void testGetBiggerRndDouble() {
        for (int counter = 0; counter < repeat_times; counter++)
        {
            double a = rndDouble();
            double b = getBiggerRndDouble(a);
            double c = getBiggerRndDouble(b);

            // Test domain
            assertTrue(String.format("Test a > 0 : %f > %f", a, 0.0), a > 0.0);
            assertTrue(String.format("Test b > 0 : %f > %f", b, 0.0), b > 0.0);
            assertTrue(String.format("Test c > 0 : %f > %f", c, 0.0), c > 0.0);

            // Test relation
            assertTrue(String.format("Test b > a : %f > %f", b, a), b > a);
            assertTrue(String.format("Test c > d : %f > %f", c, b), c > b);

            // Test special number
            double f = getBiggerRndDouble(0.0);

            assertTrue(String.format("Test f > 0.0 : %f > %f", f, 0.0), f > 0.0);
        }
    }

    @Test
    public void testGetSmallerRndDouble() {
        for (int counter = 0; counter < repeat_times; counter++)
        {
            double a = rndDouble();
            double c = getSmallerRndDouble(a);
            double e = getSmallerRndDouble(c);

            // Test domain
            assertTrue(String.format("Test a >= 0.0 : %f >= %f", a, 0.0), a >= 0.0);
            assertTrue(String.format("Test c >= 0.0 : %f >= %f", c, 0.0), c >= 0.0);
            assertTrue(String.format("Test e >= 0.0 : %f >= %f", e, 0.0), e >= 0.0);

            // Test relation
            assertTrue(String.format("Test a > c : %f, %f", a, c), a > c);
            assertTrue(String.format("Test c > e : %f, %f", c, e), a > c);
            assertTrue(String.format("Test a > e : %f, %f", a, e), a > e);

            // Test special number
            double h = getSmallerRndDouble(0.0);
            assertTrue(String.format("Test h == 0.0 : %f == %f", h, 0.0), h == 0.0);
        }
    }

    @Test
    public void testGetBiggerNegativeRndDouble() {
        for (int counter = 0; counter < repeat_times; counter++)
        {
            double a = rndDouble();
            double b = getBiggerRndNegativeDouble(a);
            double c = getBiggerRndNegativeDouble(b);
            double d = -rndDouble();
            double e = getBiggerRndNegativeDouble(d);
            double f = getBiggerRndNegativeDouble(e);

            // Test domain
            assertTrue(String.format("Test a >= 0 : %f >= %f", a, 0.0), a >= 0.0);
            assertTrue(String.format("Test b <= 0 : %f >= %f", b, 0.0), b <= 0.0);
            assertTrue(String.format("Test c <= 0 : %f >= %f", c, 0.0), c <= 0.0);
            assertTrue(String.format("Test d <= 0 : %f >= %f", d, 0.0), d <= 0.0);
            assertTrue(String.format("Test e <= 0 : %f >= %f", e, 0.0), e <= 0.0);
            assertTrue(String.format("Test f <= 0 : %f >= %f", f, 0.0), f <= 0.0);

            // Test relation
            assertTrue(String.format("Test a > b : %f > %f", a, b), a > b);
            assertTrue(String.format("Test b > c : %f > %f", b, c), b > c);
            assertTrue(String.format("Test e < d  : %f < %f", e, d), e < d);
            assertTrue(String.format("Test f < e : %f < %f", f, e), f < e);

            // Test special number
            double i = getBiggerRndNegativeDouble(0.0);

            assertTrue(String.format("Test i <= 0.0 : %f <= %f", i,0.0), i <= 0.0);
            assertTrue(String.format("Test i >= -MAX_VALUE : %f >= %f)",i,-MAX_VALUE), i >= -MAX_VALUE);
        }
    }

    @Test
    public void testGetSmallerNegativeRndDouble() {
        for (int counter = 0; counter < repeat_times; counter++)
        {
            double a = rndDouble();
            double b = getSmallerRndNegativeDouble(a);
            double c = getSmallerRndNegativeDouble(b);
            double d = -rndDouble();
            double e = getSmallerRndNegativeDouble(c);
            double f = getSmallerRndNegativeDouble(e);

            // Test domain
            assertTrue(String.format("Test a >= 0 : %f >= %f", a, 0.0), a >= 0.0);
            assertTrue(String.format("Test b <= 0 : %f >= %f", b, 0.0), b <= 0.0);
            assertTrue(String.format("Test c <= 0 : %f >= %f", c, 0.0), c <= 0.0);
            assertTrue(String.format("Test d <= 0 : %f >= %f", d, 0.0), d <= 0.0);
            assertTrue(String.format("Test e <= 0 : %f >= %f", e, 0.0), e <= 0.0);
            assertTrue(String.format("Test f <= 0 : %f >= %f", f, 0.0), f <= 0.0);

            // Test relation
            assertTrue(String.format("Test a > b : %f > %f", a, b), a > b);
            assertTrue(String.format("Test b > c : %f > %f", b, c), b < c);
            assertTrue(String.format("Test e > d : %f < %f", e, Math.abs(d)), e < Math.abs(d));
            assertTrue(String.format("Test f > e : %f > %f", f, e), f > e);    // abs(f) < abs(e)

            // Test special number
            double i = getSmallerRndNegativeDouble(0.0);
            assertTrue(String.format("Test i == 0.0 : %f == %f", i, 0.0), i == 0.0);
        }
    }
}
