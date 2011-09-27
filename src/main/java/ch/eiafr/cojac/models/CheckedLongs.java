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

package ch.eiafr.cojac.models;

public final class CheckedLongs {
    private CheckedLongs() {
        throw new AssertionError();
    }

    public static long checkedLADD(long a, long b, int reaction, String logFileName) {
        long r = a + b;

        if (((a ^ r) & (b ^ r)) < 0) {
            Reactions.react(reaction, "Overflow : LADD", logFileName);
        }

        return r;
    }

    public static long checkedLSUB(long a, long b, int reaction, String logFileName) {
        long r = a - b;

        if (((a ^ b) & (a ^ r)) < 0) {
            Reactions.react(reaction, "Overflow : LSUB", logFileName);
        }

        return r;
    }

    public static long checkedLDIV(long a, long b, int reaction, String logFileName) {
        long r = a / b;

        if (a == Long.MIN_VALUE && b == -1) {
            Reactions.react(reaction, "Overflow : LDIV", logFileName);
        }

        return r;
    }

    public static long checkedLMUL(long a, long b, int reaction, String logFileName) {
        long r = a * b;

        if (b == 0) {
            return r;
        }

        if (b == -1 && a == Long.MIN_VALUE || r / b != a) {
            Reactions.react(reaction, "Overflow : LMUL", logFileName);
        }

        return r;
    }

    public static long checkedLNEG(long a, int reaction, String logFileName) {
        if (a == Long.MIN_VALUE) {
            Reactions.react(reaction, "Overflow : LNEG", logFileName);
        }

        return -a;
    }
}