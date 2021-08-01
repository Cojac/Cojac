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
import java.util.Comparator;

//TODO: finalize this class... or remove it!
public class WrapperKasu extends ACompactWrapper<WrapperKasu> {
    private final double value;
    private final double dust;

    private WrapperKasu(double value, double dValue) {
        this.value = value;
        this.dust = dValue;
    }
   
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperKasu(WrapperKasu w) {
        this(w==null ? 0.0 : w.value,
             w==null ? 0.0 : w.dust);
    }
    
    //-------------------------------------------------------------------------
    @Override
    public WrapperKasu applyUnaryOp(DoubleUnaryOperator op) {
        return new WrapperKasu(op.applyAsDouble(value), 0.0);
    }

    @Override
    public WrapperKasu applyBinaryOp(DoubleBinaryOperator op, WrapperKasu b) {
        return new WrapperKasu(op.applyAsDouble(value, b.value), 0.0);
    }
    //-------------------------------------------------------------------------

    public WrapperKasu dadd(WrapperKasu b) {
        return addKah(this.value, this.dust, b.value, b.dust);
//        double sum=this.value;
//        double c=this.dust;
//        double y, t, input=der(b).value;
//        y=input-c; t=sum+y; c=(t-sum)-y; sum=t;
//        input=der(b).dust;
//        y=input-c; t=sum+y; c=(t-sum)-y; sum=t;
//        return new WrapperKasu(sum, c);
    }
    
    public WrapperKasu dsub(WrapperKasu b) {
        return addKah(this.value, this.dust, -b.value, -b.dust);
    }

    static WrapperKasu addKah(double a, double aC, double b, double bC) {
        // algo [cf wikipedia Kahan Summation] : 
        //      y=input-c; t=sum+y; c=(t-sum)-y; sum=t;
        double y, t;
        y=b -aC; t=a+y; aC=(t-a)-y; a=t;
        y=bC-aC; t=a+y; aC=(t-a)-y; a=t;
        return new WrapperKasu(a, aC); 
    }

    static WrapperKasu mulKah(double a, double aC, double b, double bC) {
        double y, t, sum=a*b, c=aC*bC;
        y=a*bC -c; t=sum+y; c=(t-sum)-y; sum=t;
        y=b*aC -c; t=sum+y; c=(t-sum)-y; sum=t;
        return new WrapperKasu(sum, c); 
    }

    public WrapperKasu dmul(WrapperKasu b) {
        return mulKah(this.value, this.dust, -b.value, -b.dust);
    }
//    
//    public ACojacWrapper ddiv(ACojacWrapper b) {
//        double d=this.dust*der(b).value - this.value*der(b).dust;
//        return new WrapperKasu(this.value/der(b).value, d);
//    }
//    
//    public ACojacWrapper drem(ACojacWrapper b) {
//        double d=this.dust;
//        if (der(b).dust != 0.0) // this seems hard to consider "general" dividers
//            d=Double.NaN;
//        return new WrapperKasu(this.value%der(b).value, d);
//    }
//
//    public ACojacWrapper math_sqrt() {
//        double value = Math.sqrt(this.value);
//        double dValue = this.dust / (2.0 * Math.sqrt(this.value));
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_abs(){
//        double value = Math.abs(this.value);
//        double dValue = this.value < 0.0 ? -this.dust : this.dust;
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_sin() {
//        double value = Math.sin(this.value);
//        double dValue = Math.cos(this.value) * this.dust;
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_cos() {
//        double value = Math.cos(this.value);
//        double dValue = -Math.sin(this.value) * this.dust;
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_tan() {
//        double value = Math.tan(this.value);
//        double dValue = this.dust / (Math.cos(this.value) * Math.cos(this.value));
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_asin() {
//        double value = Math.asin(this.value);
//        double dValue = this.dust / (Math.sqrt(1.0 - this.value * this.value));
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_acos() {
//        double value = Math.acos(this.value);
//        double dValue = -this.dust / (Math.sqrt(1.0 - this.value * this.value));
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_atan() {
//        double value = Math.atan(this.value);
//        double dValue = this.dust / (1.0 + this.value * this.value);
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_sinh() {
//        double value = Math.sinh(this.value);
//        double dValue = this.dust * Math.cosh(this.value);
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_cosh() {
//        double value = Math.cosh(this.value);
//        double dValue = this.dust * Math.sinh(this.value);
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_tanh() {
//        double value = Math.tanh(this.value);
//        double dValue = this.dust / (Math.cosh(this.value) * Math.cosh(this.value));
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_exp() {
//        double value = Math.exp(this.value);
//        double dValue = this.dust * Math.exp(this.value);
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_log() {
//        double value = Math.log(this.value);
//        double dValue = this.dust / this.value;
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_log10() {
//        double value = Math.log10(this.value);
//        double dValue = this.dust / (this.value * Math.log(10.0));
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_toRadians() {
//        double value = Math.toRadians(this.value);
//        double dValue = this.dust;
//        return new WrapperKasu(value, dValue); 
//    }
//    
//    public ACojacWrapper math_toDegrees() {
//        double value = Math.toDegrees(this.value);
//        double dValue = this.dust;
//        return new WrapperKasu(value, dValue); 
//    }
//
//    public ACojacWrapper math_min(ACojacWrapper b) {
//        return (this.compareTo(b)<=0) ? this : b;
//    }
//    
//    public ACojacWrapper math_max(ACojacWrapper b) {
//        return (this.compareTo(b)>=0) ? this : b;
//    }
//    
//    public ACojacWrapper math_pow(ACojacWrapper b) {
//        double value = Math.pow(this.value, der(b).value);
//        double dValue = Math.pow(this.value, der(b).value) *
//                (((der(b).value * this.dust) / this.value) + 
//                        Math.log(this.value) * der(b).dust);
//        return new WrapperKasu(value, dValue); 
//    }
    
    @Override public int compareTo(WrapperKasu o) {
        return Comparator.comparingDouble(((WrapperKasu b) -> b.value))
                .thenComparingDouble(((WrapperKasu b) -> b.dust))
                .compare(this, o);
    }
    
    @Override public double toDouble() {
        return value;
    }

    @SuppressWarnings("unused")
    @Override
    public WrapperKasu fromDouble(double a, boolean wasFromFloat) {
        return new WrapperKasu(a, 0.0);
    }

    @Override public String asInternalString() {
        return value+" (dust="+dust+")";
    }

    @Override public String wrapperName() {
        return "KaSu";
    }
    
}
