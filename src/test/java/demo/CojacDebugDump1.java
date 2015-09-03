package demo;

public class CojacDebugDump1 {

    public static void main(String[] args) {
        int a=1, b=2;
        try {
            b = a*g(a);
        } catch(Exception e) {
            System.out.println(a);
        }
        g(a);
        
        f();
    }
    
    public static void f() {
        int firstVar=5;
        double a=2*firstVar;
        System.out.println(a);
    }

    static int g(int a) {
        return 10/a;
    }
}
/*


*/