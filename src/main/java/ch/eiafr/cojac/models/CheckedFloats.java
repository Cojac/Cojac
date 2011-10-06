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

package ch.eiafr.cojac.models;

import static ch.eiafr.cojac.models.CheckedDoubles.*;
import static java.lang.Float.NEGATIVE_INFINITY;
import static java.lang.Float.POSITIVE_INFINITY;

public final class CheckedFloats {
    private CheckedFloats() {
        throw new AssertionError();
    }

    public static float checkedFADD(float a, float b, int reaction, String logFileName) {
        float r = a + b;

        if (b != 0 && r == a || a != 0 && r == b) {
            if (r != 2.0 * r)  // means here: isInfinite(r) (can't be 0 on NaN)
            {
                Reactions.react(reaction, PRECISION_MSG + "FADD", logFileName);
            }
        } else if (a != POSITIVE_INFINITY && a != NEGATIVE_INFINITY
            && b != POSITIVE_INFINITY && b != NEGATIVE_INFINITY
            && r != 0.0f) {
            if (r == POSITIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_POS_INF_MSG + "FADD", logFileName);
            } else if (r == NEGATIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_NEG_INF_MSG + "FADD", logFileName);
            } else if (Math.abs(r) <= CANCELLATION_ULP_FACTOR_FLOAT * Math.ulp(a)) {
                Reactions.react(reaction, CANCELLATION_MSG + "FADD", logFileName);
            }
        }
        return r;
    }

    public static float checkedFSUB(float a, float b, int reaction, String logFileName) {
        float r = a - b;

        if (b != 0 && r == a || a != 0 && r == -b) {
            if (r != 2.0 * r)  // means here: isInfinite(r) (can't be 0 on NaN)
            {
                Reactions.react(reaction, PRECISION_MSG + "FSUB", logFileName);
            }
        } else if (a != POSITIVE_INFINITY && a != NEGATIVE_INFINITY
            && b != POSITIVE_INFINITY && b != NEGATIVE_INFINITY
            && r != 0.0f) {
            if (r == POSITIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_POS_INF_MSG + "FSUB", logFileName);
            } else if (r == NEGATIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_NEG_INF_MSG + "FSUB", logFileName);
            } else if (Math.abs(r) <= CANCELLATION_ULP_FACTOR_FLOAT * Math.ulp(a)) {
                Reactions.react(reaction, CANCELLATION_MSG + "FSUB", logFileName);
            }
        }

        return r;
    }

    public static float checkedFMUL(float a, float b, int reaction, String logFileName) {
        float r = a * b;

        if (a != POSITIVE_INFINITY && a != NEGATIVE_INFINITY && b != POSITIVE_INFINITY && b != NEGATIVE_INFINITY) {
            if (r == POSITIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_POS_INF_MSG + "FMUL", logFileName);
            } else if (r == NEGATIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_NEG_INF_MSG + "FMUL", logFileName);
            } else if (r == 0.0f && a != 0.0f && b != 0.0f) {
                Reactions.react(reaction, UNDERFLOW_MSG + "FMUL", logFileName);
            }
        }

        return r;
    }

    public static float checkedFDIV(float a, float b, int reaction, String logFileName) {
        float r = a / b;

        if (a == a && b == b && r != r) {
            Reactions.react(reaction, RESULT_IS_NAN_MSG + "FDIV", logFileName);
        } else if (a != POSITIVE_INFINITY && a != NEGATIVE_INFINITY && b != POSITIVE_INFINITY && b != NEGATIVE_INFINITY) {
            if (r == POSITIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_POS_INF_MSG + "FDIV", logFileName);
            } else if (r == NEGATIVE_INFINITY) {
                Reactions.react(reaction, RESULT_IS_NEG_INF_MSG + "FDIV", logFileName);
            } else if (r == 0.0f && a != 0.0f) {
                Reactions.react(reaction, UNDERFLOW_MSG + "FDIV", logFileName);
            }
        }

        return r;
    }

    public static float checkedFREM(float a, float b, int reaction, String logFileName) {
        float r = a % b;

        if (a == a && b == b && r != r) {
            Reactions.react(reaction, RESULT_IS_NAN_MSG + "FREM", logFileName);
        }
        if (Math.ulp(a) > Math.abs(b)) {
            Reactions.react(reaction, PRECISION_MSG + "FREM", logFileName);
        }

        return r;
    }

    public static int checkedFCMPG(float a, float b, int reaction, String logFileName) {
        if (Float.isNaN(a) || Float.isNaN(b)) return +1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0f * a || b == 2.0f * b)  // means here: isInfinite(a) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(a)) {
            Reactions.react(reaction, VERY_CLOSE_MSG + "FCMP", logFileName);
        }
        return r;
    }

    public static int checkedFCMPL(float a, float b, int reaction, String logFileName) {
        if (Float.isNaN(a) || Float.isNaN(b)) return -1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0f * a || b == 2.0f * b)  // means here: isInfinite(a) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(a)) {
            Reactions.react(reaction, VERY_CLOSE_MSG + "FCMP", logFileName);
        }
        return r;
    }

}