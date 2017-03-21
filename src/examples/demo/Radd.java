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
 * To be run: with AutoDiff-backward wrapper:   
 *     java -javaagent:cojac.jar="-W cojac.WrapperRad" demo.Radd
 */

package demo;

public class Radd {

    static double pow(double base, int exp) {
        //if(true) return Math.pow(base, exp);
        double r=1.0;
        while(exp-- > 0) r*=base;
        return r;
    }

    public static double somePolynomial0(double x, double y) {
        return 3*x*x + 2*y;
//        System.out.println("... should be: ");
//        System.out.println("df/dx: "+ 6*x);
//        System.out.println("df/dy: "+ 2);
    }

    //derivative 1335.0*(y^6)/4.0  + x*x*(11.0*x*x*y*y -y^6 -121.0*y^4 -2.0) + 11.0*y^8/2.0 + x/(2.0*y)
    public static double somePolynomial(double x, double y) {
        return 1335.0*(pow(y, 6))/4.0 
                + x*x*(11.0*x*x*y*y -pow(y, 6) -121.0*pow(y, 4) -2.0)
                + 11.0*pow(y, 8)/2.0 
                + x/(2.0*y);
    }
    /* Partial derivatives at (x=2, y=3) with:
                              df/dx       df/dy
         CojacForwardAD:    -38954.0     8113580.0
         WolframAlpha:      -38959.5     525787.0
         CojacReverseAD:    -38959.83    525791.5
    */
    public static double COJAC_MAGIC_partialDerivativeIn(double a) { return 0;}
    public static void COJAC_MAGIC_computePartialDerivatives(double a) {}

    public static void main(String[] args) {
        double r, x, y;
        x=2.0; y=3.0; 
        r=somePolynomial(x, y);
        System.out.println("f(x,y): "+r);
        COJAC_MAGIC_computePartialDerivatives(r);
        
        System.out.println("df/dx: "+COJAC_MAGIC_partialDerivativeIn(x));
        System.out.println("df/dy: "+COJAC_MAGIC_partialDerivativeIn(y));
    }
}
//df/dx: 356207.9
//df/dy: -9380076.5
//f'(x,y): 356198.0
//f'(x,y): -1.13559756E8


