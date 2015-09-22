package ch.eiafr.cojac.models.wrappers;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class WrapperStochastic extends ACompactWrapper {
    private final double value;
   
    private WrapperStochastic(double v) {
        this.value=v;
    }
    
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperStochastic(ACojacWrapper w) {
        this(w==null ? 0.0 : ((WrapperStochastic) w).value);
    }
    
    //-------------------------------------------------------------------------
    @Override
    public ACojacWrapper applyUnaryOp(DoubleUnaryOperator op) {
        return new WrapperStochastic(op.applyAsDouble(value));
    }

    @Override
    public ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper b) {
        WrapperStochastic bb=(WrapperStochastic)b;
        return new WrapperStochastic(op.applyAsDouble(value, bb.value));
    }
    
    @Override public double toDouble() {
        return value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperStochastic(a);
    }

    @Override public String asInternalString() {
        return Double.toString(value);
    }

    @Override public String COJAC_MAGIC_wrapper() {
        return "Basic";
    }
    
    //-------------------------------------------------------------------------
}
