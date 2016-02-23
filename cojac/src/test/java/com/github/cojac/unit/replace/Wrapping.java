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

package com.github.cojac.unit.replace;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.DoubleUnaryOperator;

import org.junit.Assert;

public class Wrapping implements DoubleUnaryOperator {  
    public final double dField;
    public final float  fField;
    public static double dStaticField;
    public static float  fStaticField;

    public Wrapping(double d, float f) {
        this.dField=d;
        this.fField=f;
        dStaticField=d;
        fStaticField=f;
    }
    //-----------------------------------------------------------------------

    public static String COJAC_MAGIC_DOUBLE_wrapper() { return ""; }
    @SuppressWarnings("unused")
    public static String COJAC_MAGIC_DOUBLE_toStr(double n) { return ""; }
    @SuppressWarnings("unused")
    public static String COJAC_MAGIC_FLOAT_toStr(float  n) { return ""; }

    private static final String WRAPPER=COJAC_MAGIC_DOUBLE_wrapper();

    public static void main(String[] args) {
        go();
    }
    
    public static void go() {
        //System.out.println("Wrapper used: "+WRAPPER);
        playWithPrimitiveFloats();
        playWithPrimitiveDoubles();
        playWithJavaFloatWrapper();
        playWithJavaDoubleWrapper();
        playWithArrays();
        playWithCollection();
        playWithLambdas();
        playWithFields();
        playWithInterfaces();
        //System.out.println("The end.");
    }

    public static void playWithPrimitiveFloats() {
        final float a=0.5f;
        float f=a;
        f=twiceFloat(f)/2; ok(f==a);
        f=f-1; f=f+1; ok(f==a);
        f=f*2; f=f/2; ok(f==a);
        f=f%(f+1); ok(f==a);
        f=-(-f); ok(f==a);
        f=Math.abs(f); ok(f==a);
        f=Math.min(f, +10f); ok(f==a);
        f=Math.max(f, -10f); ok(f==a);

        double d=f;
        f=(float)d; ok(f==a);
        int i=(int)(2*f);
        f=((float)i)/2; ok(f==a);
        long l=(long)(2*f);
        f=((float)l/2); ok(f==a);
        String s=COJAC_MAGIC_FLOAT_toStr(f);
        ok((WRAPPER.length()>0) == (s.length()>0));
    }

    public static void playWithPrimitiveDoubles() {
        final double a=0.5;
        double d=a;
        d=twiceDouble(d)/2; ok(d==a);
        d=d-1; d=d+1; ok(d==a);
        d=d*2; d=d/2; ok(d==a);
        d=d%(d+1); ok(d==a);
        d=-(-d); ok(d==a);
        d=Math.abs(d); ok(d==a);
        d=Math.min(d, +10f); ok(d==a);
        d=Math.max(d, -10f); ok(d==a);
        d=Math.sqrt(Math.pow(d, 2)); ok(d==a);
        int i=(int)(2*d);
        d=((float)i)/2; ok(d==a);
        long l=(long)(2*d);
        d=((float)l/2); ok(d==a);
        String s=COJAC_MAGIC_DOUBLE_toStr(d);
        ok((WRAPPER.length()>0) == (s.length()>0));

        // WrappingAux.f(3.2); this won't work, because the AgentTest trick won't instrument other classes
    }

    private static void playWithLambdas() {
        //TODO reconsider and expand... Remember that for now, under that surefire test, 
        // only one class is instrumented, and lambdas seem to be in isolated "classes"...
        double a = 1.0;
        a = a/3.0;
        double d = a;
        DoubleUnaryOperator unOp;
        unOp = Math::sqrt;
        //mv.visitInvokeDynamicInsn(
        //   "applyAsDouble", 
        //   "()Ljava/util/function/DoubleUnaryOperator;",
        //   new Handle(Opcodes.H_INVOKESTATIC, 
        //              "java/lang/invoke/LambdaMetafactory", 
        //              "metafactory", 
        //              "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"
        //             ),
        //             new Object[]{Type.getType("(D)D"), 
        //                          new Handle(Opcodes.H_INVOKESTATIC, 
        //                                     "java/lang/Math", 
        //                                     "sqrt", 
        //                                     "(D)D"), 
        //                          Type.getType("(D)D")
        //                         }
        //);
        
        d=unOp.applyAsDouble(a*a);   // but the "enrichment" is lost...

        // this should work but... see todo above!
        //  unOp = (x->(x+2)-2);
//mv.visitInvokeDynamicInsn(... new Handle(Opcodes.H_INVOKESTATIC, "ch/eiafr/cojac/unit/replace/Wrapping", "lambda$0", "(D)D"), Type.getType("(D)D")});
              //      unOp.applyAsDouble(a); ok(d==a);
        //    unOp = Wrapping::mySqrt;
//mv.visitInvokeDynamicInsn(...new Handle(Opcodes.H_INVOKESTATIC, "ch/eiafr/cojac/unit/replace/Wrapping", "mySqrt", "(D)D"), Type.getType("(D)D")});
        //      unOp.applyAsDouble(a*a); ok(d==a);
    }

