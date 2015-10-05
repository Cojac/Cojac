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
    // Most of the operations do not follow those "operator" rules, 
    // and are thus fully redefined
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
    //-------------------------------------------------------------------------

    public ACojacWrapper dmul(ACojacWrapper b) {
        double d=this.value*der(b).deriv + this.deriv*der(b).value;
        return new WrapperDerivation(this.value*der(b).value, d);
    }
    
    public ACojacWrapper ddiv(ACojacWrapper b) {
        double d=this.deriv*der(b).value - this.value*der(b).deriv;
        return new WrapperDerivation(this.value/der(b).value, d);
    }
    
    public ACojacWrapper drem(ACojacWrapper b) {
        double d=this.deriv;
        if (der(b).deriv != 0.0) // this seems hard to consider "general" dividers
            d=Double.NaN;
        return new WrapperDerivation(this.value*der(b).value, d);
    }

    public ACojacWrapper math_sqrt() {
        double value = Math.sqrt(this.value);
        double dValue = this.deriv / (2.0 * Math.sqrt(this.value));
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_abs(){
        double value = Math.abs(this.value);
        double dValue = this.value < 0.0 ? -this.deriv : this.deriv;
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_sin() {
        double value = Math.sin(this.value);
        double dValue = Math.cos(this.value) * this.deriv;
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_cos() {
        double value = Math.cos(this.value);
        double dValue = -Math.sin(this.value) * this.deriv;
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_tan() {
        double value = Math.tan(this.value);
        double dValue = this.deriv / (Math.cos(this.value) * Math.cos(this.value));
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_asin() {
        double value = Math.asin(this.value);
        double dValue = this.deriv / (Math.sqrt(1.0 - this.value * this.value));
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_acos() {
        double value = Math.acos(this.value);
        double dValue = -this.deriv / (Math.sqrt(1.0 - this.value * this.value));
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_atan() {
        double value = Math.atan(this.value);
        double dValue = this.deriv / (1.0 + this.value * this.value);
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_sinh() {
        double value = Math.sinh(this.value);
        double dValue = this.deriv * Math.cosh(this.value);
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_cosh() {
        double value = Math.cosh(this.value);
        double dValue = this.deriv * Math.sinh(this.value);
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_tanh() {
        double value = Math.tanh(this.value);
        double dValue = this.deriv / (Math.cosh(this.value) * Math.cosh(this.value));
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_exp() {
        double value = Math.exp(this.value);
        double dValue = this.deriv * Math.exp(this.value);
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_log() {
        double value = Math.log(this.value);
        double dValue = this.deriv / this.value;
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_log10() {
        double value = Math.log10(this.value);
        double dValue = this.deriv / (this.value * Math.log(10.0));
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_toRadians() {
        double value = Math.toRadians(this.value);
        double dValue = this.deriv;
        return new WrapperDerivation(value, dValue); 
    }
    
    public ACojacWrapper math_toDegrees() {
        double value = Math.toDegrees(this.value);
        double dValue = this.deriv;
        return new WrapperDerivation(value, dValue); 
    }

    public ACojacWrapper math_min(ACojacWrapper b) {
        return (this.value < der(b).value) ? this : b;
    }
    
    public ACojacWrapper math_max(ACojacWrapper b) {
        return (this.value > der(b).value) ? this : b;
    }
    
    public ACojacWrapper math_pow(ACojacWrapper b) {
        double value = Math.pow(this.value, der(b).value);
        double dValue = Math.pow(this.value, der(b).value) *
                (((der(b).value * this.deriv) / this.value) + 
                        Math.log(this.value) * der(b).deriv);
        return new WrapperDerivation(value, dValue); 
    }
    
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
    
    // ------------------------------------------------------------------------
    public static CommonDouble COJAC_MAGIC_getDerivation(CommonDouble d) {
        WrapperDerivation res=new WrapperDerivation(der(d.val).deriv, 0);
        return new CommonDouble(res);
    }
    
    public static CommonFloat COJAC_MAGIC_getDerivation(CommonFloat d) {
        WrapperDerivation res=new WrapperDerivation(der(d.val).deriv, 0);
        return new CommonFloat(res);
    }
    
    public static CommonDouble COJAC_MAGIC_DOUBLE_asDerivationTarget(CommonDouble d) {
        WrapperDerivation res=new WrapperDerivation(der(d.val).value, 1.0);
        return new CommonDouble(res);
    }
    
    public static CommonFloat COJAC_MAGIC_DOUBLE_asDerivationTarget(CommonFloat d) {
        WrapperDerivation res=new WrapperDerivation(der(d.val).value, 1.0);
        return new CommonFloat(res);
    }

    //-------------------------------------------------------------------------
    private static WrapperDerivation der(ACojacWrapper w) {
        return (WrapperDerivation)w;
    }
}
