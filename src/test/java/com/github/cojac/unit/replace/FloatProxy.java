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

package com.github.cojac.unit.replace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static junit.framework.Assert.assertEquals;

import org.junit.Assert;

public class FloatProxy {
	
    public static void staticFieldDoubleAccess() throws Exception {
		FloatProxyNotInstrumented.staticDouble = 25.5;
		double r = FloatProxyNotInstrumented.staticDouble;
		assertEquals(25.5, r);
    }
	
	public static void staticFieldFloatAccess() throws Exception {
		FloatProxyNotInstrumented.staticFloat = 64.6f;
		float r = FloatProxyNotInstrumented.staticFloat;
		assertEquals(64.6f, r);
    }
	
	public static void instanceFieldDoubleAccess() throws Exception{
		FloatProxyNotInstrumented fpni = new FloatProxyNotInstrumented();
		assertEquals(0.0, fpni.doubleField);
		fpni.doubleField = 25.8;
		assertEquals(25.8, fpni.doubleField);
	}
	
	public static void instanceFieldFloatAccess() throws Exception{
		FloatProxyNotInstrumented fpni = new FloatProxyNotInstrumented();
		assertEquals(0.0f, fpni.floatField);
		fpni.floatField = 25.8f;
		assertEquals(25.8f, fpni.floatField);
	}
	
	public static void objectConstructor() throws Exception{
		double d1 = 5.4;
		long l1 = 512;
		float f1 = 432.2f;
		double d2 = 423.78;
		FloatProxyNotInstrumented fpni = new FloatProxyNotInstrumented(d1, l1, f1, d2);
		assertEquals(d1, fpni.d1);
		assertEquals(l1, fpni.l1);
		assertEquals(f1, fpni.f1);
		assertEquals(d2, fpni.d2);
	}
	
	public static void instanceMethod() throws Exception{
		FloatProxyNotInstrumented fpni = new FloatProxyNotInstrumented();
		short s1 = 5;
		long l1 = 5423;
		double d1 = 54.5;
		double d2 = 876.9;
		byte b1 = 2;
		char c1 = 'A';
		float f1 = 243.2f;
		fpni.instanceMethod(s1, l1, d1, d2, b1, true, c1, f1);
		assertEquals(d1, fpni.d1);
		assertEquals(l1, fpni.l1);
		assertEquals(f1, fpni.f1);
		assertEquals(d2, fpni.d2);
	}
	
	public static void staticMethod() throws Exception{
		short s1 = 5;
		long l1 = 5423;
		double d1 = 54.5;
		double d2 = 876.9;
		byte b1 = 2;
		char c1 = 'A';
		float f1 = 243.2f;
		FloatProxyNotInstrumented.staticMethod(l1, d1, s1, d2, b1, true, c1, f1);
		assertEquals(d1, FloatProxyNotInstrumented.static_d1);
		assertEquals(l1, FloatProxyNotInstrumented.static_l1);
		assertEquals(f1, FloatProxyNotInstrumented.static_f1);
		assertEquals(d2, FloatProxyNotInstrumented.static_d2);
	}
	
	public static void oneDimArrayPassingByMethod() throws Exception{
		float array[] = new float[] {12.413f, 6.5f, 8.12f, 654.5f};
		FloatProxyNotInstrumented fpni = new FloatProxyNotInstrumented();
		float r[] = fpni.oneDimArrayPassing(array);
		Assert.assertTrue(Arrays.equals(r, array));
		
		double darray[] = new double[] {12.413, 6.5, 8.12, 654.5};
		double dr[] = fpni.oneDimArrayPassing(darray);
		Assert.assertTrue(Arrays.equals(dr, darray));
	}
	
	public static void multiDimArrayPassingByMethod() throws Exception{
		float array[][] = new float[][] {{12.413f, 6.5f}, {54.212f, 53.123f}};
		FloatProxyNotInstrumented fpni = new FloatProxyNotInstrumented();
		float[][] r = fpni.multiDimArrayPassing(array);
		Assert.assertTrue(Arrays.deepEquals(r, array));
		
		double darray[][] = new double[][] {{12.413, 6.5}, {54.212, 53.123}};
		double[][] dr = fpni.multiDimArrayPassing(darray);
		Assert.assertTrue(Arrays.deepEquals(dr, darray));
	}
	
	public static void oneDimArrayField() throws Exception{
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	public static void multiDimArrayField() throws Exception{
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	public static void castedNumberPassingByMethod() throws Exception{
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	public static void castedNumberReturningByMethod() throws Exception{
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	public static void castedObjectPassingByMethod() throws Exception{
		Float f1 = new Float(5243.132);
		FloatProxyNotInstrumented fpni = new FloatProxyNotInstrumented();
		@SuppressWarnings("unused")
        Float f2 = (Float) fpni.castedObjectPassing(f1);
	}
	
	public static void castedObjectReturningByMethod() throws Exception{
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	public static void oneDimeArrayCastedObjectPassingByMethod() throws Exception{
		float[] array = new float[] {4321.1f, 453.2f, 6.31f};
		FloatProxyNotInstrumented fpni = new FloatProxyNotInstrumented();
		float[] a2 = (float[]) fpni.oneDimeArrayCastedObjectFloat(array);
		Assert.assertTrue(a2.length == 3);
		
		double[] darray = new double[] {4321.1, 453.2, 6.31};
		double[] da2 = (double[]) fpni.oneDimeArrayCastedObjectDouble(darray);
		Assert.assertTrue(da2.length == 3);
		
		char[] carray = new char[] {'A', 'd', 'h'};
		char[] ca2 = (char[]) fpni.oneDimeArrayCastedObjectChar(carray);
		Assert.assertTrue(ca2.length == 3);
	}
	
	public static void multiDimeArrayCastedObjectPassingByMethod() throws Exception{
		float[][] array = new float[][] {{4321.1f, 453.2f, 6.31f}, {432.2f, 12.1f}};
		FloatProxyNotInstrumented fpni = new FloatProxyNotInstrumented();
		float[][] a2 = (float[][]) fpni.multiDimeArrayCastedObject(array);
		Assert.assertTrue(a2.length == 2 && a2[0].length == 3 && a2[1].length == 2);
	}
	
	public static void arrayModifiedInMethodWithReference() throws Exception{
		float[] a = new float[] {7.8f, 2.3f, 5.89f};
		float[] a_sorted = new float[] {2.3f, 5.89f, 7.8f};
		Arrays.sort(a);
		Assert.assertTrue(Arrays.equals(a, a_sorted));
	}
	
	// This test is not executed (known bug, see the test call in FloatProxyLauncherTest)
	public static void arrayPassedInNotInstrumentedSideModifiedWithAnOtherMethod() throws Exception{
		float[] a = new float[] {7.8f, 2.3f, 5.89f};
		FloatProxyNotInstrumented fpni = new FloatProxyNotInstrumented();
		fpni.arrayPassing(a);
		fpni.resetPassedArray();
		Assert.assertTrue(Arrays.equals(a, new float[] {0,0,0}));
	}
	
	// This test is not executed (known bug, see the test call in FloatProxyLauncherTest)
	public static void arraySortedWithUserDefinedComparator() throws Exception{
		ArrayList<Float> a = new ArrayList<>();
		a.add(5.8f);
		a.add(7.8f);
		a.add(2.3f);
		Comparator<Float> fc = new Comparator<Float>(){
			 @Override
			public int compare(Float f1, Float f2) {
				return Float.compare(f1, f2);
			}
		};
		Collections.sort(a, fc);
	}
	
	
}
