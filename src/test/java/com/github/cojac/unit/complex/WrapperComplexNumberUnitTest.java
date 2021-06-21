package com.github.cojac.unit.complex;

import com.github.cojac.models.wrappers.WrapperComplexNumber;
import junit.framework.TestCase;

public class WrapperComplexNumberUnitTest extends TestCase {

    private final static double ERROR_TOLERANCE = 1e-6;

    public void testToDouble() {
        WrapperComplexNumber wrapper = new WrapperComplexNumber(2, 0);
        assertEquals(2.0, wrapper.toDouble(), ERROR_TOLERANCE);
    }

    public void testFromDouble() {
        WrapperComplexNumber a = new WrapperComplexNumber(0, 0);
        WrapperComplexNumber b = a.fromDouble(3, false);
        assertEquals(3.0, b.getReal(), ERROR_TOLERANCE);
        assertEquals(0.0, b.getImaginary(), ERROR_TOLERANCE);
    }

    public void testAsInternalString() {
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
}