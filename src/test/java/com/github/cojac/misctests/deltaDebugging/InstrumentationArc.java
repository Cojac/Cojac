/*
 *    Copyright 2017 Frédéric Bapst et al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
        if (!isValid(s1, "5.79577632241304", "1e-4")) {
            System.out.println("too inaccurate...");
            System.exit(-1);
        } else { 
            System.out.println("accurate enough...");
            System.exit(0);
        }
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