/**
 *  Example adapted in Java from confrac.f90 :
 *  http://crd.lbl.gov/~dhbailey/dhbpapers/numerical-bugs.tar.gz
 *  
 *  ----------------------------------------------------------------------------
 *  This computes the continued fraction expansion of a mp_real value, producing
 *  a unimodular matrix that has successively accurage rational approximations.
 *  
 *  David H Bailey    2008-01-04
 *  ----------------------------------------------------------------------------
 */
package demo;

import java.math.BigDecimal;

public class ConFrac {

    public static void main(String[] args) {

        int i, k, n;
        n = 50;
        double eps = 1e-9, t1;
        double x = 0.571428571428571428571;
        double[] a = {1, x};
        double[][] b = {{1, 0}, {0, 1}};
        double numerator = 0;
        double denominator = 0;

        for (k = 1; k <= n; k++) {
            if (k % 2 == 1) {
                t1 = (int) (a[1] / a[0]);
                a[1] = a[1] - t1 * a[0];

                for (i = 0; i <= 1; i++)
                    b[i][0] = b[i][0] + t1 * b[i][1];

            } else {
                t1 = (int) (a[0] / a[1]);
                a[0] = a[0] - t1 * a[1];

                for (i = 0; i <= 1; i++)
                    b[i][1] = b[i][1] + t1 * b[i][0];
            }

            if (a[0] < eps || a[1] < eps) {
                if (k % 2 == 1) {
                    numerator = b[1][0];
                    denominator = b[0][0];
                } else {
                    numerator = b[1][1];
                    denominator = b[0][1];
                }
                break;
            }
        }

        // --------------------------------------------------
        // Validation
        // --------------------------------------------------
        System.out.println(numerator + " / " + denominator);
        if (!(new BigDecimal(numerator).equals(new BigDecimal("4.0")) &&
                new BigDecimal(denominator).equals(new BigDecimal("7.0")))) {
            System.out.println("fail");
            System.exit(-1);
        } else {
            System.out.println("success");
            System.exit(0);
        }
    }
}
//
///**
// *  Example adapted in Java from confrac.f90 :
// *  http://crd.lbl.gov/~dhbailey/dhbpapers/numerical-bugs.tar.gz
// *  
// *  ----------------------------------------------------------------------------
// *  This computes the continued fraction expansion of a mp_real value, producing
// *  a unimodular matrix that has successively accurage rational approximations.
// *  
// *  David H Bailey    2008-01-04
// *  ----------------------------------------------------------------------------
// */
//package demo;
//
//import java.math.BigDecimal;
//
//public class ConFrac {
//
//    public static void main(String[] args) {
//
//        int i, k, n;
//        n = 50;
//        float eps = 1e-9f, t1;
//        double x = 0.571428571428571428571;
//        double[] a = {1, x};
//        float[][] b = {{1, 0}, {0, 1}};
//        float numerator = 0;
//        float denominator = 0;
//
//        for (k = 1; k <= n; k++) {
//            if (k % 2 == 1) {
//                t1 = (int) (a[1] / a[0]);
//                a[1] = a[1] - t1 * a[0];
//
//                for (i = 0; i <= 1; i++)
//                    b[i][0] = b[i][0] + t1 * b[i][1];
//
//            } else {
//                t1 = (int) (a[0] / a[1]);
//                a[0] = a[0] - t1 * a[1];
//
//                for (i = 0; i <= 1; i++)
//                    b[i][1] = b[i][1] + t1 * b[i][0];
//            }
//
//            if (a[0] < eps || a[1] < eps) {
//                if (k % 2 == 1) {
//                    numerator = b[1][0];
//                    denominator = b[0][0];
//                } else {
//                    numerator = b[1][1];
//                    denominator = b[0][1];
//                }
//                break;
//            }
//        }
//
//        // --------------------------------------------------
//        // Validation
//        // --------------------------------------------------
//        System.out.println(numerator + " / " + denominator);
//        if (!(new BigDecimal(numerator).equals(new BigDecimal("4.0")) &&
//                new BigDecimal(denominator).equals(new BigDecimal("7.0")))) {
//            System.out.println("fail");
//            System.exit(-1);
//        } else {
//            System.out.println("success");
//            System.exit(0);
//        }
//    }
//}
