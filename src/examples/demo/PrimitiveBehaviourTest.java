package demo;

public class PrimitiveBehaviourTest {
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        double a=10.000002, b=2;
        Double c = new Double(3);
        System.out.println("a = "+Double.toString(a)+", b = "+Double.toString(b)+ ", c= "+Double.toString(c));
        System.out.println(Double.toString(c));
        System.out.println("a + b = "+Double.toString(a+b));
        System.out.println("a - b = "+Double.toString(a-b));
        System.out.println("a * b = "+Double.toString(a*b));
        System.out.println("a / b = "+Double.toString(a/b));
        System.out.println("a % b = "+Double.toString(a%b));
        System.out.println("a + b = "+(a+b));
        System.out.println("a - b = "+(a-b));
        System.out.println("a * b = "+a*b);
        System.out.println("a / b = "+a/b);
        System.out.println("a % b = "+a%b);
        System.out.println("a < b = "+(a<b));
        System.out.println("sqrt(a)  = "+Double.toString(Math.sqrt(a))+"\nin double: 3.1622779763961297");
        System.out.println("pow(4,2) = "+Double.toString(Math.pow(4,2)));
    }
}
