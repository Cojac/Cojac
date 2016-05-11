package demo;

public class ATinySymbolicDemo {

    public static String COJAC_MAGIC_toString(double n) {
        return "";
    }

    public static double COJAC_MAGIC_asSymbolicUnknown(double a) {
        return a;
    }

    public static double COJAC_MAGIC_evaluateSymbolicAt(double d, double x) {
        return d;
    }

    public static double COJAC_MAGIC_evaluateBetterSymbolicAt(double d, double x) {
        return d;
    }

    public static double COJAC_MAGIC_derivateSymbolic(double d) {
        return d;
    }

    // f(x) = 3x^2 + 2x + 5
    static double myFunction(double x) {
        double res = 3 * x * x; // the computation can be complex,
        res = res + 2 * x; // with loops, calls, recursion etc.
        res = res + 5;
        return res; 
    }

    public static void main(String[] args) {
        double x = COJAC_MAGIC_asSymbolicUnknown(0.0); // define the unknown
        double f = myFunction(x);                      // define the function
        double df = COJAC_MAGIC_derivateSymbolic(f);   // compute the derivative
        System.out.println("f(x)  = " + COJAC_MAGIC_toString(f));  // print the function "f"
        System.out.println("f'(x) = " + COJAC_MAGIC_toString(df)); // print the derivative of "f"
        System.out.println("f(2)  = " + COJAC_MAGIC_evaluateSymbolicAt(f, 2)); // compute the result of the function
        System.out.println("f'(2) = " + COJAC_MAGIC_evaluateSymbolicAt(df, 2));// compute the result of the derivative
        
        double[] t = {+2, 1E-16, 1E-16, 1E-16, 5E-17, 5E-17};
        double sum = 0;
        for(double e:t) sum += e;
        System.out.println("sum  = " + COJAC_MAGIC_evaluateSymbolicAt(sum, 0.0));      // compute the sum (standard)
        System.out.println("sum  = " + COJAC_MAGIC_evaluateBetterSymbolicAt(sum, 0.0));// compute the sum (better)
        
        
    }

}
