/*
 *    Copyright 2017 Frédéric Bapst et al.
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
 */

package com.github.cojac.models.wrappers;

import java.util.ArrayList;
import java.util.function.DoubleBinaryOperator;

import com.github.cojac.symbolic.SymbUtils;
import com.github.cojac.symbolic.SymbUtils.SymbolicDerivationOperator;

import static com.github.cojac.models.wrappers.WrapperSymbolic.*;

// -------------------------------------------------------------------------
// ----------------- Symbolic expression tree ------------------------------
// -------------------------------------------------------------------------
public class SymbolicExpression {
    // The value of the constant or the value of the sub tree if
    // !containsUnknown
    public final double value;
    // Indicate if the current sub contains at least one unknown
    public final boolean containsUnknown;
    // Determine the current operator ( if the node is an unknown or a
    // constant the operator is NOP)
    public final SymbolicExpression.OP oper;
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

    // Constructor for an operator with unknowns subtrees (used in SymbPattern)
    public SymbolicExpression(SymbolicExpression.OP oper) {
        this.value = Double.NaN;
        this.containsUnknown = true;
        this.oper = oper;
        if(oper == OP.ANY) {
            this.right = null;
            this.left = null;
        } else {
            this.right = new SymbolicExpression(OP.ANY);
            this.left = new SymbolicExpression(OP.ANY);
        }
    }

    // Constructor for an operator
    public SymbolicExpression(SymbolicExpression.OP oper, SymbolicExpression left, SymbolicExpression right) {
        // pre-compute the value of the tree
        this.value = oper.apply(left.value, (right != null) ? right.value
                : Double.NaN);
        this.containsUnknown = left.containsUnknown || ((right != null)
                ? right.containsUnknown : false);
        if (this.containsUnknown || keep_constant_subtrees_mode) {
            this.oper = oper;
            this.left = left;
            this.right = right;
        } else {
            this.oper = OP.NOP;
            this.left = null;
            this.right = null;
        }
    }

    // Differentiate the current sub-tree
    public SymbolicExpression derivate() {
        return oper.derivate(this);
    }

    // Mise à plat des opérateurs d'addition et de soustration
    // Example: ADD(ADD(a,b),NEG(ADD(c,SUB(d,e))))
    //        -> [a,b,-c,-d,e]
    public ArrayList<SymbolicExpression> flattenedTermList() {
        ArrayList<SymbolicExpression> listOfSE = new ArrayList<>();
        if (oper == OP.ADD) {
            listOfSE.addAll(left.flattenedTermList());
            listOfSE.addAll(right.flattenedTermList());
        } else if (oper == OP.SUB) {
            listOfSE.addAll(left.flattenedTermList());
            for (SymbolicExpression se : right.flattenedTermList())
                listOfSE.add(smartNegate(se));
        } else if (oper == OP.NEG) {
            for (SymbolicExpression se : left.flattenedTermList())
                listOfSE.add(smartNegate(se));
        } else {
            listOfSE.add(this);
        }
        return listOfSE;
    }

    private static SymbolicExpression smartNegate(SymbolicExpression se) {
        if(se.oper==OP.NEG) return se.left; // NEG(NEG(a)) --> a
        return new SymbolicExpression(OP.NEG, se, null);
    }

    public double evaluate() {
        if(use_cached_values) {
            return value;
        }
        return evaluate(Double.NaN); // typically the "unknown" won't appear...
    }
    
    // Evaluate the current sub-tree at "x"
    public double evaluate(double x) {
        if (oper == OP.NOP)
            return containsUnknown ? x : value;
        if (smart_evaluation_mode && (oper == OP.ADD || oper == OP.SUB))
            return evaluateTermsSmarter(x);            
        if (smart_evaluation_mode && (oper == OP.MUL))
            return evaluateProductSmarter(x);            
        if (right != null)
            return oper.apply(left.evaluate(x), right.evaluate(x));
        return oper.apply(left.evaluate(x), Double.NaN);
    }

