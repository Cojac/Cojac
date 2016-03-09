/*
 * *
 *    Copyright 2011 Baptiste Wicht & Frédéric Bapst
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

package com.github.cojac.unit;

import org.junit.Test;

import static com.github.cojac.models.CheckedDoubles.*;
import static java.lang.Double.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

//import static junit.framework.Assert.fail;

public abstract class AbstractFullTests {
    public abstract Tests getTests();

    @Test(expected = ArithmeticException.class)
    public void iadd0() throws Throwable {
        getTests().iadd(Integer.MAX_VALUE, 2);
    }

    @Test(expected = ArithmeticException.class)
    public void iadd1() throws Throwable {
        getTests().iadd(Integer.MAX_VALUE, 1);
    }

    @Test(expected = ArithmeticException.class)
    public void iadd2() throws Throwable {
        getTests().iadd(1, Integer.MAX_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void iadd3() throws Throwable {
        getTests().iadd(Integer.MAX_VALUE, Integer.MAX_VALUE); //2147483647
    }

    @Test
    public void iadd4() throws Throwable {
        int result = getTests().iadd(1555, 2222);
        assertEquals(1555 + 2222, result);
    }

    @Test(expected = ArithmeticException.class)
    public void iadd5() throws Throwable {
        getTests().iadd(1000000000, 2000000000);
    }

    @Test(expected = ArithmeticException.class)
    public void iadd6() throws Throwable {
        getTests().iadd(-1000000000, -2000000000);
    }

    public void iadd7() throws Throwable {
        int result = getTests().iadd(1000000000, 1000000000);
        assertEquals(1000000000 + 1000000000, result);
    }

    @Test(expected = ArithmeticException.class)
    public void isub1() throws Throwable {
        getTests().isub(Integer.MIN_VALUE, 1);
    }

    @Test(expected = ArithmeticException.class)
    public void isub2() throws Throwable {
        getTests().isub(-1000, Integer.MAX_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void isub3() throws Throwable {
        getTests().isub(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Test
    public void isub4() throws Throwable {
        int result = getTests().isub(1555, 2222);
        assertEquals(1555 - 2222, result);
    }

    @Test(expected = ArithmeticException.class)
    public void isub5() throws Throwable {
        getTests().isub(-1000000000, 2000000000);
    }

    @Test(expected = ArithmeticException.class)
    public void imul1() throws Throwable {
        getTests().imul(Integer.MAX_VALUE, 2);
    }

    @Test(expected = ArithmeticException.class)
    public void imul2() throws Throwable {
        getTests().imul(Integer.MIN_VALUE, 2);
    }

    @Test(expected = ArithmeticException.class)
    public void imul3() throws Throwable {
        getTests().imul(2, Integer.MAX_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void imul4() throws Throwable {
        getTests().imul(2, Integer.MIN_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void imul5() throws Throwable {
        getTests().imul(1000000, 1000000);
    }

    @Test
    public void imul6() throws Throwable {
        int result = getTests().imul(1555, 2222);
        assertEquals(1555 * 2222, result);
    }

    @Test(expected = ArithmeticException.class)
    public void idiv1() throws Throwable {
        getTests().idiv(Integer.MIN_VALUE, -1);
    }

    @Test
    public void idiv2() throws Throwable {
        int result = getTests().idiv(1555, 2222);
        assertEquals(1555 / 2222, result);
    }

    @Test(expected = ArithmeticException.class)
    public void iinc1() throws Throwable {
        getTests().iinc(Integer.MAX_VALUE, 1);
    }

    @Test(expected = ArithmeticException.class)
    public void iinc2() throws Throwable {
        getTests().iinc(1, Integer.MAX_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void iinc3() throws Throwable {
        getTests().iinc(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Test
    public void iinc4() throws Throwable {
        int result = getTests().iinc(1555, 2222);
        assertEquals(1555 + 2222, result);
    }

    @Test(expected = ArithmeticException.class)
    public void iinc5() throws Throwable {
        getTests().iinc(1000000000, 2000000000);
    }

    @Test(expected = ArithmeticException.class)
    public void iinc6() throws Throwable {
        getTests().iinc(-1000000000, -2000000000);
    }

    public void iinc7() throws Throwable {
        int result = getTests().iinc(1000000000, 1000000000);
        assertEquals(1000000000 + 1000000000, result);
    }

    @Test(expected = ArithmeticException.class)
    public void ineg1() throws Throwable {
        getTests().ineg(Integer.MIN_VALUE);
    }

    @Test
    public void ineg2() throws Throwable {
        int result = getTests().ineg(1555);
        assertEquals(-1555, result);
    }

    @Test(expected = ArithmeticException.class)
    public void ladd1() throws Exception {
        getTests().ladd(Long.MAX_VALUE, 1);
    }

    @Test(expected = ArithmeticException.class)
    public void ladd2() throws Exception {
        getTests().ladd(1, Long.MAX_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void ladd3() throws Exception {
        getTests().ladd(Long.MAX_VALUE, Long.MAX_VALUE);
    }

    @Test
    public void ladd4() throws Exception {
        long result = getTests().ladd(1555, 2222);
        assertEquals(1555 + 2222, result);
    }

    @Test(expected = ArithmeticException.class)
    public void ladd5() throws Exception {
        getTests().ladd(5000000000000000000L, 5000000000000000000L);
    }

    @Test(expected = ArithmeticException.class)
    public void lsub1() throws Exception {
        getTests().lsub(Long.MIN_VALUE, 1);
    }

    @Test(expected = ArithmeticException.class)
    public void lsub2() throws Exception {
        getTests().lsub(-1000, Long.MAX_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void lsub3() throws Exception {
        getTests().lsub(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Test
    public void lsub4() throws Exception {
        long result = getTests().lsub(1555, 2222);
        assertEquals(1555 - 2222, result);
    }

    @Test(expected = ArithmeticException.class)
    public void lsub5() throws Exception {
        getTests().lsub(-5000000000000000000L, 5000000000000000000L);
    }

    @Test(expected = ArithmeticException.class)
    public void lmul1() throws Exception {
        getTests().lmul(Long.MAX_VALUE, 2);
    }

    @Test(expected = ArithmeticException.class)
    public void lmul2() throws Exception {
        getTests().lmul(Long.MIN_VALUE, 2);
    }

    @Test(expected = ArithmeticException.class)
    public void lmul3() throws Exception {
        getTests().lmul(2, Long.MAX_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void lmul4() throws Exception {
        getTests().lmul(2, Long.MIN_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void lmul5() throws Exception {
        getTests().lmul(1000000000000000000L, 1000000000000000000L);
    }

    @Test
    public void lmul6() throws Exception {
        long result = getTests().lmul(1555, 2222);
        assertEquals(1555 * 2222, result);
    }

    @Test(expected = ArithmeticException.class)
    public void ldiv1() throws Exception {
        getTests().ldiv(Long.MIN_VALUE, -1);
    }

    @Test
    public void ldiv2() throws Exception {
        long result = getTests().ldiv(1555, 2222);
        assertEquals(1555 / 2222, result);
    }

    @Test(expected = ArithmeticException.class)
    public void lneg1() throws Exception {
        getTests().lneg(Long.MIN_VALUE);
    }

    @Test
    public void lneg2() throws Exception {
        long result = getTests().lneg(1555);
        assertEquals(-1555, result);
    }

    @Test
    public void dadd1() throws Exception {
        double r = getTests().dadd(5555.5555, 77777.7777);
        assertEquals(5555.5555 + 77777.7777, r);
    }

    @Test
    public void dadd2() throws Exception {
        double r = getTests().dadd(3456.098, 0);
        assertEquals(3456.098, r);
    }

    @Test(expected = ArithmeticException.class)
    public void dadd3() throws Exception {
        getTests().dadd(10000000000000000000D, Double.MIN_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void dadd4() throws Exception {
        getTests().dadd(Double.MAX_VALUE, 1000);
    }

    @Test(expected = ArithmeticException.class)
    public void dadd5() throws Exception {
        getTests().dadd(-Double.MAX_VALUE, -1000D);
    }

    @Test(expected = ArithmeticException.class)
    public void dadd6() throws Exception {
        getTests().dadd(1.0D, 1.0E-30D);
    }

    @Test(expected = ArithmeticException.class)
    public void dadd7() throws Exception {
        getTests().dadd(1.0E-30D, 1.0D);
    }


    @Test(expected = ArithmeticException.class)
    public void dadd8() throws Exception {
        double a = 1.1111E23;
        double b = a + CANCELLATION_ULP_FACTOR_DOUBLE * Math.ulp(a);
        getTests().dadd(a, -b);   // cancellation
        //if (CANCELLATION_ULP_FACTOR_DOUBLE==0.0f) throw new ArithmeticException();
    }

    @Test(expected = ArithmeticException.class)
    public void dadd9() throws Exception {
        double a = 1.1111E-23;
        double b = a + CANCELLATION_ULP_FACTOR_DOUBLE * Math.ulp(a);
        getTests().dadd(a, -b);   // cancellation
    }

    @Test
    public void dadd10() throws Exception {
        double r;
        r = getTests().dadd(POSITIVE_INFINITY, 2);
        assertEquals(POSITIVE_INFINITY, r);
        r = getTests().dadd(NEGATIVE_INFINITY, 2);
        assertEquals(NEGATIVE_INFINITY, r);
        r = getTests().dadd(2, POSITIVE_INFINITY);
        assertEquals(POSITIVE_INFINITY, r);
        r = getTests().dadd(2, NEGATIVE_INFINITY);
        assertEquals(NEGATIVE_INFINITY, r);
        r = getTests().dadd(NaN, 2);
        assertTrue(isNaN(r));
        r = getTests().dadd(2, NaN);
        assertTrue(isNaN(r));
    }

    @Test
    public void dadd11() throws Exception {
        double r;
        r = getTests().dadd(2, -2);
        assertEquals(0.0, r);
    }

    @Test
    public void dsub1() throws Exception {
        double r = getTests().dsub(5555.5555, 77777.7777);
        assertEquals(5555.5555 - 77777.7777, r);
    }

    @Test
    public void dsub2() throws Exception {
        double r = getTests().dsub(3456.098, 0);
        assertEquals(3456.098, r);
    }

    @Test(expected = ArithmeticException.class)
    public void dsub3() throws Exception {
        getTests().dsub(10000000000000000000D, Double.MIN_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void dsub4() throws Exception {
        getTests().dsub(Double.MAX_VALUE, -1000);
    }

    @Test(expected = ArithmeticException.class)
    public void dsub5() throws Exception {
        getTests().dsub(-Double.MAX_VALUE, 1000D);
    }

    @Test(expected = ArithmeticException.class)
    public void dsub6() throws Exception {
        getTests().dsub(1.0D, 1.0E-30D);
    }

    @Test(expected = ArithmeticException.class)
    public void dsub7() throws Exception {
        getTests().dsub(1.0E-30D, 1.0D);
    }

    @Test(expected = ArithmeticException.class)
    public void dsub8() throws Exception {
        double a = 1.1111E23;
        double b = a + CANCELLATION_ULP_FACTOR_DOUBLE * Math.ulp(a);
        getTests().dsub(a, b);  // cancellation
    }

    @Test(expected = ArithmeticException.class)
    public void dsub9() throws Exception {
        double a = 1.1111E-23;
        double b = a + CANCELLATION_ULP_FACTOR_DOUBLE * Math.ulp(a);
        getTests().dsub(a, b);  // cancellation
    }

    @Test
    public void dsub10() throws Exception {
        double r;
        r = getTests().dsub(POSITIVE_INFINITY, 2);
        assertEquals(POSITIVE_INFINITY, r);
        r = getTests().dsub(NEGATIVE_INFINITY, 2);
        assertEquals(NEGATIVE_INFINITY, r);
        r = getTests().dsub(2, POSITIVE_INFINITY);
        assertEquals(NEGATIVE_INFINITY, r);
        r = getTests().dsub(2, NEGATIVE_INFINITY);
        assertEquals(POSITIVE_INFINITY, r);
        r = getTests().dsub(NaN, 2);
        assertTrue(isNaN(r));
        r = getTests().dsub(2, NaN);
        assertTrue(isNaN(r));
    }

    @Test
    public void dsub11() throws Exception {
        double r;
        r = getTests().dsub(2.0, 2.0);
        assertEquals(0.0, r);
    }


    @Test
    public void dmul1() throws Exception {
        double r = getTests().dmul(5555.5555, 77777.7777);
        assertEquals(5555.5555 * 77777.7777, r);
        assertEquals(0.0, getTests().dmul(0.0, 7.7));
        assertEquals(0.0, getTests().dmul(7.7, 0.0));
    }

    @Test
    public void dmul2() throws Exception {
        double r = getTests().dmul(3456.098, 1);
        assertEquals(3456.098, r);
    }

    @Test(expected = ArithmeticException.class)
    public void dmul4() throws Exception {
        getTests().dmul(Double.MAX_VALUE, 1000);
    }

    @Test(expected = ArithmeticException.class)
    public void dmul5() throws Exception {
        getTests().dmul(Double.MAX_VALUE, -1000);
    }

    @Test(expected = ArithmeticException.class)
    public void dmul6() throws Exception {
        getTests().dmul(Double.MIN_VALUE, 0.25);  //underflow
    }

    @Test
    public void ddiv1() throws Exception {
        double r = getTests().ddiv(5555.5555, 77777.7777);
        assertEquals(5555.5555 / 77777.7777, r);
        assertEquals(0.0, getTests().ddiv(0.0, 7.7));
    }

    @Test
    public void ddiv2() throws Exception {
        double r = getTests().ddiv(3456.098, 1);
        assertEquals(3456.098, r);
    }

    @Test(expected = ArithmeticException.class)
    public void ddiv4() throws Exception {
        getTests().ddiv(Double.MAX_VALUE, 0.005);
    }

    @Test(expected = ArithmeticException.class)
    public void ddiv5() throws Exception {
        getTests().ddiv(Double.MAX_VALUE, -0.005);
    }

    @Test(expected = ArithmeticException.class)
    public void ddiv6() throws Exception {
        getTests().ddiv(1.0, 0.0);
    }

    @Test(expected = ArithmeticException.class)
    public void ddiv7() throws Exception {
        getTests().ddiv(0.0, 0.0);
    }

    @Test(expected = ArithmeticException.class)
    public void ddiv8() throws Exception {
        getTests().ddiv(Double.MIN_VALUE, 4);  // Underflow
    }

    @Test(expected = ArithmeticException.class)
    public void drem1() throws Exception {
        getTests().drem(1234D, 0.0D);
    }

    @Test
    public void drem2() throws Exception {
        double r = getTests().drem(3456.098D, 1.088D);
        assertEquals(3456.098D % 1.088D, r);
    }

    @Test
    public void drem3() throws Exception {
        double a = 1.17E+83;
        double b = 2 * Math.ulp(a);
        double r = getTests().drem(a, b);
        assertEquals(a % b, r);
    }


    @Test(expected = ArithmeticException.class)
    public void drem4() throws Exception {
        getTests().drem(1.17E+83, 2.2);
    }


    @Test
    public void fadd1() throws Exception {
        float r = getTests().fadd(5555.5555F, 77777.7777F);
        assertEquals(5555.5555F + 77777.7777F, r);
    }

    @Test
    public void fadd2() throws Exception {
        float r = getTests().fadd(3456.098F, 0F);
        assertEquals(3456.098F, r);
    }

    @Test(expected = ArithmeticException.class)
    public void fadd3() throws Exception {   // smearing
        getTests().fadd(10000000000000000000F, Float.MIN_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void fadd4() throws Exception {  // smearing
        getTests().fadd(Float.MAX_VALUE, 1000F);
    }

    @Test(expected = ArithmeticException.class)
    public void fadd5() throws Exception {  // smearing
        getTests().fadd(-Float.MAX_VALUE, -1000F);
    }

    @Test(expected = ArithmeticException.class)
    public void fadd6() throws Exception {  // smearing
        getTests().fadd(1.0F, 1.0E-30F);
    }

    @Test(expected = ArithmeticException.class)
    public void fadd7() throws Exception {  // smearing
        getTests().fadd(1.0E-30F, 1.0F);
    }


    @Test(expected = ArithmeticException.class)
    public void fadd8() throws Exception {
        float a = 1.1111E23f;
        float b = a + CANCELLATION_ULP_FACTOR_FLOAT * Math.ulp(a);
        getTests().fadd(a, -b);   // cancellation
    }

    @Test(expected = ArithmeticException.class)
    public void fadd9() throws Exception {
        float a = 1.1111E-23f;
        float b = a + CANCELLATION_ULP_FACTOR_FLOAT * Math.ulp(a);
        getTests().fadd(a, -b);  // cancellation
    }

    @Test
    public void fadd10() throws Exception {
        float r;
        float POS_INF = Float.POSITIVE_INFINITY, NEG_INF = Float.NEGATIVE_INFINITY;
        r = getTests().fadd(POS_INF, 2);
        assertEquals(POS_INF, r);
        r = getTests().fadd(NEG_INF, 2);
        assertEquals(NEG_INF, r);
        r = getTests().fadd(2, POS_INF);
        assertEquals(POS_INF, r);
        r = getTests().fadd(2, NEG_INF);
        assertEquals(NEG_INF, r);
        r = getTests().fadd(Float.NaN, 2);
        assertTrue(Float.isNaN(r));
        r = getTests().fadd(2, Float.NaN);
        assertTrue(Float.isNaN(r));
    }

    @Test
    public void fadd11() throws Exception {
        float r;
        r = getTests().fadd(2f, -2f);
        assertEquals(0.0f, r);
    }


    @Test
    public void fsub1() throws Exception {
        float r = getTests().fsub(5555.5555F, 77777.7777F);
        assertEquals(5555.5555F - 77777.7777F, r);
    }

    @Test
    public void fsub2() throws Exception {
        float r = getTests().fsub(3456.098F, 0F);
        assertEquals(3456.098F, r);
    }

    @Test(expected = ArithmeticException.class)
    public void fsub3() throws Exception {
        getTests().fsub(10000000000000000000F, Float.MIN_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void fsub4() throws Exception {
        getTests().fsub(Float.MAX_VALUE, -1000F);
    }

    @Test(expected = ArithmeticException.class)
    public void fsub5() throws Exception {
        getTests().fsub(-Float.MAX_VALUE, 1000F);
    }

    @Test(expected = ArithmeticException.class)
    public void fsub6() throws Exception {
        getTests().fsub(1.0F, 1.0E-30F);
    }

    @Test(expected = ArithmeticException.class)
    public void fsub7() throws Exception {
        getTests().fsub(1.0E-30F, 1.0F);
    }

    @Test(expected = ArithmeticException.class)
    public void fsub8() throws Exception {
        float a = 1.1111E23f;
        float b = a + CANCELLATION_ULP_FACTOR_FLOAT * Math.ulp(a);
        getTests().fsub(a, b);   // cancellation
    }

    @Test(expected = ArithmeticException.class)
    public void fsub9() throws Exception {
        float a = 1.1111E-23f;
        float b = a + CANCELLATION_ULP_FACTOR_FLOAT * Math.ulp(a);
        getTests().fsub(a, b);   // cancellation
    }

    @Test
    public void fsub10() throws Exception {
        float r;
        float POS_INF = Float.POSITIVE_INFINITY, NEG_INF = Float.NEGATIVE_INFINITY;
        r = getTests().fsub(POS_INF, 2);
        assertEquals(POS_INF, r);
        r = getTests().fsub(NEG_INF, 2);
        assertEquals(NEG_INF, r);
        r = getTests().fsub(2, POS_INF);
        assertEquals(NEG_INF, r);
        r = getTests().fsub(2, NEG_INF);
        assertEquals(POS_INF, r);
        r = getTests().fsub(Float.NaN, 2);
        assertTrue(Float.isNaN(r));
        r = getTests().fsub(2, Float.NaN);
        assertTrue(Float.isNaN(r));
    }

    @Test
    public void fsub11() throws Exception {
        float r;
        r = getTests().fsub(2f, 2f);
        assertEquals(0.0f, r);
    }

    @Test
    public void fmul1() throws Exception {
        float r = getTests().fmul(5555.5555F, 77777.7777F);
        assertEquals(5555.5555F * 77777.7777F, r);
        assertEquals(0.0f, getTests().fmul(0.0f, 7.7f));
        assertEquals(0.0f, getTests().fmul(7.7f, 0.0f));
    }

    @Test
    public void fmul2() throws Exception {
        float r = getTests().fmul(3456.098F, 1F);
        assertEquals(3456.098F, r);
    }

    @Test(expected = ArithmeticException.class)
    public void fmul4() throws Exception {
        getTests().fmul(Float.MAX_VALUE, 1000F);
    }

    @Test(expected = ArithmeticException.class)
    public void fmul5() throws Exception {
        getTests().fmul(Float.MAX_VALUE, -1000F);
    }

    @Test(expected = ArithmeticException.class)
    public void fmul6() throws Exception {
        getTests().fmul(Float.MIN_VALUE, 0.25f); //Underflow
    }

    @Test
    public void fdiv1() throws Exception {
        float r = getTests().fdiv(5555.5555F, 77777.7777F);
        assertEquals(5555.5555F / 77777.7777F, r);
        assertEquals(0.0f, getTests().fdiv(0.0f, 7.7f));
    }

    @Test
    public void fdiv2() throws Exception {
        float r = getTests().fdiv(3456.098F, 1F);
        assertEquals(3456.098F, r);
    }

    @Test(expected = ArithmeticException.class)
    public void fdiv4() throws Exception {
        getTests().fdiv(Float.MAX_VALUE, 0.005F);
    }

    @Test(expected = ArithmeticException.class)
    public void fdiv5() throws Exception {
        getTests().fdiv(Float.MAX_VALUE, -0.005F);
    }

    @Test(expected = ArithmeticException.class)
    public void fdiv6() throws Exception {
        getTests().fdiv(1.0F, 0.0F);
    }

    @Test(expected = ArithmeticException.class)
    public void fdiv7() throws Exception {
        getTests().fdiv(0.0F, 0.0F);
    }

    @Test(expected = ArithmeticException.class)
    public void fdiv8() throws Exception {
        getTests().fdiv(Float.MIN_VALUE, 4f); //Underflow
    }

    @Test(expected = ArithmeticException.class)
    public void frem1() throws Exception {
        getTests().frem(1234F, 0.0F);
    }

    @Test
    public void frem2() throws Exception {
        float r = getTests().frem(3456.098F, 1.088F);
        assertEquals(3456.098F % 1.088F, r);
    }

    @Test
    public void frem3() throws Exception {
        float a = 1.17E+23f;
        float b = 2 * Math.ulp(a);
        float r = getTests().frem(a, b);
        assertEquals(a % b, r);
    }


    @Test(expected = ArithmeticException.class)
    public void frem4() throws Exception {
        getTests().frem(1.17E+23f, 2.2f);
    }


    @Test(expected = ArithmeticException.class)
    public void l2i1() throws Exception {
        getTests().l2i(2L * Integer.MAX_VALUE);
    }

    @Test
    public void l2i2() throws Exception {
        int i = getTests().l2i(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, i);
    }

    @Test
    public void l2i3() throws Exception {
        int i = getTests().l2i(5555L);
        assertEquals(5555, i);
    }

    @Test(expected = ArithmeticException.class)
    public void i2s1() throws Exception {
        getTests().i2s(2 * Short.MAX_VALUE);
    }

    @Test
    public void i2s2() throws Exception {
        short i = getTests().i2s(Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, i);
    }

    @Test
    public void i2s3() throws Exception {
        short i = getTests().i2s(2222);
        assertEquals(2222, i);
    }

    @Test(expected = ArithmeticException.class)
    public void i2c1() throws Exception {
        getTests().i2c(2 * Character.MAX_VALUE);
    }

    @Test
    public void i2c2() throws Exception {
        char i = getTests().i2c(Character.MAX_VALUE);
        assertEquals(Character.MAX_VALUE, i);
    }

    @Test
    public void i2c3() throws Exception {
        char i = getTests().i2c(2222);
        assertEquals(2222, i);
    }

    @Test(expected = ArithmeticException.class)
    public void i2b1() throws Exception {
        getTests().i2b(2 * Byte.MAX_VALUE);
    }

    @Test
    public void i2b2() throws Exception {
        byte i = getTests().i2b(Byte.MAX_VALUE);
        assertEquals(Byte.MAX_VALUE, i);
    }

    @Test
    public void i2b3() throws Exception {
        byte i = getTests().i2b(100);
        assertEquals(100, i);
    }

    @Test(expected = ArithmeticException.class)
    public void d2i1() throws Exception {
        getTests().d2i(2D * Integer.MAX_VALUE);
    }

    @Test
    public void d2i2() throws Exception {
        int i = getTests().d2i(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, i);
    }

    @Test
    public void d2i3() throws Exception {
        int i = getTests().d2i(5555D);
        assertEquals(5555, i);
    }

    @Test(expected = ArithmeticException.class)
    public void d2i4() throws Exception {
        getTests().d2i(Double.NaN);
    }

    @Test(expected = ArithmeticException.class)
    public void d2l1() throws Exception {
        getTests().d2l(2D * Long.MAX_VALUE);
    }

    @Test
    public void d2l2() throws Exception {
        long i = getTests().d2l(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, i);
    }

    @Test
    public void d2l3() throws Exception {
        long i = getTests().d2l(5555D);
        assertEquals(5555, i);
    }

    @Test(expected = ArithmeticException.class)
    public void d2l4() throws Exception {
        getTests().d2l(Double.NaN);
    }

    @Test(expected = ArithmeticException.class)
    public void f2i1() throws Exception {
        getTests().f2i(2F * Integer.MAX_VALUE);
    }

    @Test
    public void f2i2() throws Exception {
        int i = getTests().f2i(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, i);
    }

    @Test
    public void f2i3() throws Exception {
        int i = getTests().f2i(5555F);
        assertEquals(5555, i);
    }

    @Test(expected = ArithmeticException.class)
    public void f2i4() throws Exception {
        getTests().f2i(Float.NaN);
    }

    @Test(expected = ArithmeticException.class)
    public void f2l1() throws Exception {
        getTests().f2l(2F * Long.MAX_VALUE);
    }

    @Test
    public void f2l2() throws Exception {
        long i = getTests().f2l(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, i);
    }

    @Test
    public void f2l3() throws Exception {
        long i = getTests().f2l(5555F);
        assertEquals(5555, i);
    }

    @Test(expected = ArithmeticException.class)
    public void f2l4() throws Exception {
        getTests().f2l(Float.NaN);
    }

    @Test(expected = ArithmeticException.class)
    public void d2f1() throws Exception {
        getTests().d2f(2D * Float.MAX_VALUE);
    }

    @Test
    public void d2f2() throws Exception {
        float i = getTests().d2f(Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, i);
    }

    @Test
    public void d2f3() throws Exception {
        float i = getTests().d2f(5555D);
        assertEquals(5555F, i);
    }

    @Test
    public void d2f4() throws Exception {
        float f = getTests().d2f(Double.NaN);
        assertTrue(Float.isNaN(f));
    }

    @Test(expected = ArithmeticException.class)
    public void d2f5() throws Exception {
        getTests().d2f(Double.POSITIVE_INFINITY);
    }

    @Test(expected = ArithmeticException.class)
    public void d2f6() throws Exception {
        getTests().d2f(Double.NEGATIVE_INFINITY);
    }
    
    @Test
    public void i2f1() throws Exception {
        getTests().i2f(-123);
        getTests().i2f(+123);
        getTests().i2f(-123_456);
        getTests().i2f(+123_456);
    }
    
    @Test(expected = ArithmeticException.class)
    public void i2f2() throws Exception {
        getTests().i2f(Integer.MAX_VALUE-10);
    }

    @Test
    public void l2d1() throws Exception {
        getTests().l2d(-123L);
        getTests().l2d(+123L);
        getTests().l2d(-123_456L);
        getTests().l2d(+123_456L);
        getTests().l2d(-123_456_789_123_456L);
        getTests().l2d(+123_456_789_123_456L);
    }

    @Test(expected = ArithmeticException.class)
    public void l2d2() throws Exception {
        getTests().l2d(Long.MAX_VALUE-10);
    }


    @Test
    public void pow() throws Exception {
        double pow = getTests().pow(10, 4);
        assertEquals(10000D, pow);
    }

    @Test(expected = ArithmeticException.class)
    public void pow2() throws Exception {
        getTests().pow(10, 1000);
    }

    @Test
    public void asin1() throws Exception {
        double asin = getTests().asin(0.4);
        assertEquals(StrictMath.asin(0.4), asin);
    }

    @Test(expected = ArithmeticException.class)
    public void asin2() throws Exception {
        getTests().asin(-2);
    }

    @Test
    public void exp1() throws Exception {
        double exp = getTests().exp(2);
        assertEquals(StrictMath.exp(2), exp);
    }

    @Test(expected = ArithmeticException.class)
    public void exp2() throws Exception {
        getTests().exp(Double.POSITIVE_INFINITY);
    }

    @Test
    public void log1() throws Exception {
        double log = getTests().log(2);
        assertEquals(StrictMath.log(2), log);
    }

    @Test(expected = ArithmeticException.class)
    public void log2() throws Exception {
        getTests().log(0);
    }

    @Test
    public void dcmp1() throws Exception {
        assertEquals(-1, getTests().dcmpg(20, 30));
        assertEquals(-1, getTests().dcmpl(20, 30));
        assertEquals(-1, getTests().dcmpg(Double.NEGATIVE_INFINITY, 30));
        assertEquals(-1, getTests().dcmpl(Double.NEGATIVE_INFINITY, 30));
        assertEquals(-1, getTests().dcmpg(20, Double.POSITIVE_INFINITY));
        assertEquals(-1, getTests().dcmpl(20, Double.POSITIVE_INFINITY));
        assertEquals(-1, getTests().dcmpg(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
        assertEquals(-1, getTests().dcmpl(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    @Test
    public void dcmp2() throws Exception {
        assertEquals(1, getTests().dcmpg(40, 30));
        assertEquals(1, getTests().dcmpl(40, 30));
        assertEquals(1, getTests().dcmpg(30, Double.NEGATIVE_INFINITY));
        assertEquals(1, getTests().dcmpl(30, Double.NEGATIVE_INFINITY));
        assertEquals(1, getTests().dcmpg(Double.POSITIVE_INFINITY, 20));
        assertEquals(1, getTests().dcmpl(Double.POSITIVE_INFINITY, 20));
        assertEquals(1, getTests().dcmpg(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
        assertEquals(1, getTests().dcmpl(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    @Test
    public void dcmp3() throws Exception {
        assertEquals(0, getTests().dcmpg(30, 30));
        assertEquals(0, getTests().dcmpl(30, 30));
    }

    @Test
    public void dcmp4() throws Exception {
        assertEquals(0, getTests().dcmpg(1.05, 1.05));
        assertEquals(0, getTests().dcmpl(1.05, 1.05));
    }

    @Test(expected = ArithmeticException.class)
    public void dcmpg5() throws Exception {
        getTests().dcmpg(1.05, 1.05 + CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(1.05));
    }

    @Test(expected = ArithmeticException.class)
    public void dcmpl5() throws Exception {
        getTests().dcmpl(1.05, 1.05 + CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(1.05));
    }

    @Test(expected = ArithmeticException.class)
    public void dcmpg6() throws Exception {
        getTests().dcmpg(1.05 + CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(1.05), 1.05);
    }

    @Test(expected = ArithmeticException.class)
    public void dcmpl6() throws Exception {
        getTests().dcmpl(1.05 + CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(1.05), 1.05);
    }

    @Test
    public void dcmp7() throws Exception {
        assertEquals(+1, getTests().dcmpg(3.0, Double.NaN));
        assertEquals(+1, getTests().dcmpg(Double.NaN, 3.0));
        assertEquals(-1, getTests().dcmpl(3.0, Double.NaN));
        assertEquals(-1, getTests().dcmpl(Double.NaN, 3.0));
    }


    @Test
    public void fcmp1() throws Exception {
        assertEquals(-1, getTests().fcmpg(20f, 30f));
        assertEquals(-1, getTests().fcmpl(20f, 30f));
        assertEquals(-1, getTests().fcmpg(Float.NEGATIVE_INFINITY, 30));
        assertEquals(-1, getTests().fcmpl(Float.NEGATIVE_INFINITY, 30));
        assertEquals(-1, getTests().fcmpg(20, Float.POSITIVE_INFINITY));
        assertEquals(-1, getTests().fcmpl(20, Float.POSITIVE_INFINITY));
        assertEquals(-1, getTests().fcmpg(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY));
        assertEquals(-1, getTests().fcmpl(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY));
    }

    @Test
    public void fcmp2() throws Exception {
        assertEquals(1, getTests().fcmpg(40f, 30f));
        assertEquals(1, getTests().fcmpl(40f, 30f));
        assertEquals(1, getTests().fcmpg(30, Float.NEGATIVE_INFINITY));
        assertEquals(1, getTests().fcmpl(30, Float.NEGATIVE_INFINITY));
        assertEquals(1, getTests().fcmpg(Float.POSITIVE_INFINITY, 20));
        assertEquals(1, getTests().fcmpl(Float.POSITIVE_INFINITY, 20));
        assertEquals(1, getTests().fcmpg(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY));
        assertEquals(1, getTests().fcmpl(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY));
    }

    @Test
    public void fcmp3() throws Exception {
        assertEquals(0, getTests().fcmpg(30f, 30f));
        assertEquals(0, getTests().fcmpl(30f, 30f));
    }

    @Test
    public void fcmp4() throws Exception {
        assertEquals(0, getTests().fcmpg(1.05f, 1.05f));
        assertEquals(0, getTests().fcmpl(1.05f, 1.05f));
    }

    @Test(expected = ArithmeticException.class)
    public void fcmp5g() throws Exception {
        getTests().fcmpg(1.05f, 1.05f + CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(1.05f));
    }

    @Test(expected = ArithmeticException.class)
    public void fcmp5l() throws Exception {
        getTests().fcmpl(1.05f, 1.05f + CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(1.05f));
    }

    @Test(expected = ArithmeticException.class)
    public void fcmp6g() throws Exception {
        getTests().fcmpg(1.05f + CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(1.05f), 1.05f);
    }

    @Test(expected = ArithmeticException.class)
    public void fcmp6l() throws Exception {
        getTests().fcmpl(1.05f + CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(1.05f), 1.05f);
    }

    @Test
    public void fcmp7() throws Exception {
        assertEquals(+1, getTests().fcmpg(3.0f, Float.NaN));
        assertEquals(+1, getTests().fcmpg(Float.NaN, 3.0f));
        assertEquals(-1, getTests().fcmpl(3.0f, Float.NaN));
        assertEquals(-1, getTests().fcmpl(Float.NaN, 3.0f));
    }

}
