/* 
 * To be run: 
 * - without Cojac:          java demo.RumpPolynomialAndChebfun
 * - with Chebfun wrapper:   java -javaagent:cojac.jar="-Rcheb" demo.RumpPolynomialAndChebfun
 */

package demo;

public class RumpPolynomialAndChebfun {
    // ----------------------------------------------------
    // ---------- COJAC MAGIC -----------------------------
    // ----------------------------------------------------
    public static String COJAC_MAGIC_toString(double n) {
        return "";
    }

    public static boolean COJAC_MAGIC_isChebfun(double a) {
        return false;
    }

    public static double COJAC_MAGIC_identityFct() {
        return Double.NaN;
    }

    public static double COJAC_MAGIC_evaluateAt(double d, double x) {
        return d;
    }

    public static double COJAC_MAGIC_derivative(double d) {
        return d;
    }
    
    public static void COJAC_MAGIC_setChebfunDomain(double min, double max) {}

    //---------------------------------------------------------------------
    
    static double pow(double base, int exp) {
        if(false) return Math.pow(base, exp);
        double r=1.0;
        while(exp-- > 0) r*=base;
        return r;
    }

    public static double rumpPolynomial1(double x, double y) {
        return x+y;
    }

    public static double rumpPolynomial(double x, double y) {
        return 1335.0*(pow(y, 6))/4.0 
                + x*x*(11.0*x*x*y*y -pow(y, 6) -121.0*pow(y, 4) -2.0)
                + 11.0*pow(y, 8)/2.0 
                + x/(2.0*y);
    }

    private static void compute(double x, double y) {
        double r=rumpPolynomial(x, y);
        System.out.println("direct eval:   f("+x+" , "+y+") = " + r);
        double xx=COJAC_MAGIC_identityFct();
        r=rumpPolynomial(xx, y);
        System.out.println("as Chebfun(x): f("+x+" , "+y+") = " + COJAC_MAGIC_toString(r));
        r=COJAC_MAGIC_evaluateAt(r, x);
        System.out.println("     it gives: f("+x+" , "+y+") = " + r);
//        double yy=COJAC_MAGIC_identityFct(dummy);
//        r=rumpPolynomial(x, yy);
//        r=COJAC_MAGIC_evaluateAt(r, y);
//        System.out.println("as Chebfun(y): f("+x+" , "+y+") = " + r);
    }
    
    public static void main0(String[] args) {
        double x=COJAC_MAGIC_identityFct();
        double r=5*x;
        System.out.println(COJAC_MAGIC_toString(r));
    }
    
    public static void main(String[] args) {
        COJAC_MAGIC_setChebfunDomain(0, 80000);

        compute(0.3, 0.8); 
        compute(0.3, 2.1); 
        compute(0.3, 3.1); 
        // unfortunately, it is not successful around the "bad point"...
        compute(77617, 33096);
    }
}
