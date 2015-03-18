package ch.eiafr.cojac.unit.interval;

import org.junit.Test;

import static ch.eiafr.cojac.unit.interval.DoubleUtils.*;
import static java.lang.Double.MAX_VALUE;
import static java.lang.Double.MIN_VALUE;
import static org.junit.Assert.assertTrue;
import static java.lang.Double.NaN;

public class DoubleUtilsTest
{
    private int repeat_times = 10000;

    @Test
    public void testRDouble() throws Exception
    {
        for (int counter = 0; counter< repeat_times; counter++)
        {
            double a = rDouble();
            assertTrue(String.format("%f > 0", a), a >= 0.0);
            assertTrue(String.format("%f < %f", a, Double.MAX_VALUE), a <= Double.MAX_VALUE);
        }
    }

    @Test
    public void testGetBiggerDouble() throws Exception
    {
        for (int counter = 0; counter < repeat_times; counter++)
        {
            double a = rDouble();
            double b = getBiggerDouble(a);
            double c = getBiggerDouble(b);

            // Test domain
            assertTrue(String.format("Test a > 0 : %f > %f", a, 0.0), a > 0.0);
            assertTrue(String.format("Test b > 0 : %f > %f", b, 0.0), b > 0.0);
            assertTrue(String.format("Test c > 0 : %f > %f", c, 0.0), c > 0.0);

            // Test relation
            assertTrue(String.format("Test b > a : %f > %f", b, a), b > a);
            assertTrue(String.format("Test c > d : %f > %f", c, b), c > b);

            // Test special number
            double f = getBiggerDouble(0.0);

            assertTrue(String.format("Test f > 0.0 : %f > %f", f, 0.0), f > 0.0);
        }
    }

    @Test
    public void testGetSmallerDouble() throws Exception
    {
        for (int counter = 0; counter < repeat_times; counter++)
        {
            double a = rDouble();
            double c = getSmallerDouble(a);
            double e = getSmallerDouble(c);

            // Test domain
            assertTrue(String.format("Test a >= 0.0 : %f >= %f", a, 0.0), a >= 0.0);
            assertTrue(String.format("Test c >= 0.0 : %f >= %f", c, 0.0), c >= 0.0);
            assertTrue(String.format("Test e >= 0.0 : %f >= %f", e, 0.0), e >= 0.0);

            // Test relation
            assertTrue(String.format("Test a > c : %f, %f", a, c), a > c);
            assertTrue(String.format("Test c > e : %f, %f", c, e), a > c);
            assertTrue(String.format("Test a > e : %f, %f", a, e), a > e);

            // Test special number
            double h = getSmallerDouble(0.0);
            assertTrue(String.format("Test h == 0.0 : %f == %f", h, 0.0), h == 0.0);
        }
    }

    @Test
    public void testGetBiggerNegativeDouble() throws Exception
    {
        for (int counter = 0; counter < repeat_times; counter++)
        {
            double a = rDouble();
            double b = getBiggerNegativeDouble(a);
            double c = getBiggerNegativeDouble(b);
            double d = -rDouble();
            double e = getBiggerNegativeDouble(d);
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
            double i = getBiggerNegativeDouble(0.0);

            assertTrue(String.format("Test i <= 0.0 : %f <= %f", i,0.0), i <= 0.0);
            assertTrue(String.format("Test i >= -MAX_VALUE : %f >= %f)",i,-MAX_VALUE), i >= -MAX_VALUE);
        }
    }

    @Test
    public void testGetSmallerNegativeDouble() throws Exception
    {
        for (int counter = 0; counter < repeat_times; counter++)
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
            double i = getSmallerNegativeDouble(0.0);
            assertTrue(String.format("Test i == 0.0 : %f == %f", i, 0.0), i == 0.0);
        }
    }
}
