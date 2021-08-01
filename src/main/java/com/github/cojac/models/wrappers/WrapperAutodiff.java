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

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class WrapperAutodiff extends ACompactWrapper<WrapperAutodiff> {
    private final double value;
    private final double deriv;

    private WrapperAutodiff(double value, double dValue) {
        this.value = value;
        this.deriv = dValue;
    }
   
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperAutodiff(WrapperAutodiff w) {
        this(w==null ? 0.0 : w.value,
             w==null ? 0.0 : w.deriv);
    }
    
    //-------------------------------------------------------------------------
    // Most of the operations do not follow those "operator" rules, 
    // and are thus fully redefined
    @Override
    public WrapperAutodiff applyUnaryOp(DoubleUnaryOperator op) {
        return new WrapperAutodiff(op.applyAsDouble(value), op.applyAsDouble(deriv));
    }

    @Override
    public WrapperAutodiff applyBinaryOp(DoubleBinaryOperator op, WrapperAutodiff b) {
        return new WrapperAutodiff(op.applyAsDouble(value, b.value),
                                     op.applyAsDouble(deriv, b.deriv));
    }
    //-------------------------------------------------------------------------

    public WrapperAutodiff dmul(WrapperAutodiff b) {
        double d=this.value*b.deriv + this.deriv*b.value;
        return new WrapperAutodiff(this.value*b.value, d);
    }
    
    public WrapperAutodiff ddiv(WrapperAutodiff b) {
        double d=this.deriv*b.value - this.value*b.deriv;
        return new WrapperAutodiff(this.value/b.value, d);
    }
    
    public WrapperAutodiff drem(WrapperAutodiff b) {
        double d=this.deriv;
        if (b.deriv != 0.0) // this seems hard to consider "general" dividers
            d=Double.NaN;
        return new WrapperAutodiff(this.value % b.value, d);
    }

    public WrapperAutodiff math_sqrt() {
        double value = Math.sqrt(this.value);
        double dValue = this.deriv / (2.0 * Math.sqrt(this.value));
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_abs(){
        double value = Math.abs(this.value);
        double dValue = this.value < 0.0 ? -this.deriv : this.deriv;
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_sin() {
        double value = Math.sin(this.value);
        double dValue = Math.cos(this.value) * this.deriv;
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_cos() {
        double value = Math.cos(this.value);
        double dValue = -Math.sin(this.value) * this.deriv;
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_tan() {
        double value = Math.tan(this.value);
        double dValue = this.deriv / (Math.cos(this.value) * Math.cos(this.value));
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_asin() {
        double value = Math.asin(this.value);
        double dValue = this.deriv / (Math.sqrt(1.0 - this.value * this.value));
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_acos() {
        double value = Math.acos(this.value);
        double dValue = -this.deriv / (Math.sqrt(1.0 - this.value * this.value));
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_atan() {
        double value = Math.atan(this.value);
        double dValue = this.deriv / (1.0 + this.value * this.value);
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_sinh() {
        double value = Math.sinh(this.value);
        double dValue = this.deriv * Math.cosh(this.value);
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_cosh() {
        double value = Math.cosh(this.value);
        double dValue = this.deriv * Math.sinh(this.value);
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_tanh() {
        double value = Math.tanh(this.value);
        double dValue = this.deriv / (Math.cosh(this.value) * Math.cosh(this.value));
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_exp() {
        double value = Math.exp(this.value);
        double dValue = this.deriv * Math.exp(this.value);
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_log() {
        double value = Math.log(this.value);
        double dValue = this.deriv / this.value;
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_log10() {
        double value = Math.log10(this.value);
        double dValue = this.deriv / (this.value * Math.log(10.0));
        return new WrapperAutodiff(value, dValue); 
    }
    
    public WrapperAutodiff math_toRadians() {
        double value = Math.toRadians(this.value);
        return new WrapperAutodiff(value, this.deriv);
    }
    
    public WrapperAutodiff math_toDegrees() {
        double value = Math.toDegrees(this.value);
        return new WrapperAutodiff(value, this.deriv);
    }

    public WrapperAutodiff math_min(WrapperAutodiff b) {
        return (this.value < b.value) ? this : b;
    }
    
    public WrapperAutodiff math_max(WrapperAutodiff b) {
        return (this.value > b.value) ? this : b;
    }
    
    public WrapperAutodiff math_pow(WrapperAutodiff b) {
        double value = Math.pow(this.value, b.value);
        double dValue = Math.pow(this.value, b.value) *
                (((b.value * this.deriv) / this.value) +
                        Math.log(this.value) * b.deriv);
        return new WrapperAutodiff(value, dValue); 
    }
    
    @Override public double toDouble() {
        return value;
    }

    @SuppressWarnings("unused")
    @Override
    public WrapperAutodiff fromDouble(double a, boolean wasFromFloat) {
        return new WrapperAutodiff(a, 0.0);
    }

    @Override public String asInternalString() {
        return value+" (deriv="+deriv+")";
    }

    @Override public String wrapperName() {
        return "Derivation";
    }
    
    // ------------------------------------------------------------------------
    public static CommonDouble COJAC_MAGIC_derivative(CommonDouble d) {
        WrapperAutodiff res=new WrapperAutodiff(der(d.val).deriv, 0);
        return new CommonDouble(res);
    }
    
    // TODO: consider renaming COJAC_MAGIC_derivative(d)
    public static CommonFloat COJAC_MAGIC_derivative(CommonFloat d) {
        WrapperAutodiff res=new WrapperAutodiff(der(d.val).deriv, 0);
        return new CommonFloat(res);
    }
    
    // consider renaming.. but how? asDerivativeVar
    // asDifferentiationVar asIndependentVar? asDerivativeFocus?
    public static CommonDouble COJAC_MAGIC_asDerivativeVar(CommonDouble d) {
        WrapperAutodiff res=new WrapperAutodiff(der(d.val).value, 1.0);
        return new CommonDouble(res);
    }
    
    public static CommonFloat COJAC_MAGIC_asDerivativeVar(CommonFloat d) {
        WrapperAutodiff res=new WrapperAutodiff(der(d.val).value, 1.0);
        return new CommonFloat(res);
    }

    //-------------------------------------------------------------------------
    private static WrapperAutodiff der(ACojacWrapper w) {
        return (WrapperAutodiff)w;
    }
}
