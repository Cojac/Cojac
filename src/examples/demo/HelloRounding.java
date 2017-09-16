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

/* For simplicity, we don't use JUnit here (only the idea)...
 * To be run: 
 * - without Cojac:        java demo.HelloSniffer
 * - with Cojac:           java -javaagent:cojac.jar demo.HelloSniffer
 * - with Cojac filtered:  java -javaagent:cojac.jar="-Xs -Xf" demo.HelloSniffer
 */

package demo;

public class HelloRounding {
    public static double computePi() {
        double res = 0.0;
        int denom = 1;
        double tmp = 1.0;
        boolean alt = true;

        while (tmp > 1E-7) {
            tmp = 1.0 / denom;
            res += (alt?+1:-1)*tmp;
            alt = ! alt;
            denom += 2;
        }
        return res*4;
    }

  public static void main(String[] args) {
      double d=computePi();
      System.out.println(d);
  }

  /*
  Normal Java: 3.1415928535897395
  RoundUp:     3.1415928546998697
  RoundDown:   3.141592852479425
  RoundDownEm: 3.141592851369279
  RoundUpEm:   3.1415928558101998
 */
}
