package com.github.cojac.profiler;

import com.github.cojac.models.wrappers.SymbolicExpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Recommendation {

   // --- Settings ---
   // maximal absolute value of y when proposing a replacement for Math.pow(x, y)
   private static final int MAX_POW_REPLACEMENT = 5;
   // thresholds for trigonometric approximations
   // adding new ones is fine but keep them sorted, also if they are not power of 10 it will break the custom string
   public static final double[] SIN_THRESHOLDS = {
           0.85375, // 0.1
           0.39249, // 0.01
           0.18181, // 0.001
           0.08435, // 0.0001
   };
   public static final double[] COS_THRESHOLDS = {
           1.26124, // 0.1
           0.70281, // 0.01
           0.39410, // 0.001
           0.22142, // 0.0001
           0.12448, // 0.00001
           0.06999, // 0.000001
   };
   public static final double[] TAN_THRESHOLDS = {
           0.63165, // 0.1
           0.30677, // 0.01
           0.14382, // 0.001
           0.06690, // 0.0001
   };

   private static String trigoApproxError(ProfileData profile, final double[] source) {
      String r = "maximal error : ";
      double max = Math.max(Math.abs(profile.min), Math.abs(profile.max));
      for(int i = source.length-1; i >= 0; i--) {
         if(max <= source[i]) {
            String fmt = "0.%0" + (i+1) + "d";
            return r + String.format(fmt, 1);
         }
      }
      return "";
   }

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
              "Calculations like \"a * b + c\" can be replaced by a call to Math.fma(a, b, c) to increase precision " +
                      "by rounding the result only once.",
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
              "Calculations like \"a * Math.pow(2, b)\" can be replaced by a call to Math.scalb(a, b) to increase " +
                      "performances by performing the operation directly on the floating point representation.",
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
              "Calculations like \"Math.log(1.0 + x)\" can be replaced by a call to Math.log1p(x) to increase " +
                      "precision for small values of x.",
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
              "Calculations like \"exp(x) - 1.0\" can be replaced by a call to Math.expm1(x) to increase precision " +
                      "for small values of x.",
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
              "Calculations like \"Math.pow(x, y)\" where \"y\" is a constant integer should be replaced by their " +
                      "plain maths equivalent to increase performances.",
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
              (profile) -> profile.min == profile.max && profile.max == Math.rint(profile.max) && Math.abs(profile.max) <= MAX_POW_REPLACEMENT,
              (profile) -> {
                 StringBuilder r = new StringBuilder("Math.pow(x, " + profile.max + ") -> ");
                 if (profile.max == 0) {
                    return r + "1";
                 }
                 if (profile.max < 0) {
                    r.append("1.0/(");
                 }
                 for (int i = 0; i < Math.abs(profile.max); i++) {
                    r.append("x*");
                 }
                 // remove extra '*'
                 r.deleteCharAt(r.length() - 1);
                 if (profile.max < 0) {
                    r.append(")");
                 }
                 return r.toString();
              }
      );

      // PYTH_TO_HYPOT
      final SymbExpressionGoal pythToHypotGoal;
      PYTH_TO_HYPOT = new Recommendation(
              "Calculations like \"Math.sqrt(a*a + b*b)\" can be replaced by a call to Math.hypot(a, b) to avoid " +
                      "intermediate overflows at the price of a high performance impact.",
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
                    // Non-trivial because it would require the values from the multiplications
                    profile.overflow = true;
                 }
                 profile.update(pythToHypotGoal.getLeftValue());
              },
              (profile) -> profile.overflow
      );

      // HYPOT_TO_PYTH
      final SymbExpressionGoal hypotToPythGoal;
      HYPOT_TO_PYTH = new Recommendation(
              "Math.hypot(a, b) should not be used when the values do not overflow because of its high performance " +
                      "impact. Consider replacing the call by \"Math.sqrt(a*a + b*b)\".",
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
              "This Math.abs call always always receive values of the same sign and is therefore useless.",
              // abs(x)
              new SymbPattern(
                      goal = new SymbExpressionGoal(SymbolicExpression.OP.ABS)
              ),
              goal,
              (profile) -> profile.max <= 0 || profile.min >= 0,
              (profile) -> {
                 if (profile.max == 0 && profile.min == 0) {
                    return "Value is always \"0\"";
                 } else if (profile.max <= 0) {
                    return "Value is always negative";
                 } else {
                    return "Value is always positive";
                 }
              }

      );

      // SIN_APPROX
      SIN_APPROX = new Recommendation(
              "Value of sin(x) can be approximated with the following estimation : \"x\" for small values of x.",
              // sin(x)
              new SymbPattern(
                      goal = new SymbExpressionGoal(SymbolicExpression.OP.SIN)
              ),
              goal,
              (profile) -> Math.max(Math.abs(profile.min), Math.abs(profile.max)) <= SIN_THRESHOLDS[0],
              (profile) -> trigoApproxError(profile, SIN_THRESHOLDS)
      );

      // COS_APPROX
      COS_APPROX = new Recommendation(
              "Value of cos(x) can be approximated with the following estimation : \"1.0 - x*x/2\" for small values of x.",
              // cos(x)
              new SymbPattern(
                      goal = new SymbExpressionGoal(SymbolicExpression.OP.COS)
              ),
              goal,
              (profile) -> Math.max(Math.abs(profile.min), Math.abs(profile.max)) <= COS_THRESHOLDS[0],
              (profile) -> trigoApproxError(profile, COS_THRESHOLDS)
      );

      // TAN_APPROX
      TAN_APPROX = new Recommendation(
              "Value of tan(x) can be approximated with the following estimation : \"x\" for small values of x.",
              // tan(x)
              new SymbPattern(
                      goal = new SymbExpressionGoal(SymbolicExpression.OP.TAN)
              ),
              goal,
              (profile) -> Math.max(Math.abs(profile.min), Math.abs(profile.max)) <= TAN_THRESHOLDS[0],
              (profile) -> trigoApproxError(profile, TAN_THRESHOLDS)
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
   private final Function<ProfileData, String> customStringProvider;

   private Recommendation(String recommendation, SymbPattern pattern) {
      this.recommendation = recommendation;
      this.pattern = pattern;
      this.goal = null;
      this.profileUpdater = null;
      this.profileAnalyser = null;
      this.customStringProvider = null;
   }

   private Recommendation(String recommendation, SymbPattern pattern, SymbExpressionGoal goal,
                          Predicate<ProfileData> profileAnalyser) {
      this(recommendation, pattern, goal, null, profileAnalyser, null);
   }

   private Recommendation(String recommendation, SymbPattern pattern, SymbExpressionGoal goal,
                          Predicate<ProfileData> profileAnalyser, Function<ProfileData, String> customStringProvider) {
      this(recommendation, pattern, goal, null, profileAnalyser, customStringProvider);
   }

   private Recommendation(String recommendation, SymbPattern pattern, SymbExpressionGoal goal,
                          Consumer<ProfileData> profileUpdater, Predicate<ProfileData> profileAnalyser) {
      this(recommendation, pattern, goal, profileUpdater, profileAnalyser, null);
   }

   private Recommendation(String recommendation, SymbPattern pattern, SymbExpressionGoal goal,
                          Consumer<ProfileData> profileUpdater, Predicate<ProfileData> profileAnalyser,
                          Function<ProfileData, String> customStringProvider) {
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
      this.customStringProvider = customStringProvider;
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

   public boolean hasCustomStringProvider() {
      return this.customStringProvider != null;
   }

   public String getCustomString(ProfileData profileData) {
      if (customStringProvider == null) {
         throw new UnsupportedOperationException();
      }
      return this.customStringProvider.apply(profileData);
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