    // just for (fun!) the demo, detect one of the following patterns:
    //   a*(b/a) == (b/a)*a == b
    private double evaluateProductSmarter(double x) {
        if(right.oper==OP.DIV && right.left.value!=0.0 && right.right.value==left.value)
            return right.left.evaluate(x);
        if(left.oper==OP.DIV && left.left.value!=0.0 && left.right.value==right.value)
            return left.left.evaluate(x);
        return oper.apply(left.evaluate(x), right.evaluate(x)); 
    }
    
    private double evaluateTermsSmarter(double x) {
        ArrayList<SymbolicExpression> terms = this.flattenedTermList();
        ArrayList<Double> termsValues = new ArrayList<>();
        for (SymbolicExpression se : terms)
            termsValues.add(se.evaluate(x));
        return smartSum(termsValues);
    }

    private double smartSum(ArrayList<Double> termsValues) {
        // TODO: reconsider how to sort terms (M. Badoud used decreasing abs value)
        /* Possible algorithm: 
         * - if all have the same sign: 
         *     possibly sort (increasing), then use Kahan
         * - if strong cancellation (sum of abs(xi) >> abs(sum of xi)): 
         *     ???
         *   else 
         *     ???
         */
        
        termsValues.sort((d1,d2) -> Double.compare(Math.abs(d2), Math.abs(d1)));
        //termsValues.sort((d1,d2) -> Double.compare(Math.abs(d1), Math.abs(d2)));
//        System.out.println(this.toString());
//        System.out.println("  "+terms);
//        System.out.println("  "+termsValues);
        // sum them with Kahan algorithm:
        double sum = 0;
        double corr = 0;
        for (double term : termsValues) {
            double corrTerm = term - corr;
            double tmpSum = sum + corrTerm;
            corr = (tmpSum - sum) - corrTerm;
            sum = tmpSum;
        }
        return sum;
    }


    // Retourne la représentation de l'arbre sous la forme préfixe
    public String toString() {
        if (containsUnknown && oper == OP.NOP || oper == OP.ANY)
            return "x";
        if (oper == OP.NOP)
            return value + "";
        if (right != null)
            return oper + "(" + left + "," + right + ")";
        if (left != null)
            return oper + "(" + left + ")";
        return oper + "(x, x)";
    }

    //=======================================================================
    // Define all the operators managed by the wrapper
    // TODO: consider refactoring symbOP as an abstract OP.derivate() method.
    public static enum OP {
        NOP(((x, y) -> Double.NaN), SymbUtils::derivateNOP),
        ADD((Double::sum), SymbUtils::derivateADD, true),
        SUB(((x, y) -> x - y), SymbUtils::derivateSUB),
        MUL(((x, y) -> x * y), SymbUtils::derivateMUL, true),
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
        MIN(Math::min, SymbUtils::derivateMIN),
        MAX(Math::max, SymbUtils::derivateMAX),
        POW(Math::pow, SymbUtils::derivatePOW),
        HYPOT(Math::hypot, SymbUtils::derivateHYPOT, true),
        // profiler
        ANY(null, null);

        // Standard operator
        private final DoubleBinaryOperator binaryOp;
        // Operator use for differentiating
        private final SymbolicDerivationOperator symbOP;
        // Is the operation commutative ?
        // Used in SymbPattern
        private final boolean commutative;

        // Constructor, define the operators
        OP(DoubleBinaryOperator binaryOp, SymbolicDerivationOperator symbOP) {
            this(binaryOp, symbOP, false);
        }

        OP(DoubleBinaryOperator binaryOp, SymbolicDerivationOperator symbOP, boolean commutative) {
            this.binaryOp = binaryOp;
            this.symbOP = symbOP;
            this.commutative = commutative;
        }

        // Apply the standard operator
        public double apply(double left, double right) {
            return binaryOp.applyAsDouble(left, right);
        }
        // Apply the differentiation
        public SymbolicExpression derivate(SymbolicExpression se) {
            return symbOP.derivate(se);
        }

        public boolean isCommutative() {
            return this.commutative;
        }
    }

}
