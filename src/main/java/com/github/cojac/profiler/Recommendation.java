package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

public enum Recommendation {

   // --- DIRECT PATTERNS ---
   FMA(
           "FMA",
           // a * b + c
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.ADD,
                           new SymbolicExpression(SymbolicExpression.OP.MUL),
                           new SymbolicExpression(SymbolicExpression.OP.ANY)
                   )
           ),
           true
   ),
   SCALB(
           "scalb",
           // a * pow(2, b)
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.MUL,
                           new SymbolicExpression(SymbolicExpression.OP.ANY),
                           new SymbolicExpression(SymbolicExpression.OP.POW,
                                   new SymbolicExpression(2.0),
                                   new SymbolicExpression(SymbolicExpression.OP.ANY)
                           )
                   )
           ),
           true
   ),
   LOG1P(
           "log1p",
           // log(1 + x)
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.LOG,
                           new SymbolicExpression(SymbolicExpression.OP.ADD,
                                   new SymbolicExpression(1.0),
                                   new SymbolicExpression(SymbolicExpression.OP.ANY)
                           ),
                           null
                   )
           ),
           true
   ),
   EXPM1(
           "expm1",
           // exp(x) - 1
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.SUB,
                           new SymbolicExpression(SymbolicExpression.OP.EXP),
                           new SymbolicExpression(1.0)
                   )
           ),
           true
   ),
   // --- PROFILED PATTERNS ---
   INTPOW(
           "pow of int",
           // pow(a, x)
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.POW)
                   // no validator even though we want x to always be an integer
                   // because we need to instrument its value further down the process
           ),
           false
   ),
   PYTH_TO_HYPOT(
           "hypot instead of sqrt",
           // sqrt(a*a + b*b)
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.SQRT,
                           new SymbolicExpression(SymbolicExpression.OP.ADD,
                                   new SymbolicExpression(SymbolicExpression.OP.MUL),
                                   new SymbolicExpression(SymbolicExpression.OP.MUL)
                           ),
                           null
                   ),
                   (other) -> {
                      // no fears of null pointers because the pattern is matched first
                      SymbolicExpression firstMul = other.left.left;
                      SymbolicExpression secMul = other.left.right;
                      return firstMul.left.evaluate() == firstMul.right.evaluate() &&
                              secMul.left.evaluate() == secMul.right.evaluate();
                   }
           ),
           false
   ),
   HYPOT_TO_PYTH(
           "sqrt instead of hypot",
           // hypot(a, b)
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.HYPOT)
           ),
           false
   ),
   USELESS_ABS(
           "useless abs call",
           // abs(x)
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.ABS)
           ),
           false
   ),
   // unused for now
   SIN_APPROX(
           "sin can be approximated",
           // sin(x)
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.SIN)
           ),
           false
   ),
   COS_APPROX(
           "cos can be approximated",
           // cos(x)
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.COS)
           ),
           false
   ),
   TAN_APPROX(
           "tan can be approximated",
           // tan(x)
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.TAN)
           ),
           false
   );

   private final String recommendation;
   private final SymbPattern pattern;
   private final boolean directMatch;

   Recommendation(String recommendation, SymbPattern pattern, boolean directMatch) {
      this.recommendation = recommendation;
      this.pattern = pattern;
      this.directMatch = directMatch;
   }

   @Override
   public String toString() {
      return recommendation + " | " + pattern + " | direct: " + directMatch;
   }

   public String getRecommendation() {
      return recommendation;
   }

   public SymbPattern getPattern() {
      return pattern;
   }

   public boolean isDirectMatch() {
      return directMatch;
   }
}
