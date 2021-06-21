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

public abstract class ACojacWrapper implements Comparable<ACojacWrapper>{
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------

    // public ACojacWrapper(ACojacWrapper v) { }

    //-------------------------------------------------------------------------
    
    public abstract ACojacWrapper dadd(ACojacWrapper b);
    public abstract ACojacWrapper dsub(ACojacWrapper b);
    public abstract ACojacWrapper dmul(ACojacWrapper b);
    public abstract ACojacWrapper ddiv(ACojacWrapper b);
    public abstract ACojacWrapper drem(ACojacWrapper b);
    public abstract ACojacWrapper dneg();
    public abstract double toDouble();
    
    public int dcmpl(ACojacWrapper b) {
        if (this.isNaN() || b.isNaN()) return -1;
        return this.compareTo(b);
    }
    public int dcmpg(ACojacWrapper b) {
        if (this.isNaN() || b.isNaN()) return +1;
        return this.compareTo(b);
    }
    public int  d2i() { return (int)  toDouble();}
    public long d2l() { return (long) toDouble();}
    public abstract ACojacWrapper math_sqrt();

    // TODO make this abstract and implement it for all wrappers
    public ACojacWrapper math_cbrt() {
        throw new UnsupportedOperationException();
    }

    public abstract ACojacWrapper math_abs();
    public abstract ACojacWrapper math_sin();
    public abstract ACojacWrapper math_cos();
    public abstract ACojacWrapper math_tan();
    public abstract ACojacWrapper math_asin();
    public abstract ACojacWrapper math_acos();
    public abstract ACojacWrapper math_atan();
    public abstract ACojacWrapper math_sinh();
    public abstract ACojacWrapper math_cosh();
    public abstract ACojacWrapper math_tanh();
    public abstract ACojacWrapper math_exp();
    public abstract ACojacWrapper math_log();
    public abstract ACojacWrapper math_log10();
    public abstract ACojacWrapper math_toRadians();
    public abstract ACojacWrapper math_toDegrees();

    public ACojacWrapper math_min(ACojacWrapper b) {
        if (this.compareTo(b)<0) return this;
        return b;
    }
    
    public ACojacWrapper math_max(ACojacWrapper b) {
        if (this.compareTo(b)>0) return this;
        return b;
    }
    
    public abstract ACojacWrapper math_pow(ACojacWrapper b);
    // TODO make this abstract and implement it for all wrappers
    public ACojacWrapper math_hypot(ACojacWrapper b) { throw new UnsupportedOperationException();}
    
    public boolean isNaN() {
        return Double.isNaN(toDouble());
    }
    //-------------------------------------------------------------------------
    /** wasFromFloat can be used to distinguish two kinds of numbers */
    public abstract ACojacWrapper fromDouble(double a, boolean wasFromFloat);
    
    
    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------
    
	@Override public int compareTo(ACojacWrapper o) {
	    return Double.compare(toDouble(), o.toDouble());
	}
	
    @Override public boolean equals(Object obj) {
        if (obj == null) return false;
        if (! (obj instanceof ACojacWrapper)) return false;
        ACojacWrapper ow = (ACojacWrapper)obj;
        return new Double(toDouble()).equals(new Double(ow.toDouble()));
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
