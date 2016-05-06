package demo;

public class PrimitiveBehaviourTest {
    public static void main(String[] args) {
        for(String s: args){
            System.out.println("arg: "+s);
        }
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
        
        double d = 1.0;
        double e = 1E-16;
        System.out.println(" d + e = " +(d+e));
        System.out.println(" d - e = " +(d-e));
        System.out.println("-d - e = " +(-d-e));
        System.out.println("-d + e = " +(-d+e));
    }
}
