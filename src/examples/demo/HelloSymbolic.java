package demo;

public class HelloSymbolic {
    public static String COJAC_MAGIC_toString(double n) { return ""; }

    public static void main(String[] args) {
        double a = 3.1, b = a - 0.1;
        double c = b * (a / (a - 0.1));
        System.out.println(c);
        System.out.println(COJAC_MAGIC_toString(c));
    }
}
