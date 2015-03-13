package ch.eiafr.cojac.unit.interval;

import org.junit.Test;

import static ch.eiafr.cojac.unit.interval.DoubleUtils.*;
import static org.junit.Assert.assertTrue;

public class DoubleUtilsTest
{

    @Test
    public void testRDouble() throws Exception
    {
        double a = rDouble();
        assertTrue(String.format("%f > 0", a), a >= 0.0);
        assertTrue(String.format("%f < %f", a, Double.MAX_VALUE), a <= Double.MAX_VALUE);
    }

    @Test
    public void testGetBiggerDouble() throws Exception
    {
        double a = rDouble();
        double b = getBiggerDouble(a);
        double c = getBiggerDouble(b);
        double d = -getBiggerDouble(c);
        double e = getBiggerDouble(d);

        // Test domain
        assertTrue(String.format("Test a > 0 : %f > %f", a, 0.0), a > 0.0);
        assertTrue(String.format("Test b > 0 : %f > %f", b, 0.0), b > 0.0);
        assertTrue(String.format("Test c > 0 : %f > %f", c, 0.0), c > 0.0);
        assertTrue(String.format("Test d < 0 : %f > %f", d, 0.0), d < 0.0);
        assertTrue(String.format("Test e > 0 : %f > %f", e, 0.0), e > 0.0);

        // Test relation
        assertTrue(String.format("Test b > a : %f > %f", b, a), b > a);
        assertTrue(String.format("Test c > d : %f > %f", c, b), c > b);
        assertTrue(String.format("Test d < b : %f < %f", d, b), d < b);
        assertTrue(String.format("Test e > 0 : %f > %f", e, 0.0), e > 0.0);

        // Test Negative
        a = -rDouble();
        b = getBiggerDouble(a);
        c = getBiggerDouble(b);
        d = -getBiggerDouble(c);
        e = getBiggerDouble(d);

        assertTrue(String.format("Test b > a : %f > %f", b, a), b > a);
        assertTrue(String.format("Test c > d : %f > %f", c, b), c > b);
        assertTrue(String.format("Test d < b : %f < %f", d, b), d < b);
        assertTrue(String.format("Test e > 0 : %f > %f", e, 0.0), e > 0.0);

        // Test special number
        double f = getBiggerDouble(0.0);
        double g = getBiggerDouble(Double.MAX_VALUE);
        double h = getBiggerDouble(Double.MIN_VALUE);
        double i = getBiggerDouble(Double.NaN);
        double j = getBiggerDouble(Double.POSITIVE_INFINITY);
        double k = getBiggerDouble(Double.NEGATIVE_INFINITY);

        assertTrue(String.format("Test f > 0.0 : %f > %f", f, 0.0), f > 0.0);
        assertTrue(String.format("Test g == MAX_VALUE : %f == %f", g, Double.MAX_VALUE), g == Double.MAX_VALUE);
        assertTrue(String.format("Test h > 0.0 : %f > %f", h, 0.0), h > 0.0);
        assertTrue(String.format("Test i is Nan : %f == %f", i, Double.NaN), Double.isNaN(i));
        assertTrue(String.format("Test j == MAX_VALUE : %f == %f", j, Double.MAX_VALUE), j == Double.MAX_VALUE);
        assertTrue(String.format("Test k > NEGATIVE_INFINITY : %f < %f", Double.NEGATIVE_INFINITY, k), Double.NEGATIVE_INFINITY < k);
        assertTrue(String.format("Test k > 0.0: %f > %f", k, 0.0), k > 0.0);
    }

    @Test
    public void testGetSmallerDouble() throws Exception
    {
        double a = rDouble();
        double b = -rDouble();
        double c = getSmallerDouble(a);
        double d = getSmallerDouble(b);
        double e = getSmallerDouble(c);

        // Test domain
        assertTrue(String.format("Test a >= 0.0 : %f >= %f", a, 0.0), a >= 0.0);
        assertTrue(String.format("Test b <= 0.0 : %f <= %f", b, 0.0), b <= 0.0);
        assertTrue(String.format("Test c >= 0.0 : %f >= %f", c, 0.0), c >= 0.0);
        assertTrue(String.format("Test d >= 0.0 : %f >= %f", d, 0.0), d >= 0.0);
        assertTrue(String.format("Test e >= 0.0 : %f >= %f", e, 0.0), e >= 0.0);

        // Test relation
        assertTrue(String.format("Test a > c : %f, %f", a, c), a > c);
        assertTrue(String.format("Test c > e : %f, %f", c, e), a > c);
        assertTrue(String.format("Test b < 0.0 : %f, %f", b, 0.0), b < 0.0);
        assertTrue(String.format("Test a > e : %f, %f", a, e), a > e);

        // Test special number
        double f = getSmallerDouble(Double.MAX_VALUE);
        double g = getSmallerDouble(Double.MIN_VALUE);
        double h = getSmallerDouble(0.0);
        double i = getSmallerDouble(Double.NaN);
        double j = getSmallerDouble(Double.POSITIVE_INFINITY);
        double k = getSmallerDouble(Double.NEGATIVE_INFINITY);
        assertTrue(String.format("Test f < MAX_VALUE : %f < %f", f, Double.MAX_VALUE), f < Double.MAX_VALUE);
        assertTrue(String.format("Test g == 0.0 : %f == %f", g, 0.0), g == 0.0);
        assertTrue(String.format("Test h == 0.0 : %f == %f", h, 0.0), h == 0.0);
        assertTrue(String.format("Test i == Nan : %f == NaN", i), Double.isNaN(i));
        assertTrue(String.format("Test j >= 0.0 : %f >= %f", j, 0.0), j >= 0.0);
        assertTrue(String.format("Test j <= MAX_VALUE 0.0 : %f <= %f", j, Double.MAX_VALUE), j <= Double.MAX_VALUE);
        assertTrue(String.format("Test k >= 0.0 : %f >= %f", k, 0.0), k >= 0.0);
    }

