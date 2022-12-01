package com.github.cojac.utils;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class Posit32UtilsTest {
    private final static double ERROR_TOLERANCE = 1e-6;

    @BeforeClass
    public static void loadLibrary() {
        Posit32Utils.loadLibrary();
    }

    @Test
    public void testAdd() {
        float a = 1.5f;
        float b = 3.25f;
        float result = Posit32Utils.toFloat(Posit32Utils.add(Posit32Utils.toPosit(a), Posit32Utils.toPosit(b)));
        assertEquals(a + b, result, ERROR_TOLERANCE);
    }

    @Test
    public void testSubstract() {
        float a = 1.5f;
        float b = 3.25f;
        float result = Posit32Utils.toFloat(Posit32Utils.substract(Posit32Utils.toPosit(a), Posit32Utils.toPosit(b)));
        assertEquals(-1.75f, result, ERROR_TOLERANCE);
    }

    @Test
    public void testMultiply() {
        float a = 1.5f;
        float b = 3.25f;
        float result = Posit32Utils.toFloat(Posit32Utils.multiply(Posit32Utils.toPosit(a), Posit32Utils.toPosit(b)));
        assertEquals(4.875f, result, ERROR_TOLERANCE);
    }

    @Test
    public void testDivide() {
        float a = 3.25f;
        float b = 0.5f;
        float result = Posit32Utils.toFloat(Posit32Utils.divide(Posit32Utils.toPosit(a), Posit32Utils.toPosit(b)));
        assertEquals(6.5f, result, ERROR_TOLERANCE);
    }

    @Test
    public void testToFloatAndToPosit() {
        float a = 1296.354f;
        assertEquals(a, Posit32Utils.toFloat(Posit32Utils.toPosit(a)), 0);
    }

    @Test
    public void testSqrt() {
        float a = 2.5f;
        float result = Posit32Utils.toFloat(Posit32Utils.sqrt(Posit32Utils.toPosit(a)));
        assertEquals((float) Math.sqrt(a), result, ERROR_TOLERANCE);
    }

    @Test
    public void testEquals() {
        float a = -5.25f;
        assertTrue(Posit32Utils.equals(Posit32Utils.toPosit(a), Posit32Utils.toPosit(a)));
    }

    @Test
    public void testIsLess() {
        float a = -5.25f;
        float b = 3.6f;
        assertTrue(a + " should be less than " + b, Posit32Utils.isLess(Posit32Utils.toPosit(a),
                Posit32Utils.toPosit(b)));
        assertFalse(b + " should not be less than " + a, Posit32Utils.isLess(Posit32Utils.toPosit(b),
                Posit32Utils.toPosit(a)));
        assertFalse(a + " should not be less than " + a, Posit32Utils.isLess(Posit32Utils.toPosit(a),
                Posit32Utils.toPosit(a)));
    }

    @Test
    public void testIsLessOrEquals() {
        float a = -5.25f;
        float b = 3.6f;
        assertTrue(a + " should be less or equal than " + b, Posit32Utils.isLessOrEquals(Posit32Utils.toPosit(a),
                Posit32Utils.toPosit(b)));
        assertFalse(b + " should not be less or equal than " + a, Posit32Utils.isLessOrEquals(Posit32Utils.toPosit(b),
                Posit32Utils.toPosit(a)));
        assertTrue(a + " should be less or equal than " + a, Posit32Utils.isLessOrEquals(Posit32Utils.toPosit(a),
                Posit32Utils.toPosit(a)));
    }

    @Test
    public void testFma() {
        float a = 1.5f;
        float b = 3.25f;
        float c = -6.75f;
        float result = Posit32Utils.toFloat(Posit32Utils.fma(Posit32Utils.toPosit(a), Posit32Utils.toPosit(b),
                Posit32Utils.toPosit(c)));
        assertEquals(Math.fma(a, b, c), result, ERROR_TOLERANCE);
    }
}