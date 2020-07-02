package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Recommendation {

   // https://stackoverflow.com/a/9090575/6882070
   private static boolean almostEqual(double a, double b, double eps) {
      return Math.abs(a - b) < eps;
   }

   public static final List<Recommendation> recommandations;

   // All the recommendations, listed to be used by unit tests
   public final static Recommendation FMA;
   public final static Recommendation SCALB;
   public final static Recommendation LOG1P;
   public final static Recommendation EXPM1;
   public final static Recommendation INTPOW;
   public final static Recommendation PYTH_TO_HYPOT;
   public final static Recommendation HYPOT_TO_PYTH;
   public final static Recommendation USELESS_ABS;
   public final static Recommendation SIN_APPROX;
   public final static Recommendation COS_APPROX;
   public final static Recommendation TAN_APPROX;


   static {
      // used to temporarily store the new SymbExpressionsGoal to pass it to the recommandation constructor
      // some recommendations have a dedicated temporary goal storage when it is required to use it in a lambda
      // (because it needs to be effectively final)
      SymbExpressionGoal goal;

      recommandations = new ArrayList<>();

      // --- DIRECT PATTERNS ---
      // FMA
      FMA = new Recommendation(
              "FMA",
              // a * b + c
              new SymbPattern(
                      new SymbolicExpression(SymbolicExpression.OP.ADD,
                              new SymbolicExpression(SymbolicExpression.OP.MUL),
                              new SymbolicExpression(SymbolicExpression.OP.ANY)
                      )
              )
      );

      // SCALB
      SCALB = new Recommendation(
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
      );

      // --- PROFILED PATTERNS ---
      // LOG1P
      LOG1P = new Recommendation(
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
      );

      // EXPM1
      EXPM1 = new Recommendation(
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
      );

      // INTPOW
      final SymbExpressionGoal intPowGoal;
      INTPOW = new Recommendation(
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
      );

      // PYTH_TO_HYPOT
      final SymbExpressionGoal pythToHypotGoal;
      PYTH_TO_HYPOT = new Recommendation(
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
      );

      // HYPOT_TO_PYTH
      final SymbExpressionGoal hypotToPythGoal;
      HYPOT_TO_PYTH = new Recommendation(
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
      );

      // USELESS_ABS
      USELESS_ABS = new Recommendation(
              "useless abs call",
              // abs(x)
              new SymbPattern(
                      goal = new SymbExpressionGoal(SymbolicExpression.OP.ABS)
              ),
              goal,
              (profile) -> profile.max <= 0 || profile.min >= 0
      );

      // SIN_APPROX
      SIN_APPROX = new Recommendation(
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
      );

      // COS_APPROX
      COS_APPROX = new Recommendation(
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
      );

      // TAN_APPROX
      TAN_APPROX = new Recommendation(
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
      );


      Collections.addAll(recommandations,
              FMA, SCALB, LOG1P, EXPM1, INTPOW, PYTH_TO_HYPOT, HYPOT_TO_PYTH, USELESS_ABS, SIN_APPROX, COS_APPROX,
              TAN_APPROX);
   }

   private final String recommendation;
   private final SymbPattern pattern;
   private final SymbExpressionGoal goal;
   private final Consumer<ProfileData> profileUpdater;
   private final Predicate<ProfileData> profileAnalyser;

   private Recommendation(String recommendation, SymbPattern pattern) {
      this.recommendation = recommendation;
      this.pattern = pattern;
      this.goal = null;
      this.profileUpdater = null;
      this.profileAnalyser = null;
   }

   private Recommendation(String recommendation, SymbPattern pattern, SymbExpressionGoal goal,
                          Predicate<ProfileData> profileAnalyser) {
      this(recommendation, pattern, goal, null, profileAnalyser);
   }

   private Recommendation(String recommendation, SymbPattern pattern, SymbExpressionGoal goal,
                          Consumer<ProfileData> profileUpdater, Predicate<ProfileData> profileAnalyser) {
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
      return profileAnalyser.test(profile);
   }
}
