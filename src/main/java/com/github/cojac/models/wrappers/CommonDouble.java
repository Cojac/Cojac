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

public class CommonDouble<T extends ACojacWrapper<T>> extends Number implements Comparable<CommonDouble<T>>{
    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    protected final T val;
    
    protected CommonDouble(T w) {
        this.val=w;
    }
    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public CommonDouble(double v) {
        val = newInstance((T)null).fromDouble(v, false);
    }
    
    public CommonDouble(String v) {
        val = newInstance((T)null).fromString(v, false);
    }
    
    public CommonDouble(CommonFloat<T> v) {
        val=newInstance(v.val);
    }
    
    public CommonDouble(CommonDouble<T> v) {
        val=newInstance(v.val);
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------
    
    public static <T extends ACojacWrapper<T>> CommonDouble<T> dadd(CommonDouble<T> a, CommonDouble<T> b) {
        return new CommonDouble<>(a.val.dadd(b.val));
    }
    
    public static <T extends ACojacWrapper<T>> CommonDouble<T> dsub(CommonDouble<T> a, CommonDouble<T> b) {
        return new CommonDouble<>(a.val.dsub(b.val));
    }

    public static <T extends ACojacWrapper<T>> CommonDouble<T> dmul(CommonDouble<T> a, CommonDouble<T> b) {
        return new CommonDouble<>(a.val.dmul(b.val));
    }

    public static <T extends ACojacWrapper<T>> CommonDouble<T> ddiv(CommonDouble<T> a, CommonDouble<T> b) {
        return new CommonDouble<>(a.val.ddiv(b.val));
    }

    public static <T extends ACojacWrapper<T>> CommonDouble<T> drem(CommonDouble<T> a, CommonDouble<T> b) {
        return new CommonDouble<>(a.val.drem(b.val));
    }
    
    public static <T extends ACojacWrapper<T>> CommonDouble<T> dneg(CommonDouble<T> a) {
        return new CommonDouble<>(a.val.dneg());
    }

    public static <T extends ACojacWrapper<T>> double toDouble(CommonDouble<T> a) {
        return a.val.toDouble();
    }
    
    public static <T extends ACojacWrapper<T>> Double toRealDoubleWrapper(CommonDouble<T> a){
        return a.val.toDouble();
    }
    
    public static <T extends ACojacWrapper<T>> int dcmpl(CommonDouble<T> a, CommonDouble<T> b) {
        return a.val.dcmpl(b.val);
    }
    
    public static <T extends ACojacWrapper<T>> int dcmpg(CommonDouble<T> a, CommonDouble<T> b) {
        return a.val.dcmpg(b.val);
    }
    
    public static <T extends ACojacWrapper<T>> int d2i(CommonDouble<T> a) {
        return (int)a.val.toDouble();
    }
    
    public static <T extends ACojacWrapper<T>> long d2l(CommonDouble<T> a) {
        return (long)a.val.toDouble();
    }

    public static <T extends ACojacWrapper<T>> boolean double_isNaN(CommonDouble<T> a){
        return a.val.isNaN();
    }

    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_sqrt(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_sqrt());
    }

    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_cbrt(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_cbrt());
    }
	
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_abs(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_abs());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_sin(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_sin());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_cos(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_cos());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_tan(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_tan());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_asin(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_asin());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_acos(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_acos());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_atan(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_atan());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_sinh(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_sinh());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_cosh(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_cosh());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_tanh(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_tanh());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_exp(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_exp());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_log(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_log());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_log10(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_log10());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_toRadians(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_toRadians());
    }
    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_toDegrees(CommonDouble<T> a){
        return new CommonDouble<>(a.val.math_toDegrees());
    }

    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_min(CommonDouble<T> a, CommonDouble<T> b) {
        return new CommonDouble<>(a.val.math_min(b.val));
    }

    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_max(CommonDouble<T> a, CommonDouble<T> b) {
        return new CommonDouble<>(a.val.math_max(b.val));
    }

    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_pow(CommonDouble<T> a, CommonDouble<T> b) {
        return new CommonDouble<>(a.val.math_pow(b.val));
    }

    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_hypot(CommonDouble<T> a, CommonDouble<T> b) {
        return new CommonDouble<>(a.val.math_hypot(b.val));
    }

    public static <T extends ACojacWrapper<T>> CommonDouble<T> math_fma(CommonDouble<T> a, CommonDouble<T> b, CommonDouble<T> c){
        return new CommonDouble<>(a.val.math_fma(b.val, c.val));
    }
    	
    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static <T extends ACojacWrapper<T>> CommonDouble<T> fromDouble(double a) {
        return new CommonDouble<>(a);
    }
    
    public static <T extends ACojacWrapper<T>> CommonDouble<T> fromRealDoubleWrapper(Double a) {
        return fromDouble(a);
    }

    public static <T extends ACojacWrapper<T>> CommonDouble<T> fromString(String a){
        return new CommonDouble<>(a);
    }
    
    public static <T extends ACojacWrapper<T>> CommonDouble<T> i2d(int a) {
        return fromDouble(a);
    }
    
    public static <T extends ACojacWrapper<T>> CommonDouble<T> l2d(long a) {
        return fromDouble(a);
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------
    
	@Override public int compareTo(CommonDouble<T> o) {
        return this.val.compareTo(o.val);
	}
	
    @Override public boolean equals(Object obj) {
        if (obj == null) return false;
        if (! (obj instanceof CommonDouble)) return false;
        CommonDouble<T> ow = (CommonDouble<T>)obj;
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
	
    public static <T extends ACojacWrapper<T>>String COJAC_MAGIC_wrapperName() {
        return newInstance((T)null).wrapperName();
    }

    public static <T extends ACojacWrapper<T>> String COJAC_MAGIC_toString(CommonDouble<T> n) {
        return n.val.asInternalString();
    }

    public static <T extends ACojacWrapper<T>> String COJAC_MAGIC_toString(CommonFloat<T> n) {
        return n.val.asInternalString();
    }

    // TODO: remove once we switch to the NG wrapper
    public static <T extends ACojacWrapper<T>> String COJAC_MAGIC_DOUBLE_wrapper() {
        return newInstance((T)null).wrapperName();
    }

    // TODO: remove once we switch to the NG wrapper
    public static <T extends ACojacWrapper<T>> String COJAC_MAGIC_DOUBLE_toStr(CommonDouble<T> n) {
        return n.val.asInternalString();
    }
    
    // TODO: remove once we switch to the NG wrapper
    public static <T extends ACojacWrapper<T>> String COJAC_MAGIC_DOUBLE_toStr(CommonFloat<T> n) {
        return n.val.asInternalString();
    }
    
    //-------------------------------------------------------------------------
    //--------------------- Auxiliary methods ---------------------------------
	//------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    protected static <T extends ACojacWrapper<T>> T newInstance(T w) {
        try {
            Constructor<?> c=COJAC_WRAPPER_NG_CLASS.getConstructor(COJAC_WRAPPER_NG_CLASS);
            return (T)c.newInstance(w);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


}
