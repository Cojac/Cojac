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

package com.github.cojac.models;

public final class CheckedCasts {
    private CheckedCasts() {
        throw new AssertionError();
    }

    public static int checkedL2I(long a, int reaction, String logFileName) {
        if (a < Integer.MIN_VALUE || a > Integer.MAX_VALUE) {
            Reactions.react(reaction, "Overflow : L2I", logFileName);
        }

        return (int) a;
    }

    public static short checkedI2S(int a, int reaction, String logFileName) {
        if (a < Short.MIN_VALUE || a > Short.MAX_VALUE) {
            Reactions.react(reaction, "Overflow : I2S", logFileName);
        }

        return (short) a;
    }

    public static char checkedI2C(int a, int reaction, String logFileName) {
        if (a < Character.MIN_VALUE || a > Character.MAX_VALUE) {
            Reactions.react(reaction, "Overflow : I2C", logFileName);
        }

        return (char) a;
    }

    public static byte checkedI2B(int a, int reaction, String logFileName) {
        if (a < Byte.MIN_VALUE || a > Byte.MAX_VALUE) {
            Reactions.react(reaction, "Overflow : I2B", logFileName);
        }

        return (byte) a;
    }

    public static int checkedD2I(double a, int reaction, String logFileName) {
        if (!(a >= Integer.MIN_VALUE && a <= Integer.MAX_VALUE)) {  // NaN too...
            Reactions.react(reaction, "Overflow : D2I", logFileName);
        }

        return (int) a;
    }

    public static long checkedD2L(double a, int reaction, String logFileName) {
        if (!(a >= Long.MIN_VALUE && a <= Long.MAX_VALUE)) {  // NaN too...
            Reactions.react(reaction, "Overflow : D2L", logFileName);
        }

        return (long) a;
    }

    public static int checkedF2I(float a, int reaction, String logFileName) {
        if (!(a >= Integer.MIN_VALUE && a <= Integer.MAX_VALUE)) {  // NaN too...
            Reactions.react(reaction, "Overflow : F2I", logFileName);
        }

        return (int) a;
    }

    public static long checkedF2L(float a, int reaction, String logFileName) {
        if (!(a >= Long.MIN_VALUE && a <= Long.MAX_VALUE)) {  // NaN too...
            Reactions.react(reaction, "Overflow : F2L", logFileName);
        }

        return (long) a;
    }

    public static float checkedD2F(double a, int reaction, String logFileName) {
        if (a < -Float.MAX_VALUE || a > Float.MAX_VALUE) {
            Reactions.react(reaction, "Overflow : D2F", logFileName);
        }

        return (float) a;
    }
    
    public static float checkedI2F(int a, int reaction, String logFileName) {
        if ((int)((float)a) != a) {
            Reactions.react(reaction, "Loss of precision : I2F", logFileName);
        }

        return (float) a;
    }
    
    public static double checkedL2D(long a, int reaction, String logFileName) {
        if ((long)((double)a) != a) {
            Reactions.react(reaction, "Loss of precision : L2D", logFileName);
        }

        return (double) a;
    }



    
}