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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_BIGDECIMAL_PRECISION;

public class BigDecimalDouble extends Number implements
        Comparable<BigDecimalDouble> {
    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    final BigDecimal val;
    final boolean isNaN;
    final boolean isInfinite;
    final boolean isPositiveInfinite;

    private static MathContext mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);

    BigDecimalDouble(BigDecimal v) {
        val = v;
        isNaN = isInfinite = isPositiveInfinite = false;
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    public BigDecimalDouble(double v) {
        if (Double.isNaN(v)) {
            isNaN = true;
            isInfinite = isPositiveInfinite = false;
            val = null;
        } else if (Double.isInfinite(v)) {
            isNaN=false;
            isInfinite = true;
            isPositiveInfinite = v > 0;
            val = null;
        } else {
            val = new BigDecimal(v, mathContext);
            isNaN = isInfinite = isPositiveInfinite = false;
        }
    }

    public BigDecimalDouble(String v) {
        //mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        val = new BigDecimal(v, mathContext);
        isNaN = isInfinite = isPositiveInfinite = false;
    }

    public BigDecimalDouble(BigDecimalFloat v) {
        this(v.toBigDecimal());
    }
    
    public BigDecimalDouble(BigDecimalDouble v) {
        this(v.val);
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------

    public static BigDecimalDouble dadd(BigDecimalDouble a, BigDecimalDouble b) {
        if (a.isNaN || b.isNaN)
            return new BigDecimalDouble(Double.NaN);
        if (a.isInfinite || b.isInfinite) {
            double aVal = a.getDoubleInfiniteValue();
            double bVal = b.getDoubleInfiniteValue();
            return new BigDecimalDouble(aVal + bVal);
        }
        return new BigDecimalDouble(a.val.add(b.val, mathContext));
    }

    public static BigDecimalDouble dsub(BigDecimalDouble a, BigDecimalDouble b) {
        if (a.isNaN || b.isNaN)
            return new BigDecimalDouble(Double.NaN);
        if (a.isInfinite || b.isInfinite) {
            double aVal = a.getDoubleInfiniteValue();
            double bVal = b.getDoubleInfiniteValue();
            return new BigDecimalDouble(aVal - bVal);
        }
        return new BigDecimalDouble(a.val.subtract(b.val, mathContext));
    }

    public static BigDecimalDouble dmul(BigDecimalDouble a, BigDecimalDouble b) {
        if (a.isNaN || b.isNaN)
            return new BigDecimalDouble(Double.NaN);
        if (a.isInfinite || b.isInfinite) {
            double aVal = a.getDoubleInfiniteValue();
            double bVal = b.getDoubleInfiniteValue();
            return new BigDecimalDouble(aVal * bVal);
        }
        return new BigDecimalDouble(a.val.multiply(b.val, mathContext));
    }

    public static BigDecimalDouble ddiv(BigDecimalDouble a, BigDecimalDouble b) {
        if (a.isNaN || b.isNaN)
            return new BigDecimalDouble(Double.NaN);
        if (a.isInfinite || b.isInfinite) {
            double aVal = a.getDoubleInfiniteValue();
            double bVal = b.getDoubleInfiniteValue();
            return new BigDecimalDouble(aVal / bVal);
        }
        if (b.val.equals(BigDecimal.ZERO))
            return new BigDecimalDouble(a.val.doubleValue() / 0.0);
        return new BigDecimalDouble(a.val.divide(b.val, mathContext));
    }

    public static BigDecimalDouble drem(BigDecimalDouble a, BigDecimalDouble b) {
        if (a.isNaN || b.isNaN)
            return new BigDecimalDouble(Double.NaN);
        if (a.isInfinite || b.isInfinite) {
            double aVal = a.getDoubleInfiniteValue();
            double bVal = b.getDoubleInfiniteValue();
            return new BigDecimalDouble(aVal % bVal);
        }
        return new BigDecimalDouble(a.val.remainder(b.val, mathContext));
        // is this correct?
    }

    public static BigDecimalDouble dneg(BigDecimalDouble a) {
        if (a.isNaN)
            return new BigDecimalDouble(Double.NaN);
        if (a.isInfinite) {
            double aVal = a.getDoubleInfiniteValue();
            return new BigDecimalDouble(-aVal);
        }
        return new BigDecimalDouble(a.val.negate(mathContext));
    }

    public static double toDouble(BigDecimalDouble a) {
        if (a.isNaN)
            return Double.NaN;
        if (a.isInfinite)
            return a.isPositiveInfinite ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        return a.val.doubleValue();
    }

    public static Double toRealDoubleWrapper(BigDecimalDouble a) {
        return toDouble(a);
    }

    public static int dcmpl(BigDecimalDouble a, BigDecimalDouble b) {
        if (a.isNaN || b.isNaN) return -1;
        return a.compareTo(b);
    }

    public static int dcmpg(BigDecimalDouble a, BigDecimalDouble b) {
        if (a.isNaN || b.isNaN) return +1;
        return a.compareTo(b);
    }

    public static int d2i(BigDecimalDouble a) {
        return (int) toDouble(a);
//        if (a.isNaN)
//            return (int) Double.NaN;
//        if (a.isInfinite) {
//            if (a.isPositiveInfinite)
//                return (int) Double.POSITIVE_INFINITY;
//            else
//                return (int) Double.NEGATIVE_INFINITY;
//        }
//        return a.val.intValue();
    }

    public static long d2l(BigDecimalDouble a) {
        return (long) toDouble(a);
//        if (a.isNaN)
//            return (long) Double.NaN;
//        if (a.isInfinite) {
//            if (a.isPositiveInfinite)
//                return (long) Double.POSITIVE_INFINITY;
//            else
//                return (long) Double.NEGATIVE_INFINITY;
//        }
//        return a.val.longValue();
    }

//    public static BigDecimalFloat d2f(BigDecimalDouble a) {
//        if (a.isNaN)
//            return new BigDecimalFloat(Float.NaN);
//        if (a.isInfinite) {
//            if (a.isPositiveInfinite)
//                return new BigDecimalFloat(Float.POSITIVE_INFINITY);
//            else
//                return new BigDecimalFloat(Float.NEGATIVE_INFINITY);
//        }
//        return new BigDecimalFloat(a.val);
//    }

    // TODO : make better functions for all math function !

    public static BigDecimalDouble math_sqrt(BigDecimalDouble a) {
        return new BigDecimalDouble(sqrtHeron(a.val));
    }

    public static BigDecimalDouble math_abs(BigDecimalDouble value) {
        return new BigDecimalDouble(value.val.abs());
    }

    public static BigDecimalDouble math_sin(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.sin(value.doubleValue()));
    }

    public static BigDecimalDouble math_cos(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.cos(value.doubleValue()));
    }

    public static BigDecimalDouble math_tan(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.tan(value.doubleValue()));
    }

    public static BigDecimalDouble math_asin(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.asin(value.doubleValue()));
    }

    public static BigDecimalDouble math_acos(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.acos(value.doubleValue()));
    }

    public static BigDecimalDouble math_atan(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.atan(value.doubleValue()));
    }

    public static BigDecimalDouble math_sinh(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.sinh(value.doubleValue()));
    }

    public static BigDecimalDouble math_cosh(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.cosh(value.doubleValue()));
    }

    public static BigDecimalDouble math_tanh(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.tanh(value.doubleValue()));
    }

    public static BigDecimalDouble math_exp(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.exp(value.doubleValue()));
    }

    public static BigDecimalDouble math_log(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.log(value.doubleValue()));
    }

    public static BigDecimalDouble math_log10(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.log10(value.doubleValue()));
    }

    public static BigDecimalDouble math_toRadians(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.toRadians(value.doubleValue()));
    }

    public static BigDecimalDouble math_toDegrees(BigDecimalDouble value) {
        return new BigDecimalDouble(Math.toDegrees(value.doubleValue()));
    }

    public static BigDecimalDouble math_max(BigDecimalDouble valueA, BigDecimalDouble valueB) {
        return (valueA.compareTo(valueB) > 0) ? valueA : valueB;
    }

    public static BigDecimalDouble math_min(BigDecimalDouble valueA, BigDecimalDouble valueB) {
        return (valueA.compareTo(valueB) < 0) ? valueA : valueB;
    }
    
    public static BigDecimalDouble math_pow(BigDecimalDouble base, BigDecimalDouble exponent) {
        // TODO: use the pow method from BigDecimal
        return new BigDecimalDouble(Math.pow(base.doubleValue(), exponent.doubleValue()));
    }

    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static BigDecimalDouble fromDouble(double a) {
        return new BigDecimalDouble(a);
    }

    public static BigDecimalDouble fromRealDoubleWrapper(Double a) {
        return fromDouble(a);
    }

    public static BigDecimalDouble fromString(String a) {
        return new BigDecimalDouble(a);
    }

    public static BigDecimalDouble i2d(int a) {
        return new BigDecimalDouble(new BigDecimal(a));
    }

    public static BigDecimalDouble l2d(long a) {
        return new BigDecimalDouble(new BigDecimal(a));
    }

