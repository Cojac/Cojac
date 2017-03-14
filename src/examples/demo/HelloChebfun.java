// Run with "Symbolic" Wrapper: ... cojac.jar="-Rsy"


package demo;

public class HelloChebfun {
    public static String COJAC_MAGIC_toString(double n) { return ""; }
    public static double COJAC_MAGIC_identityFct() { return 0.5; }
    public static double COJAC_MAGIC_evaluateAt(double d, double x) { return 0; }
    public static double COJAC_MAGIC_derivative(double d) { return d; }
    
    public static void main(String[] args) {
        double x=COJAC_MAGIC_identityFct(); // f(x)=x
        double a = 3.0, b = a + 1;
        double c = b * (a / (a - x));
        System.out.println(COJAC_MAGIC_toString(c));
        System.out.println(COJAC_MAGIC_evaluateAt(c, 0.5));
        System.out.println(c);
        c=COJAC_MAGIC_derivative(c);
        System.out.println(COJAC_MAGIC_evaluateAt(c, 0.5));
    }
}
