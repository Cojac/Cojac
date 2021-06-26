package com.github.cojac.models.wrappers;

import junit.framework.TestCase;

public class WrapperComplexNumberUnitTest extends TestCase {

    private final static double ERROR_TOLERANCE = 1e-6;

    public void testToDouble_real() {
        WrapperComplexNumber wrapper = new WrapperComplexNumber(2, 0);
        assertEquals(2.0, wrapper.toDouble(), ERROR_TOLERANCE);
    }

    public void testToDouble_complex_normalMode() {
        WrapperComplexNumber.setStrictMode(false);
        WrapperComplexNumber wrapper = new WrapperComplexNumber(2, 10);
        assertEquals(2.0, wrapper.toDouble(), ERROR_TOLERANCE);
    }

    public void testToDouble_complex_strictMode() {
        WrapperComplexNumber.setStrictMode(true);
        WrapperComplexNumber wrapper = new WrapperComplexNumber(2, 10);
        try {
            wrapper.toDouble();
            fail("No exception threw");
        } catch (ArithmeticException exception) {
            // success
        }
    }

    public void testFromDouble() {
        WrapperComplexNumber a = new WrapperComplexNumber(0, 0);
        WrapperComplexNumber b = a.fromDouble(3, false);
        assertEquals(3.0, b.getReal(), ERROR_TOLERANCE);
        assertEquals(0.0, b.getImaginary(), ERROR_TOLERANCE);
    }

    public void testAsInternalString() {
        WrapperComplexNumber a = new WrapperComplexNumber(2.5, -3.25);
        String internal = a.asInternalString();
        assertEquals("(2.5, -3.25)", internal);
    }

    public void testDadd() {
        WrapperComplexNumber a = new WrapperComplexNumber(2, -1.25);
        WrapperComplexNumber b = new WrapperComplexNumber(-5.5, 3);
        WrapperComplexNumber c = a.dadd(b);
        assertEquals(-3.5, c.getReal(), ERROR_TOLERANCE);
        assertEquals(1.75, c.getImaginary(), ERROR_TOLERANCE);
    }

    public void testDsub() {
        WrapperComplexNumber a = new WrapperComplexNumber(2, -1.25);
        WrapperComplexNumber b = new WrapperComplexNumber(-5.5, 3);
        WrapperComplexNumber c = a.dsub(b);
        assertEquals(7.5, c.getReal(), ERROR_TOLERANCE);
        assertEquals(-4.25, c.getImaginary(), ERROR_TOLERANCE);
    }

    public void testDmul_Real() {
        WrapperComplexNumber a = new WrapperComplexNumber(2, 0);
        WrapperComplexNumber b = new WrapperComplexNumber(7, 0);
        WrapperComplexNumber c = a.dmul(b);
        assertEquals(14, c.getReal(), ERROR_TOLERANCE);
        assertEquals(0, c.getImaginary(), ERROR_TOLERANCE);
    }

    public void testDmul_Complex() {
        WrapperComplexNumber a = new WrapperComplexNumber(2, 4);
        WrapperComplexNumber b = new WrapperComplexNumber(7, 1);
        // (2 + 4i)(7 + i) = 14 + 28i + 2i + 4i^2 = 10 + 30i
        WrapperComplexNumber c = a.dmul(b);
        assertEquals(10.0, c.getReal(), ERROR_TOLERANCE);
        assertEquals(30.0, c.getImaginary(), ERROR_TOLERANCE);
    }

    public void testDdiv_Real() {
        WrapperComplexNumber a = new WrapperComplexNumber(5, 0);
        WrapperComplexNumber b = new WrapperComplexNumber(-2, 0);
        WrapperComplexNumber c = a.ddiv(b);
        assertEquals(-2.5, c.getReal(), ERROR_TOLERANCE);
        assertEquals(0.0, c.getImaginary(), ERROR_TOLERANCE);
    }

    public void testDdiv_Complex() {
        WrapperComplexNumber a = new WrapperComplexNumber(10, 30);
        WrapperComplexNumber b = new WrapperComplexNumber(7, 1);
        WrapperComplexNumber c = a.ddiv(b);
        assertEquals(2.0, c.getReal(), ERROR_TOLERANCE);
        assertEquals(4.0, c.getImaginary(), ERROR_TOLERANCE);
    }

