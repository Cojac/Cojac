package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Recommendation {

   // https://stackoverflow.com/a/9090575/6882070
   private static boolean almostEqual(double a, double b, double eps) {
      return Math.abs(a - b) < eps;
   }

   public static final List<Recommendation> recommandations;

   static {
      // used to temporarily store the new SymbExpressionsGoal to pass it to the recommandation constructor
      // some recommendations have a dedicated temporary goal storage when it is required to use it in a lambda
      // (because it needs to be effectively final)
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

      // --- PROFILED PATTERNS ---
      // LOG1P
      recommandations.add(new Recommendation(
              "log1p",
              // log(1 + x)
              new SymbPattern(
                      new SymbolicExpression(SymbolicExpression.OP.LOG,
                              new SymbolicExpression(SymbolicExpression.OP.ADD,
                                      new SymbolicExpression(1.0),
                                      goal = new SymbExpressionGoal(SymbolicExpression.OP.ANY)
                              ),
                              null
                      )
              ),
              goal,
              (profile) -> almostEqual(profile.mean, 0.0, 1.0E-05) && profile.std < 1.0E-05
      ));

      // EXPM1
      recommandations.add(new Recommendation(
              "expm1",
              // exp(x) - 1
              new SymbPattern(
                      new SymbolicExpression(SymbolicExpression.OP.SUB,
                              goal = new SymbExpressionGoal(SymbolicExpression.OP.EXP),
                              new SymbolicExpression(1.0)
                      )
              ),
              goal,
              (profile) -> almostEqual(profile.mean, 0.0, 1.0E-05) && profile.std < 1.0E-05
      ));

      // INTPOW
      final SymbExpressionGoal intPowGoal;
      recommandations.add(new Recommendation(
              "pow of int",
              // pow(a,x)
              new SymbPattern(
                      intPowGoal = new SymbExpressionGoal(SymbolicExpression.OP.POW)
                      // no validator even though we want x to always be an integer
                      // because we need to instrument its value further down the process
              ),
              intPowGoal,
              (profile) -> {
                 // need to instrument right value to monitor the exponent
                 profile.update(intPowGoal.getRightValue());
              },
              (profile) -> profile.min == profile.max && profile.max == Math.rint(profile.max)
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
                 // need to check if the multiplication produced an overflow
                 if (Double.isInfinite(pythToHypotGoal.getLeftValue())) {
                    // TODO maybe check that using hypot would not have produced an overflow
                    profile.overflow = true;
                 }
                 profile.update(pythToHypotGoal.getLeftValue());
              },
              (profile) -> profile.overflow
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
                 // need to check if the execution using simple maths would produce an overflow
                 double left = hypotToPythGoal.getLeftValue();
                 double right = hypotToPythGoal.getRightValue();
                 double sum = left * left + right * right;
                 if (Double.isInfinite(sum)) {
                    profile.overflow = true;
                 }
                 profile.update(sum);

                 // also update children to gain some insights on the values
                 profile.updateChildren(left, right);
              },
              (profile) -> !profile.overflow
      ));

      // USELESS_ABS
      recommandations.add(new Recommendation(
              "useless abs call",
              // abs(x)
              new SymbPattern(
                      goal = new SymbExpressionGoal(SymbolicExpression.OP.ABS)
              ),
              goal,
              (profile) -> profile.max <= 0 || profile.min >= 0
      ));

      // SIN_APPROX
      recommandations.add(new Recommendation(
              "sin can be approximated",
              // sin(x)
              new SymbPattern(
                      goal = new SymbExpressionGoal(SymbolicExpression.OP.SIN)
              ),
              goal,
              (profile) -> {
                 // TODO
                 return false;
              }
      ));

      // COS_APPROX
      recommandations.add(new Recommendation(
              "cos can be approximated",
              // cos(x)
              new SymbPattern(
                      goal = new SymbExpressionGoal(SymbolicExpression.OP.COS)
              ),
              goal,
              (profile) -> {
                 // TODO
                 return false;
              }
      ));

      // TAN_APPROX
      recommandations.add(new Recommendation(
              "tan can be approximated",
              // tan(x)
              new SymbPattern(
                      new SymbExpressionGoal(SymbolicExpression.OP.TAN)
              ),
              goal,
              (profile) -> {
                 // TODO
                 return false;
              }
      ));
   }

   private final String recommendation;
   private final SymbPattern pattern;
   private final SymbExpressionGoal goal;
   private final Consumer<ProfileData> profileUpdater;
   private final Function<ProfileData, Boolean> profileAnalyser;

   private Recommendation(String recommendation, SymbPattern pattern) {
      this.recommendation = recommendation;
      this.pattern = pattern;
      this.goal = null;
      this.profileUpdater = null;
      this.profileAnalyser = null;
   }

   private Recommendation(String recommendation, SymbPattern pattern, SymbExpressionGoal goal, Function<ProfileData,
           Boolean> profileAnalyser) {
      this(recommendation, pattern, goal, null, profileAnalyser);
   }

   private Recommendation(String recommendation, SymbPattern pattern, SymbExpressionGoal goal,
                          Consumer<ProfileData> profileUpdater, Function<ProfileData, Boolean> profileAnalyser) {
      this.recommendation = recommendation;

      this.goal = goal;
      this.pattern = pattern;
      if (profileUpdater != null) {
         this.profileUpdater = profileUpdater;
      } else {
         // default updater
         this.profileUpdater = x -> x.update(this.goal.getLeftValue());
      }
      this.profileAnalyser = profileAnalyser;
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

   public boolean isRelevant(ProfileData profile) {
      return profileAnalyser.apply(profile);
   }
}
