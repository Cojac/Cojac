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
public class CheckedDoubleBehaviour {
    public CheckedDoubleBehaviour() {
        throw new AssertionError();
    }
    /*--------------------------DOUBLE--------------------------*/
    public static double DADD(double a, double b) {
        double r = a + b;

        if (b != 0 && r == a || a != 0 && r == b) {
            if (r != 2.0 * r)  // means here: isInfinite(r) (can't be 0 on NaN)
            {
                Reactions.react( PRECISION_MSG + "DADD");
            }
        } else if (a != POSITIVE_INFINITY_DOUBLE && a != NEGATIVE_INFINITY_DOUBLE &&
            b != POSITIVE_INFINITY_DOUBLE && b != NEGATIVE_INFINITY_DOUBLE &&
            r != 0.0) {
            if (r == POSITIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "DADD");
            } else if (r == NEGATIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "DADD");
            } else if (Math.abs(r) <= CANCELLATION_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
                Reactions.react( CANCELLATION_MSG + "DADD");
            }
        }

        return r;
    }
   
    public static double DSUB(double a, double b) {
        double r = a - b;

        if (b != 0.0 && r == a || a != 0.0 && r == -b) {
            if (r != 2.0 * r)  // means here: isInfinite(r) (can't be 0 on NaN)
            {
                Reactions.react( PRECISION_MSG + "DSUB");
            }
        } else if (a != POSITIVE_INFINITY_DOUBLE && a != NEGATIVE_INFINITY_DOUBLE &&
            b != POSITIVE_INFINITY_DOUBLE && b != NEGATIVE_INFINITY_DOUBLE &&
            r != 0.0) {
            if (r == POSITIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "DSUB");
            } else if (r == NEGATIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "DSUB");
            } else if (Math.abs(r) <= CANCELLATION_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
                Reactions.react( CANCELLATION_MSG + "DSUB");
            }
        }

        return r;
    }

    public static double DMUL(double a, double b) {
        double r = a * b;

        if (a != POSITIVE_INFINITY_DOUBLE && a != NEGATIVE_INFINITY_DOUBLE && b != POSITIVE_INFINITY_DOUBLE && b != NEGATIVE_INFINITY_DOUBLE) {
            if (r == POSITIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "DMUL");
            } else if (r == NEGATIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "DMUL");
            } else if (r == 0.0 && a != 0.0 && b != 0.0) {
                Reactions.react( UNDERFLOW_MSG + "DMUL");
            }
        }

        return r;
    }

    public static double DDIV(double a, double b) {
        double r = a / b;

        if (a == a && b == b && r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "DDIV");
        } else if (a != POSITIVE_INFINITY_DOUBLE && a != NEGATIVE_INFINITY_DOUBLE && b != POSITIVE_INFINITY_DOUBLE && b != NEGATIVE_INFINITY_DOUBLE) {
            if (r == POSITIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_POS_INF_MSG + "DDIV");
            } else if (r == NEGATIVE_INFINITY_DOUBLE) {
                Reactions.react( RESULT_IS_NEG_INF_MSG + "DDIV");
            } else if (r == 0.0 && a != 0.0) {
                Reactions.react( UNDERFLOW_MSG + "DDIV");
            }
        }

        return r;
    }

    public static double DREM(double a, double b) {
        double r = a % b;

        if (a == a && b == b && r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "DREM");
        }
        if (Math.ulp(a) > Math.abs(b)) {
            Reactions.react( PRECISION_MSG + "DREM");
        }
        return r;
    }

    public static int DCMPG(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return +1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0 * a || b == 2.0 * b)  // means here: isInfinite(r) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
            Reactions.react( VERY_CLOSE_MSG + "DCMP");
        }
        return r;
    }

    public static int DCMPL(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return -1;
        int r = a == b ? 0 : a < b ? -1 : 1;
        if (a == 2.0 * a || b == 2.0 * b)  // means here: isInfinite(r) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
            Reactions.react( VERY_CLOSE_MSG + "DCMP");
        }
        return r;
    }
    
}
