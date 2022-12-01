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


import com.github.cojac.utils.Posit32Utils;

import java.util.function.BiFunction;
import java.util.function.Function;

public class WrapperPosit32 extends ACojacWrapper<WrapperPosit32> {
    protected final float posit32;

    protected WrapperPosit32(float posit32) {
        this.posit32 = posit32;
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperPosit32(WrapperPosit32 w) {
        // CommonDouble can call this constructor with a null wrapper
        if (w == null) {
            this.posit32 = Posit32Utils.toPosit(0);
        } else {
            this.posit32 = w.posit32;
        }
    }

    //-------------------------------------------------------------------------    
    @Override
    public double toDouble() {
        return Posit32Utils.toFloat(posit32);
    }

    @Override
    public WrapperPosit32 fromDouble(double a, boolean wasFromFloat) {
        return new WrapperPosit32(Posit32Utils.toPosit((float) a));
    }

    @Override
    public WrapperPosit32 fromString(String a, boolean wasFromFloat){
        return fromDouble(Double.parseDouble(a), wasFromFloat);
    }

    @Override
    public String asInternalString() {
        return Double.toString(this.toDouble());
    }

    @Override
    public String wrapperName() {
        return "Posit32";
    }

    @Override
    public WrapperPosit32 dadd(WrapperPosit32 wrapper) {
        return new WrapperPosit32(Posit32Utils.add(this.posit32, wrapper.posit32));
    }

    @Override
    public WrapperPosit32 dsub(WrapperPosit32 wrapper) {
        return new WrapperPosit32(Posit32Utils.substract(this.posit32, wrapper.posit32));
    }

    @Override
    public WrapperPosit32 dmul(WrapperPosit32 wrapper) {
        return new WrapperPosit32(Posit32Utils.multiply(this.posit32, wrapper.posit32));
    }

    @Override
    public WrapperPosit32 ddiv(WrapperPosit32 wrapper) {
        return new WrapperPosit32(Posit32Utils.divide(this.posit32, wrapper.posit32));
    }

    private float floatRemainder(float a, float b) {
        return a % b;
    }

    @Override
    public WrapperPosit32 drem(WrapperPosit32 b) {
        return new WrapperPosit32(
                applyOperationWithFloatingPoints(this.posit32, b.posit32, this::floatRemainder));
    }

    @Override
    public WrapperPosit32 dneg() {
        return new WrapperPosit32(Posit32Utils.substract(Posit32Utils.toPosit(0), this.posit32));
    }

    @Override
    public WrapperPosit32 math_sqrt() {
        return new WrapperPosit32(Posit32Utils.sqrt(this.posit32));
    }

    @Override
    public WrapperPosit32 math_cbrt() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::cbrt));
    }

    // TODO - implement this using posit directly
    @Override
    public WrapperPosit32 math_abs() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::abs));
    }

    @Override
    public WrapperPosit32 math_sin() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::sin));
    }

    @Override
    public WrapperPosit32 math_cos() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::cos));
    }

    @Override
    public WrapperPosit32 math_tan() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::tan));
    }

    @Override
    public WrapperPosit32 math_asin() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::asin));
    }

    @Override
    public WrapperPosit32 math_acos() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::acos));
    }

    @Override
    public WrapperPosit32 math_atan() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::atan));
    }

    @Override
    public WrapperPosit32 math_sinh() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::sinh));
    }

    @Override
    public WrapperPosit32 math_cosh() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::cosh));
    }

    @Override
    public WrapperPosit32 math_tanh() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::tanh));
    }

    @Override
    public WrapperPosit32 math_exp() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::exp));
    }

    @Override
    public WrapperPosit32 math_log() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::log));
    }

    @Override
    public WrapperPosit32 math_log10() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::log10));
    }

    @Override
    public WrapperPosit32 math_toRadians() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::toRadians));
    }

    @Override
    public WrapperPosit32 math_toDegrees() {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, Math::toDegrees));
    }

    @Override
    public WrapperPosit32 math_pow(WrapperPosit32 wrapper) {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, wrapper.posit32,
                Math::pow));
    }

    @Override
    public int compareTo(WrapperPosit32 wrapper) {
        if (wrapper == null) {
            throw new IllegalArgumentException();
        }
        float posit32 = wrapper.posit32;
        if (Posit32Utils.equals(this.posit32, posit32)) {
            return 0;
        }
        return Posit32Utils.isLess(this.posit32, posit32) ? -1 : 1;
    }

    @Override
    public WrapperPosit32 math_hypot(WrapperPosit32 wrapper) {
        return new WrapperPosit32(applyOperationWithFloatingPoints(this.posit32, wrapper.posit32,
                Math::hypot));
    }

    @Override
    public WrapperPosit32 math_fma(WrapperPosit32 a, WrapperPosit32 b) {
        return new WrapperPosit32(Posit32Utils.fma(this.posit32, a.posit32, b.posit32));
    }

    // TODO - replace all calls to this method by implementing it using posit directly
    private static float applyOperationWithFloatingPoints(float posit32, Function<Float, Number> operation) {
        return Posit32Utils.toPosit(operation.apply(Posit32Utils.toFloat(posit32)).floatValue());
    }

    // TODO - replace all calls to this method by implementing it using posit directly
    private static float applyOperationWithFloatingPoints(float positA, float positB,
                                                          BiFunction<Float, Float, Number> operation) {
        return Posit32Utils.toPosit(operation.apply(
                Posit32Utils.toFloat(positA),
                Posit32Utils.toFloat(positB)
        ).floatValue());
    }
}
