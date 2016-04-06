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
import java.util.PriorityQueue;
import java.util.function.DoubleBinaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.block.Arrangement;

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

        return new WrapperSymbolic(a);
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

    public static CommonDouble COJAC_MAGIC_evaluateBetterSymbolicAt(CommonDouble d, CommonDouble x) {
        double result = symb(d.val).expr.evaluateBetter(symb(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_evaluateBetterSymbolicAt(CommonFloat d, CommonFloat x) {
        double result = symb(d.val).expr.evaluateBetter(symb(x.val).expr.value);
        WrapperSymbolic res = new WrapperSymbolic(result);
        return new CommonFloat(res);
    }

    public static CommonDouble COJAC_MAGIC_derivateSymbolic(CommonDouble d) {
        WrapperSymbolic res = new WrapperSymbolic(symb(d.val).expr.derivate());
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_derivateSymbolic(CommonFloat d) {
        WrapperSymbolic res = new WrapperSymbolic(symb(d.val).expr.derivate());
        return new CommonFloat(res);
    }

    // -------------------------------------------------------------------------
    private static WrapperSymbolic symb(ACojacWrapper w) {
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
            switch (oper) {
            case NOP:
                return derivateNOP();
            case ADD:
                return derivateADD();
            case SUB:
                return derivateSUB();
            case MUL:
                return derivateMUL();
            case DIV:
                return derivateDIV();
            case NEG:
                return derivateNEG();
            case REM:
                return derivateREM();
            case SQRT:
                return derivateSQRT();
            case ABS:
                return derivateABS();
            case SIN:
                return derivateSIN();
            case COS:
                return derivateCOS();
            case TAN:
                return derivateTAN();
            case ASIN:
                return derivateASIN();
            case ACOS:
                return derivateACOS();
            case ATAN:
                return derivateATAN();
            case SINH:
                return derivateSINH();
            case COSH:
                return derivateCOSH();
            case TANH:
                return derivateTANH();
            case EXP:
                return derivateEXP();
            case LOG:
                return derivateLOG();
            case LOG10:
                return derivateLOG10();
            case RAD:
                return derivateRAD();
            case DEG:
                return derivateDEG();
            case MIN:
                return derivateMIN();
            case MAX:
                return derivateMAX();
            case POW:
                return derivatePOW();
            default:

                return null;
            }
        }

        // -------------------------------------------------------------------------
        public SymbolicExpression derivateNOP() {
            if (containsUnknown)
                // x -> 1
                return new SymbolicExpression(1d);
            // C -> 0
            return new SymbolicExpression(0d);
        }

        // u + v -> u' + v'
        public SymbolicExpression derivateADD() {

            return new SymbolicExpression(OP.ADD, left.derivate(), right.derivate());
        }

        // u - v -> u' - v'
        public SymbolicExpression derivateSUB() {

            return new SymbolicExpression(OP.SUB, left.derivate(), right.derivate());
        }

        // u * v -> u' * v + u * v'
        public SymbolicExpression derivateMUL() {
            /*
             * // f(x) * C -> f'(x) * C if (left.containsUnknown &&
             * !right.containsUnknown) return new SymbolicExpression(oper,
             * left.derivate(), right); // C * f(x) -> C * f'(x) if
             * (!left.containsUnknown && right.containsUnknown) return new
             * SymbolicExpression(oper, left, right.derivate());
             */
            SymbolicExpression dl_mul_r = new SymbolicExpression(OP.MUL, left.derivate(), right);
            SymbolicExpression l_mul_dr = new SymbolicExpression(OP.MUL, left, right.derivate());
            return new SymbolicExpression(OP.ADD, dl_mul_r, l_mul_dr);
        }

        // u / v -> (u' * v - u * v') / v^2
        public SymbolicExpression derivateDIV() {
            SymbolicExpression dl_mul_r = new SymbolicExpression(OP.MUL, left.derivate(), right);
            SymbolicExpression l_mul_dr = new SymbolicExpression(OP.MUL, left, right.derivate());
            SymbolicExpression n = new SymbolicExpression(OP.SUB, dl_mul_r, l_mul_dr);
            SymbolicExpression d = new SymbolicExpression(OP.POW, right, new SymbolicExpression(2));
            return new SymbolicExpression(OP.DIV, n, d);
        }

        public SymbolicExpression derivateNEG() {
            return new SymbolicExpression(OP.NEG, left.derivate());
        }

        public SymbolicExpression derivateREM() {
            if (containsUnknown) {
                Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not derivate symbolic expressions containing operator %");
                return new SymbolicExpression(Double.NaN);
            }
            return new SymbolicExpression(0d);
        }

        // sqrt(f(x)) -> f'(x)/(2*sqrt(f(x)))
        public SymbolicExpression derivateSQRT() {
            SymbolicExpression n = left.derivate();
            SymbolicExpression d = new SymbolicExpression(OP.MUL, new SymbolicExpression(2), this);
            return new SymbolicExpression(OP.DIV, n, d);
        }

        // |u| or sqrt(u^2) -> u*u'/|u|
        public SymbolicExpression derivateABS() {
            SymbolicExpression n = new SymbolicExpression(OP.MUL, left, left.derivate());
            return new SymbolicExpression(OP.DIV, n, this);
        }

        // sin(u) -> cos(u) * u'
        public SymbolicExpression derivateSIN() {
            SymbolicExpression l = new SymbolicExpression(OP.COS, left);
            return new SymbolicExpression(OP.MUL, l, left.derivate());
        }

        // cos(u) -> -sin(u) * u'
        public SymbolicExpression derivateCOS() {
            SymbolicExpression l1 = new SymbolicExpression(OP.SIN, left);
            SymbolicExpression l2 = new SymbolicExpression(OP.NEG, l1);
            return new SymbolicExpression(OP.MUL, l2, left.derivate());
        }

        // tan(u) -> u'/cos^2(u)
        public SymbolicExpression derivateTAN() {
            SymbolicExpression n = left.derivate();
            SymbolicExpression d = new SymbolicExpression(OP.COS, left);
            d = new SymbolicExpression(OP.POW, d, new SymbolicExpression(2));
            return new SymbolicExpression(OP.DIV, n, d);
        }

        // asin(u) -> u'/sqrt(1-u^2)
        public SymbolicExpression derivateASIN() {
            SymbolicExpression n = left.derivate();
            SymbolicExpression d = new SymbolicExpression(OP.POW, left, new SymbolicExpression(2));
            d = new SymbolicExpression(OP.SUB, new SymbolicExpression(1), d);
            d = new SymbolicExpression(OP.SQRT, d);
            return new SymbolicExpression(OP.DIV, n, d);
        }

        // acos(u) -> -u'/sqrt(1-u^2)
        public SymbolicExpression derivateACOS() {
            SymbolicExpression n = new SymbolicExpression(OP.NEG, left.derivate());
            SymbolicExpression d = new SymbolicExpression(OP.POW, left, new SymbolicExpression(2));
            d = new SymbolicExpression(OP.SUB, new SymbolicExpression(1), d);
            d = new SymbolicExpression(OP.SQRT, d);
            return new SymbolicExpression(OP.DIV, n, d);
        }

        // atan(u) -> u'/(1-u^2)
        public SymbolicExpression derivateATAN() {
            SymbolicExpression n = left.derivate();
            SymbolicExpression d = new SymbolicExpression(OP.POW, left, new SymbolicExpression(2));
            d = new SymbolicExpression(OP.ADD, new SymbolicExpression(1), d);
            return new SymbolicExpression(OP.DIV, n, d);
        }

        // sinh(u) -> cosh(u) * u'
        public SymbolicExpression derivateSINH() {
            SymbolicExpression l = new SymbolicExpression(OP.COSH, left);
            return new SymbolicExpression(OP.MUL, l, left.derivate());
        }

        // cosh(u) -> sinh(u) * u'
        public SymbolicExpression derivateCOSH() {
            SymbolicExpression l = new SymbolicExpression(OP.SINH, left);
            return new SymbolicExpression(OP.MUL, l, left.derivate());
        }

        // tanh(u) -> u'/cosh^2(u)
        public SymbolicExpression derivateTANH() {
            SymbolicExpression n = left.derivate();
            SymbolicExpression d = new SymbolicExpression(OP.COSH, left);
            d = new SymbolicExpression(OP.POW, d, new SymbolicExpression(2));
            return new SymbolicExpression(OP.DIV, n, d);
        }

        // e^u -> e^u * u'
        public SymbolicExpression derivateEXP() {
            return new SymbolicExpression(OP.MUL, this, left.derivate());
        }

        // log(u) -> u'/u
        public SymbolicExpression derivateLOG() {
            return new SymbolicExpression(OP.DIV, left.derivate(), left);
        }

        // log10(u) -> u'/(u*log(10))
        public SymbolicExpression derivateLOG10() {
            SymbolicExpression n = left.derivate();
            SymbolicExpression d = new SymbolicExpression(OP.LOG, new SymbolicExpression(10));
            d = new SymbolicExpression(OP.MUL, d, left);
            return new SymbolicExpression(OP.DIV, n, d);
        }

        public SymbolicExpression derivateRAD() {
            if (containsUnknown) {
                Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not derivate symbolic expressions containing operator toRadians");
                return new SymbolicExpression(Double.NaN);
            }
            return new SymbolicExpression(0d);
        }

        public SymbolicExpression derivateDEG() {
            if (containsUnknown) {
                Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not derivate symbolic expressions containing operator toDegrees");
                return new SymbolicExpression(Double.NaN);
            }
            return new SymbolicExpression(0d);
        }

        public SymbolicExpression derivateMIN() {
            if (containsUnknown) {
                Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not derivate symbolic expressions containing operator min");
                return new SymbolicExpression(Double.NaN);
            }
            return new SymbolicExpression(0d);
        }

        public SymbolicExpression derivateMAX() {
            if (containsUnknown) {
                Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not derivate symbolic expressions containing operator max");
                return new SymbolicExpression(Double.NaN);
            }
            return new SymbolicExpression(0d);
        }

        // u^v = e^log(u^v) = e^(v*log(u))
        public SymbolicExpression derivatePOW() {
            // distinger ?? a^x et x^a??
            SymbolicExpression p = new SymbolicExpression(OP.LOG, left);
            p = new SymbolicExpression(OP.MUL, right, p);
            p = new SymbolicExpression(OP.EXP, p);
            return p.derivate();
        }

        // -------------------------------------------------------------------------
        /*
         * public double symbEvaluate(double x) { SEEStruct s =
         * this.toSEEStruct(x); return symbEvaluate(s);
         * 
         * } public double symbEvaluate(SEEStruct s) { double val = 0; for (int
         * i = 0; i< s.length(); i++){ val =s.operators[i].apply(val,
         * s.values[i]); } return val; }
         * 
         * public SEEStruct toSEEStruct(double x) { if (containsUnknown && oper
         * == OP.NOP) { SEEStruct s = new SEEStruct(); s.operators = new OP[1];
         * s.values = new double[]{x}; } if (oper == OP.NOP) { SEEStruct s = new
         * SEEStruct(); s.operators = new OP[1]; s.values = new double[]{value};
         * } if (right != null) { if(oper==OP.ADD) merge(left, right, oper); } }
         */

        public ArrayList<SymbolicExpression> flatOperator(OP fOper) {
            ArrayList<SymbolicExpression> listOfSE = new ArrayList<SymbolicExpression>();
            if (oper == fOper) {
                listOfSE.addAll(left.flatOperator(fOper));
                listOfSE.addAll(right.flatOperator(fOper));
            } else {
                listOfSE.add(this);
            }
            return listOfSE;
        }

        public double evaluateBetter(double x) {
            if (oper == OP.ADD) {
                ArrayList<SymbolicExpression> listOfSE = this.flatOperator(OP.ADD);

                // version 1
                // double sum = 0;
                // for (SymbolicExpression se : listOfSE)
                // sum += se.evaluateBetter(x);

                // version 2
                // PriorityQueue<Double> queue = new PriorityQueue<Double>();
                // double sum = 0;
                // for (SymbolicExpression se : listOfSE)
                // queue.add(se.evaluateBetter(x));
                // while (!queue.isEmpty()) {
                // sum += queue.remove();
                // }

                ArrayList<Double> list = new ArrayList<Double>();

                for (SymbolicExpression se : listOfSE)
                    list.add(se.evaluateBetter(x));
                double sum = 0;
                while (true) {

                    list.sort(new ABSComparator());

                    int shortestIndex = 1;
                    double shortestDist = Double.POSITIVE_INFINITY;
                    for (int i = 0; i + 1 < list.size(); i++) {
                        double tmpDist = relativeDistance(list.get(i), list.get(i +
                                1));
                        if (tmpDist < shortestDist) {
                            shortestDist = tmpDist;
                            shortestIndex = i;
                        }
                    }
                    sum = list.remove(shortestIndex) +
                            list.remove(shortestIndex);
                    if (list.isEmpty())
                        break;
                    list.add(sum);
                }

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
    }

    public static class ABSComparator implements Comparator<Double> {

        @Override
        public int compare(Double d1, Double d2) {
            return Double.compare(Math.abs(d1), Math.abs(d2));
        }
    }

    /*
     * private class SEEStruct {
     * 
     * double values[]; OP operators[];
     * 
     * public int length() { return operators.length; }
     * 
     * } private SEEStruct merge(SEEStruct left,SEEStruct right,OP oper){
     * 
     * SEEStruct s = this.new SEEStruct(); s.values= new
     * double[left.length()+right.length()]; left.operators[0]=OP.ADD;
     * right.operators[0]=oper; for (int i= 0,j= 0,k = 0;i<s.values.length;i++
     * ){ if (k>=left.length() || (j<left.length() &&
     * left.values[j]<=right.values[k])){ s.values[i]= left.values[j];
     * s.operators[i]= left.operators[j]; }else { s.values[i] = right.values[k];
     * s.operators[i]= right.operators[k]; } } return s; }
     */

    // -------------------------------------------------------------------------
    public enum OP {
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
