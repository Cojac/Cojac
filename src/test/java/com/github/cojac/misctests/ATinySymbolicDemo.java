/*
 *    Copyright 2017 Frédéric Bapst et al.
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
 */

// Run with "Symbolic" Wrapper: ... cojac.jar="-Rsymb"

package com.github.cojac.misctests;

import java.math.BigDecimal;

public class ATinySymbolicDemo {

    public static String COJAC_MAGIC_toString(double n) {
        return "";
    }
    public static double COJAC_MAGIC_identityFct() {
        return 0.0;
    }
    public static double COJAC_MAGIC_evaluateAt(double d, double x) {
        return d;
    }
    public static double COJAC_MAGIC_derivative(double d) {
        return d;
    }
    public static void COJAC_MAGIC_setSymbolicEvaluationMode(boolean smartMode) {}

    // f(x) = 3x^2 + 2x + 5
    static double myFunction(double x) {
        double res = 3 * x * x; 
        res = res + 2 * x; 
        res = res + 5;
        return res; 
    }
    
    // f(x) = ...
    static double myFunction2(double x, int n) {
        double res=0.0;
        for(int i=0; i<n; i++) 
            res += Math.sin(x*i-x*x);
        return res; 
    }


    public static void main(String[] args) {
        double x = COJAC_MAGIC_identityFct();    // define the unknown
        double f = myFunction(x);                      // define the function
        double df = COJAC_MAGIC_derivative(f);   // compute the derivative
        System.out.println("f(x)  = " + COJAC_MAGIC_toString(f));  // print the function "f"
        System.out.println("f'(x) = " + COJAC_MAGIC_toString(df)); // print the derivative of "f"
        System.out.println("f(2)  = " + COJAC_MAGIC_evaluateAt(f, 2)); // compute the result of the function
        System.out.println("f'(2) = " + COJAC_MAGIC_evaluateAt(df, 2));// compute the result of the derivative
        
        double[] t = {+2, 1E-16, 1E-16, 1E-16, 5E-17, 5E-17, +5, -3, -2};
        double sum = 0;
        for(double e:t) sum += e;
        System.out.println("sum  = " + COJAC_MAGIC_evaluateAt(sum, 0.0));      // compute the sum (smart sum-of-floats)
        COJAC_MAGIC_setSymbolicEvaluationMode(false);
        System.out.println("sum  = " + COJAC_MAGIC_evaluateAt(sum, 0.0));      // compute the sum (naive sum-of-floats)
        double g = 2*x;
        g = COJAC_MAGIC_derivative(g);
        System.out.println(COJAC_MAGIC_toString(g));
        BigDecimal b = new BigDecimal(""+0.08);
        BigDecimal c = new BigDecimal(""+0.0491);
        BigDecimal d = new BigDecimal(""+0.3218);
        BigDecimal r = b.add(c).add(d);
        System.out.println(r);
        f=myFunction2(3.0, 5);
        System.out.println("f(x)  = " + COJAC_MAGIC_toString(f));  // print the function "f"
    }
    
}
