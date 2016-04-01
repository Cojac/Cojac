package com.github.cojac.models;

public class CheckedIntBehaviour {
    public CheckedIntBehaviour() {
        throw new AssertionError();
    }
    /*--------------------------INT--------------------------*/
    public static int IADD(int a, int b) {
        int r = a + b;

        if (((a ^ r) & (b ^ r)) < 0) {
            Reactions.react( "Overflow : IADD");
        }

        return r;
    }

    public static int ISUB(int a, int b) {
        int r = a - b;

        if (((a ^ b) & (a ^ r)) < 0) {
            Reactions.react( "Overflow : ISUB");
        }

        return r;
    }

    public static int IDIV(int a, int b) {
        int r = a / b;

        if (a == Integer.MIN_VALUE && b == -1) {
            Reactions.react( "Overflow : IDIV");
        }

        return r;
    }

    public static int IMUL(int a, int b) {
        int r = a * b;

        if (b == 0) {
            return r;
        }

        if (b == -1 && a == Integer.MIN_VALUE || r / b != a) {
            Reactions.react( "Overflow : IMUL");
        }

        return r;
    }

    public static int IINC(int a, int b) {
        int r = a + b;

        if (((a ^ r) & (b ^ r)) < 0) {
            Reactions.react( "Overflow : IINC");
        }

        return r;
    }

    public static int INEG(int a) {
        if (a == Integer.MIN_VALUE) {
            Reactions.react( "Overflow : INEG");
        }

        return -a;
    }
}
