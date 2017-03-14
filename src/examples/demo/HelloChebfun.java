// Run with "Symbolic" Wrapper: ... cojac.jar="-Rsy"


package demo;

public class HelloChebfun {
    public static String COJAC_MAGIC_toString(double n) { return ""; }
    public static double COJAC_MAGIC_asChebfun() { return 0.5; }
    public static double COJAC_MAGIC_evaluateChebfunAt(double d, double x) { return 0; }
    public static double COJAC_MAGIC_derivateChebfun(double d) { return d; }
    
    public static void main(String[] args) {
        double x=COJAC_MAGIC_asChebfun(); // f(x)=x
        double a = 3.0, b = a + 1;
        double c = b * (a / (a - x));
        System.out.println(COJAC_MAGIC_toString(c));
        System.out.println(COJAC_MAGIC_evaluateChebfunAt(c, 0.5));
        System.out.println(c);
    }
}
