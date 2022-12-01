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
package com.github.cojac.unit.posit;

/*
 * These tests should work with the double to float instrumenter. (option -Bdaf)
 *
 * */
public class WrapperPosit32Tests {

    public float testAdd() {
        return 10E8f + 10E-5f;
    }

    public float testSub() {
        return 10E8f - 10E-5f;
    }

    public float testMul() {
        return 10E8f * 10E-5f;
    }

    public float testDiv() {
        return 10E3f / 10E-5f;
    }

    public float testFma() {
        return Math.fma(31E2f, 235E-3f, 65E4f);
    }

    /**
     * Should return true
     */
    public boolean testEquality(){
        float a = -864.352f;
        return a == a;
    }

    /**
     * Should return false
     */
    public boolean testInequality(){
        float a = -864.352f;
        return a != a;
    }

    /**
     * Should return true
     */
    public boolean testComparisonEquality(){
        float a = -84.352f;
        return a <= a;
    }

    /**
     * Should return true
     */
    public boolean testComparison(){
        float a = -84.352f;
        float b = -12.2f;
        return a <= b;
    }
}