//    public static BigDecimalDouble f2d(BigDecimalFloat a) {
//        return new BigDecimalDouble(a);
//    }

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

    @Override public int compareTo(BigDecimalDouble o) {
        // if o >  this => -1
        // if o <  this =>  1
        // if o == this =>  0

        if (isNaN && o.isNaN) // according to Double.equals semantics
            return 0;
        if (isNaN)
            return 1;
        if (o.isNaN)
            return -1;

        if (isInfinite || o.isInfinite) {
            Double aVal = getDoubleInfiniteValue();
            Double bVal = o.getDoubleInfiniteValue();
            return aVal.compareTo(bVal);
        }

        return val.compareTo(o.val);
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof BigDecimalDouble == false)
            return false;
        BigDecimalDouble bdd = (BigDecimalDouble) obj;
        if (isNaN && bdd.isNaN) // according to Double.equals semantics
            return true;
        if (isNaN || bdd.isNaN)
            return false;

        if (isInfinite || bdd.isInfinite) {
            Double aVal = getDoubleInfiniteValue();
            Double bVal = bdd.getDoubleInfiniteValue();
            return aVal.equals(bVal);
        }

        return val.equals(bdd.val);
    }

    @Override public int hashCode() {
        int hash = 7;
        if (isNaN) return hash;
        if (isInfinite) return hash + (isPositiveInfinite ? 3 : 5);
        return hash + Objects.hashCode(this.val);
    }

    @Override public int intValue() {
        return d2i(this);
    }

    @Override public long longValue() {
        return d2l(this);
    }

    @Override public float floatValue() {
        return (float) toDouble(this);
    }

    @Override public double doubleValue() {
        return toDouble(this);
    }

    //-------------------------------------------------------------------------
    //----------------- "Magic" methods ---------------------------------------
    //-------------------------------------------------------------------------

    public static String COJAC_MAGIC_DOUBLE_wrapper() {
        return "BigDecimal";
    }

    public static String COJAC_MAGIC_DOUBLE_toStr(BigDecimalDouble n) {
        return n.toString();
    }

    //-------------------------------------------------------------------------
    //--------------------- Auxiliary methods ---------------------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    private double getDoubleInfiniteValue() {
        if (!isInfinite)
            return val.doubleValue();
        if (isPositiveInfinite)
            return Double.POSITIVE_INFINITY;
        return Double.NEGATIVE_INFINITY;
    }

    BigDecimal toBigDecimal() {
        return val;
    }

    private static BigDecimal sqrtHeron(BigDecimal x) {
        BigDecimal epsilon = new BigDecimal(10.0).pow(-COJAC_BIGDECIMAL_PRECISION, mathContext); // precision
        BigDecimal root = new BigDecimal(1.0, mathContext);
        BigDecimal lroot = x.abs(mathContext);

        while (root.subtract(lroot, mathContext).abs(mathContext).compareTo(epsilon) == 1) {
            lroot = root.abs(mathContext);
            root = root.add(x.divide(root, mathContext)).divide(new BigDecimal(2.0, mathContext), mathContext);
        }
        return root;
    }
    
    public static void setPrecision(int precision) {
        mathContext = new MathContext(precision);
    }
    
}
