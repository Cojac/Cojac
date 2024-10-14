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

import com.github.cojac.models.behaviours.*;

public class CheckedOperations implements MathOperations {
    @Override
    public int iadd(int a, int b) {
        return CheckedIntBehaviour.IADD(a, b);
    }

    @Override
    public int isub(int a, int b) {
        return CheckedIntBehaviour.ISUB(a, b);
    }

    @Override
    public int imul(int a, int b) {
        return CheckedIntBehaviour.IMUL(a, b);
    }

    @Override
    public int idiv(int a, int b) {
        return CheckedIntBehaviour.IDIV(a, b);
    }

    @Override
    public int iinc(int a, int b) {
        return CheckedIntBehaviour.IINC(a, b);
    }

    @Override
    public int ineg(int a) {
        return CheckedIntBehaviour.INEG(a);
    }

    @Override
    public long ladd(long a, long b) {
        return CheckedLongBehaviour.LADD(a, b);
    }

    @Override
    public long lsub(long a, long b) {
        return CheckedLongBehaviour.LSUB(a, b);
    }

    @Override
    public long lmul(long a, long b) {
        return CheckedLongBehaviour.LMUL(a, b);
    }

    @Override
    public long ldiv(long a, long b) {
        return CheckedLongBehaviour.LDIV(a, b);
    }

    @Override
    public long lneg(long a) {
        return CheckedLongBehaviour.LNEG(a);
    }

    @Override
    public double dadd(double a, double b) {
        return CheckedDoubleBehaviour.DADD(a, b);
    }

    @Override
    public double dsub(double a, double b) {
        return CheckedDoubleBehaviour.DSUB(a, b);
    }

    @Override
    public double dmul(double a, double b) {
        return CheckedDoubleBehaviour.DMUL(a, b);
    }

    @Override
    public double ddiv(double a, double b) {
        return CheckedDoubleBehaviour.DDIV(a, b);
    }

    @Override
    public float fadd(float a, float b) {
        return CheckedFloatBehaviour.FADD(a, b);
    }

    @Override
    public float fsub(float a, float b)  {
        return CheckedFloatBehaviour.FSUB(a, b);
    }

    @Override
    public float fmul(float a, float b)  {
        return CheckedFloatBehaviour.FMUL(a, b);
    }

    @Override
    public float fdiv(float a, float b) {
        return CheckedFloatBehaviour.FDIV(a, b);
    }

    @Override
    public int l2i(long a) {
        return CheckedCastBehaviour.L2I(a);
    }

    @Override
    public short i2s(int a) {
        return (short) CheckedCastBehaviour.I2S(a);
    }

    @Override
    public byte i2b(int a) {
        return (byte) CheckedCastBehaviour.I2B(a);
    }

    @Override
    public char i2c(int a) {
        return (char) CheckedCastBehaviour.I2C(a);
    }

    @Override
    public int d2i(double a) {
        return CheckedCastBehaviour.D2I(a);
    }

    @Override
    public long d2l(double a) {
        return CheckedCastBehaviour.D2L(a);
    }

    @Override
    public int f2i(float a) {
        return CheckedCastBehaviour.F2I(a);
    }

    @Override
    public long f2l(float a) {
        return CheckedCastBehaviour.F2L(a);
    }

    @Override
    public float d2f(double a) {
        return CheckedCastBehaviour.D2F(a);
    }

    @Override
    public float i2f(int a) {
        return CheckedCastBehaviour.I2F(a);
    }

    @Override
    public double l2d(long a) {
        return CheckedCastBehaviour.L2D(a);
    }

    @Override
    public double pow(double a, double b) throws Exception {
        return CheckedMathBehaviour.pow(a, b);
    }

    @Override
    public double asin(double a) throws Exception {
        return CheckedMathBehaviour.asin(a);
    }

    @Override
    public double exp(double a) throws Exception {
        return CheckedMathBehaviour.exp(a);
    }

    @Override
    public double log(double a) throws Exception {
        return CheckedMathBehaviour.log(a);
    }

    @Override
    public float frem(float a, float b) {
        return CheckedFloatBehaviour.FREM(a, b);
    }

    @Override
    public int fcmpl(float a, float b) {
        return CheckedFloatBehaviour.FCMPL(a, b);
    }

    @Override
    public int fcmpg(float a, float b) {
        return CheckedFloatBehaviour.FCMPG(a, b);
    }

    @Override
    public double drem(double a, double b) {
        return CheckedDoubleBehaviour.DREM(a, b);
    }

    @Override
    public int dcmpg(double a, double b) {
        return CheckedDoubleBehaviour.DCMPG(a, b);
    }

    @Override
    public int dcmpl(double a, double b) {
        return CheckedDoubleBehaviour.DCMPL(a, b);
    }
}