/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst
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


import org.apache.commons.math3.complex.Complex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WrapperComplexNumber extends ACojacWrapper<WrapperComplexNumber> {
    private static final String NUMBER_PATTERN = "(?:.*[^E])";
    private static final Pattern COMPLEX_PATTERN =
            Pattern.compile("((?:[+-]?)" + NUMBER_PATTERN + ")((?:[+-])" + NUMBER_PATTERN + "?)[ij]");
    private static final Pattern REAL_PATTERN =
            Pattern.compile("((?:[+-]?)" + NUMBER_PATTERN + ")");
    private static final Pattern IMAGINARY_PATTERN =
            Pattern.compile("((?:[+-]?)" + NUMBER_PATTERN + "?)([ij])");
    protected static boolean strictMode;
    protected final Complex complex;

    private WrapperComplexNumber(double v) {
        this(v, 0);
    }

    protected WrapperComplexNumber(double real, double imaginary) {
        this.complex = new Complex(real, imaginary);
    }

    private WrapperComplexNumber(Complex complex) {
        this.complex = complex;
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperComplexNumber(WrapperComplexNumber w) {
        // CommonDouble can call this constructor with a null wrapper
        if (w == null) {
            this.complex = new Complex(0, 0);
        } else {
            Complex value = w.complex;
            this.complex = new Complex(value.getReal(), value.getImaginary());
        }
    }

    //-------------------------------------------------------------------------    
    @Override
    public double toDouble() {
        if (strictMode && this.complex.getImaginary() != 0) {
            throw new ClassCastException("Imaginary part lost when casting to double. Lost of imaginary part is not " +
                    "allowed in strict mode: " + this.complex);
        }
        return this.complex.getReal();
    }

    @SuppressWarnings("unused")
    @Override
    public WrapperComplexNumber fromDouble(double a, boolean wasFromFloat) {
        return new WrapperComplexNumber(a);
    }

    @Override
    public WrapperComplexNumber fromString(String a, boolean wasFromFloat) {
        a = a.replaceAll("\\s+", "");

        Matcher matcher = COMPLEX_PATTERN.matcher(a);
        if (matcher.matches()) {
            String imaginaryStr = matcher.group(2);
            // for +i or -i which are crops to + or -
            if (imaginaryStr.length() == 1) {
                imaginaryStr += "1";
            }
            return new WrapperComplexNumber(new Complex(Double.parseDouble(matcher.group(1)),
                    Double.parseDouble(imaginaryStr)));
        }

        matcher = IMAGINARY_PATTERN.matcher(a);
        if (matcher.matches()) {
            return new WrapperComplexNumber(new Complex(0, Double.parseDouble(matcher.group(1))));
        }

        matcher = REAL_PATTERN.matcher(a);
        if (matcher.matches()) {
            return new WrapperComplexNumber(new Complex(Double.parseDouble(matcher.group(1)), 0));
        }
        throw new NumberFormatException("Invalid format for complex number: " + a);
    }

    @Override
    public String asInternalString() {
        return this.complex.toString();
    }

    @Override
    public String wrapperName() {
        return "Complex";
    }

    @Override
    public WrapperComplexNumber dadd(WrapperComplexNumber wrapper) {
        Complex complex = wrapper.complex;
        return new WrapperComplexNumber(this.complex.add(complex));
    }

    @Override
    public WrapperComplexNumber dsub(WrapperComplexNumber wrapper) {
        Complex complex = wrapper.complex;
        return new WrapperComplexNumber(this.complex.subtract(complex));
    }

    @Override
    public WrapperComplexNumber dmul(WrapperComplexNumber wrapper) {
        Complex complex = wrapper.complex;
        return new WrapperComplexNumber(this.complex.multiply(complex));
    }

    @Override
    public WrapperComplexNumber ddiv(WrapperComplexNumber wrapper) {
        Complex complex = wrapper.complex;
        return new WrapperComplexNumber(this.complex.divide(complex));
    }

    @Override
    public WrapperComplexNumber drem(WrapperComplexNumber b) {
        Complex complex = b.complex;
        Complex quotient = this.complex.divide(complex);
        quotient = new Complex(Math.round(quotient.getReal()), Math.round(quotient.getImaginary()));
        Complex remainder = this.complex.subtract(quotient.multiply(complex));
        return new WrapperComplexNumber(remainder);
    }

    @Override
    public WrapperComplexNumber dneg() {
        return new WrapperComplexNumber(this.complex.negate());
    }

    @Override
    public WrapperComplexNumber math_sqrt() {
        return new WrapperComplexNumber(this.complex.sqrt());
    }

    @Override
    public WrapperComplexNumber math_abs() {
        double r = this.complex.getReal();
        double i = this.complex.getImaginary();
        return new WrapperComplexNumber(new Complex(r * r + i * i).sqrt());
    }

    @Override
    public WrapperComplexNumber math_sin() {
        return new WrapperComplexNumber(this.complex.sin());
    }

    @Override
    public WrapperComplexNumber math_cos() {
        return new WrapperComplexNumber(this.complex.cos());
    }

    @Override
    public WrapperComplexNumber math_tan() {
        return new WrapperComplexNumber(this.complex.tan());
    }

    @Override
    public WrapperComplexNumber math_asin() {
        return new WrapperComplexNumber(this.complex.asin());
    }

    @Override
    public WrapperComplexNumber math_acos() {
        return new WrapperComplexNumber(this.complex.acos());
    }

    @Override
    public WrapperComplexNumber math_atan() {
        return new WrapperComplexNumber(this.complex.atan());
    }

    @Override
    public WrapperComplexNumber math_sinh() {
        return new WrapperComplexNumber(this.complex.sinh());
    }

    @Override
    public WrapperComplexNumber math_cosh() {
        return new WrapperComplexNumber(this.complex.cosh());
    }

    @Override
    public WrapperComplexNumber math_tanh() {
        return new WrapperComplexNumber(this.complex.tanh());
    }

    @Override
    public WrapperComplexNumber math_exp() {
        return new WrapperComplexNumber(this.complex.exp());
    }

    @Override
    public WrapperComplexNumber math_log() {
        return new WrapperComplexNumber(this.complex.log());
    }

    @Override
    public WrapperComplexNumber math_log10() {
        return new WrapperComplexNumber(this.complex.log().divide(Math.log(10)));
    }

    @Override
    public WrapperComplexNumber math_toRadians() {
        return new WrapperComplexNumber(new Complex(
                Math.toRadians(this.complex.getReal()),
                Math.toRadians(this.complex.getImaginary())
        ));
    }

    @Override
    public WrapperComplexNumber math_toDegrees() {
        return new WrapperComplexNumber(new Complex(
                Math.toDegrees(this.complex.getReal()),
                Math.toDegrees(this.complex.getImaginary())
        ));
    }

    @Override
    public WrapperComplexNumber math_pow(WrapperComplexNumber wrapper) {
        Complex complex = wrapper.complex;
        return new WrapperComplexNumber(this.complex.pow(complex));
    }

    @Override
    public int compareTo(WrapperComplexNumber wrapper) {
        if (wrapper == null) {
            throw new IllegalArgumentException();
        }
        Complex complex = wrapper.complex;
        if (this.complex.equals(complex)) {
            return 0;
        }
        if (strictMode && (this.complex.getImaginary() != 0 || complex.getImaginary() != 0)) {
            throw new ArithmeticException("Comparison between complex numbers with imaginary parts is not allowed in " +
                    "strict mode: " + this.complex + " and " + complex);
        }
        int compare = Double.compare(this.complex.getReal(), complex.getReal());
        if (compare == 0) {
            compare = Double.compare(this.complex.getImaginary(), complex.getImaginary());
        }
        return compare;
    }

    @Override
    public boolean isNaN() {
        return this.complex.isNaN();
    }

    @Override
    public WrapperComplexNumber math_hypot(WrapperComplexNumber wrapper) {
        // TODO - improve this naive implementation
        // it is prone to overflow and underflow
        Complex x2 = this.complex.multiply(this.complex);
        Complex y = wrapper.complex;
        Complex y2 = y.multiply(y);
        return new WrapperComplexNumber(x2.add(y2).sqrt());
    }

    @Override
    public String toString() {
        double real = this.complex.getReal();
        double imaginary = this.complex.getImaginary();
        if (imaginary == 0) {
            return real + "";
        }
        if (real == 0) {
            return imaginary + "i";
        }
        String sign = imaginary >= 0 ? " + " : " - ";
        return real + sign + Math.abs(imaginary) + "i";
    }

    protected double getReal() {
        return this.complex.getReal();
    }

    protected double getImaginary() {
        return this.complex.getImaginary();
    }

    public static void setStrictMode(boolean strictMode) {
        WrapperComplexNumber.strictMode = strictMode;
    }

    public static CommonDouble<WrapperComplexNumber> COJAC_MAGIC_getReal(CommonDouble<WrapperComplexNumber> d) {
        double real = d.val.complex.getReal();
        return new CommonDouble<>(new WrapperComplexNumber(real));
    }

    public static CommonDouble<WrapperComplexNumber> COJAC_MAGIC_getImaginary(CommonDouble<WrapperComplexNumber> d) {
        double imaginary = d.val.complex.getImaginary();
        return new CommonDouble<>(new WrapperComplexNumber(imaginary));
    }

    public static boolean COJAC_MAGIC_equals(CommonDouble<WrapperComplexNumber> a, CommonDouble<WrapperComplexNumber> b) {
        Complex c1 = a.val.complex;
        Complex c2 = b.val.complex;
        return c1.equals(c2);
    }
}
