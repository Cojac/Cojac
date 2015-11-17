/*
 * *
 *    Copyright 2014 Frédéric Bapst & Romain Monnard
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

package com.github.cojac.unit;
import java.util.ArrayList;
import java.util.LinkedList;

//TODO: a reasonable/coherent test example for FloatReplace

class TFEAux {
    static float myStaticFloat=3.0f;
    static int myStaticInt=3;
    float tfeaux_fct(float f) {
        return 2*f;
    }
}

public class TinyFloatExample {

    static float myStaticFloat=73.0f;
    static Float myStaticFloatWrapper= Float.valueOf(73f);
    static float[] myStaticFloatArray = {5f, 6f, 41.21f};

    
    static float everyFloatOpcode(float f) {
        long l=2L; double d=2.0; int i=2;
        float a=f;
        a = a+f;
        a = a-f;
        a = a*f;
        a = a/f;
        a = a%10f;
        a = -a;
        if (a>f) a=f;
        if (a<f) a=f;
        a = a+0f+1f+2f;
        a += (int)f;
        a += (long)f;
        a += (double)f;
        a += (float)l;
        a += (float)d;
        a += (float)i;
        return a;
    }
    
    static double everyDoubleOpcode(double f) {
        long l=2L; float d=2.0f; int i=2;
        double a=f;
        a = a+f;
        a = a-f;
        a = a*f;
        a = a/f;
        a = a%10f;
        a = -a;
        if (a>f) a=f;
        if (a<f) a=f;
        a = a+0f+1f+2f;
        a += (int)f;
        a += (long)f;
        a += (float)f;
        a += (double)l;
        a += (double)d;
        a += (double)i;
        return a;
    }
    
    static double firstFrameLocalVariables(double da, long la, double db, long lb){
        double ret;
        if(db * lb > 500){
            double a = da * la;
            ret = a / lb;
        }
        else{
            double a = db / lb;
            ret = a * la;
        }
        return ret;
    }
    
    static double dup2Operation(double val, long longVal){
        double da;
        double db;
        long la;
        long lb;
        da = db = val;
        la = lb = longVal;
        return da;
    }
    
    static Float testFloatWrapper(Float a, Float b){
        float val = 5.2f;
        double dval = 7.91;
        // Float creation
        Float f1 = 1.5f;
        Float f2 = new Float(f1);
        Float f3 = val;
        Float f4 = new Float(val);
        Float f5 = new Float("521.235");
        Float f6 = new Float(5.21);
        Float f7 = new Float(dval);
 
        Float f8 = Float.parseFloat("76.654");
        
        // Float conversions
        byte by = f1.byteValue();
        double d = f1.doubleValue();
        float f = f1.floatValue();
        int i = f1.intValue();
        long l = f1.longValue();
        short s = f1.shortValue();
        
        // other methods
        Float.isInfinite(f1);
        Float.isInfinite(f1);
        Float.isNaN(f2);
        Float.compare(f1, f2);
        Float.toHexString(f1);
        f1.compareTo(f2);
        float hex = Float.intBitsToFloat(i);
        
        
        float v = f1 + f2 + f3 + f4;
        
        return a + b + v;
    }
    
    static Double testDoubleWrapper(Double a, Double b){
        double val = 5.2;
        float fval = 7.91f;
        // Float creation
        Double d1 = 1.5;
        Double d2 = new Double(d1);
        Double d3 = val;
        Double d4 = new Double(val);
        Double d5 = new Double("521.235");
        Double d6 = new Double(5.21);
        Double d7 = new Double(fval);
 
        Double f8 = Double.parseDouble("76.654");
        
        // Float conversions
        byte by = d1.byteValue();
        double d = d1.doubleValue();
        float f = d1.floatValue();
        int i = d1.intValue();
        long l = d1.longValue();
        short s = d1.shortValue();
        
        // other methods
        Double.isInfinite(d1);
        Double.isInfinite(d1);
        Double.isNaN(d2);
        Double.compare(d1, d2);
        Double.toHexString(d1);
        d1.compareTo(d2);
        
        double v = d1 + d2 + d3 + d4;
        if (causesParamRemappingWrapper(2.0, 3.0) < 0) 
            throw new RuntimeException("stupid bug");
        return a + b + v;
    }
    
    static Double causesParamRemappingWrapper(Double a, Double b) {
        Double c = -1.5;
        Double d = -1.5;
        return a;
    }
    
    public static void testMathLib(){
        double a = 100;
        Math.sqrt(a);
    }
	
	public static ArrayList<Double> testList(){
		
		//Line2D line = new Line2D.Double(0,0,0,0);
		
		return new ArrayList<>();
	}
    
    public static float myFct1(float f) {
        float a;
        if (f>0) {
            float b1=f; 
            a=b1*b1;
        } else  {
            int b2=4; 
            a=b2/f;
        }
        return a+a ; //+ (new TFEAux()).tfeaux_fct(a);
    }
    
    public static float twoReusedFloatVar(float f){
        if (f>0){
            float a=f+f;
            return a;
        } // else 
        float a=f*f;
        return a;
    }
    
    public static float simpleFcmp(float f){
        if (f>0){
            return f+f;
        } // else {
        return f*f;
    }

    protected static float fct2(float f, float g) {
        for(int i=0; i<10; i++){
            f= 3*f +g;
        }
        //f=Float.valueOf(f);
        return f % g;
    }
    
    public static float myFct(float f) {
        return f+f;
    }

    
    public static void go() {
        float a=4; //double a=4; //float a=3.9f;
        float b = 8.0f * a;
        long la = 25;
        long lb = 52;
        b=myFct1(a);
        b=fct2(b,b);
        b+= everyFloatOpcode(b);
        b+= everyDoubleOpcode(b);
        
        b+= firstFrameLocalVariables(b, la, a, lb);
        b += dup2Operation(b,lb);
        
        int res=(int) b;
        LinkedList<Float> ll=new LinkedList<>();
        ll.add(b);
        
        Float fw1 = 1.6f;
        Float fw2 = b;
        Float fwres = testFloatWrapper(fw1, fw2);
        Double dw1 = 1.6;
        Double dw2 = (double)b;
        Double dwres = testDoubleWrapper(dw1, dw2);
        
        testMathLib();
		testList();
        
        //System.out.println("inside TinyFloatExample.go() "+res);
        //System.out.println("inside TinyFloatExample.go() "+(int)(TFEAux.myStaticFloat));
    }
    
    public static void main(String[] args) {
        go();
    }
    
}
    // D:\Git-MyRepository\cojac\target\test-classes>
    // java -javaagent:..\cojac.jar="-v -R" 
    // -Djava.system.class.loader=com.github.cojac.VerboseClassLoader 
    // com.github.cojac.unit.TinyFloatExample > e:\auxaux.txt}
