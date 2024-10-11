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

package com.github.cojac.perfs.opcodes;

import java.util.concurrent.Callable;

public class FREMCallable implements Callable<Float> {
    private float a = 1.0F;
    private float b = 2.0F;
    private float c = 3.0F;

    @Override
    public Float call() {
        return a % b % c % b % a % b % c % b % a % b % c % a % b % c % b % a % b % c % b % a % b %
            a % b % c % b % a % b % c % b % a % b % c % a % b % c % b % a % b % c % b % a % b %
            a % b % c % b % a % b % c % b % a % b % c;
    }

    public void setA(float a) {
        this.a = a;
    }

    public void setB(float b) {
        this.b = b;
    }

    public void setC(float c) {
        this.c = c;
    }
}