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

package com.github.cojac.models.wrappers;

//import java.util.function.DoubleBinaryOperator;
//import java.util.function.DoubleUnaryOperator;

/* This class belongs to the "old generation" wrapping mechanism, which
 * might be deprecated in later releases. The Cojac front-end now relies
 * on CommonFloat/CommonDouble, along with the number models named
 * WrapperXYZ: WrapperBigDecimal, WrapperInterval, WrapperStochastic, 
 * WrapperDerivation...
 */

public class BasicDouble extends Number implements Comparable<BasicDouble>{
    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    private final double val;
    
    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public BasicDouble(double v) {
        val=v;
    }

    public BasicDouble(String v) {
        val=Double.valueOf(v);
    }
    
    public BasicDouble(BasicFloat v) {
        val=BasicFloat.toFloat(v);
    }
    
    public BasicDouble(BasicDouble v) {
        val=v.val;
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------
    
    public static BasicDouble dadd(BasicDouble a, BasicDouble b) {
        return new BasicDouble(a.val+b.val);
    }
    
    public static BasicDouble dsub(BasicDouble a, BasicDouble b) {
        return new BasicDouble(a.val-b.val);
    }

    public static BasicDouble dmul(BasicDouble a, BasicDouble b) {
        return new BasicDouble(a.val*b.val);
    }

    public static BasicDouble ddiv(BasicDouble a, BasicDouble b) {
        return new BasicDouble(a.val/b.val);
    }

    public static BasicDouble drem(BasicDouble a, BasicDouble b) {
        return new BasicDouble(a.val%b.val);
    }
    
    public static BasicDouble dneg(BasicDouble a) {
        return new BasicDouble(-a.val);
    }

    public static double toDouble(BasicDouble a) {
        return a.val;
    }
    
    public static Double toRealDoubleWrapper(BasicDouble a){
        return new Double(a.val);
    }
    
    public static int dcmpl(BasicDouble a, BasicDouble b) {
        if (Double.isNaN(a.val) || Double.isNaN(b.val)) return -1;
        if (a.val < b.val) return -1;
        if (a.val > b.val) return +1;
        return 0;
    }
    
    public static int dcmpg(BasicDouble a, BasicDouble b) {
        if (Double.isNaN(a.val) || Double.isNaN(b.val)) return +1;
        if (a.val < b.val) return -1;
        if (a.val > b.val) return +1;
        return 0;
    }
    
    public static int d2i(BasicDouble a) {
        return (int) a.val;
    }
    
    public static long d2l(BasicDouble a) {
        return (long) a.val;
    }
    

//    private BasicDouble afterUnaryOp(DoubleUnaryOperator op) {
//        return new BasicDouble(op.applyAsDouble(this.val));
//    }
//    
//    private BasicDouble afterBinaryOp(DoubleBinaryOperator op, BasicDouble arg) {
//        return new BasicDouble(op.applyAsDouble(this.val, arg.val));
//    }

    public static BasicDouble math_sqrt(BasicDouble a){
//	    return a.afterUnaryOp(Math::sqrt);
        return new BasicDouble(Math.sqrt(a.val));
    }
	
    public static BasicDouble math_abs(BasicDouble a){
        return new BasicDouble(Math.abs(a.val));
    }
    public static BasicDouble math_sin(BasicDouble a){
        return new BasicDouble(Math.sin(a.val));
    }
    public static BasicDouble math_cos(BasicDouble a){
        return new BasicDouble(Math.cos(a.val));
    }
    public static BasicDouble math_tan(BasicDouble a){
        return new BasicDouble(Math.tan(a.val));
    }
    public static BasicDouble math_asin(BasicDouble a){
        return new BasicDouble(Math.asin(a.val));
    }
    public static BasicDouble math_acos(BasicDouble a){
        return new BasicDouble(Math.acos(a.val));
    }
    public static BasicDouble math_atan(BasicDouble a){
        return new BasicDouble(Math.atan(a.val));
    }
    public static BasicDouble math_sinh(BasicDouble a){
        return new BasicDouble(Math.sinh(a.val));
    }
    public static BasicDouble math_cosh(BasicDouble a){
        return new BasicDouble(Math.cosh(a.val));
    }
    public static BasicDouble math_tanh(BasicDouble a){
        return new BasicDouble(Math.tanh(a.val));
    }
    public static BasicDouble math_exp(BasicDouble a){
        return new BasicDouble(Math.exp(a.val));
    }
    public static BasicDouble math_log(BasicDouble a){
        return new BasicDouble(Math.log(a.val));
    }
    public static BasicDouble math_log10(BasicDouble a){
        return new BasicDouble(Math.log10(a.val));
    }
    public static BasicDouble math_toRadians(BasicDouble a){
        return new BasicDouble(Math.toRadians(a.val));
    }
    public static BasicDouble math_toDegrees(BasicDouble a){
        return new BasicDouble(Math.toDegrees(a.val));
    }
	
	/*
    public static MyWrapper math_sqrt(MyWrapper a) {
    public static MyWrapper math_abs(MyWrapper a) {
    public static MyWrapper math_sin(MyWrapper a) {
    public static MyWrapper math_cos(MyWrapper a) {
    public static MyWrapper math_tan(MyWrapper a) {
    public static MyWrapper math_sinh(MyWrapper a) {
    public static MyWrapper math_cosh(MyWrapper a) {
    public static MyWrapper math_tanh(MyWrapper a) {
    public static MyWrapper math_acos(MyWrapper a) {
    public static MyWrapper math_atan(MyWrapper a) {
    public static MyWrapper math_asin(MyWrapper a) {
    public static MyWrapper math_exp(MyWrapper a) {
    public static MyWrapper math_log(MyWrapper a) {
    public static MyWrapper math_log10(MyWrapper a) {
    public static MyWrapper math_toRadians(MyWrapper a) {
    public static MyWrapper math_toDegrees(MyWrapper a) {

	public static MyWrapper math_min(MyWrapper a, MyWrapper b) {
    public static MyWrapper math_max(MyWrapper a, MyWrapper b) {
    public static MyWrapper math_pow(MyWrapper a, MyWrapper b) {
	*/
    
    public static BasicDouble math_min(BasicDouble a, BasicDouble b) {
        return fromDouble(Math.min(a.val, b.val));
    }

    public static BasicDouble math_max(BasicDouble a, BasicDouble b) {
        return fromDouble(Math.max(a.val, b.val));
    }

    public static BasicDouble math_pow(BasicDouble a, BasicDouble b) {
        return fromDouble(Math.pow(a.val, b.val));
    }
    	
    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static BasicDouble fromDouble(double a) {
        return new BasicDouble(a);
    }
    
    public static BasicDouble fromRealDoubleWrapper(Double a) {
        return new BasicDouble(a.doubleValue());
    }

    public static BasicDouble fromString(String a){
        return new BasicDouble(Double.valueOf(a));
    }
    
    public static BasicDouble i2d(int a) {
        return new BasicDouble(a);
    }
    
    public static BasicDouble l2d(long a) {
        return new BasicDouble(a);
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------
    
	@Override public int compareTo(BasicDouble o) {
        return Double.compare(this.val, o.val);
	}
	
    @Override public boolean equals(Object obj) {
        Double d=null;
        if (obj instanceof Double) d=(Double) obj;
        if (obj instanceof BasicDouble) 
            d=new Double(((BasicDouble) obj).val);
        return new Double(this.val).equals(d);
    }

	@Override public int hashCode() {
	    return Double.hashCode(this.val);
	}
	
    @Override public String toString(){
        return Double.toString(val);
    }

	@Override public int intValue() {
		return (int) val;
	}

	@Override public long longValue() {
		return (long) val;
	}

	@Override public float floatValue() {
		return (float) val;
	}

	@Override public double doubleValue() {
		return val;
	}
	
    //-------------------------------------------------------------------------
    //----------------- "Magic" methods ---------------------------------------
    //-------------------------------------------------------------------------

    public static String COJAC_MAGIC_DOUBLE_wrapper() {
        return "Basic";
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(BasicDouble n) {
        return n.toString();
    }
    
    //-------------------------------------------------------------------------
    //--------------------- Auxiliary methods ---------------------------------
	//------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------


}
