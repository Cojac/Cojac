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

public abstract class ACojacWrapper<T extends ACojacWrapper<T>> implements Comparable<T> {
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------

    // public ConcreteWrapper(ConcreteWrapper v) { }

    //-------------------------------------------------------------------------

    public abstract T dadd(T b);
    public abstract T dsub(T b);
    public abstract T dmul(T b);
    public abstract T ddiv(T b);
    public abstract T drem(T b);
    public abstract T dneg();
    public abstract double toDouble();

    public int dcmpl(T b) {
        if (this.isNaN() || b.isNaN()) return -1;
        return this.compareTo(b);
    }
    public int dcmpg(T b) {
        if (this.isNaN() || b.isNaN()) return +1;
        return this.compareTo(b);
    }
    public int  d2i() { return (int)  toDouble();}
    public long d2l() { return (long) toDouble();}
    public abstract T math_sqrt();

    public T math_cbrt() {
        return math_pow(fromDouble(1d/3, false));
    }

    public abstract T math_abs();
    public abstract T math_sin();
    public abstract T math_cos();
    public abstract T math_tan();
    public abstract T math_asin();
    public abstract T math_acos();
    public abstract T math_atan();
    public abstract T math_sinh();
    public abstract T math_cosh();
    public abstract T math_tanh();
    public abstract T math_exp();
    public abstract T math_log();
    public abstract T math_log10();
    public abstract T math_toRadians();
    public abstract T math_toDegrees();

    @SuppressWarnings("unchecked")
    public T math_min(T b) {
        if (this.compareTo(b)<0) return (T)this;
        return b;
    }

    @SuppressWarnings("unchecked")
    public T math_max(T b) {
        if (this.compareTo(b)>0) return (T)this;
        return b;
    }
    
    public abstract T math_pow(T b);
    // TODO make this abstract and implement it for all wrappers
    public T math_hypot(T b) { throw new UnsupportedOperationException();}

    public T math_fma(T a, T b) {
        return this.dmul(a).dadd(b);
    }
    
    public boolean isNaN() {
        return Double.isNaN(toDouble());
    }
    //-------------------------------------------------------------------------
    /** wasFromFloat can be used to distinguish two kinds of numbers */
    public abstract T fromDouble(double a, boolean wasFromFloat);

    public T fromString(String a, boolean wasFromFloat){
        return fromDouble(Double.parseDouble(a), wasFromFloat);
    }
    
    
    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------
    
	@Override public int compareTo(T o) {
	    return Double.compare(toDouble(), o.toDouble());
	}

    @SuppressWarnings("unchecked")
    @Override public boolean equals(Object obj) {
        if (obj == null) return false;
        if (! (obj instanceof ACojacWrapper)) return false;
        ACojacWrapper<T> ow;
        ow = (ACojacWrapper<T>)obj;
        return Double.valueOf(toDouble()).equals(ow.toDouble());
    }

	@Override public int hashCode()       { return Double.hashCode(toDouble()); }
    @Override public String toString()    { return Double.toString(toDouble()); }

    public abstract String asInternalString();
    
    //-------------------------------------------------------------------------
    //----------------- "Magic" methods ---------------------------------------
    //-------------------------------------------------------------------------

    // Only 2 magic methods are common; the others will be specific...
    public abstract String wrapperName();
}
