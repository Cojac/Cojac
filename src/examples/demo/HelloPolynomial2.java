/* 
 * To be run: 
 * - without Cojac:             java demo.HelloPolynomial2
 * - with Derivation wrapper:   java -javaagent:cojac.jar="-Ra" demo.HelloPolynomial1
 */

package demo;

public class HelloPolynomial2 {

    static double pow(double base, int exp) {
        //if(true) return Math.pow(base,  exp);
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

    public static double COJAC_MAGIC_derivative(double a) { return 0; }
    public static double asDerivativeVar(double a) { return a; }

    public static void main(String[] args) {
        double r, x, y;
        x=2.0; y=3.0;
        x=asDerivativeVar(x);
        r=somePolynomial(x, y);
        System.out.println("f (x,y): "+r);
        System.out.println("f'(x,y): "+COJAC_MAGIC_derivative(r));
        
        x=2.0; y=3.0;
        y=asDerivativeVar(y);
        r=somePolynomial(x, y);
        System.out.println("f (x,y): "+r);
        System.out.println("f'(x,y): "+COJAC_MAGIC_derivative(r));
    }
}
