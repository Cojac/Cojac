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

import static com.github.cojac.models.FloatReplacerClasses.COJAC_BIGDECIMAL_PRECISION;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/* This class belongs to the "old generation" wrapping mechanism, which
 * might be deprecated in later releases. The Cojac front-end now relies
 * on CommonFloat/CommonDouble, along with the number models named
 * WrapperXYZ: WrapperBigDecimal, WrapperInterval, WrapperStochastic, 
 * WrapperDerivation...
 */

public class BigDecimalFloat extends Number implements
        Comparable<BigDecimalFloat> {
    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------
    
    private final BigDecimal val;

    private final boolean isNaN;
    private final boolean isInfinite;
    private final boolean isPositiveInfinite;

    private static MathContext mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);;

    BigDecimalFloat(BigDecimal v) {
        val = v;
        isNaN = isInfinite = isPositiveInfinite = false;
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public BigDecimalFloat(float v) {
        if (Float.isNaN(v)) {
            isNaN = true;
            isInfinite = isPositiveInfinite = false;
            val = null;
        } else if (Float.isInfinite(v)) {
            isInfinite = true;
            isPositiveInfinite = v > 0;
            isNaN = false;
            val = null;
        } else {
            val = new BigDecimal(v, mathContext);
            isNaN = isInfinite = isPositiveInfinite = false;
        }
    }

    public BigDecimalFloat(String v) {
        mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        val = new BigDecimal(v, mathContext);
        isNaN = isInfinite = isPositiveInfinite = false;
    }

    public BigDecimalFloat(BigDecimalFloat v) {
        this(v.val);
    }

    public BigDecimalFloat(BigDecimalDouble v) {
        this(v.toBigDecimal());
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------

    public static BigDecimalFloat fadd(BigDecimalFloat a, BigDecimalFloat b) {
        if (a.isNaN || b.isNaN)
            return new BigDecimalFloat(Float.NaN);
        if (a.isInfinite || b.isInfinite) {
            float aVal = a.getFloatInfiniteValue();
            float bVal = b.getFloatInfiniteValue();
            return new BigDecimalFloat(aVal + bVal);
        }
        return new BigDecimalFloat(a.val.add(b.val, mathContext));
    }

    public static BigDecimalFloat fsub(BigDecimalFloat a, BigDecimalFloat b) {
        if (a.isNaN || b.isNaN)
            return new BigDecimalFloat(Float.NaN);
        if (a.isInfinite || b.isInfinite) {
            float aVal = a.getFloatInfiniteValue();
            float bVal = b.getFloatInfiniteValue();
            return new BigDecimalFloat(aVal - bVal);
        }
        return new BigDecimalFloat(a.val.subtract(b.val, mathContext));
    }

    public static BigDecimalFloat fmul(BigDecimalFloat a, BigDecimalFloat b) {
        if (a.isNaN || b.isNaN)
            return new BigDecimalFloat(Float.NaN);
        if (a.isInfinite || b.isInfinite) {
            float aVal = a.getFloatInfiniteValue();
            float bVal = b.getFloatInfiniteValue();
            return new BigDecimalFloat(aVal * bVal);
        }
        return new BigDecimalFloat(a.val.multiply(b.val, mathContext));
    }

    public static BigDecimalFloat fdiv(BigDecimalFloat a, BigDecimalFloat b) {
        if (a.isNaN || b.isNaN)
            return new BigDecimalFloat(Float.NaN);
        if (a.isInfinite || b.isInfinite) {
            float aVal = a.getFloatInfiniteValue();
            float bVal = b.getFloatInfiniteValue();
            return new BigDecimalFloat(aVal / bVal);
        }
        if (b.val.equals(BigDecimal.ZERO))
            return new BigDecimalFloat(a.val.floatValue() / 0.0f);
        return new BigDecimalFloat(a.val.divide(b.val, mathContext));
    }

    public static BigDecimalFloat frem(BigDecimalFloat a, BigDecimalFloat b) {
        if (a.isNaN || b.isNaN)
            return new BigDecimalFloat(Float.NaN);
        if (a.isInfinite || b.isInfinite) {
            float aVal = a.getFloatInfiniteValue();
            float bVal = b.getFloatInfiniteValue();
            return new BigDecimalFloat(aVal % bVal);
        }
        BigDecimal rem=a.val.remainder(b.val, mathContext);
        if(a.val.compareTo(BigDecimal.ZERO)<0) rem=rem.negate();
        return new BigDecimalFloat(rem);
    }

    public static BigDecimalFloat fneg(BigDecimalFloat a) {
        if (a.isNaN)
            return new BigDecimalFloat(Float.NaN);
        if (a.isInfinite) {
            float aVal = a.getFloatInfiniteValue();
            return new BigDecimalFloat(-aVal);
        }
        return new BigDecimalFloat(a.val.negate(mathContext));
    }

    public static float toFloat(BigDecimalFloat a) {
        if (a.isNaN)
            return Float.NaN;
        if (a.isInfinite)
            return (a.isPositiveInfinite) ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        return a.val.floatValue();
    }

    public static Float toRealFloatWrapper(BigDecimalFloat a) {
        return toFloat(a);
    }

    public static int fcmpl(BigDecimalFloat a, BigDecimalFloat b) {
        if (a.isNaN || b.isNaN) return -1; 
        return a.compareTo(b); 
    }

    public static int fcmpg(BigDecimalFloat a, BigDecimalFloat b) {
        if (a.isNaN || b.isNaN) return +1; 
        return a.compareTo(b);
    }

    /* Mathematical function */
    public static BigDecimalFloat math_abs(BigDecimalFloat value) {
        return new BigDecimalFloat(value.val.abs());
    }

    public static BigDecimalFloat math_max(BigDecimalFloat valueA, BigDecimalFloat valueB) {
        return (valueA.compareTo(valueB) > 0) ? valueA : valueB;
    }

    public static BigDecimalFloat math_min(BigDecimalFloat valueA, BigDecimalFloat valueB) {
        return (valueA.compareTo(valueB) < 0) ? valueA : valueB;
    }

    public static int f2i(BigDecimalFloat a) {
        return (int) toFloat(a);
    }

    public static long f2l(BigDecimalFloat a) {
        return (long) toFloat(a);
    }

    public static BigDecimalDouble f2d(BigDecimalFloat a) {
        if (a.isNaN)
            return BigDecimalDouble.fromDouble(Double.NaN);
        if (a.isInfinite) {
            double v=a.isPositiveInfinite ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
            return BigDecimalDouble.fromDouble(v);
        }
        return new BigDecimalDouble(a.val);
    }

    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static BigDecimalFloat fromString(String a) {
        return new BigDecimalFloat(a);
    }

    public static BigDecimalFloat fromFloat(float a) {
        return new BigDecimalFloat(a);
    }

    public static BigDecimalFloat fromRealFloatWrapper(Float a) {
        return fromFloat(a);
    }

    public static BigDecimalFloat d2f(BigDecimalDouble a) {
        if (a.isNaN)
            return BigDecimalFloat.fromFloat(Float.NaN);
        if (a.isInfinite) {
            float v=a.isPositiveInfinite ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
            return BigDecimalFloat.fromFloat(v);
        }
        return new BigDecimalFloat(a.val);
    }

    public static BigDecimalFloat i2f(int a) {
        return new BigDecimalFloat(a);
    }

    public static BigDecimalFloat l2f(long a) {
        return new BigDecimalFloat(a);
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------

    @Override public String toString() {
        if (isNaN)
            return "NaN";
        if (isInfinite)
            return (isPositiveInfinite ? '+' : '-') + "Infinity";
        return val.toString();
    }

    @Override public int compareTo(BigDecimalFloat o) {
        if (isNaN && o.isNaN)
            return 0;
        if (isNaN)
            return 1;
        if (o.isNaN)
            return -1;

        if (isInfinite || o.isInfinite) {
            Float aVal = getFloatInfiniteValue();
            Float bVal = o.getFloatInfiniteValue();
            return aVal.compareTo(bVal);
        }

        return val.compareTo(o.val);
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof BigDecimalFloat == false)
            return false;
        BigDecimalFloat bdd = (BigDecimalFloat) obj;
        if (isNaN && bdd.isNaN)
            return true;
        if (isNaN || bdd.isNaN)
            return false;

        if (isInfinite || bdd.isInfinite) {
            Float aVal = getFloatInfiniteValue();
            Float bVal = bdd.getFloatInfiniteValue();
            return aVal.equals(bVal);
        }

        return val.equals(bdd.val);
    }

    @Override public int hashCode() {
        int hash = 9;
        if (isNaN) return hash;
        if (isInfinite) return hash + (isPositiveInfinite ? 3 : 5);
        return hash + Objects.hashCode(this.val);
    }

    @Override public int intValue() {
        return val.intValue();
    }

    @Override public long longValue() {
        return val.longValue();
    }

    @Override public float floatValue() {
        return toFloat(this);
    }

    @Override public double doubleValue() {
        return toFloat(this);
    }

    //-------------------------------------------------------------------------
    //----------------- "Magic" methods ---------------------------------------
    //-------------------------------------------------------------------------

    public static String COJAC_MAGIC_FLOAT_wrapper() {
        return "BigDecimal";
    }

    public static String COJAC_MAGIC_FLOAT_toStr(BigDecimalFloat n) {
        return n.toString();
    }

    //-------------------------------------------------------------------------
    //--------------------- Auxiliary methods ---------------------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    private float getFloatInfiniteValue() {
        if (!isInfinite)
            return val.floatValue();
        if (isPositiveInfinite)
            return Float.POSITIVE_INFINITY;
        return Float.NEGATIVE_INFINITY;
    }

    BigDecimal toBigDecimal() {
        return val;
    }

//    public static void setPrecision(int precision) {
//        mathContext = new MathContext(precision);
//    }

}
