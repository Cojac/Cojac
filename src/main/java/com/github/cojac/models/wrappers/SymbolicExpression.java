package com.github.cojac.models.wrappers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.DoubleBinaryOperator;

import com.github.cojac.symbolic.SymbUtils;
import com.github.cojac.symbolic.SymbUtils.SymbolicDerivationOperator;

// -------------------------------------------------------------------------
// ----------------- Symbolic expression tree ------------------------------
// -------------------------------------------------------------------------
public class SymbolicExpression {
    // Active compression for constant sub trees
    private static final boolean COMPRESSION_MODE = false;
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
    public SymbolicExpression(SymbolicExpression.OP oper, SymbolicExpression left, SymbolicExpression right) {
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
            // Trie les termes de manière absolue et dans un ordre descendent
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

    //=======================================================================
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
        // Apply the standard operator
        public double apply(double left, double right) {
            return binaryOp.applyAsDouble(left, right);
        }
        // Apply the differentiation
        public SymbolicExpression derivate(SymbolicExpression se) {
            return symbOP.derivate(se);
        }
    }

}
// -------------------------------------------------------------------------