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

/* 
 * To be run: 
 * - without Cojac:           java demo.HelloPolynomial1
 * - with Interval wrapper:   java -javaagent:cojac.jar="-Ri" demo.HelloPolynomial1
 * - with Stochastic wrapper: java -javaagent:cojac.jar="-Rs" demo.HelloPolynomial1
 * - with BigDecimal wrapper: java -javaagent:cojac.jar="-Rb 40" demo.HelloPolynomial1
 */

package demo;

public class HelloPolynomial1 {

    static double pow(double base, int exp) {
        return Math.pow(base, exp);
    }

    static double pow1(double base, int exp) {
        double r=1.0;
        while(exp-- > 0) r*=base;
        return r;
    }

    public static double somePolynomial(double x, double y) {
        return 1335.0*(pow(y, 6))/4.0 
                + x*x*(11.0*x*x*y*y -pow(y, 6) -121.0*pow(y, 4) -2.0)
                + 11.0*pow(y, 8)/2.0 
                + x/(2.0*y);
    }

    public static void main(String[] args) {
        double r, x, y;
        x=2.0; y=3.0;
        
        r=somePolynomial(x, y);
        System.out.println("f("+x+" , "+y+") = " + r);
        System.out.println();
        x=77617; y=33096;
        r=somePolynomial(x, y);
        System.out.println("f("+x+" , "+y+") = " + r);
    }
}
