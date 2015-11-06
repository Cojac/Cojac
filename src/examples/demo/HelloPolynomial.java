package demo;

public class HelloPolynomial {
  
  static double pow(double base, int exp) {
    double r=1.0;
    while(exp-- > 0) r*=base;
    return r;
  }
  
  public static double somePolynomial(double x, double y) {
    return 1335.0*(pow(y, 6))/4.0 
         + x*x*(11.0*x*x*y*y -pow(y, 6) -121.0*pow(y, 4) -2.0)
         + 11.0*pow(y, 8)/2.0 
         + x/(2.0*y);
  }
  
  public static double COJAC_MAGIC_getDerivation(double a) { return 0;  }
  public static double COJAC_MAGIC_asDerivationTarget(double a) { return a; }

  public static void main1(String[] args) {
    double r, x, y;
    x=2.0; y=3.0;
    r=somePolynomial(x, y);
    System.out.println(r);
  }
  
  public static void main2(String[] args) {
    double r, x, y;
    x=77617; y=33096;
    r=somePolynomial(x, y);
    // the exact value is -54767/66192.0 ~= -0.8274
    // without Cojac: -1.1805916207174113E21
    // with "-Rb 40": -0.8273960599468214
    System.out.println(r);
  }
  
  public static void main3(String[] args) {
    double r, x, y;
    x=2.0; y=3.0;
    x=COJAC_MAGIC_asDerivationTarget(x);
    r=somePolynomial(x, y);
    System.out.println("f (x,y): "+r);
    System.out.println("f'(x,y): "+COJAC_MAGIC_getDerivation(r));
  }
  
  public static void main4(String[] args) {
    double r, x, y;
    x=2.0; y=3.0;
    y=COJAC_MAGIC_asDerivationTarget(y);
    r=somePolynomial(x, y);
    System.out.println("f (x,y): "+r);
    System.out.println("f'(x,y): "+COJAC_MAGIC_getDerivation(r));
  }

  public static void main(String[] args) {
    rumpWithFloat(); System.exit(0);
    main1(args);
    main2(args);
    main3(args);
    main4(args);
  }
  
  public static float rumpPolynom(float x, float y) {
    return 1335.0F * ((float) pow(y, 6)) / 4.0F +
        x * x * (11F * x * x * y * y - (float) pow(y, 6) -
            121.0F * (float) pow(y, 4) - 2.0F) +
        11.0F * (float) pow(y, 8) / 2.0F + x / (2.0F * y);
  }

  public static void rumpWithFloat() {
    System.out.println("\n--- Test rump Polynom on floats");
    float a = rumpPolynom(77617.0F, 33096.0F);
    System.out.println(a);
}

}
