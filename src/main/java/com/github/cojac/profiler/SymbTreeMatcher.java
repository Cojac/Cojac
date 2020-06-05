package com.github.cojac.profiler;

import com.github.cojac.models.Reactions;
import com.github.cojac.models.wrappers.SymbolicExpression;

import java.util.function.Function;

public class SymbTreeMatcher {

   private static class SymbPattern {

      private final SymbolicExpression expr;
      private final Function<SymbolicExpression, Boolean> validator;

      SymbPattern(SymbolicExpression expr) {
         this.expr = expr;
         this.validator = null;
      }

      SymbPattern(SymbolicExpression expr, Function<SymbolicExpression, Boolean> validator) {
         this.expr = expr;
         this.validator = validator;
      }

      private static boolean match(SymbolicExpression pattern, SymbolicExpression candidate) {
         //System.out.println(pattern + " | " + candidate);
         if (pattern == null) {
            // returns true if both are null (handle empty right tree case)
            return candidate == null;
         }
         if (pattern.oper == SymbolicExpression.OP.ANY) {
            return true;
         }
         if (candidate == null) {
            // candidate is null, but pattern is not ANY --> fails
            return false;
         }
         if (pattern.oper != candidate.oper) {
            return false;
         }
         if (pattern.oper == SymbolicExpression.OP.NOP) {
            return pattern.value == candidate.value;
         }
         return match(pattern.left, candidate.left) && match(pattern.right, candidate.right);
      }

      boolean match(SymbolicExpression other) {
         boolean r = match(expr, other);
         if (r && this.validator != null) {
            return this.validator.apply(other);
         } else {
            return r;
         }
      }

      @Override
      public String toString() {
         return "PATTERN : " + expr.toString();
      }
   }

   private static final SymbPattern[] patterns = {
           // a * b + c
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.ADD,
                           new SymbolicExpression(SymbolicExpression.OP.MUL),
                           new SymbolicExpression(SymbolicExpression.OP.ANY)
                   )
           ),
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
           // exp(x) - 1
           new SymbPattern(
                   new SymbolicExpression(SymbolicExpression.OP.SUB,
                           new SymbolicExpression(SymbolicExpression.OP.EXP,
                                   new SymbolicExpression(SymbolicExpression.OP.ANY),
                                   null
                           ),
                           new SymbolicExpression(1.0)
                   )
           ),
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
           )

   };

   private boolean active = false;

   public SymbTreeMatcher() {

   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public boolean isActive() {
      return this.active;
   }

   public void match(SymbolicExpression expr) {
      for (SymbPattern pattern : patterns) {
         if (pattern.match(expr)) {
            Reactions.react("Match for " + pattern + " for EXPR : " + expr);
         }
      }
   }
}
