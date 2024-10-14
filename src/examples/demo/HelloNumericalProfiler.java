package demo;

import java.util.Random;

public class HelloNumericalProfiler {
  static Random rnd = new Random();
  static double rp() {
    return rnd.nextDouble();
  }
  static double demo() {
    double a = 5.0, b = 3.0, c = 7.0;
    double mv = Double.MAX_VALUE;
    double d = Math.sqrt(mv)*2, e = Math.sqrt(mv);
    double x = 1.22122E-6, y = 1.23E-11;
    double f, res = 0;
    res += c + a * b;          // →  safer FMA
    res += a * Math.pow(2, b); // → faster SCALB
    res -= Math.log(1.0 + x);  // →  safer LOG1P
    res += Math.exp(x) - 1.0;  // →  safer EXPM1
    res += Math.abs(rp());     // → faster ABS(alwaysPositive)
    res += Math.pow(rp(), 3);  // → faster POW(…, INT_CONST)
    f = rp() * 1E-2;
    res += Math.sin(f);        // → faster SIN_APPROX
    res += Math.hypot(a, b);   // → faster Math.sqrt()
    f = Math.sqrt(d*d + e*e);  // →  safer Math.hypot()
    if(Double.isInfinite(f))
      res += Math.hypot(d, e);
    return res;
  }

  public static void main(String[] args) {
    double res = 0.0;
    for(int i=0; i<100; i++)
      res = demo();
    System.out.println(res);
  }
  static double fullDemo() {
    double a = 5.0, b = 3.0, c = 7.0;
    double d = Math.sqrt(Double.MAX_VALUE)*2, e = Math.sqrt(Double.MAX_VALUE);
    double x = 1.22122E-6, y = 1.23E-11;
    double res = 0;
    res += a * b + c; // → FMA
    res += c + a * b; // → FMA
    res += a * Math.pow(2, b); // → SCALB
    res += Math.pow(2, b) * a; // → SCALB
    res += Math.log(x + 1.0); // → LOG1P
    res -= Math.log(1.0 + x); // → LOG1P
    res += Math.exp(x) - 1.0; // → EXPM1
    res += Math.abs(rp()); // → ABS(alwaysPositive)
    res += Math.pow(rp(), 3); // → POW(…, INT_CONST)
    res -= Math.pow(rp(), -1); // → POW(…, INT_CONST)
    res += Math.sin(Math.PI); // → SIN_APPROX
    res += Math.hypot(a, b); // → Math.sqrt() faster
    res += Math.sqrt(d*d + e*e); // → Math.hypot() safer
    return res;
  }

}
