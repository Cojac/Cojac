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

public class WrapperBigDecimalWithNaN extends ACompactWrapper<WrapperBigDecimalWithNaN> {
    //=======================================================
    static enum NumberKind {
        NORMAL(0), 
        NAN(Double.NaN), 
        POS_INF(Double.POSITIVE_INFINITY), 
        NEG_INF(Double.NEGATIVE_INFINITY);
        final double asDouble;
        NumberKind(double v) { asDouble=v; }
        static NumberKind fromDouble(double d) {
            if (Double.isNaN(d)) return NAN;
            if (d==Double.POSITIVE_INFINITY) return POS_INF;
            if (d==Double.NEGATIVE_INFINITY) return NEG_INF;
            return NORMAL;
        }
    }
    //=======================================================
    private static MathContext mathContext = null;

    private final BigDecimal value;
    private final NumberKind kind;
    
    private WrapperBigDecimalWithNaN(double v) {
        this(new BigDecimal(asNormal(v), mathContext), NumberKind.fromDouble(v));
    }
    
    private WrapperBigDecimalWithNaN(BigDecimal v, NumberKind k) {
        if (mathContext==null)
            mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        value=v;
        kind=k;
    }
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperBigDecimalWithNaN(WrapperBigDecimalWithNaN w) {
        this(w==null ? BigDecimal.ZERO : c(w).value,
             w==null ? NumberKind.NORMAL : c(w).kind);
    }
    
    //-------------------------------------------------------------------------
    @Override
    public WrapperBigDecimalWithNaN applyUnaryOp(DoubleUnaryOperator op) {
        return new WrapperBigDecimalWithNaN(op.applyAsDouble(value.doubleValue()));
    }

    @Override
    public WrapperBigDecimalWithNaN applyBinaryOp(DoubleBinaryOperator op, WrapperBigDecimalWithNaN b) {
        return new WrapperBigDecimalWithNaN(op.applyAsDouble(value.doubleValue(), b.value.doubleValue()));
    }

    @Override public WrapperBigDecimalWithNaN dadd(WrapperBigDecimalWithNaN b) {
        if(kind!=NumberKind.NORMAL)
            return super.dadd(b); // so via applyOp()
        return new WrapperBigDecimalWithNaN(value.add(big(b), mathContext), NumberKind.NORMAL); 
    }
    
    @Override public WrapperBigDecimalWithNaN dsub(WrapperBigDecimalWithNaN b) {
        if(kind!=NumberKind.NORMAL)
            return super.dsub(b); // so via applyOp()
        return new WrapperBigDecimalWithNaN(value.subtract(big(b), mathContext), NumberKind.NORMAL);
    }
    
    @Override public WrapperBigDecimalWithNaN dmul(WrapperBigDecimalWithNaN b) {
        if(kind!=NumberKind.NORMAL)
            return super.dmul(b); // so via applyOp()
        return new WrapperBigDecimalWithNaN(value.multiply(big(b), mathContext), NumberKind.NORMAL); 
    }
    
    @Override public WrapperBigDecimalWithNaN ddiv(WrapperBigDecimalWithNaN b) {
        if (kind!=NumberKind.NORMAL || c(b).kind !=NumberKind.NORMAL 
                || big(b).equals(BigDecimal.ZERO))
            return super.ddiv(b); // so via applyOp()
        return new WrapperBigDecimalWithNaN(value.divide(big(b), mathContext), NumberKind.NORMAL);
    }
    
    @Override public WrapperBigDecimalWithNaN dneg()                {
        if(kind!=NumberKind.NORMAL)
            return super.dneg(); // so via applyOp()
        return new WrapperBigDecimalWithNaN(value.negate(mathContext), NumberKind.NORMAL);
    }
    
    @Override public WrapperBigDecimalWithNaN drem(WrapperBigDecimalWithNaN b) {
        if (kind!=NumberKind.NORMAL || c(b).kind !=NumberKind.NORMAL 
                || big(b).equals(BigDecimal.ZERO))
            return super.drem(b); // so via applyOp()
        BigDecimal rem=value.remainder(big(b), mathContext);
        if(this.value.compareTo(BigDecimal.ZERO)<0) rem=rem.negate();
        return new WrapperBigDecimalWithNaN(rem, NumberKind.NORMAL); 
    }
    
    public WrapperBigDecimalWithNaN math_sqrt() {
        if (kind!=NumberKind.NORMAL || value.compareTo(BigDecimal.ZERO)<0)
            return applyUnaryOp(Math::sqrt);
        return new WrapperBigDecimalWithNaN(sqrtHeron(value), NumberKind.NORMAL);
    }
    
    // TODO : make better functions for all math function (sin/cos/log/...) !
    //        A library has been tried (24.08.15), but it was not really convincing; 
    //        maybe look deeper inside (http://arxiv.org/src/0908.3030v2/anc)...
    
    @Override public double toDouble() {
        if (kind!=NumberKind.NORMAL)
            return kind.asDouble;
        return value.doubleValue();
    }

    @SuppressWarnings("unused")
    @Override
    public WrapperBigDecimalWithNaN fromDouble(double a, boolean wasFromFloat) {
        return new WrapperBigDecimalWithNaN(a);
    }

    @Override public String asInternalString() {
        if (kind!=NumberKind.NORMAL)
            return Double.toString(kind.asDouble);
        return value.toString();
    }

    @Override public String wrapperName() {
        return "BigDecimalWithNaN";
    }
    
    @Override public int compareTo(WrapperBigDecimalWithNaN w) {
        if (kind != NumberKind.NORMAL || c(w).kind != NumberKind.NORMAL)
            return Double.compare(kind.asDouble, c(w).kind.asDouble);
        return value.compareTo(c(w).value);
    }

    //-------------------------------------------------------------------------
    private static BigDecimal big(ACojacWrapper a) {
        return c(a).value;
    }

    private static WrapperBigDecimalWithNaN c(ACojacWrapper a) {
        return (WrapperBigDecimalWithNaN) a;
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

    private static double asNormal(double v) {
        if (NumberKind.fromDouble(v) == NumberKind.NORMAL) return v;
        return 0.0;
    }
}
