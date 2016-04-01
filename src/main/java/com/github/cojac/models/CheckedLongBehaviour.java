package com.github.cojac.models;

public class CheckedLongBehaviour {
    public CheckedLongBehaviour() {
        throw new AssertionError();
    }
 /*--------------------------LONG--------------------------*/
    
    public static long LADD(long a, long b) {
        long r = a + b;

        if (((a ^ r) & (b ^ r)) < 0) {
            Reactions.react( "Overflow : LADD");
        }

        return r;
    }

    public static long LSUB(long a, long b) {
        long r = a - b;

        if (((a ^ b) & (a ^ r)) < 0) {
            Reactions.react( "Overflow : LSUB");
        }

        return r;
    }

    public static long LDIV(long a, long b) {
        long r = a / b;

        if (a == Long.MIN_VALUE && b == -1) {
            Reactions.react( "Overflow : LDIV");
        }

        return r;
    }

    public static long LMUL(long a, long b) {
        long r = a * b;

        if (b == 0) {
            return r;
        }

        if (b == -1 && a == Long.MIN_VALUE || r / b != a) {
            Reactions.react( "Overflow : LMUL");
        }

        return r;
    }

    public static long LNEG(long a) {
        if (a == Long.MIN_VALUE) {
            Reactions.react( "Overflow : LNEG");
        }

        return -a;
    }
}
