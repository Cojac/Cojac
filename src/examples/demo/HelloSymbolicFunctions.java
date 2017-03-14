// Run with "Symbolic" Wrapper: ... cojac.jar="-Rsy"


package demo;

public class HelloSymbolicFunctions {
    public static String COJAC_MAGIC_toString(double n) { return ""; }
    public static double COJAC_MAGIC_identityFct() { return 0; }
    public static double COJAC_MAGIC_evaluateAt(double fct, double x) { return 0; }
    public static double COJAC_MAGIC_derivative(double x) { return 0; }
    public static void main(String[] args) {
        double x=COJAC_MAGIC_identityFct();
        double a = 3.0, b = a + 1;
        double c = b * (a / (a - x));
        System.out.println(COJAC_MAGIC_toString(c));
        System.out.println(COJAC_MAGIC_evaluateAt(c, 5.0));
        System.out.println(c);
        c=COJAC_MAGIC_derivative(c);
        System.out.println(COJAC_MAGIC_toString(c));
    }
}
