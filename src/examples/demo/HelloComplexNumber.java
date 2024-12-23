/*
 *    Copyright 2017 Frédéric Bapst et al.
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
 */

/*
 * To be run: with AutoDiff-reverse wrapper:
 *     java -javaagent:cojac.jar="-Rc" demo.HelloComplexNumber
 */

package demo;

import java.util.ArrayList;
import java.util.List;

public class HelloComplexNumber {
  public static String COJAC_MAGIC_toString(double a) {
    return "";
  }

  static double conjugate(double a) {
    double module = Math.abs(a);
    return module * module / a;
  }

  static double realPartOnly(double a) {
    double realPart = (a + conjugate(a))/2;
    return Math.abs(realPart); // just to ensure a possible tiny img part disappears
  }

  static double imaginaryPartOnly(double a) {
    return a - realPartOnly(a);
  }

  static double imaginaryCoefficient(double a) {
    double c = imaginaryPartOnly(a) * (-Math.sqrt(-1));
    return Math.abs(c); // just to ensure a possible tiny img part disappears
  }

  static void tinyDemo() {
    double v = Math.sqrt(-9); // = 3i
    System.out.println("Math.sqrt(-9) gives 3i, internally: " + COJAC_MAGIC_toString(v));
    double val = 5 * v * v;     // = 5*9*i^2 = -45
    System.out.println(val + " …should be '-45.0' (with COJAC!)");
    double y = 4.0 + v;
    System.out.println(y + " …should be '4 + 3i'");
    System.out.println(" conjugate: "+ conjugate(y) + " …should be '4 - 3i'");
    System.out.println("  realPart: "+ realPartOnly(y) + " …should be '4'");
    System.out.println("   imgPart: "+ imaginaryPartOnly(y) + " …should be '3i'");
    System.out.println("   imgCoef: "+ imaginaryCoefficient(y) + " …should be '3'");
  }

  // Solve a cubic equation of the form ax^3 + bx^2 + cx + d = 0 with the general cubic formula
  // This formula can be found on: https://en.wikipedia.org/wiki/Cubic_equation#General_cubic_formula
  // (we could also return a Double[] array...)
  static List<Double> cubicEquationSolutions(double a, double b, double c, double d) {
    List<Double> sols = new ArrayList<>();
    double det0 = b * b - 3 * a * c;
    double det1 = 2 * b * b * b - 9 * a * b * c + 27 * a * a * d;

    double sqrt = Math.sqrt(det1 * det1 - 4 * det0 * det0 * det0);
    if (Double.isNaN(sqrt))
      // the root can't be calculated if this value is not available.
      return sols;
    double coef = Math.cbrt((det1 - sqrt) / 2);
    if (coef == 0) {
      coef = Math.cbrt((det1 + sqrt) / 2);
    }
    double coef2 = coef * (-1 + Math.sqrt(-3)) / 2;
    double coef3 = coef * (-1 - Math.sqrt(-3)) / 2;
    if (coef == 0) {
      sols.add(-b / (3 * a));
      return sols;
    }
    double res1 = -(b + coef + det0 / coef) / (3 * a);
    double res2 = -(b + coef2 + det0 / coef2) / (3 * a);
    double res3 = -(b + coef3 + det0 / coef3) / (3 * a);
    sols.add(res1);
    sols.add(res2);
    sols.add(res3);
    return sols;
  }

  public static void main(String[] args) {
    tinyDemo();
    List<Double> sols;
    sols = cubicEquationSolutions(2, 1, 3, 1);
    String expected = "{-0.34563, -0.07719-1.20029i, -0.07719+1.20029i}";
    System.out.println(sols + "\n …should be " + expected);
    sols = cubicEquationSolutions(1, -2, -13, -10);
    expected = "{-2, -1, -5}";
    System.out.println(sols + "\n …should be " + expected);
  }
}
