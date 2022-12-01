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

public class WrapperAutodiffReverse extends ACojacWrapper<WrapperAutodiffReverse> {
    private final double value;
    private final WrapperAutodiffReverse op1, op2; // @Nullable
    private final double dop1, dop2;

    private double adjoint;  // CAUTION: this Wrapper is thus NOT immutable
                             // (the consequences have not been analyzed...)

    private WrapperAutodiffReverse(double value, 
                             WrapperAutodiffReverse op1, WrapperAutodiffReverse op2,
                             double dop1, double dop2) {
        this.value = value;
        this.adjoint = 0;
        this.op1=op1; this.dop1=dop1;
        this.op2=op2; this.dop2=dop2;
    }
    
    private WrapperAutodiffReverse(double value, 
                             WrapperAutodiffReverse op1,
                             double dop1) {
        this(value, op1, null, dop1, 0.0);
    }
   
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperAutodiffReverse(WrapperAutodiffReverse w) {
        this(w==null ? 0.0 : w.value,
             w==null ? null : w.op1,
             w==null ? null : w.op2,
             w==null ? 0.0 : w.dop1,
             w==null ? 0.0 : w.dop2
            );
    }
    
    //-------------------------------------------------------------------------
    private WrapperAutodiffReverse unaryBuild(DoubleUnaryOperator op, double dop1) {
        return new WrapperAutodiffReverse(op.applyAsDouble(value), this, dop1);
    }
    private WrapperAutodiffReverse binaryBuild(DoubleBinaryOperator op, double dop1,
                                               WrapperAutodiffReverse op2, double dop2) {
        return new WrapperAutodiffReverse(op.applyAsDouble(value, op2.value),
                                    this, op2, dop1, dop2);
    }
    private WrapperAutodiffReverse unaryBuild(DoubleUnaryOperator op, DoubleUnaryOperator dop1) {
        return new WrapperAutodiffReverse(op.applyAsDouble(value), this, dop1.applyAsDouble(value) );
    }
    private WrapperAutodiffReverse binaryBuild(DoubleBinaryOperator op, DoubleBinaryOperator dop1,
                                               WrapperAutodiffReverse op2, DoubleBinaryOperator dop2) {
        return new WrapperAutodiffReverse(op.applyAsDouble(value, op2.value),
                this, op2,
                dop1.applyAsDouble(value, op2.value),
                dop2.applyAsDouble(value, op2.value) );
    }
    
    //-------------------------------------------------------------------------

    public WrapperAutodiffReverse dadd(WrapperAutodiffReverse b) { return binaryBuild(((x,y)->x+y), 1, b, 1); }
    public WrapperAutodiffReverse dsub(WrapperAutodiffReverse b) { return binaryBuild(((x,y)->x-y), 1, b,-1); }
    public WrapperAutodiffReverse dmul(WrapperAutodiffReverse b) { return binaryBuild(((x,y)->x*y), b.value, b, value); }
    public WrapperAutodiffReverse ddiv(WrapperAutodiffReverse b) { return binaryBuild(((x,y)->x/y), 1/b.value, b, value); }
    public WrapperAutodiffReverse drem(WrapperAutodiffReverse b) { return binaryBuild(((x,y)->x%y), 1, b, Double.NaN); }
    public WrapperAutodiffReverse dneg()                { return unaryBuild((x->-x), -1); }
    
    public WrapperAutodiffReverse math_sqrt() { return unaryBuild(Math::sqrt, x -> 0.5/Math.sqrt(x) ); }
    
    public WrapperAutodiffReverse math_abs() { return unaryBuild(Math::sqrt, Math::signum ); }
    