    @Test
    public void testGetBiggerNegativeDouble() throws Exception
    {
        double a = rDouble();
        double b = getBiggerNegativeDouble(a);
        double c = getBiggerNegativeDouble(b);
        double d = -rDouble();
        double e = getBiggerNegativeDouble(c);
        double f = getBiggerNegativeDouble(e);

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
        double g = getBiggerNegativeDouble(Double.MAX_VALUE);
        double h = getBiggerNegativeDouble(Double.MIN_VALUE);
        double i = getBiggerNegativeDouble(0.0);
        double j = getBiggerNegativeDouble(Double.NaN);
        double k = getBiggerNegativeDouble(Double.POSITIVE_INFINITY);
        double l = getBiggerNegativeDouble(Double.NEGATIVE_INFINITY);

        assertTrue(String.format("Test g == -MAX_VALUE : %f == %f", g, -Double.MAX_VALUE), g == (-Double.MAX_VALUE));
        assertTrue(String.format("Test h <= 0.0 : %f <= %f", h, 0.0), h <= 0.0);
        assertTrue(String.format("Test h >= -MAX_VALUE : %f >= %f", h, -Double.MAX_VALUE), h >= -Double.MAX_VALUE);
        assertTrue(String.format("Test i <= 0.0 : %f == %f", g, -Double.MAX_VALUE), g == (-Double.MAX_VALUE));
        assertTrue(String.format("Test i >= -MAX_VALUE : %f == %f", g, -Double.MAX_VALUE), g == (-Double.MAX_VALUE));
        assertTrue(String.format("Test j == NaN : %f == %f", j, Double.NaN), Double.isNaN(j));
        assertTrue(String.format("Test k == NEGATIVE_INFINITY : %f == %f", k, Double.NEGATIVE_INFINITY), k == Double.NEGATIVE_INFINITY);
        assertTrue(String.format("Test l == NEGATIVE_INFINITY : %f == %f", k, Double.NEGATIVE_INFINITY), l == Double.NEGATIVE_INFINITY);
    }

    @Test
    public void testGetSmallerNegativeDouble() throws Exception
    {
        double a = rDouble();
        double b = getSmallerNegativeDouble(a);
        double c = getSmallerNegativeDouble(b);
        double d = -rDouble();
        double e = getSmallerNegativeDouble(c);
        double f = getSmallerNegativeDouble(e);

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
        double g = getSmallerNegativeDouble(Double.MAX_VALUE);
        double h = getSmallerNegativeDouble(Double.MIN_VALUE);
        double i = getSmallerNegativeDouble(0.0);
        double j = getSmallerNegativeDouble(Double.NaN);
        double k = getSmallerNegativeDouble(Double.POSITIVE_INFINITY);
        double l = getSmallerNegativeDouble(Double.NEGATIVE_INFINITY);

        assertTrue(String.format("Test g <= 0.0 : %f <= %f", g, 0.0), g <= 0.0);
        assertTrue(String.format("Test g >= -MAX_VALUE : %f >= %f", g, -Double.MAX_VALUE), g >= -Double.MAX_VALUE);
        assertTrue(String.format("Test h == 0.0 : %f == %f", h, 0.0), h == 0.0);
        assertTrue(String.format("Test i == 0.0 : %f == %f", i, 0.0), i == 0.0);
        assertTrue(String.format("Test j == NaN : %f == %f", j, Double.NaN), Double.isNaN(j));
        assertTrue(String.format("Test k <= 0.0 : %f <= %f", k, 0.0), k <= 0.0);
        assertTrue(String.format("Test k >= -MAX_VALUE : %f >= %f", k, -Double.MAX_VALUE), k >= -Double.MAX_VALUE);
        assertTrue(String.format("Test l <= 0.0 : %f <= %f", l, 0.0), l <= 0.0);
        assertTrue(String.format("Test l >= -MAX_VALUE : %f >= %f", l, -Double.MAX_VALUE), l >= -Double.MAX_VALUE);
    }
}
