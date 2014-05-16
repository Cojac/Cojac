/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.eiafr.cojac.unit.replace;

/**
 *
 * @author romain
 */
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
	
}
