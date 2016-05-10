/*
 * *
 *    Copyright 2011-2016 Baptiste Wicht & Frédéric Bapst & Valentin Gazzola
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
package com.github.cojac.models.behaviours;

import com.github.cojac.models.Reactions;

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
