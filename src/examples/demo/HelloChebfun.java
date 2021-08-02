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

// Run with "ChebFun" Wrapper: ... cojac.jar="-Rcheb"
// Run with "Symbolic" Wrapper: ... cojac.jar="-Rsymb"


package demo;

public class HelloChebfun {
    public static String COJAC_MAGIC_toString(double n) { return ""; }
    public static double COJAC_MAGIC_identityFct() { return 0.5; }
    public static double COJAC_MAGIC_evaluateAt(double d, double x) { return 0; }
    public static double COJAC_MAGIC_derivative(double d) { return d; }
    
    public static void main(String[] args) {
        double x=COJAC_MAGIC_identityFct(); // f(x)=x
        double a = 3.0, b = a + 1;
        double c = b * (a / (a - x));
        System.out.println(COJAC_MAGIC_toString(c));
        System.out.println(COJAC_MAGIC_evaluateAt(c, 0.5));
        System.out.println(c);
        c=COJAC_MAGIC_derivative(c);
        System.out.println(COJAC_MAGIC_evaluateAt(c, 0.5));
    }
}
