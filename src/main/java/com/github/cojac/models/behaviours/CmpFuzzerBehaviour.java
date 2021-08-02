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

/* In this new behaviour, we toggle the result of a comparison when the 
 * two operands are too close together.
 */
package com.github.cojac.models.behaviours;
import static com.github.cojac.models.behaviours.CheckedBehaviourConstants.CLOSENESS_ULP_FACTOR_DOUBLE;
import static com.github.cojac.models.behaviours.CheckedBehaviourConstants.CLOSENESS_ULP_FACTOR_FLOAT;

//import com.github.cojac.models.Reactions;

public class CmpFuzzerBehaviour {
    private CmpFuzzerBehaviour() {
        throw new AssertionError();
    }
    public static int DCMPG(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return +1;
        int r = Double.compare(a, b);
        if (a == 2.0 * a || b == 2.0 * b)  // means here: isInfinite(r) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
            return -r;
        }
        return r;
    }

    public static int DCMPL(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return -1;
        int r = Double.compare(a, b);
        if (a == 2.0 * a || b == 2.0 * b)  // means here: isInfinite(r) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_DOUBLE * Math.ulp(a)) {
            return -r;
        }
        return r;
    }

    public static int FCMPG(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) return +1;
        int r = Float.compare(a, b);
        if (a == 2.0f * a || b == 2.0f * b)  // means here: isInfinite(a) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(a)) {
            return -r;
        }
        return r;
    }

    public static int FCMPL(float a, float b) {
        if (Float.isNaN(a) || Float.isNaN(b)) return -1;
        int r = Float.compare(a, b);
        if (a == 2.0f * a || b == 2.0f * b)  // means here: isInfinite(a) (can't be 0 on NaN)
            return r;
        if (r != 0 && Math.abs(a - b) <= CLOSENESS_ULP_FACTOR_FLOAT * Math.ulp(a)) {
            return -r;
        }
        return r;
    }

}
