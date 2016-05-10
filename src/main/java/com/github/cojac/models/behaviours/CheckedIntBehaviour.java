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
