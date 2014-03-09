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

package ch.eiafr.cojac.models;

import java.lang.reflect.Array;

public class FloatWrapper {
    private final float val;
    
    public FloatWrapper(float v) {
        val=v;
    }
    
    public static FloatWrapper fromFloat(float a) {
        return new FloatWrapper(a);
    }

    public static FloatWrapper fadd(FloatWrapper a, FloatWrapper b) {
        return new FloatWrapper(a.val+b.val);
    }
    
    public static FloatWrapper fsub(FloatWrapper a, FloatWrapper b) {
        return new FloatWrapper(a.val-b.val);
    }

    public static FloatWrapper fmul(FloatWrapper a, FloatWrapper b) {
        return new FloatWrapper(a.val*b.val);
    }

    public static FloatWrapper fdiv(FloatWrapper a, FloatWrapper b) {
        return new FloatWrapper(a.val/b.val);
    }

    public static FloatWrapper frem(FloatWrapper a, FloatWrapper b) {
        return new FloatWrapper(a.val%b.val);
    }
    
    public static FloatWrapper fneg(FloatWrapper a) {
        return new FloatWrapper(-a.val);
    }

    public static float toFloat(FloatWrapper a) {
        return a.val;
    }
    
    // TODO: correctly implement fcmpl and fcmpg
    public static int fcmpl(FloatWrapper a, FloatWrapper b) {
        if (a.val < b.val) return -1;
        if (a.val > b.val) return +1;
        return 0;
    }
    
    public static int fcmpg(FloatWrapper a, FloatWrapper b) {
        if (a.val < b.val) return -1;
        if (a.val > b.val) return +1;
        return 0;
    }
    
    public static int f2i(FloatWrapper a) {
        return (int) a.val;
    }
    
    public static long f2l(FloatWrapper a) {
        return (long) a.val;
    }
    
    public static double f2d(FloatWrapper a) {
        return (double) a.val;
    }
    
    public static FloatWrapper i2f(int a) {
        return new FloatWrapper((float)a);
    }
    
    public static FloatWrapper l2f(long a) {
        return new FloatWrapper((float)a);
    }

    public static FloatWrapper d2f(double a) {
        return new FloatWrapper((float)a);
    }

    
    public static FloatWrapper[] newarray(int size){
        FloatWrapper[] a = new FloatWrapper[size];
        for (int i = 0; i < a.length; i++) {
            a[i] = new FloatWrapper(0);
        }
        return a;
    }
    
    
    // Get the Type of an array of type compClass with the number of dimensions
    private static Class<?> arrayClass(Class<?> compClass, int dimensions) {
        if (dimensions == 0) {
            return compClass;
        }
        int[] dims = new int[dimensions];
        Object dummy = Array.newInstance(compClass, dims);
        return dummy.getClass();
    }

    public static Object multianewarray(int ... sizes) {
        int dimensions = sizes.length;
        Object a;
        if(dimensions == 1){
            a = newarray(sizes[sizes.length-1]); // Create a simple array for the last dimension
        }
        else{
            Class<?> compType = arrayClass(FloatWrapper.class, dimensions - 1);
            a = Array.newInstance(compType, sizes[sizes.length-1]);

            Object[] b = (Object[]) a; // All arrays or multi-arrays can be cast to Object[]
            for (int i = 0; i < b.length; i++) {
                int newsize[] = new int[sizes.length-1];
                for (int j = 0; j < newsize.length; j++) {
                    newsize[j] = sizes[j];
                }
                b[i] = multianewarray(newsize); // Initialise the others dimensions
            }
        }
        return a;
    }
    
    //TODO: define a "magic call" feature: getFloatInfo(float f) ---> call getFloatInfo on the FloatWrapper

}
