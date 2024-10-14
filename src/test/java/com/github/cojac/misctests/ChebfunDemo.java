/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst & HEIA-FR students
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

// Run with "Chebfun" Wrapper: ... cojac.jar="-Rcheb"

package com.github.cojac.misctests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class ChebfunDemo {

    private static final double epsilon = 1e-6;
    private static final Map<String, Method> methods;

    static {
        methods = new TreeMap<>();
        for (Method method : ChebfunDemo.class.getDeclaredMethods()) {
            methods.put(method.getName(), method);
        }
    }

    // ----------------------------------------------------
    // ---------- COJAC MAGIC -----------------------------
    // ----------------------------------------------------
    public static String COJAC_MAGIC_toString(double n) {
        return "";
    }

    public static boolean COJAC_MAGIC_isChebfun(double a) {
        return false;
    }

    public static double COJAC_MAGIC_identityFct() {
        return 0;
    }

    public static double COJAC_MAGIC_evaluateAt(double d, double x) {
        return d;
    }

    public static double COJAC_MAGIC_derivative(double d) {
        return d;
    }

    // ----------------------------------------------------
    // ---------- GLOBAL TEST -----------------------------
    // ----------------------------------------------------

    public static void main(String[] args) {

        int nbrError = 0;
        //nbrError += comparisonTest();
        nbrError += runChebfunTest();

        if (nbrError == 0) {
            System.out.println("----------------------------------------");
            System.out.println(" Test finished successfully !");
            System.out.println("----------------------------------------");
        } else {
            System.err.println("----------------------------------------");
            System.err.println(" Test finished with " + nbrError + " error");
            System.err.println("----------------------------------------");
        }

    }

    // ----------------------------------------------------
    // ---------- COMPARISON TEST -------------------------
    // ----------------------------------------------------

    public static int comparisonTest() {
        System.out.println("----------------------------------------");
        System.out.println(" Start Comparison Test (warning are expected!)");
        System.out.println("----------------------------------------");

        int nbrError = 0;

        double x = COJAC_MAGIC_identityFct();
        Double y = someFunction(x, 3, 4);

        if (y > 2) {
            System.err.println(y + ">" + 2);
            nbrError++;
        }
        if (y >= 2) {
            System.err.println(y + ">=" + 2);
            nbrError++;
        }
        if (y == 2) {
            System.err.println(y + "==" + 2);
            nbrError++;
        }
        if (!(y != 2)) {
            System.err.println(y + "!=" + 2);
            nbrError++;
        }
        if (y < 2) {
            System.err.println(y + "<" + 2);
            nbrError++;
        }
        if (y <= 2) {
            System.err.println(y + "<=" + 2);
            nbrError++;
        }

        if (2 > y) {
            System.err.println(y + ">" + 2);
            nbrError++;
        }
        if (2 >= y) {
            System.err.println(y + ">=" + 2);
            nbrError++;
        }
        if (2 == y) {
            System.err.println(y + "==" + 2);
            nbrError++;
        }
        if (!(2 != y)) {
            System.err.println(y + "!=" + 2);
            nbrError++;
        }
        if (2 < y) {
            System.err.println(y + "<" + 2);
            nbrError++;
        }
        if (2 <= y) {
            System.err.println(y + "<=" + 2);
            nbrError++;
        }

        double j = Double.NaN;
        if (y > y) {
            System.err.println(y + ">" + y);
            nbrError++;
        }
        if (y >= y) {
            System.err.println(y + ">=" + y);
            nbrError++;
        }
        if (y == j) {
            System.err.println(y + "==" + j);
            nbrError++;
        }
        if (!(y != j)) {
            System.err.println(y + "!=" + j);
            nbrError++;
        }
        if (y < y) {
            System.err.println(y + "<" + y);
            nbrError++;
        }
        if (y <= y) {
            System.err.println(y + "<=" + y);
            nbrError++;
        }

        System.out.println("----------------------------------------");
        System.out.println(" End Comparison Test with " + nbrError + " error");
        System.out.println("----------------------------------------");
        return nbrError;
    }

    // ----------------------------------------------------
    // ---------- Chebfun TEST -------------------------
    // ----------------------------------------------------

    public static int runChebfunTest() {
        System.out.println("----------------------------------------");
        System.out.println(" Start Chebfun Test");
        System.out.println("----------------------------------------");
        int nbrError = 0;
        int[] fId={1, 3, 4, 5, 8, 10};
        double[] xs = new double[]{0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5,
                0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
        for (int i = 0; i < fId.length; i++) {
            try {
                nbrError += runFx(fId[i], xs[i]);
            } catch (InvocationTargetException | IllegalAccessException
                    | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        System.out.println("----------------------------------------");
        System.out.println(" End Chebfun Test with " + nbrError + " error");
        System.out.println("----------------------------------------");
        return nbrError;
    }

    // ----------------------------------------------------

    public static int runFx(int fx, double x) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        int nbrError = 0;

        Method f = methods.get("f" + fx);
        Method df = methods.get("df" + fx);

        if (f == null || df == null) {
            throw new NoSuchMethodException(fx + " not found...");
        }

        System.out.printf("Function %d\n", fx);
        double unknwon = COJAC_MAGIC_identityFct();
        double function = (double) f.invoke(ChebfunDemo.class, unknwon);
        double derivative = COJAC_MAGIC_derivative(function);

        double funcStdEval = (double) f.invoke(ChebfunDemo.class, x);
        double funcSymbEval = COJAC_MAGIC_evaluateAt(function, x);

        double derStdEval = (double) df.invoke(ChebfunDemo.class, x);
        double derSymbEval = COJAC_MAGIC_evaluateAt(derivative, x);

        System.out.printf("f%d(%s) = %s Standard Eval\n", fx, x, funcStdEval);
        System.out.printf("f%d(%s) = %s Chebfun Eval\n", fx, x, funcSymbEval);

        System.out.printf("df%d(%s) = %s Standard Eval\n", fx, x, derStdEval);
        System.out.printf("df%d(%s) = %s Chebfun Eval\n", fx, x, derSymbEval);

        if (relativeError(funcStdEval, funcSymbEval) > epsilon ||
                (Double.isNaN(funcStdEval) ^ Double.isNaN(funcSymbEval)))
            nbrError++;

        if (relativeError(derStdEval, derSymbEval) > epsilon ||
                (Double.isNaN(derStdEval) ^ Double.isNaN(derSymbEval)))
            nbrError++;

        if (nbrError > 0)
            System.err.println("Error at function" + fx);
        return nbrError;
    }

    // ----------------------------------------------------
    // ---------- TESTED FONCTIONS ------------------------
    // ----------------------------------------------------

    public static double f1(double x) {
        double j = 100;
        double i = 100 + j;
        return 4.0 * Math.pow(x, 3.0) + i;
    }

    public static double df1(double x) {
        return 12.0 * Math.pow(x, 2.0);
    }

    public static double f2(double x) {
        return Math.pow(x, x);
    }

    public static double df2(double x) {
        return Math.pow(x, x) * (Math.log(x) + 1.0);
    }

    public static double f3(double x) {
        return Math.sin(x) + 4.0 * x;
    }

    public static double df3(double x) {
        return Math.cos(x) + 4.0;
    }

    public static double f4(double x) {
        return Math.cos(Math.pow(x, 2.0)) - 4.0 * x + 3;
    }

    public static double df4(double x) {
        return -Math.sin(Math.pow(x, 2.0)) * 2.0 * x - 4.0;
    }

    public static double f5(double x) {
        return 4.0 * x + Math.tan(x);
    }

    public static double df5(double x) {
        return 1.0 + Math.pow(Math.tan(x), 2.0) + 4.0;
    }

    public static double f6(double x) {
        return 1 / Math.sqrt(x);
    }

    public static double df6(double x) {
        return -1.0 / (2.0 * Math.pow(x, 3.0 / 2.0));
    }

    public static double f7(double x) {
        return -Math.log(x);
    }

    public static double df7(double x) {
        return -1.0 / x;
    }

    public static double f8(double x) {
        return Math.sinh(x) + Math.cosh(x) + Math.tanh(x);
    }

    public static double df8(double x) {
        return Math.sinh(x) + Math.cosh(x) + 1.0 - Math.pow(Math.tanh(x), 2.0);
    }

    public static double f9(double x) {
        return Math.asin(x) + Math.acos(x) + Math.atan(x);
    }

    public static double df9(double x) {
        return 1.0 / (Math.pow(x, 2.0) + 1.0);
    }

    public static double f10(double x) {
        return Math.exp(Math.pow(x, 2.0));
    }

    public static double df10(double x) {
        return Math.exp(Math.pow(x, 2.0)) * 2.0 * x;
    }

    public static double f11(double x) {
        return 3.0 % x;
    }

    public static double df11(double x) {
        return Double.NaN;
    }

    public static double f12(double x) {
        return x % 3.0;
    }

    public static double df12(double x) {
        return Double.NaN;
    }

    public static double f13(double x) {
        return Math.abs(x);
    }

    public static double df13(double x) {
        return x < 0.0 ? -1.0 : 1.0;
    }

    // ----------------------------------------------------
    static double someFunction(double x, double a, double b) {
        double res = a * x * x;
        res = res * b + x;
        res = res * 1;
        return res;
    }

    // ----------------------------------------------------
    public static double relativeError(double a, double b) {
        return Math.abs(a - b) / a;
    }

    // ----------------------------------------------------

}
