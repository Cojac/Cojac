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

public class SimpleOperations implements MathOperations {
    @Override
    public int iadd(int a, int b) {
        return a + b;
    }

    @Override
    public int isub(int a, int b) {
        return a - b;
    }

    @Override
    public int imul(int a, int b) {
        return a * b;
    }

    @Override
    public int idiv(int a, int b) {
        return a / b;
    }

    @Override
    public int iinc(int a, int b) {
        return a + b;
    }

    @Override
    public int ineg(int a) {
        return -a;
    }

    @Override
    public long ladd(long a, long b) {
        return a + b;
    }

    @Override
    public long lsub(long a, long b) {
        return a - b;
    }

    @Override
    public long lmul(long a, long b) {
        return a * b;
    }

    @Override
    public long ldiv(long a, long b) {
        return a / b;
    }

    @Override
    public long lneg(long a) {
        return -a;
    }

    @Override
    public double dadd(double a, double b) {
        return a + b;
    }

    @Override
    public double dsub(double a, double b) {
        return a - b;
    }

    @Override
    public double dmul(double a, double b) {
        return a * b;
    }

    @Override
    public double ddiv(double a, double b) {
        return a / b;
    }

    @Override
    public float fadd(float a, float b) {
        return a + b;
    }

    @Override
    public float fsub(float a, float b) {
        return a - b;
    }

    @Override
    public float fmul(float a, float b) {
        return a * b;
    }

    @Override
    public float fdiv(float a, float b) {
        return a / b;
    }

    @Override
    public int l2i(long a) {
        return (int) a;
    }

    @Override
    public short i2s(int a) {
        return (short) a;
    }

    @Override
    public byte i2b(int a) {
        return (byte) a;
    }

    @Override
    public char i2c(int a) {
        return (char) a;
    }

    @Override
    public int d2i(double a) {
        return (int) a;
    }

    @Override
    public long d2l(double a) {
        return (long) a;
    }

    @Override
    public int f2i(float a) {
        return (int) a;
    }

    @Override
    public long f2l(float a) {
        return (long) a;
    }

    @Override
    public float d2f(double a) {
        return (float) a;
    }

    @Override
    public float i2f(int a) {
        return (float) a;
    }

    @Override
    public double l2d(long a) {
        return (double) a;
    }

    @Override
    public double pow(double a, double b) throws Exception {
        return Math.pow(a, b);
    }

    @Override
    public double asin(double a) throws Exception {
        return Math.asin(a);
    }

    @Override
    public double exp(double a) throws Exception {
        return Math.exp(a);
    }

    @Override
    public double log(double a) throws Exception {
        return Math.log(a);
    }

    @Override
    public double drem(double a, double b) {
        return a % b;
    }

    @Override
    public int dcmpg(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return +1;
        // do not replace by: Double.compare(a, b);
        return a == b ? 0 : a < b ? -1 : 1;
    }

    @Override
    public int dcmpl(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return -1;
        // do not replace by: Double.compare(a, b);
        return a == b ? 0 : a < b ? -1 : 1;
    }

    @Override
    public float frem(float a, float b) {
        return a % b;
    }

    @Override
    public int fcmpg(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) return +1;
        return a == b ? 0 : a < b ? -1 : 1;
    }

    @Override
    public int fcmpl(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) return -1;
        return a == b ? 0 : a < b ? -1 : 1;
    }

}