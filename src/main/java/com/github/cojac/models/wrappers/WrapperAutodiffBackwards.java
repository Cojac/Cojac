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

public class WrapperAutodiffBackwards extends ACojacWrapper {
    private final double value;
    private final WrapperAutodiffBackwards op1, op2; // @Nullable
    private final double dop1, dop2;

    private double adjoint;  // CAUTION: this Wrapper is thus NOT immutable
                             // (the consequences have not been analyzed...)

    private WrapperAutodiffBackwards(double value, 
                             WrapperAutodiffBackwards op1, WrapperAutodiffBackwards op2,
                             double dop1, double dop2) {
        this.value = value;
        this.adjoint = 0;
        this.op1=op1; this.dop1=dop1;
        this.op2=op2; this.dop2=dop2;
    }
    
    private WrapperAutodiffBackwards(double value, 
                             WrapperAutodiffBackwards op1,
                             double dop1) {
        this(value, op1, null, dop1, 0.0);
    }
   
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperAutodiffBackwards(ACojacWrapper w) {
        this(w==null ? 0.0 : der(w).value, 
             w==null ? null : der(w).op1,
             w==null ? null : der(w).op2,
             w==null ? 0.0 : der(w).dop1,
             w==null ? 0.0 : der(w).dop2
            );
    }
    
    //-------------------------------------------------------------------------
    private WrapperAutodiffBackwards unaryBuild(DoubleUnaryOperator op, double dop1) {
        return new WrapperAutodiffBackwards(op.applyAsDouble(value), this, dop1);
    }
    private WrapperAutodiffBackwards binaryBuild(DoubleBinaryOperator op, double dop1, 
                                         ACojacWrapper op2, double dop2) {
        return new WrapperAutodiffBackwards(op.applyAsDouble(value, der(op2).value), 
                                    this, der(op2), dop1, dop2);
    }
    private WrapperAutodiffBackwards unaryBuild(DoubleUnaryOperator op, DoubleUnaryOperator dop1) {
        return new WrapperAutodiffBackwards(op.applyAsDouble(value), this, dop1.applyAsDouble(value) );
    }
    private WrapperAutodiffBackwards binaryBuild(DoubleBinaryOperator op, DoubleBinaryOperator dop1, 
                                        ACojacWrapper op2, DoubleBinaryOperator dop2) {
        return new WrapperAutodiffBackwards(op.applyAsDouble(value, der(op2).value), 
                this, der(op2), 
                dop1.applyAsDouble(value, der(op2).value), 
                dop2.applyAsDouble(value, der(op2).value) );
    }
    
    //-------------------------------------------------------------------------

    public ACojacWrapper dadd(ACojacWrapper b) { return binaryBuild(((x,y)->x+y), 1, b, 1); }
    public ACojacWrapper dsub(ACojacWrapper b) { return binaryBuild(((x,y)->x-y), 1, b,-1); }
    public ACojacWrapper dmul(ACojacWrapper b) { return binaryBuild(((x,y)->x*y), der(b).value, b, value); }
    public ACojacWrapper ddiv(ACojacWrapper b) { return binaryBuild(((x,y)->x/y), 1/der(b).value, b, value); }
    public ACojacWrapper drem(ACojacWrapper b) { return binaryBuild(((x,y)->x%y), 1, b, Double.NaN); }
    public ACojacWrapper dneg()                { return unaryBuild((x->-x), -1); }
    
    public ACojacWrapper math_sqrt() { return unaryBuild(Math::sqrt, x -> 0.5/Math.sqrt(x) ); }
    
    public ACojacWrapper math_abs() { return unaryBuild(Math::sqrt, Math::signum ); }
    
