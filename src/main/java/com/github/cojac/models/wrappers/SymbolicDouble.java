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

//TODO: (not related to this class...) Maybe expand COJAC as: 
//      "Climbing Over Java Arithmetic Computation" or
//      "Creating Other Juicy Arithmetic Capabilities"
//       Creator Of jaunty|jolly|jovial|joyful|joyous|jubilant|judicious|jumbo

/* This class belongs to the "old generation" wrapping mechanism, which
 * might be deprecated in later releases. The Cojac front-end now relies
 * on CommonFloat/CommonDouble, along with the number models named
 * WrapperXYZ: WrapperBigDecimal, WrapperInterval, WrapperStochastic, 
 * WrapperDerivation...
 */

public class SymbolicDouble extends Number implements
        Comparable<SymbolicDouble> {

    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    private final double value;
    private final double deriv;

    private SymbolicDouble(double value, double dValue) {
        this.value = value;
        this.deriv = dValue;
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public SymbolicDouble(double v) {
        this.value = v;
        this.deriv = 0.0;
    }

    public SymbolicDouble(String v) {
        this.value = Double.parseDouble(v);
        this.deriv = 0.0;
    }

    public SymbolicDouble(SymbolicFloat v) {
        this.value = v.delegate.value;
        this.deriv = v.delegate.deriv;
    }

    public SymbolicDouble(SymbolicDouble v) {
        this.value = v.value;
        this.deriv = v.deriv;
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------

    public static SymbolicDouble dadd(SymbolicDouble a, SymbolicDouble b) {
        return new SymbolicDouble(a.value + b.value, a.deriv + b.deriv);
    }

    public static SymbolicDouble dsub(SymbolicDouble a, SymbolicDouble b) {
        return new SymbolicDouble(a.value - b.value, a.deriv - b.deriv);
    }

    public static SymbolicDouble dmul(SymbolicDouble a, SymbolicDouble b) {
        double value = a.value * b.value;
        double dValue = a.deriv * b.value + a.value * b.deriv;
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble ddiv(SymbolicDouble a, SymbolicDouble b) {
        double value = a.value / b.value;
        double dValue = (a.deriv * b.value - b.deriv * a.value) /
                (b.value * b.value);
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble drem(SymbolicDouble a, SymbolicDouble b) {
        double value = a.value % b.value;
        if (b.deriv != 0.0) {
            double dValue = Double.NaN;
            return new SymbolicDouble(value, dValue);
        } 
        double dValue = a.deriv;
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble dneg(SymbolicDouble a) {
        return new SymbolicDouble(-a.value, -a.deriv);
    }

    public static double toDouble(SymbolicDouble a) {
        return a.value;
    }

    public static Double toRealDoubleWrapper(SymbolicDouble a) {
        return new Double(a.value);
    }

    public static int dcmpl(SymbolicDouble a, SymbolicDouble b) {
        if (Double.isNaN(a.value)|| Double.isNaN(b.value)) return -1;
        if (a.value < b.value) return -1;
        if (a.value > b.value) return +1;
        return 0;
    }

    public static int dcmpg(SymbolicDouble a, SymbolicDouble b) {
        if (Double.isNaN(a.value)|| Double.isNaN(b.value)) return +1;
        if (a.value < b.value) return -1;
        if (a.value > b.value) return +1;
        return 0;
    }

    public static int d2i(SymbolicDouble a) {
        return (int) a.value;
    }

    public static long d2l(SymbolicDouble a) {
        return (long) a.value;
    }

    public static SymbolicDouble math_sqrt(SymbolicDouble a) {
        double value = Math.sqrt(a.value);
        double dValue = a.deriv / (2.0 * Math.sqrt(a.value));
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_abs(SymbolicDouble a) {
        double value = Math.abs(a.value);
        double dValue = a.value < 0.0 ? -a.deriv : a.deriv; //BAP: corrected, was dValue
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_sin(SymbolicDouble a) {
        double value = Math.sin(a.value);
        double dValue = Math.cos(a.value) * a.deriv;
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_cos(SymbolicDouble a) {
        double value = Math.cos(a.value);
        double dValue = -Math.sin(a.value) * a.deriv;
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_tan(SymbolicDouble a) {
        double value = Math.tan(a.value);
        double dValue = a.deriv / (Math.cos(a.value) * Math.cos(a.value));
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_asin(SymbolicDouble a) {
        double value = Math.asin(a.value);
        double dValue = a.deriv / (Math.sqrt(1.0 - a.value * a.value));
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_acos(SymbolicDouble a) {
        double value = Math.acos(a.value);
        double dValue = -a.deriv / (Math.sqrt(1.0 - a.value * a.value));
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_atan(SymbolicDouble a) {
        double value = Math.atan(a.value);
        double dValue = a.deriv / (1.0 + a.value * a.value);
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_sinh(SymbolicDouble a) {
        double value = Math.sinh(a.value);
        double dValue = a.deriv * Math.cosh(a.value);
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_cosh(SymbolicDouble a) {
        double value = Math.cosh(a.value);
        double dValue = a.deriv * Math.sinh(a.value);
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_tanh(SymbolicDouble a) {
        double value = Math.tanh(a.value);
        double dValue = a.deriv / (Math.cosh(a.value) * Math.cosh(a.value));
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_exp(SymbolicDouble a) {
        double value = Math.exp(a.value);
        double dValue = a.deriv * Math.exp(a.value);
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_log(SymbolicDouble a) {
        double value = Math.log(a.value);
        double dValue = a.deriv / a.value;
        return new SymbolicDouble(value, dValue);
    }

    public static SymbolicDouble math_log10(SymbolicDouble a) {
        double value = Math.log10(a.value);
        double dValue = a.deriv / (a.value * Math.log(10.0));
        return new SymbolicDouble(value, dValue);
    }
    
    public static SymbolicDouble math_toRadians(SymbolicDouble a) { 
        return new SymbolicDouble(Math.toRadians(a.value), a.deriv);
    }

    public static SymbolicDouble math_toDegrees(SymbolicDouble a) { 
        return new SymbolicDouble(Math.toDegrees(a.value), a.deriv);
    }

    public static SymbolicDouble math_min(SymbolicDouble a, SymbolicDouble b) {
        return (a.value < b.value) ? a : b;
    }

    public static SymbolicDouble math_max(SymbolicDouble a, SymbolicDouble b) {
        return (a.value > b.value) ? a : b;
    }

    public static SymbolicDouble math_pow(SymbolicDouble a, SymbolicDouble b) {
        double value = Math.pow(a.value, b.value);
        double dValue = Math.pow(a.value, b.value) *
                (((b.value * a.deriv) / a.value) + Math.log(a.value) *
                        b.deriv);
        return new SymbolicDouble(value, dValue);
    }

    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static SymbolicDouble fromDouble(double v) {
        return new SymbolicDouble(v);
    }

    public static SymbolicDouble fromRealDoubleWrapper(Double v) {
        return fromDouble(v);
    }

    public static SymbolicDouble fromString(String v) {
        return new SymbolicDouble(v);
    }

    public static SymbolicDouble i2d(int a) {
        return new SymbolicDouble(a);
    }

    public static SymbolicDouble l2d(long a) {
        return new SymbolicDouble(a);
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------

    @Override public int compareTo(SymbolicDouble o) {
        return Double.compare(this.value, o.value);
    }

    @Override public int hashCode() {
        return Double.hashCode(this.value);
    }

    @Override public boolean equals(Object obj) {
        Double d=null;
        if (obj instanceof Double) d=(Double) obj;
        if (obj instanceof SymbolicDouble) 
            d=new Double(((SymbolicDouble) obj).value);
        return new Double(this.value).equals(d);
    }

    @Override public int intValue() {
        return (int) value;
    }

    @Override public long longValue() {
        return (long) value;
    }

    @Override public float floatValue() {
        return (float) value;
    }

    @Override public double doubleValue() {
        return value;
    }
    
    //-------------------------------------------------------------------------
    //----------------- "Magic" methods ---------------------------------------
    //-------------------------------------------------------------------------

    public static String COJAC_MAGIC_DOUBLE_wrapper() {
        return "AutoDiff";
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(SymbolicDouble n) {
        return n.value+" (deriv="+n.deriv+")";
    }

    public static SymbolicDouble COJAC_MAGIC_DOUBLE_getDerivation(SymbolicDouble a) {
        return new SymbolicDouble(a.deriv);
    }

    public static SymbolicDouble COJAC_MAGIC_DOUBLE_asDerivationTarget(SymbolicDouble a) {
        return new SymbolicDouble(a.value, 1.0);
    }

    //-------------------------------------------------------------------------
    //--------------------- Auxiliary methods ---------------------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

}
