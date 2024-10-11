/*
 * *
 *    Copyright 2011 Baptiste Wicht & Frédéric Bapst
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
 *
 */

package com.github.cojac.perfs;

public class LinpackRunnable implements Runnable {
    @Override
    public void run() {
        double mflops_result = 0.0;
        double residn_result = 0.0;
        double time_result = 0.0;
        double eps_result = 0.0;

        double a[][] = new double[500][501];
        double b[] = new double[500];
        double x[] = new double[500];
        double ops, total, norma, normx;
        double resid, time;
        int n, i;
        int ipvt[] = new int[500];

        n = 500;

        ops = (2.0e0 * (n * n * n)) / 3.0 + 2.0 * (n * n);

        norma = matgen(a, n, b);
        time = second();

        dgefa(a, n, ipvt);
        dgesl(a, n, ipvt, b, 0);
        total = second() - time;

        for (i = 0; i < n; i++) {
            x[i] = b[i];
        }
        norma = matgen(a, n, b);
        for (i = 0; i < n; i++) {
            b[i] = -b[i];
        }
        dmxpy(n, b, n, x, a);
        resid = 0.0;
        normx = 0.0;
        for (i = 0; i < n; i++) {
            resid = (resid > abs(b[i])) ? resid : abs(b[i]);
            normx = (normx > abs(x[i])) ? normx : abs(x[i]);
        }

        eps_result = epslon(1.0);

        residn_result = resid / (n * norma * normx * eps_result);
        residn_result += 0.005; // for rounding
        residn_result = (int) (residn_result * 100);
        residn_result /= 100;

        time_result = total;
        time_result += 0.005; // for rounding
        time_result = (int) (time_result * 100);
        time_result /= 100;

        mflops_result = ops / (1.0e6 * total);
        mflops_result += 0.0005; // for rounding
        mflops_result = (int) (mflops_result * 1000);
        mflops_result /= 1000;
    }

    final double abs(final double d) {
        return (d >= 0) ? d : -d;
    }

    double second_orig = -1;

    final double second() {
        if (second_orig == -1) {
            second_orig = System.currentTimeMillis();
        }
        return (System.currentTimeMillis() - second_orig) / 1000;
    }

