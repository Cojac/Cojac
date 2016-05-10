/*
 * *
 *    Copyright 2011-2016 Valentin Gazzola & Frédéric Bapst
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


public final class CheckedBehaviourConstants {
    
    public static final String PRECISION_MSG = "Smearing: ";
    public static final String RESULT_IS_POS_INF_MSG = "Result is +Infinity: ";
    public static final String RESULT_IS_NEG_INF_MSG = "Result is -Infinity: ";
    public static final String UNDERFLOW_MSG = "Underflow: ";
    public static final String RESULT_IS_NAN_MSG = "Result is NaN: ";
    public static final String VERY_CLOSE_MSG = "Comparing very close: ";
    public static final String CANCELLATION_MSG = "Cancellation: ";

    public static final float CLOSENESS_ULP_FACTOR_FLOAT = 16.0f;
    public static final float CANCELLATION_ULP_FACTOR_FLOAT = 16.0f;
    
    public static final double NEGATIVE_INFINITY_FLOAT = Float.NEGATIVE_INFINITY;
    public static final double POSITIVE_INFINITY_FLOAT = Float.POSITIVE_INFINITY;
    
    public static final double CLOSENESS_ULP_FACTOR_DOUBLE = 32.0;
    public static final double CANCELLATION_ULP_FACTOR_DOUBLE = 32.0;
    
    public static final double NEGATIVE_INFINITY_DOUBLE = Double.NEGATIVE_INFINITY;
    public static final double POSITIVE_INFINITY_DOUBLE = Double.POSITIVE_INFINITY;
}
