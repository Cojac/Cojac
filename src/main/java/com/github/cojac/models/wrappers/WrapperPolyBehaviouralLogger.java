/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst & Rémi Badoud
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

import com.github.cojac.utils.PolyBehaviourLogger;

public class WrapperPolyBehaviouralLogger extends ACompactWrapper {
    private final double value;

    private WrapperPolyBehaviouralLogger(double v) {
        this.value = v;
    }

    // -------------------------------------------------------------------------
    // ----------------- Necessary constructor -------------------------------
    // -------------------------------------------------------------------------
    public WrapperPolyBehaviouralLogger(ACojacWrapper w) {
        this(w == null ? 0.0 : ((WrapperPolyBehaviouralLogger) w).value);
    }

    // -------------------------------------------------------------------------
    @Override
    public ACojacWrapper applyUnaryOp(DoubleUnaryOperator op) {
        log();
        return new WrapperPolyBehaviouralLogger(op.applyAsDouble(value));
    }

    @Override
    public ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper b) {
        log();
        WrapperPolyBehaviouralLogger bb = (WrapperPolyBehaviouralLogger) b;
        return new WrapperPolyBehaviouralLogger(op.applyAsDouble(value, bb.value));
    }

    @Override
    public double toDouble() {
        return value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperPolyBehaviouralLogger(a);
    }

    @Override
    public String asInternalString() {
        return Double.toString(value);
    }

    @Override
    public String wrapperName() {
        return "Basic";
    }
    // -------------------------------------------------------------------------

    private void log() {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[5];
        PolyBehaviourLogger.getinstance().log(ste.getClassName(), ste.getLineNumber());
    }
    // @Override public int compareTo(ACojacWrapper o) {
    // System.out.println("@@@ WrapperBasic.compareTo");
    // return Double.compare(toDouble(), o.toDouble());
    // }

    // -------------------------------------------------------------------------
}
