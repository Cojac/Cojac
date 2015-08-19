package ch.eiafr.cojac.models.wrappers;

import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_BIGDECIMAL_PRECISION;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class WrapperBigDecimal extends ACompactWrapper {
    private static MathContext mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);

    static {
//        System.out.println("yeah "+(java.util.function.DoubleUnaryOperator)Math::sqrt);
//        WrapperBigDecimal a=new WrapperBigDecimal(16.0);
//        System.out.println("ohla: "+a.asInternalString());
//        ACojacWrapper b=a.math_sqrt();
//        System.out.println("ohlb: "+b.asInternalString());
    }
    private final BigDecimal value;
   
    private WrapperBigDecimal(double v) {
        this(new BigDecimal(v, mathContext));
    }
    private WrapperBigDecimal(BigDecimal v) {
        value=v;
    }
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperBigDecimal(ACojacWrapper w) {
        this(w==null ? BigDecimal.ZERO : ((WrapperBigDecimal) w).value);
    }
    
    //-------------------------------------------------------------------------
    @Override
    public ACojacWrapper applyUnaryOp(DoubleUnaryOperator op) {
        return new WrapperBigDecimal(op.applyAsDouble(value.doubleValue()));
    }

    @Override
    public ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper b) {
        WrapperBigDecimal bb=(WrapperBigDecimal)b;
        return new WrapperBigDecimal(op.applyAsDouble(value.doubleValue(), bb.value.doubleValue()));
    }

    public ACojacWrapper dadd(ACojacWrapper b) { return new WrapperBigDecimal(value.add(big(b), mathContext)); }
    public ACojacWrapper dsub(ACojacWrapper b) { return new WrapperBigDecimal(value.subtract(big(b), mathContext)); }
    public ACojacWrapper dmul(ACojacWrapper b) { return new WrapperBigDecimal(value.multiply(big(b), mathContext)); }
    public ACojacWrapper ddiv(ACojacWrapper b) { return new WrapperBigDecimal(value.divide(big(b), mathContext)); }
    public ACojacWrapper drem(ACojacWrapper b) { return new WrapperBigDecimal(value.remainder(big(b), mathContext)); }
    public ACojacWrapper dneg()                { return new WrapperBigDecimal(value.negate(mathContext)); }
    
    @Override
    public double toDouble() {
        return value.doubleValue();
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperBigDecimal(a);
    }

    @Override
    public String asInternalString() {
        return value.toString();
    }

    @Override
    public String COJAC_MAGIC_wrapper() {
        return "BigDecimal";
    }
    
    //-------------------------------------------------------------------------
    private BigDecimal big(ACojacWrapper a) {
        return ((WrapperBigDecimal) a).value;
    }


}
