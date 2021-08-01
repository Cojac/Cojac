/*
 * *
 *    Copyright 2014 Frédéric Bapst
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

package com.github.cojac.models.wrappers;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public abstract class ACompactWrapper<T extends ACompactWrapper<T>> extends ACojacWrapper<T> {
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------

    // public ACompactWrapper(T v) { }

    //-------------------------------------------------------------------------
    public abstract T applyUnaryOp(DoubleUnaryOperator op);
    public abstract T applyBinaryOp(DoubleBinaryOperator op, T b);
    
    public T dadd(T b) { return applyBinaryOp((Double::sum), b); }
    public T dsub(T b) { return applyBinaryOp(((x,y)->x-y), b); }
    public T dmul(T b) { return applyBinaryOp(((x,y)->x*y), b); }
    public T ddiv(T b) { return applyBinaryOp(((x,y)->x/y), b); }
    public T drem(T b) { return applyBinaryOp(((x,y)->x%y), b); }
    public T dneg()                { return applyUnaryOp((x->-x)); }
    
    public T math_sqrt() { return applyUnaryOp(Math::sqrt); }
    public T math_abs(){ return applyUnaryOp(Math::abs); }
    public T math_sin() { return applyUnaryOp(Math::sin); }
    public T math_cos() { return applyUnaryOp(Math::cos); }
    public T math_tan() { return applyUnaryOp(Math::tan); }
    public T math_asin() { return applyUnaryOp(Math::asin); }
    public T math_acos() { return applyUnaryOp(Math::acos); }
    public T math_atan() { return applyUnaryOp(Math::atan); }
    public T math_sinh() { return applyUnaryOp(Math::sinh); }
    public T math_cosh() { return applyUnaryOp(Math::cosh); }
    public T math_tanh() { return applyUnaryOp(Math::tanh); }
    public T math_exp() { return applyUnaryOp(Math::exp); }
    public T math_log() { return applyUnaryOp(Math::log); }
    public T math_log10() { return applyUnaryOp(Math::log10); }
    public T math_toRadians() { return applyUnaryOp(Math::toRadians); }
    public T math_toDegrees() { return applyUnaryOp(Math::toDegrees); }

    // public T math_min(T b) { return applyBinaryOp(Math::min, b); }
    // public T math_max(T b) { return applyBinaryOp(Math::max, b); }
    public T math_pow(T b) { return applyBinaryOp(Math::pow, b); }
    
    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------
}
