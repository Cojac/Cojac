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

package ch.eiafr.cojac.perfs.scimark;

public class SOR {
    public static final double num_flops(int M, int N, int num_iterations) {
        double Md = (double) M;
        double Nd = (double) N;
        double num_iterD = (double) num_iterations;

        return (Md - 1) * (Nd - 1) * num_iterD * 6.0;
    }

    public static final void execute(double omega, double G[][], int
        num_iterations) {
        int M = G.length;
        int N = G[0].length;

        double omega_over_four = omega * 0.25;
        double one_minus_omega = 1.0 - omega;

        // update interior points
        //
        int Mm1 = M - 1;
        int Nm1 = N - 1;
        for (int p = 0; p < num_iterations; p++) {
            for (int i = 1; i < Mm1; i++) {
                double[] Gi = G[i];
                double[] Gim1 = G[i - 1];
                double[] Gip1 = G[i + 1];
                for (int j = 1; j < Nm1; j++) {
                    Gi[j] = omega_over_four * (Gim1[j] + Gip1[j] + Gi[j - 1]
                        + Gi[j + 1]) + one_minus_omega * Gi[j];
                }
            }
        }
    }
}
			
