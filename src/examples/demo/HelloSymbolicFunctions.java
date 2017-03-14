// Run with "Symbolic" Wrapper: ... cojac.jar="-Rsy"


package demo;

public class HelloSymbolicFunctions {
    public static String COJAC_MAGIC_toString(double n) { return ""; }
    public static double COJAC_MAGIC_asSymbolicUnknown() { return 0; }
    public static double COJAC_MAGIC_evaluateSymbolicAt(double fct, double x) { return 0; }
    public static void main(String[] args) {
        double x=COJAC_MAGIC_asSymbolicUnknown();
        double a = 3.0, b = a + 1;
        double c = b * (a / (a - x));
        System.out.println(COJAC_MAGIC_toString(c));
        System.out.println(COJAC_MAGIC_evaluateSymbolicAt(c, 5.0));
        System.out.println(c);
    }
}
