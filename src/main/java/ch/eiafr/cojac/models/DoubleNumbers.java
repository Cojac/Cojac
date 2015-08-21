/*
 * *
 *    Copyright 2014 Frédéric Bapst & Romain Monnard
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

import static ch.eiafr.cojac.models.FloatReplacerClasses.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;


public class DoubleNumbers {
	
	public static Object[] newarray(int size) throws Exception{
		Object[] a = (Object[]) Array.newInstance(COJAC_DOUBLE_WRAPPER_CLASS, size);
        for (int i = 0; i < a.length; i++) {
            a[i] = COJAC_DOUBLE_WRAPPER_CLASS.getConstructor(double.class).newInstance(0);
        }
        // WRAPPER SPEC: DW(double)
        return a;
    }

	public static Object initializeMultiArray(Object array, int dimensions) throws Exception {
        Object a[] = (Object[]) array;
        if(dimensions == 1){
            return newarray(a.length);
        }
        for (int i = 0; i < a.length; i++) {
            a[i] = initializeMultiArray(a[i], dimensions-1);
        }
        return array;
    }
    
    private static Object[] convertArray(double[] array) throws Exception{
        Object[] a = (Object[]) Array.newInstance(COJAC_DOUBLE_WRAPPER_CLASS, array.length);
        for (int i = 0; i < a.length; i++)
            a[i] = COJAC_DOUBLE_WRAPPER_CLASS.getConstructor(double.class).newInstance(array[i]);
        // WRAPPER SPEC: DW(double)
        return a;
    }
    
    private static double[] convertArray(Object[] array) throws Exception{
        double[] a = new double[array.length];
        for (int i = 0; i < a.length; i++){
			Method m = COJAC_DOUBLE_WRAPPER_CLASS.getMethod("toDouble", new Class[] {COJAC_DOUBLE_WRAPPER_CLASS});
			a[i] = (double)m.invoke(COJAC_DOUBLE_WRAPPER_CLASS, array[i]);
	        // WRAPPER SPEC: DW.toDouble(DW) -> double
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

    public static Object convertArrayToReal(Object array, int dimensions) throws Exception {
        Object a;
		Object[] input = (Object[])array;
        if(dimensions == 1){
            a = convertArray(input);
        } else {
            Class<?> compType = arrayClass(double.class, dimensions - 1);
            a = Array.newInstance(compType, input.length);
            Object[] b = (Object[]) a; // All arrays or multi-arrays can be cast to Object[]
            for (int i = 0; i < b.length; i++) {
                b[i] = convertArrayToReal(input[i], dimensions-1); // Initialise the others dimensions
            }
        }
        return a;
    }
	
	public static Object convertArrayToCojac(Object array, int dimensions) throws Exception {
        Object a;
        if(dimensions == 1){
            a = convertArray((double[])array);
        } else {
			Object[] input = (Object[])array;
            Class<?> compType = arrayClass(COJAC_DOUBLE_WRAPPER_CLASS, dimensions - 1);
            a = Array.newInstance(compType, input.length);
            Object[] b = (Object[]) a; // All arrays or multi-arrays can be cast to Object[]
            for (int i = 0; i < b.length; i++) {
                b[i] = convertArrayToCojac(input[i], dimensions-1); // Initialise the others dimensions
            }
        }
        return a;
    }
    
	public static Object initialize(Object a) throws Exception{
		if(a == null)
			return COJAC_DOUBLE_WRAPPER_CLASS.getConstructor(double.class).newInstance(0);
        // WRAPPER SPEC: DW(double)
		return a;
	}
	
	public static Object castFromObject(Object obj) throws Exception{
		if(obj instanceof Double)
			return COJAC_DOUBLE_WRAPPER_CLASS.getConstructor(double.class).newInstance((Double)obj);
        // WRAPPER SPEC: DW(double)
		return obj;
	}
	
	public static Object convertFromObjectToReal(Object obj) throws Exception{
		if(obj == null)
			return obj;
		if(obj.getClass().isArray()){
			Class<?> type = getArrayType(obj);
			if(type.equals(COJAC_FLOAT_WRAPPER_CLASS)){
				int dim = getArrayDimension(obj);
				return FloatNumbers.convertArrayToReal(obj, dim);
			}
			if(type.equals(COJAC_DOUBLE_WRAPPER_CLASS)){
				int dim = getArrayDimension(obj);
				return DoubleNumbers.convertArrayToReal(obj, dim);
			}
			if(isPrimitiveType(type))
				return obj;
			Object array[] = (Object[]) obj;
			for (int i = 0; i < array.length; i++) 
				array[i] = convertFromObjectToReal(array[i]);
			return array;
		} // else...
		if(COJAC_FLOAT_WRAPPER_CLASS.isInstance(obj)){
		    Method m = COJAC_FLOAT_WRAPPER_CLASS.getMethod("toRealFloatWrapper", new Class[] {COJAC_FLOAT_WRAPPER_CLASS});
		    return m.invoke(COJAC_FLOAT_WRAPPER_CLASS, obj);
		    // WRAPPER SPEC: FW.toRealDoubleWrapper(FW) -> Float
		}
		if(COJAC_DOUBLE_WRAPPER_CLASS.isInstance(obj)){
		    Method m = COJAC_DOUBLE_WRAPPER_CLASS.getMethod("toRealDoubleWrapper", new Class[] {COJAC_DOUBLE_WRAPPER_CLASS});
		    return m.invoke(COJAC_DOUBLE_WRAPPER_CLASS, obj);
		    // WRAPPER SPEC: DW.toRealDoubleWrapper(DW) -> Double
		}
		return obj;
	}
	
	public static Object convertFromObjectToCojac(Object obj) throws Exception{
		if(obj == null)
			return obj;
		if(obj.getClass().isArray()){
			Class<?> type = getArrayType(obj);
			if(type.equals(float.class) || type.equals(Float.class)){
				int dim = getArrayDimension(obj);
				return FloatNumbers.convertArrayToCojac(obj, dim);
			}
			if(type.equals(double.class) || type.equals(Double.class)){
				int dim = getArrayDimension(obj);
				return DoubleNumbers.convertArrayToCojac(obj, dim);
			}
			if(isPrimitiveType(type))
				return obj;
			Object array[] = (Object[]) obj;
			for (int i = 0; i < array.length; i++) 
				array[i] = convertFromObjectToCojac(array[i]);
			return array;
		}  // else...
		if(obj instanceof Float)
		    return COJAC_FLOAT_WRAPPER_CLASS.getConstructor(float.class).newInstance((Float)obj);
		// WRAPPER SPEC: FW(float)
		if(obj instanceof Double)
		    return COJAC_DOUBLE_WRAPPER_CLASS.getConstructor(double.class).newInstance((Double)obj);
		// WRAPPER SPEC: DW(double)
		return obj;
	}
	
	private static boolean isPrimitiveType(Class<?> type){
		return type.equals(boolean.class) || type.equals(byte.class) ||
				type.equals(char.class) || type.equals(double.class) ||
				type.equals(float.class) || type.equals(int.class) ||
				type.equals(long.class) || type.equals(short.class);
	}
	
	private static Class<?> getArrayType(Object array){
		Class<?> type = array.getClass();
		while (type.isArray())
			type = type.getComponentType();
		return type;
	}
	
	public static int getArrayDimension(Object array) {
		int count = 0;
		Class<?> type = array.getClass();
		while ( type.isArray() ) {
			count++;
			type = type.getComponentType();
		}
		return count;
	}
	
	public static void mergeOriginalArrayIntoCojac(Object cojac, Object original) throws Exception{
		if(cojac == null){
			return;
		}
		if(cojac.getClass().isArray()){
			Class<?> type = getArrayType(cojac);
			if(type.equals(COJAC_FLOAT_WRAPPER_CLASS)){
				int dim = getArrayDimension(cojac);
				mergeFloatArray(original, cojac, dim);
			}
			if(type.equals(COJAC_DOUBLE_WRAPPER_CLASS)){
				int dim = getArrayDimension(cojac);
				mergeDoubleArray(original, cojac, dim);
			}
			
		}
	}
	
	private static void mergeFloatArray(Object original, Object cojac, int dimension) throws Exception{
        if(dimension == 1){
            if(original instanceof float[])
                mergeFloatArray((float[])original, (Object[]) cojac);
            else if(original instanceof Float[]) {
                Float[] t=(Float[])original;
                float[] t1=new float[t.length];
                for(int i=0; i<t.length; i++) t1[i]=t[i];
                mergeFloatArray(t1, (Object[]) cojac);
            }
        }
        else{
			Object[] originalArray = (Object[])original;
			Object[] cojacArray = (Object[])cojac;
            for (int i = 0; i < originalArray.length; i++) {
                mergeFloatArray(originalArray[i], cojacArray[i], dimension-1);
            }
        }
	}
	
	private static void mergeFloatArray(float[] original, Object[] cojac) throws Exception{
		for (int i = 0; i < cojac.length; i++) {
			Method m = COJAC_FLOAT_WRAPPER_CLASS.getMethod("toFloat", new Class[] {COJAC_FLOAT_WRAPPER_CLASS});
            // WRAPPER SPEC: FW.toFloat(FW) -> float
			float val = (float) m.invoke(COJAC_FLOAT_WRAPPER_CLASS, cojac[i]);
			if(original[i] != val){
				cojac[i] = COJAC_FLOAT_WRAPPER_CLASS.getConstructor(float.class).newInstance(original[i]);
		        // WRAPPER SPEC: FW(float)
			}
		}
	}
	
	private static void mergeDoubleArray(Object original, Object cojac, int dimension) throws Exception{
        if(dimension == 1){
            if(original instanceof double[])
                mergeDoubleArray((double[])original, (Object[]) cojac);
            else if(original instanceof Double[]) {
                Double[] t=(Double[])original;
                double[] t1=new double[t.length];
                for(int i=0; i<t.length; i++) t1[i]=t[i];
                mergeDoubleArray(t1, (Object[]) cojac);
            }
        }
        else{
			Object[] originalArray = (Object[])original;
			Object[] cojacArray = (Object[])cojac;
            for (int i = 0; i < originalArray.length; i++) {
                mergeDoubleArray(originalArray[i], cojacArray[i], dimension-1);
            }
        }
	}
	
	private static void mergeDoubleArray(double[] original, Object[] cojac) throws Exception{
		for (int i = 0; i < cojac.length; i++) {
			Method m = COJAC_DOUBLE_WRAPPER_CLASS.getMethod("toDouble", new Class[] {COJAC_DOUBLE_WRAPPER_CLASS});
			double val = (double) m.invoke(COJAC_DOUBLE_WRAPPER_CLASS, cojac[i]);
            // WRAPPER SPEC: DW.toDouble(DW) -> double
			if(original[i] != val){
				cojac[i] = COJAC_DOUBLE_WRAPPER_CLASS.getConstructor(double.class).newInstance(original[i]);
		        // WRAPPER SPEC: DW(double)
			}
		}
	}
	
}
