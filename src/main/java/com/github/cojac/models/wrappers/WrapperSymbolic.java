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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.function.DoubleBinaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.cojac.models.wrappers.WrapperSymbolic.SymbolicExpression.OP;
import com.github.cojac.symbolic.SymbUtils;
import com.github.cojac.symbolic.SymbUtils.SymbolicDerivationOperator;

public class WrapperSymbolic extends ACojacWrapper {

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
        this(w == null ? null : asSymbWrapper(w).expr);
    }
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
    @Override
    public double toDouble() {
        return expr.value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {

        return new WrapperSymbolic(a);
    }

    @Override
    public int dcmpl(ACojacWrapper w) {
        if (this.expr.containsUnknown || asSymbWrapper(w).expr.containsUnknown)
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not compare symbolic expressions containing unknowns");
        if (this.isNaN() || w.isNaN())
            return -1;
        return this.compareTo(w);
    }

    @Override
    public int dcmpg(ACojacWrapper w) {
        if (this.expr.containsUnknown || asSymbWrapper(w).expr.containsUnknown)
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
        return asSymbWrapper(d.val).expr.containsUnknown;
    }

    public static boolean COJAC_MAGIC_isSymbolicUnknown(CommonFloat d) {
        return asSymbWrapper(d.val).expr.containsUnknown;
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
        double result = asSymbWrapper(d.val).expr.evaluate(asSymbWrapper(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_evaluateSymbolicAt(CommonFloat d, CommonFloat x) {
        double result = asSymbWrapper(d.val).expr.evaluate(asSymbWrapper(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonFloat(res);
    }

    public static CommonDouble COJAC_MAGIC_evaluateBetterSymbolicAt(CommonDouble d, CommonDouble x) {
        double result = asSymbWrapper(d.val).expr.evaluateBetter(asSymbWrapper(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_evaluateBetterSymbolicAt(CommonFloat d, CommonFloat x) {
        double result = asSymbWrapper(d.val).expr.evaluateBetter(asSymbWrapper(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonFloat(res);
    }

    public static CommonDouble COJAC_MAGIC_derivateSymbolic(CommonDouble d) {
        WrapperSymbolic res = new WrapperSymbolic(asSymbWrapper(d.val).expr.derivate());
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_derivateSymbolic(CommonFloat d) {
        WrapperSymbolic res = new WrapperSymbolic(asSymbWrapper(d.val).expr.derivate());
        return new CommonFloat(res);
    }

    // -------------------------------------------------------------------------

    private static WrapperSymbolic asSymbWrapper(ACojacWrapper w) {
        return (WrapperSymbolic) w;
    }

    // -------------------------------------------------------------------------

    public static class SymbolicExpression {
        private static final boolean COMPR = false;

        public final double value;
        public final boolean containsUnknown;
        public final OP oper;
        public final SymbolicExpression left;
        public final SymbolicExpression right;

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

        public SymbolicExpression(OP oper, SymbolicExpression left) {
            this.value = oper.apply(left.value, Double.NaN);
            this.containsUnknown = left.containsUnknown;
            if (this.containsUnknown || !COMPR) {
                this.oper = oper;
                this.left = left;
                this.right = null;
            } else {
                this.oper = OP.NOP;
                this.left = null;
                this.right = null;
            }
        }

        public SymbolicExpression(OP oper, SymbolicExpression left, SymbolicExpression right) {
            this.value = oper.apply(left.value, right.value);
            this.containsUnknown = left.containsUnknown ||
                    right.containsUnknown;
            if (this.containsUnknown || !COMPR) {
                this.oper = oper;
                this.left = left;
                this.right = right;
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

        public SymbolicExpression derivate() {
            return oper.derivate(this);
        }

        public ArrayList<SymbolicExpression> flatOperatorForSummation() {
            ArrayList<SymbolicExpression> listOfSE = new ArrayList<SymbolicExpression>();
            if (oper == OP.ADD) {
                listOfSE.addAll(left.flatOperatorForSummation());
                listOfSE.addAll(right.flatOperatorForSummation());
            } else if (oper == OP.SUB) {
                listOfSE.addAll(left.flatOperatorForSummation());
                for (SymbolicExpression se : right.flatOperatorForSummation())
                    listOfSE.add(new SymbolicExpression(OP.NEG, se));
            } else if (oper == OP.NEG) {
                for (SymbolicExpression se : left.flatOperatorForSummation())
                    listOfSE.add(new SymbolicExpression(OP.NEG, se));
            } else {
                listOfSE.add(this);
            }
            return listOfSE;
        }

        public double evaluateBetter(double x) {
            if (oper == OP.ADD || oper == OP.SUB) {
                ArrayList<SymbolicExpression> listOfSE = this.flatOperatorForSummation();
                ArrayList<Double> list = new ArrayList<Double>();
                for (SymbolicExpression se : listOfSE)
                    list.add(se.evaluateBetter(x));
                list.sort(new AbsDescComparator());

                double sum = 0;
                double corr = 0;
                for (Double term : list) {
                    double corrTerm = term - corr;
                    double tmpSum = sum + corrTerm;
                    corr = (tmpSum - sum) - corrTerm;
                    sum = tmpSum;
                }

                /*
                 * double sum = 0; while (true) {
                 * 
                 * list.sort(new ABSComparator());
                 * 
                 * int shortestIndex = 1; double shortestDist =
                 * Double.POSITIVE_INFINITY; for (int i = 0; i + 1 <
                 * list.size(); i++) { double tmpDist =
                 * relativeDistance(list.get(i), list.get(i + 1)); if (tmpDist <
                 * shortestDist) { shortestDist = tmpDist; shortestIndex = i; }
                 * } sum = list.remove(shortestIndex) +
                 * list.remove(shortestIndex); if (list.isEmpty()) break;
                 * list.add(sum); }
                 */

                return sum;
            }

            if (containsUnknown && oper == OP.NOP)
                return x;
            if (oper == OP.NOP)
                return value;
            if (right != null)
                return oper.apply(left.evaluateBetter(x), right.evaluateBetter(x));
            return oper.apply(left.evaluateBetter(x), Double.NaN);
        }

        public double relativeDistance(double a, double b) {
            a = Math.abs(a);
            b = Math.abs(b);
            if (a >= b)
                return Math.abs(a - b) / a;
            return Math.abs(a - b) / b;
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

        public static class AbsDescComparator implements Comparator<Double> {

            @Override
            public int compare(Double d1, Double d2) {
                return Double.compare(Math.abs(d2), Math.abs(d1));
            }
        }

        public static enum OP {
            NOP(((x, y) -> Double.NaN), SymbUtils::derivateNOP),
            ADD(((x, y) -> x + y), SymbUtils::derivateADD),
            SUB(((x, y) -> x - y), SymbUtils::derivateSUB),
            MUL(((x, y) -> x * y), SymbUtils::derivateMUL),
            DIV(((x, y) -> x / y), SymbUtils::derivateDIV),
            REM(((x, y) -> x % y), SymbUtils::derivateREM),
            NEG(((x, y) -> -x), SymbUtils::derivateNEG),
            SQRT((x, y) -> Math.sqrt(x), SymbUtils::derivateSQRT),
            ABS((x, y) -> Math.abs(x), SymbUtils::derivateABS),
            SIN((x, y) -> Math.sin(x), SymbUtils::derivateSIN),
            COS((x, y) -> Math.cos(x), SymbUtils::derivateCOS),
            TAN((x, y) -> Math.tan(x), SymbUtils::derivateTAN),
            ASIN((x, y) -> Math.asin(x), SymbUtils::derivateASIN),
            ACOS((x, y) -> Math.acos(x), SymbUtils::derivateACOS),
            ATAN((x, y) -> Math.atan(x), SymbUtils::derivateATAN),
            SINH((x, y) -> Math.sinh(x), SymbUtils::derivateSINH),
            COSH((x, y) -> Math.cosh(x), SymbUtils::derivateCOSH),
            TANH((x, y) -> Math.tanh(x), SymbUtils::derivateTANH),
            EXP((x, y) -> Math.exp(x), SymbUtils::derivateEXP),
            LOG((x, y) -> Math.log(x), SymbUtils::derivateLOG),
            LOG10((x, y) -> Math.log10(x), SymbUtils::derivateLOG10),
            RAD((x, y) -> Math.toRadians(x), SymbUtils::derivateRAD),
            DEG((x, y) -> Math.toDegrees(x), SymbUtils::derivateDEG),
            MIN((x, y) -> Math.min(x, y), SymbUtils::derivateMIN),
            MAX((x, y) -> Math.max(x, y), SymbUtils::derivateMAX),
            POW((x, y) -> Math.pow(x, y), SymbUtils::derivatePOW);

            private final DoubleBinaryOperator binaryOp;
            private final SymbolicDerivationOperator symbOP;

            OP(DoubleBinaryOperator binaryOp, SymbolicDerivationOperator symbOP) {
                this.binaryOp = binaryOp;
                this.symbOP = symbOP;
            }

            public double apply(double left, double right) {
                return binaryOp.applyAsDouble(left, right);
            }

            public SymbolicExpression derivate(SymbolicExpression se) {
                return symbOP.derivate(se);
            }
        }

    }
    // -------------------------------------------------------------------------

}
