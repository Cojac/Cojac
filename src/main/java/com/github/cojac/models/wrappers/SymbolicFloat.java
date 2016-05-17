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

// This float wrapper is done by delegation to SymbolicDouble
public class SymbolicFloat extends Number implements
        Comparable<SymbolicFloat> {

    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    protected final SymbolicDouble delegate;

    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public SymbolicFloat(float v) {
        delegate = new SymbolicDouble(v);
    }

    public SymbolicFloat(String v) {
        delegate = new SymbolicDouble(v);
    }

    public SymbolicFloat(SymbolicFloat v) {
        delegate = new SymbolicDouble(v.delegate);
    }

    public SymbolicFloat(SymbolicDouble v) {
        delegate = new SymbolicDouble(v);
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------

    public static SymbolicFloat fadd(SymbolicFloat a, SymbolicFloat b) {
        return new SymbolicFloat(SymbolicDouble.dadd(a.delegate, b.delegate));
    }

    public static SymbolicFloat fsub(SymbolicFloat a, SymbolicFloat b) {
        return new SymbolicFloat(SymbolicDouble.dsub(a.delegate, b.delegate));
    }

    public static SymbolicFloat fmul(SymbolicFloat a, SymbolicFloat b) {
        return new SymbolicFloat(SymbolicDouble.dmul(a.delegate, b.delegate));
    }

    public static SymbolicFloat fdiv(SymbolicFloat a, SymbolicFloat b) {
        return new SymbolicFloat(SymbolicDouble.ddiv(a.delegate, b.delegate));
    }

    public static SymbolicFloat frem(SymbolicFloat a, SymbolicFloat b) {
        return new SymbolicFloat(SymbolicDouble.drem(a.delegate, b.delegate));
    }

    public static SymbolicFloat fneg(SymbolicFloat a) {
        return new SymbolicFloat(SymbolicDouble.dneg(a.delegate));
    }

    public static float toFloat(SymbolicFloat a) {
        return (float) SymbolicDouble.toDouble(a.delegate);
    }

    public static Float toRealFloatWrapper(SymbolicFloat a) {
        return toFloat(a);
    }

    public static int fcmpl(SymbolicFloat a, SymbolicFloat b) {
        return SymbolicDouble.dcmpl(a.delegate, b.delegate);
    }

    public static int fcmpg(SymbolicFloat a, SymbolicFloat b) {
        return SymbolicDouble.dcmpg(a.delegate, b.delegate);
    }

    public static SymbolicFloat math_abs(SymbolicFloat a) {
        return new SymbolicFloat((SymbolicDouble.math_abs(a.delegate)));
    }

    public static SymbolicFloat math_min(SymbolicFloat a, SymbolicFloat b) {
        return new SymbolicFloat((SymbolicDouble.math_min(a.delegate, b.delegate)));
    }
    public static SymbolicFloat math_max(SymbolicFloat a, SymbolicFloat b) {
        return new SymbolicFloat((SymbolicDouble.math_max(a.delegate, b.delegate)));
    }

    public static int f2i(SymbolicFloat a) {
        return SymbolicDouble.d2i(a.delegate);
    }

    public static long f2l(SymbolicFloat a) {
        return SymbolicDouble.d2l(a.delegate);
    }

    public static SymbolicDouble f2d(SymbolicFloat a) {
        return new SymbolicDouble(a.delegate);
    }

    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static SymbolicFloat fromString(String v) {
        return new SymbolicFloat(v);
    }

    public static SymbolicFloat fromFloat(float v) {
        return new SymbolicFloat(v);
    }

    public static SymbolicFloat fromRealFloatWrapper(Float v) {
        return fromFloat(v);
    }
    
    public static SymbolicFloat d2f(SymbolicDouble a) {
        return new SymbolicFloat(a);
    }

    public static SymbolicFloat i2f(int a) {
        return new SymbolicFloat(SymbolicDouble.i2d(a));
    }

    public static SymbolicFloat l2f(long a) {
        return new SymbolicFloat(SymbolicDouble.l2d(a));
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------

    @Override public int compareTo(SymbolicFloat o) {
        return delegate.compareTo(o.delegate);
    }

    @Override public boolean equals(Object obj) {
        if (obj==null || !(obj instanceof SymbolicFloat)) return false;
        return delegate.equals(((SymbolicFloat)obj).delegate);
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

    public static String COJAC_MAGIC_FLOAT_toStr(SymbolicFloat n) {
        return SymbolicDouble.COJAC_MAGIC_DOUBLE_toStr(n.delegate);
    }

    public static SymbolicFloat COJAC_MAGIC_FLOAT_getDerivation(SymbolicFloat a) {
        return new SymbolicFloat(SymbolicDouble.COJAC_MAGIC_DOUBLE_getDerivation(a.delegate));
    }

    public static SymbolicFloat COJAC_MAGIC_FLOAT_asDerivationTarget(SymbolicFloat a) {
        return new SymbolicFloat(SymbolicDouble.COJAC_MAGIC_DOUBLE_asDerivationTarget(a.delegate));
    }

}
