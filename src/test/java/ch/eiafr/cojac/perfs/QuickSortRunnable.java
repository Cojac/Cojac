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

package ch.eiafr.cojac.perfs;

public class QuickSortRunnable implements Runnable {
    private int[] array;

    public void setArray(int[] array) {
        this.array = array;
    }

    @Override
    public void run() {
        quicksort(0, array.length - 1, array);
    }

    private static void quicksort(int low, int high, int[] numbers) {
        int i = low;
        int j = high;

        int pivot = numbers[low + (high - low) / 2];

        while (i <= j) {
            while (numbers[i] < pivot) {
                i++;
            }

            while (numbers[j] > pivot) {
                j--;
            }

            if (i <= j) {
                swap(i, j, numbers);
                i++;
                j--;
            }
        }

        if (low < j) {
            quicksort(low, j, numbers);
        }

        if (i < high) {
            quicksort(i, high, numbers);
        }
    }

    private static void swap(int i, int j, int[] numbers) {
        int temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
    }
}