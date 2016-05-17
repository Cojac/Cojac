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
import java.util.function.DoubleBinaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.cojac.models.wrappers.WrapperSymbolic.SymbolicExpression.OP;
import com.github.cojac.symbolic.SymbUtils;
import com.github.cojac.symbolic.SymbUtils.SymbolicDerivationOperator;

public class WrapperSymbolic extends ACojacWrapper {

    private final SymbolicExpression expr; //

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
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not compare symbolic expressions containing unknowns");
        return super.dcmpl(w);
    }

    @Override
    public int dcmpg(ACojacWrapper w) {
        // Averti que l'on ne peut pas comparer des fonctions
        if (this.expr.containsUnknown || asSymbWrapper(w).expr.containsUnknown)
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not compare symbolic expressions containing unknowns");
        return super.dcmpg(w);
    }

    // -------------------------------------------------------------------------
    // ----------------- Necessary wrapper methods -----------------------------
    // -------------------------------------------------------------------------
    @Override
    public double toDouble() {
        return expr.value;
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

    public static boolean COJAC_MAGIC_isSymbolicFunction(CommonFloat d) {
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
    // ----------------- Useful methods ----------------------------------------
    // -------------------------------------------------------------------------

    private static WrapperSymbolic asSymbWrapper(ACojacWrapper w) {
        return (WrapperSymbolic) w;
    }

    // -------------------------------------------------------------------------
    // ----------------- Symbolic expression tree ------------------------------
    // -------------------------------------------------------------------------
    public static class SymbolicExpression {
        // Active compression for constant sub trees
        private static final boolean COMPRESSION_MODE = false;
        // The value of the constant or the value of the sub tree if
        // !containsUnknown
        public final double value;
        // Indicate if the current sub contains at least one unknown
        public final boolean containsUnknown;
        // Determine the current operator ( if the node is an unknown or a
        // constant the operator is NOP)
        public final OP oper;
        // Sub trees : both are NULL if the node is a constant or an unknown
        // right is null if the node is an unary operator
        public final SymbolicExpression left;
        public final SymbolicExpression right;

        // Constructor for unknown
        public SymbolicExpression() {
            this.value = Double.NaN;
            this.containsUnknown = true;
            this.oper = OP.NOP;
            this.right = null;
            this.left = null;
        }

        // Constructor for constant
        public SymbolicExpression(double value) {
            this.value = value;
            this.containsUnknown = false;
            this.oper = OP.NOP;
            this.left = null;
            this.right = null;
        }

        // // Constructor for operators
        // public SymbolicExpression(OP oper, SymbolicExpression left) {
        // this.value = oper.apply(left.value, Double.NaN);
        // this.containsUnknown = left.containsUnknown;
        // if (this.containsUnknown || !COMPRESSION_MODE) {
        // this.oper = oper;
        // this.left = left;
        // this.right = null;
        // } else {
        // this.oper = OP.NOP;
        // this.left = null;
        // this.right = null;
        // }
        // }

        // Constructor operator
        public SymbolicExpression(OP oper, SymbolicExpression left, SymbolicExpression right) {
            // pre-compute the value of the tree
            this.value = oper.apply(left.value, (right != null) ? right.value
                    : Double.NaN);
            this.containsUnknown = left.containsUnknown || ((right != null)
                    ? right.containsUnknown : false);
            if (this.containsUnknown || !COMPRESSION_MODE) {
                this.oper = oper;
                this.left = left;
                this.right = right;
            } else {
                this.oper = OP.NOP;
                this.left = null;
                this.right = null;
            }
        }

        // Evaluate the current sub-tree at "x"
        public double evaluate(double x) {
            if (containsUnknown && oper == OP.NOP)
                return x;
            if (oper == OP.NOP)
                return value;
            if (right != null)
                return oper.apply(left.evaluate(x), right.evaluate(x));
            return oper.apply(left.evaluate(x), Double.NaN);
        }

        // Differentiate the current sub-tree
        public SymbolicExpression derivate() {
            return oper.derivate(this);
        }

        // Mise à plat des opérateurs d'addition et de soustration
        public ArrayList<SymbolicExpression> flatOperatorForSummation() {
            ArrayList<SymbolicExpression> listOfSE = new ArrayList<SymbolicExpression>();
            if (oper == OP.ADD) {
                listOfSE.addAll(left.flatOperatorForSummation());
                listOfSE.addAll(right.flatOperatorForSummation());
            } else if (oper == OP.SUB) {
                listOfSE.addAll(left.flatOperatorForSummation());
                for (SymbolicExpression se : right.flatOperatorForSummation())
                    listOfSE.add(new SymbolicExpression(OP.NEG, se, null));
            } else if (oper == OP.NEG) {
                for (SymbolicExpression se : left.flatOperatorForSummation())
                    listOfSE.add(new SymbolicExpression(OP.NEG, se, null));
            } else {
                listOfSE.add(this);
            }
            return listOfSE;
        }

        /*
         * Cette méthode permet d'évaluter l'arbre d'un manière plus précise.
         * Elle met à plat l'opérateur d'addition et de soustration puis
         * réordonne ceux-ci dans un ordre asbosolu décroissant et finalement
         * applique la somme de Kahan
         */
        public double evaluateBetter(double x) {
            // Si l'on rencontre ADD ou SUB -> mise à plat -> Kahan
            if (oper == OP.ADD || oper == OP.SUB) {
                ArrayList<SymbolicExpression> listOfSE = this.flatOperatorForSummation();
                ArrayList<Double> list = new ArrayList<Double>();
                for (SymbolicExpression se : listOfSE)
                    list.add(se.evaluateBetter(x));

                list.sort(new Comparator<Double>() {
                    @Override
                    public int compare(Double d1, Double d2) {
                        return Double.compare(Math.abs(d2), Math.abs(d1));
                    };
                });

                double sum = 0;
                double corr = 0;
                for (Double term : list) {
                    double corrTerm = term - corr;
                    double tmpSum = sum + corrTerm;
                    corr = (tmpSum - sum) - corrTerm;
                    sum = tmpSum;
                }

                /*
                 * Somme les termes dans l'ordre les plus proches
                 * (relatif/asbsolue) double sum = 0; while (true) {
                 * 
                 * list.sort(new AbsAscComparator());
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

        // Retourne la distance relative absolue
        public double relativeDistance(double a, double b) {
            a = Math.abs(a);
            b = Math.abs(b);
            if (a >= b)
                return Math.abs(a - b) / a;
            return Math.abs(a - b) / b;
        }

        // Retourne la réprentation de l'arbre sous la forme prefixe
        public String toString() {
            if (containsUnknown && oper == OP.NOP)
                return "x";
            if (oper == OP.NOP)
                return value + "";
            if (right != null)
                return oper + "(" + left + "," + right + ")";
            return oper + "(" + left + ")";
        }

        // Define all the operator managed by the wrapper
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

            // Standard operator
            private final DoubleBinaryOperator binaryOp;
            // Operator use for differentiating
            private final SymbolicDerivationOperator symbOP;

            // Constructor, define the operators
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
