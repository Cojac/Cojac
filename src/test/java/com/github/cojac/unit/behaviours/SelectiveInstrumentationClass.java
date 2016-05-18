/*
 * *
 *    Copyright 2011-2016 Valentin Gazzola & Frédéric Bapst
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
package com.github.cojac.unit.behaviours;

/*
 * These tests are for Selective instrumentation.
 * Result vary with option.
 * 
 * */
public class SelectiveInstrumentationClass {
    double x =Math.PI;
    double y = Math.E;
    public double[] method0() {
        double[] d= new double[2];
        d[0] = x+y;
        d[1] = x+y;
        return d;
    }
    public double[] method1() {
        double[] d= new double[2];
        d[0] = x+y;
        d[1] = x+y;
        return d;
    }
    public double[] method2() {
        double[] d= new double[2];
        d[0] = x+y;
        d[1] = x+y;
        return d;
    }
    public double[] method3() {
        double[] d= new double[2];
        d[0] = x+y;
        d[1] = x+/*DADD:op 34*/y;
        return d;
    }
    public double[] method4() {
        double[] d= new double[2];
        d[0] = x+y;
        d[1] = x+y;
        return d;
    }
}