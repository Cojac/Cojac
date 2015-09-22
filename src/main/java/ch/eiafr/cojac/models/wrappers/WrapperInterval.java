package ch.eiafr.cojac.models.wrappers;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class WrapperInterval extends ACompactWrapper {
    private final double value;
   
    private WrapperInterval(double v) {
        this.value=v;
    }
    
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperInterval(ACojacWrapper w) {
        this(w==null ? 0.0 : ((WrapperInterval) w).value);
    }
    
    //-------------------------------------------------------------------------
    @Override
    public ACojacWrapper applyUnaryOp(DoubleUnaryOperator op) {
        return new WrapperInterval(op.applyAsDouble(value));
    }

    @Override
    public ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper b) {
        WrapperInterval bb=(WrapperInterval)b;
        return new WrapperInterval(op.applyAsDouble(value, bb.value));
    }
    
    @Override public double toDouble() {
        return value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperInterval(a);
    }

    @Override public String asInternalString() {
        return Double.toString(value);
    }

    @Override public String COJAC_MAGIC_wrapper() {
        return "Basic";
    }
    
    //-------------------------------------------------------------------------
}
