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

import java.io.IOException;

import com.github.cojac.utils.NativeUtils;

/**
 * This class represents different behaviours, which all apply a pre 
 * and a post treatment on doubles and floats, both on opcodes and on 
 * Math methods.
 * 
 * Behaviours implemented are:
 * <ul>
 * <li>No conversion (Standard behaviour)</li>
 * <li>Double to float conversion</li>
 * <li>Arbitrary precision</li>
 * </ul>
 * 
 * com.github.cojac.models.ConversionBehaviour
 * FE_UPWARD    = 2048
 * FE_TONEAREST = 0
 * FE_TOWARDZERO= 3072
 * FE_DOWNWARD  = 1024
 * @author Gazzola Valentin
 *
 */
public class ConversionBehaviour {
    /*Native rounding behaviour methods and constants*/
   @UtilityMethod
   public static native int changeRounding(int rounding);
   public static final int FE_UPWARD    = 2048;
   public static final int FE_TONEAREST = 0;
   public static final int FE_TOWARDZERO= 3072;
   public static final int FE_DOWNWARD  = 1024;
   private static int currentRoundingMode = FE_TONEAREST;
   private static int originalRoundingMode = FE_TONEAREST;
   static{
       String libRoot = "/native-libraries/";
       String winLib64 = libRoot+"NativeRoundingMode64.dll";
       String winLib32 = libRoot+"NativeRoundingMode32.dll";
       String linLib = libRoot+"libNativeRoundingMode.so";
       String OSName = System.getProperty("os.name");
       int arch = Integer.parseInt(System.getProperty("sun.arch.data.model"));
       System.out.println(arch);
       try {    
           if(OSName.startsWith("Windows")){
               if(arch == 32)
                   NativeUtils.loadLibraryFromJar(winLib32); 
               else if(arch == 64)
                   NativeUtils.loadLibraryFromJar(winLib64); 
           }else if(OSName.startsWith("Linux")){
               NativeUtils.loadLibraryFromJar(linLib); 
           }else{
               System.out.println("OS not corresponding to a supported OS, .");
           }
             
       } catch (IOException e) {
           //trick for tests, when the jar doesn't exist yet.
           System.loadLibrary("NativeRoundingMode");   
           e.printStackTrace();
       }    
       //
   }
   public static Conversion c  = Conversion.NoConversion;
   /*Arbitrary precision behaviour variables*/
   public static final int SIGNIFICATIVE_DOUBLE_BITS = 52;
   public static final int SIGNIFICATIVE_FLOAT_BITS = 23;
   private static int significativeBits=SIGNIFICATIVE_DOUBLE_BITS;
   private static long mask = 0xffffffffffffffffL;
   public static double DADD(double a, double b) {
       return outTransform(inTransform(a) + inTransform(b));
   }
   
   public static double DSUB(double a, double b) {
       return outTransform(inTransform(a)- inTransform(b));
   }
   public static double DMUL(double a, double b) {
       return outTransform(inTransform(a) * inTransform(b));
   }
   public static double DDIV(double a, double b){
       return outTransform(inTransform(a) / inTransform(b));
   }
   public static double DREM(double a, double b){
       return outTransform(inTransform(a) %inTransform(b));
   }
   public static int DCMPL(double a, double b){
       return Double.compare(inTransform(a) ,inTransform(b));
   }
   public static int DCMPG(double a, double b){
       if(Double.isNaN(a)||Double.isNaN(b))
           return 1;
       return DCMPL(a,b);
   }
   public static float FADD(float a, float b) {
       return outTransform(inTransform(a) + inTransform(b));
   }
   public static float FSUB(float a, float b) {
       return outTransform(inTransform(a)- inTransform(b));
   }
   public static float FMUL(float a, float b) {
       return outTransform(inTransform(a) * inTransform(b));
   }
   public static float FDIV(float a, float b){
       return outTransform(inTransform(a) / inTransform(b));
   }
   public static float FREM(float a, float b){
       return outTransform(inTransform(a) %inTransform(b));
   }
   public static int FCMPL(float a, float b){
       return Double.compare(inTransform(a) ,inTransform(b));
   }
   public static int FCMPG(float a, float b){
       if(Double.isNaN(a)||Double.isNaN(b))
           return 1;
       return DCMPL(a,b);
   }
    public static double abs(double a){
        return outTransform( Math.abs(a));
    }
    public static double acos(double a){
        return outTransform( Math.acos(inTransform(a)));
    }
    public static double asin(double a){
        return outTransform( Math.asin(inTransform(a)));
    }
    public static double atan(double a){
        return outTransform( Math.atan(inTransform(a)));
    }
    public static double atan2(double x,double y){
        return outTransform( Math.atan2(inTransform(x),inTransform(y)));
    }
    public static double cbrt(double a){
        return outTransform( Math.cbrt(inTransform(a)));
    }
    public static double copySign(double a,double b){
        return Math.copySign(inTransform(a),inTransform(b));
    }
    public static double cos(double a){
        return outTransform( Math.cos(inTransform(a)));
    }
    public static double cosh(double a){
        return outTransform( Math.cosh(inTransform(a)));
    }
    public static double exp(double a){
        return outTransform( Math.exp(inTransform(a)));
    }
    public static double expm1(double a){
        return outTransform( Math.expm1(inTransform(a)));
    }
    public static int getExponent(double a){
        return Math.getExponent(inTransform(a));
    }
    public static double hypot(double a,double b){
        return outTransform( Math.hypot(inTransform(a),inTransform(b)));
    }
    public static double IEEEremainder(double a,double b){
        return outTransform( Math.IEEEremainder(inTransform(a),inTransform(b)));
    }
    public static double log(double a){
        return outTransform( Math.log(inTransform(a)));
    }
    public static double log10(double a){
        return outTransform( Math.log10(inTransform(a)));
    }
    public static double log1p(double a){
        return outTransform( Math.log1p(inTransform(a)));
    }
    public static double max(double a, double b){
        return Math.max(inTransform(a), inTransform(b));
    }
    public static double min(double a, double b){
        return Math.min(inTransform(a), inTransform(b));
    }
    
