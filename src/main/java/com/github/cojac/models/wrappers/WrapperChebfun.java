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

public class WrapperChebfun extends ACompactWrapper<WrapperChebfun> {
    // -------------------------------------------------------------------------
    // ----------------- Constants ---------------------------------------------
    // -------------------------------------------------------------------------
    private static final double EPSILON = 1E-15;
    // initial degree of polynomial used for initialize the chebfun (p(x) = x)
    private static final int BASE_DEGREE = 32;
    // maximum degree of polynomial used for stop non converging polynomial
    private static final int MAX_DEGREE = 65536; //8192;// 65536

    // TODO: Chebfun domains other than -1..+1 have been only superficially tested...
    // TODO: consider ensuring thread-safety when setting demainMin/Max via magic method
    private static double domainMin = -1.0;
    private static double domainMax = +1.0;

    private static final DftNormalization FFT_NORMALIZATION=DftNormalization.STANDARD; //STANDARD; //UNITARY;
    // -------------------------------------------------------------------------
    // ----------------- Attributes --------------------------------------------
    // -------------------------------------------------------------------------
    // value of the constant if !isChebfun else NAN
    // TODO decide whether it is better to store constants as {x,x} chebfun...
    private final double value;
    // values of the function at the Chebyshev points if isChebfun else NULL
    private final double[] funcValues;
    

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
    public WrapperChebfun(WrapperChebfun w) {
        this(w == null ? 0.0  : asCheb(w).value,
             w == null ? null : asCheb(w).funcValues);
    }

    // -------------------------------------------------------------------------
    // ----------------- Generic operators -------------------------------------
    // -------------------------------------------------------------------------
    @Override
    public WrapperChebfun applyUnaryOp(DoubleUnaryOperator op) {
        if (!this.isChebfun())
            return new WrapperChebfun(op.applyAsDouble(this.value), null);
        return new WrapperChebfun(Double.NaN, applyChebUnaryOp(this.funcValues, op));
    }

