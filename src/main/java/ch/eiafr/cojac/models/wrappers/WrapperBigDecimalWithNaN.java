package ch.eiafr.cojac.models.wrappers;

import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_BIGDECIMAL_PRECISION;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class WrapperBigDecimalWithNaN extends ACompactWrapper {
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
    };
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
    public WrapperBigDecimalWithNaN(ACojacWrapper w) {
        this(w==null ? BigDecimal.ZERO : c(w).value,
             w==null ? NumberKind.NORMAL : c(w).kind);
    }
    
    //-------------------------------------------------------------------------
    @Override
    public ACojacWrapper applyUnaryOp(DoubleUnaryOperator op) {
        return new WrapperBigDecimalWithNaN(op.applyAsDouble(value.doubleValue()));
    }

    @Override
    public ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper b) {
        WrapperBigDecimalWithNaN bb=(WrapperBigDecimalWithNaN)b;
        return new WrapperBigDecimalWithNaN(op.applyAsDouble(value.doubleValue(), bb.value.doubleValue()));
    }

    @Override public ACojacWrapper dadd(ACojacWrapper b) {
        if(kind!=NumberKind.NORMAL)
            return super.dadd(b); // so via applyOp()
        return new WrapperBigDecimalWithNaN(value.add(big(b), mathContext), NumberKind.NORMAL); 
    }
    
    @Override public ACojacWrapper dsub(ACojacWrapper b) { 
        if(kind!=NumberKind.NORMAL)
            return super.dsub(b); // so via applyOp()
        return new WrapperBigDecimalWithNaN(value.subtract(big(b), mathContext), NumberKind.NORMAL);
    }
    
    @Override public ACojacWrapper dmul(ACojacWrapper b) { 
        if(kind!=NumberKind.NORMAL)
            return super.dmul(b); // so via applyOp()
        return new WrapperBigDecimalWithNaN(value.multiply(big(b), mathContext), NumberKind.NORMAL); 
    }
    
    @Override public ACojacWrapper ddiv(ACojacWrapper b) { 
        if (kind!=NumberKind.NORMAL || c(b).kind !=NumberKind.NORMAL 
                || big(b).equals(BigDecimal.ZERO))
            return super.ddiv(b); // so via applyOp()
        return new WrapperBigDecimalWithNaN(value.divide(big(b), mathContext), NumberKind.NORMAL);
    }
    
    @Override public ACojacWrapper dneg()                {
        if(kind!=NumberKind.NORMAL)
            return super.dneg(); // so via applyOp()
        return new WrapperBigDecimalWithNaN(value.negate(mathContext), NumberKind.NORMAL);
    }
    
    @Override public ACojacWrapper drem(ACojacWrapper b) {
        if (kind!=NumberKind.NORMAL || c(b).kind !=NumberKind.NORMAL 
                || big(b).equals(BigDecimal.ZERO))
            return super.drem(b); // so via applyOp()
        BigDecimal rem=value.remainder(big(b), mathContext);
        if(this.value.compareTo(BigDecimal.ZERO)<0) rem=rem.negate();
        return new WrapperBigDecimalWithNaN(rem, NumberKind.NORMAL); 
    }
    
    public ACojacWrapper math_sqrt() {
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

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
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
    
    @Override public int compareTo(ACojacWrapper w) {
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
        while (root.subtract(lroot, mathContext).abs(mathContext).compareTo(epsilon) == 1) {
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
