/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst
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

public class WrapperBasicFloatDouble extends ACompactWrapper {
    private final boolean isFloat;
    private final double value;
    private final float fValue;
   
    private WrapperBasicFloatDouble(double v) {
        this.value=v;
        this.fValue=0;
        this.isFloat=false;
    }
    
    private WrapperBasicFloatDouble(float v) {
        this.fValue=v;
        this.value=0;
        this.isFloat=true;
    }

    
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperBasicFloatDouble(ACojacWrapper w) {
        this(w==null ? 0.0 : (w(w).isFloat ? w(w).fValue : w(w).value));
    }
    
    //-------------------------------------------------------------------------
    @Override
    public ACojacWrapper applyUnaryOp(DoubleUnaryOperator op) {
        if (isFloat) 
            return new WrapperBasicFloatDouble((float)op.applyAsDouble(fValue));
        return new WrapperBasicFloatDouble(op.applyAsDouble(value));
    }

    @Override
    public ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper b) {
        WrapperBasicFloatDouble bb=w(b);
        if (isFloat) 
            return new WrapperBasicFloatDouble((float)op.applyAsDouble(fValue, bb.fValue));
        return new WrapperBasicFloatDouble(op.applyAsDouble(value, bb.value));
    }
    
    @Override public double toDouble() {
        return isFloat ? fValue : value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
       if(wasFromFloat)
           return new WrapperBasicFloatDouble((float)a);
       return new WrapperBasicFloatDouble(a);
    }

    @Override public String asInternalString() {
        return isFloat ? Float.toString(fValue) : Double.toString(value);
    }

    @Override public String wrapperName() {
        return "BasicFloatDouble";
    }
    
    //-------------------------------------------------------------------------
    private static WrapperBasicFloatDouble w(ACojacWrapper a) {
        return (WrapperBasicFloatDouble) a;
    }
}
