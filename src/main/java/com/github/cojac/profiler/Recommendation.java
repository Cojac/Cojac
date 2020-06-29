package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Recommendation {

   public static final List<Recommendation> recommandations;

   static {
      SymbExpressionGoal goal;
      recommandations = new ArrayList<>();

      // --- DIRECT PATTERNS ---
      // FMA
      recommandations.add(new Recommendation(
              "FMA",
              // a * b + c
              new SymbPattern(
                      new SymbolicExpression(SymbolicExpression.OP.ADD,
                              new SymbolicExpression(SymbolicExpression.OP.MUL),
                              new SymbolicExpression(SymbolicExpression.OP.ANY)
                      )
              )
      ));

      // SCALB
      recommandations.add(new Recommendation(
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
              )
      ));

      // LOG1P
      recommandations.add(new Recommendation(
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
              )
      ));

      // EXPM1
      recommandations.add(new Recommendation(
              "expm1",
              // exp(x) - 1
              new SymbPattern(
                      new SymbolicExpression(SymbolicExpression.OP.SUB,
                              new SymbolicExpression(SymbolicExpression.OP.EXP),
                              new SymbolicExpression(1.0)
                      )
              )
      ));

      // --- PROFILED PATTERNS ---
      // INTPOW
      recommandations.add(new Recommendation(
              "pow of int",
              // pow(a,x)
              new SymbPattern(
                      goal = new SymbExpressionGoal(SymbolicExpression.OP.POW)
                      // no validator even though we want x to always be an integer
                      // because we need to instrument its value further down the process
              ),
              goal
      ));

      // PYTH_TO_HYPOT
      final SymbExpressionGoal pythToHypotGoal;
      recommandations.add(new Recommendation(
              "hypot instead of sqrt",
              // sqrt(a*a + b*b)
              new SymbPattern(
                      pythToHypotGoal = new SymbExpressionGoal(SymbolicExpression.OP.SQRT,
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
              pythToHypotGoal,
              (profile) -> {
                 if(Double.isInfinite(pythToHypotGoal.getLeftValue())) {
                    profile.overflow = true;
                 }
                 profile.update(pythToHypotGoal.getLeftValue());
              }
      ));

      // HYPOT_TO_PYTH
      final SymbExpressionGoal hypotToPythGoal;
      recommandations.add(new Recommendation(
              "sqrt instead of hypot",
              // hypot(a, b)
              new SymbPattern(
                      hypotToPythGoal = new SymbExpressionGoal(SymbolicExpression.OP.HYPOT)
              ),
              hypotToPythGoal,
              (profile) -> {
                  double left = hypotToPythGoal.getLeftValue();
                  double right = hypotToPythGoal.getRightValue();
                  double sum = left*left + right*right;
                  if(Double.isInfinite(sum)) {
                     profile.overflow = true;
                  }
                  profile.update(sum);
                  profile.updateChildren(left, right);
              }
      ));

      // USELESS_ABS
      recommandations.add(new Recommendation(
              "useless abs call",
              // abs(x)
              new SymbPattern(
                      goal = new SymbExpressionGoal(SymbolicExpression.OP.ABS)
              ),
              goal
      ));

      // SIN_APPROX
      recommandations.add(new Recommendation(
              "sin can be approximated",
              // sin(x)
              new SymbPattern(
                      new SymbolicExpression(SymbolicExpression.OP.SIN)
              )
      ));

      // COS_APPROX
      recommandations.add(new Recommendation(
              "cos can be approximated",
              // cos(x)
              new SymbPattern(
                      new SymbolicExpression(SymbolicExpression.OP.COS)
              )
      ));

      // TAN_APPROX
      recommandations.add(new Recommendation(
              "tan can be approximated",
              // tan(x)
              new SymbPattern(
                      new SymbolicExpression(SymbolicExpression.OP.TAN)
              )
      ));
   }

   private final String recommendation;
   private final SymbPattern pattern;
   private final SymbExpressionGoal goal;
   private final Consumer<ProfileData> profileUpdater;

   private Recommendation(String recommendation, SymbPattern pattern) {
      this.recommendation = recommendation;
      this.pattern = pattern;
      this.goal = null;
      this.profileUpdater = null;
   }

   private Recommendation(String recommendation, SymbPattern pattern, SymbExpressionGoal goal) {
      this(recommendation, pattern, goal, null);
   }

   private Recommendation(String recommendation, SymbPattern pattern, SymbExpressionGoal goal,
                  Consumer<ProfileData> profileUpdater) {
      this.recommendation = recommendation;

      this.goal = goal;
      this.pattern = pattern;
      if (profileUpdater != null) {
         this.profileUpdater = profileUpdater;
      } else {
         // default updater
         this.profileUpdater = x -> x.update(this.goal.getLeftValue());
      }
   }

   @Override
   public String toString() {
      return recommendation + " | " + pattern + " | direct: " + isDirectMatch();
   }

   public String getRecommendation() {
      return recommendation;
   }

   public SymbPattern getPattern() {
      return pattern;
   }

   public boolean isDirectMatch() {
      return goal == null;
   }

   public void updateTracking(ProfileData profileData) {
      if (profileUpdater == null) {
         throw new UnsupportedOperationException();
      }
      profileUpdater.accept(profileData);
   }
}
