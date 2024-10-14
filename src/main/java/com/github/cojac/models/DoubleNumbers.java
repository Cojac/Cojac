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

package com.github.cojac.models;

import static com.github.cojac.models.FloatReplacerClasses.*;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.objectweb.asm.Type;

// Warning: the public methods are accessed by reflection...

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
        Object[] a = (Object[]) array;
        if(dimensions == 1){
            return newarray(a.length);
        }
        for (int i = 0; i < a.length; i++) {
            a[i] = initializeMultiArray(a[i], dimensions-1);
        }
        return array;
    }
    
    private static Object[] cojacFromPrimitive1D(double[] array) throws Exception{
        Object[] a = (Object[]) Array.newInstance(COJAC_DOUBLE_WRAPPER_CLASS, array.length);
        for (int i = 0; i < a.length; i++)
            a[i] = COJAC_DOUBLE_WRAPPER_CLASS.getConstructor(double.class).newInstance(array[i]);
        // WRAPPER SPEC: DW(double)
        return a;
    }
    
    private static Object[] cojacFromJWrapper1D(Double[] array) throws Exception{
        Object[] a = (Object[]) Array.newInstance(COJAC_DOUBLE_WRAPPER_CLASS, array.length);
        for (int i = 0; i < a.length; i++)
            a[i] = COJAC_DOUBLE_WRAPPER_CLASS.getConstructor(double.class).newInstance(array[i]);
        // WRAPPER SPEC: DW(double)
        return a;
    }

    private static Double[] jWrapperFromCojac1D(Object[] array) throws Exception{
        Double[] a = new Double[array.length];
        for (int i = 0; i < a.length; i++){
            Method m = COJAC_DOUBLE_WRAPPER_CLASS.getMethod("toRealDoubleWrapper", COJAC_DOUBLE_WRAPPER_CLASS);
            a[i] = (Double)m.invoke(COJAC_DOUBLE_WRAPPER_CLASS, array[i]);
            // WRAPPER SPEC: DW.toRealDoubleWrapper(DW) -> Double
        }
        return a;
    }
    
    private static double[] primitiveFromCojac1D(Object[] array) throws Exception{
        double[] a = new double[array.length];
        for (int i = 0; i < a.length; i++){
			Method m = COJAC_DOUBLE_WRAPPER_CLASS.getMethod("toDouble", COJAC_DOUBLE_WRAPPER_CLASS);
			a[i] = (double)m.invoke(COJAC_DOUBLE_WRAPPER_CLASS, array[i]);
	        // WRAPPER SPEC: DW.toDouble(DW) -> double
		}
        return a;
    }
    
	// Get the Type of an array of type compClass with the number of dimensions
    static Class<?> arrayClass(Class<?> compClass, int dimensions) {
        if (dimensions == 0) {
            return compClass;
        }
        int[] dims = new int[dimensions];
        Object dummy = Array.newInstance(compClass, dims);
        return dummy.getClass();
    }

    public static Object convertArrayToPrimitive(Object array, int dimensions) throws Exception {
        Object a;
		Object[] input = (Object[])array;
        if(dimensions == 1){
            a = primitiveFromCojac1D(input);
        } else {
            Class<?> compType = arrayClass(double.class, dimensions - 1);
            // TODO: need a way to choose between primitive or JavaWrapper. -> same in FloatNumbers
            a = Array.newInstance(compType, input.length);
            Object[] b = (Object[]) a; // All arrays or multi-arrays can be cast to Object[]
            for (int i = 0; i < b.length; i++) {
                b[i] = convertArrayToPrimitive(input[i], dimensions-1); // Initialise the others dimensions
            }
        }
        return a;
    }
    
    public static Object convertArrayToJWrapper(Object array, int dimensions) throws Exception {
        Object a;
        Object[] input = (Object[])array;
        if(dimensions == 1){
            a = jWrapperFromCojac1D(input);
        } else {
            Class<?> compType = arrayClass(Double.class, dimensions - 1);
            a = Array.newInstance(compType, input.length);
            Object[] b = (Object[]) a; // All arrays or multi-arrays can be cast to Object[]
            for (int i = 0; i < b.length; i++) {
                b[i] = convertArrayToJWrapper(input[i], dimensions-1); // Initialise the others dimensions
            }
        }
        return a;
    }
	
	public static Object convertPrimitiveArrayToCojac(Object array, int dimensions) throws Exception {
        Object a;
        if(dimensions == 1){
            a = cojacFromPrimitive1D((double[])array);
        } else {
			Object[] input = (Object[])array;
            Class<?> compType = arrayClass(COJAC_DOUBLE_WRAPPER_CLASS, dimensions - 1);
            a = Array.newInstance(compType, input.length);
            Object[] b = (Object[]) a; // All arrays or multi-arrays can be cast to Object[]
            for (int i = 0; i < b.length; i++) {
                b[i] = convertPrimitiveArrayToCojac(input[i], dimensions-1); // Initialise the others dimensions
            }
        }
        return a;
    }
    
    public static Object convertJWrapperArrayToCojac(Object array, int dimensions) throws Exception {
        Object a;
        if(dimensions == 1) {
            a = cojacFromJWrapper1D((Double[])array);
        } else {
            Object[] input = (Object[])array;
            Class<?> compType = arrayClass(COJAC_DOUBLE_WRAPPER_CLASS, dimensions - 1);
            a = Array.newInstance(compType, input.length);
            Object[] b = (Object[]) a; // All arrays or multi-arrays can be cast to Object[]
            for (int i = 0; i < b.length; i++) {
                b[i] = convertJWrapperArrayToCojac(input[i], dimensions-1); // Initialise the others dimensions
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
	
	public static Object convertFromObjectToReal(Object obj) throws Exception {
		if(obj == null)
			return null;
		if(obj.getClass().isArray()){
			Class<?> type = getArrayElementType(obj);
			if(type.equals(COJAC_FLOAT_WRAPPER_CLASS)){
				int dim = getArrayDimension(obj);
				return FloatNumbers.convertArrayToPrimitive(obj, dim);
			}
			if(type.equals(COJAC_DOUBLE_WRAPPER_CLASS)){
				int dim = getArrayDimension(obj);
				return convertArrayToPrimitive(obj, dim);
			}
			if(isPrimitiveType(type))
				return obj;
			Object[] array = (Object[]) obj;
			for (int i = 0; i < array.length; i++) 
				array[i] = convertFromObjectToReal(array[i]);
			return array;
		} // else...
		if(COJAC_FLOAT_WRAPPER_CLASS.isInstance(obj)){
		    Method m = COJAC_FLOAT_WRAPPER_CLASS.getMethod("toRealFloatWrapper", COJAC_FLOAT_WRAPPER_CLASS);
		    return m.invoke(COJAC_FLOAT_WRAPPER_CLASS, obj);
		    // WRAPPER SPEC: FW.toRealDoubleWrapper(FW) -> Float
		}
		if(COJAC_DOUBLE_WRAPPER_CLASS.isInstance(obj)){
		    Method m = COJAC_DOUBLE_WRAPPER_CLASS.getMethod("toRealDoubleWrapper", COJAC_DOUBLE_WRAPPER_CLASS);
		    return m.invoke(COJAC_DOUBLE_WRAPPER_CLASS, obj);
		    // WRAPPER SPEC: DW.toRealDoubleWrapper(DW) -> Double
		}
		return obj;
	}
	
	public static Object convertFromObjectToCojac(Object obj) throws Exception{
		if(obj == null)
			return null;
		if(obj.getClass().isArray()){
			Class<?> type = getArrayElementType(obj);
			if(type.equals(float.class)){
				return FloatNumbers.convertPrimitiveArrayToCojac(obj, getArrayDimension(obj));
			}
            if(type.equals(Float.class)){
                return FloatNumbers.convertJWrapperArrayToCojac(obj, getArrayDimension(obj));
            }
			if(type.equals(double.class)) {
				return convertPrimitiveArrayToCojac(obj, getArrayDimension(obj));
			}
            if(type.equals(Double.class)){
                return convertJWrapperArrayToCojac(obj, getArrayDimension(obj));
            }
			if(isPrimitiveType(type))
				return obj;
			Object[] array = (Object[]) obj;
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
	
	private static Class<?> getArrayElementType(Object array){
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
		if(cojac == null) {
			return;
		}
		if(cojac.getClass().isArray()){
			Class<?> type = getArrayElementType(cojac);
			if(type.equals(COJAC_FLOAT_WRAPPER_CLASS)) {
				int dim = getArrayDimension(cojac);
				mergeFloatArray(original, cojac, dim);
			} else if(type.equals(COJAC_DOUBLE_WRAPPER_CLASS)) {
				int dim = getArrayDimension(cojac);
				mergeDoubleArray(original, cojac, dim);
			}
		}
	}
	
	private static void mergeFloatArray(Object original, Object cojac, int dimension) throws Exception{
        if(dimension == 1) {
            if(original instanceof float[]) {
                mergeFloatArray((float[])original, (Object[]) cojac);
            } else if(original instanceof Float[]) {
                Float[] t=(Float[])original;
                float[] t1=new float[t.length];
                for(int i=0; i<t.length; i++) t1[i]=t[i];
                mergeFloatArray(t1, (Object[]) cojac);
            }
        } else {
			Object[] originalArray = (Object[])original;
			Object[] cojacArray = (Object[])cojac;
            for (int i = 0; i < originalArray.length; i++) {
                mergeFloatArray(originalArray[i], cojacArray[i], dimension-1);
            }
        }
	}
	
	private static void mergeFloatArray(float[] original, Object[] cojac) throws Exception{
		for (int i = 0; i < cojac.length; i++) {
			Method m = COJAC_FLOAT_WRAPPER_CLASS.getMethod("toFloat", COJAC_FLOAT_WRAPPER_CLASS);
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
			Method m = COJAC_DOUBLE_WRAPPER_CLASS.getMethod("toDouble", COJAC_DOUBLE_WRAPPER_CLASS);
			double val = (double) m.invoke(COJAC_DOUBLE_WRAPPER_CLASS, cojac[i]);
            // WRAPPER SPEC: DW.toDouble(DW) -> double
			if(original[i] != val){
				cojac[i] = COJAC_DOUBLE_WRAPPER_CLASS.getConstructor(double.class).newInstance(original[i]);
		        // WRAPPER SPEC: DW(double)
			}
		}
	}
	
    public static Object myInvoke(Method m, Object target, Object[] prms) {
        try {
            if (!m.canAccess(target)) {
//                System.out.println("ACH, not accessible: "+m);
                m.setAccessible(true);
            }
            return m.invoke(target, prms);
        } catch (IllegalAccessException | 
                IllegalArgumentException | 
                InvocationTargetException |
                SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /** returns null if such a method does not exist */
    public static Method possibleMethod(Object target, String methodName, String desc) {
	    if (target==null) return null;
	    Class<?> clazz=target.getClass();
        //if(methodName.contains("apply")) System.out.println(clazz+" "+methodName+desc);
	    Method[] mt=clazz.getMethods();
	    for(Method m:mt) {
	        // m.getDeclaringClass()
	        if (!m.getName().equals(methodName)) continue;
	        String d=Type.getMethodDescriptor(m);
	        if (d.equals(desc)) { 
	            //System.out.println("-- Found! -- "+methodName+" "+desc+" "+target);
	            return m;
	        }
	    }
	    return null;
	}
    
//    private static Object[] unwrapped(Object[] args) {
//        try {
//            int n=args.length;
//            Object[] nArgs=new Object[n];
//            System.arraycopy(args, 0, nArgs, 0, n);
//            for(int i=0; i<n; i++) {
//                Object a=nArgs[i];
//                if (isCojacWrapper(a))
//                    nArgs[i]=convertFromObjectToReal(a);                
//            }
//            return nArgs;
//        } catch(Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
    
//    private static boolean isCojacWrapper(Object a) {
//        if (a==null) return false;
//        String prefix="com.github.cojac.models.wrappers";
//        String className=a.getClass().getName();
//        return className.startsWith(prefix);
//    }
    
    /* Methods that get redirected. See ReplaceFloatMethods. 
     * Here is just an example with printf() - commented because we now 
     * handle that in the proxy (createConvertMethod).

    public static PrintStream myPrintStreamPrintf(PrintStream ps, String format, Object... args) {
        return ps.printf(format, unwrapped(args));
    }

    public static void myPrintWriterPrintf(PrintWriter pw, String format, Object... args) {
        pw.printf(format, unwrapped(args));
    }
    */

}
