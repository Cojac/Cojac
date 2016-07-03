package demo;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Date;

public class ConFrac {

    public static void main(String[] args) {
        // long start = System.currentTimeMillis();
        // for (int t = 0; t < 100000000; t++) {

        int i, j, k, n;
        n = 50;
        double eps = 1e-9f, t1, t2;
        // double x = 0.1408444346022470381636;
        double x = 0.571428571428571428571;
        double[] a = {1, x};
        double[][] b = {{1, 0}, {0, 1}};

        // ! x = 0.5714285714285714285714285714285714285714d0 ! 4/7
        // ! x = 0.3962264150943396226415094339622641509434d0 ! 21/53
        // ! x = 0.2268969453701828358895987921415551177478d0 ! 12323/54311
        // ! x = 0.1886209104677659198897180750179956993717d0 !
        // 123419/654323
        // ! x = 0.1612911738795927067229990004359619912214d0 !
        // 1234577/7654337
        // x = 0.1408444346022470381636300203302018694595d0 !
        // 12345623/87654319
        // ! x = 0.1249999282390623555811129906069898935966d0 !
        // 123456719/987654319

        for (k = 1; k <= n; k++) {
            if (k % 2 == 1) {
                t1 = (int) (a[1] / a[0]);
                a[1] = a[1] - t1 * a[0];

                for (i = 0; i <= 1; i++)
                    b[i][0] = b[i][0] + t1 * b[i][1];

            } else {
                // t1 = aint (a[0] / a[1]);
                t1 = (int) (a[0] / a[1]);
                a[0] = a[0] - t1 * a[1];

                for (i = 0; i <= 1; i++)
                    b[i][1] = b[i][1] + t1 * b[i][0];
            }
            //
            // write (6, *) 'k =', k

            // write (6, '(4f19.0)') b(1,1), b(2,1), b(1,2), b(2,2)
            // write (6, '(1p,2d25.16)') a(1), a(2)

            if (a[0] < eps || a[1] < eps) {
                if (k % 2 == 1) {
                    System.out.println(b[1][0] + ", " + b[0][0]);
                    if (!(b[1][0] == 4 && b[0][0] == 7)) {
                        System.out.println("lala");
                        System.exit(-1);
                    }
                }
                // write (6, '(f15.0," /",f15.0)') b(2,1), b(1,1)
                else {
                    System.out.println(b[1][1] + ", " + b[0][1]);
                    // write (6, '(f15.0," /",f15.0)') b(2,2), b(1,2)
                    if (!(b[1][1] == 4 && b[0][1] == 7)) {
                        System.out.println("lala");
                        System.exit(-1);
                    }
                }
                break;
            }
        }
        // }
        // long end = System.currentTimeMillis();
        //
        // System.out.println((end - start));
    }
}
