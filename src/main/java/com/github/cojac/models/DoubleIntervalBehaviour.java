/*
 * *
 *    Copyright 2011-2016  Frédéric Bapst & Valentin Gazzola
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
package com.github.cojac.models;

import com.github.cojac.interval.FloatInterval;

public class DoubleIntervalBehaviour {
   /*inf value is stored as the 35 MS bits of the double
   (it's the double equivalent of a float truncated to 23 bits of mantissa)*/
   private static final long INF_MASK = 0xffffffffe0000000L;
   /*gap value is stored as the 29 LS bits of the double
   (We loose only 2 bits (set to 1, as worst case), the sign bit being always '0')*/
   private static final long GAP_MASK = 0x000000001fffffffL;
  /* public static void LDC(MethodVisitor mv, double a){
       double b = 3.0;
       mv.visitLdcInsn(a+b);
   }*/
   
   public static double DADD(double a, double b) {
       FloatInterval aI = extractValues(a);
       FloatInterval bI = extractValues(b);
       return embedValues(FloatInterval.add(aI, bI));
   }
   public static double DSUB(double a, double b) {
       FloatInterval aI = extractValues(a);
       FloatInterval bI = extractValues(b);
       return embedValues(FloatInterval.sub(aI, bI));
   }
   public static double DMUL(double a, double b) {
       FloatInterval aI = extractValues(a);
       FloatInterval bI = extractValues(b);
       return embedValues(FloatInterval.mul(aI, bI));
   }
   public static double DDIV(double a, double b){
       FloatInterval aI = extractValues(a);
       FloatInterval bI = extractValues(b);
       return embedValues(FloatInterval.div(aI, bI));
   }
   public static double DCONST_0(){
       return embedValues(new FloatInterval(0.0f));
   }
   public static double DCONST_1(){
       return embedValues(new FloatInterval(1.0f));
   }
   @FromClass("java/lang/Double")
   public static String toString(double a){
       return extractValues(a).toString();
   }
   @FromClass("java/lang/Double")
   public static double parseDouble(String s){
       double a = Double.parseDouble(s);
       return embedValues(new FloatInterval((float)a));
   }
   
   @UtilityMethod
   public static FloatInterval extractValues(double d) {
       long dL = Double.doubleToLongBits(d);
       float inf = (float) Double.longBitsToDouble((dL & INF_MASK)); 
       float sup = inf + Float.intBitsToFloat((((int) (dL & GAP_MASK)) << 2) | 3); //3 : the two last bits we lost
       return new FloatInterval(inf,sup);
   }
   @UtilityMethod
   public static double embedValues(FloatInterval interval) {
       long dL = Double.doubleToRawLongBits(interval.inf);
       dL = dL & INF_MASK;// (Float.floatToRawIntBits(sup)& SUP_MASK);
       float gap = Math.nextUp(interval.sup - interval.inf);
       dL = dL | (Float.floatToRawIntBits(gap) >>> 2);
       return Double.longBitsToDouble(dL);
   }
   
}
