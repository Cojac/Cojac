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

package com.github.cojac.misctests.deltaDebugging;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *  Example adapted in Java from simpsons.f90 :
 *  http://crd.lbl.gov/~dhbailey/dhbpapers/numerical-bugs.tar.gz
 *  
 *  ----------------------------------------------------------------------------
 *  This demonstrates a potential numerical inaccuracy in the context of
 *  using Simpson's rule to compute the value of a definite integral.
 *
 *  David H Bailey    2008-01-09
 *  ----------------------------------------------------------------------------
 */

public class Simpsons {
    public static double pi2;

    public static void main(String[] args) {

        int k, n = 1000000;
        double a, b, h, x;
        double s1;

        a = 0.0;
        b = 1.0;
        pi2 = 0.5 * Math.acos(-1);
        h = (b - a) / (2.0 * n);

        x = a;
        s1 = fun(x);

        for (k = 1; k <= n; k++) {
            x = a + (2 * k - 1) * h;
            double tmp = 4 * fun(x);
            s1 = s1 + tmp;

            if (k == n)
                break;
            x = x + h;
            tmp = 2 * fun(x);
            s1 = s1 + tmp;
        }

        x = x + h;
        double tmp = fun(x);
        s1 = s1 + tmp;
        s1 = h * s1 / 3.0;
        System.out.println("s1->" + s1 + " sould be 0.636619772367581");

        System.out.println(BigDecimal
                .valueOf(0.636619772367581)
                .subtract(BigDecimal.valueOf(s1))
                .divide(BigDecimal.valueOf(0.636619772367581), 16, RoundingMode.HALF_UP).abs());

        if (!isValid(s1, "0.636619772367581", "1e-6")) {
            System.out.println("Simpsons too inaccurate");
            System.exit(-1);
        } else {
            System.out.println("Simpsons accurate enough");
            System.exit(0);
        }

    }

    public static double fun(double x) {
        return Math.sin(pi2 * x);
    }

    public static boolean isValid(double res, String exp, String tol) {
        BigDecimal expB = new BigDecimal(exp);
        BigDecimal resB = new BigDecimal(res);
        BigDecimal tolB = new BigDecimal(tol);
        return (expB.subtract(resB).divide(expB, 16, RoundingMode.HALF_UP).abs().compareTo(tolB) <= 0);
    }
}

/// **
// * Example adapted in Java from simpsons.f90 :
// * http://crd.lbl.gov/~dhbailey/dhbpapers/numerical-bugs.tar.gz
// *
// *
/// ----------------------------------------------------------------------------
// * This demonstrates a potential numerical inaccuracy in the context of
// * using Simpson's rule to compute the value of a definite integral.
// *
// * David H Bailey 2008-01-09
// *
/// ----------------------------------------------------------------------------
// */
// package demo;
//
// import java.math.BigDecimal;
//
// public class Simpsons {
// public static float pi2;
//
// public static void main(String[] args) {
// long s = System.currentTimeMillis();
// for (int i = 0; i < 1000; i++) {
// int k, n = 1000000;
// float a, b, h, x;
// double s1;
//
// a = 0.0f;
// b = 1.0f;
// pi2 = 0.5f * (float) Math.acos(-1);
// h = (b - a) / (2.0f * n);
//
// x = a;
// s1 = fun(x);
//
// for (k = 1; k <= n; k++) {
// x = a + (2 * k - 1) * h;
// float tmp = 4 * fun(x);
// s1 = s1 + tmp;
//
// if (k == n)
// break;
// x = x + h;
// tmp = 2 * fun(x);
// s1 = s1 + tmp;
// }
//
// x = x + h;
// float tmp = fun(x);
// s1 = s1 + tmp;
// s1 = h * s1 / 3.0;
//// System.out.println("s1->" + s1 + " sould be 0.636619772367581");
////
//// System.out.println(BigDecimal.valueOf(0.636619772367581).subtract(BigDecimal.valueOf(s1)).divide(BigDecimal.valueOf(0.636619772367581),
/// 16, BigDecimal.ROUND_HALF_UP).abs());
////
//// if (!isValid(s1, "0.636619772367581", "1e-6")) {
//// System.out.println("Failed");
//// System.exit(-1);
//// } else {
//// System.exit(0);
//// }
// }
// long e = System.currentTimeMillis();
// System.out.println(e - s);
// }
//
// public static float fun(float x) {
// return (float) Math.sin(pi2 * x);
// }
//
// public static boolean isValid(double res, String exp, String tol) {
// BigDecimal expB = new BigDecimal(exp);
// BigDecimal resB = new BigDecimal(res);
// BigDecimal tolB = new BigDecimal(tol);
// return (expB.subtract(resB).divide(expB, 16,
/// BigDecimal.ROUND_HALF_UP).abs().compareTo(tolB) <= 0);
// }
// }
