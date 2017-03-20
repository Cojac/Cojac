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
//    java -javaagent:cojac.jar="-Ri"     or
//    java -javaagent:cojac.jar="-Rs"
// An additional possible option: "-RunstableAt 1E-5"

package com.github.cojac.misctests;

public class StochasticOrIntervalDemo {
    public static void main(String[] args) {
        runDoubleTest();
        runFloatTest();
    }

    public static double rumpPolynom(double x, double y) {
        double res = 1335.0;
        res = res * Math.pow(y, 6.0);
        res /= 4.0;
        double tmp = x * x;
        double tmp2 = 11.0 * tmp;
        tmp2 *= y * y;
        tmp2 -= Math.pow(y, 6.0);
        tmp2 -= 121.0 * Math.pow(y, 4.0);
        tmp2 -= 2.0;
        tmp *= tmp2;
        res += tmp;
        tmp = 11.0 * Math.pow(y, 8.0);
        tmp /= 2.0;
        System.out.println("res = " + COJAC_MAGIC_toString(res));
        System.out.println("tmp = " + COJAC_MAGIC_toString(tmp));
        res += tmp;
        System.out.println("res = " + COJAC_MAGIC_toString(res));
        res += x / (2.0 * y);
        return res;
    }

    public static float rumpPolynom(float x, float y) {
        return 1335.0F * ((float) Math.pow(y, 6.0F)) / 4.0F +
                x * x * (11F * x * x * y * y - (float) Math.pow(y, 6.0F) -
                        121.0F * (float) Math.pow(y, 4.0F) - 2.0F) +
                11.0F * (float) Math.pow(y, 8F) / 2.0F + x / (2.0F * y);
    }

    public static double r(double x) {
        return x - (11184811.0 / 33554432.0) * Math.pow(x, 3.0) -
                (13421773.0 / 67108864.0) * Math.pow(x, 5.0);
    }

    public static void runDoubleTest() {
        System.out.println("\n\n--- Run stochastic and interval demonstration for double ---");
        DoubleRump();
        DoublePI();
    }

    public static void DoubleRump() {
        System.out.println("\n--- Test rump Polynom");
        double a = rumpPolynom(77617.0, 33096.0);
        System.out.println("Relative error rump : " +
                COJAC_MAGIC_relativeError(a));
        System.out.println();
    }

    public static void DoublePI() {
        System.out.println("\n--- Test PI");
        double pi;
        System.out.println(pi = computePiD());
        System.out.println(COJAC_MAGIC_toString(pi));
        System.out.println("Pi relative error : " +
                COJAC_MAGIC_relativeError(pi));
    }

    public static void runFloatTest() {
        System.out.println("\n\n--- Run stochastic and interval demonstration for float ---");
        FloatRump();
        FloatPi();
    }

    public static void FloatRump() {
        System.out.println("\n--- Test rump Polynom");
        float a = rumpPolynom(77617.0F, 33096.0F);
        System.out.println("Relative error rump : " +
                COJAC_MAGIC_relativeError(a));
        System.out.println();
    }

    public static void FloatPi() {
        System.out.println("\n--- Test PI");
        float pi;
        System.out.println(pi = computePiF());
        System.out.println(COJAC_MAGIC_toString(pi));
        System.out.println("Pi relative error : " +
                COJAC_MAGIC_relativeError(pi));
    }

    public static double computePiD() {
        double res = 0.0;
        int numerator = 1;
        double tmp = 4.0;
        boolean alt = true;

        while (tmp > 0.1E-5) {
            if (alt) {
                res += tmp;
                numerator += 2;
                tmp = 4.0 / numerator;
                alt = false;
            } else {
                res -= tmp;
                numerator += 2;
                tmp = 4.0 / numerator;
                alt = true;
            }
        }
        return res;
    }

    public static float computePiF() {
        System.out.println("Start computation of pi");
        float res = 0.0F;
        int numerator = 1;
        float tmp = 4.0F;
        boolean alt = true;

        while (tmp > 0.1E-5) {
            if (alt) {
                res += tmp;
                numerator += 2;
                tmp = 4.0F / numerator;
                alt = false;
            } else {
                res -= tmp;
                numerator += 2;
                tmp = 4.0F / numerator;
                alt = true;
            }
        }
        return res;
    }

    public static double[] resolve2ndDegreeEquation(double a, double b, double c) {
        double x1 = (-b + Math.sqrt(b * b - 4.0 * a * c)) / (2 * a);
        double x2 = (-b - Math.sqrt(b * b - 4.0 * a * c)) / (2 * a);
        return new double[]{x1, x2};
    }

    public static String COJAC_MAGIC_toString(double a) {
        return Double.toString(a);
    }

    public static double COJAC_MAGIC_relativeError(double n) {
        return 1.0;
    }

    public static double COJAC_MAGIC_relativeError(float n) {
        return 1.0F;
    }

    public static String COJAC_MAGIC_toString(float n) {
        return "";
    }
}
