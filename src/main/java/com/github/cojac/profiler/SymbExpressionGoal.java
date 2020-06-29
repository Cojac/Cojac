package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

public class SymbExpressionGoal extends SymbolicExpression {

   private double leftValue;
   private double rightValue;

   public SymbExpressionGoal() {
      super();
   }

   public SymbExpressionGoal(double value) {
      super(value);
   }

   public SymbExpressionGoal(OP oper) {
      super(oper);
   }

   public SymbExpressionGoal(OP oper, SymbolicExpression left, SymbolicExpression right) {
      super(oper, left, right);
   }

   void saveValues(double leftValue, double rightValue) {
      this.leftValue = leftValue;
      this.rightValue = rightValue;
   }

   double getRightValue() {
      return this.rightValue;
   }

   double getLeftValue() {
      return this.leftValue;
   }

}
