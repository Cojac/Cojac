package demo;

public class HelloPosit32 {

    // From [BeatingFloatingPoint] p. 15 (§4.5.3)
    static float f1() {
        return (-100 + (float) Math.sqrt(9976.0f)) / 6.0f;
        // correct: −0:020012014  (from paper)
        // float32: -0.020011902  (from paper)
        // posit32: −0:02001206   (from paper)
    }

    // From [BeatingFloatingPoint] p. 14 (§4.5.1)
    static float f2() {
        float exp = 67 / 16f;
        float num = (float) (27 / 10f - (float) Math.E);
        float den = (float) ((float) Math.PI - ((float) Math.sqrt(2) + (float) Math.sqrt(3)));
        return (float) Math.pow(num / den, exp);
        // correct: 302:8827196   (from paper)
        // float32: 302.9124      (from paper)
        // posit32: 302.88231     (from paper)
    }

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

    static void convincingDemo() {
        System.out.print(f1());
        System.out.println(" ... should be -0.02001206 (with Posit32), and not -0.020011902");
        System.out.print(f2());
        System.out.println(" ... should be 302.88231 (with Posit32), and not 302.9124");
        //          302.89627   Slight distortion (due to intermediate casting?)
    }

    // It was not clear which Posit type was expected (maybe Posit64 ?)
    static void lessConvincingDemo() {
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

    public static void main(String[] args) {
        convincingDemo();
        lessConvincingDemo();
    }
}
