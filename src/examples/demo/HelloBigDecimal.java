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

/* To be run: 
 * $ java demo.HelloBigDecimal
 * $ java -javaagent:cojac.jar="-Rb 50" demo.HelloBigDecimal
 */

package demo;

public class HelloBigDecimal {
    static double mullerRecurrence(int n) {
        double[] u = new double[n+1];
        u[0] = +2.0; 
        u[1] = -4.0;
        for(int i=2; i<=n; i++)
            u[i] = 111 - 1130/u[i-1] + 3000/(u[i-1]*u[i-2]);
        return u[n];
    }

    public static void main(String[] args) {
        double m = mullerRecurrence(20);
        double r = 6.04;
        System.out.print(m);
        System.out.println("  ... should be: ~ "+r);
    }

}
