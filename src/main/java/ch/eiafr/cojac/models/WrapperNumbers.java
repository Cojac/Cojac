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

public class WrapperNumbers {

	private static Class doubleWrapper;
	private static Class floatWrapper;
	
	public static Object[] newarray(int size) throws Exception{
		Object[] a = (Object[]) Array.newInstance(doubleWrapper, size);
        for (int i = 0; i < a.length; i++) {
            a[i] = doubleWrapper.getConstructor(double.class).newInstance(0);
        }
        return a;
    }
    
    public Object initializeMultiArray(Object array, int dimensions) throws Exception {
        Object a[] = (Object[]) array;
        if(dimensions == 1){
            return newarray(a.length);
        }
        for (int i = 0; i < a.length; i++) {
            a[i] = initializeMultiArray(a[i], dimensions-1);
        }
        return array;
    }
    
    public Object[] convertArray(double[] array) throws Exception{
		Object[] a = (Object[]) Array.newInstance(doubleWrapper, array.length);
        for (int i = 0; i < a.length; i++)
            a[i] = doubleWrapper.getConstructor(doubleWrapper).newInstance(array[i]);
        return a;
    }
    
    public double[] convertArray(Object[] array) throws Exception{
        double[] a = new double[array.length];
        for (int i = 0; i < a.length; i++)
			a[i] = (double) doubleWrapper.getMethod("toDouble", doubleWrapper).invoke(array[i]);
        return a;
    }
    
	// Get the Type of an array of type compClass with the number of dimensions
    private Class<?> arrayClass(Class<?> compClass, int dimensions) {
        if (dimensions == 0) {
            return compClass;
        }
        int[] dims = new int[dimensions];
        Object dummy = Array.newInstance(compClass, dims);
        return dummy.getClass();
    }

    public Object convertArrayToReal(Object array, int dimensions) throws Exception {
        Object a;
		Object[] input = (Object[])array;
        if(dimensions == 1){
            a = convertArray(input);
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
	
	public Object convertArrayToCojac(Object array, int dimensions) throws Exception {
        Object a;
        if(dimensions == 1){
            a = convertArray((double[])array);
        }
        else{
			Object[] input = (Object[])array;
            Class<?> compType = arrayClass(doubleWrapper, dimensions - 1);
            a = Array.newInstance(compType, input.length);
            Object[] b = (Object[]) a; // All arrays or multi-arrays can be cast to Object[]
            for (int i = 0; i < b.length; i++) {
                b[i] = convertArrayToCojac(input[i], dimensions-1); // Initialise the others dimensions
            }
        }
        return a;
    }
    
	public Object initialize(Object a) throws Exception{
		if(a == null)
			return doubleWrapper.getConstructor(double.class).newInstance(0);
		return a;
	}
	
	public Object castFromObject(Object obj) throws Exception{
		if(obj instanceof Double)
			return doubleWrapper.getConstructor(Double.class).newInstance((Double)obj);
		return obj;
	}
	
	public Object convertFromObjectToReal(Object obj){
		if(obj == null)
			return obj;
		if(obj.getClass().isArray()){
			Class type = getArrayType(obj);
			if(type.equals(FloatWrapper.class)){
				int dim = getArrayDimension(obj);
				return FloatNumbers.convertArrayToReal(obj, dim);
			}
			if(type.equals(DoubleWrapper.class)){
				int dim = getArrayDimension(obj);
				return WrapperNumbers.convertArrayToReal(obj, dim);
			}
			if(isPrimitiveType(type))
				return obj;
			Object array[] = (Object[]) obj;
			for (int i = 0; i < array.length; i++) 
				array[i] = convertFromObjectToReal(array[i]);
			return (Object) array;
		}
		else{
			if(obj instanceof FloatWrapper)
				return FloatWrapper.toRealFloatWrapper((FloatWrapper)obj);
			if(obj instanceof DoubleWrapper)
				return DoubleWrapper.toRealDoubleWrapper((DoubleWrapper)obj);
			return obj;
		}
	}
	
	public Object convertFromObjectToCojac(Object obj){
		if(obj == null)
			return obj;
		if(obj.getClass().isArray()){
			Class type = getArrayType(obj);
			if(type.equals(float.class) || type.equals(Float.class)){
				int dim = getArrayDimension(obj);
				return FloatNumbers.convertArrayToCojac(obj, dim);
			}
			if(type.equals(double.class) || type.equals(Double.class)){
				int dim = getArrayDimension(obj);
				return WrapperNumbers.convertArrayToCojac(obj, dim);
			}
			if(isPrimitiveType(type))
				return obj;
			Object array[] = (Object[]) obj;
			for (int i = 0; i < array.length; i++) 
				array[i] = convertFromObjectToCojac(array[i]);
			return (Object) array;
		}
		else{
			if(obj instanceof Float)
				return new FloatWrapper((Float)obj);
			if(obj instanceof Double)
				return new DoubleWrapper((Double)obj);
			return obj;
		}
	}
	
	private boolean isPrimitiveType(Class type){
		return type.equals(boolean.class) || type.equals(byte.class) ||
				type.equals(char.class) || type.equals(double.class) ||
				type.equals(float.class) || type.equals(int.class) ||
				type.equals(long.class) || type.equals(short.class);
	}
	
	private Class getArrayType(Object array){
		Class type = array.getClass();
		while (type.isArray())
			type = type.getComponentType();
		return type;
	}
	
	public int getArrayDimension(Object array) {
		int count = 0;
		Class type = array.getClass();
		while ( type.isArray() ) {
			count++;
			type = type.getComponentType();
		}
		return count;
	}
}
