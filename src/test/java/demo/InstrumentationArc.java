package demo;

import java.math.BigDecimal;

public class InstrumentationArc {
    public static double fun(double x) {
        double t1 = x;
        double d1 = 1.0;
        for (int k = 1; k <= 5; k++) {
            d1 = 2.0 * d1;
            t1 = t1 + Math.sin(d1 * x) / d1;//add
        }
        return t1;
    }

    public static void main(String[] args) {
        int n = 1000000;
        double s1 = 0.0;
        double t1 = 0.0;
        double t2;
        double dppi = Math.acos(-1.0);
        double h = dppi / n;

        for (int i = 1; i <= n; i++) {
            t2 = fun(i * h);
            s1 = s1 + Math.sqrt(h * h + (t2 - t1) * (t2 - t1)); //add
            t1 = t2;
        }
        System.out.println("s1 -> " + s1 + " should be 5.79577632241304 ");

        boolean fail = false;
        System.out.println(BigDecimal.valueOf(5.79577632241304).subtract(BigDecimal.valueOf(s1)).divide(BigDecimal.valueOf(5.79577632241304),10, BigDecimal.ROUND_HALF_UP).abs());
        // try {
        if (!(BigDecimal.valueOf(5.79577632241304).subtract(BigDecimal.valueOf(s1)).divide(BigDecimal.valueOf(5.79577632241304), 10, BigDecimal.ROUND_HALF_UP).abs().compareTo(BigDecimal.valueOf(1e-6)) <= 0))
            fail = true;
        if (fail) {
            System.out.println("lala");
            System.exit(1);
        }
        // }
    }
}
