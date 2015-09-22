package ch.eiafr.cojac.models.wrappers;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class WrapperBasic extends ACompactWrapper {
    private final double value;
   
    private WrapperBasic(double v) {
        this.value=v;
    }
    
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperBasic(ACojacWrapper w) {
        this(w==null ? 0.0 : ((WrapperBasic) w).value);
    }
    
    //-------------------------------------------------------------------------
    @Override
    public ACojacWrapper applyUnaryOp(DoubleUnaryOperator op) {
        return new WrapperBasic(op.applyAsDouble(value));
    }

    @Override
    public ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper b) {
        WrapperBasic bb=(WrapperBasic)b;
        return new WrapperBasic(op.applyAsDouble(value, bb.value));
    }
    
    @Override public double toDouble() {
        return value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperBasic(a);
    }

    @Override public String asInternalString() {
        return Double.toString(value);
    }

    @Override public String COJAC_MAGIC_wrapper() {
        return "Basic";
    }
    
    //-------------------------------------------------------------------------
}
