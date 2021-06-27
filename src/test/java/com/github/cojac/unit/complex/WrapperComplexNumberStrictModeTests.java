/*
 * *
 *    Copyright 2011-2016 Valentin Gazzola & Frédéric Bapst
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
package com.github.cojac.unit.complex;

/*
 * These tests should work with the double to float instrumenter. (option -Bdaf)
 *
 * */
public class WrapperComplexNumberStrictModeTests {

    public static double COJAC_MAGIC_getReal(double d) {
        return d;
    }

    public static double COJAC_MAGIC_getImaginary(double d) {
        return 0;
    }

    public static boolean COJAC_MAGIC_equals(double a, double b) {
        return a == b;
    }

    /**
     * Should return -4
     */
    public double testSqrt() {
        double a = Math.sqrt(-4);
        return a * a;
    }

    /**
     * Should return 2.5 (= Math.hypot(2, -1.5))
     */
    public double testAbs() {
        double a = Math.sqrt(-4);
        return Math.abs(a - 1.5);
    }


    /**
     * Should return 2
     */
    public double testParseDouble_Real() {
        return COJAC_MAGIC_getReal(Double.parseDouble("2 - 5.5i"));
    }

    /**
     * Should return -5.5
     */
    public double testParseDouble_Imaginary() {
        return COJAC_MAGIC_getImaginary(Double.parseDouble("2 - 5.5i"));
    }

    /**
     * Should return 2
     */
    public double testParseFloat_Real() {
        return COJAC_MAGIC_getReal(Float.parseFloat("2 - 5.5i"));
    }

    /**
     * Should return -5.5
     */
    public double testParseFloat_Imaginary() {
        return COJAC_MAGIC_getImaginary(Float.parseFloat("2 - 5.5i"));
    }

    /**
     * Should return False
     */
    public boolean testRealSmaller() {
        double a = -0.5;
        double b = -2;
        return a < b;
    }

    /**
     * Should return True
     */
    public boolean testRealEqual() {
        double a = -2;
        double b = -2;
        return a == b;
    }

    /**
     * Should return True
     */
    public boolean testRealNotEqual() {
        double a = -2;
        double b = 4;
        return a != b;
    }

    /**
     * Should return True
     */
    public boolean testComplexEqual1() {
        double i = Math.sqrt(-1);
        double a = -2 + i;
        double b = -2 + i;
        return a == b;
    }

    /**
     * Should return True
     */
    public boolean testComplexEqual2() {
        double i = Math.sqrt(-1);
        double a = -2 + i;
        double b = -2 + i;
        return COJAC_MAGIC_equals(a, b);
    }

    /**
     * Should return False
     */
    public boolean testComplexNotEqual() {
        double i = Math.sqrt(-1);
        double a = -2 + i;
        double b = -2 + 2 * i;
        return COJAC_MAGIC_equals(a, b);
    }

    /**
     * Should return an ArithmeticException
     */
    public Exception testComplexGreater() {
        double i = Math.sqrt(-1);
        double a = 1 - i;
        double b = 1 - 2 * i;
        try {
            boolean c = a > b;
        } catch (Exception e) {
            return e;
        }
        return null;
    }


    /**
     * Should return an ArithmeticException
     */
    public Exception testToDouble() {
        double a = Math.sqrt(-28) - (-6);
        try {
            int b = (int) a;
        } catch (Exception e) {
            return e;
        }
        return null;
    }
}