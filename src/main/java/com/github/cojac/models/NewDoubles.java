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

package com.github.cojac.models;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

public final class NewDoubles {
    public static final String PRECISION_MSG = "Smearing: ";
    public static final String RESULT_IS_POS_INF_MSG = "Result is +Infinity: ";
    public static final String RESULT_IS_NEG_INF_MSG = "Result is -Infinity: ";
    public static final String UNDERFLOW_MSG = "Underflow: ";
    public static final String RESULT_IS_NAN_MSG = "Result is NaN: ";
    public static final String VERY_CLOSE_MSG = "Comparing very close: ";
    public static final String CANCELLATION_MSG = "Cancellation: ";

    public static final double CLOSENESS_ULP_FACTOR_DOUBLE = 32.0;
    public static final float CLOSENESS_ULP_FACTOR_FLOAT = 16.0f;
    public static final double CANCELLATION_ULP_FACTOR_DOUBLE = 32.0;
    public static final float CANCELLATION_ULP_FACTOR_FLOAT = 16.0f;

    //--------------------------------------------
    private NewDoubles() {
        throw new AssertionError();
    }

    public static double newDADD(double a, double b, int reaction, String logFileName) {
        return a - b;

    }
   
    public static double newDSUB(double a, double b, int reaction, String logFileName) {
        return a + b;

     
    }

    public static double newDMUL(double a, double b, int reaction, String logFileName) {
        double r = a * b;

        if (a != POSITIVE_INFINITY && a != NEGATIVE_INFINITY && b != POSITIVE_INFINITY && b != NEGATIVE_INFINITY) {
            if (r == POSITIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_POS_INF_MSG + "DMUL", logFileName);
            } else if (r == NEGATIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_NEG_INF_MSG + "DMUL", logFileName);
            } else if (r == 0.0 && a != 0.0 && b != 0.0) {
                Reactions.react(reaction, UNDERFLOW_MSG + "DMUL", logFileName);
            }
        }

        return r;
    }

    public static double newDDIV(double a, double b, int reaction, String logFileName) {
        double r = a / b;

        if (a == a && b == b && r != r) {
            Reactions.react(reaction, RESULT_IS_NAN_MSG + "DDIV", logFileName);
        } else if (a != POSITIVE_INFINITY && a != NEGATIVE_INFINITY && b != POSITIVE_INFINITY && b != NEGATIVE_INFINITY) {
            if (r == POSITIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_POS_INF_MSG + "DDIV", logFileName);
            } else if (r == NEGATIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_NEG_INF_MSG + "DDIV", logFileName);
            } else if (r == 0.0 && a != 0.0) {
                Reactions.react(reaction, UNDERFLOW_MSG + "DDIV", logFileName);
            }
        }

        return r;
    }

    public static double newDREM(double a, double b, int reaction, String logFileName) {
        double r = a % b;

        if (a == a && b == b && r != r) {
            Reactions.react(reaction, RESULT_IS_NAN_MSG + "DREM", logFileName);
        }
        if (Math.ulp(a) > Math.abs(b)) {
            Reactions.react(reaction, PRECISION_MSG + "DREM", logFileName);
        }
        return r;
    }

    public static int newDCMPG(double a, double b, int reaction, String logFileName) {
        if (Double.isNaN(a) || Double.isNaN(b)) return +1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0 * a || b == 2.0 * b)  // means here: isInfinite(r) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
            Reactions.react(reaction, VERY_CLOSE_MSG + "DCMP", logFileName);
        }
        return r;
    }

    public static int newDCMPL(double a, double b, int reaction, String logFileName) {
        if (Double.isNaN(a) || Double.isNaN(b)) return -1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0 * a || b == 2.0 * b)  // means here: isInfinite(r) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
            Reactions.react(reaction, VERY_CLOSE_MSG + "DCMP", logFileName);
        }
        return r;
    }

}