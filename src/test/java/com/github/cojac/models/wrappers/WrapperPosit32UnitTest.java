package com.github.cojac.models.wrappers;

import com.github.cojac.utils.Posit32Utils;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class WrapperPosit32UnitTest {

    private final static double ERROR_TOLERANCE = 1e-6;

    @BeforeClass
    public static void loadLibrary() {
        Posit32Utils.loadLibrary();
    }

    public static WrapperPosit32 positWrapperFromFloat(float a){
        return new WrapperPosit32(Posit32Utils.toPosit(a));
    }

    @Test
    public void testConstructorNull() {
        WrapperPosit32 a = new WrapperPosit32(null);
        assertEquals(0.0, a.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testConstructorNotNull() {
        WrapperPosit32 a = positWrapperFromFloat(4.5f);
        WrapperPosit32 b = new WrapperPosit32(a);
        assertEquals(4.5f, b.toDouble(), ERROR_TOLERANCE);
        assertEquals(a.toDouble(), b.toDouble(), 0);
    }

    @Test
    public void testToDouble() {
        WrapperPosit32 wrapper = positWrapperFromFloat(-10.175f);
        assertEquals(-10.175f, wrapper.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testFromDouble() {
        WrapperPosit32 a = positWrapperFromFloat(-15.5f);
        WrapperPosit32 b = a.fromDouble(-15.5f, false);
        assertEquals(-15.5f, b.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testAsInternalString() {
        WrapperPosit32 a = positWrapperFromFloat(7.825f);
        String internal = a.asInternalString();
        assertEquals(Double.toString(a.toDouble()), internal);
    }

    @Test
    public void testDadd() {
        WrapperPosit32 a = positWrapperFromFloat(1.5f);
        WrapperPosit32 b = positWrapperFromFloat(-5.25f);
        WrapperPosit32 c = a.dadd(b);
        assertEquals(-3.75f, c.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testDsub() {
        WrapperPosit32 a = positWrapperFromFloat(1.5f);
        WrapperPosit32 b = positWrapperFromFloat(-5.25f);
        WrapperPosit32 c = a.dsub(b);
        assertEquals(6.75f, c.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testDmul() {
        WrapperPosit32 a = positWrapperFromFloat(2);
        WrapperPosit32 b = positWrapperFromFloat(7.25f);
        WrapperPosit32 c = a.dmul(b);
        assertEquals(14.5f, c.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testDdiv() {
        WrapperPosit32 a = positWrapperFromFloat(5);
        WrapperPosit32 b = positWrapperFromFloat(-2);
        WrapperPosit32 c = a.ddiv(b);
        assertEquals(-2.5f, c.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testDneg() {
        WrapperPosit32 a = positWrapperFromFloat(-6.5f);
        WrapperPosit32 b = a.dneg();
        assertEquals(6.5f, b.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_sqrt() {
        WrapperPosit32 a = positWrapperFromFloat(4);
        WrapperPosit32 b = a.math_sqrt();
        assertEquals(2.0, b.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_cbrt() {
        WrapperPosit32 a = positWrapperFromFloat(-15.625f);
        WrapperPosit32 b = a.math_cbrt();
        assertEquals(Math.cbrt(-15.625f), b.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_abs() {
        WrapperPosit32 a = positWrapperFromFloat(-4.5f);
        WrapperPosit32 b = a.math_abs();
        assertEquals(4.5f, b.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_toRadians() {
        WrapperPosit32 a = positWrapperFromFloat((float)Math.PI);
        WrapperPosit32 b = a.math_toDegrees();
        assertEquals(180.0, b.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_toDegrees() {
        WrapperPosit32 a = positWrapperFromFloat(180);
        WrapperPosit32 b = a.math_toRadians();
        assertEquals(Math.PI, b.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testIsNaN() {
        WrapperPosit32 a = positWrapperFromFloat(Float.NaN);
        assertTrue(a.isNaN());
    }

    @Test
    public void testWrapperName() {
        WrapperPosit32 a = positWrapperFromFloat(2.5f);
        String name = a.wrapperName();
        assertEquals("Posit32", name);
    }

    @Test
    public void testDrem() {
        WrapperPosit32 number = positWrapperFromFloat(4.325f);
        WrapperPosit32 remainder = number.drem(positWrapperFromFloat(3.321f));
        assertEquals(4.325f % 3.321f, remainder.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_sin() {
        float value = 9.365f;
        WrapperPosit32 number = positWrapperFromFloat(value).math_sin();
        assertEquals(Math.sin(value), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_cos() {
        float value = 9.365f;
        WrapperPosit32 number = positWrapperFromFloat(value).math_cos();
        assertEquals(Math.cos(value), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_tan() {
        float value = 9.365f;
        WrapperPosit32 number = positWrapperFromFloat(value).math_tan();
        assertEquals(Math.tan(value), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_asin() {
        float value = 9.365f;
        WrapperPosit32 number = positWrapperFromFloat(value).math_asin();
        assertEquals(Math.asin(value), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_acos() {
        float value = 9.365f;
        WrapperPosit32 number = positWrapperFromFloat(value).math_acos();
        assertEquals(Math.acos(value), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_atan() {
        float value = 9.365f;
        WrapperPosit32 number = positWrapperFromFloat(value).math_atan();
        assertEquals(Math.atan(value), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_sinh() {
        float value = 2.365f;
        WrapperPosit32 number = positWrapperFromFloat(value).math_sinh();
        assertEquals(Math.sinh(value), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_cosh() {
        float value = 1.365f;
        WrapperPosit32 number = positWrapperFromFloat(value).math_cosh();
        assertEquals(Math.cosh(value), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_tanh() {
        float value = 9.365f;
        WrapperPosit32 number = positWrapperFromFloat(value).math_tanh();
        assertEquals(Math.tanh(value), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_exp() {
        WrapperPosit32 number = positWrapperFromFloat(-2.5f).math_exp();
        assertEquals(Math.exp(-2.5), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_log() {
        WrapperPosit32 number = positWrapperFromFloat(-2.5f).math_log();
        assertEquals(Math.log(-2.5), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_log10() {
        WrapperPosit32 number = positWrapperFromFloat(-2.5f).math_log10();
        assertEquals(Math.log10(-2.5), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMath_pow() {
        WrapperPosit32 base = positWrapperFromFloat(-2);
        WrapperPosit32 power = positWrapperFromFloat(3.5f);
        WrapperPosit32 number = base.math_pow(power);
        assertEquals(Math.pow(-2, 3.5), number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testCompareTo_equality() {
        WrapperPosit32 number1 = positWrapperFromFloat(-2.4896f);
        WrapperPosit32 number2 = positWrapperFromFloat(-2.4896f);
        assertEquals(0, number1.compareTo(number2));
    }

    @Test
    public void testCompareTo_imaginary_normalMode() {
        WrapperPosit32 number1 = positWrapperFromFloat(-2.4896f);
        WrapperPosit32 number2 = positWrapperFromFloat(-2.32f);
        assertTrue(number1.compareTo(number2) < 0);
        assertTrue(number2.compareTo(number1) > 0);
    }

    @Test
    public void testMath_hypot() {
        WrapperPosit32 a = positWrapperFromFloat(3);
        WrapperPosit32 b = positWrapperFromFloat(-4);
        WrapperPosit32 number = a.math_hypot(b);
        assertEquals(5, number.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testFromString() {
        WrapperPosit32 a = new WrapperPosit32(null).fromString("-4.5", true);
        assertEquals(-4.5f, a.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMin() {
        WrapperPosit32 a = positWrapperFromFloat(3);
        WrapperPosit32 b = positWrapperFromFloat(5);
        WrapperPosit32 min = a.math_min(b);
        assertEquals(3, min.toDouble(), ERROR_TOLERANCE);
    }

    @Test
    public void testMax() {
        WrapperPosit32 a = positWrapperFromFloat(3);
        WrapperPosit32 b = positWrapperFromFloat(5);
        WrapperPosit32 max = a.math_max(b);
        assertEquals(5, max.toDouble(), ERROR_TOLERANCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMinException() {
        WrapperPosit32 number1 = positWrapperFromFloat(3);
        ACojacWrapper number2 = new WrapperBigDecimal(null);

        number1.math_min(number2);
        fail("No exception threw");
    }

    @Test
    public void testMath_fma() {
        WrapperPosit32 a = positWrapperFromFloat(3);
        WrapperPosit32 b = positWrapperFromFloat(5);
        WrapperPosit32 c = positWrapperFromFloat(-3.5f);
        WrapperPosit32 result = a.math_fma(b, c);
        assertEquals(Math.fma(3, 5, -3.5f), result.toDouble(), ERROR_TOLERANCE);
    }
}