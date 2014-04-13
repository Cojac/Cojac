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


public class DoubleWrapper {
    private final double val;
    
    public DoubleWrapper(double v) {
        val=v;
    }
    
    public static DoubleWrapper fromDouble(double a) {
        return new DoubleWrapper(a);
    }
    
    public static DoubleWrapper fromString(String a){
        return new DoubleWrapper(Double.valueOf(a));
    }

    public static DoubleWrapper dadd(DoubleWrapper a, DoubleWrapper b) {
        return new DoubleWrapper(a.val+b.val);
    }
    
    public static DoubleWrapper dsub(DoubleWrapper a, DoubleWrapper b) {
        return new DoubleWrapper(a.val-b.val);
    }

    public static DoubleWrapper dmul(DoubleWrapper a, DoubleWrapper b) {
        return new DoubleWrapper(a.val*b.val);
    }

    public static DoubleWrapper ddiv(DoubleWrapper a, DoubleWrapper b) {
        return new DoubleWrapper(a.val/b.val);
    }

    public static DoubleWrapper drem(DoubleWrapper a, DoubleWrapper b) {
        return new DoubleWrapper(a.val%b.val);
    }
    
    public static DoubleWrapper dneg(DoubleWrapper a) {
        return new DoubleWrapper(-a.val);
    }

    public static double toDouble(DoubleWrapper a) {
        return a.val;
    }
    
    public static Double toRealDoubleWrapper(DoubleWrapper a){
        return new Double(a.val);
    }
    
    // TODO: correctly implement fcmpl and fcmpg
    public static int dcmpl(DoubleWrapper a, DoubleWrapper b) {
        if (a.val < b.val) return -1;
        if (a.val > b.val) return +1;
        return 0;
    }
    
    public static int dcmpg(DoubleWrapper a, DoubleWrapper b) {
        if (a.val < b.val) return -1;
        if (a.val > b.val) return +1;
        return 0;
    }
    
    public static int d2i(DoubleWrapper a) {
        return (int) a.val;
    }
    
    public static long d2l(DoubleWrapper a) {
        return (long) a.val;
    }
    
    public static FloatWrapper d2f(DoubleWrapper a) {
        return new FloatWrapper((float)a.val);
    }
    
    public static DoubleWrapper i2d(int a) {
        return new DoubleWrapper((double)a);
    }
    
    public static DoubleWrapper l2d(long a) {
        return new DoubleWrapper((double)a);
    }

    public static DoubleWrapper f2d(FloatWrapper a) {
        return new DoubleWrapper((double)FloatWrapper.toFloat(a));
    }

    public static DoubleWrapper[] newarray(int size){
        DoubleWrapper[] a = new DoubleWrapper[size];
        for (int i = 0; i < a.length; i++) {
            a[i] = new DoubleWrapper(0);
        }
        return a;
    }
    
    public static Object initializeMultiArray(Object array, int dimensions) {
        Object a[] = (Object[]) array;
        if(dimensions == 1){
            return newarray(a.length);
        }
        for (int i = 0; i < a.length; i++) {
            a[i] = initializeMultiArray(a[i], dimensions-1);
        }
        return array;
    }
    
    //TODO: define a "magic call" feature: getFloatInfo(float f) ---> call getFloatInfo on the FloatWrapper

}
