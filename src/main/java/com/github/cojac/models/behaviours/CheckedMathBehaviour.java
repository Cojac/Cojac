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

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import com.github.cojac.models.Reactions;

public class CheckedMathBehaviour {
    /*--------------------------MATH--------------------------*/
  public static double sqrt (double a) { return applyUnaryOp(StrictMath::sqrt,  "Math.sqrt",  a); }
  public static double tan  (double a) { return applyUnaryOp(StrictMath::tan,   "Math.tan",   a); }
  public static double asin (double a) { return applyUnaryOp(StrictMath::asin,  "Math.asin",  a); }
  public static double acos (double a) { return applyUnaryOp(StrictMath::acos,  "Math.acos",  a); }
  public static double atan (double a) { return applyUnaryOp(StrictMath::atan,  "Math.atan",  a); }
  public static double exp  (double a) { return applyUnaryOp(StrictMath::exp,   "Math.exp",   a); }
  public static double expm1(double a) { return applyUnaryOp(StrictMath::expm1, "Math.expm1", a); }
  public static double log  (double a) { return applyUnaryOp(StrictMath::log,   "Math.log",   a); }
  public static double log10(double a) { return applyUnaryOp(StrictMath::log10, "Math.log10", a); }
  public static double log1p(double a) { return applyUnaryOp(StrictMath::log1p, "Math.log1p", a); }
  
  public static double pow(double a, double b) { return applyBinaryOp(StrictMath::pow, "Math.pow", a, b); }

  // Other Math.* methods should not cause any trouble: 
  // Math::abs Math::sin Math::cos Math::sinh Math::cosh Math::tanh 
  // Math::toRadians Math::toDegrees Math::min...

    
    private static void watchMathMethodResult(double r, String operationName, double a, double b) {
        // no reaction if any operand was NaN or infinite...
        if (a!=a || b!=b || a==Double.POSITIVE_INFINITY || b==Double.POSITIVE_INFINITY)
            return;
        if (r != r) {
            Reactions.react(RESULT_IS_NAN_MSG + operationName);
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react(RESULT_IS_POS_INF_MSG + operationName);
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react(RESULT_IS_NEG_INF_MSG + operationName);
        }  
    }

    private static void watchMathMethodResult(double r, String operationName, double a) {
        // no reaction if any operand was NaN or infinite...
        if (a!=a  || a==Double.POSITIVE_INFINITY )
            return;
        if (r != r) {
            Reactions.react(RESULT_IS_NAN_MSG + operationName);
        } else if (r == Double.POSITIVE_INFINITY) {
            Reactions.react(RESULT_IS_POS_INF_MSG + operationName);
        } else if (r == Double.NEGATIVE_INFINITY) {
            Reactions.react(RESULT_IS_NEG_INF_MSG + operationName);
        }  
    }

    private static double applyUnaryOp(DoubleUnaryOperator op, String opName, double a) {
        double r = op.applyAsDouble(a);
        watchMathMethodResult(r, opName, a); 
        return r;
    }
    
    private static double applyBinaryOp(DoubleBinaryOperator op, String opName, double a, double b) {
        double r = op.applyAsDouble(a,b);
        watchMathMethodResult(r, opName, a, b);
        return r;
    }

}
