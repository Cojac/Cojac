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

package demo;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.DoubleUnaryOperator;

import javax.swing.JFrame;

/* ------------------------------------------
To be run without Cojac, or with cojac with those options:
    -javaagent:D:\Git-MyRepository\cojac\target\cojac.jar=

"-Bb 3"
"-Rs"
"-Ri"
"-Ra"
"-W cojac.WrapperBasic"

 ------------------------------------------ */

public class Wrapping {  

  public static String COJAC_MAGIC_wrapperName() { return ""; }
  public static String COJAC_MAGIC_toString(double n) { return ""; }
  public static String COJAC_MAGIC_toString(float  n) { return ""; }

  private static final String WRAPPER=COJAC_MAGIC_wrapperName();
    
  public static void main(String[] args) {
      new MyCojacDebugDump(8);
//      System.out.println("yeahhh "+(java.util.function.DoubleUnaryOperator)Math::sqrt);

    System.out.println("Wrapper used: "+WRAPPER);
    playWithPrimitiveFloats();
    playWithPrimitiveDoubles();
    playWithJavaFloatWrapper();
    playWithJavaDoubleWrapper();
    playWithArrays();
    playWithCollection();
//    playWithGUI();
    playWithLambdas();
    System.out.println("The end.");    
  }
  
  public static void playWithPrimitiveFloats() {
    final float a=0.5f;
    float f=a;
    f=twiceFloat(f)/2; ok(f==a);
    f=f*2; f=f/2; ok(f==a);
    f=f-1; f=f+1; ok(f==a);
    System.out.println(" A "+COJAC_MAGIC_toString(f));
    f=f%(f+1); ok(f==a);
    f=-(-f); ok(f==a);
    f=Math.abs(f); ok(f==a);
    f=Math.min(f, +10f); ok(f==a);
    f=Math.max(f, -10f); ok(f==a);
    
    double d=(double)f;
    f=(float)d; ok(f==a);
    int i=(int)(2*f);
    f=((float)i)/2; ok(f==a);
    long l=(long)(2*f);
    f=((float)l/2); ok(f==a);
    String s=COJAC_MAGIC_toString(f);
    ok((WRAPPER.length()>0) == (s.length()>0));
  }
  
  public static void playWithPrimitiveDoubles() {
    final double a=0.5;
    double d=a;
    d=twiceDouble(d)/2; ok(d==a);
    d=d*2; d=d/2; ok(d==a);
    d=d-1; d=d+1; ok(d==a);
    d=d%(d+1); ok(d==a);
    d=-(-d); ok(d==a);
    d=Math.abs(d); ok(d==a);
    d=Math.min(d, +10f); ok(d==a);
    d=Math.max(d, -10f); ok(d==a);
    
    int i=(int)(2*d);
    d=((float)i)/2; ok(d==a);
    long l=(long)(2*d);
    d=((float)l/2); ok(d==a);
    String s=COJAC_MAGIC_toString(d);
    ok((WRAPPER.length()>0) == (s.length()>0));
    if (WRAPPER.equals("BigDecimal")) {
        double x=1.0;
        x = x/3.0;
        String s1=""+x;
        String s2=COJAC_MAGIC_toString(x);
        ok(s2.length()>s1.length());
    }
  }

