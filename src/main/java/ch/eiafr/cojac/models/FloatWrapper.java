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

public class FloatWrapper extends NumberWrapper {
    private final float val;
    
    public FloatWrapper(float v) {
        val=v;
    }
    
    public FloatWrapper(FloatWrapper v) {
        val=v.val;
    }
    
    public FloatWrapper(String v) {
        val=Float.valueOf(v);
    }
    
    public FloatWrapper(DoubleWrapper v) {
        val=(float)DoubleWrapper.toDouble(v);
    }
    
    public static FloatWrapper fromFloat(float a) {
        return new FloatWrapper(a);
    }
    
    public static FloatWrapper fromString(String a){
        return new FloatWrapper(Float.valueOf(a));
    }
    
    public static FloatWrapper fromDouble(DoubleWrapper a){
        return new FloatWrapper((float)DoubleWrapper.toDouble(a));
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
    
    public static Float toRealFloatWrapper(FloatWrapper a){
        return new Float(a.val);
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
    
    public static DoubleWrapper f2d(FloatWrapper a) {
        return new DoubleWrapper(a.val);
    }
    
    public static FloatWrapper i2f(int a) {
        return new FloatWrapper((float)a);
    }
    
    public static FloatWrapper l2f(long a) {
        return new FloatWrapper((float)a);
    }

    public static FloatWrapper d2f(DoubleWrapper a) {
        return new FloatWrapper((float)DoubleWrapper.toDouble(a));
    }

    
    public static FloatWrapper[] newarray(int size){
        FloatWrapper[] a = new FloatWrapper[size];
        for (int i = 0; i < a.length; i++)
            a[i] = new FloatWrapper(0);
        return a;
    }
    
    public static Object initializeMultiArray(Object array, int dimensions) {
        Object a[] = (Object[]) array;
        if(dimensions == 1)
            return newarray(a.length);
        for (int i = 0; i < a.length; i++)
            a[i] = initializeMultiArray(a[i], dimensions-1);
        return array;
    }
    
    public static FloatWrapper[] convertArray(float[] array){
        FloatWrapper[] a = new FloatWrapper[array.length];
        for (int i = 0; i < a.length; i++)
            a[i] = new FloatWrapper(array[i]);
        return a;
    }
    
    public static float[] convertArray(FloatWrapper[] array){
        float[] a = new float[array.length];
        for (int i = 0; i < a.length; i++)
            a[i] = FloatWrapper.toFloat(array[i]);
        return a;
    }
	
	// Not the good way
	 public static Object convertArrayToReal(Object array, int dimensions){
        Object a[] = (Object[]) array;
        if(dimensions == 1)
            return convertArray((FloatWrapper[])a);
        for (int i = 0; i < a.length; i++)
            a[i] = convertArrayToReal(a[i], dimensions-1);
        return array;
    }
    
	 // Not the good way
    public static Object convertArrayToCojac(Object array, int dimensions){
        Object a[] = (Object[]) array;
        if(dimensions == 1)
            return convertArray((float[])array);
        for (int i = 0; i < a.length; i++)
            a[i] = convertArrayToReal(a[i], dimensions-1);
        return array;
    }

	public static FloatWrapper initialize(FloatWrapper a){
		if(a == null)
			return new FloatWrapper(0);
		return a;
	}

    
    @Override
    public String toString(){
        return Float.toString(val);
    }
    
    //TODO: define a "magic call" feature: getFloatInfo(float f) ---> call getFloatInfo on the FloatWrapper

    public static boolean isInfiniteProxy(FloatWrapper a){
        return Float.isInfinite(a.val);
    }


    
}
