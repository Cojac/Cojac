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
 * These tests should work with real processor rounding mode 
 * Behaviour: native rounding. (option -Rnu)
 * 
 * */
public class NativeRoundingTests {

    /*
     * This test checks that the processor rounds effectively up for an addition of two positive numbers.
     * return 1.0 under "roundToNearest" mode, nextUp(1.0) = 1.0000000000000002 with "toward infinity" mode
     */
    public double testAddition1() {
        double a = 1.0;
        double b = 1E-16;
        return a+b;
    }
    /*
     * This test checks that the processor rounds effectively up for an addition of a positive 
     * and a negative number.
     * return -1.0 under "roundToNearest" mode, nextUp(-1.0) = -0.9999999999999999 with "toward infinity" mode
     */
    public double testAddition2() {
        double a = -1.0;
        double b = 1E-16;
        return a+b;
    }
    /*
     * This test checks that the processor rounds effectively up for a subtraction of two positive numbers.
     * return nextDown(1.0) = 0.9999999999999999 under "roundToNearest" mode, 1.0 with "toward infinity" mode
     */
    public double testAddition3(){
        double a = 1.0;
        double b = -1E-16;
        return a+b;
    }
    
    /*
     * This test should check the precision difference between float and double in a substraction
     * return should be -0.9f when instrumented 
     */
    public double testMultiplication(){
        double a = 0.1;
        double b = 1;
        return a-b;
    }
    /*
     * This test should check the precision difference between float and double in a multiplication
     * return should be 1.1f when instrumented 
     */
    public double testDivision(){
        double a = 0.05;
        double b = 2;
        return a*b;
    }
}