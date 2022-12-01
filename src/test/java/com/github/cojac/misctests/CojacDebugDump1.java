/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst & HEIA-FR students
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

package com.github.cojac.misctests;

public class CojacDebugDump1 {

    static Double testDoubleWrapper(Double a, Double b){
        Double d1 = 1.5;
        Double d2 = Double.valueOf(d1);
        d1.compareTo(d2);
        return a;
    }
    
    public static void go() {
        Double fw1 = 8.6;
        Double fwres = testDoubleWrapper(fw1, fw1);
        System.out.println(fwres);
    }
    
    public static void main(String... args) {
        go();
    }

    public static void main1(String[] args) {        
        try {
          f();
          Object f="";
          ((Float)f).floatValue();
        } catch(ClassCastException | NoSuchMethodError | AbstractMethodError e) {
            System.out.println(e);
        }
    }
    
    public static void f() {
        int firstVar=5;
        double a=2*firstVar;
        if (a>9)
            System.out.println(a);
        if (a>9)
            System.out.println(a);
    }

//    static int g(int a) {
//        return 10/a;
//    }
}
/*
BON SANG: java/io/PrintStream java/io/PrintStream println (D)V $ java/io/PrintStream
BEFORE f()V
   L0
    LINENUMBER 10 L0
    ICONST_5
    ISTORE 0
   L1
    LINENUMBER 11 L1
    ICONST_2
    ILOAD 0
    IMUL
    I2D
    DSTORE 1
   L2
    LINENUMBER 13 L2
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    DLOAD 1
    INVOKEVIRTUAL java/io/PrintStream.println (D)V
   L3
    LINENUMBER 16 L3
    RETURN
   L4
    LOCALVARIABLE firstVar I L1 L4 0
    LOCALVARIABLE a D L2 L4 1
    MAXSTACK = 3
    MAXLOCALS = 3
AFTER  f()V
   L0
    LINENUMBER 10 L0
    ICONST_5
    ISTORE 0
   L1
    LINENUMBER 11 L1
    ICONST_2
    ILOAD 0
    IMUL
    INVOKESTATIC ch/eiafr/cojac/models/wrappers/BigDecimalDouble.i2d (I)Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;
    ASTORE 1
   L2
    LINENUMBER 13 L2
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ALOAD 1
    INVOKESTATIC demo/CojacDebugDump1.COJAC_TYPE_CONVERT (Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;)[Ljava/lang/Object;
    DUP2
    ASTORE 3
    ASTORE 4
    NOP
    NOP
    ALOAD 4
    ALOAD 3
    DUP
    LDC 0
    AALOAD
    CHECKCAST [Ljava/lang/Object;
    LDC 1
    AALOAD
    CHECKCAST java/lang/Double
    INVOKEVIRTUAL java/lang/Double.doubleValue ()D
    DUP2_X1
    POP2
    POP
    INVOKEVIRTUAL java/io/PrintStream.println (D)V
    POP
    POP
   L3
    LINENUMBER 16 L3
    RETURN
   L4
    LOCALVARIABLE firstVar I L1 L4 0
    LOCALVARIABLE a Lch/eiafr/cojac/models/wrappers/BigDecimalDouble; L2 L4 1
    MAXSTACK = 8
    MAXLOCALS = 5
being dumped: demo/CojacDebugDump1
10.0


*/