    public ACojacWrapper math_sin() { return unaryBuild(Math::sin, Math::cos ); }
    public ACojacWrapper math_cos() { return unaryBuild(Math::cos, x -> -Math.sin(x) ); }
    public ACojacWrapper math_tan() { return unaryBuild(Math::tan, x -> 1/(Math.cos(x)*Math.cos(x)) ); }
    public ACojacWrapper math_asin() { return unaryBuild(Math::asin, x ->  1/(Math.sqrt(1.0 - x*x)) ); }
    public ACojacWrapper math_acos() { return unaryBuild(Math::acos, x -> -1/(Math.sqrt(1.0 - x*x)) ); }
    public ACojacWrapper math_atan() { return unaryBuild(Math::atan, x -> 1/(1.0 + x*x) ); }
    public ACojacWrapper math_sinh() { return unaryBuild(Math::sinh, Math::cosh ); }
    public ACojacWrapper math_cosh() { return unaryBuild(Math::cosh, Math::sinh ); }
    public ACojacWrapper math_tanh() { return unaryBuild(Math::tanh, x -> 1/(Math.cosh(x)*Math.cosh(x)) ); }
    public ACojacWrapper math_exp() { return unaryBuild(Math::exp, Math::exp); }
    public ACojacWrapper math_log() { return unaryBuild(Math::log, x -> 1/x ); }
    public ACojacWrapper math_log10() { return unaryBuild(Math::log10, x -> 1/(x*Math.log(10.0)) ); }
    public ACojacWrapper math_toRadians() { return unaryBuild(Math::toRadians, x -> Math.toRadians(1) ); }
    public ACojacWrapper math_toDegrees() { return unaryBuild(Math::toDegrees, x -> Math.toDegrees(1) ); }
    
    public ACojacWrapper math_min(ACojacWrapper b) { return binaryBuild(Math::min, (x,y) -> (x<y)?1:0, b, (x,y) -> (x<y)?0:1 ); }
    
    public ACojacWrapper math_max(ACojacWrapper b) { return binaryBuild(Math::max, (x,y) -> (x>y)?1:0, b, (x,y) -> (x>y)?0:1 ); } 
    
    public ACojacWrapper math_pow(ACojacWrapper b) { return binaryBuild(Math::pow, (x,y) -> y*Math.pow(x, y-1), b, (x,y) -> Math.log(x)*Math.pow(x,y) ); }
    
    @Override public double toDouble() {
        return value;
    }

    @SuppressWarnings("unused")
    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperAutodiffBackwards(a, null, 0.0);
    }

    @Override public String asInternalString() {
        return value+" (adjoint="+adjoint+")";
    }

    @Override public String wrapperName() {
        return "ReverseAD";
    }
    
    private void backPropagate(double delta) {
        this.adjoint += delta;
        bp(op1, delta*dop1);
        bp(op2, delta*dop2);
    }

    private void backReset() {
        this.adjoint = 0;
        br(op1);
        br(op2);
    }
    
    private static void bp(WrapperAutodiffBackwards op, double delta) {
        if(op==null || delta==0.0) return;
        op.backPropagate(delta);
    }
    
    private static void br(WrapperAutodiffBackwards op) {
        if(op==null) return;
        op.backReset();
    }

    // ------------------------------------------------------------------------
    public static CommonDouble COJAC_MAGIC_partialDerivativeIn(CommonDouble d) {
        WrapperAutodiffBackwards res=new WrapperAutodiffBackwards(der(d.val).adjoint, null, 0);
        return new CommonDouble(res);
    }
    
    public static CommonFloat COJAC_MAGIC_partialDerivativeIn(CommonFloat d) {
        WrapperAutodiffBackwards res=new WrapperAutodiffBackwards(der(d.val).adjoint,null, 0);
        return new CommonFloat(res);
    }
    
    public static void COJAC_MAGIC_computePartialDerivatives(CommonDouble d) {
        WrapperAutodiffBackwards w=der(d.val);
        w.backPropagate(1.0);
    }
    
    public static void COJAC_MAGIC_computePartialDerivatives(CommonFloat d) {
        WrapperAutodiffBackwards w=der(d.val);
        w.backPropagate(1.0);
    }

    public static void COJAC_MAGIC_resetPartialDerivatives(CommonDouble d) {
        WrapperAutodiffBackwards w=der(d.val);
        w.backReset();
    }
    
    public static void COJAC_MAGIC_resetPartialDerivatives(CommonFloat d) {
        WrapperAutodiffBackwards w=der(d.val);
        w.backReset();
    }
    //-------------------------------------------------------------------------
    private static WrapperAutodiffBackwards der(ACojacWrapper w) {
        return (WrapperAutodiffBackwards)w;
    }
}
