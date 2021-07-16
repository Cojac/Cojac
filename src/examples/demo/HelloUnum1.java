package demo;

public class HelloUnum1 {

    private static double func1E(double a) {
        if (a == 0) return 1;
        return (Math.exp(a) - 1) / a;
    }

    private static double func1Q(double a) {
        double sqrt = Math.sqrt(a * a + 1);
        return Math.abs(a - sqrt) - 1 / (a + sqrt);
    }

    public static double func1H(double a) {
        double q = func1Q(a);
        return func1E(q * q);
    }

    public static double scalarProduct(double[] a, double[] b) {
        assert (a.length == b.length);
        double result = 0.;
        for (int i = 0; i < a.length; i++) {
            result += a[i] * b[i];
        }
        return result;
    }

    public static void main(String[] args) {
        // example from https://youtu.be/jN9L7TpMxeA?t=1994
        double[] inputs = new double[]{15, 16, 17, 9999};
        for (double input : inputs) {
            double result = func1H(input);
            System.out.println(result + " should be 1.0");
        }

        // example from https://youtu.be/aP0Y1uAA-2Y?t=104
        double[] a = new double[]{3.2e7, 1, -1, 8.0e7};
        double[] b = new double[]{4.0e7, 1, -1, -1.6e7};
        double result = scalarProduct(a, b);
        System.out.println(result + " should be 2.0");
    }
}
