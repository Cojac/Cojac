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
 * To be run: with AutoDiff-reverse wrapper:   
 *     java -javaagent:cojac.jar="-Ra" demo.HelloAutodiffForward
 */

package demo;

public class HelloAutodiffForward {
    public static double COJAC_MAGIC_derivative(double a) { return 0; }
    public static double COJAC_MAGIC_asDerivativeVar(double a) { return a; }

    static double someFunction(double x, double a, double b) {
        double res = a * x * x;  // the computation can be complex,
        res = res + b * x;       // with loops, calls, recursion etc.
        res = res + 1;
        return res;     // f: a X^2 + b X + 1        f': 2aX + b
    }

    public static void main(String[] args) {
        double x = 2;
        x = COJAC_MAGIC_asDerivativeVar(x);  // we'll compute df/dx
        double y = someFunction(x, 3, 4);
        System.out.println("f(2):  " + y);
        System.out.println("f'(2): " + COJAC_MAGIC_derivative(y));
    }
}
