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
 *     java -javaagent:cojac.jar="-Rar" demo.HelloAutodiffReverse
 */
// TODO - update command above

package demo;

public class HelloComplexNumber {

    // Find a root of a cubic equation of the form ax^3 + bx^2 + cx + d = 0 with the general cubic formula
    // This formula can be found on wikipedia: https://en.wikipedia.org/wiki/Cubic_equation#General_cubic_formula
    static double solveCubicEquation(double a, double b, double c, double d) {
        double det0 = b * b - 3 * a * c;
        double det1 = 2 * b * b * b - 9 * a * b * c + 27 * a * a * d;

        double sqrt = Math.sqrt(det1 * det1 - 4 * det0 * det0 * det0);
        if (Double.isNaN(sqrt))
            // the root can't be calculated if this value is not available.
            return Double.NaN;
        double coef = Math.cbrt((det1 + sqrt) / 2);

        if (coef == 0) {
            coef = Math.cbrt((det1 - sqrt) / 2);
        }
        if (coef == 0) {
            return -b / (3 * a);
        }
        return -(b + coef + det0 / coef) / (3 * a);
    }

    public static void main(String[] args) {
        System.out.println(solveCubicEquation(2, 1, 3, 1) + " should be ≈ -0.34563");
        System.out.println(solveCubicEquation(1, -2, -13, -10) + " should be -1, -2 or 5");
    }
}
