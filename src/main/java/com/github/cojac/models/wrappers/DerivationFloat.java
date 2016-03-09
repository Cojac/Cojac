/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst & HEIA-FR students
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

/* This class belongs to the "old generation" wrapping mechanism, which
 * might be deprecated in later releases. The Cojac front-end now relies
 * on CommonFloat/CommonDouble, along with the number models named
 * WrapperXYZ: WrapperBigDecimal, WrapperInterval, WrapperStochastic, 
 * WrapperDerivation...
 */

// This float wrapper is done by delegation to DerivationDouble
public class DerivationFloat extends Number implements
        Comparable<DerivationFloat> {

    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    protected final DerivationDouble delegate;

    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public DerivationFloat(float v) {
        delegate = new DerivationDouble(v);
    }

    public DerivationFloat(String v) {
        delegate = new DerivationDouble(v);
    }

    public DerivationFloat(DerivationFloat v) {
        delegate = new DerivationDouble(v.delegate);
    }

    public DerivationFloat(DerivationDouble v) {
        delegate = new DerivationDouble(v);
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------

    public static DerivationFloat fadd(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat(DerivationDouble.dadd(a.delegate, b.delegate));
    }

    public static DerivationFloat fsub(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat(DerivationDouble.dsub(a.delegate, b.delegate));
    }

    public static DerivationFloat fmul(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat(DerivationDouble.dmul(a.delegate, b.delegate));
    }

    public static DerivationFloat fdiv(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat(DerivationDouble.ddiv(a.delegate, b.delegate));
    }

    public static DerivationFloat frem(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat(DerivationDouble.drem(a.delegate, b.delegate));
    }

    public static DerivationFloat fneg(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.dneg(a.delegate));
    }

    public static float toFloat(DerivationFloat a) {
        return (float) DerivationDouble.toDouble(a.delegate);
    }

    public static Float toRealFloatWrapper(DerivationFloat a) {
        return toFloat(a);
    }

    public static int fcmpl(DerivationFloat a, DerivationFloat b) {
        return DerivationDouble.dcmpl(a.delegate, b.delegate);
    }

    public static int fcmpg(DerivationFloat a, DerivationFloat b) {
        return DerivationDouble.dcmpg(a.delegate, b.delegate);
    }

    public static DerivationFloat math_abs(DerivationFloat a) {
        return new DerivationFloat((DerivationDouble.math_abs(a.delegate)));
    }

    public static DerivationFloat math_min(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat((DerivationDouble.math_min(a.delegate, b.delegate)));
    }
    public static DerivationFloat math_max(DerivationFloat a, DerivationFloat b) {
        return new DerivationFloat((DerivationDouble.math_max(a.delegate, b.delegate)));
    }

    public static int f2i(DerivationFloat a) {
        return DerivationDouble.d2i(a.delegate);
    }

    public static long f2l(DerivationFloat a) {
        return DerivationDouble.d2l(a.delegate);
    }

    public static DerivationDouble f2d(DerivationFloat a) {
        return new DerivationDouble(a.delegate);
    }

    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static DerivationFloat fromString(String v) {
        return new DerivationFloat(v);
    }

    public static DerivationFloat fromFloat(float v) {
        return new DerivationFloat(v);
    }

    public static DerivationFloat fromRealFloatWrapper(Float v) {
        return fromFloat(v);
    }
    
    public static DerivationFloat d2f(DerivationDouble a) {
        return new DerivationFloat(a);
    }

    public static DerivationFloat i2f(int a) {
        return new DerivationFloat(DerivationDouble.i2d(a));
    }

    public static DerivationFloat l2f(long a) {
        return new DerivationFloat(DerivationDouble.l2d(a));
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------

    @Override public int compareTo(DerivationFloat o) {
        return delegate.compareTo(o.delegate);
    }

    @Override public boolean equals(Object obj) {
        if (obj==null || !(obj instanceof DerivationFloat)) return false;
        return delegate.equals(((DerivationFloat)obj).delegate);
    }
    
    @Override public int hashCode() {
        return delegate.hashCode();
    }
    
    @Override public String toString() {
        return delegate.toString();
    }
    
    @Override public int intValue() {
        return delegate.intValue();
    }

    @Override public long longValue() {
        return delegate.longValue();
    }

    @Override public float floatValue() {
        return delegate.floatValue();
    }

    @Override public double doubleValue() {
        return delegate.doubleValue();
    }
    
    //-------------------------------------------------------------------------
    //----------------- "Magic" methods ---------------------------------------
    //-------------------------------------------------------------------------

    public static String COJAC_MAGIC_FLOAT_wrapper() {
        return "AutoDiff";
    }

    public static String COJAC_MAGIC_FLOAT_toStr(DerivationFloat n) {
        return DerivationDouble.COJAC_MAGIC_DOUBLE_toStr(n.delegate);
    }

    public static DerivationFloat COJAC_MAGIC_FLOAT_getDerivation(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.COJAC_MAGIC_DOUBLE_getDerivation(a.delegate));
    }

    public static DerivationFloat COJAC_MAGIC_FLOAT_asDerivationTarget(DerivationFloat a) {
        return new DerivationFloat(DerivationDouble.COJAC_MAGIC_DOUBLE_asDerivationTarget(a.delegate));
    }

}
