/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst
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

package com.github.cojac.models.wrappers;

import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class WrapperChebfun extends ACojacWrapper {
    private static final int N = 32;
    private final double value;
    private final boolean isChebfun;
    private final double poly[];

    private WrapperChebfun(double value, boolean isChebfun, double[] poly) {
        this.value = value;
        this.isChebfun = isChebfun;
        this.poly = poly;
    }

    private WrapperChebfun(double value, boolean isChebfun) {
        this.value = value;
        this.isChebfun = isChebfun;
        this.poly = isChebfun ? initPoly(N) : null;
    }

    // -------------------------------------------------------------------------
    // ----------------- Necessary constructor ---------------------------------
    // -------------------------------------------------------------------------
    public WrapperChebfun(ACojacWrapper w) {
        this(w == null ? 0.0 : asCheb(w).value, w == null ? false
                : asCheb(w).isChebfun, w == null ? null : asCheb(w).poly);
    }
    // -------------------------------------------------------------------------

    @Override
    public ACojacWrapper dadd(ACojacWrapper w) {
        return applyOp(this, asCheb(w), (x, y) -> x + y);
    }

    @Override
    public ACojacWrapper dsub(ACojacWrapper w) {
        return applyOp(this, asCheb(w), (x, y) -> x - y);
    }

    @Override
    public ACojacWrapper dmul(ACojacWrapper w) {
        return applyOp(this, asCheb(w), (x, y) -> x * y);
    }

    @Override
    public ACojacWrapper ddiv(ACojacWrapper w) {
        return applyOp(this, asCheb(w), (x, y) -> x / y);
    }

    @Override
    public ACojacWrapper drem(ACojacWrapper w) {
        return new WrapperChebfun(w);
    }

    @Override
    public ACojacWrapper dneg() {
        return this;
    }

    @Override
    public ACojacWrapper math_sqrt() {
        return this;
    }

    @Override
    public ACojacWrapper math_abs() {
        return this;
    }

    @Override
    public ACojacWrapper math_sin() {
        return this;
    }

    @Override
    public ACojacWrapper math_cos() {
        return this;
    }

    @Override
    public ACojacWrapper math_tan() {
        return this;
    }

    @Override
    public ACojacWrapper math_asin() {
        return this;
    }

    @Override
    public ACojacWrapper math_acos() {
        return this;
    }

    @Override
    public ACojacWrapper math_atan() {
        return this;
    }

    @Override
    public ACojacWrapper math_sinh() {
        return this;
    }

    @Override
    public ACojacWrapper math_cosh() {
        return this;
    }

    @Override
    public ACojacWrapper math_tanh() {
        return this;
    }

    @Override
    public ACojacWrapper math_exp() {
        return this;
    }

    @Override
    public ACojacWrapper math_log() {
        return this;
    }

    @Override
    public ACojacWrapper math_log10() {
        return this;
    }

    @Override
    public ACojacWrapper math_toRadians() {
        return this;
    }

    @Override
    public ACojacWrapper math_toDegrees() {
        return this;
    }

    @Override
    public ACojacWrapper math_min(ACojacWrapper w) {
        return new WrapperChebfun(w);
    }

    @Override
    public ACojacWrapper math_max(ACojacWrapper w) {
        return new WrapperChebfun(w);
    }

    @Override
    public ACojacWrapper math_pow(ACojacWrapper w) {
        return new WrapperChebfun(w);
    }

    // -------------------------------------------------------------------------
    @Override
    public double toDouble() {
        return value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperChebfun(a, false);
    }

    @Override
    public int dcmpl(ACojacWrapper w) {
        // if (this.expr.containsUnknown || asCheb(w).expr.containsUnknown)
        // Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING,
        // "Can not compare symbolic expressions containing unknowns");
        if (this.isNaN() || w.isNaN())
            return -1;
        return this.compareTo(w);
    }

    @Override
    public int dcmpg(ACojacWrapper w) {
        // if (this.expr.containsUnknown || asCheb(w).expr.containsUnknown)
        // Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING,
        // "Can not compare symbolic expressions containing unknowns");
        if (this.isNaN() || w.isNaN())
            return 1;
        return this.compareTo(w);
    }

    @Override
    public int compareTo(ACojacWrapper w) {
        return Double.compare(toDouble(), w.toDouble());
    }

    @Override
    public String asInternalString() {
        return "todo";
    }

    @Override
    public String wrapperName() {
        return "Symbolic";
    }

    // ------------------------------------------------------------------------
    public static boolean COJAC_MAGIC_isChebfun(CommonDouble d) {
        return asCheb(d.val).isChebfun;
    }

    public static boolean COJAC_MAGIC_isChebfun(CommonFloat d) {
        return asCheb(d.val).isChebfun;
    }

    public static CommonDouble COJAC_MAGIC_asChebfun(CommonDouble d) {
        WrapperChebfun res = new WrapperChebfun(Double.NaN, true);
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_asChebfun(CommonFloat d) {
        WrapperChebfun res = new WrapperChebfun(Double.NaN, true);
        return new CommonFloat(res);
    }

    // TODO
    public static CommonDouble COJAC_MAGIC_evaluateChebfunAt(CommonDouble d, CommonDouble x) {
        double result = asCheb(d.val).evaluateAt(asCheb(x.val).value);
        WrapperChebfun res = new WrapperChebfun(result, false);
        return new CommonDouble(res);
    }

    // TODO
    public static CommonFloat COJAC_MAGIC_evaluateChebfunAt(CommonFloat d, CommonFloat x) {
        double result = asCheb(d.val).value;
        WrapperChebfun res = new WrapperChebfun(result, false);
        return new CommonFloat(res);
    }

    // -------------------------------------------------------------------------

    private static WrapperChebfun asCheb(ACojacWrapper w) {
        return (WrapperChebfun) w;
    }

    // -------------------------------------------------------------------------
    private static double[] initPoly(int n) {
        double p[] = new double[n + 1];
        for (int j = 0; j <= n; j++)
            p[j] = chebPoint(j, n);
        return p;
    }

    private static double chebPoint(int j, int n) {
        return Math.cos(j * Math.PI / n);
    }

    private double evaluateAt(double x) {
        int n = poly.length - 1;

        double nominator = 0;
        for (int j = 0; j <= n; j++) {
            double tmp;
            if (j % 2 == 0)
                tmp = 1 / (x - chebPoint(j, n)) * poly[j];
            else
                tmp = -1 / (x - chebPoint(j, n)) * poly[j];
            if (j == 0 || j == n)
                tmp *= 0.5;
            nominator += tmp;
        }

        double denominator = 0;
        for (int j = 0; j <= n; j++) {
            double tmp;
            if (j % 2 == 0)
                tmp = 1 / (x - chebPoint(j, n));
            else
                tmp = -1 / (x - chebPoint(j, n));
            if (j == 0 || j == n)
                tmp *= 0.5;
            denominator += tmp;
        }

        return nominator / denominator;
    }

    private static double[] fft(double[] poly) {
        int n = poly.length - 1;
        double[] f = poly;

        double[][] dataRI = new double[2][2 * n];
        for (int j = 0; j <= n; j++) {
            dataRI[0][j] = f[j];
        }

        for (int j = 1; j <= n - 1; j++) {
            dataRI[0][n + j] = f[n - j];
        }

        System.out.println("--- fft");
        FastFourierTransformer.transformInPlace(dataRI, DftNormalization.STANDARD, TransformType.FORWARD);
        double[] a = new double[n + 1];
        for (int j = 0; j <= n; j++) {
            a[j] = dataRI[0][n - j];
        }

        a[0] /= (2 * n);
        for (int j = 1; j < n; j++) {
            a[j] /= (n);
        }
        a[n] /= (2 * n);

        return a;
    }

    private static double[] ifft(double[] coeffs) {
        int n = coeffs.length - 1;
        double[] a = Arrays.copyOf(coeffs, coeffs.length);

        a[0] *= (2 * n);
        for (int j = 1; j < n; j++) {
            a[j] *= (n);
        }
        a[n] *= (2 * n);

        double[][] dataRI1 = new double[2][2 * n];
        for (int j = 0; j <= n; j++) {
            dataRI1[0][j] = a[n - j];
        }

        for (int j = 1; j <= n - 1; j++) {
            dataRI1[0][n + j] = a[j];
        }

        FastFourierTransformer.transformInPlace(dataRI1, DftNormalization.STANDARD, TransformType.INVERSE);

        double[] f = new double[n + 1];
        for (int j = 0; j <= n; j++) {
            f[j] = dataRI1[0][j];
        }
        return f;
    }

    private static WrapperChebfun applyOp(WrapperChebfun w1, WrapperChebfun w2, DoubleBinaryOperator op) {
        if (!w1.isChebfun && !w2.isChebfun)
            return new WrapperChebfun(op.applyAsDouble(w1.value, w2.value), false);
        double[] poly;
        if (w1.isChebfun && w2.isChebfun) {
            poly = applyOp(w1.poly, w2.poly, op);
        } else if (w1.isChebfun) {
            poly = applyOp(w1.poly, w2.value, op);
        } else {
            poly = applyOp(asCheb(w2).poly, w1.value, op);
        }
        return new WrapperChebfun(Double.NaN, true, poly);
    }

    private static double[] applyOp(double[] p1, double[] p2, DoubleBinaryOperator op) {
        double[] res = new double[p1.length];
        for (int i = 0; i < res.length; i++)
            res[i] = op.applyAsDouble(p1[i], p2[i]);
        return res;
    }

    private static double[] applyOp(double[] p1, double c, DoubleBinaryOperator op) {
        double[] res = new double[p1.length];
        for (int i = 0; i < p1.length; i++)
            res[i] = op.applyAsDouble(p1[i], c);
        return res;
    }

    private static double[] applyOp(double[] p1, DoubleUnaryOperator op) {
        double[] res = new double[p1.length];
        for (int i = 0; i < p1.length; i++)
            res[i] = op.applyAsDouble(p1[i]);
        return res;
    }

}
