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
package com.github.cojac.unit;

/*
 * These tests should work with the double to float instrumenter. (option -BD2F)
 * 
 * */
public class Double2FloatTests {

    /*
     * This test checks that the call to Math.nextUp(double) is correctly redirected to Math.nextUp(float)
     * Return should be Math.nextUp(3.0f) when instrumented
     */
    public double testNextUp() {
        double a = Math.nextUp(3.0);
        return a;
    }
    /*
     * This test should check the precision difference between float and double in an addition
     * return should be 1.1f when instrumented
     */
    public double testPrecisionAdd(){
        double a = 0.1;
        double b = 1;
        return a+b;
    }
    /*
     * This test should check the precision difference between float and double in a substraction
     * return should be -0.9f when instrumented 
     */
    public double testPrecisionSub(){
        double a = 0.1;
        double b = 1;
        return a-b;
    }
    /*
     * This test should check the precision difference between float and double in a multiplication
     * return should be 1.1f when instrumented 
     */
    public double testPrecisionMul(){
        double a = 0.05;
        double b = 2;
        return a*b;
    }
    /*
     * This test should check the precision difference between float and double in a division
     * return should be 10f when instrumented 
     */
    public double testPrecisionDiv(){
        double a = 1;
        double b = 0.1;
        return a/b;
    }
    
}