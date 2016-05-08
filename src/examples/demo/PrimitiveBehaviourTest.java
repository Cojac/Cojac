package demo;

public class PrimitiveBehaviourTest {
    public static void main(String[] args) {
        String s = "2.32340";
        System.out.println("s= "+s+",   2s ="+2*Double.parseDouble(s));
        double a=10.000002, b=2.564654654654;
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
        a = foo(a);
        System.out.println(a);
    }
    private static double foo(double a){
        return Math.sqrt(a);
    }
}
