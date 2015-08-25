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

"-R -FW cojac.BasicFloat -DW cojac.BasicDouble"
"-BDP 3"
... soon:
"-STO"
"-I"
"-AD"

 ------------------------------------------ */

public class Wrapping {  

  public static String COJAC_MAGIC_DOUBLE_wrapper() { return ""; }
  public static String COJAC_MAGIC_DOUBLE_toStr(double n) { return ""; }
  public static String COJAC_MAGIC_FLOAT_toStr(float  n) { return ""; }

  private static final String WRAPPER=COJAC_MAGIC_DOUBLE_wrapper();
    
  public static void main(String[] args) {
//      System.out.println("yeahhh "+(java.util.function.DoubleUnaryOperator)Math::sqrt);

    System.out.println("Wrapper used: "+WRAPPER);
    playWithPrimitiveFloats();
    playWithPrimitiveDoubles();
    playWithJavaFloatWrapper();
    playWithJavaDoubleWrapper();
    playWithArrays();
    playWithCollection();
    playWithGUI();
    System.out.println("The end.");    
  }
  
  public static void playWithPrimitiveFloats() {
    final float a=0.5f;
    float f=a;
    f=twiceFloat(f)/2; ok(f==a);
    f=f*2; f=f/2; ok(f==a);
    f=f-1; f=f+1; ok(f==a);
    System.out.println(" A "+COJAC_MAGIC_FLOAT_toStr(f));
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
    String s=COJAC_MAGIC_FLOAT_toStr(f);
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
    String s=COJAC_MAGIC_DOUBLE_toStr(d);
    ok((WRAPPER.length()>0) == (s.length()>0));
    if (WRAPPER.equals("BigDecimal")) {
        double x=1.0;
        x = x/3.0;
        String s1=""+x;
        String s2=COJAC_MAGIC_DOUBLE_toStr(x);
        ok(s2.length()>s1.length());
    }
    
    //DoubleUnaryOperator[] mathDoubleUnaryOps= {Math::sqrt};

    // java.lang.NoSuchMethodError: demo.Wrapping.lambda$0(D)D
    //DoubleUnaryOperator[] mathDoubleUnaryOps= {(x->x+2)};
    
    //java.lang.AbstractMethodError: Method demo/Wrapping$$Lambda$6.applyAsDouble(D)D is abstract
    //  at demo.Wrapping$$Lambda$6/1826771953.applyAsDouble(Unknown Source)

    //for(DoubleUnaryOperator op:mathDoubleUnaryOps) d=op.applyAsDouble(a);
    
    // TODO: this does not work with our wrapping mechanism... :-(
    // DoubleUnaryOperator[] mathDoubleUnaryOps= {Math::sqrt, Math::sin, Math::cos, Math::tan};
    // for(DoubleUnaryOperator op:mathDoubleUnaryOps) d=op.applyAsDouble(a);
    // Although this works:
    //   IntUnaryOperator[] muo= {Math::abs};
    //   int u=-9;
    //   for(IntUnaryOperator op:muo) u=op.applyAsInt(u);
  }

  /** works only if d has a chance to have a long development, and only 
   * for BigDecimal wrapping (with a number of digits > 16) */
  static boolean keepsEnrichment(double d) {
      if (!WRAPPER.equals("BigDecimal")) return true;
      String s1=""+d;
      String s2=COJAC_MAGIC_DOUBLE_toStr(d);
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
    MyCojacDebugDump.f(3);
    //myCojacDebugDump.f(new ArrayList<Double>());
    double a=1.0;
    double b= a/3.0;
    System.out.println("oneThird orig: "+b);
    System.out.println("oneThird: "+COJAC_MAGIC_DOUBLE_toStr(b));
    ArrayList<MyWrapper> l1=new ArrayList<>();
    l1.add(new MyWrapper(b));
    double c=l1.get(0).dValue;
    ArrayList<Double> l2=new ArrayList<>();
    l2.add(b); l2.add(b/2);
    Collections.sort(l2);
    System.out.println("oneThird in sorted list: "+COJAC_MAGIC_DOUBLE_toStr(l2.get(0)));
    ok(keepsEnrichment(l2.get(0)));

    System.out.println("oneThird in list: "+COJAC_MAGIC_DOUBLE_toStr(c));
    Double d=b;
    System.out.println("oneThird origWrapper: "+b);
    System.out.println("oneThird wrapper magic: "+COJAC_MAGIC_DOUBLE_toStr(d));
  }

  public static void playWithArrays() { //TODO: rewrite
    double a=1.0;
    double b= a/3.0;
    double[]t=new double[]{b, b/2};
    Arrays.sort(t);
    System.out.println("oneThird in sorted primitive array: "+COJAC_MAGIC_DOUBLE_toStr(t[0]));   
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
  static class MyCojacDebugDump {
      public MyCojacDebugDump(int a) {
          
      }
      
      public static int f(int a) {
          return 2*a;
      }
      public static void f(ArrayList<Double> c) {
          double b=1; b/=3.0;
          Double[]t2=new Double[]{b, b/2};
          Arrays.sort(t2);
          System.out.println("oneThird in sorted jwrapper array: "+COJAC_MAGIC_DOUBLE_toStr(t2[0]));   

          Double d=3.0;
          if(d.equals(d))
              c.add(d);
      }
      static int g(int a) {
          try {
              return 3/a;
          } catch(NoSuchMethodError|AbstractMethodError e) {
              return a/7;
          }
      }
  }
  //======================================================================
  static class MyWrapper {
    public final double dValue;
    public final float fValue;
    MyWrapper(double d) {dValue=d; fValue=(float)d;}
  }
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
