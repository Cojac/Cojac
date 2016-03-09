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

import static com.github.cojac.models.CheckedDoubles.*;

public final class CheckedMaths {
    private CheckedMaths() {
        throw new AssertionError();
    }
    
    public static final String CHECK_MATH_RESULT_PATH = "com/github/cojac/models/CheckedMaths";
    public static final String CHECK_MATH_RESULT_NAME = "checkMathMethodResult";
    
    public static void checkMathMethodResult(double r, int reaction, String logFileName, String operationName) {
        if (r != r) {
            Reactions.react(reaction, RESULT_IS_NAN_MSG + operationName, logFileName);
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react(reaction, RESULT_IS_POS_INF_MSG + operationName, logFileName);
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react(reaction, RESULT_IS_NEG_INF_MSG + operationName, logFileName);
        }  
    }
    
    public static double checkedPow(double a, double b, int reaction, String logFileName) {
        double r = StrictMath.pow(a, b);

        if (r != r) {
            Reactions.react(reaction, RESULT_IS_NAN_MSG + "Math.pow()", logFileName);
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react(reaction, RESULT_IS_POS_INF_MSG + "Math.pow()", logFileName);
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react(reaction, RESULT_IS_NEG_INF_MSG + "Math.pow()", logFileName);
        }

        return r;
    }

    public static double checkedAsin(double a, int reaction, String logFileName) {
        double r = StrictMath.asin(a);

        if (r != r) {
            Reactions.react(reaction, RESULT_IS_NAN_MSG + "Math.asin()", logFileName);
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react(reaction, RESULT_IS_POS_INF_MSG + "Math.asin()", logFileName);
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react(reaction, RESULT_IS_NEG_INF_MSG + "Math.asin()", logFileName);
        }

        return r;
    }

    public static double checkedExp(double a, int reaction, String logFileName) {
        double r = StrictMath.exp(a);

        if (r != r) {
            Reactions.react(reaction, RESULT_IS_NAN_MSG + "Math.exp()", logFileName);
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react(reaction, RESULT_IS_POS_INF_MSG + "Math.exp()", logFileName);
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react(reaction, RESULT_IS_NEG_INF_MSG + "Math.exp()", logFileName);
        }

        return r;
    }

    public static double checkedLog(double a, int reaction, String logFileName) {
        double r = StrictMath.log(a);

        if (r != r) {
            Reactions.react(reaction, RESULT_IS_NAN_MSG + "Math.log()", logFileName);
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react(reaction, RESULT_IS_POS_INF_MSG + "Math.log()", logFileName);
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react(reaction, RESULT_IS_NEG_INF_MSG + "Math.log()", logFileName);
        }

        return r;
    }
}