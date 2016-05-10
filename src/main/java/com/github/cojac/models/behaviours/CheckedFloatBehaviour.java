/*
 * *
 *    Copyright 2011-2016 Baptiste Wicht & Frédéric Bapst & Valentin Gazzola
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
package com.github.cojac.models.behaviours;
import static com.github.cojac.models.behaviours.CheckedBehaviourConstants.*;

import com.github.cojac.models.Reactions;
public class CheckedFloatBehaviour {
    public CheckedFloatBehaviour() {
        throw new AssertionError();
    }
 /*--------------------------FLOAT--------------------------*/
    
    public static float FADD(float a, float b) {
        float r = a + b;

        if (b != 0 && r == a || a != 0 && r == b) {
            if (r != 2.0 * r)  // means here: isInfinite(r) (can't be 0 on NaN)
            {
                Reactions.react( PRECISION_MSG + "FADD");
            }
        } else if (a != POSITIVE_INFINITY_FLOAT && a != NEGATIVE_INFINITY_FLOAT
            && b != POSITIVE_INFINITY_FLOAT && b != NEGATIVE_INFINITY_FLOAT
            && r != 0.0f) {
            if (r == POSITIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "FADD");
            } else if (r == NEGATIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "FADD");
            } else if (Math.abs(r) <= CANCELLATION_ULP_FACTOR_FLOAT * Math.ulp(a)) {
                Reactions.react( CANCELLATION_MSG + "FADD");
            }
        }
        return r;
    }

    public static float FSUB(float a, float b) {
        float r = a - b;

        if (b != 0 && r == a || a != 0 && r == -b) {
            if (r != 2.0 * r)  // means here: isInfinite(r) (can't be 0 on NaN)
            {
                Reactions.react( PRECISION_MSG + "FSUB");
            }
        } else if (a != POSITIVE_INFINITY_FLOAT && a != NEGATIVE_INFINITY_FLOAT
            && b != POSITIVE_INFINITY_FLOAT && b != NEGATIVE_INFINITY_FLOAT
            && r != 0.0f) {
            if (r == POSITIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "FSUB");
            } else if (r == NEGATIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "FSUB");
            } else if (Math.abs(r) <= CANCELLATION_ULP_FACTOR_FLOAT * Math.ulp(a)) {
                Reactions.react( CANCELLATION_MSG + "FSUB");
            }
        }

        return r;
    }

    public static float FMUL(float a, float b) {
        float r = a * b;

        if (a != POSITIVE_INFINITY_FLOAT && a != NEGATIVE_INFINITY_FLOAT && b != POSITIVE_INFINITY_FLOAT && b != NEGATIVE_INFINITY_FLOAT) {
            if (r == POSITIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "FMUL");
            } else if (r == NEGATIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "FMUL");
            } else if (r == 0.0f && a != 0.0f && b != 0.0f) {
                Reactions.react( UNDERFLOW_MSG + "FMUL");
            }
        }

        return r;
    }

    public static float FDIV(float a, float b) {
        float r = a / b;

        if (a == a && b == b && r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "FDIV");
        } else if (a != POSITIVE_INFINITY_FLOAT && a != NEGATIVE_INFINITY_FLOAT && b != POSITIVE_INFINITY_FLOAT && b != NEGATIVE_INFINITY_FLOAT) {
            if (r == POSITIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "FDIV");
            } else if (r == NEGATIVE_INFINITY_FLOAT) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "FDIV");
            } else if (r == 0.0f && a != 0.0f) {
                Reactions.react( UNDERFLOW_MSG + "FDIV");
            }
        }

        return r;
    }

    public static float FREM(float a, float b) {
        float r = a % b;

        if (a == a && b == b && r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "FREM");
        }
        if (Math.ulp(a) > Math.abs(b)) {
            Reactions.react( PRECISION_MSG + "FREM");
        }

        return r;
    }

    public static int FCMPG(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) return +1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0f * a || b == 2.0f * b)  // means here: isInfinite(a) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(a)) {
            Reactions.react( VERY_CLOSE_MSG + "FCMP");
        }
        return r;
    }

    public static int FCMPL(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) return -1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0f * a || b == 2.0f * b)  // means here: isInfinite(a) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(a)) {
            Reactions.react( VERY_CLOSE_MSG + "FCMP");
        }
        return r;
    }
    
    
}
