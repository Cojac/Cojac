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

public final class CheckedInts {
    private CheckedInts() {
        throw new AssertionError();
    }

    public static int checkedIADD(int a, int b, int reaction, String logFileName) {
        int r = a + b;

        if (((a ^ r) & (b ^ r)) < 0) {
            Reactions.react(reaction, "Overflow : IADD", logFileName);
        }

        return r;
    }

    public static int checkedISUB(int a, int b, int reaction, String logFileName) {
        int r = a - b;

        if (((a ^ b) & (a ^ r)) < 0) {
            Reactions.react(reaction, "Overflow : ISUB", logFileName);
        }

        return r;
    }

    public static int checkedIDIV(int a, int b, int reaction, String logFileName) {
        int r = a / b;

        if (a == Integer.MIN_VALUE && b == -1) {
            Reactions.react(reaction, "Overflow : IDIV", logFileName);
        }

        return r;
    }

    public static int checkedIMUL(int a, int b, int reaction, String logFileName) {
        int r = a * b;

        if (b == 0) {
            return r;
        }

        if (b == -1 && a == Integer.MIN_VALUE || r / b != a) {
            Reactions.react(reaction, "Overflow : IMUL", logFileName);
        }

        return r;
    }

    public static int checkedIINC(int a, int b, int reaction, String logFileName) {
        int r = a + b;

        if (((a ^ r) & (b ^ r)) < 0) {
            Reactions.react(reaction, "Overflow : IINC", logFileName);
        }

        return r;
    }

    public static int checkedINEG(int a, int reaction, String logFileName) {
        if (a == Integer.MIN_VALUE) {
            Reactions.react(reaction, "Overflow : INEG", logFileName);
        }

        return -a;
    }
}
