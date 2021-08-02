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
 * - without Cojac:     java demo.HelloSniffer
 * - with Cojac:        java -javaagent:cojac.jar="-Xs -Xf" demo.HelloSniffer
 */

package demo;

public class HelloCmpFuzzer {

  public static void main(String[] args) {
      // In -Bfuz mode, we toggle the result of a floating point comparison when the
      // two operands are too close together.

      double a=3.2, b=a+1E-14, c=3.2002;
      System.out.print(a + " < " + b + " ? ");
      System.out.println(a<b);
      System.out.print(b + " < " + c + " ? ");
      System.out.println(b<c);
  }

}
