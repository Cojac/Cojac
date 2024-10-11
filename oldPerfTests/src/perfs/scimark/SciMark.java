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

package com.github.cojac.perfs.scimark;

public class SciMark {
    public void run(boolean large) {
        double min_time = Constants.RESOLUTION_DEFAULT;

        int FFT_size = Constants.FFT_SIZE;
        int SOR_size = Constants.SOR_SIZE;
        int Sparse_size_M = Constants.SPARSE_SIZE_M;
        int Sparse_size_nz = Constants.SPARSE_SIZE_nz;
        int LU_size = Constants.LU_SIZE;

        if (large) {
            FFT_size = Constants.LG_FFT_SIZE;
            SOR_size = Constants.LG_SOR_SIZE;
            Sparse_size_M = Constants.LG_SPARSE_SIZE_M;
            Sparse_size_nz = Constants.LG_SPARSE_SIZE_nz;
            LU_size = Constants.LG_LU_SIZE;
        }

        double[] res = new double[6];

        Random R = new Random(Constants.RANDOM_SEED);

        res[1] = Kernel.measureFFT(FFT_size, min_time, R);
        res[2] = Kernel.measureSOR(SOR_size, min_time, R);
        res[3] = Kernel.measureMonteCarlo(min_time, R);
        res[4] = Kernel.measureSparseMatmult(Sparse_size_M, Sparse_size_nz, min_time, R);
        res[5] = Kernel.measureLU(LU_size, min_time, R);

        res[0] = (res[1] + res[2] + res[3] + res[4] + res[5]) / 5;

        System.out.println("Composite Score: " + res[0]);
        System.out.print("FFT (" + FFT_size + "): ");

        if (res[1] == 0.0) {
            System.out.println(" ERROR, INVALID NUMERICAL RESULT!");
        } else {
            System.out.println(res[1]);
        }

        System.out.println("SOR (" + SOR_size + 'x' + SOR_size + "): " + "  " + res[2]);
        System.out.println("Monte Carlo : " + res[3]);
        System.out.println("Sparse matmult (N=" + Sparse_size_M + ", nz=" + Sparse_size_nz + "): " + res[4]);
        System.out.print("LU (" + LU_size + 'x' + LU_size + "): ");

        if (res[5] == 0.0) {
            System.out.println(" ERROR, INVALID NUMERICAL RESULT!");
        } else {
            System.out.println(res[5]);
        }
    }
}