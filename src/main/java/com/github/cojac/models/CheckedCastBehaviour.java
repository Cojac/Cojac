package com.github.cojac.models;

public class CheckedCastBehaviour {
/*--------------------------CASTING--------------------------*/
    public CheckedCastBehaviour() {
        throw new AssertionError();
    }
    public static int L2I(long a) {
        if (a < Integer.MIN_VALUE || a > Integer.MAX_VALUE) {
            Reactions.react( "Overflow : L2I");
        }

        return (int) a;
    }

    public static int I2S(int a) {
        if (a < Short.MIN_VALUE || a > Short.MAX_VALUE) {
            Reactions.react( "Overflow : I2S");
        }

        return (short) a;
    }

    public static int I2C(int a) {
        if (a < Character.MIN_VALUE || a > Character.MAX_VALUE) {
            Reactions.react( "Overflow : I2C");
        }

        return (char) a;
    }

    public static int I2B(int a) {
        if (a < Byte.MIN_VALUE || a > Byte.MAX_VALUE) {
            Reactions.react( "Overflow : I2B");
        }

        return (byte) a;
    }

    public static int D2I(double a) {
        if (!(a >= Integer.MIN_VALUE && a <= Integer.MAX_VALUE)) {  // NaN too...
            Reactions.react( "Overflow : D2I");
        }

        return (int) a;
    }

    public static long D2L(double a) {
        if (!(a >= Long.MIN_VALUE && a <= Long.MAX_VALUE)) {  // NaN too...
            Reactions.react( "Overflow : D2L");
        }

        return (long) a;
    }

    public static int F2I(float a) {
        if (!(a >= Integer.MIN_VALUE && a <= Integer.MAX_VALUE)) {  // NaN too...
            Reactions.react( "Overflow : F2I");
        }

        return (int) a;
    }

    public static long F2L(float a) {
        if (!(a >= Long.MIN_VALUE && a <= Long.MAX_VALUE)) {  // NaN too...
            Reactions.react( "Overflow : F2L");
        }

        return (long) a;
    }

    public static float D2F(double a) {
        if (a < -Float.MAX_VALUE || a > Float.MAX_VALUE) {
            Reactions.react( "Overflow : D2F");
        }

        return (float) a;
    }
    
    public static float I2F(int a) {
        if ((int)((float)a) != a) {
            Reactions.react( "Loss of precision : I2F");
        }

        return (float) a;
    }
    
    public static double L2D(long a) {
        if ((long)((double)a) != a) {
            Reactions.react( "Loss of precision : L2D");
        }

        return (double) a;
    }
}
