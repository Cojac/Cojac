package com.github.cojac.unit.profiler;

import com.github.cojac.profiler.Recommendation;

@SuppressWarnings("unused")
public class NumericalProfilerTests {

   public static String COJAC_MAGIC_toString(double n) { return ""; }
   /*
      This method will be called by the "NumericalProfilerTest". It should print a recommendation to use Math.fma().
    */
   public double testFMA() {
      double a = 5.0, b = 3.0, c = 7.0;
      double res = a * b + c;
      return res;
   }

   public double testFMAReverse() {
      double a = 5.0, b = 3.0, c = 7.0;
      double res = c + a * b;
      return res;
   }

   public double testScalb() {
      double a = 7.0;
      int b = 6;
      double res = a * Math.pow(2, b);
      return res;
   }

   public double testScalbReverse() {
      double a = 7.0;
      int b = 6;
      double res = Math.pow(2, b) * a;
      return res;
   }

   public double testLog1p() {
      double x = 0.00000122122;
      double res = Math.log(1.0 + x);
      return res;
   }

   public double testLog1pReverse() {
      double x = 0.00000122122;
      double res = Math.log(x + 1.0);
      return res;
   }

   public double testExpm1() {
      double x = 1.23E-11;
      double res = Math.exp(x) - 1.0;
      return res;
   }

   public double testAbsMixed() {
      double sum = 0;
      for(int i = 0; i < 10; i++) {
         double r = 2.0 * (i%2 == 0 ? -1.0 : 1.0);
         if(Math.abs(r) >= 1.0) {
            sum += r;
         }
      }
      return sum;
   }

   public double testAbsOnlyPos() {
      double sum = 0;
      for(int i = 0; i < 10; i++) {
         double r = 2.0;
         if(Math.abs(r) >= 1.0) {
            sum += r;
         }
      }
      return sum;
   }

   public double testPow() {
      double sum = 0;
      sum += Math.pow(4, 2);
      sum += Math.pow(sum, 3);
      sum = Math.pow(sum, -1);
      return sum;
   }

   public double testHypFromSqrt() {
      double a = 3.0, b = 5.0;
      double c = Math.sqrt(Double.MAX_VALUE)*2, d = Math.sqrt(Double.MAX_VALUE);
      double res = Math.sqrt(a*a + b*b);
      double res2 = Math.sqrt(c*c + d*d);
      return res + res2;
   }

   public double testHypToSqrt() {
      double a = 3.0, b = 5.0;
      double c = Math.sqrt(Double.MAX_VALUE)*2, d = Math.sqrt(Double.MAX_VALUE);
      double res = Math.hypot(a, b);
      double res2 = Math.hypot(c, d);
      return res + res2;
   }

   public double testSinApprox() {
      double r = 0;
      for(int i = 0; i < Recommendation.SIN_THRESHOLDS.length; i++) {
         r += Math.sin(Recommendation.SIN_THRESHOLDS[i]);
      }
      double r2 = Math.sin(Math.PI);
      return r + r2;
   }

   public double testCosApprox() {
      double r = 0;
      for(int i = 0; i < Recommendation.COS_THRESHOLDS.length; i++) {
         r += Math.cos(Recommendation.COS_THRESHOLDS[i]);
      }
      double r2 = Math.cos(Math.PI);
      return r + r2;
   }

   public double testTanApprox() {
      double r = 0;
      for(int i = 0; i < Recommendation.TAN_THRESHOLDS.length; i++) {
         r += Math.tan(Recommendation.TAN_THRESHOLDS[i]);
      }
      double r2 = Math.tan(Math.PI);
      return r + r2;
   }
}
