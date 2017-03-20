// Run with "Symbolic" Wrapper: ... cojac.jar="-Rsymb"


package demo;

public class HelloSymbolicExpressions {
    public static String COJAC_MAGIC_toString(double n) { return "-"; }
    
    static float sum(float[] t) {
        float r=0.0f;
        for(float e:t) r += e;
        return r;
    }

    public static void main(String[] args) {
        double a = 3.1, b = a - 0.1;
        double c = b * (a / (a - 0.1));
        System.out.println(c);
        System.out.println(COJAC_MAGIC_toString(c));
        float[] t={23.0f, 0.0000008f, 0.0000009f};
        System.out.println(sum(t));
    }
}
