/**
 *  Example adapted in Java from funcarc.f90 :
 *  http://crd.lbl.gov/~dhbailey/dhbpapers/numerical-bugs.tar.gz
 *  
 *  ----------------------------------------------------------------------------
 *  This demonstrates numerical error when attempting to find the arc length
 *  of an irregular function.
 *
 *  David H Bailey    2008-01-09
 *  ----------------------------------------------------------------------------
 */

package com.github.cojac.misctests.deltaDebugging;

import java.math.BigDecimal;

public class InstrumentationArc {

    public static double fun(double x) {
        double t1 = x;
        double d1 = 1.0f;
        for (int k = 1; k <= 5; k++) {
            d1 = 2.0f * d1;
            double tmp = Math.sin(d1 * x) / d1;
            t1 = t1 + tmp;
        }
        return t1;
    }

    public static void main(String[] args) {
        int n = 1000000;
        double s1 = 0.0f;
        double t1 = 0.0f;
        double t2;
        double dppi = Math.acos(-1.0);
        double h = dppi / n;

        for (int i = 1; i <= n; i++) {
            t2 = fun(i * h);
            double tmp = Math.sqrt(h * h + (t2 - t1) * (t2 - t1));
            s1 = s1 + tmp;
            t1 = t2;
        }
        System.out.println("s1 -> " + s1 + " should be 5.79577632241304 ");

        System.out.println(BigDecimal.valueOf(5.79577632241304).subtract(BigDecimal.valueOf(s1)).divide(BigDecimal.valueOf(5.79577632241304), 10, BigDecimal.ROUND_HALF_UP).abs());
        if (!isValid(s1, "5.79577632241304", "1e-4"))
            System.exit(-1);
        else
            System.exit(0);
    }

    public static boolean isValid(double res, String exp, String tol) {
        BigDecimal expB = new BigDecimal(exp);
        BigDecimal resB = new BigDecimal(res);
        BigDecimal tolB = new BigDecimal(tol);
        return (expB.subtract(resB).divide(expB, 16, BigDecimal.ROUND_HALF_UP).abs().compareTo(tolB) <= 0);
    }
}
//
//package demo;
//
//import java.math.BigDecimal;
//
//public class InstrumentationArc {
//
//    public static float fun(float x) {
//        double t1 = x;
//        float d1 = 1.0f;
//        for (int k = 1; k <= 5; k++) {
//            d1 = 2.0f * d1;
//            double tmp = Math.sin(d1 * x) / d1;
//            t1 = t1 + tmp;
//        }
//        return (float)t1;
//    }
//
//    public static void main(String[] args) {
//        int n = 1000000;
//        double s1 = 0.0f;
//        float t1 = 0.0f;
//        float t2;
//        float dppi = (float)Math.acos(-1.0);
//        float h = dppi / n;
//
//        for (int i = 1; i <= n; i++) {
//            t2 = fun(i * h);
//            double tmp = Math.sqrt(h * h + (t2 - t1) * (t2 - t1));
//            s1 = s1 + tmp;
//            t1 = t2;
//        }
//        System.out.println("s1 -> " + s1 + " should be 5.79577632241304 ");
//
//        System.out.println(BigDecimal.valueOf(5.79577632241304).subtract(BigDecimal.valueOf(s1)).divide(BigDecimal.valueOf(5.79577632241304), 10, BigDecimal.ROUND_HALF_UP).abs());
//        if (!isValid(s1, "5.79577632241304", "1e-4"))
//            System.exit(-1);
//        else
//            System.exit(0);
//    }
//
//    public static boolean isValid(double res, String exp, String tol) {
//        BigDecimal expB = new BigDecimal(exp);
//        BigDecimal resB = new BigDecimal(res);
//        BigDecimal tolB = new BigDecimal(tol);
//        return (expB.subtract(resB).divide(expB, 16, BigDecimal.ROUND_HALF_UP).abs().compareTo(tolB) <= 0);
//    }
//}