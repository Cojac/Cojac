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
      System.out.println(m);
      System.out.println("... should be: ~ "+r);
  }

}
