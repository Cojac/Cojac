// compile this to a jar and launch it with the following command :
/*
    java -javaagent:cojac.jar="-AD" -jar myJar.jar
 */

public class DerivationTest
{
    private static int nbrTestPassed = 0;
    private static final double epsilon = 0.1;

    public static void main(String[] args)
    {
        runDerivationTest();
    }

    /*
        Magic method, see cojac implementation of those in the DerivationDouble or DerivationFloat classes
     */
    public static double COJAC_MAGIC_DOUBLE_getDerivation(double a)
    {
        return a;
    }

    public static double COJAC_MAGIC_DOUBLE_specifieToDerivate(double a)
    {
        return a;
    }

    public static double f1(double x)
    {
        return 4.0 * Math.pow(x, 3.0);
    }

    public static double df1(double x)
    {
        return 12.0 * Math.pow(x, 2.0);
    }

    public static double f2(double x)
    {
        return Math.pow(x, x);
    }

    public static double df2(double x)
    {
        return Math.pow(x, x) * (Math.log(x) + 1.0);
    }

    public static double f3(double x)
    {
        return Math.sin(x) + 4.0 * x;
    }

    public static double df3(double x)
    {
        return Math.cos(x) + 4.0;
    }

    public static double f4(double x)
    {
        return Math.cos(Math.pow(x,2.0)) - 4.0 * x + 3;
    }

    public static double df4(double x)
    {
        return -Math.sin(Math.pow(x, 2.0)) * 2.0 * x - 4.0;
    }

    public static double f5(double x)
    {
        return 4.0 * x + Math.tan(x);
    }

    public static double df5(double x)
    {
        return 1.0 + Math.pow(Math.tan(x), 2.0) + 4.0;
    }

    public static double f6(double x)
    {
        return 1 / Math.sqrt(x);
    }

    public static double df6(double x)
    {
        return -1.0 / (2.0 * Math.pow(x, 3.0 / 2.0));
    }

    public static double f7(double x)
    {
        return -Math.log(x);
    }

    public static double df7(double x)
    {
        return -1.0 / x;
    }

    public static double f8(double x)
    {
        return Math.sinh(x) + Math.cosh(x) + Math.tanh(x);
    }

    public static double df8(double x)
    {
        return Math.sinh(x) + Math.cosh(x) + 1.0 - Math.pow(Math.tanh(x), 2.0);
    }

    public static double f9(double x)
    {
        return Math.asin(x) + Math.acos(x) + Math.atan(x);
    }

    public static double df9(double x)
    {
        return 1.0 / (Math.pow(x, 2.0) + 1.0);
    }

    public static double f10(double x)
    {
        return Math.exp(Math.pow(x, 2.0));
    }

    public static double df10(double x)
    {
        return Math.exp(Math.pow(x, 2.0)) * 2.0 * x;
    }

    public static double f11(double x)
    {
        return 3.0 % x;
    }

    public static double df11(double x)
    {
        return Double.NaN;
    }

    public static double f12(double x)
    {
        return x % 3.0;
    }

    public static double df12(double x)
    {
        return 1.0;
    }

    public static double f13(double x)
    {
        return Math.abs(x);
    }

    public static double df13(double x)
    {
        return x < 0.0 ? -1.0 : 1.0;
    }

    public static void runF1()
    {
        System.out.println("Function 1");
        double x = 4.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f1(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f1(x) = " + res);
        System.out.println("f1'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df1(x));
        if(Math.abs(dRes - df1(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF2()
    {
        System.out.println("Function 2");
        double x = 4.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f2(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f2(x) = " + res);
        System.out.println("f2'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df2(x));
        if(Math.abs(dRes - df2(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF3()
    {
        System.out.println("Function 3");
        double x = 4.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f3(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f3(x) = " + res);
        System.out.println("f3'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df3(x));
        if(Math.abs(dRes - df3(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF4()
    {
        System.out.println("Function 4");
        double x = 1.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f4(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f4(x) = " + res);
        System.out.println("f4'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df4(x));
        if(Math.abs(dRes - df4(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF5()
    {
        System.out.println("Function 5");
        double x = 4.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f5(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f5(x) = " + res);
        System.out.println("f5'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df5(x));
        if(Math.abs(dRes - df5(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF6()
    {
        System.out.println("Function 6");
        double x = 4.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f6(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f6(x) = " + res);
        System.out.println("f6'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df6(x));
        if(Math.abs(dRes - df6(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF7()
    {
        System.out.println("Function 7");
        double x = 4.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f7(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f7(x) = " + res);
        System.out.println("f7'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df7(x));
        if(Math.abs(dRes - df7(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF8()
    {
        System.out.println("Function 8");
        double x = 4.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f8(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f8(x) = " + res);
        System.out.println("f8'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df8(x));
        if(Math.abs(dRes - df8(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF9()
    {
        System.out.println("Function 9");
        double x = 0.4;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f9(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f9(x) = " + res);
        System.out.println("f9'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df9(x));
        if(Math.abs(dRes - df9(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF10()
    {
        System.out.println("Function 10");
        double x = 4.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f10(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f10(x) = " + res);
        System.out.println("f10'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df10(x));
        if(Math.abs(dRes - df10(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF11()
    {
        System.out.println("Function 11");
        double x = 4.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f11(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f11(x) = " + res);
        System.out.println("f11'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df11(x));
        if(Double.isNaN(dRes))
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF12()
    {
        System.out.println("Function 12");
        double x = 4.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f12(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f12(x) = " + res);
        System.out.println("f12'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df12(x));
        if(Math.abs(dRes - df12(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runF13()
    {
        System.out.println("Function 13");
        double x = 4.0;
        x = COJAC_MAGIC_DOUBLE_specifieToDerivate(x);
        double res = f13(x);
        double dRes = COJAC_MAGIC_DOUBLE_getDerivation(res);
        System.out.println("f13(x) = " + res);
        System.out.println("f13'(x) = " + COJAC_MAGIC_DOUBLE_getDerivation(res) + " should be " + df13(x));
        if(Math.abs(dRes - df13(x)) < epsilon)
        {
            System.out.println("Test ok");
            nbrTestPassed++;
        }
    }

    public static void runDerivationTest()
    {
        runF1();
        runF2();
        runF3();
        runF4();
        runF5();
        runF6();
        runF7();
        runF8();
        runF9();
        runF10();
        runF11();
        runF12();
        runF13();
        System.out.println("\n");
        if(nbrTestPassed == 13)
        {
            System.out.println("All test passed successfully !");
        }
    }
}
