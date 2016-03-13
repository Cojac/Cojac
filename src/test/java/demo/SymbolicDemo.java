/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst & HEIA-FR students
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

// compile this to a jar and launch it with the following command :
// java -javaagent:cojac.jar="-Ra"

package demo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import com.github.cojac.models.wrappers.CommonDouble;
import com.github.cojac.models.wrappers.WrapperSymbolic;

public class SymbolicDemo {
    private static int nbrTestPassed = 0;
    private static final double epsilon = 0.1;
    private static Map<String, Method> methods;

    static {
        methods = new TreeMap<>();
        for (Method method : SymbolicDemo.class.getDeclaredMethods()) {
            methods.put(method.getName(), method);
        } // end for
    }

    // ----------------------------------------------------

    public static void main(String[] args) {
        // main1( args );
        runSymbolicTest();

        System.out.println("\n");
        if (nbrTestPassed == 13) {
            System.out.println("All test passed successfully !");
        }
    }

    // ----------------------------------------------------

    static double someFunction(double x, double a, double b) {
        System.out.println("double res = a * x * x;");
        double res = a * x * x;
        System.out.println("res = res * b * x;");
        res = res * b + x;
        System.out.println("res = res * 1;");
        res = res * 1;
        System.out.println("return res;");
        return res;
    }

    public static void main1(String[] args) {
        System.out.println("double x = 2;");
        double x = 2;
        System.out.println("x = COJAC_MAGIC_asSymbolicTarget( x );");
        x = COJAC_MAGIC_asSymbolicUnknown(x);
        System.out.println("double y = someFunction( x, 3, 4 );");
        double y = someFunction(x, 3, 4);
        System.out.println("f(2):  " + y);
        System.out.println("x : " + COJAC_MAGIC_isSymbolicUnknown(x));
        System.out.println("f(10): " + COJAC_MAGIC_evaluateSymbolicAt(y, 10));

    }

    public static void runSymbolicTest() {
        // run the Symbolic functions 1 to 13
        double[] xs = new double[]{4.0, 4.0, 4.0, 1.0, 4.0, 4.0, 4.0, 4.0, 0.4,
                4.0, 4.0, 4.0, 4.0};
        for (int i = 1; i <= 13; i++) {
            try {
                runFx(i, xs[i - 1]);
            } catch (InvocationTargetException | IllegalAccessException
                    | NoSuchMethodException e) {
                e.printStackTrace();
            }
        } // end for

    }

    // ----------------------------------------------------

    public static void runFx(int fx, double x) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        Method f = methods.get("f" + fx);
        Method df = methods.get("df" + fx);

        if (f == null || df == null) {
            throw new NoSuchMethodException(fx + " not found...");
        }

        System.out.printf("Function %d\n", fx);
        double res = (double) f.invoke(SymbolicDemo.class, x);
        double u = COJAC_MAGIC_asSymbolicUnknown(0);
        double y = (double) f.invoke(SymbolicDemo.class, u);
        double symRes = COJAC_MAGIC_evaluateSymbolicAt(y, x);

        System.out.printf("f%d(x) = %s \n", fx, COJAC_MAGIC_toString(y));
        System.out.printf("f%d(%s) = %s \n", fx, x, symRes);

        if (Math.abs(res - symRes) < epsilon) {
            System.out.println("Test ok");
            nbrTestPassed++;
        }

    }

    // ----------------------------------------------------

    /*
     * Magic method, see cojac implementation of those in the SymbolicDouble
     */
    public static String COJAC_MAGIC_toString(double n) {
        return "";
    }

    public static String COJAC_MAGIC_toString(float n) {
        return "";
    }

    public static double COJAC_MAGIC_getSymbolic(double a) {
        return 0;
    }

    public static boolean COJAC_MAGIC_isSymbolicUnknown(double a) {
        return false;
    }

    public static double COJAC_MAGIC_asSymbolicTarget(double a) {
        return a;
    }

    public static double COJAC_MAGIC_asSymbolicUnknown(double a) {
        return a;
    }

    public static double COJAC_MAGIC_evaluateSymbolicAt(double d, double x) {
        return d;
    }

    public static double f1(double x) {
        return 4.0 * Math.pow(x, 3.0);
    }

    public static double df1(double x) {
        return 12.0 * Math.pow(x, 2.0);
    }

    public static double f2(double x) {
        return Math.pow(x, x);
    }

    public static double df2(double x) {
        return Math.pow(x, x) * (Math.log(x) + 1.0);
    }

    public static double f3(double x) {
        return Math.sin(x) + 4.0 * x;
    }

    public static double df3(double x) {
        return Math.cos(x) + 4.0;
    }

    public static double f4(double x) {
        return Math.cos(Math.pow(x, 2.0)) - 4.0 * x + 3;
    }

    public static double df4(double x) {
        return -Math.sin(Math.pow(x, 2.0)) * 2.0 * x - 4.0;
    }

    public static double f5(double x) {
        return 4.0 * x + Math.tan(x);
    }

    public static double df5(double x) {
        return 1.0 + Math.pow(Math.tan(x), 2.0) + 4.0;
    }

    public static double f6(double x) {
        return 1 / Math.sqrt(x);
    }

    public static double df6(double x) {
        return -1.0 / (2.0 * Math.pow(x, 3.0 / 2.0));
    }

    public static double f7(double x) {
        return -Math.log(x);
    }

    public static double df7(double x) {
        return -1.0 / x;
    }

    public static double f8(double x) {
        return Math.sinh(x) + Math.cosh(x) + Math.tanh(x);
    }

    public static double df8(double x) {
        return Math.sinh(x) + Math.cosh(x) + 1.0 - Math.pow(Math.tanh(x), 2.0);
    }

    public static double f9(double x) {
        return Math.asin(x) + Math.acos(x) + Math.atan(x);
    }

    public static double df9(double x) {
        return 1.0 / (Math.pow(x, 2.0) + 1.0);
    }

    public static double f10(double x) {
        return Math.exp(Math.pow(x, 2.0));
    }

    public static double df10(double x) {
        return Math.exp(Math.pow(x, 2.0)) * 2.0 * x;
    }

    public static double f11(double x) {
        return 3.0 % x;
    }

    public static double df11(double x) {
        return Double.NaN;
    }

    public static double f12(double x) {
        return x % 3.0;
    }

    public static double df12(double x) {
        return 1.0;
    }

    public static double f13(double x) {
        return Math.abs(x);
    }

    public static double df13(double x) {
        return x < 0.0 ? -1.0 : 1.0;
    }

}
