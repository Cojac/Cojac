package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

import java.util.function.Predicate;

public class SymbPattern {

   private final SymbolicExpression expr;
   private final Predicate<SymbolicExpression> validator;

   SymbPattern(SymbolicExpression expr) {
      this(expr, null);
   }

   SymbPattern(SymbolicExpression expr, Predicate<SymbolicExpression> validator) {
      this.expr = expr;
      this.validator = validator;
   }

   private void saveCandidateValue(SymbolicExpression pattern, SymbolicExpression candidate, SymbExpressionGoal goal) {
      if (pattern instanceof SymbExpressionGoal) {
         //System.out.println("Saving candidate : " + pattern + " | " + candidate);
         if (candidate != null) {
            SymbExpressionGoal patternGoal = (SymbExpressionGoal) pattern;
            double left = Double.NaN, right = Double.NaN;
            if (candidate.left != null) {
               left = candidate.left.evaluate();
            }
            if (candidate.right != null) {
               right = candidate.right.evaluate();
            }
            if (candidate.left == null && candidate.right == null || patternGoal.oper == SymbolicExpression.OP.ANY) {
               // candidate contains a value, setting it to this goal left value
               // TODO check that this is a reasonable thing to do
               left = candidate.evaluate();
            }
            goal.saveValues(left, right);
         } else {
            // this should never happen because the goal should not match against a null value
            throw new IllegalStateException();
         }
      }
   }

   private boolean match(SymbolicExpression pattern, SymbolicExpression candidate, SymbExpressionGoal goal) {
      //System.out.println(pattern + " | " + candidate);
      if (pattern == null) {
         // returns true if both are null (handle empty right tree case)
         return candidate == null;
      }
      if (pattern.oper == SymbolicExpression.OP.ANY) {
         saveCandidateValue(pattern, candidate, goal);
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

      if (match(pattern.left, candidate.left, goal) && match(pattern.right, candidate.right, goal)) {
         saveCandidateValue(pattern, candidate, goal);
         return true;
      } else if (pattern.oper.isCommutative()) {
         boolean r = match(pattern.left, candidate.right, goal) && match(pattern.right, candidate.left, goal);
         if (r) {
            saveCandidateValue(pattern, candidate, goal);
         }
         return r;
      }

      return false;
   }

   SymbExpressionGoal match(SymbolicExpression other) {
      SymbExpressionGoal goal = new SymbExpressionGoal();
      boolean r = match(expr, other, goal);
      if (r) {
         if (this.validator == null || this.validator.test(other)) {
            return goal;
         }
      }
      return null;
   }

   @Override
   public String toString() {
      return "PATTERN : " + expr.toString();
   }
}
