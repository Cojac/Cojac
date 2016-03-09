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

package com.github.cojac.perfs;

import java.util.Random;
import java.util.concurrent.Callable;

public class StringSearchingRunnable implements Callable<Integer> {
    private final String fullString;
    private final String searchString;

    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz";

    static final int HASHER = 301;
    static final int BASE = 256;

    public StringSearchingRunnable() {
        super();

        fullString = newRandomString(10000);
        searchString = newRandomString(5);
    }

    private static String newRandomString(int size) {
        char[] val = new char[size];

        Random random = new Random();

        for (int i = 0; i < size; i++) {
            val[i] = CHARS.charAt(random.nextInt(CHARS.length()));
        }

        return new String(val);
    }

    private static int firstFootprint(CharSequence s, int len) {
        int footPrint = 0;

        for (int i = len - 1; i >= 0; i--) {
            footPrint = (footPrint + s.charAt(i) * powerModulo(BASE, len - i - 1, HASHER)) % HASHER;
        }

        return footPrint;
    }

    private static int nextFootprint(int previousFootPrint, char dropChar, char newChar, int coef) {
        int footPrint = previousFootPrint * BASE % HASHER;

        footPrint = (footPrint - dropChar * coef) % HASHER;
        footPrint = (footPrint + HASHER) % HASHER;
        footPrint = (footPrint + newChar) % HASHER;

        return footPrint;
    }

    public static int indexOf_RabinKarp(String t, String p) {
        int pLength = p.length();
        int tLength = t.length();

        if (tLength < pLength) {
            return -1;
        }

        int searched = firstFootprint(p, pLength);
        int currentFootPrint = firstFootprint(t, pLength);
        int coef = powerModulo(BASE, pLength, HASHER);

        for (int i = 0; i <= tLength - pLength; i++) {
            if (currentFootPrint == searched && p.equals(t.substring(i, i + pLength))) {
                return i;
            }

            if (i == tLength - pLength) {
                break;
            }

            currentFootPrint = nextFootprint(currentFootPrint, t.charAt(i), t.charAt(pLength + i), coef);
        }

        return -1;
    }

    public static int powerModulo(int base, int exp, int mod) {
        if (exp == 0) {
            return 1;
        }

        int tmp = powerModulo(base * base % mod, exp / 2, mod);

        if (exp % 2 != 0) {
            tmp = tmp * base % mod;
        }

        return tmp;
    }

    @Override
    public Integer call() {
        return indexOf_RabinKarp(fullString, searchString);
    }
}
