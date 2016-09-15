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
import com.github.cojac.models.wrappers.SymbolicExpression.OP;


public class WrapperSymbolic extends ACojacWrapper {
    // In "smart" mode, terms are reordered (slower, but often more precise) 
    public static boolean smart_evaluation_mode = true;
    // If constant subtrees are dropped, we lose an opportunity to improve precision (via smart mode)
    public static boolean keep_constant_subtrees_mode = true;

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
    }

    // -------------------------------------------------------------------------
    // ----------------- Necessary constructor ---------------------------------
    // -------------------------------------------------------------------------
    public WrapperSymbolic(ACojacWrapper w) {
        this(w == null ? null : asSymbWrapper(w).expr);
    }

    // -------------------------------------------------------------------------
    // ----------------- Override operators ------------------------------------
    // -------------------------------------------------------------------------
    @Override
    public ACojacWrapper dadd(ACojacWrapper w) {
        return new WrapperSymbolic(OP.ADD, this.expr, asSymbWrapper(w).expr);
    }

    @Override
    public ACojacWrapper dsub(ACojacWrapper w) {
        return new WrapperSymbolic(OP.SUB, this.expr, asSymbWrapper(w).expr);
    }

    @Override
    public ACojacWrapper dmul(ACojacWrapper w) {
        return new WrapperSymbolic(OP.MUL, this.expr, asSymbWrapper(w).expr);
    }

    @Override
    public ACojacWrapper ddiv(ACojacWrapper w) {
        return new WrapperSymbolic(OP.DIV, this.expr, asSymbWrapper(w).expr);
    }

    @Override
    public ACojacWrapper drem(ACojacWrapper w) {
        return new WrapperSymbolic(OP.REM, this.expr, asSymbWrapper(w).expr);
    }

    @Override
    public ACojacWrapper dneg() {
        return new WrapperSymbolic(OP.NEG, this.expr, null);
    }

    @Override
    public ACojacWrapper math_sqrt() {
        return new WrapperSymbolic(OP.SQRT, this.expr, null);
    }

    @Override
    public ACojacWrapper math_abs() {
        return new WrapperSymbolic(OP.ABS, this.expr, null);
    }

    @Override
    public ACojacWrapper math_sin() {
        return new WrapperSymbolic(OP.SIN, this.expr, null);
    }

    @Override
    public ACojacWrapper math_cos() {
        return new WrapperSymbolic(OP.COS, this.expr, null);
    }

    @Override
    public ACojacWrapper math_tan() {
        return new WrapperSymbolic(OP.TAN, this.expr, null);
    }

    @Override
    public ACojacWrapper math_asin() {
        return new WrapperSymbolic(OP.ASIN, this.expr, null);
    }

    @Override
    public ACojacWrapper math_acos() {
        return new WrapperSymbolic(OP.ACOS, this.expr, null);
    }

    @Override
    public ACojacWrapper math_atan() {
        return new WrapperSymbolic(OP.ATAN, this.expr, null);
    }

    @Override
    public ACojacWrapper math_sinh() {
        return new WrapperSymbolic(OP.SINH, this.expr, null);
    }

    @Override
    public ACojacWrapper math_cosh() {
        return new WrapperSymbolic(OP.COSH, this.expr, null);
    }

    @Override
    public ACojacWrapper math_tanh() {
        return new WrapperSymbolic(OP.TANH, this.expr, null);
    }

    @Override
    public ACojacWrapper math_exp() {
        return new WrapperSymbolic(OP.EXP, this.expr, null);
    }

    @Override
    public ACojacWrapper math_log() {
        return new WrapperSymbolic(OP.LOG, this.expr, null);
    }

    @Override
    public ACojacWrapper math_log10() {
        return new WrapperSymbolic(OP.LOG10, this.expr, null);
    }

    @Override
    public ACojacWrapper math_toRadians() {
        return new WrapperSymbolic(OP.RAD, this.expr, null);
    }

    @Override
    public ACojacWrapper math_toDegrees() {
        return new WrapperSymbolic(OP.DEG, this.expr, null);
    }

    @Override
    public ACojacWrapper math_min(ACojacWrapper w) {
        return new WrapperSymbolic(OP.MIN, this.expr, asSymbWrapper(w).expr);
    }

    @Override
    public ACojacWrapper math_max(ACojacWrapper w) {
        return new WrapperSymbolic(OP.MAX, this.expr, asSymbWrapper(w).expr);
    }

    @Override
    public ACojacWrapper math_pow(ACojacWrapper w) {
        return new WrapperSymbolic(OP.POW, this.expr, asSymbWrapper(w).expr);
    }

    // -------------------------------------------------------------------------
    // ----------------- Comparison operator _----------------------------------
    // -------------------------------------------------------------------------
    @Override
    public int dcmpl(ACojacWrapper w) {
        // Averti que l'on ne peut pas comparer des fonctions
        if (this.expr.containsUnknown || asSymbWrapper(w).expr.containsUnknown)
            pkgLogger().log(Level.WARNING, "Can not compare symbolic expressions containing unknowns");
        return super.dcmpl(w);
    }

    @Override
    public int dcmpg(ACojacWrapper w) {
        // Averti que l'on ne peut pas comparer des fonctions
        if (this.expr.containsUnknown || asSymbWrapper(w).expr.containsUnknown)
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
    public ACojacWrapper fromDouble(double a, @SuppressWarnings("unused") boolean wasFromFloat) {
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

    public static boolean COJAC_MAGIC_isSymbolicFunction(CommonDouble d) {
        return asSymbWrapper(d.val).expr.containsUnknown;
    }

    public static CommonDouble COJAC_MAGIC_asSymbolicUnknown() {
        WrapperSymbolic res = new WrapperSymbolic();
        return new CommonDouble(res);
    }

    public static CommonDouble COJAC_MAGIC_evaluateSymbolicAt(CommonDouble d, CommonDouble x) {
        double result = asSymbWrapper(d.val).expr.evaluate(asSymbWrapper(x.val).expr.evaluate());
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonDouble(res);
    }

    public static CommonDouble COJAC_MAGIC_derivateSymbolic(CommonDouble d) {
        WrapperSymbolic res = new WrapperSymbolic(asSymbWrapper(d.val).expr.derivate());
        return new CommonDouble(res);
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

    private static WrapperSymbolic asSymbWrapper(ACojacWrapper w) {
        return (WrapperSymbolic) w;
    }

    //==========================================================================
    
}
