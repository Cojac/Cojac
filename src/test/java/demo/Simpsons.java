package demo;

import java.math.BigDecimal;

public class Simpsons {
    public static double pi2;

    public static void main(String[] args) {

        int k, n = 1000000;
        double a, b, h, x;
        double s1;

        a = 0;
        b = 1;
        pi2 = 0.5 * Math.acos(-1);
        h = (b - a) / (2 * n);

        x = a;
        s1 = fun(x);

        for (k = 1; k <= n; k++) {
            x = a + (2 * k - 1) * h;
            double tmp = 4 * fun(x);
            s1 = s1 + tmp;

            if (k == n)
                break;
            x = x + h;
            tmp = 2 * fun(x);
            s1 = s1 + tmp;
        }

        x = x + h;
        double tmp = fun(x);
        s1 = s1 + tmp;
        s1 = h * s1 / 3;
        System.out.println("s1->" + s1 + " sould be 0.636619772367581");

        // write (6, '(1p,d25.16)') x, h * s1 / 3.d0
        boolean fail = false;
        System.out.println(BigDecimal.valueOf(0.636619772367581).subtract(BigDecimal.valueOf(s1)).divide(BigDecimal.valueOf(0.636619772367581), 16, BigDecimal.ROUND_HALF_UP).abs());
        // try {
        if (!(BigDecimal.valueOf(0.636619772367581).subtract(BigDecimal.valueOf(s1)).divide(BigDecimal.valueOf(0.636619772367581), 16, BigDecimal.ROUND_HALF_UP).abs().compareTo(BigDecimal.valueOf(1e-6)) <= 0))
            fail = true;
        if (fail) {
            System.out.println("Failed");
            System.exit(1);
        }

    }

    public static double fun(double x) {
        return Math.sin(pi2 * x);
    }
}
