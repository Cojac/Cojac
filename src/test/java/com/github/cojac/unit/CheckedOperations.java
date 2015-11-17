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

import com.github.cojac.models.*;

public class CheckedOperations implements MathOperations {
    @Override
    public int iadd(int a, int b) throws Exception {
        return CheckedInts.checkedIADD(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int isub(int a, int b) throws Exception {
        return CheckedInts.checkedISUB(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int imul(int a, int b) throws Exception {
        return CheckedInts.checkedIMUL(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int idiv(int a, int b) throws Exception {
        return CheckedInts.checkedIDIV(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int iinc(int a, int b) throws Exception {
        return CheckedInts.checkedIINC(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int ineg(int a) throws Exception {
        return CheckedInts.checkedINEG(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long ladd(long a, long b) throws Exception {
        return CheckedLongs.checkedLADD(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long lsub(long a, long b) throws Exception {
        return CheckedLongs.checkedLSUB(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long lmul(long a, long b) throws Exception {
        return CheckedLongs.checkedLMUL(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long ldiv(long a, long b) throws Exception {
        return CheckedLongs.checkedLDIV(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long lneg(long a) throws Exception {
        return CheckedLongs.checkedLNEG(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double dadd(double a, double b) throws Exception {
        return CheckedDoubles.checkedDADD(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double dsub(double a, double b) throws Exception {
        return CheckedDoubles.checkedDSUB(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double dmul(double a, double b) throws Exception {
        return CheckedDoubles.checkedDMUL(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double ddiv(double a, double b) throws Exception {
        return CheckedDoubles.checkedDDIV(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float fadd(float a, float b) throws Exception {
        return CheckedFloats.checkedFADD(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float fsub(float a, float b) throws Exception {
        return CheckedFloats.checkedFSUB(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float fmul(float a, float b) throws Exception {
        return CheckedFloats.checkedFMUL(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float fdiv(float a, float b) throws Exception {
        return CheckedFloats.checkedFDIV(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int l2i(long a) throws Exception {
        return CheckedCasts.checkedL2I(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public short i2s(int a) throws Exception {
        return CheckedCasts.checkedI2S(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public byte i2b(int a) throws Exception {
        return CheckedCasts.checkedI2B(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public char i2c(int a) throws Exception {
        return CheckedCasts.checkedI2C(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int d2i(double a) throws Exception {
        return CheckedCasts.checkedD2I(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long d2l(double a) throws Exception {
        return CheckedCasts.checkedD2L(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int f2i(float a) throws Exception {
        return CheckedCasts.checkedF2I(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long f2l(float a) throws Exception {
        return CheckedCasts.checkedF2L(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float d2f(double a) throws Exception {
        return CheckedCasts.checkedD2F(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double pow(double a, double b) throws Exception {
        return CheckedMaths.checkedPow(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double asin(double a) throws Exception {
        return CheckedMaths.checkedAsin(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double exp(double a) throws Exception {
        return CheckedMaths.checkedExp(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double log(double a) throws Exception {
        return CheckedMaths.checkedLog(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float frem(float a, float b) throws Exception {
        return CheckedFloats.checkedFREM(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int fcmpl(float a, float b) throws Exception {
        return CheckedFloats.checkedFCMPL(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int fcmpg(float a, float b) throws Exception {
        return CheckedFloats.checkedFCMPG(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double drem(double a, double b) throws Exception {
        return CheckedDoubles.checkedDREM(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int dcmpg(double a, double b) throws Exception {
        return CheckedDoubles.checkedDCMPG(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int dcmpl(double a, double b) throws Exception {
        return CheckedDoubles.checkedDCMPL(a, b, ReactionType.EXCEPTION.value(), "");
    }
}