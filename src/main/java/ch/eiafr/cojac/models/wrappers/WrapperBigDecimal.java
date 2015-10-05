package ch.eiafr.cojac.models.wrappers;

import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_BIGDECIMAL_PRECISION;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

//TODO: decide whether we want a field enum {NORMAL, NAN, POS_INF, NEG_INF}
public class WrapperBigDecimal extends ACompactWrapper {
    private static MathContext mathContext = null;

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
        if (mathContext==null)
            mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
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

    public ACojacWrapper math_sqrt() {
        return new WrapperBigDecimal(sqrtHeron(value));
    }
    
    @Override public ACojacWrapper dadd(ACojacWrapper b) {
        return new WrapperBigDecimal(value.add(big(b), mathContext)); 
    }
    
    @Override public ACojacWrapper dsub(ACojacWrapper b) { 
        return new WrapperBigDecimal(value.subtract(big(b), mathContext));
    }
    
    @Override public ACojacWrapper dmul(ACojacWrapper b) { 
        return new WrapperBigDecimal(value.multiply(big(b), mathContext)); 
    }
    
    @Override public ACojacWrapper ddiv(ACojacWrapper b) { 
        if (big(b).equals(BigDecimal.ZERO))
            return new WrapperBigDecimal(BigDecimal.ZERO); // maybe reconsider...
        return new WrapperBigDecimal(value.divide(big(b), mathContext));
    }
    
    @Override public ACojacWrapper dneg()                {
        return new WrapperBigDecimal(value.negate(mathContext));
    }
    
    @Override public ACojacWrapper drem(ACojacWrapper b) {
        BigDecimal rem=value.remainder(big(b), mathContext);
        if(this.value.compareTo(BigDecimal.ZERO)<0) rem=rem.negate();
        return new WrapperBigDecimal(rem); 
    }
    
    @Override public double toDouble() {
        return value.doubleValue();
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperBigDecimal(a);
    }

    @Override public String asInternalString() {
        return value.toString();
    }

    @Override public String COJAC_MAGIC_wrapper() {
        return "BigDecimal";
    }
    
    //-------------------------------------------------------------------------
    private static BigDecimal big(ACojacWrapper a) {
        return ((WrapperBigDecimal) a).value;
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


}
