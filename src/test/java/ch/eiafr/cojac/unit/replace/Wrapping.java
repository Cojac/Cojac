package ch.eiafr.cojac.unit.replace;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;
import org.junit.Assert;

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
//    playWithArrays();
//    playWithCollection();
    //System.out.println("The end.");
  }
  
  public static void playWithPrimitiveFloats() {
    final float a=0.5f;
    float f=a;
    f=twiceFloat(f)/2; ok(f==a);
    f=f*2; f=f/2; ok(f==a);
    f=f-1; f=f+1; ok(f==a);
    f=f%(f+1); ok(f==a);
    //System.out.println(" A "+COJAC_MAGIC_FLOAT_toStr(f));
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
    
    // TODO: this does not work with our wrapping mechanism... :-(
    // DoubleUnaryOperator[] mathDoubleUnaryOps= {Math::sqrt, Math::sin, Math::cos, Math::tan};
    // for(DoubleUnaryOperator op:mathDoubleUnaryOps) d=op.applyAsDouble(a);
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
    ok(d.equals(a));  //TODO: this does not work under surefire (yet it does work under JUnit..)
    ok(!d.isNaN());
    ok(!d.isInfinite());
  }

  private static float  twiceFloat(float f)  { return 2f*f; }
  private static double twiceDouble(double d) { return 2*d;  }

  public static void playWithCollection() {  //TODO: rewrite
    double a=1.0;
    double b= a/3.0;
    System.out.println("oneThird orig: "+b);
    System.out.println("oneThird: "+COJAC_MAGIC_DOUBLE_toStr(b));
    ArrayList<MyWrapper> l1=new ArrayList<>();
    l1.add(new MyWrapper(b));
    double c=l1.get(0).dValue;
    ArrayList<Double> l2=new ArrayList<>();
    l2.add(b);

    System.out.println("oneThird in list: "+COJAC_MAGIC_DOUBLE_toStr(c));
    Double d=b;
    System.out.println("oneThird origWrapper: "+b);
    System.out.println("oneThird wrapper magic: "+COJAC_MAGIC_DOUBLE_toStr(d));
  }

  public static void playWithArrays() { //TODO: rewrite
    double a=1.0;
    double b= a/3.0;
    double[]t=new double[]{b};
    Arrays.sort(t);
  }
  
  private static void ok(boolean b) {
      Assert.assertTrue("bad news...", b);
      //if (!b) throw new RuntimeException("bad news...");
  }
  //======================================================================
  static class MyWrapper {
    public final double dValue;
    public final float fValue;
    MyWrapper(double d) { dValue=d; fValue=(float)d; }
  }
}
