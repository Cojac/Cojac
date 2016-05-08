package demo;

public class PrimitiveBehaviourTest {
    static final long EXP_MASK =  0x7ff0000000000000L;
    static final long MANTISSA_MASK = 0x000fffffffffffffL;
    static final long SIGN_MASK = 0x8000000000000000L;
    public static void main(String[] args) {
        float one = 1.0f;
        double oneD = 1.0;
        
        System.out.println("ulp(1) in float: "+Math.nextDown(one));
        System.out.println("ulp(1) in double: "+Math.nextDown(oneD));
        System.out.println("ulp(1) in float ");
        printDouble(Math.nextUp(one));
        System.out.println("\n\n\n\n\n\n******************\nulp(1) in double ");
        printDouble(Math.nextUp(oneD));
        
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
    public static void printDouble(double a){
        printDouble(a,0);
    }
    public static void printDouble(double a, int spacing){
        String space = "";
        for (int i = 0; i < spacing; i++) {
            space +="\t";
        }
        Long longA = Double.doubleToLongBits(a);
        System.out.println(space+"Double    :"+a);
        System.out.println(space+"Binary    :"+Long.toBinaryString(longA));
        System.out.println(space+"Raw binary:"+Long.toBinaryString(Double.doubleToRawLongBits(a)));
        System.out.println(space+"Sign      :"+Long.toBinaryString(longA & SIGN_MASK).charAt(0));
        String s = Long.toBinaryString(longA & EXP_MASK);
        System.out.println(space+"Exponant  :"+s.substring(0, Math.min(10, s.length())));
        System.out.println(space+"Mantissa  :"+Long.toBinaryString(longA & MANTISSA_MASK));
        
        if(spacing ==0){
            System.out.println("\tULP");
            printDouble(Math.ulp(a),1);
        }
    }
}