  private static void playWithLambdas() {  // JAVA8 HOT TODO
      double a = 1.0;
      a = a/3.0;
      double d = a;
      DoubleUnaryOperator unOp;
      unOp = Math::sqrt;
      /* mv.visitInvokeDynamicInsn(
           "applyAsDouble", 
           "()Ljava/util/function/DoubleUnaryOperator;",
           new Handle(Opcodes.H_INVOKESTATIC, 
                      "java/lang/invoke/LambdaMetafactory", 
                      "metafactory", 
                      "(Ljava/lang/invoke/MethodHandles$Lookup;
                        Ljava/lang/String;Ljava/lang/invoke/MethodType;
                        Ljava/lang/invoke/MethodType;
                        Ljava/lang/invoke/MethodHandle;
                        Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"
                     ),
          new Object[]{Type.getType("(D)D"), 
                       new Handle(Opcodes.H_INVOKESTATIC, 
                                             "java/lang/Math", 
                                             "sqrt", 
                                             "(D)D"), 
                       Type.getType("(D)D") 
                      }
        );
      */
      d=unOp.applyAsDouble(a*a);   // works (but the "enrichment" is lost...)

      // TODO: this does not work for the moment...
      unOp = (x->(x+2)-2);
      /* mv.visitInvokeDynamicInsn(... 
           new Handle(Opcodes.H_INVOKESTATIC, 
                       "ch/eiafr/cojac/unit/replace/Wrapping", 
                       "lambda$0", "(D)D"), Type.getType("(D)D")});
      */
      unOp.applyAsDouble(a); ok(d==a);
      
      unOp = Wrapping::mySqrt;
      /* mv.visitInvokeDynamicInsn(...
           new Handle(Opcodes.H_INVOKESTATIC, 
                        "ch/eiafr/cojac/unit/replace/Wrapping", 
                        "mySqrt", "(D)D"), 
                        Type.getType("(D)D")});
       
       */
      unOp.applyAsDouble(a*a);
  }
  
  private static double mySqrt(double x) {
      return Math.sqrt(x);
  }
  
  /** works only if d has a chance to have a long development, and only 
   * for BigDecimal wrapping (with a number of digits > 16) */
  static boolean keepsEnrichment(double d) {
      if (!WRAPPER.equals("BigDecimal")) return true;
      String s1=""+d;
      String s2=COJAC_MAGIC_toString(d);
      return s2.length()>s1.length();
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
    ok(f.equals(a));
    ok(!f.isNaN());
    ok(!f.isInfinite());
  }

  public static void playWithJavaDoubleWrapper() {
    final double a=1.0f;
    Double d=a; ok(d==a);
    d=new Double(a); ok(d==a);
    d=new Double(""+a); ok(d==a);
    d=new Double((double)d); ok(d==a);
    d=new Double(d.doubleValue()); ok(d==a);
    d=new Double(d.floatValue()); ok(d==a);
    d=new Double(d.intValue()); ok(d==a);
    d=new Double(d.longValue()); ok(d==a);
    d=new Double(d.shortValue()); ok(d==a);
    d=new Double(d.byteValue()); ok(d==a);
    ok(d.compareTo(a)==0);
    d=2*d/2;
    Double aa=Double.valueOf(a);
    ok(d.equals(aa));
    ok(!d.isNaN());
    ok(!d.isInfinite());
  }

  private static float  twiceFloat(float f)  { return 2f*f; }
  private static double twiceDouble(double d) { return 2*d;  }

  public static void playWithCollection() {  //TODO: rewrite
    //myCojacDebugDump.f(new ArrayList<Double>());
    double a=1.0;
    double b= a/3.0;
    System.out.println("oneThird orig: "+b);
    System.out.println("oneThird: "+COJAC_MAGIC_toString(b));
    b=MyCojacDebugDump.f(b);
    System.out.println("applyAsDouble "+COJAC_MAGIC_toString(b));
    ArrayList<MyWrapper> l1=new ArrayList<>();
//    l1.add(new MyWrapper(b));
//    double c=l1.get(0).dValue;
    ArrayList<Double> l2=new ArrayList<>();
    l2.add(b); l2.add(b/2);
    Collections.sort(l2);
    System.out.println("oneThird in sorted list: "+COJAC_MAGIC_toString(l2.get(0)));
    ok(keepsEnrichment(l2.get(0)));

//    System.out.println("oneThird in list: "+COJAC_MAGIC_DOUBLE_toStr(c));
    Double d=b;
    System.out.println("oneThird origWrapper: "+b);
    System.out.println("oneThird wrapper magic: "+COJAC_MAGIC_toString(d));
  }

