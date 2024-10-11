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
 *     java -javaagent:cojac.jar="-Rar" demo.HelloAutodiffReverse
 */

package demo;

public class HelloAutodiffReverse {

    public static double COJAC_MAGIC_partialDerivativeIn(double a) { return 0;}
    public static void   COJAC_MAGIC_computePartialDerivatives(double a) {}
    public static void COJAC_MAGIC_resetPartialDerivatives(double d) {}

    public static double somePolynomial(double x, double y) {
        return 3*x*x + 2*y;
//        System.out.println("... should be: ");
//        System.out.println("df/dx: "+ 6*x);
//        System.out.println("df/dy: "+ 2);
    }

    public static void main(String[] args) {
        double r, x, y;
        x = 2.0; y = 3.0;
        r=somePolynomial(x, y);
        System.out.println("f(x,y): " + r);
        COJAC_MAGIC_computePartialDerivatives(r);
        
        System.out.println("df/dx: " + COJAC_MAGIC_partialDerivativeIn(x));
        System.out.println("df/dy: " + COJAC_MAGIC_partialDerivativeIn(y));
        
        COJAC_MAGIC_resetPartialDerivatives(r);
        COJAC_MAGIC_computePartialDerivatives(r);
        System.out.println("df/dx: " + COJAC_MAGIC_partialDerivativeIn(x));
        
        COJAC_MAGIC_resetPartialDerivatives(r);
        COJAC_MAGIC_computePartialDerivatives(r);
        System.out.println("df/dx: " + COJAC_MAGIC_partialDerivativeIn(x));

    }
}
