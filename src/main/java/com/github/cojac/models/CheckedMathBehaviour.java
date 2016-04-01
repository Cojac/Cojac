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
package com.github.cojac.models;

import static com.github.cojac.models.CheckedBehaviourConstants.*;
public class CheckedMathBehaviour {
    /*--------------------------MATH--------------------------*/
    public static double pow(double a, double b) {
        double r = StrictMath.pow(a, b);

        if (r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "Math.pow()");
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react( RESULT_IS_POS_INF_MSG + "Math.pow()");
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react( RESULT_IS_NEG_INF_MSG + "Math.pow()");
        }

        return r;
    }

    public static double asin(double a) {
        double r = StrictMath.asin(a);

        if (r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "Math.asin()");
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react( RESULT_IS_POS_INF_MSG + "Math.asin()");
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react( RESULT_IS_NEG_INF_MSG + "Math.asin()");
        }

        return r;
    }

    public static double exp(double a) {
        double r = StrictMath.exp(a);

        if (r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "Math.exp()");
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react( RESULT_IS_POS_INF_MSG + "Math.exp()");
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react( RESULT_IS_NEG_INF_MSG + "Math.exp()");
        }

        return r;
    }

    public static double log(double a) {
        double r = StrictMath.log(a);

        if (r != r) {
            Reactions.react( RESULT_IS_NAN_MSG + "Math.log()");
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react( RESULT_IS_POS_INF_MSG + "Math.log()");
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react( RESULT_IS_NEG_INF_MSG + "Math.log()");
        }

        return r;
    }
}
