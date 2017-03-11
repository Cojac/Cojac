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

//import com.github.cojac.models.*;
import com.github.cojac.models.behaviours.*;

public class CheckedOperations implements MathOperations {
    @Override
    public int iadd(int a, int b) throws Exception {
        return CheckedIntBehaviour.IADD(a, b);
        //return CheckedInts.checkedIADD(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int isub(int a, int b) throws Exception {
        return CheckedIntBehaviour.ISUB(a, b);
//        return CheckedInts.checkedISUB(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int imul(int a, int b) throws Exception {
        return CheckedIntBehaviour.IMUL(a, b);
//        return CheckedInts.checkedIMUL(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int idiv(int a, int b) throws Exception {
        return CheckedIntBehaviour.IDIV(a, b);
//        return CheckedInts.checkedIDIV(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int iinc(int a, int b) throws Exception {
        return CheckedIntBehaviour.IINC(a, b);
//        return CheckedInts.checkedIINC(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int ineg(int a) throws Exception {
        return CheckedIntBehaviour.INEG(a);
//        return CheckedInts.checkedINEG(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long ladd(long a, long b) throws Exception {
        return CheckedLongBehaviour.LADD(a, b);
//        return CheckedLongs.checkedLADD(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long lsub(long a, long b) throws Exception {
        return CheckedLongBehaviour.LSUB(a, b);
//        return CheckedLongs.checkedLSUB(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long lmul(long a, long b) throws Exception {
        return CheckedLongBehaviour.LMUL(a, b);
//        return CheckedLongs.checkedLMUL(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long ldiv(long a, long b) throws Exception {
        return CheckedLongBehaviour.LDIV(a, b);
//        return CheckedLongs.checkedLDIV(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long lneg(long a) throws Exception {
        return CheckedLongBehaviour.LNEG(a);
//        return CheckedLongs.checkedLNEG(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double dadd(double a, double b) throws Exception {
        return CheckedDoubleBehaviour.DADD(a, b);
//        return CheckedDoubles.checkedDADD(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double dsub(double a, double b) throws Exception {
        return CheckedDoubleBehaviour.DSUB(a, b);
//        return CheckedDoubles.checkedDSUB(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double dmul(double a, double b) throws Exception {
        return CheckedDoubleBehaviour.DMUL(a, b);
//        return CheckedDoubles.checkedDMUL(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double ddiv(double a, double b) throws Exception {
        return CheckedDoubleBehaviour.DDIV(a, b);
//        return CheckedDoubles.checkedDDIV(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float fadd(float a, float b) throws Exception {
        return CheckedFloatBehaviour.FADD(a, b);
//        return CheckedFloats.checkedFADD(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float fsub(float a, float b) throws Exception {
        return CheckedFloatBehaviour.FSUB(a, b);
//        return CheckedFloats.checkedFSUB(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float fmul(float a, float b) throws Exception {
        return CheckedFloatBehaviour.FMUL(a, b);
//        return CheckedFloats.checkedFMUL(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float fdiv(float a, float b) throws Exception {
        return CheckedFloatBehaviour.FDIV(a, b);
//        return CheckedFloats.checkedFDIV(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int l2i(long a) throws Exception {
        return CheckedCastBehaviour.L2I(a);
//        return CheckedCasts.checkedL2I(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public short i2s(int a) throws Exception {
        return (short) CheckedCastBehaviour.I2S(a);
//        return CheckedCasts.checkedI2S(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public byte i2b(int a) throws Exception {
        return (byte) CheckedCastBehaviour.I2B(a);
//        return CheckedCasts.checkedI2B(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public char i2c(int a) throws Exception {
        return (char) CheckedCastBehaviour.I2C(a);
//        return CheckedCasts.checkedI2C(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int d2i(double a) throws Exception {
        return CheckedCastBehaviour.D2I(a);
//        return CheckedCasts.checkedD2I(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long d2l(double a) throws Exception {
        return CheckedCastBehaviour.D2L(a);
//        return CheckedCasts.checkedD2L(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int f2i(float a) throws Exception {
        return CheckedCastBehaviour.F2I(a);
//        return CheckedCasts.checkedF2I(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public long f2l(float a) throws Exception {
        return CheckedCastBehaviour.F2L(a);
//        return CheckedCasts.checkedF2L(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float d2f(double a) throws Exception {
        return CheckedCastBehaviour.D2F(a);
//        return CheckedCasts.checkedD2F(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public float i2f(int a) throws Exception {
        return CheckedCastBehaviour.I2F(a);
//        return CheckedCasts.checkedI2F(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double l2d(long a) throws Exception {
        return CheckedCastBehaviour.L2D(a);
//        return CheckedCasts.checkedL2D(a, ReactionType.EXCEPTION.value(), "");
    }

//    @Override
//    public double pow(double a, double b) throws Exception {
//        return CheckedMaths.checkedPow(a, b, ReactionType.EXCEPTION.value(), "");
//    }
    @Override
    public double pow(double a, double b) throws Exception {
        return CheckedMathBehaviour.pow(a, b);
    }

    @Override
    public double asin(double a) throws Exception {
        return CheckedMathBehaviour.asin(a);
//        return CheckedMaths.checkedAsin(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double exp(double a) throws Exception {
        return CheckedMathBehaviour.exp(a);
//        return CheckedMaths.checkedExp(a, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double log(double a) throws Exception {
        return CheckedMathBehaviour.log(a);
//        return CheckedMaths.checkedLog(ape.EXCEPTION.value(), "");
    }

    @Override
    public float frem(float a, float b) throws Exception {
        return CheckedFloatBehaviour.FREM(a, b);
//        return CheckedFloats.checkedFREM(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int fcmpl(float a, float b) throws Exception {
        return CheckedFloatBehaviour.FCMPL(a, b);
//        return CheckedFloats.checkedFCMPL(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int fcmpg(float a, float b) throws Exception {
        return CheckedFloatBehaviour.FCMPG(a, b);
//        return CheckedFloats.checkedFCMPG(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public double drem(double a, double b) throws Exception {
        return CheckedDoubleBehaviour.DREM(a, b);
//        return CheckedDoubles.checkedDREM(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int dcmpg(double a, double b) throws Exception {
        return CheckedDoubleBehaviour.DCMPG(a, b);
//        return CheckedDoubles.checkedDCMPG(a, b, ReactionType.EXCEPTION.value(), "");
    }

    @Override
    public int dcmpl(double a, double b) throws Exception {
        return CheckedDoubleBehaviour.DCMPL(a, b);
//        return CheckedDoubles.checkedDCMPL(a, b, ReactionType.EXCEPTION.value(), "");
    }
}