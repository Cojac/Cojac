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
public class DoubleNumbers {
	
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
    
    public static DoubleWrapper[] convertArray(double[] array){
        DoubleWrapper[] a = new DoubleWrapper[array.length];
        for (int i = 0; i < a.length; i++)
            a[i] = new DoubleWrapper(array[i]);
        return a;
    }
    
    public static double[] convertArray(DoubleWrapper[] array){
        double[] a = new double[array.length];
        for (int i = 0; i < a.length; i++)
            a[i] = DoubleWrapper.toDouble(array[i]);
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
            a = convertArray((DoubleWrapper[])input);
        }
        else{
            Class<?> compType = arrayClass(double.class, dimensions - 1);
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
            a = convertArray((double[])array);
        }
        else{
			Object[] input = (Object[])array;
            Class<?> compType = arrayClass(DoubleWrapper.class, dimensions - 1);
            a = Array.newInstance(compType, input.length);
            Object[] b = (Object[]) a; // All arrays or multi-arrays can be cast to Object[]
            for (int i = 0; i < b.length; i++) {
                b[i] = convertArrayToCojac(input[i], dimensions-1); // Initialise the others dimensions
            }
        }
        return a;
    }
    
	public static DoubleWrapper initialize(DoubleWrapper a){
		if(a == null)
			return new DoubleWrapper(0);
		return a;
	}
	
	public static DoubleWrapper castFromObject(Object obj){
		if(obj instanceof Double)
			return new DoubleWrapper((Double)obj);
		return (DoubleWrapper)obj;
	}
	
	public static Object convertFromObject(Object obj){
		if(obj instanceof FloatWrapper){
			return FloatWrapper.toRealFloatWrapper((FloatWrapper)obj);
		}
		if(obj instanceof DoubleWrapper){
			return DoubleWrapper.toRealDoubleWrapper((DoubleWrapper)obj);
		}
		return obj;
	}
	
	public static Object convertFromObjectArray(Object obj){
		if(obj instanceof Object[]){
			Class type = getArrayType(obj);
			if(type.equals(FloatWrapper.class)){
				int dim = getArrayDimension(obj);
				return FloatNumbers.convertArrayToReal(obj, dim);
			}
			if(type.equals(DoubleWrapper.class)){
				int dim = getArrayDimension(obj);
				return DoubleNumbers.convertArrayToReal(obj, dim);
			}
			
			Object array[] = (Object[]) obj;
			for (int i = 0; i < array.length; i++) {
				array[i] = convertFromObjectArray(array[i]);
			}
			return (Object) array;
		}
		else{
			return convertFromObject(obj);
		}
	}
	
	private static Class getArrayType(Object array){
		Class type = array.getClass();
		while (type.isArray())
			type = type.getComponentType();
		return type;
	}
	
	public static int getArrayDimension(Object array) {
		int count = 0;
		Class type = array.getClass();
		while ( type.isArray() ) {
			count++;
			type = type.getComponentType();
		}
		return count;
	}
	
}
