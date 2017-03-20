/* For simplicity, we don't use JUnit here (only the idea)...
 * To be run: 
 * - without Cojac:        java demo.HelloSniffer
 * - with Cojac:           java -javaagent:cojac.jar demo.HelloSniffer
 * - with Cojac filtered:  java -javaagent:cojac.jar="-Xs -Xf" demo.HelloSniffer
 */

package demo;

public class HelloCmpFuzzer {

  public static void main(String[] args) {
      double a=3.2, b=a+1E-14, c=3.2002;
      System.out.println(a<b);
      System.out.println(a>b);
      System.out.println(b<c);
      a=2;
      System.out.println(a+2);
      System.out.println("End of demo");
  }

}
