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

package com.github.cojac;

import java.util.EnumMap;
import java.util.Map;

public final class Signatures {
    private static final Map<Arg, String> SIGNATURES = new EnumMap<Arg, String>(Arg.class);

    static {
        SIGNATURES.put(Arg.IADD, Signatures.CHECK_INTEGER_BINARY);
        SIGNATURES.put(Arg.ISUB, Signatures.CHECK_INTEGER_BINARY);
        SIGNATURES.put(Arg.IMUL, Signatures.CHECK_INTEGER_BINARY);
        SIGNATURES.put(Arg.IDIV, Signatures.CHECK_INTEGER_BINARY);

        SIGNATURES.put(Arg.INEG, Signatures.CHECK_INTEGER_UNARY);
        SIGNATURES.put(Arg.IINC, Signatures.CHECK_INTEGER_UNARY);

        SIGNATURES.put(Arg.LADD, Signatures.CHECK_LONG_BINARY);
        SIGNATURES.put(Arg.LSUB, Signatures.CHECK_LONG_BINARY);
        SIGNATURES.put(Arg.LMUL, Signatures.CHECK_LONG_BINARY);
        SIGNATURES.put(Arg.LDIV, Signatures.CHECK_LONG_BINARY);

        SIGNATURES.put(Arg.LNEG, Signatures.CHECK_LONG_UNARY);

        SIGNATURES.put(Arg.DADD, Signatures.CHECK_DOUBLE_BINARY);
        SIGNATURES.put(Arg.DSUB, Signatures.CHECK_DOUBLE_BINARY);
        SIGNATURES.put(Arg.DMUL, Signatures.CHECK_DOUBLE_BINARY);
        SIGNATURES.put(Arg.DDIV, Signatures.CHECK_DOUBLE_BINARY);
        SIGNATURES.put(Arg.DREM, Signatures.CHECK_DOUBLE_BINARY);
        SIGNATURES.put(Arg.DCMP, Signatures.CHECK_DOUBLE_CMP);

        SIGNATURES.put(Arg.FADD, Signatures.CHECK_FLOAT_BINARY);
        SIGNATURES.put(Arg.FSUB, Signatures.CHECK_FLOAT_BINARY);
        SIGNATURES.put(Arg.FMUL, Signatures.CHECK_FLOAT_BINARY);
        SIGNATURES.put(Arg.FDIV, Signatures.CHECK_FLOAT_BINARY);
        SIGNATURES.put(Arg.FREM, Signatures.CHECK_FLOAT_BINARY);
        SIGNATURES.put(Arg.FCMP, Signatures.CHECK_FLOAT_CMP);

        SIGNATURES.put(Arg.L2I, Signatures.CHECK_L2I);
        SIGNATURES.put(Arg.I2S, Signatures.CHECK_I2S);
        SIGNATURES.put(Arg.I2C, Signatures.CHECK_I2C);
        SIGNATURES.put(Arg.I2B, Signatures.CHECK_I2B);
        SIGNATURES.put(Arg.D2F, Signatures.CHECK_D2F);
        SIGNATURES.put(Arg.D2I, Signatures.CHECK_D2I);
        SIGNATURES.put(Arg.D2L, Signatures.CHECK_D2L);
        SIGNATURES.put(Arg.F2I, Signatures.CHECK_F2I);
        SIGNATURES.put(Arg.F2L, Signatures.CHECK_F2L);
        SIGNATURES.put(Arg.I2F, Signatures.CHECK_I2F);
        SIGNATURES.put(Arg.L2D, Signatures.CHECK_L2D);
    }

    //Injected methods

    public static final String CHECK_INTEGER_BINARY = "(II)I";
    public static final String CHECK_INTEGER_UNARY = "(I)I";

    private static final String CHECK_LONG_BINARY = "(JJ)J";
    private static final String CHECK_LONG_UNARY = "(J)J";

    public static final String CHECK_DOUBLE_BINARY = "(DD)D";
    public static final String CHECK_DOUBLE_CMP = "(DD)I";

    public static final String CHECK_FLOAT_BINARY = "(FF)F";
    public static final String CHECK_FLOAT_CMP = "(FF)I";

    public static final String CHECK_L2I = "(J)I";
    public static final String CHECK_I2S = "(I)S";
    public static final String CHECK_I2C = "(I)C";
    public static final String CHECK_I2B = "(I)B";
    public static final String CHECK_D2F = "(D)F";
    public static final String CHECK_D2I = "(D)I";
    public static final String CHECK_D2L = "(D)J";
    public static final String CHECK_F2I = "(F)I";
    public static final String CHECK_F2L = "(F)J";
    public static final String CHECK_I2F = "(I)F";
    public static final String CHECK_L2D = "(J)D";

    public static final String CHECK_MATH_RESULT = "(DILjava/lang/String;Ljava/lang/String;)V";
    //Raw methods

    public static final String RAW_INTEGER_BINARY = "(IIILjava/lang/String;)I";
    public static final String RAW_INTEGER_UNARY = "(IILjava/lang/String;)I";

    public static final String RAW_LONG_BINARY = "(JJILjava/lang/String;)J";
    public static final String RAW_LONG_UNARY = "(JILjava/lang/String;)J";

    public static final String RAW_DOUBLE_BINARY = "(DDILjava/lang/String;)D";
    public static final String RAW_DOUBLE_CMP = "(DDILjava/lang/String;)I";

    public static final String RAW_FLOAT_BINARY = "(FFILjava/lang/String;)F";
    public static final String RAW_FLOAT_CMP = "(FFILjava/lang/String;)I";

    public static final String RAW_L2I = "(JILjava/lang/String;)I";
    public static final String RAW_I2S = "(IILjava/lang/String;)S";
    public static final String RAW_I2C = "(IILjava/lang/String;)C";
    public static final String RAW_I2B = "(IILjava/lang/String;)B";
    public static final String RAW_D2F = "(DILjava/lang/String;)F";
    public static final String RAW_D2I = "(DILjava/lang/String;)I";
    public static final String RAW_D2L = "(DILjava/lang/String;)J";
    public static final String RAW_F2I = "(FILjava/lang/String;)I";
    public static final String RAW_F2L = "(FILjava/lang/String;)J";
    public static final String RAW_I2F = "(IILjava/lang/String;)F";
    public static final String RAW_L2D = "(JILjava/lang/String;)D";

    //React methods

    public static final String REACT = "(Ljava/lang/String;)V";
    public static final String REACT_LOG = "(Ljava/lang/String;Ljava/lang/String;)V";

    private Signatures() {
        throw new AssertionError();
    }

    public static String getSignature(Arg arg) {
        if (!arg.isOperator()) {
            throw new IllegalArgumentException("Arg must be an opCode");
        }

        return SIGNATURES.get(arg);
    }
}