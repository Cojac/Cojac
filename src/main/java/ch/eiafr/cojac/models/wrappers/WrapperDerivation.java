package ch.eiafr.cojac.models.wrappers;

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

public class WrapperDerivation extends ACompactWrapper {
    private final double value;
    private final double deriv;

    private WrapperDerivation(double value, double dValue) {
        this.value = value;
        this.deriv = dValue;
    }
   
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperDerivation(ACojacWrapper w) {
        this(w==null ? 0.0 : ((WrapperDerivation) w).value, ((WrapperDerivation) w).deriv);
    }
    
    //-------------------------------------------------------------------------
    @Override
    public ACojacWrapper applyUnaryOp(DoubleUnaryOperator op) {
        return new WrapperDerivation(op.applyAsDouble(value), op.applyAsDouble(deriv));
    }

    @Override
    public ACojacWrapper applyBinaryOp(DoubleBinaryOperator op, ACojacWrapper b) {
        WrapperDerivation bb=(WrapperDerivation)b;
        return new WrapperDerivation(op.applyAsDouble(value, bb.value),
                                     op.applyAsDouble(deriv, bb.deriv));
    }
    
    public ACojacWrapper dadd(ACojacWrapper b) { return applyBinaryOp(((x,y)->x+y), b); }
    public ACojacWrapper dsub(ACojacWrapper b) { return applyBinaryOp(((x,y)->x-y), b); }
    public ACojacWrapper dmul(ACojacWrapper b) { return applyBinaryOp(((x,y)->x*y), b); }
    public ACojacWrapper ddiv(ACojacWrapper b) { return applyBinaryOp(((x,y)->x/y), b); }
    public ACojacWrapper drem(ACojacWrapper b) { return applyBinaryOp(((x,y)->x%y), b); }
    public ACojacWrapper dneg()                { return applyUnaryOp((x->-x)); }

    
    @Override public double toDouble() {
        return value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperDerivation(a, 0.0);
    }

    @Override public String asInternalString() {
        return value+" (deriv="+deriv+")";
    }

    @Override public String COJAC_MAGIC_wrapper() {
        return "Derivation";
    }
    
    public WrapperDerivation COJAC_MAGIC_DOUBLE_getDerivation() {
        return new WrapperDerivation(deriv, 0.0);
    }

    public WrapperDerivation COJAC_MAGIC_DOUBLE_asDerivationTarget() {
        return new WrapperDerivation(value, 1.0);
    }

    //-------------------------------------------------------------------------
}