    public static double nextAfter(double a, double b){
        //Necessary to redirect to the float method
        //TODO: an equivalent trick for the Arbitrary behaviour
        if(c == Conversion.Double2Float){
            return Math.nextAfter((float) a,(float) b);
        }
        return Math.nextAfter(inTransform(a), inTransform(b));
    }
    public static double nextDown(double a){
      //Necessary to redirect to the float method
      //TODO: an equivalent trick for the Arbitrary behaviour
        if(c == Conversion.Double2Float){
            return Math.nextDown((float) a);
        }
        return  Math.nextDown(inTransform(a));
    }
    public static double nextUp(double a){
      //Necessary to redirect to the float method
      //TODO: an equivalent trick for the Arbitrary behaviour
        if(c == Conversion.Double2Float){
            return Math.nextUp((float) a);
        }
        return  Math.nextUp(inTransform(a));
    }
    public static double pow(double a, double b){
        return outTransform( Math.pow(inTransform(a), inTransform(b)));
    }
    public static double random(){
        return outTransform( Math.random());
    }
    public static double scalb(double a, int b){
        return outTransform(Math.scalb(inTransform( a),b));
    }
    public static double sin(double a){
        return outTransform( Math.sin(inTransform(a)));
    }
    public static double sinh(double a){
        return outTransform( Math.sinh(inTransform(a)));
    }
    public static double sqrt(double a){
        return outTransform( Math.sqrt(inTransform(a)));
    }
    public static double tan(double a){
        return outTransform( Math.tan(inTransform(a)));
    }
    public static double tanh(double a){
        return outTransform( Math.tanh(inTransform(a)));
    }
    public static double toDegrees(double a){
        return outTransform( Math.toDegrees(inTransform(a)));
    }
    public static double toRadians(double a){
        return outTransform( Math.toRadians(inTransform(a)));
    }
    public static double ulp(double a){
        //TODO: comme les autres.
        return Math.ulp(inTransform(a));
    }
    /**
     * Transformation applied on double parameters (depends on "c") 
     * @param a the parameter on which the transformation occur
     * @return the transformed parameter, in form of a double
     */
    private static double inTransform(double a){
        switch(c){
        case Double2Float:
           return (float)a;
        case Arbitrary:
           return Double.longBitsToDouble(Double.doubleToLongBits(a)&mask);
        case NativeRounding:
            changeRounding(currentRoundingMode);
            return a;
        }
        return a;
    }
    /**
     * Transformation applied on double returns (depends on "c") 
     * @param a the return value on which the transformation occur
     * @return the transformed value, in form of a double
     */
    private static double outTransform(double a){
        switch(c){
        case Double2Float:
           return (float)a;
        case Arbitrary:
           return Double.longBitsToDouble(Double.doubleToLongBits(a)&mask);
        case NativeRounding:
            changeRounding(originalRoundingMode);
            return a;
        }
        return a;
    }
    /**
     * Transformation applied on float parameters (depends on "c") 
     * @param a the parameter on which the transformation occur
     * @return the transformed parameter, in form of a float
     */
    private static float inTransform(float a){
        switch(c){
        case Arbitrary:
            if (significativeBits<SIGNIFICATIVE_FLOAT_BITS)
                return (float) Double.longBitsToDouble(Double.doubleToLongBits(a)&mask);
            break;
        case NativeRounding:
            System.out.println("oldRoundingValue: "+changeRounding(currentRoundingMode));
            return a;
        }
        return a;
    }
    /**
     * Transformation applied on float parameters (depends on "c") 
     * @param a the return value on which the transformation occur
     * @return the transformed value, in form of a float
     */
    private static float outTransform(float a){
        switch(c){
        case Arbitrary:
            if (significativeBits<SIGNIFICATIVE_FLOAT_BITS)
                return (float) Double.longBitsToDouble(Double.doubleToLongBits(a)&mask);
        case NativeRounding:
            changeRounding(originalRoundingMode);
            return a;
        }
        return a;
    }
    /**
     * Conversions behaviours implemented by this class
     * 
     * @author Gazzola Valentin
     *
     */
    public enum Conversion{
        Double2Float,
        Arbitrary,
        NoConversion,
        NativeRounding;
    }
    @UtilityMethod
    public static void setSignificativeBits(int nb){
        significativeBits = nb;
        if(nb <SIGNIFICATIVE_DOUBLE_BITS && nb >=0)
            mask = mask ^((1L<<(52-nb))-1);
        //System.out.println("mask: "+Long.toBinaryString(mask));
    }
    @UtilityMethod
    public static void setRoundingMode(int mode){
        currentRoundingMode = mode;
    }
   
}
