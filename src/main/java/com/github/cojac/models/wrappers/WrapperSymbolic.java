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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.cojac.Arg;
import com.github.cojac.CojacReferences;
import com.github.cojac.models.wrappers.SymbolicExpression.OP;


public class WrapperSymbolic extends ACojacWrapper<WrapperSymbolic> {
    // In "smart" mode, terms are reordered (slower, but often more precise) 
    public static boolean smart_evaluation_mode = true;
    // If constant subtrees are dropped, we lose an opportunity to improve precision (via smart mode)
    public static boolean keep_constant_subtrees_mode = true;
    // Use the cached value instead of recalculating the tree
    public static boolean use_cached_values = false;

    private final SymbolicExpression expr;

    // -------------------------------------------------------------------------
    // ----------------- Constructors ------------------------------------------
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

    private WrapperSymbolic(OP oper, SymbolicExpression left, SymbolicExpression right) {
        this.expr = new SymbolicExpression(oper, left, right);
        // profiler
        if(CojacReferences.getInstance().getArgs().isSpecified(Arg.NUMERICAL_PROFILER)) {
            CojacReferences.getInstance().getNumericalProfiler().handle(this.expr);
        }
    }

    // -------------------------------------------------------------------------
    // ----------------- Necessary constructor ---------------------------------
    // -------------------------------------------------------------------------
    public WrapperSymbolic(WrapperSymbolic w) {
        this(w == null ? null : w.expr);
    }

    // -------------------------------------------------------------------------
    // ----------------- Override operators ------------------------------------
    // -------------------------------------------------------------------------
    @Override
    public WrapperSymbolic dadd(WrapperSymbolic w) {
        return new WrapperSymbolic(OP.ADD, this.expr, w.expr);
    }

    @Override
    public WrapperSymbolic dsub(WrapperSymbolic w) {
        return new WrapperSymbolic(OP.SUB, this.expr, w.expr);
    }

    @Override
    public WrapperSymbolic dmul(WrapperSymbolic w) {
        return new WrapperSymbolic(OP.MUL, this.expr, w.expr);
    }

    @Override
    public WrapperSymbolic ddiv(WrapperSymbolic w) {
        return new WrapperSymbolic(OP.DIV, this.expr, w.expr);
    }

    @Override
    public WrapperSymbolic drem(WrapperSymbolic w) {
        return new WrapperSymbolic(OP.REM, this.expr, w.expr);
    }

    @Override
    public WrapperSymbolic dneg() {
        return new WrapperSymbolic(OP.NEG, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_sqrt() {
        return new WrapperSymbolic(OP.SQRT, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_abs() {
        return new WrapperSymbolic(OP.ABS, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_sin() {
        return new WrapperSymbolic(OP.SIN, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_cos() {
        return new WrapperSymbolic(OP.COS, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_tan() {
        return new WrapperSymbolic(OP.TAN, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_asin() {
        return new WrapperSymbolic(OP.ASIN, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_acos() {
        return new WrapperSymbolic(OP.ACOS, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_atan() {
        return new WrapperSymbolic(OP.ATAN, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_sinh() {
        return new WrapperSymbolic(OP.SINH, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_cosh() {
        return new WrapperSymbolic(OP.COSH, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_tanh() {
        return new WrapperSymbolic(OP.TANH, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_exp() {
        return new WrapperSymbolic(OP.EXP, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_log() {
        return new WrapperSymbolic(OP.LOG, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_log10() {
        return new WrapperSymbolic(OP.LOG10, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_toRadians() {
        return new WrapperSymbolic(OP.RAD, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_toDegrees() {
        return new WrapperSymbolic(OP.DEG, this.expr, null);
    }

    @Override
    public WrapperSymbolic math_min(WrapperSymbolic w) {
        return new WrapperSymbolic(OP.MIN, this.expr, w.expr);
    }

    @Override
    public WrapperSymbolic math_max(WrapperSymbolic w) {
        return new WrapperSymbolic(OP.MAX, this.expr, w.expr);
    }

    @Override
    public WrapperSymbolic math_pow(WrapperSymbolic w) {
        return new WrapperSymbolic(OP.POW, this.expr, w.expr);
    }

    @Override
    public WrapperSymbolic math_hypot(WrapperSymbolic w) {
        return new WrapperSymbolic(OP.HYPOT, this.expr, w.expr);
    }

    // -------------------------------------------------------------------------
    // ----------------- Comparison operator _----------------------------------
    // -------------------------------------------------------------------------
    @Override
    public int dcmpl(WrapperSymbolic w) {
        // Averti que l'on ne peut pas comparer des fonctions
        if (this.expr.containsUnknown || w.expr.containsUnknown)
            pkgLogger().log(Level.WARNING, "Can not compare symbolic expressions containing unknowns");
        return super.dcmpl(w);
    }

    @Override
    public int dcmpg(WrapperSymbolic w) {
        // Averti que l'on ne peut pas comparer des fonctions
        if (this.expr.containsUnknown || w.expr.containsUnknown)
            pkgLogger().log(Level.WARNING, "Can not compare symbolic expressions containing unknowns");
        return super.dcmpg(w);
    }

    // -------------------------------------------------------------------------
    // ----------------- Necessary wrapper methods -----------------------------
    // -------------------------------------------------------------------------
    @Override
    public double toDouble() {
        return expr.evaluate();
    }

    @Override
    public WrapperSymbolic fromDouble(double a, @SuppressWarnings("unused") boolean wasFromFloat) {
        return new WrapperSymbolic(a);
    }

    @Override
    public String asInternalString() {
        return expr + "";
    }

    @Override
    public String wrapperName() {
        return "Symbolic";
    }

    // -------------------------------------------------------------------------
    // ----------------- Magic methods -----------------------------------------
    // -------------------------------------------------------------------------

    public static boolean COJAC_MAGIC_isSymbolicFunction(CommonDouble<WrapperSymbolic> d) {
        return d.val.expr.containsUnknown;
    }

    public static CommonDouble<WrapperSymbolic> COJAC_MAGIC_identityFct() {
        WrapperSymbolic res = new WrapperSymbolic();
        return new CommonDouble<>(res);
    }

    public static CommonDouble<WrapperSymbolic> COJAC_MAGIC_evaluateAt(CommonDouble<WrapperSymbolic> d, CommonDouble<WrapperSymbolic> x) {
        double result = d.val.expr.evaluate(x.val.expr.evaluate());
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonDouble<>(res);
    }

    public static CommonDouble<WrapperSymbolic> COJAC_MAGIC_derivative(CommonDouble<WrapperSymbolic> d) {
        WrapperSymbolic res = new WrapperSymbolic(d.val.expr.derivate());
        return new CommonDouble<>(res);
    }

    public static void COJAC_MAGIC_setSymbolicEvaluationMode(boolean smartMode) {
        smart_evaluation_mode=smartMode;
    }

    public static void COJAC_MAGIC_setConstantSubtreeMode(boolean keepTreeMode) {
        keep_constant_subtrees_mode=keepTreeMode;
    }
    
    // -------------------------------------------------------------------------
    // ----------------- Useful methods ----------------------------------------
    // -------------------------------------------------------------------------

    private static Logger pkgLogger() {
        return Logger.getLogger(WrapperChebfun.class.getPackage().getName());
    }

    //==========================================================================
    
}
