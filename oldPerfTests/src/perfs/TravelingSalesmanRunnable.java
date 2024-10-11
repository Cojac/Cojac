/*
 * *
 *    Copyright 2011 Baptiste Wicht & Frédéric Bapst
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.cojac.perfs;

import java.util.Random;

/**
 * Created by IntelliJ IDEA. User: wichtounet Date: Nov 24, 2010 Time: 9:58:36 AM To change this template use File |
 * Settings | File Templates.
 */
public class TravelingSalesmanRunnable implements Runnable {
    @Override
    public void run() {
        salesman(generatePoints(1500), new int[1500]);
    }

    static private TSPPoint[] generatePoints(int n) {
        TSPPoint[] p = new TSPPoint[n];
        Random r = new Random(15);
        for (int i = 0; i < n; i++) {
            p[i] = new TSPPoint();
            p[i].x = 500 * r.nextFloat();
            p[i].y = 500 * r.nextFloat();
        }
        return p;
    }

    public static void salesman(TSPPoint[] t, int[] path) {

        int n = t.length;

        double[] dx = new double[n];
        double[] dy = new double[n];
        int[] di = new int[n];

        int shortestPath = 0;
        for (int i = 0; i < n; i++) {
            dx[i] = t[i].x;
            dy[i] = t[i].y;
            di[i] = i;
        }

        int i = n - 1;
        int l = n - 1;
        path[0] = n - 1;
        while (i > 0) {
            double d1 = 3.4028234663852886E+038D;
            double d5 = dx[l];
            double d6 = dy[l];
            for (int j = 0; j < i; j++) {
                double d2 = d5 - dx[j];
                double d4 = d2 * d2;
                if (d4 < d1) {
                    double d3 = d6 - dy[j];
                    d4 += d3 * d3;
                    if (d4 < d1) {
                        d1 = d4;
                        shortestPath = j;
                    }
                }
            }

            path[n - i] = di[shortestPath];
            i--;

            int tmpD = di[i];
            double tmpX = dx[i];
            double tmpY = dy[i];

            di[i] = di[shortestPath];
            dx[i] = dx[shortestPath];
            dy[i] = dy[shortestPath];

            di[shortestPath] = tmpD;
            dx[shortestPath] = tmpX;
            dy[shortestPath] = tmpY;

            l = i;
        }
    }

    private static class TSPPoint {
        double x;
        double y;
    }
}