  public static void playWithArrays() { //TODO: rewrite
    double a=1.0;
    double b= a/3.0;
    double[]t=new double[]{b, b/2};
    Arrays.sort(t);
    System.out.println("oneThird in sorted primitive array: "+COJAC_MAGIC_toString(t[0]));   
    ok(keepsEnrichment(t[0]));
  }
  
  private static void playWithGUI() {
      SimpleGUI f=new SimpleGUI();
      try {Thread.sleep(500);} catch(Exception e) {Thread.currentThread().interrupt();}
      f.dispose();
  }
  
  private static void ok(boolean b) {
    if (!b) throw new RuntimeException("bad news...");
  }
  //======================================================================
  // any class with name containing "CojacDebugDump" will be dumped after
  // instrumentation, for debugging purposes (see cojac/Agent)
  static class MyCojacDebugDump implements DoubleUnaryOperator { // implements DoubleUnaryOperator {
      //MyWrapper mmm;
      public MyCojacDebugDump(int a) {
          //mmm=new MyWrapper(0.2);
      }
      
//      @Override public double applyAsDouble(double operand) {
//         return 2*operand;
//      }

      public static double f(double a) {
//          double[][] t1={{2.0}};
//          double[][] t2=(double[][])t1.clone(); 
//          double[] t11={2.0};
//          double[] t21=(double[])t11.clone(); 
          
          Double[] t3={2.0};
          Double[] t4=(Double[])t3.clone();
//          return 2*a;
//          return 9;
          DoubleUnaryOperator op;
          op=new MyCojacDebugDump(0);
          return op.applyAsDouble(a);
      }
 
      public static void f(ArrayList<Double> c) {
//          double b=1; b/=3.0;
//          Double[]t2=new Double[]{b, b/2};
//          Arrays.sort(t2);
//          System.out.println("oneThird in sorted jwrapper array: "+COJAC_MAGIC_DOUBLE_toStr(t2[0]));   
//          Double d=3.0;
//          if(d.equals(d))
//              c.add(d);
      }

      static int g(int a) {
          int b=a;
          try {
              System.out.println(a);
          } catch(NoSuchMethodError|AbstractMethodError e) {
              System.out.println(b);
          }
          return b;
      }


      
      public static void playLambdas() {
          double a = 1.0;
          a = a/3.0;
          double d = a;
          DoubleUnaryOperator unOp;
          unOp = Math::sqrt;
          d=unOp.applyAsDouble(a*a);   // but the "enrichment" is lost...
      }

    @Override
    public double applyAsDouble(double operand) {
        return 2*operand;
    }

