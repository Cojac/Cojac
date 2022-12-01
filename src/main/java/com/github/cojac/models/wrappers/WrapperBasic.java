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

public class WrapperBasic extends ACompactWrapper<WrapperBasic> {
    private final double value;
   
    private WrapperBasic(double v) {
        this.value=v;
    }
    
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperBasic(WrapperBasic w) {
        this(w==null ? 0.0 : w.value);
    }
    
    //-------------------------------------------------------------------------
    @Override
    public WrapperBasic applyUnaryOp(DoubleUnaryOperator op) {
        return new WrapperBasic(op.applyAsDouble(value));
    }

    @Override
    public WrapperBasic applyBinaryOp(DoubleBinaryOperator op, WrapperBasic b) {
        WrapperBasic bb = b;
        return new WrapperBasic(op.applyAsDouble(value, bb.value));
    }
    
    @Override public double toDouble() {
        return value;
    }

    @SuppressWarnings("unused")
    @Override
    public WrapperBasic fromDouble(double a, boolean wasFromFloat) {
        return new WrapperBasic(a);
    }

    @Override public String asInternalString() {
        return Double.toString(value);
    }

    @Override public String wrapperName() {
        return "Basic";
    }
    
//    @Override public int compareTo(ACojacWrapper o) {
//        System.out.println("@@@ WrapperBasic.compareTo");
//        return Double.compare(toDouble(), o.toDouble());
//    }

    //-------------------------------------------------------------------------
}
