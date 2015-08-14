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

package ch.eiafr.cojac.models.wrappers;

// WARNING: this class is not used... for the moment. Towards refactoring the wrapping mechanism
public abstract class ACojacWrapper implements Comparable<ACojacWrapper>{
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------

    public ACojacWrapper(ACojacWrapper v) { }

    //-------------------------------------------------------------------------
    
    public abstract ACojacWrapper dadd(ACojacWrapper b);
    public abstract ACojacWrapper dsub(ACojacWrapper b);
    public abstract ACojacWrapper dmul(ACojacWrapper b);
    public abstract ACojacWrapper ddiv(ACojacWrapper b);
    public abstract ACojacWrapper drem(ACojacWrapper b);
    public abstract ACojacWrapper dneg();
    public abstract double toDouble();
    
    public abstract int dcmpl(ACojacWrapper b);
    public abstract int dcmpg(ACojacWrapper b);
    public int  d2i() { return (int)  toDouble();}
    public long d2l() { return (long) toDouble();}
    public abstract ACojacWrapper math_sqrt();
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

    public abstract ACojacWrapper math_min(ACojacWrapper b);
    public abstract ACojacWrapper math_max(ACojacWrapper b);
    public abstract ACojacWrapper math_pow(ACojacWrapper b);
    
    //-------------------------------------------------------------------------

    public abstract ACojacWrapper fromDouble(double a);
    
    // fromFloat can be overridden, eg to "tag" the number as a "float"...
    public ACojacWrapper fromFloat(double a) { return fromDouble(a); }

    
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
//    @Override public byte byteValue()     { return (byte) toDouble(); }
//    @Override public short shortValue()   { return (short) toDouble(); }
//	@Override public int intValue()       { return (int) toDouble(); }
//	@Override public long longValue()     { return (long) toDouble(); }
//	@Override public float floatValue()   { return (float) toDouble(); }
//	@Override public double doubleValue() { return toDouble(); }

    public abstract String asInternalString();
    
    //-------------------------------------------------------------------------
    //----------------- "Magic" methods ---------------------------------------
    //-------------------------------------------------------------------------

    // Only 2 magic methods are common; the others will be specific...
    public abstract String COJAC_MAGIC_wrapper();
    public String COJAC_MAGIC_toStr() { return asInternalString(); }

}
