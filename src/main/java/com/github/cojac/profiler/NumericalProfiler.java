package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

public class NumericalProfiler {

   public void handle(SymbolicExpression expr) {
      for (Recommendation r : Recommendation.values()) {
         if (r.getPattern().match(expr)) {
            if (r.isDirectMatch()) {
               handleDirectMatch(r, expr);
            } else {
               handleProfiledMatch(r, expr);
            }
         }
      }
   }

   private void handleDirectMatch(Recommendation r, SymbolicExpression expr) {
      System.out.println("Direct match : " + expr + " for recommendation " + r);
   }

   private void handleProfiledMatch(Recommendation r, SymbolicExpression expr) {
      System.out.println("Indirect match : " + expr + " for recommendation " + r);
   }
}
