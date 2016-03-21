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
import java.util.logging.Level;
import java.util.logging.Logger;

public class WrapperSymbolic extends ACojacWrapper {

    private static final boolean compr = true;
    private final SymbolicExpression expr;

    // -------------------------------------------------------------------------
    private WrapperSymbolic() {
        this.expr = new SymbolicExpression();
    }

    private WrapperSymbolic(double value) {
        this.expr = new SymbolicExpression(value);
    }

    private WrapperSymbolic(SymbolicExpression expr) {
        this.expr = expr;
    }

    private WrapperSymbolic(OP oper, SymbolicExpression left) {
        this.expr = new SymbolicExpression(oper, left);
    }

    private WrapperSymbolic(OP oper, SymbolicExpression left, SymbolicExpression right) {
        this.expr = new SymbolicExpression(oper, left, right);
    }

    // -------------------------------------------------------------------------
    // ----------------- Necessary constructor ---------------------------------
    // -------------------------------------------------------------------------
    public WrapperSymbolic(ACojacWrapper w) {
        this(w == null ? null : symb(w).expr);
    }
    // -------------------------------------------------------------------------

    @Override
    public ACojacWrapper dadd(ACojacWrapper w) {
        return new WrapperSymbolic(OP.ADD, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper dsub(ACojacWrapper w) {
        return new WrapperSymbolic(OP.SUB, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper dmul(ACojacWrapper w) {
        return new WrapperSymbolic(OP.MUL, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper ddiv(ACojacWrapper w) {
        return new WrapperSymbolic(OP.DIV, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper drem(ACojacWrapper w) {
        return new WrapperSymbolic(OP.REM, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper dneg() {
        return new WrapperSymbolic(OP.NEG, this.expr);
    }

    @Override
    public ACojacWrapper math_sqrt() {
        return new WrapperSymbolic(OP.SQRT, this.expr);
    }

    @Override
    public ACojacWrapper math_abs() {
        return new WrapperSymbolic(OP.ABS, this.expr);
    }

    @Override
    public ACojacWrapper math_sin() {
        return new WrapperSymbolic(OP.SIN, this.expr);
    }

    @Override
    public ACojacWrapper math_cos() {
        return new WrapperSymbolic(OP.COS, this.expr);
    }

    @Override
    public ACojacWrapper math_tan() {
        return new WrapperSymbolic(OP.TAN, this.expr);
    }

    @Override
    public ACojacWrapper math_asin() {
        return new WrapperSymbolic(OP.ASIN, this.expr);
    }

    @Override
    public ACojacWrapper math_acos() {
        return new WrapperSymbolic(OP.ACOS, this.expr);
    }

    @Override
    public ACojacWrapper math_atan() {
        return new WrapperSymbolic(OP.ATAN, this.expr);
    }

    @Override
    public ACojacWrapper math_sinh() {
        return new WrapperSymbolic(OP.SINH, this.expr);
    }

    @Override
    public ACojacWrapper math_cosh() {
        return new WrapperSymbolic(OP.COSH, this.expr);
    }

    @Override
    public ACojacWrapper math_tanh() {
        return new WrapperSymbolic(OP.TANH, this.expr);
    }

    @Override
    public ACojacWrapper math_exp() {
        return new WrapperSymbolic(OP.EXP, this.expr);
    }

    @Override
    public ACojacWrapper math_log() {
        return new WrapperSymbolic(OP.LOG, this.expr);
    }

    @Override
    public ACojacWrapper math_log10() {
        return new WrapperSymbolic(OP.LOG10, this.expr);
    }

    @Override
    public ACojacWrapper math_toRadians() {
        return new WrapperSymbolic(OP.RAD, this.expr);
    }

    @Override
    public ACojacWrapper math_toDegrees() {
        return new WrapperSymbolic(OP.DEG, this.expr);
    }

    @Override
    public ACojacWrapper math_min(ACojacWrapper w) {
        return new WrapperSymbolic(OP.MIN, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper math_max(ACojacWrapper w) {
        return new WrapperSymbolic(OP.MAX, this.expr, symb(w).expr);
    }

    @Override
    public ACojacWrapper math_pow(ACojacWrapper w) {
        return new WrapperSymbolic(OP.POW, this.expr, symb(w).expr);
    }

    // -------------------------------------------------------------------------
    @Override
    public double toDouble() {
        return expr.value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {

        return new WrapperSymbolic(new SymbolicExpression(a));
    }

    @Override
    public int dcmpl(ACojacWrapper w) {
        if (this.expr.containsUnknown || symb(w).expr.containsUnknown)
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not compare symbolic expressions containing unknowns");
        if (this.isNaN() || w.isNaN())
            return -1;
        return this.compareTo(w);
    }

    @Override
    public int dcmpg(ACojacWrapper w) {
        if (this.expr.containsUnknown || symb(w).expr.containsUnknown)
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not compare symbolic expressions containing unknowns");
        if (this.isNaN() || w.isNaN())
            return 1;
        return this.compareTo(w);
    }

    @Override
    public int compareTo(ACojacWrapper w) {
        return Double.compare(toDouble(), w.toDouble());
    }

    @Override
    public String asInternalString() {
        return expr + "";
    }

    @Override
    public String wrapperName() {
        return "Symbolic";
    }

    // ------------------------------------------------------------------------
    public static boolean COJAC_MAGIC_isSymbolicUnknown(CommonDouble d) {
        return symb(d.val).expr.containsUnknown;
    }

    public static boolean COJAC_MAGIC_isSymbolicUnknown(CommonFloat d) {
        return symb(d.val).expr.containsUnknown;
    }

    public static CommonDouble COJAC_MAGIC_asSymbolicUnknown(CommonDouble d) {
        WrapperSymbolic res = new WrapperSymbolic();
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_asSymbolicUnknown(CommonFloat d) {
        WrapperSymbolic res = new WrapperSymbolic();
        return new CommonFloat(res);
    }

    public static CommonDouble COJAC_MAGIC_evaluateSymbolicAt(CommonDouble d, CommonDouble x) {
        double result = symb(d.val).expr.evaluate(symb(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_evaluateSymbolicAt(CommonFloat d, CommonFloat x) {
        double result = symb(d.val).expr.evaluate(symb(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonFloat(res);
    }

    // -------------------------------------------------------------------------
    private static WrapperSymbolic symb(ACojacWrapper w) {
        return (WrapperSymbolic) w;
    }

    // -------------------------------------------------------------------------
    private class SymbolicExpression {

        private final double value;
        private final boolean containsUnknown;
        private final OP oper;
        private final SymbolicExpression left;
        private final SymbolicExpression right;

        public SymbolicExpression() {
            this.value = Double.NaN;
            this.containsUnknown = true;
            this.oper = OP.NOP;
            this.right = null;
            this.left = null;
        }

        public SymbolicExpression(double value) {
            this.value = value;
            this.containsUnknown = false;
            this.oper = OP.NOP;
            this.left = null;
            this.right = null;
        }

        public SymbolicExpression(OP oper, SymbolicExpression left, SymbolicExpression right) {
            this.value = oper.apply(left.value, right.value);
            this.containsUnknown = left.containsUnknown ||
                    right.containsUnknown;
            if (this.containsUnknown && compr) {
                this.oper = oper;
                this.left = left;
                this.right = right;
            } else {
                this.oper = OP.NOP;
                this.left = null;
                this.right = null;
            }
        }

        public SymbolicExpression(OP oper, SymbolicExpression left) {
            this.value = oper.apply(left.value, Double.NaN);
            this.containsUnknown = left.containsUnknown;
            if (this.containsUnknown && compr) {
                this.oper = oper;
                this.left = left;
                this.right = null;
            } else {
                this.oper = OP.NOP;
                this.left = null;
                this.right = null;
            }
        }

        public double evaluate(double x) {
            if (containsUnknown && oper == OP.NOP)
                return x;
            if (oper == OP.NOP)
                return value;
            if (right != null)
                return oper.apply(left.evaluate(x), right.evaluate(x));
            return oper.apply(left.evaluate(x), Double.NaN);
        }

        public String toString() {
            if (containsUnknown && oper == OP.NOP)
                return "x";
            if (oper == OP.NOP)
                return value + "";
            if (right != null)
                return oper + "(" + left + "," + right + ")";
            return oper + "(" + left + ")";
        }

    }

    // -------------------------------------------------------------------------
    public static enum OP {
        NOP(((x, y) -> Double.NaN)),
        ADD(((x, y) -> x + y)),
        SUB(((x, y) -> x - y)),
        MUL(((x, y) -> x * y)),
        DIV(((x, y) -> x / y)),
        REM(((x, y) -> x % y)),
        NEG(((x, y) -> -x)),
        SQRT((x, y) -> Math.sqrt(x)),
        ABS((x, y) -> Math.abs(x)),
        SIN((x, y) -> Math.sin(x)),
        COS((x, y) -> Math.cos(x)),
        TAN((x, y) -> Math.tan(x)),
        ASIN((x, y) -> Math.asin(x)),
        ACOS((x, y) -> Math.acos(x)),
        ATAN((x, y) -> Math.atan(x)),
        SINH((x, y) -> Math.sinh(x)),
        COSH((x, y) -> Math.cosh(x)),
        TANH((x, y) -> Math.tanh(x)),
        EXP((x, y) -> Math.exp(x)),
        LOG((x, y) -> Math.log(x)),
        LOG10((x, y) -> Math.log10(x)),
        RAD((x, y) -> Math.toRadians(x)),
        DEG((x, y) -> Math.toDegrees(x)),
        MIN((x, y) -> Math.min(x, y)),
        MAX((x, y) -> Math.max(x, y)),
        POW((x, y) -> Math.pow(x, y));

        private final DoubleBinaryOperator binaryOp;

        OP(DoubleBinaryOperator binaryOp) {
            this.binaryOp = binaryOp;
        }

        public double apply(double left, double right) {
            return binaryOp.applyAsDouble(left, right);
        }

    }

}