    final double matgen(double a[][], final int n, double b[]) {
        double norma;
        int init, i, j;

        init = 1325;
        norma = 0.0;
        /*
         * Next two for() statements switched. Solver wants matrix in column
         * order. --dmd 3/3/97
         */
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                init = 3125 * init % 65536;
                a[j][i] = (init - 32768.0) / 16384.0;
                norma = (a[j][i] > norma) ? a[j][i] : norma;
            }
        }
        for (i = 0; i < n; i++) {
            b[i] = 0.0;
        }
        for (j = 0; j < n; j++) {
            for (i = 0; i < n; i++) {
                b[i] += a[j][i];
            }
        }
        return norma;
    }

    /*
     * dgefa factors a double precision matrix by gaussian elimination.
     *
     * dgefa is usually called by dgeco, but it can be called directly with a
     * saving in time if rcond is not needed. (time for dgefa) .
     *
     * on entry
     *
     * a double precision[n][lda] the matrix to be factored.
     *
     * lda integer the leading dimension of the array a .
     *
     * n integer the order of the matrix a .
     *
     * on return
     *
     * a an upper triangular matrix and the multipliers which were used to
     * obtain it. u where l is a product of permutation and unit lower
     * triangular matrices and u is upper triangular.
     *
     * ipvt integer[n] an integer vector of pivot indices.
     *
     * info integer = 0 normal value. = k if u[k][k] .eq. 0.0 . this is not an
     * error condition for this subroutine, but it does indicate that dgesl or
     * dgedi will divide by zero if called. use rcond in dgeco for a reliable
     * indication of singularity.
     *
     * linpack. this version dated 08/14/78. cleve moler, university of new
     * mexico, argonne national lab.
     *
     * functions
     *
     * blas daxpy,dscal,idamax
     */

    final int dgefa(final double a[][], final int n, int ipvt[]) {
        double[] col_k, col_j;
        double t;
        int j, k, kp1, l, nm1;
        int info;

        // gaussian elimination with partial pivoting

        info = 0;
        nm1 = n - 1;
        if (nm1 >= 0) {
            for (k = 0; k < nm1; k++) {
                col_k = a[k];
                kp1 = k + 1;

                // find l = pivot index

                l = idamax(n - k, col_k, k, 1) + k;
                ipvt[k] = l;

                // zero pivot implies this column already triangularized

                if (col_k[l] != 0) {

                    // interchange if necessary

                    if (l != k) {
                        t = col_k[l];
                        col_k[l] = col_k[k];
                        col_k[k] = t;
                    }

                    // compute multipliers

                    t = -1.0 / col_k[k];
                    dscal(n - (kp1), t, col_k, kp1, 1);

                    // row elimination with column indexing

                    for (j = kp1; j < n; j++) {
                        col_j = a[j];
                        t = col_j[l];
                        if (l != k) {
                            col_j[l] = col_j[k];
                            col_j[k] = t;
                        }
                        daxpy(n - (kp1), t, col_k, kp1, 1, col_j, kp1, 1);
                    }
                } else {
                    info = k;
                }
            }
        }
        ipvt[n - 1] = n - 1;
        if (a[(n - 1)][(n - 1)] == 0) {
            info = n - 1;
        }

        return info;
    }

    /*
     * dgesl solves the double precision system x = b or trans(a) * x = b using
     * the factors computed by dgeco or dgefa.
     *
     * on entry
     *
     * a double precision[n][lda] the output from dgeco or dgefa.
     *
     * lda integer the leading dimension of the array a .
     *
     * n integer the order of the matrix a .
     *
     * ipvt integer[n] the pivot vector from dgeco or dgefa.
     *
     * b double precision[n] the right hand side vector.
     *
     * job integer x = b , x = b where trans(a) is the transpose.
     *
     * on return
     *
     * b the solution vector x .
     *
     * error condition
     *
     * a division by zero will occur if the input factor contains a zero on the
     * diagonal. technically this indicates singularity but it is often caused
     * by improper arguments or improper setting of lda . it will not occur if
     * the subroutines are called correctly and if dgeco has set rcond .gt. 0.0
     * or dgefa has set info .eq. 0 .
     *
     * c where c is a matrix with p columns dgeco(a,lda,n,ipvt,rcond,z) if
     * (!rcond is too small){ for (j=0,j
     * <p,j++) dgesl(a,lda,n,ipvt,c[j][0],0); }
     *
     * linpack. this version dated 08/14/78 . cleve moler, university of new
     * mexico, argonne national lab.
     *
     * functions
     *
     * blas daxpy,ddot
     */

    final void dgesl(final double a[][], final int n, final int ipvt[], double b[], final int job) {
        double t;
        int k, kb, l, nm1, kp1;

        nm1 = n - 1;
        if (job == 0) {

            // job = 0 , solve a * x = b. first solve l*y = b

            if (nm1 >= 1) {
                for (k = 0; k < nm1; k++) {
                    l = ipvt[k];
                    t = b[l];
                    if (l != k) {
                        b[l] = b[k];
                        b[k] = t;
                    }
                    kp1 = k + 1;
                    daxpy(n - (kp1), t, a[k], kp1, 1, b, kp1, 1);
                }
            }

            // now solve u*x = y

            for (kb = 0; kb < n; kb++) {
                k = n - (kb + 1);
                b[k] /= a[k][k];
                t = -b[k];
                daxpy(k, t, a[k], 0, 1, b, 0, 1);
            }
        } else {

            // job = nonzero, solve trans(a) * x = b. first solve trans(u)*y =
            // b

            for (k = 0; k < n; k++) {
                t = ddot(k, a[k], 0, 1, b, 0, 1);
                b[k] = (b[k] - t) / a[k][k];
            }

            // now solve trans(l)*x = y

            if (nm1 >= 1) {
                for (kb = 1; kb < nm1; kb++) {
                    k = n - (kb + 1);
                    kp1 = k + 1;
                    b[k] += ddot(n - (kp1), a[k], kp1, 1, b, kp1, 1);
                    l = ipvt[k];
                    if (l != k) {
                        t = b[l];
                        b[l] = b[k];
                        b[k] = t;
                    }
                }
            }
        }
    }

    /*
     * constant times a vector plus a vector. jack dongarra, linpack, 3/11/78.
     */

    final void daxpy(
        final int n,
        final double da,
        final double dx[],
        final int dx_off,
        final int incx,
        double dy[],
        final int dy_off,
        final int incy) {
        int i, ix, iy;

        if ((n > 0) && (da != 0)) {
            if (incx != 1 || incy != 1) {

                // code for unequal increments or equal increments not equal to
                // 1

                ix = 0;
                iy = 0;
                if (incx < 0) {
                    ix = (-n + 1) * incx;
                }
                if (incy < 0) {
                    iy = (-n + 1) * incy;
                }
                for (i = 0; i < n; i++) {
                    dy[iy + dy_off] += da * dx[ix + dx_off];
                    ix += incx;
                    iy += incy;
                }
                return;
            } else {

                // code for both increments equal to 1

                for (i = 0; i < n; i++) {
                    dy[i + dy_off] += da * dx[i + dx_off];
                }
            }
        }
    }

    /*
     * forms the dot product of two vectors. jack dongarra, linpack, 3/11/78.
     * mcj: declared some parameters as final
     */

    final double ddot(
        final int n,
        final double dx[],
        final int dx_off,
        final int incx,
        final double dy[],
        final int dy_off,
        final int incy) {
        double dtemp;
        int i, ix, iy;

        dtemp = 0;

        if (n > 0) {
            if (incx != 1 || incy != 1) {
                // code for unequal increments or equal increments not equal to
                // 1

                ix = 0;
                iy = 0;
                if (incx < 0) {
                    ix = (-n + 1) * incx;
                }
                if (incy < 0) {
                    iy = (-n + 1) * incy;
                }
                for (i = 0; i < n; i++) {
                    dtemp += dx[ix + dx_off] * dy[iy + dy_off];
                    ix += incx;
                    iy += incy;
                }
            } else {
                // code for both increments equal to 1
                for (i = 0; i < n; i++) {
                    dtemp += dx[i + dx_off] * dy[i + dy_off];
                }
            }
        }
        return (dtemp);
    }

    /*
     * scales a vector by a constant. jack dongarra, linpack, 3/11/78.
     * mcj: declared some parameters as final
     */

    final void dscal(final int n, final double da, double dx[], final int dx_off, final int incx) {
        int i, nincx;

        if (n > 0) {
            if (incx != 1) {

                // code for increment not equal to 1

                nincx = n * incx;
                for (i = 0; i < nincx; i += incx) {
                    dx[i + dx_off] *= da;
                }
            } else {

                // code for increment equal to 1

                for (i = 0; i < n; i++) {
                    dx[i + dx_off] *= da;
                }
            }
        }
    }

    /*
     * finds the index of element having max. absolute value. jack dongarra,
     * linpack, 3/11/78.
     */

    final int idamax(final int n, final double dx[], final int dx_off, final int incx) {
        double dmax, dtemp;
        int i, ix, itemp = 0;

        if (n < 1) {
            itemp = -1;
        } else if (n == 1) {
            itemp = 0;
        } else if (incx != 1) {

            // code for increment not equal to 1

            dmax = abs(dx[dx_off]);
            ix = 1 + incx;
            for (i = 1; i < n; i++) {
                dtemp = abs(dx[ix + dx_off]);
                if (dtemp > dmax) {
                    itemp = i;
                    dmax = dtemp;
                }
                ix += incx;
            }
        } else {

            // code for increment equal to 1

            itemp = 0;
            dmax = abs(dx[dx_off]);
            for (i = 1; i < n; i++) {
                dtemp = abs(dx[i + dx_off]);
                if (dtemp > dmax) {
                    itemp = i;
                    dmax = dtemp;
                }
            }
        }
        return (itemp);
    }

    /*
     * estimate unit roundoff in quantities of size x.
     *
     * this program should function properly on all systems satisfying the
     * following two assumptions, 1. the base used in representing dfloating
     * point numbers is not a power of three. 2. the quantity a in statement 10
     * is represented to the accuracy used in dfloating point variables that
     * are stored in memory. the statement number 10 and the go to 10 are
     * intended to force optimizing compilers to generate code satisfying
     * assumption 2. under these assumptions, it should be true that, a is not
     * exactly equal to four-thirds, b has a zero for its last bit or digit, c
     * is not exactly equal to one, eps measures the separation of 1.0 from the
     * next larger dfloating point number. the developers of eispack would
     * appreciate being informed about any systems where these assumptions do
     * not hold.
     *
     * **************************************************************** this
     * routine is one of the auxiliary routines used by eispack iii to avoid
     * machine dependencies.
     * ****************************************************************
     *
     * this version dated 4/6/83.
     */

    final double epslon(final double x) {
        final double a = 4.0e0 / 3.0e0;
        double b, c, eps = 0;

        while (eps == 0) {
            b = a - 1.0;
            c = b + b + b;
            eps = abs(c - 1.0);
        }
        return (eps * abs(x));
    }

    /*
     * purpose: multiply matrix m times vector x and add the result to vector y.
     *
     * parameters:
     *
     * n1 integer, number of elements in vector y, and number of rows in matrix
     * m
     *
     * y double [n1], vector of length n1 to which is added x
     *
     * n2 integer, number of elements in vector x, and number of columns in
     * matrix m
     *
     * ldm integer, leading dimension of array m
     *
     * x double [n2], vector of length n2
     *
     * m double [ldm][n2], matrix of n1 rows and n2 columns
     */

    final void dmxpy(final int n1, double y[], final int n2, final double x[], final double m[][]) {
        int j, i;

        // cleanup odd vector
        for (j = 0; j < n2; j++) {
            for (i = 0; i < n1; i++) {
                y[i] += x[j] * m[j][i];
            }
        }
    }
}
