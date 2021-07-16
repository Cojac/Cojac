package demo;

public class HelloComplexNumber3 {
    public static void main(String[] args) {
        double val = Math.sqrt(-9); // = 3i
        val = 5 * val * val;        // = 5*9*i^2 = -45
        System.out.println(val);    // -45.0 (with COJAC!)
    }
}
