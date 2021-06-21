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

public class WrapperComplexNumber extends ACojacWrapper {
    protected final Complex complex;

    private WrapperComplexNumber(double v) {
        this(v, 0);
    }

    public WrapperComplexNumber(double real, double imaginary) {
        this.complex = new Complex(real, imaginary);
    }

    private WrapperComplexNumber(Complex complex) {
        this.complex = new Complex(complex.getReal(), complex.getImaginary());
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperComplexNumber(ACojacWrapper w) {
        if (w == null || !(w instanceof WrapperComplexNumber)) {
            complex = new Complex(0, 0);
        } else {
            Complex value = ((WrapperComplexNumber) w).complex;
            complex = new Complex(value.getReal(), value.getImaginary());
        }
    }

    //-------------------------------------------------------------------------    
    @Override
    public double toDouble() {
        // TODO - throw exception if the imaginary part is lost
        // this currently doesn't work in some cases like Double.isNaN()
        /*if (complex.getImaginary() != 0) {
            throw new RuntimeException("Imaginary part lost when casting to double");
        }*/
        return complex.getReal();
    }

    @SuppressWarnings("unused")
    @Override
    public WrapperComplexNumber fromDouble(double a, boolean wasFromFloat) {
        return new WrapperComplexNumber(a);
    }

    @Override
    public String asInternalString() {
        return complex.toString();
    }

    @Override
    public String wrapperName() {
        return "ComplexNumber";
    }

    @Override
    public WrapperComplexNumber dadd(ACojacWrapper wrapper) {
        Complex complex = castWrapper(wrapper).complex;
        return new WrapperComplexNumber(this.complex.add(complex));
    }

    @Override
    public WrapperComplexNumber dsub(ACojacWrapper wrapper) {
        Complex complex = castWrapper(wrapper).complex;
        return new WrapperComplexNumber(this.complex.subtract(complex));
    }

    @Override
    public WrapperComplexNumber dmul(ACojacWrapper wrapper) {
        Complex complex = castWrapper(wrapper).complex;
        return new WrapperComplexNumber(this.complex.multiply(complex));
    }

    @Override
    public WrapperComplexNumber ddiv(ACojacWrapper wrapper) {
        Complex complex = castWrapper(wrapper).complex;
        return new WrapperComplexNumber(this.complex.divide(complex));
    }

    @Override
    public WrapperComplexNumber drem(ACojacWrapper b) {
        throw new UnsupportedOperationException();
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
    public WrapperComplexNumber math_cbrt() {
        return new WrapperComplexNumber(this.complex.pow(1d / 3d));
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
        throw new UnsupportedOperationException();
    }

    @Override
    public WrapperComplexNumber math_toRadians() {
        return new WrapperComplexNumber(new Complex(
                Math.toRadians(complex.getReal()),
                Math.toRadians(complex.getImaginary())
        ));
    }

    @Override
    public WrapperComplexNumber math_toDegrees() {
        return new WrapperComplexNumber(new Complex(
                Math.toDegrees(complex.getReal()),
                Math.toDegrees(complex.getImaginary())
        ));
    }

    @Override
    public WrapperComplexNumber math_pow(ACojacWrapper wrapper) {
        Complex complex = castWrapper(wrapper).complex;
        return new WrapperComplexNumber(this.complex.pow(complex));
    }

    @Override
    public int compareTo(ACojacWrapper wrapper) {
        if (!(wrapper instanceof WrapperComplexNumber)) {
            throw new IllegalArgumentException();
        }
        Complex complex = castWrapper(wrapper).complex;
        if (this.complex.equals(complex)) {
            return 0;
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

    private static WrapperComplexNumber castWrapper(ACojacWrapper wrapper) {
        return (WrapperComplexNumber) wrapper;
    }

    public double getReal() {
        return complex.getReal();
    }

    public double getImaginary() {
        return complex.getImaginary();
    }
}
