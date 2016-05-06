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

// compile this to a jar and launch it with the following command :
// java -javaagent:cojac.jar="-Ra"

package demo;



public class ChebfunDemo {

    // ----------------------------------------------------

    public static void main(String[] args) {
        smallTest();
    }

    // ----------------------------------------------------

    public static void smallTest() {
        double chebfun = COJAC_MAGIC_asChebfun(0.0);
//        chebfun*=50;
//        chebfun = Math.sin(chebfun);
        chebfun = Math.sin(chebfun);
        chebfun = COJAC_MAGIC_derivateChebfun(chebfun);
        chebfun = COJAC_MAGIC_derivateChebfun(chebfun);
        chebfun = COJAC_MAGIC_derivateChebfun(chebfun);
        chebfun = COJAC_MAGIC_derivateChebfun(chebfun);
        chebfun = COJAC_MAGIC_derivateChebfun(chebfun);
        chebfun = COJAC_MAGIC_derivateChebfun(chebfun);
        chebfun = COJAC_MAGIC_derivateChebfun(chebfun);
        chebfun = COJAC_MAGIC_derivateChebfun(chebfun);
        chebfun = COJAC_MAGIC_derivateChebfun(chebfun);

        
        double r = COJAC_MAGIC_evaluateChebfunAt(chebfun, 0.5123);
        System.out.printf("f(x) = %s should be (%s) \n", r, Math.cos(0.5123));
    }

    // ----------------------------------------------------
    // COJAC_MAGIC
    // ----------------------------------------------------
    public static String COJAC_MAGIC_toString(double n) {
        return "";
    }

    public static boolean COJAC_MAGIC_isChebfun(double a) {
        return false;
    }

    public static double COJAC_MAGIC_asChebfun(double a) {
        return a;
    }

    public static double COJAC_MAGIC_evaluateChebfunAt(double d, double x) {
        return d;
    }
    public static double COJAC_MAGIC_derivateChebfun(double d) {
        return d;
    }

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
        return 1.0;
    }

    public static double f13(double x) {
        return Math.abs(x);
    }

    public static double df13(double x) {
        return x < 0.0 ? -1.0 : 1.0;
    }

    public static double f14(double x) {
        double i = 1e16;
        i += 2;
        // i += 1e-16;
        i += -1e16;
        // i += 1e-16;

        return i;
    }

    public static double df14(double x) {
        return x < 0.0 ? -1.0 : 1.0;
    }

    public static double f15(double x) {
        double a = 0.08, b = 0.0491, c = 0.3218;
        return a + b + c;
    }

    public static double df15(double x) {
        return x < 0.0 ? -1.0 : 1.0;
    }

}
