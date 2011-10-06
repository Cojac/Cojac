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

package ch.eiafr.cojac.perfs.opcodes;

import java.util.concurrent.Callable;

public class DADDCallable implements Callable<Double> {
    private double a = 1.0D;
    private double b = 2.0D;
    private double c = 3.0D;

    @Override
    public Double call() {
        return a + b + c + b + a + b + c + b + a + b + c + a + b + c + b + a + b + c + b + a + b +
            a + b + c + b + a + b + c + b + a + b + c + a + b + c + b + a + b + c + b + a + b +
            a + b + c + b + a + b + c + b + a + b + c;
    }

    public void setA(double a) {
        this.a = a;
    }

    public void setB(double b) {
        this.b = b;
    }

    public void setC(double c) {
        this.c = c;
    }
}