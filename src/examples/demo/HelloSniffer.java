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

public class HelloSniffer {
  static int powerModA(int x, int y, int z) {
    int res=1;
    while(y-- > 0)
      res = res*x;
    return res % z;
  }

  static int powerModB(int x, int y, int z) {
    return (int) Math.pow(x,y) % z;
  }

  public static void tinyTest() {
    int a,b;
    a= powerModA(2, 4, 5);
    b= powerModB(2, 4, 5);
    assertEquals(1, a);
    assertEquals(1, b);
    a= powerModA(497, 1854, 281);
    b= powerModB(497, 1854, 281);
    assertEquals(157, a);
    assertEquals(157, b);
  }
  
  
  private static void assertEquals(int expected, int effective) {
    if (expected != effective)
        throw new RuntimeException("Bad news: expected "+expected+" got "+effective);
  }

  public static void main(String[] args) {
      tinyTest();
      System.out.println("End of tinyTest");
  }

}
