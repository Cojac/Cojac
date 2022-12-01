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

///*01*/package demo;
///*02*/public class SimpleExample {
///*03*/  public static void main(String[] args) {
///*04*/    double a = 1e8;
///*05*/    double b = 1;
///*06*/    double c = b + 2;
///*07*/    double d = a + b;
///*08*/    double e = d - a;
///*09*/    System.out.println(" c -> " + c +" should be 3.0 ");
///*10*/    System.out.println(" e -> " + e +" should be 1.0 ");
///*11*/  }
///*12*/}

package com.github.cojac.misctests.deltaDebugging;

import java.math.BigDecimal;

public class SimpleExample {
    public static void main(String[] args) {
        double a = 1e8;
        double b = 1;
        double c = b + 2;
        double d = a + b;
        double e = d - a;

        System.out.println(" c -> " + c + " should be 3.0 ");
        System.out.println(" e -> " + e + " should be 1.0 ");
        // --------------------------------------------------
        // Validation
        // --------------------------------------------------
        if (!(new BigDecimal(d).equals(new BigDecimal("3.0"))))
            System.exit(-1);
        if (!(new BigDecimal(e).equals(new BigDecimal("1.0"))))
            System.exit(-1);
        else
            System.exit(0);
    }
}

/// *01*/package demo;
/// *02*/public class SimpleExample {
/// *03*/ public static void main(String[] args) {
/// *04*/ float a = 1e8f; //op. 3
/// *05*/ float b = 1; //op. 4
/// *06*/ float c = b + 2; //op. 5, 6
/// *07*/ double d = (double)a + (double)b; //op. 7
/// *08*/ float e = (float)((double)d - (double)a);//op. 8
/// *09*/ System.out.println(" c -> " + c +" should be 3.0 ");
/// *10*/ System.out.println(" d -> " + e +" should be 1.0 ");
/// *11*/ }
/// *12*/}

//// double e = com.github.cojac.models.behaviours.ConversionBehaviour.DSUB(c,
//// a);