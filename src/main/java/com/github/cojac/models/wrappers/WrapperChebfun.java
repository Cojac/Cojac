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
 *    References :
 *    [1] Trefethen, Computing numerically with functions instead of numbers Math. in Comp. Sci. 2007
 *        http://www.chebfun.org/publications/trefethen_functions.pdf
 *    [2] Battles and Trefethen, An extension of Matlab to continuous functions and operators SIAM J. Sci. Comp. 2004
 *        http://www.chebfun.org/publications/chebfun_paper.pdf
 *
 */

package com.github.cojac.models.wrappers;

import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class WrapperChebfun extends ACompactWrapper {
    // -------------------------------------------------------------------------
    // ----------------- Constants ---------------------------------------------
    // -------------------------------------------------------------------------
    private static final double EPSILON = 1E-16;
    // initial degree of polynomial used for initialize the chebfun (p(x) = x)
    private static final int BASE_DEGREE = 32;
    // maximum degree of polynomial used for stop non converging polynomial
    private static final int MAX_DEGREE = 65536;

    // -------------------------------------------------------------------------
    // ----------------- Attributes --------------------------------------------
    // -------------------------------------------------------------------------
    // value of the constant if !isChebfun else NAN
    private final double value;
    // values of the function at the chebyshev points if isChebfun else NULL
    private final double funcValues[];

    // -------------------------------------------------------------------------
    // ----------------- Constructor -------------------------------------------
    // -------------------------------------------------------------------------
    private WrapperChebfun(double value, double[] funcValues) {
        this.value = value;
        this.funcValues = funcValues;
    }

    // -------------------------------------------------------------------------
    // ----------------- Necessary constructor ---------------------------------
    // -------------------------------------------------------------------------
    public WrapperChebfun(ACojacWrapper w) {
        this(w == null ? 0.0 : asCheb(w).value, w == null ? null
                : asCheb(w).funcValues);
    }

    // -------------------------------------------------------------------------
    // ----------------- Generic operators -------------------------------------
    // -------------------------------------------------------------------------
    @Override
    public ACojacWrapper applyUnaryOp(DoubleUnaryOperator op) {
        if (!this.isChebfun())
            return new WrapperChebfun(op.applyAsDouble(this.value), null);
        return new WrapperChebfun(Double.NaN, applyChebUnaryOp(this.funcValues, op));
    }

    @Override
    public ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper w2) {

        if (!this.isChebfun() && !asCheb(w2).isChebfun())
            return new WrapperChebfun(op.applyAsDouble(this.value, asCheb(w2).value), null);

        double[] poly;

        if (this.isChebfun() && asCheb(w2).isChebfun()) {
            poly = applyOp(this.funcValues, asCheb(w2).funcValues, op);
        } else if (this.isChebfun()) {
            poly = applyOp(this.funcValues, asCheb(w2).value, op);
        } else {
            poly = applyOp(this.value, asCheb(w2).funcValues, op);
        }

        return new WrapperChebfun(Double.NaN, poly);
    }

    // -------------------------------------------------------------------------
    // ----------------- Override operators ------------------------------------
    // -------------------------------------------------------------------------

    @Override
    public ACojacWrapper math_abs() {
        if (isChebfun())
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "The operation (abs) should not be used with a Chefun");
        return super.math_abs();
    }

    @Override
    public ACojacWrapper math_min(ACojacWrapper b) {
        if (isChebfun() || asCheb(b).isChebfun())
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "The operation (min) should not be used with a Chefun");
        return super.math_min(b);
    }

    @Override
    public ACojacWrapper math_max(ACojacWrapper b) {
        if (isChebfun() || asCheb(b).isChebfun())
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "The operation (max) should not be used with a Chefun");
        return super.math_max(b);
    }

    // -------------------------------------------------------------------------
    // ----------------- Comparison operators ----------------------------------
    // -------------------------------------------------------------------------
    @Override
    public int dcmpl(ACojacWrapper w) {
        if (this.isChebfun() || asCheb(w).isChebfun())
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not compare Chebfuns");
        if (this.isNaN() || w.isNaN())
            return -1;
        return this.compareTo(w);
    }

    @Override
    public int dcmpg(ACojacWrapper w) {
        if (this.isChebfun() || asCheb(w).isChebfun())
            Logger.getLogger(this.getClass().getPackage().getName()).log(Level.WARNING, "Can not compare Chebfuns");
        if (this.isNaN() || w.isNaN())
            return 1;
        return this.compareTo(w);
    }

    @Override
    public int compareTo(ACojacWrapper w) {
        return Double.compare(toDouble(), w.toDouble());
    }

    // -------------------------------------------------------------------------
    // ----------------- Wrapper methods ---------------------------------------
    // -------------------------------------------------------------------------
    @Override
    public double toDouble() {
        return this.value;
    }


    @Override
    public ACojacWrapper fromDouble(double a, @SuppressWarnings("unused") boolean wasFromFloat) {
        return new WrapperChebfun(a, null);
    }

    // TODO: implémenter
    @Override
    public String asInternalString() {
        if (!isChebfun())
            return "" + this.value;

        String s = "Real poly degree : " + (funcValues.length - 1) + "\n";
        s += "Min poly degree : " + (minReasonableDegree(fft(funcValues))) +
                "\n";
        s += "Poly values at Cheb points :  \n";
        for (double d : funcValues)
            s += d + "\n";
        return s;
    }

    @Override
    public String wrapperName() {
        return "Chebfun";
    }

    // -------------------------------------------------------------------------
    // ----------------- Magic methods -----------------------------------------
    // -------------------------------------------------------------------------
    public static boolean COJAC_MAGIC_isChebfun(CommonDouble d) {
        return asCheb(d.val).isChebfun();
    }

    public static boolean COJAC_MAGIC_isChebfun(CommonFloat d) {
        return asCheb(d.val).isChebfun();
    }

    public static CommonDouble COJAC_MAGIC_asChebfun(@SuppressWarnings("unused") CommonDouble d) {
        WrapperChebfun res = new WrapperChebfun(Double.NaN, initChebun(BASE_DEGREE));
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_asChebfun(@SuppressWarnings("unused") CommonFloat d) {
        WrapperChebfun res = new WrapperChebfun(Double.NaN, initChebun(BASE_DEGREE));
        return new CommonFloat(res);
    }

    public static CommonDouble COJAC_MAGIC_evaluateChebfunAt(CommonDouble d, CommonDouble x) {
        if (!asCheb(d.val).isChebfun()) {
            Logger.getLogger(asCheb(d.val).getClass().getPackage().getName()).log(Level.WARNING, "Only Chefuns can be evaluate");
            WrapperChebfun res = new WrapperChebfun(Double.NaN, null);
            return new CommonDouble(res);
        } else if (asCheb(d.val).value < -1 || asCheb(d.val).value > 1) {
            Logger.getLogger(asCheb(d.val).getClass().getPackage().getName()).log(Level.WARNING, "A Chebfun can only be evaluate on [-1, 1]");
            WrapperChebfun res = new WrapperChebfun(Double.NaN, null);
            return new CommonDouble(res);
        }
        double result = evaluateAt(asCheb(d.val).funcValues, asCheb(x.val).value);
        WrapperChebfun res = new WrapperChebfun(result, null);
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_evaluateChebfunAt(CommonFloat d, CommonFloat x) {
        if (!asCheb(d.val).isChebfun()) {
            Logger.getLogger(asCheb(d.val).getClass().getPackage().getName()).log(Level.WARNING, "Only Chefuns can be evaluate");
            WrapperChebfun res = new WrapperChebfun(Double.NaN, null);
            return new CommonFloat(res);
        } else if (asCheb(d.val).value < -1 || asCheb(d.val).value > 1) {
            Logger.getLogger(asCheb(d.val).getClass().getPackage().getName()).log(Level.WARNING, "A Chebfun can only be evaluate on [-1, 1]");
            WrapperChebfun res = new WrapperChebfun(Double.NaN, null);
            return new CommonFloat(res);
        }
        double result = evaluateAt(asCheb(d.val).funcValues, asCheb(x.val).value);
        WrapperChebfun res = new WrapperChebfun(result, null);
        return new CommonFloat(res);
    }

    public static CommonDouble COJAC_MAGIC_derivateChebfun(CommonDouble d) {
        if (!asCheb(d.val).isChebfun()) {
            Logger.getLogger(asCheb(d.val).getClass().getPackage().getName()).log(Level.WARNING, "Only Chefuns can be derivate");
            WrapperChebfun res = new WrapperChebfun(Double.NaN, null);
            return new CommonDouble(res);
        }
        double[] result = derivate(asCheb(d.val).funcValues);
        WrapperChebfun res = new WrapperChebfun(Double.NaN, result);
        return new CommonDouble(res);
    }

    public static CommonFloat COJAC_MAGIC_derivateChebfun(CommonFloat d) {
        if (!asCheb(d.val).isChebfun()) {
            Logger.getLogger(asCheb(d.val).getClass().getPackage().getName()).log(Level.WARNING, "Only Chebfuns can be derivate");
            WrapperChebfun res = new WrapperChebfun(Double.NaN, null);
            return new CommonFloat(res);
        }
        double[] result = derivate(asCheb(d.val).funcValues);
        WrapperChebfun res = new WrapperChebfun(Double.NaN, result);
        return new CommonFloat(res);
    }

    // -------------------------------------------------------------------------
    // ----------------- Useful methods ----------------------------------------
    // -------------------------------------------------------------------------

    private static WrapperChebfun asCheb(ACojacWrapper w) {
        return (WrapperChebfun) w;
    }

    private boolean isChebfun() {
        return funcValues != null;
    }

    // Retourne les points de chebyshev de j=0 à n compris
    private static double[] initChebun(int n) {
        double p[] = new double[n + 1];
        for (int j = 0; j <= n; j++)
            p[j] = chebPoint(j, n);
        return p;
    }

    // Retourne le j(ième) point de chebyshev sur n
    private static double chebPoint(int j, int n) {
        return Math.cos(j * Math.PI / n);
    }

    // -------------------------------------------------------------------------
    // ----------------- Useful methods ----------------------------------------
    // -------------------------------------------------------------------------

    // Permet d'évaluer le polynome au point x
    // voir page 14 fig. 3.3-3.5 de la référence [1]
    private static double evaluateAt(double[] funcValues, double x) {
        int n = funcValues.length - 1;
        // si x est un point de chebyshev retourne directement la valeur
        // correspondante
        for (int j = 0; j <= n; j++)
            if (Math.abs(x - chebPoint(j, n)) < EPSILON)
                return funcValues[j];

        // Applique la formule d'interpolation baricentrique sur le polynôme
        double nom = 0;
        for (int j = 0; j <= n; j++) {
            double tmp = 1 / (x - chebPoint(j, n)) * funcValues[j];
            tmp = (j % 2 == 0) ? tmp : -tmp;
            nom += (j == 0 || j == n) ? tmp / 2 : tmp;
        }

        double den = 0;
        for (int j = 0; j <= n; j++) {
            double tmp = 1 / (x - chebPoint(j, n));
            tmp = (j % 2 == 0) ? tmp : -tmp;
            den += (j == 0 || j == n) ? tmp / 2 : tmp;
        }

        return nom / den;
    }

    // Permet de passer de la représentation temporelle/{f0,...,fN}/funcValues
    // à la représentation fréquentielle/{a0,...,aN}/coeffs
    // voir page 1755 ou 13 de la référence [2]
    private static double[] fft(double[] funcValues) {
        int n = funcValues.length - 1;
        double[] f = funcValues;

        // extension du vecteur pour permettre la FFT {f0,...,fn,fn-1,...,f1}
        double[][] dataRI = new double[2][2 * n];
        for (int j = 0; j <= n; j++)
            dataRI[0][j] = f[j];
        for (int j = 1; j <= n - 1; j++)
            dataRI[0][n + j] = f[n - j];

        // applique la FFT le vecteur étendu
        FastFourierTransformer.transformInPlace(dataRI, DftNormalization.UNITARY, TransformType.FORWARD);

        // extraction des N+1 premiers éléments (de 0 à N compris)
        double[] a = new double[n + 1];
        for (int j = 0; j <= n; j++)
            a[j] = dataRI[0][j];

        // applique la pondération
        a[0] /= (2 * n);
        for (int j = 1; j < n; j++)
            a[j] /= (n);
        a[n] /= (2 * n);

        return a;
    }

    // Permet de passer de la représentation fréquentielle/{a0,...,aN}/coeffs
    // à la représentation temporelle/{f0,...,fN}/funcValues
    // voir page 1755 ou 13 de la référence [2]
    private static double[] ifft(double[] coeffs) {
        int n = coeffs.length - 1;
        double[] a = Arrays.copyOf(coeffs, coeffs.length);

        // applique la pondération
        a[0] *= (2 * n);
        for (int j = 1; j < n; j++) {
            a[j] *= (n);
        }
        a[n] *= (2 * n);

        // extension du vecteur pour permettre la IFFT {a0,...,aN,aN-1,...,a1}
        double[][] dataRI1 = new double[2][2 * n];
        for (int j = 0; j <= n; j++)
            dataRI1[0][j] = a[j];
        for (int j = 1; j <= n - 1; j++)
            dataRI1[0][n + j] = a[n - j];

        // applique la IFFT le vecteur étendu
        FastFourierTransformer.transformInPlace(dataRI1, DftNormalization.UNITARY, TransformType.INVERSE);

        // extraction des N+1 premiers éléments (de 0 à N compris)
        double[] f = new double[n + 1];
        for (int j = 0; j <= n; j++)
            f[j] = dataRI1[0][j];
        return f;
    }

    // Permet d'appliquer une opération entre 2 Chebfuns
    private static double[] applyOp(double[] funcValuesA, double[] funcValuesB, DoubleBinaryOperator op) {

        double[] resFuncValues; // polynôme résultant

        // égualise les tailles des 2 polynôme
        while (funcValuesA.length > funcValuesB.length)
            funcValuesB = extendDegree(funcValuesB);
        while (funcValuesA.length < funcValuesB.length)
            funcValuesA = extendDegree(funcValuesA);

        while (true) {
            resFuncValues = new double[funcValuesA.length];
            // applique l'opération entre les 2 Chebfuns
            // ex : {f0+g0,f1+g1,...,fN+gN}
            for (int i = 0; i < resFuncValues.length; i++)
                resFuncValues[i] = op.applyAsDouble(funcValuesA[i], funcValuesB[i]);
            // contrôle si le degrée du polynôme résultant est suffisant
            if (isDegreeGoodEnough(resFuncValues))
                break;

            // étend la taille des polyômes
            funcValuesA = extendDegree(funcValuesA);
            funcValuesB = extendDegree(funcValuesB);
        }

        return resFuncValues;
    }

    // Permet d'appliquer une opération entre un Chefun et une constante
    private static double[] applyOp(double[] funcValues, double constant, DoubleBinaryOperator op) {

        double[] resFuncValues; // polynôme résultant

        while (true) {
            resFuncValues = new double[funcValues.length];
            // applique l'opération entre le Chebfun et la constante
            // ex : {f0+C,f1+C,...,fN+C}
            for (int i = 0; i < funcValues.length; i++)
                resFuncValues[i] = op.applyAsDouble(funcValues[i], constant);
            // contrôle si le degrée du polynôme résultant est suffisant
            if (isDegreeGoodEnough(resFuncValues))
                break;
            // étend la taille du polyôme
            funcValues = extendDegree(funcValues);
        }
        return resFuncValues;
    }

    // Permet d'appliquer une opération entre un Chefun et une constante
    private static double[] applyOp(double constant, double[] funcValues, DoubleBinaryOperator op) {

        double[] resFuncValues; // polynôme résultant

        while (true) {
            resFuncValues = new double[funcValues.length];
            // applique l'opération entre le Chebfun et la constante
            // ex : {C*f0,C*f1,...,C+fN}
            for (int i = 0; i < funcValues.length; i++)
                resFuncValues[i] = op.applyAsDouble(constant, funcValues[i]);
            // contrôle si le degrée du polynôme résultant est suffisant
            if (isDegreeGoodEnough(resFuncValues))
                break;
            // étend la taille du polyôme
            funcValues = extendDegree(funcValues);
        }
        return resFuncValues;
    }

    // Permet d'appliquer une opération sur un Chebfun
    private static double[] applyChebUnaryOp(double[] funcValues, DoubleUnaryOperator op) {

        double[] resFuncValues; // polynôme résultant

        while (true) {
            resFuncValues = new double[funcValues.length];
            for (int i = 0; i < funcValues.length; i++)
                resFuncValues[i] = op.applyAsDouble(funcValues[i]);

            if (isDegreeGoodEnough(resFuncValues))
                break;

            funcValues = extendDegree(funcValues);
        }
        return resFuncValues;
    }

    // Permet d'étendre le degré polynome par un facteur de 2
    private static double[] extendDegree(double[] funcValues) {
        int extendedDegree = (funcValues.length - 1) * 2;
        double[] extendedValues = new double[extendedDegree + 1];

        for (int i = 0; i < extendedValues.length; i++)
            extendedValues[i] = (i % 2 == 0) ? funcValues[i / 2]
                    : evaluateAt(funcValues, chebPoint(i, extendedDegree));

        return extendedValues;
    }

    // Permet de déterminer si le degré du polynome est suffisant pour
    // repérenter la fonction de manière précise
    private static boolean isDegreeGoodEnough(double[] funcValues) {
        if (Double.isNaN(funcValues[0]))
            return true;
        if (funcValues.length - 1 >= MAX_DEGREE)
            return true;
        double[] coeffs = fft(funcValues);
        return minReasonableDegree(coeffs) < coeffs.length - 1;
    }

    private static int minReasonableDegree(double[] p) {
        int n = p.length - 1;
        int j;
        for (j = 0; j < n; j++)
            if (Math.abs(p[j]) < EPSILON && Math.abs(p[j + 1]) < EPSILON)
                break;
        return j;
    }

    private static double[] derivate(double[] funcValues) {

        int n = funcValues.length - 1;

        double[] a = fft(funcValues);
        double[] b = new double[a.length];

        b[n] = 0;
        b[n - 1] = 2 * n * a[n];
        for (int k = n - 1; k >= 2; k--)
            b[k - 1] = b[k + 1] + 2 * k * a[k];
        b[0] = b[2] / 2 + a[1];

        return ifft(b);
    }

}
