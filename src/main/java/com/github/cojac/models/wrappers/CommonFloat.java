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

import static com.github.cojac.models.wrappers.CommonDouble.newInstance;

/* CommonDouble/CommonFloat is the "new generation" wrapping mechanism, where
 * we isolate the behavior of numbers in a ACojacWrapper abstract class.
 * We consider that cleaner than our first attempt.
 */

public class CommonFloat<T extends ACojacWrapper<T>> extends Number implements Comparable<CommonFloat<T>> {
    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    protected final T val;
    
    protected CommonFloat(T w) {
        this.val=w;
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public CommonFloat(float v) {
        val = newInstance((T)null).fromDouble(v, true);
    }
    
    public CommonFloat(String v) {
        val = newInstance((T)null).fromString(v, true);
    }
    
    public CommonFloat(CommonFloat<T> v) {
        val=newInstance(v.val);
    }
    
    public CommonFloat(CommonDouble<T> v) {
        val=newInstance(v.val);
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------

    public static <T extends ACojacWrapper<T>> CommonFloat<T> fadd(CommonFloat<T> a, CommonFloat<T> b) {
        return new CommonFloat<>(a.val.dadd(b.val));
    }
    
    public static <T extends ACojacWrapper<T>> CommonFloat<T> fsub(CommonFloat<T> a, CommonFloat<T> b) {
        return new CommonFloat<>(a.val.dsub(b.val));
    }

    public static <T extends ACojacWrapper<T>> CommonFloat<T> fmul(CommonFloat<T> a, CommonFloat<T> b) {
        return new CommonFloat<>(a.val.dmul(b.val));
    }

    public static <T extends ACojacWrapper<T>> CommonFloat<T> fdiv(CommonFloat<T> a, CommonFloat<T> b) {
        return new CommonFloat<>(a.val.ddiv(b.val));
    }

    public static <T extends ACojacWrapper<T>> CommonFloat<T> frem(CommonFloat<T> a, CommonFloat<T> b) {
        return new CommonFloat<>(a.val.drem(b.val));
    }
    
    public static <T extends ACojacWrapper<T>> CommonFloat<T> fneg(CommonFloat<T> a) {
        return new CommonFloat<>(a.val.dneg());
    }

    public static <T extends ACojacWrapper<T>> float toFloat(CommonFloat<T> a) {
        return (float)a.val.toDouble();
    }
    
    public static <T extends ACojacWrapper<T>> Float toRealFloatWrapper(CommonFloat<T> a){
        return toFloat(a);
    }
    
    public static <T extends ACojacWrapper<T>> int fcmpl(CommonFloat<T> a, CommonFloat<T> b) {
        return a.val.dcmpl(b.val);
    }
    
    public static <T extends ACojacWrapper<T>> int fcmpg(CommonFloat<T> a, CommonFloat<T> b) {
        return a.val.dcmpg(b.val);
    }
    
    public static <T extends ACojacWrapper<T>> CommonFloat<T> math_abs(CommonFloat<T> a) {
        return new CommonFloat<>(a.val.math_abs());
    }

    public static <T extends ACojacWrapper<T>> CommonFloat<T> math_min(CommonFloat<T> a, CommonFloat<T> b) {
        return new CommonFloat<>(a.val.math_min(b.val));
    }

    public static <T extends ACojacWrapper<T>> CommonFloat<T> math_max(CommonFloat<T> a, CommonFloat<T> b) {
        return new CommonFloat<>(a.val.math_max(b.val));
    }

    public static <T extends ACojacWrapper<T>> int f2i(CommonFloat<T> a) {
        return (int)a.val.toDouble();
    }
    
    public static <T extends ACojacWrapper<T>> long f2l(CommonFloat<T> a) {
        return (long)a.val.toDouble();
    }
    
    public static <T extends ACojacWrapper<T>> CommonDouble<T> f2d(CommonFloat<T> a) {
        return new CommonDouble<>(a.val);
    }

    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static <T extends ACojacWrapper<T>> CommonFloat<T> fromFloat(float a) {
        return new CommonFloat<>(a);
    }

    public static <T extends ACojacWrapper<T>> CommonFloat<T> fromRealFloatWrapper(Float a) {
        return fromFloat(a);
    }

    public static <T extends ACojacWrapper<T>> CommonFloat<T> fromString(String a){
        return new CommonFloat<>(a);
    }
    
    public static <T extends ACojacWrapper<T>> CommonFloat<T> d2f(CommonDouble<T> a) {
        return new CommonFloat<>(a.val);
    }
    
    public static <T extends ACojacWrapper<T>> CommonFloat<T> i2f(int a) {
        return fromFloat(a);
    }
    
    public static <T extends ACojacWrapper<T>> CommonFloat<T> l2f(long a) {
        return fromFloat(a);
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------

    @Override public int compareTo(CommonFloat<T> o) {
        return this.val.compareTo(o.val);
	}

    @SuppressWarnings("unchecked")
	@Override public boolean equals(Object obj) {
        if (obj == null) return false;
        if (! (obj instanceof CommonFloat)) return false;
        CommonFloat<T> ow = (CommonFloat<T>)obj;
        return this.val.equals(ow.val);
    }

	@Override public int hashCode() {
        return this.val.hashCode();
	}

    @Override public String toString() {
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
    //--------------------- Auxiliary methods ---------------------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

}
