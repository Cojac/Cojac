/*
 * *
 *    Copyright 2014 Frédéric Bapst
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

import static com.github.cojac.models.FloatReplacerClasses.COJAC_WRAPPER_NG_CLASS;

import java.lang.reflect.Constructor;

/* CommonDouble/CommonFloat is the "new generation" wrapping mechanism, where
 * we isolate the behavior of numbers in a ACojacWrapper abstract class.
 * We consider that cleaner than our first attempt.
 */

public class CommonDouble extends Number implements Comparable<CommonDouble>{
    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    protected final ACojacWrapper val;
    
    protected CommonDouble(ACojacWrapper w) {
        this.val=w;
    }
    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public CommonDouble(double v) {
        val = newInstance(null).fromDouble(v, false);
    }
    
    public CommonDouble(String v) {
        this(Double.valueOf(v));
    }
    
    public CommonDouble(CommonFloat v) {
        val=newInstance(v.val);
    }
    
    public CommonDouble(CommonDouble v) {
        val=newInstance(v.val);
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------
    
    public static CommonDouble dadd(CommonDouble a, CommonDouble b) {
        return new CommonDouble(a.val.dadd(b.val));
    }
    
    public static CommonDouble dsub(CommonDouble a, CommonDouble b) {
        return new CommonDouble(a.val.dsub(b.val));
    }

    public static CommonDouble dmul(CommonDouble a, CommonDouble b) {
        return new CommonDouble(a.val.dmul(b.val));
    }

    public static CommonDouble ddiv(CommonDouble a, CommonDouble b) {
        return new CommonDouble(a.val.ddiv(b.val));
    }

    public static CommonDouble drem(CommonDouble a, CommonDouble b) {
        return new CommonDouble(a.val.drem(b.val));
    }
    
    public static CommonDouble dneg(CommonDouble a) {
        return new CommonDouble(a.val.dneg());
    }

    public static double toDouble(CommonDouble a) {
        return a.val.toDouble();
    }
    
    public static Double toRealDoubleWrapper(CommonDouble a){
        return a.val.toDouble();
    }
    
    public static int dcmpl(CommonDouble a, CommonDouble b) {
        return a.val.dcmpl(b.val);
    }
    
    public static int dcmpg(CommonDouble a, CommonDouble b) {
        return a.val.dcmpg(b.val);
    }
    
    public static int d2i(CommonDouble a) {
        return (int)a.val.toDouble();
    }
    
    public static long d2l(CommonDouble a) {
        return (long)a.val.toDouble();
    }

    public static CommonDouble math_sqrt(CommonDouble a){
        return new CommonDouble(a.val.math_sqrt());
    }
	
    public static CommonDouble math_abs(CommonDouble a){
        return new CommonDouble(a.val.math_abs());
    }
    public static CommonDouble math_sin(CommonDouble a){
        return new CommonDouble(a.val.math_sin());
    }
    public static CommonDouble math_cos(CommonDouble a){
        return new CommonDouble(a.val.math_cos());
    }
    public static CommonDouble math_tan(CommonDouble a){
        return new CommonDouble(a.val.math_tan());
    }
    public static CommonDouble math_asin(CommonDouble a){
        return new CommonDouble(a.val.math_asin());
    }
    public static CommonDouble math_acos(CommonDouble a){
        return new CommonDouble(a.val.math_acos());
    }
    public static CommonDouble math_atan(CommonDouble a){
        return new CommonDouble(a.val.math_atan());
    }
    public static CommonDouble math_sinh(CommonDouble a){
        return new CommonDouble(a.val.math_sinh());
    }
    public static CommonDouble math_cosh(CommonDouble a){
        return new CommonDouble(a.val.math_cosh());
    }
    public static CommonDouble math_tanh(CommonDouble a){
        return new CommonDouble(a.val.math_tanh());
    }
    public static CommonDouble math_exp(CommonDouble a){
        return new CommonDouble(a.val.math_exp());
    }
    public static CommonDouble math_log(CommonDouble a){
        return new CommonDouble(a.val.math_log());
    }
    public static CommonDouble math_log10(CommonDouble a){
        return new CommonDouble(a.val.math_log10());
    }
    public static CommonDouble math_toRadians(CommonDouble a){
        return new CommonDouble(a.val.math_toRadians());
    }
    public static CommonDouble math_toDegrees(CommonDouble a){
        return new CommonDouble(a.val.math_toDegrees());
    }

    public static CommonDouble math_min(CommonDouble a, CommonDouble b) {
        return new CommonDouble(a.val.math_min(b.val));
    }

    public static CommonDouble math_max(CommonDouble a, CommonDouble b) {
        return new CommonDouble(a.val.math_max(b.val));
    }

    public static CommonDouble math_pow(CommonDouble a, CommonDouble b) {
        return new CommonDouble(a.val.math_pow(b.val));
    }

    public static CommonDouble math_hypot(CommonDouble a, CommonDouble b) {
        return new CommonDouble(a.val.math_hypot(b.val));
    }
    	
    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static CommonDouble fromDouble(double a) {
        return new CommonDouble(a);
    }
    
    public static CommonDouble fromRealDoubleWrapper(Double a) {
        return fromDouble(a);
    }

    public static CommonDouble fromString(String a){
        return fromDouble(Double.valueOf(a));
    }
    
    public static CommonDouble i2d(int a) {
        return fromDouble(a);
    }
    
    public static CommonDouble l2d(long a) {
        return fromDouble(a);
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------
    
	@Override public int compareTo(CommonDouble o) {
        return this.val.compareTo(o.val);
	}
	
    @Override public boolean equals(Object obj) {
        if (obj == null) return false;
        if (! (obj instanceof CommonDouble)) return false;
        CommonDouble ow = (CommonDouble)obj;
        return this.val.equals(ow.val);
    }

	@Override public int hashCode() {
	    return this.val.hashCode();
	}
	
    @Override public String toString(){
        return this.val.toString();
    }

	@Override public int intValue() {
		return (int)this.val.toDouble();
	}

	@Override public long longValue() {
        return (long)this.val.toDouble();
	}

    @Override public byte byteValue() {
        return (byte)this.val.toDouble();
    }
    
    @Override public short shortValue() {
        return (short)this.val.toDouble();
    }
    
	@Override public float floatValue() {
        return (float)this.val.toDouble();
	}

	@Override public double doubleValue() {
        return this.val.toDouble();
	}
	
    //-------------------------------------------------------------------------
    //----------------- "Magic" methods ---------------------------------------
    //-------------------------------------------------------------------------

	// TODO: maybe reconsider the magic method mechanism if we really switch 
	//       to the new generation wrapping (the indirection is annoying...). 
	//       see cojac.instrumenters.ReplaceFloatsMethods.instrument()
	
    public static String COJAC_MAGIC_wrapperName() {
        return newInstance(null).wrapperName();
    }

    public static String COJAC_MAGIC_toString(CommonDouble n) {
        return n.val.asInternalString();
    }

    public static String COJAC_MAGIC_toString(CommonFloat n) {
        return n.val.asInternalString();
    }

    // TODO: remove once we switch to the NG wrapper
    public static String COJAC_MAGIC_DOUBLE_wrapper() {
        return newInstance(null).wrapperName();
    }

    // TODO: remove once we switch to the NG wrapper
    public static String COJAC_MAGIC_DOUBLE_toStr(CommonDouble n) {
        return n.val.asInternalString();
    }
    
    // TODO: remove once we switch to the NG wrapper
    public static String COJAC_MAGIC_DOUBLE_toStr(CommonFloat n) {
        return n.val.asInternalString();
    }
    
    //-------------------------------------------------------------------------
    //--------------------- Auxiliary methods ---------------------------------
	//------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    protected static ACojacWrapper newInstance(ACojacWrapper w) {
        try {
            Constructor<?> c=COJAC_WRAPPER_NG_CLASS.getConstructor(ACojacWrapper.class);
            return (ACojacWrapper)c.newInstance(w);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


}
