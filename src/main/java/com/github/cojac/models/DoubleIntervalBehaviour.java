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


public class DoubleIntervalBehaviour {
   private static final long INF_MASK = 0xffffffff00000000L;
   private static final long SUP_MASK = 0x00000000ffffffffL;
   private static final int INF_ID = 0;
   private static final int SUP_ID = 1;
  /* public static void LDC(MethodVisitor mv, double a){
       double b = 3.0;
       mv.visitLdcInsn(a+b);
   }*/
   
   public static double DADD(double a, double b) {
       float[] aLim = extractValues(a);
       float[] bLim = extractValues(b);
       return embedValues(aLim[0]+bLim[0], aLim[1]+bLim[1]);
   }
   public static double DSUB(double a, double b) {
       float[] aLim = extractValues(a);
       float[] bLim = extractValues(b);
       return embedValues(aLim[0]-bLim[1], aLim[1]-bLim[0]);
   }
   public static double DMUL(double a, double b) {
       float[] aLim = extractValues(a);
       float[] bLim = extractValues(b);
       float inf = Math.min(Math.min(aLim[0] * bLim[0], aLim[0] * bLim[1]), Math.min(aLim[1] *
               bLim[0], aLim[1] * bLim[1]));
       float sup = Math.max(Math.max(aLim[0] * bLim[0], aLim[0] * bLim[1]), Math.max(aLim[1] *
               bLim[0], aLim[1] * bLim[1]));
       return embedValues(inf, sup);
   }
   public static double DDIV(double a, double b){
       float[] aLim = extractValues(a);
       float[] bLim = extractValues(b);
       float inf = Math.min(Math.min(aLim[0] / bLim[0], aLim[0] / bLim[1]), Math.min(aLim[1] /
               bLim[0], aLim[1] / bLim[1]));
       float sup = Math.max(Math.max(aLim[0] / bLim[0], aLim[0] / bLim[1]), Math.max(aLim[1] /
               bLim[0], aLim[1] / bLim[1]));
       return embedValues(inf, sup);
   }
   public static String toString(double a){
       float[] aLim = extractValues(a);
       return "["+aLim[0]+";"+aLim[1]+"]";
   }
   @UtilityMethod
   public static float[] extractValues(Double d){
       float[] f = new float[2];
       long dL = Double.doubleToLongBits(d);
       
       f[SUP_ID] = Float.intBitsToFloat((int)(dL & SUP_MASK)); 
       f[INF_ID] = Float.intBitsToFloat((int)(dL>>32 & SUP_MASK));
       
       return f;
   }
   @UtilityMethod
   public static double embedValues(float inf, float sup){
       long dL = Float.floatToRawIntBits(inf);
       dL = ((dL << 32) )| (Float.floatToRawIntBits(sup)& SUP_MASK); 
       return Double.longBitsToDouble(dL);
   }
   
}
