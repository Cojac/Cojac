package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

import java.util.function.Function;

public class SymbPattern {

   private final SymbolicExpression expr;
   private final Function<SymbolicExpression, Boolean> validator;

   SymbPattern(SymbolicExpression expr) {
      this(expr, null);
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