    @Override
    public WrapperChebfun applyBinaryOp(DoubleBinaryOperator op, WrapperChebfun w2) {

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
    public WrapperChebfun math_abs() {
        if (isChebfun())
            pkgLogger().log(Level.WARNING, "The operation (abs) should not be used with a Chefun");
        return super.math_abs();
    }

    @Override
    public WrapperChebfun math_min(WrapperChebfun b) {
        if (isChebfun() || asCheb(b).isChebfun())
            pkgLogger().log(Level.WARNING, "The operation (min) should not be used with a Chefun");
        return super.math_min(b);
    }

    @Override
    public WrapperChebfun math_max(WrapperChebfun b) {
        if (isChebfun() || asCheb(b).isChebfun())
            pkgLogger().log(Level.WARNING, "The operation (max) should not be used with a Chefun");
        return super.math_max(b);
    }

    @Override
    public WrapperChebfun drem(WrapperChebfun b) {
        if (isChebfun() || asCheb(b).isChebfun())
            pkgLogger().log(Level.WARNING, "The operation (%) should not be used with a Chefun");
        return super.drem(b);
    }

    // -------------------------------------------------------------------------
    // ----------------- Comparison operators ----------------------------------
    // -------------------------------------------------------------------------
    @Override
    public int dcmpl(WrapperChebfun w) {
        if (this.isChebfun() || asCheb(w).isChebfun())
            pkgLogger().log(Level.WARNING, "Can not compare Chebfuns");
        if (this.isNaN() || w.isNaN())
            return -1;
        return this.compareTo(w);
    }

    @Override
    public int dcmpg(WrapperChebfun w) {
        if (this.isChebfun() || asCheb(w).isChebfun())
            pkgLogger().log(Level.WARNING, "Can not compare Chebfuns");
        if (this.isNaN() || w.isNaN())
            return 1;
        return this.compareTo(w);
    }

    @Override
    public int compareTo(WrapperChebfun w) {
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
    public WrapperChebfun fromDouble(double a, @SuppressWarnings("unused") boolean wasFromFloat) {
        return new WrapperChebfun(a, null);
    }

    @Override
    public String asInternalString() {
        if (!isChebfun())
            return "Chebfun(" + this.value+")";
        double[] fv=fft(funcValues);
        int deg=minReasonableDegree(fv);
        int nmax=64;
        String s = "Chebfun(deg:" + (funcValues.length - 1) + ", ";
        s += "effective deg: " + deg + ", ";
        s += toTruncatedStr(funcValues, nmax);
        s += ", fft: "+toTruncatedStr(fft(funcValues), nmax);
//        s += "\n, i(f): "+toTruncatedStr(ifft(fft(funcValues)), nmax);
//        s += "\n, ext: "+toTruncatedStr(extendDegree(funcValues), nmax);
        return s;
    }
    
    private static String toTruncatedStr(double[] t, int n) {
        if(t.length<n) return Arrays.toString(t);
        return Arrays.toString(Arrays.copyOf(t, n))+"etc. ";
    }

    @Override
    public String wrapperName() {
        return "Chebfun";
    }

    // -------------------------------------------------------------------------
    // ----------------- Magic methods -----------------------------------------
    // -------------------------------------------------------------------------
    public static boolean COJAC_MAGIC_isChebfun(CommonDouble<WrapperChebfun> d) {
        return asCheb(d.val).isChebfun();
    }

    public static CommonDouble<WrapperChebfun> COJAC_MAGIC_identityFct() {
        WrapperChebfun res = new WrapperChebfun(Double.NaN, initChebun(BASE_DEGREE));
        return new CommonDouble<>(res);
    }

    public static CommonDouble<WrapperChebfun> COJAC_MAGIC_evaluateAt(CommonDouble<WrapperChebfun> d,
                                                                      CommonDouble<WrapperChebfun> x) {
        WrapperChebfun dd = asCheb(d.val);
        WrapperChebfun xx = asCheb(x.val);
        if (!dd.isChebfun()) {
            pkgLogger().log(Level.WARNING, "Only Chefuns can be evaluate");
            WrapperChebfun res = new WrapperChebfun(Double.NaN, null);
            return new CommonDouble<>(res);
        } else if (xx.value < domainMin || xx.value > domainMax) {
            pkgLogger().log(Level.WARNING, "A Chebfun can only be evaluate on [-1, 1]");
            WrapperChebfun res = new WrapperChebfun(Double.NaN, null);
            return new CommonDouble<>(res);
        }
        double result = evaluateAt(dd.funcValues, xx.value);
        WrapperChebfun res = new WrapperChebfun(result, null);
        return new CommonDouble<>(res);
    }

    public static CommonDouble<WrapperChebfun> COJAC_MAGIC_derivative(CommonDouble<WrapperChebfun> d) {
        if (!asCheb(d.val).isChebfun()) {
            pkgLogger().log(Level.WARNING, "Only Chefuns can be derivate");
            WrapperChebfun res = new WrapperChebfun(Double.NaN, null);
            return new CommonDouble<>(res);
        }
        double[] result = derivate(asCheb(d.val).funcValues);
        WrapperChebfun res = new WrapperChebfun(Double.NaN, result);
        return new CommonDouble<>(res);
    }

    /** Caution: this has a global effect, and any Chebfun computed before
     * becomes de facto invalid. Maybe it should be moved as a Cojac option. 
     */
    public static void COJAC_MAGIC_setChebfunDomain(CommonDouble<WrapperChebfun> min, CommonDouble<WrapperChebfun> max) {
        domainMin=asCheb(min.val).value;
        domainMax=asCheb(max.val).value;
    }
    
    // -------------------------------------------------------------------------
    // ----------------- Useful methods ----------------------------------------
    // -------------------------------------------------------------------------

    private static WrapperChebfun asCheb(WrapperChebfun w) {
        return w;
    }

    private boolean isChebfun() {
        return funcValues != null;
    }

    // Retourne les points de chebyshev de j=0 à n compris
    private static double[] initChebun(int n) {
        double[] p = new double[n + 1];
        for (int j = 0; j <= n; j++)
            p[j] = remappedInMinMaxDomain(chebPoint(j, n));
        return p;
    }

    // Retourne le j(ième) sur n point de chebyshev, entre -1 et +1. 
    private static double chebPoint(int j, int n) {
        return Math.cos(j * Math.PI / n);
    }

    private static double remappedInMinMaxDomain(double c) {
        return (c+1)*0.5*(domainMax-domainMin) + domainMin;
    }

    // Permet d'évaluer le polynome au point x
    // voir page 14 fig. 3.3-3.5 de la référence [1]
    private static double evaluateAt(double[] funcValues, double x0) {
        double x=2*(x0-domainMin)/(domainMax-domainMin) - 1.0;
        //x=x0;
        int n = funcValues.length - 1;
        // si x est un point de chebyshev retourne directement la valeur
        // correspondante
        for (int j = 0; j <= n; j++)
            if (Math.abs(x - chebPoint(j, n)) < EPSILON)
                return funcValues[j];

        // Applique la formule d'interpolation barycentrique sur le polynôme
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

        // extension du vecteur pour permettre la FFT {f0,...,fn,fn-1,...,f1}
        double[][] dataRI = new double[2][2 * n];
        System.arraycopy(funcValues, 0, dataRI[0], 0, n + 1);
        for (int j = 1; j <= n - 1; j++)
            dataRI[0][n + j] = funcValues[n - j];

        // applique la FFT le vecteur étendu
        FastFourierTransformer.transformInPlace(dataRI, FFT_NORMALIZATION, TransformType.FORWARD);

        // extraction des N+1 premiers éléments (de 0 à N compris)
        double[] a = new double[n + 1];
        System.arraycopy(dataRI[0], 0, a, 0, n + 1);

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
        System.arraycopy(a, 0, dataRI1[0], 0, n + 1);
        for (int j = 1; j <= n - 1; j++)
            dataRI1[0][n + j] = a[n - j];

        // applique la IFFT le vecteur étendu
        FastFourierTransformer.transformInPlace(dataRI1, FFT_NORMALIZATION, TransformType.INVERSE);

        // extraction des N+1 premiers éléments (de 0 à N compris)
        double[] f = new double[n + 1];
        System.arraycopy(dataRI1[0], 0, f, 0, n + 1);
        return f;
    }

    // Permet d'appliquer une opération entre 2 Chebfuns
    private static double[] applyOp(double[] funcValuesA, double[] funcValuesB, DoubleBinaryOperator op) {

        double[] resFuncValues; // polynôme résultant

        // égalise les tailles des 2 polynômes
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

    // Permet d'appliquer une opération entre une constante et un Chefun
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

    private static double[] extendDegree(double[] funcValues) {
        return extendDegreeViaFft(funcValues);
        //return extendDegreeViaEvaluate(funcValues); // just for debugging
    }
    
    // Permet d'étendre le degré polynome par un facteur de 2
    private static double[] extendDegreeViaFft(double[] funcValues) {
        double[] f=fft(funcValues);
        int extendedDegree = (funcValues.length - 1) * 2;
        double[] extendedFft = new double[extendedDegree + 1];
        System.arraycopy(f, 0, extendedFft, 0, f.length);
        return ifft(extendedFft);
    }

    // idem but with the naïve evaluation method, in O(n2)
//    private static double[] extendDegreeViaEvaluation(double[] funcValues) {
//        int extendedDegree = (funcValues.length - 1) * 2;
//        double[] extendedValues = new double[extendedDegree + 1];
//
//        for (int i = 0; i < extendedValues.length; i++)
//            extendedValues[i] = (i % 2 == 0) ? funcValues[i / 2]
//                    : evaluateAt(funcValues, chebPoint(i, extendedDegree));
//
//        return extendedValues;
//    }

    // Permet de déterminer si le degré du polynome est suffisant pour
    // représenter la fonction de manière précise
    private static boolean isDegreeGoodEnough(double[] funcValues) {
        for (double funcValue : funcValues) {
            if (Double.isNaN(funcValue)) {
                pkgLogger().log(Level.WARNING, "Chebfun contains NaN");
                return true;
            }
        }

        if (funcValues.length - 1 >= MAX_DEGREE) {
            pkgLogger().log(Level.WARNING, "Chebfun not converging");
            return true;
        }
        double[] coeffs = fft(funcValues);
//        int mm1= minReasonableDegreeBadoud(coeffs);
//        int mm2= minReasonableDegree      (coeffs);
//        if(mm1!=mm2) {
//            System.out.println("OUUUPS: "+mm1+" "+mm2+Arrays.toString(coeffs));
//        }        
        return minReasonableDegree(coeffs) < coeffs.length - 1;
    }

    private static int minReasonableDegree(double[] p) {
        double max=0.0;
        for(double d:p) max=Math.max(max, Math.abs(d));
        if (max<1E-100) return 1; // vector considered as zero
        int j;
        for(j = p.length-1; j >= 0; j--)
            if (Math.abs(p[j])/max >= 1e1*EPSILON)
                break;
        return j+1;
    }

//    private static int minReasonableDegreeBadoud(double[] p) {
//        int n = p.length - 1;
//        int j;
//        for (j = 0; j < n; j++)
//            if (Math.abs(p[j]) < EPSILON && Math.abs(p[j + 1]) < EPSILON)
//                break;
//        return j;
//    }

    private static double[] derivate(double[] funcValues) {
        int n = funcValues.length - 1;
        double[] a = fft(funcValues);
        double[] b = new double[a.length];
        b[n] = 0;
        b[n - 1] = 2 * n * a[n];
        for (int k = n - 1; k >= 2; k--)
            b[k - 1] = b[k + 1] + 2 * k * a[k];
        b[0] = b[2] / 2 + a[1];
        double[] bi=ifft(b);
        double scale = 2 / (domainMax-domainMin);
        for(int i=0; i<bi.length; i++)
            bi[i] = bi[i]*scale;
        return bi;
    }
    
    private static Logger pkgLogger() {
        return Logger.getLogger(WrapperChebfun.class.getPackage().getName());
    }

    //=========================================================================
    public static void main(String...a) {
        double[] d=new double[]{5000, 5000};
        //d=new double[]{5E-15, 5E-15};
        WrapperChebfun w=new WrapperChebfun(3, d);
        System.out.println(w.asInternalString());
        System.out.println("fi "+Arrays.toString(d));
        System.out.println("evalAt: "+evaluateAt(d, -0.6));
        System.out.println(isDegreeGoodEnough(d));
        System.out.println(" fft: "+Arrays.toString(fft(d)));
        System.out.println("ifft: "+Arrays.toString(ifft(fft(d))));
        
        d=extendDegree(d); System.out.println(" --- extending ---");
        d=extendDegree(d); System.out.println(" --- extending ---");
        System.out.println("fi "+Arrays.toString(d));
        System.out.println("evalAt: "+evaluateAt(d, -0.6));
        System.out.println(" fft: "+Arrays.toString(fft(d)));
        System.out.println("ifft: "+Arrays.toString(ifft(fft(d))));
        System.out.println(isDegreeGoodEnough(d));
    }
}
