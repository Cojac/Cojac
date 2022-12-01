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

import static com.github.cojac.models.FloatReplacerClasses.COJAC_BIGDECIMAL_PRECISION;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

//TODO: decide whether we want a field enum {NORMAL, NAN, POS_INF, NEG_INF}
public class WrapperBigDecimal extends ACompactWrapper<WrapperBigDecimal> {
    private static MathContext mathContext = null;

//    static {
//        System.out.println("yeah "+(java.util.function.DoubleUnaryOperator)Math::sqrt);
//        WrapperBigDecimal a=new WrapperBigDecimal(16.0);
//        System.out.println("ohla: "+a.asInternalString());
//        ACojacWrapper b=a.math_sqrt();
//        System.out.println("ohlb: "+b.asInternalString());
//    }
    private final BigDecimal value;
   
    private WrapperBigDecimal(double v) {
        this(new BigDecimal(Double.toString(v), mathContext));
        // this(new BigDecimal(v, mathContext));
    }
    
    private WrapperBigDecimal(BigDecimal v) {
        if (mathContext==null)
            mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        value=v;
    }
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperBigDecimal(WrapperBigDecimal w) {
        this(w==null ? BigDecimal.ZERO : w.value);
    }
    
    //-------------------------------------------------------------------------
    @Override
    public WrapperBigDecimal applyUnaryOp(DoubleUnaryOperator op) {
        return new WrapperBigDecimal(op.applyAsDouble(value.doubleValue()));
    }

    @Override
    public WrapperBigDecimal applyBinaryOp(DoubleBinaryOperator op, WrapperBigDecimal b) {
        return new WrapperBigDecimal(op.applyAsDouble(value.doubleValue(), b.value.doubleValue()));
    }

    @Override public WrapperBigDecimal dadd(WrapperBigDecimal b) {
        return new WrapperBigDecimal(value.add(big(b), mathContext));
    }
    
    @Override public WrapperBigDecimal dsub(WrapperBigDecimal b) {
        return new WrapperBigDecimal(value.subtract(big(b), mathContext));
    }
    
    @Override public WrapperBigDecimal dmul(WrapperBigDecimal b) {
        return new WrapperBigDecimal(value.multiply(big(b), mathContext)); 
    }
    
    @Override public WrapperBigDecimal ddiv(WrapperBigDecimal b) {
        if (big(b).equals(BigDecimal.ZERO))
            return new WrapperBigDecimal(BigDecimal.ZERO); // maybe reconsider...
        return new WrapperBigDecimal(value.divide(big(b), mathContext));
    }
    
    @Override public WrapperBigDecimal dneg()                {
        return new WrapperBigDecimal(value.negate(mathContext));
    }
    
    @Override public WrapperBigDecimal drem(WrapperBigDecimal b) {
        BigDecimal rem=value.remainder(big(b), mathContext);
        if(this.value.compareTo(BigDecimal.ZERO)<0) rem=rem.negate();
        return new WrapperBigDecimal(rem); 
    }
    
    public WrapperBigDecimal math_sqrt() {
        return new WrapperBigDecimal(sqrtHeron(value));
    }

    // TODO : make better functions for all math function (sin/cos/log/...) !
    //        A library has been tried (24.08.15), but it was not really convincing; 
    //        maybe look deeper inside (http://arxiv.org/src/0908.3030v2/anc)...
    
    @Override public double toDouble() {
        return value.doubleValue();
    }

    @SuppressWarnings("unused")
    @Override
    public WrapperBigDecimal fromDouble(double a, boolean wasFromFloat) {
        return new WrapperBigDecimal(a);
    }

    @Override public String asInternalString() {
        return value.toString();
    }

    @Override public String wrapperName() {
        return "BigDecimal";
    }
    
    //-------------------------------------------------------------------------
    private static BigDecimal big(WrapperBigDecimal a) {
        return a.value;
    }

    private static BigDecimal sqrtHeron(BigDecimal x) {
        BigDecimal epsilon = new BigDecimal(10.0).pow(-COJAC_BIGDECIMAL_PRECISION, mathContext); // precision
        BigDecimal root = new BigDecimal(1.0, mathContext);
        BigDecimal lroot = x.abs(mathContext);
        while (root.subtract(lroot, mathContext).abs(mathContext).compareTo(epsilon) > 0) {
            lroot = root.abs(mathContext);
            root = root.add(x.divide(root, mathContext)).divide(new BigDecimal(2.0, mathContext), mathContext);
        }
        return root;
    }


}