    public WrapperAutodiffReverse math_sin() { return unaryBuild(Math::sin, Math::cos ); }
    public WrapperAutodiffReverse math_cos() { return unaryBuild(Math::cos, x -> -Math.sin(x) ); }
    public WrapperAutodiffReverse math_tan() { return unaryBuild(Math::tan, x -> 1/(Math.cos(x)*Math.cos(x)) ); }
    public WrapperAutodiffReverse math_asin() { return unaryBuild(Math::asin, x ->  1/(Math.sqrt(1.0 - x*x)) ); }
    public WrapperAutodiffReverse math_acos() { return unaryBuild(Math::acos, x -> -1/(Math.sqrt(1.0 - x*x)) ); }
    public WrapperAutodiffReverse math_atan() { return unaryBuild(Math::atan, x -> 1/(1.0 + x*x) ); }
    public WrapperAutodiffReverse math_sinh() { return unaryBuild(Math::sinh, Math::cosh ); }
    public WrapperAutodiffReverse math_cosh() { return unaryBuild(Math::cosh, Math::sinh ); }
    public WrapperAutodiffReverse math_tanh() { return unaryBuild(Math::tanh, x -> 1/(Math.cosh(x)*Math.cosh(x)) ); }
    public WrapperAutodiffReverse math_exp() { return unaryBuild(Math::exp, Math::exp); }
    public WrapperAutodiffReverse math_log() { return unaryBuild(Math::log, x -> 1/x ); }
    public WrapperAutodiffReverse math_log10() { return unaryBuild(Math::log10, x -> 1/(x*Math.log(10.0)) ); }
    public WrapperAutodiffReverse math_toRadians() { return unaryBuild(Math::toRadians, x -> Math.toRadians(1) ); }
    public WrapperAutodiffReverse math_toDegrees() { return unaryBuild(Math::toDegrees, x -> Math.toDegrees(1) ); }
    
    public WrapperAutodiffReverse math_min(WrapperAutodiffReverse b) { return binaryBuild(Math::min, (x,y) -> (x<y)?1:0, b, (x,y) -> (x<y)?0:1 ); }
    
    public WrapperAutodiffReverse math_max(WrapperAutodiffReverse b) { return binaryBuild(Math::max, (x,y) -> (x>y)?1:0, b, (x,y) -> (x>y)?0:1 ); }
    
    public WrapperAutodiffReverse math_pow(WrapperAutodiffReverse b) { return binaryBuild(Math::pow, (x,y) -> y*Math.pow(x, y-1), b, (x,y) -> Math.log(x)*Math.pow(x,y) ); }
    
    @Override public double toDouble() {
        return value;
    }

    @SuppressWarnings("unused")
    @Override
    public WrapperAutodiffReverse fromDouble(double a, boolean wasFromFloat) {
        return new WrapperAutodiffReverse(a, null, 0.0);
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
    
    private static void bp(WrapperAutodiffReverse op, double delta) {
        if(op==null || delta==0.0) return;
        op.backPropagate(delta);
    }
    
    private static void br(WrapperAutodiffReverse op) {
        if(op==null) return;
        op.backReset();
    }

    // ------------------------------------------------------------------------
    public static CommonDouble<WrapperAutodiffReverse> COJAC_MAGIC_partialDerivativeIn(CommonDouble<WrapperAutodiffReverse> d) {
        WrapperAutodiffReverse res=new WrapperAutodiffReverse(d.val.adjoint, null, 0);
        return new CommonDouble<>(res);
    }
    
    public static CommonFloat<WrapperAutodiffReverse> COJAC_MAGIC_partialDerivativeIn(CommonFloat<WrapperAutodiffReverse> d) {
        WrapperAutodiffReverse res=new WrapperAutodiffReverse(d.val.adjoint,null, 0);
        return new CommonFloat<>(res);
    }
    
    public static void COJAC_MAGIC_computePartialDerivatives(CommonDouble<WrapperAutodiffReverse> d) {
        WrapperAutodiffReverse w=d.val;
        w.backPropagate(1.0);
    }
    
    public static void COJAC_MAGIC_computePartialDerivatives(CommonFloat<WrapperAutodiffReverse> d) {
        WrapperAutodiffReverse w=d.val;
        w.backPropagate(1.0);
    }

    public static void COJAC_MAGIC_resetPartialDerivatives(CommonDouble<WrapperAutodiffReverse> d) {
        WrapperAutodiffReverse w=d.val;
        w.backReset();
    }
    
    public static void COJAC_MAGIC_resetPartialDerivatives(CommonFloat<WrapperAutodiffReverse> d) {
        WrapperAutodiffReverse w=d.val;
        w.backReset();
    }
}