    public void testDneg() {
        WrapperComplexNumber a = new WrapperComplexNumber(8, -6.5);
        WrapperComplexNumber b = a.dneg();
        assertEquals(-a.getReal(), b.getReal(), ERROR_TOLERANCE);
        assertEquals(-a.getImaginary(), b.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_sqrt_neg() {
        WrapperComplexNumber a = new WrapperComplexNumber(-4, 0);
        WrapperComplexNumber b = a.math_sqrt();
        assertEquals(0.0, b.getReal(), ERROR_TOLERANCE);
        assertEquals(2.0, b.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_sqrt_complex() {
        // (2 - 5i)*(2 - 5i) = 4 - 20i + 25i^2 = -21 - 20i
        WrapperComplexNumber a = new WrapperComplexNumber(-21, -20);
        WrapperComplexNumber b = a.math_sqrt();
        assertEquals(2.0, b.getReal(), ERROR_TOLERANCE);
        assertEquals(-5.0, b.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_cbrt() {
        // (2 - 5i)*(2 - 5i) = 4 - 20i + 25i^2 = -21 - 20i
        WrapperComplexNumber a = new WrapperComplexNumber(-27, -1);
        WrapperComplexNumber b = a.math_cbrt();
        // comparison with solution of https://www.wolframalpha.com/input/?i=%28-27+-+i%29%5E%281%2F3%29
        assertEquals(1.53229538, b.getReal(), ERROR_TOLERANCE);
        assertEquals(-2.57995818, b.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_abs() {
        WrapperComplexNumber a = new WrapperComplexNumber(-3, -4);
        WrapperComplexNumber b = a.math_abs();
        assertEquals(5.0, b.getReal(), ERROR_TOLERANCE);
        assertEquals(0.0, b.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_toRadians() {
        WrapperComplexNumber a = new WrapperComplexNumber(Math.PI, 0);
        WrapperComplexNumber b = a.math_toDegrees();
        assertEquals(180.0, b.getReal(), ERROR_TOLERANCE);
        assertEquals(0.0, b.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_toDegrees() {
        WrapperComplexNumber a = new WrapperComplexNumber(180, 0);
        WrapperComplexNumber b = a.math_toRadians();
        assertEquals(Math.PI, b.getReal(), ERROR_TOLERANCE);
        assertEquals(0.0, b.getImaginary(), ERROR_TOLERANCE);
    }

    public void testIsNaN() {
        WrapperComplexNumber a = new WrapperComplexNumber(0, 0);
        WrapperComplexNumber b = a.ddiv(new WrapperComplexNumber(0, 0));
        assertTrue("Divide by 0 should result in NaN", b.isNaN());
    }

    public void testWrapperName() {
        WrapperComplexNumber a = new WrapperComplexNumber(0, 0);
        String name = a.wrapperName();
        assertEquals("Complex", name);
    }

    public void testDrem() {
        WrapperComplexNumber number = new WrapperComplexNumber(4, 0);
        WrapperComplexNumber remainder = number.drem(new WrapperComplexNumber(3, 3));
        assertEquals(-2, remainder.getReal(), ERROR_TOLERANCE);
        assertEquals(0.0, remainder.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_sin() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_sin();
        assertEquals(0.5309211, number.getReal(), ERROR_TOLERANCE);
        assertEquals(3.5905646, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_cos() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_cos();
        assertEquals(-3.7245455, number.getReal(), ERROR_TOLERANCE);
        assertEquals(0.5118226, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_tan() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_tan();
        assertEquals(-0.0098844, number.getReal(), ERROR_TOLERANCE);
        assertEquals(-0.9653859, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_asin() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_asin();
        assertEquals(0.9646585, number.getReal(), ERROR_TOLERANCE);
        assertEquals(-1.9686379, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_acos() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_acos();
        assertEquals(0.6061378, number.getReal(), ERROR_TOLERANCE);
        assertEquals(1.9686379, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_atan() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_atan();
        assertEquals(1.3389725, number.getReal(), ERROR_TOLERANCE);
        assertEquals(-0.1469467, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_sinh() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_sinh();
        assertEquals(-4.168907, number.getReal(), ERROR_TOLERANCE);
        assertEquals(-9.1544991, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_cosh() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_cosh();
        assertEquals(-4.1896257, number.getReal(), ERROR_TOLERANCE);
        assertEquals(-9.1092279, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_tanh() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_tanh();
        assertEquals(1.0032386, number.getReal(), ERROR_TOLERANCE);
        assertEquals(0.003764, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_exp() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_exp();
        assertEquals(-8.3585327, number.getReal(), ERROR_TOLERANCE);
        assertEquals(-18.263727, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_log() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_log();
        assertEquals(1.2824747, number.getReal(), ERROR_TOLERANCE);
        assertEquals(-0.5880026, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_log10() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2).math_log10();
        assertEquals(0.5569717, number.getReal(), ERROR_TOLERANCE);
        assertEquals(-0.2553663, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testMath_pow() {
        WrapperComplexNumber base = new WrapperComplexNumber(3, -2);
        WrapperComplexNumber power = new WrapperComplexNumber(-1, 1);
        WrapperComplexNumber number = base.math_pow(power);
        assertEquals(-0.1474123, number.getReal(), ERROR_TOLERANCE);
        assertEquals(0.4770829, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testCompareTo_equality() {
        WrapperComplexNumber number1 = new WrapperComplexNumber(3, -2);
        WrapperComplexNumber number2 = new WrapperComplexNumber(3, -2);
        assertEquals(0, number1.compareTo(number2));
    }

    public void testCompareTo_real_smaller() {
        WrapperComplexNumber number1 = new WrapperComplexNumber(-3, 0);
        WrapperComplexNumber number2 = new WrapperComplexNumber(-1, 0);
        assertTrue(number1.compareTo(number2) < 0);
    }

    public void testCompareTo_real_greater() {
        WrapperComplexNumber number1 = new WrapperComplexNumber(3, 0);
        WrapperComplexNumber number2 = new WrapperComplexNumber(-1, 0);
        assertTrue(number1.compareTo(number2) > 0);
    }

    public void testCompareTo_imaginary_normalMode() {
        WrapperComplexNumber.setStrictMode(false);
        WrapperComplexNumber number1 = new WrapperComplexNumber(3, -2);
        WrapperComplexNumber number2 = new WrapperComplexNumber(3, 5);
        assertTrue(number1.compareTo(number2) < 0);
        assertTrue(number2.compareTo(number1) > 0);
    }

    public void testCompareTo_imaginary_strictMode() {
        WrapperComplexNumber.setStrictMode(true);
        WrapperComplexNumber number1 = new WrapperComplexNumber(3, -2);
        WrapperComplexNumber number2 = new WrapperComplexNumber(3, 5);

        try {
            number1.compareTo(number2);
            fail("No exception threw");
        } catch (ArithmeticException exception) {
            // success
        }
    }

    public void testTestIsNaN() {
        WrapperComplexNumber number = new WrapperComplexNumber(4, Double.NaN);
        assertTrue(number.isNaN());
        number = new WrapperComplexNumber(4, -1);
        assertFalse(number.isNaN());
    }

    public void testMath_hypot() {
        WrapperComplexNumber a = new WrapperComplexNumber(3, -2);
        WrapperComplexNumber b = new WrapperComplexNumber(-1, 1);
        WrapperComplexNumber number = a.math_hypot(b);
        assertEquals(3.1516717, number.getReal(), ERROR_TOLERANCE);
        assertEquals(-2.2210435, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testGetReal() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2);
        assertEquals(3, number.getReal(), ERROR_TOLERANCE);
    }

    public void testGetImaginary() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2);
        assertEquals(-2, number.getImaginary(), ERROR_TOLERANCE);
    }

    public void testCOJAC_MAGIC_getReal() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2);
        CommonDouble realCommonDouble = WrapperComplexNumber.COJAC_MAGIC_getReal(new CommonDouble(number));
        WrapperComplexNumber real = (WrapperComplexNumber) realCommonDouble.val;
        assertEquals(3, real.getReal(), ERROR_TOLERANCE);
        assertEquals(0, real.getImaginary(), ERROR_TOLERANCE);
    }

    public void testCOJAC_MAGIC_getImaginary() {
        WrapperComplexNumber number = new WrapperComplexNumber(3, -2);
        CommonDouble realCommonDouble = WrapperComplexNumber.COJAC_MAGIC_getImaginary(new CommonDouble(number));
        WrapperComplexNumber imaginary = (WrapperComplexNumber) realCommonDouble.val;
        assertEquals(-2, imaginary.getReal(), ERROR_TOLERANCE);
        assertEquals(0, imaginary.getImaginary(), ERROR_TOLERANCE);
    }

    public void testCOJAC_MAGIC_equals_equality() {
        WrapperComplexNumber number1 = new WrapperComplexNumber(3, -2);
        WrapperComplexNumber number2 = new WrapperComplexNumber(3, -2);
        assertTrue(WrapperComplexNumber.COJAC_MAGIC_equals(new CommonDouble(number1), new CommonDouble(number2)));
    }

    public void testCOJAC_MAGIC_equals_inequality() {
        WrapperComplexNumber number1 = new WrapperComplexNumber(3, 2);
        WrapperComplexNumber number2 = new WrapperComplexNumber(3, -2);
        assertFalse(WrapperComplexNumber.COJAC_MAGIC_equals(new CommonDouble(number1), new CommonDouble(number2)));
    }

    public void testFromString_toString() {
        WrapperComplexNumber[] numbers = new WrapperComplexNumber[]{
                new WrapperComplexNumber(4, 0),
                new WrapperComplexNumber(0, -5),
                new WrapperComplexNumber(-6.1, 2.5),
        };
        for (WrapperComplexNumber number : numbers) {
            int comparison = number.compareTo(number.fromString(number.toString(), false));
            assertEquals("Should be: number.toString()", 0, comparison);
        }
    }
}