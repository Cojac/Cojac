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

import com.github.cojac.utils.PolyBehaviourLoader;

public class WrapperPolyBehavioural extends ACompactWrapper {

    private final double value;

    private WrapperPolyBehavioural(double v) {
        this.value = v;
    }

    // -------------------------------------------------------------------------
    // ----------------- Necessary constructor -------------------------------
    // -------------------------------------------------------------------------
    public WrapperPolyBehavioural(ACojacWrapper w) {
        this(w == null ? 0.0 : ((WrapperPolyBehavioural) w).value);
    }

    // -------------------------------------------------------------------------
    @Override
    public ACojacWrapper applyUnaryOp(DoubleUnaryOperator op) {
        if (isSpecifiedBehaviour())
            switch (getSpecifiedBehaviour()) {
            case "FLOAT":
                return new WrapperPolyBehavioural((float) op.applyAsDouble((float) value));
            default:
                break;
            }
        return new WrapperPolyBehavioural(op.applyAsDouble(value));
    }

    @Override
    public ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper b) {
        WrapperPolyBehavioural bb = (WrapperPolyBehavioural) b;
        if (isSpecifiedBehaviour())
            switch (getSpecifiedBehaviour()) {
            case "FLOAT":
                return new WrapperPolyBehavioural((float) op.applyAsDouble((float) value, (float) bb.value));
            default:
                break;
            }
        return new WrapperPolyBehavioural(op.applyAsDouble(value, bb.value));
    }

    @Override
    public double toDouble() {
        return value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperPolyBehavioural(a);
    }

    @Override
    public String asInternalString() {
        return Double.toString(value);
    }

    @Override
    public String wrapperName() {
        return "PolyBehavioural";
    }

    private boolean isSpecifiedBehaviour() {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[5];
        return PolyBehaviourLoader.getinstance().isSpecifiedBehaviour(ste.getClassName(), ste.getLineNumber());
    }

    private String getSpecifiedBehaviour() {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[5];
        return PolyBehaviourLoader.getinstance().getSpecifiedBehaviour(ste.getClassName(), ste.getLineNumber());
    }

    // @Override public int compareTo(ACojacWrapper o) {
    // System.out.println("@@@ WrapperBasic.compareTo");
    // return Double.compare(toDouble(), o.toDouble());
    // }

    // -------------------------------------------------------------------------
}