          /*
  // access flags 0x9
  public static playLambdas() : void
   L0
    LINENUMBER 224 L0
    DCONST_1
    DSTORE 0
   L1
    LINENUMBER 225 L1
    DLOAD 0: a
    LDC 3.0
    DDIV
    DSTORE 0: a
   L2
    LINENUMBER 226 L2
    DLOAD 0: a
    DSTORE 2
   L3
    LINENUMBER 228 L3
    INVOKEDYNAMIC applyAsDouble() : DoubleUnaryOperator [
      // handle kind 0x6 : INVOKESTATIC
      LambdaMetafactory.metafactory(MethodHandles$Lookup, String, MethodType, MethodType, MethodHandle, MethodType) : CallSite
      // arguments:
      (double) : double, 
      // handle kind 0x6 : INVOKESTATIC
      Math.sqrt(double) : double, 
      (double) : double
    ]
    ASTORE 4
   L4
    LINENUMBER 229 L4
    ALOAD 4: unOp
    DLOAD 0: a
    DLOAD 0: a
    DMUL
    INVOKEINTERFACE DoubleUnaryOperator.applyAsDouble (double) : double
    DSTORE 2: d
   L5
    LINENUMBER 230 L5
    RETURN
   L6
    LOCALVARIABLE a double L1 L6 0
    LOCALVARIABLE d double L3 L6 2
    LOCALVARIABLE unOp DoubleUnaryOperator L4 L6 4
    MAXSTACK = 5
    MAXLOCALS = 5
    
      public static void playLambdas();
    Code:
       0: dconst_1      
       1: invokestatic  #152                // Method ch/eiafr/cojac/models/wrappers/CommonDouble.fromDouble:(D)Lch/eiafr/cojac/models/wrappers/CommonDouble;
       4: astore_0      
       5: aload_0       
       6: ldc2_w        #36                 // double 3.0d
       9: invokestatic  #152                // Method ch/eiafr/cojac/models/wrappers/CommonDouble.fromDouble:(D)Lch/eiafr/cojac/models/wrappers/CommonDouble;
      12: invokestatic  #174                // Method ch/eiafr/cojac/models/wrappers/CommonDouble.ddiv:(Lch/eiafr/cojac/models/wrappers/CommonDouble;Lch/eiafr/cojac/models/wrappers/CommonDouble;)Lch/eiafr/cojac/models/wrappers/CommonDouble;
      15: astore_0      
      16: aload_0       
      17: astore_2      
      18: invokedynamic #175,  0            // InvokeDynamic #1:applyAsDouble:()Ljava/util/function/DoubleUnaryOperator;
      23: astore        4
      25: aload         4
      27: aload_0       
      28: aload_0       
      29: invokestatic  #178                // Method ch/eiafr/cojac/models/wrappers/CommonDouble.dmul:(Lch/eiafr/cojac/models/wrappers/CommonDouble;Lch/eiafr/cojac/models/wrappers/CommonDouble;)Lch/eiafr/cojac/models/wrappers/CommonDouble;
      32: invokestatic  #186                // Method COJAC_TYPE_CONVERT:(Lch/eiafr/cojac/models/wrappers/CommonDouble;)[Ljava/lang/Object;
      35: dup2          
      36: dup           
      37: ldc           #155                // int 0
      39: aaload        
      40: checkcast     #188                // class "[Ljava/lang/Object;"
      43: ldc           #158                // int 1
      45: aaload        
      46: checkcast     #19                 // class java/lang/Double
      49: invokevirtual #73                 // Method java/lang/Double.doubleValue:()D
      52: dup2_x1       
      53: pop2          
      54: pop           
      55: invokeinterface #42,  3           // InterfaceMethod java/util/function/DoubleUnaryOperator.applyAsDouble:(D)D
      60: dup2_x1       
      61: pop2          
      62: pop           
      63: dup2_x1       
      64: pop2          
=====>65: invokestatic  #152                // Method ch/eiafr/cojac/models/wrappers/CommonDouble.fromDouble:(D)Lch/eiafr/cojac/models/wrappers/CommonDouble;
      68: astore_2      
      69: return        
    LineNumberTable:
      line 224: 0
      line 225: 5
      line 226: 16
      line 228: 18
      line 229: 25
      line 230: 69
    LocalVariableTable:
      Start  Length  Slot  Name   Signature
          5      65     0     a   Lch/eiafr/cojac/models/wrappers/CommonDouble;
         18      52     2     d   Lch/eiafr/cojac/models/wrappers/CommonDouble;
         25      45     4  unOp   Ljava/util/function/DoubleUnaryOperator;
*/
      
  }
  //======================================================================
  static class MyWrapper { }
//    public final double dValue;
//    public final float fValue;
//    MyWrapper(double d) {dValue=d; fValue=(float)d;}
//  }
  //======================================================================
  static class SimpleGUI extends JFrame {
      public SimpleGUI() {
          super("dummy simpleGUI...");
          this.setSize(100,100);
          this.setVisible(true);
      }
      public void paint(Graphics g) {
          Graphics2D g2 = (Graphics2D) g;
          g2.setColor(Color.GREEN);
          g2.drawLine(0,0,100,100);
          Wrapping.playWithJavaDoubleWrapper();
      }
  }
}
