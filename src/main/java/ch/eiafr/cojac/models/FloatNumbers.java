/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.eiafr.cojac.models;

import java.lang.reflect.Array;

/**
 *
 * @author romain
 */
public class FloatNumbers {
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
	
	// Get the Type of an array of type compClass with the number of dimensions
    private static Class<?> arrayClass(Class<?> compClass, int dimensions) {
        if (dimensions == 0) {
            return compClass;
        }
        int[] dims = new int[dimensions];
        Object dummy = Array.newInstance(compClass, dims);
        return dummy.getClass();
    }

    public static Object convertArrayToReal(Object array, int dimensions) {
        Object a;
		Object[] input = (Object[])array;
        if(dimensions == 1){
            a = convertArray((FloatWrapper[])input);
        }
        else{
            Class<?> compType = arrayClass(float.class, dimensions - 1);
            a = Array.newInstance(compType, input.length);
            Object[] b = (Object[]) a; // All arrays or multi-arrays can be cast to Object[]
            for (int i = 0; i < b.length; i++) {
                b[i] = convertArrayToReal(input[i], dimensions-1); // Initialise the others dimensions
            }
        }
        return a;
    }
	
	public static Object convertArrayToCojac(Object array, int dimensions) {
        Object a;
        if(dimensions == 1){
            a = convertArray((float[])array);
        }
        else{
			Object[] input = (Object[])array;
            Class<?> compType = arrayClass(FloatWrapper.class, dimensions - 1);
            a = Array.newInstance(compType, input.length);
            Object[] b = (Object[]) a; // All arrays or multi-arrays can be cast to Object[]
            for (int i = 0; i < b.length; i++) {
                b[i] = convertArrayToCojac(input[i], dimensions-1); // Initialise the others dimensions
            }
        }
        return a;
    }
	 /*
	 // Not the good way
    public static Object convertArrayToCojac(Object array, int dimensions){
        Object a[] = (Object[]) array;
        if(dimensions == 1)
            return convertArray((float[])array);
        for (int i = 0; i < a.length; i++)
            a[i] = convertArrayToReal(a[i], dimensions-1);
        return array;
    }
*/
	public static FloatWrapper initialize(FloatWrapper a){
		if(a == null)
			return new FloatWrapper(0);
		return a;
	}

	public static FloatWrapper castFromObject(Object obj){
		if(obj instanceof Float)
			return new FloatWrapper((Float)obj);
		return (FloatWrapper)obj;
	}
	
	
	
}
