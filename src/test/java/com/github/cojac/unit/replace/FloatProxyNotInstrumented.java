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

public class FloatProxyNotInstrumented {
	
	public static double staticDouble = 53.75;
	public static float staticFloat = 53.75f;
	
	public double doubleField;
	public float floatField;
	
	public FloatProxyNotInstrumented(){
	}
	
	double d1;
	long l1;
	float f1;
	double d2;
	
	public FloatProxyNotInstrumented(double d1, long l1, float f1, double d2){
		this.d1 = d1;
		this.l1 = l1;
		this.f1 = f1;
		this.d2 = d2;
	}
	
	@SuppressWarnings("unused")
    public void instanceMethod(short s1, long l1, double d1, double d2, byte b1, boolean bo1, char c1,  float f1){
		this.d1 = d1;
		this.l1 = l1;
		this.f1 = f1;
		this.d2 = d2;
	}
	
	static double static_d1;
	static long static_l1;
	static float static_f1;
	static double static_d2;
	
    @SuppressWarnings("unused")
	public static void staticMethod(long l1, double d1, short s1, double d2, byte b1, boolean bo1, char c1,  float f1){
		static_d1 = d1;
		static_l1 = l1;
		static_f1 = f1;
		static_d2 = d2;
	}
	
	
	public float[] oneDimArrayPassing(float[] array){
		return array;
	}
	
	public double[] oneDimArrayPassing(double[] array){
		return array;
	}
	
	public float[][] multiDimArrayPassing(float[][] array) {
		return array;
	}
	
	public double[][] multiDimArrayPassing(double[][] array) {
		return array;
	}
	
	public Object castedObjectPassing(Object val){
		Float f = (Float)val;
		f += 432.1f;
		return f;
	}
	
	public Float jWrapperPassing(Float val){
	    Float f = val;
	    f += 432.1f;
	    return f;
	}

	public Object oneDimeArrayCastedObjectFloat(Object val){
		float[] f = (float[])val;
		f[2] *= 0.789f;
		return f;
	}
	
	public Object oneDimeArrayCastedObjectDouble(Object val){
		double[] f = (double[])val;
		f[2] *= 0.789;
		return f;
	}
	
	public Object oneDimeArrayCastedObjectChar(Object val){
		char[] f = (char[])val;
		f[2] += 'a';
		return f;
	}
	
	public Object multiDimeArrayCastedObject(Object val){
		float[][] f = (float[][])val;
		f[0][0] %= 0.231;
		f[1][0] /= 2.5;
		return f;
	}
	
	private float[] farray;
	public void arrayPassing(float[] array){
		this.farray = array;
	}
	
	public void resetPassedArray(){
		for (int i = 0; i < farray.length; i++) {
			farray[i] = 0;
		}
	}
	
}