    private static double mySqrt(double x) {
        return Math.sqrt(x);
    }

    public static void playWithJavaFloatWrapper() {
        final float a=1.0f;
        Float f=a; ok(f==a);
        f=new Float(a); ok(f==a);
        f=new Float(""+a); ok(f==a);
        f=new Float((double)f); ok(f==a);
        f=new Float(f.doubleValue()); ok(f==a);
        f=new Float(f.floatValue()); ok(f==a);
        f=new Float(f.intValue()); ok(f==a);
        f=new Float(f.longValue()); ok(f==a);
        f=new Float(f.shortValue()); ok(f==a);
        f=new Float(f.byteValue()); ok(f==a);
        ok(f.compareTo(a)==0);
        //ok(f.equals(a));
        ok(!f.isNaN());
        ok(!f.isInfinite());
    }

    public static void playWithFields() {
        double b=1.0;
        b= b/3.0;
        Wrapping w=new Wrapping(b, (float)b);
        ok(w.dField==b);
        ok(w.fField==(float)b);
        ok(dStaticField==b);
        ok(fStaticField==(float)b);
    }
        
    public static void playWithJavaDoubleWrapper() {
        final double a=1.0f;
        Double d=a; ok(d==a);
        d=new Double(a); ok(d==a);
        d=new Double(""+a); ok(d==a);
        d=new Double(d); ok(d==a);
        d=new Double(d.doubleValue()); ok(d==a);
        d=new Double(d.floatValue()); ok(d==a);
        d=new Double(d.intValue()); ok(d==a);
        d=new Double(d.longValue()); ok(d==a);
        d=new Double(d.shortValue()); ok(d==a);
        d=new Double(d.byteValue()); ok(d==a);
        ok(d.compareTo(a)==0);
        ok(d.equals(a)); 
        ok(!d.isNaN());
        ok(!d.isInfinite());
    }

    private static float  twiceFloat(float f)  { return 2f*f; }
    private static double twiceDouble(double d) { return 2*d;  }

    public static void playWithCollection() {
        double a= 1.0;
        double b= a/3.0;
        ArrayList<Double> l=new ArrayList<>();
        l.add(b); l.add(b/2);
        Collections.sort(l);
        ok(keepsEnrichment(l.get(0)));
        ok(l.get(1)==b);
    }

    public static void playWithArrays() { 
        double b=1.0;
        b= b/3.0;
        double a=b;
        double[]t=new double[]{b, b/2};
        Arrays.sort(t);
        ok(keepsEnrichment(t[0]));
        ok(t[1]==b);
        double[][]tt={{b}};
        ok(tt[0][0]==a);
        double[] t2=(double[])t.clone(); 
        double[][] t2a=(double[][])tt.clone(); 
        Double[] t3={b};
        Double[] t4=(Double[])t3.clone();
        Double[][] t5={{b}}; 
        Double[][] t5a=(Double[][])t5.clone(); 
        
        float[] t1f={3f};
        float[] t2f=(float[])t1f.clone(); 
        float[][]ttf={{3f}};
        float[][] t2af=(float[][])ttf.clone(); 
        Float[] t3f={3f};
        Float[] t4f=(Float[])t3f.clone();
        Float[][] t5f={{3f}}; 
        Float[][] t5fa=(Float[][])t5f.clone(); 
    }

    public static void playWithInterfaces() {
        double b=1.0;
        b= b/3.0;
        Wrapping w = new Wrapping(b, (float)b);
        b=w.applyAsDouble(b);  // This is easy!
        ok(keepsEnrichment(b));
        DoubleUnaryOperator o=w;
        b=o.applyAsDouble(b);  // this much much harder!"
        ok(keepsEnrichment(b));
    }

    
    /** works only if d happens to have a long development, and only 
     * for BigDecimal wrapping (with a number of digits > 16) */
    static boolean keepsEnrichment(double d) {
        if (!WRAPPER.equals("BigDecimal")) return true;
        String s1=""+d;
        String s2=COJAC_MAGIC_DOUBLE_toStr(d);
        return s2.length()>s1.length();
    }

    private static void ok(boolean b) {
        Assert.assertTrue("bad news...", b);
        //if (!b) throw new RuntimeException("bad news...");
    }

    @Override
    public double applyAsDouble(double operand) {
        return operand + 1.0;
    }
    
//  public static void badOverload(double d) {}
//  public static void badOverload(Double d) {}  // This won't work (same instrumented signature)!

}
