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

public abstract class ACompactWrapper extends ACojacWrapper {
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------

    // public ACompactWrapper(ACojacWrapper v) { }

    //-------------------------------------------------------------------------
    public abstract ACojacWrapper applyUnaryOp(DoubleUnaryOperator op);
    public abstract ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper b);
    
    public ACojacWrapper dadd(ACojacWrapper b) { return applyBinaryOp(((x,y)->x+y), b); }
    public ACojacWrapper dsub(ACojacWrapper b) { return applyBinaryOp(((x,y)->x-y), b); }
    public ACojacWrapper dmul(ACojacWrapper b) { return applyBinaryOp(((x,y)->x*y), b); }
    public ACojacWrapper ddiv(ACojacWrapper b) { return applyBinaryOp(((x,y)->x/y), b); }
    public ACojacWrapper drem(ACojacWrapper b) { return applyBinaryOp(((x,y)->x%y), b); }
    public ACojacWrapper dneg()                { return applyUnaryOp((x->-x)); }
    
    public ACojacWrapper math_sqrt() { return applyUnaryOp(Math::sqrt); }
    public ACojacWrapper math_abs(){ return applyUnaryOp(Math::abs); }
    public ACojacWrapper math_sin() { return applyUnaryOp(Math::sin); }
    public ACojacWrapper math_cos() { return applyUnaryOp(Math::cos); }
    public ACojacWrapper math_tan() { return applyUnaryOp(Math::tan); }
    public ACojacWrapper math_asin() { return applyUnaryOp(Math::asin); }
    public ACojacWrapper math_acos() { return applyUnaryOp(Math::acos); }
    public ACojacWrapper math_atan() { return applyUnaryOp(Math::atan); }
    public ACojacWrapper math_sinh() { return applyUnaryOp(Math::sinh); }
    public ACojacWrapper math_cosh() { return applyUnaryOp(Math::cosh); }
    public ACojacWrapper math_tanh() { return applyUnaryOp(Math::tanh); }
    public ACojacWrapper math_exp() { return applyUnaryOp(Math::exp); }
    public ACojacWrapper math_log() { return applyUnaryOp(Math::log); }
    public ACojacWrapper math_log10() { return applyUnaryOp(Math::log10); }
    public ACojacWrapper math_toRadians() { return applyUnaryOp(Math::toRadians); }
    public ACojacWrapper math_toDegrees() { return applyUnaryOp(Math::toDegrees); }

    public ACojacWrapper math_min(ACojacWrapper b) { return applyBinaryOp(Math::min, b); }
    public ACojacWrapper math_max(ACojacWrapper b) { return applyBinaryOp(Math::max, b); }
    public ACojacWrapper math_pow(ACojacWrapper b) { return applyBinaryOp(Math::pow, b); }
    
    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------
}
