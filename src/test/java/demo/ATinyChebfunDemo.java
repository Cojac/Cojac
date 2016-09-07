package demo;

public class ATinyChebfunDemo {

    public static String COJAC_MAGIC_toString(double n) {
        return "";
    }

    public static double COJAC_MAGIC_asChebfun(double a) {
        return a;
    }

    public static double COJAC_MAGIC_evaluateChebfunAt(double d, double x) {
        return d;
    }

    public static double COJAC_MAGIC_derivateChebfun(double d) {
        return d;
    }

    // f(x) = 3x^2 + 2x + 5
    static double myFunction(double x) {
        double res = 3 * x * x;
        res = res + 2 * x;
        res = res + 5;
        return res;
    }

    // f0(x) = 6x + 2
    static double myDerivative(double x) {
        double res = 6 * x;
        res = res + 2;
        return res;
    }

    // f(x) = ... a complicated function ;)
    static double myComplexFunction(double x) {
        double f = Math.sin(Math.PI * x);
        double s = f;
        for (int i = 0; i < 15; i++) {
            f = (3.0 / 4.0) * (1 - 2 * Math.pow(f, 4.0));
            s = s + f;
        }
        return s;
    }

    public static void main(String[] args) {

        double x = COJAC_MAGIC_asChebfun(0.0); // define a function x=x

        double f = myFunction(x);
        double resf = COJAC_MAGIC_evaluateChebfunAt(f, 0.5);
        System.out.println("f(0.5)  = " + resf + "   (" + myFunction(0.5) + ")");

        double df = COJAC_MAGIC_derivateChebfun(f);
        double resdf = COJAC_MAGIC_evaluateChebfunAt(df, 0.5);
        System.out.println("f(0.5)  = " + resdf + "  (" + myDerivative(0.5) +  ")");

        double cf = myComplexFunction(x);
        double rescf = COJAC_MAGIC_evaluateChebfunAt(cf, 0.5);
        System.out.println("f(0.5)  = " + rescf + "  (" + myComplexFunction(0.5) + ")");

        // //
        // // System.out.printf("f(x) = %s \n",COJAC_MAGIC_evaluateChebfunAt(df,
        // // 0.5));
        // // System.out.printf("f(x) = %s \n",Math.cos(0.5));

        //
        // double h = Math.sin(Math.PI * -0.00706789258810);
        // // double g = Math.cos(x);
        //
        // double r = h;
        // for (int i = 0; i < 15; i++) {
        // h = (3.0 / 4.0) * (1 - 2 * Math.pow(h, 4.0));
        // r = r + h;
        // }
        //
        // //
        // //
        // // double df= COJAC_MAGIC_derivateChebfun(f); // compute the
        // derivative
        // // of f
        // // System.out.printf("f(x) = %s s\n",s);
        // // System.out.printf("f(x) = %s s\n",f);
        // System.out.printf("f(x) = %s s\n", COJAC_MAGIC_evaluateChebfunAt(s,
        // -0.00706789258810));
        // System.out.printf("f(x) = %s s\n", r);
        // // System.out.printf("f(x) = %s should be (%s) \n",
        // // COJAC_MAGIC_evaluateChebfunAt(f, 0.5123), Math.sin(2*0.5123));
        // // System.out.printf("f'(x) = %s should be (%s) \n",
        // // COJAC_MAGIC_evaluateChebfunAt(df, 0.5123),2*Math.cos(2*0.5123));
        // System.out.println(COJAC_MAGIC_toString(s));

    }

    // public static void main(String[] args) {
    //
    // double x = COJAC_MAGIC_asChebfun(0.0); // define a function x=x
    // double a = 20.0;
    // a*=a;
    // System.out.printf("f(x) = %s s\n", a);
    //
    //
    // // double f = Math.sin(x); // apply operations on x
    //// double df =COJAC_MAGIC_derivateChebfun(f);
    //// df =COJAC_MAGIC_derivateChebfun(df);
    //// df =COJAC_MAGIC_derivateChebfun(df);
    //
    ////
    //// System.out.printf("f(x) = %s \n",COJAC_MAGIC_evaluateChebfunAt(df,
    // 0.5));
    //// System.out.printf("f(x) = %s \n",Math.cos(0.5));
    // double f = Math.sin(Math.PI*x);
    //// double g = Math.cos(x);
    //
    // double s = f;
    // for (int i = 0; i < 15; i++) {
    // f = (3.0/4.0)*(1 - 2*Math.pow(f, 4.0));
    // s = s + f;
    // }
    //
    // double h = Math.sin(Math.PI*-0.00706789258810);
    //// double g = Math.cos(x);
    //
    // double r = h;
    // for (int i = 0; i < 15; i++) {
    // h = (3.0/4.0)*(1 - 2*Math.pow(h, 4.0));
    // r = r + h;
    // }
    //
    //
    ////
    ////
    //// double df= COJAC_MAGIC_derivateChebfun(f); // compute the derivative of
    // f
    //// System.out.printf("f(x) = %s s\n",s);
    //// System.out.printf("f(x) = %s s\n",f);
    // System.out.printf("f(x) = %s s\n", COJAC_MAGIC_evaluateChebfunAt(s,
    // -0.00706789258810));
    // System.out.printf("f(x) = %s s\n", r);
    //// System.out.printf("f(x) = %s should be (%s) \n",
    // COJAC_MAGIC_evaluateChebfunAt(f, 0.5123), Math.sin(2*0.5123));
    //// System.out.printf("f'(x) = %s should be (%s) \n",
    // COJAC_MAGIC_evaluateChebfunAt(df, 0.5123),2*Math.cos(2*0.5123));
    // System.out.println(COJAC_MAGIC_toString(s));
    //
    // }

}
