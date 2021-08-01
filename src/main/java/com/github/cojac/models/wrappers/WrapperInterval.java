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

import static com.github.cojac.models.FloatReplacerClasses.COJAC_CHECK_UNSTABLE_COMPARISONS;
import static com.github.cojac.models.FloatReplacerClasses.COJAC_STABILITY_THRESHOLD;

import com.github.cojac.interval.DoubleInterval;
import com.github.cojac.models.Reactions;

public class WrapperInterval extends ACojacWrapper<WrapperInterval> {
    protected final double value;
    protected final DoubleInterval interval;
    protected final boolean isUnStable;
   
    private WrapperInterval(double v) {
        this(v, new DoubleInterval(v, v), false);
    }
    
    private WrapperInterval(double value, DoubleInterval interval, boolean unstable) {
        this.value = value;
        this.interval = interval;
        this.isUnStable=checkedStability(unstable);
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperInterval(WrapperInterval w) {
        this(w==null ? 0.0 : w.value,
             w==null ? new DoubleInterval(0,0): w.interval,
                w != null && w.isUnStable);
    }
    
    //-------------------------------------------------------------------------    
    @Override public double toDouble() {
        return value;
    }

    @SuppressWarnings("unused")
    @Override
    public WrapperInterval fromDouble(double a, boolean wasFromFloat) {
        return new WrapperInterval(a);
    }

    @Override public String asInternalString() {
        return String.format("%s:%s", value, interval);
    }

    @Override public String wrapperName() {
        return "Interval";
    }

    @Override public WrapperInterval dadd(WrapperInterval b) {
        return new WrapperInterval(
                value + b.value,
                DoubleInterval.add(interval, b.interval),
                isUnStable || b.isUnStable);
    }

    @Override public WrapperInterval dsub(WrapperInterval b) {
        return new WrapperInterval(
                value - b.value,
                DoubleInterval.sub(interval, b.interval),
                isUnStable || b.isUnStable);
    }

    @Override public WrapperInterval dmul(WrapperInterval b) {
        return new WrapperInterval(
                value * b.value,
                DoubleInterval.mul(interval, b.interval),
                isUnStable || b.isUnStable);
    }

    @Override public WrapperInterval ddiv(WrapperInterval b) {
        return new WrapperInterval(
                value / b.value,
                DoubleInterval.div(interval, b.interval),
                isUnStable || b.isUnStable);
    }

    @Override  public WrapperInterval drem(WrapperInterval b) {
        return new WrapperInterval(
                value % b.value,
                DoubleInterval.modulo(interval, b.interval),
                isUnStable || b.isUnStable);
    }

    @Override public WrapperInterval dneg() {
        return new WrapperInterval(
                -value, 
                DoubleInterval.neg(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_sqrt() {
        return new WrapperInterval(
                Math.sqrt(value), 
                DoubleInterval.sqrt(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_abs() {
        return new WrapperInterval(
                Math.abs(value), 
                DoubleInterval.abs(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_sin() {
        return new WrapperInterval(
                Math.sin(value), 
                DoubleInterval.sin(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_cos() {
        return new WrapperInterval(
                Math.cos(value), 
                DoubleInterval.cos(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_tan() {
        return new WrapperInterval(
                Math.tan(value), 
                DoubleInterval.tan(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_asin() {
        return new WrapperInterval(
                Math.asin(value), 
                DoubleInterval.asin(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_acos() {
        return new WrapperInterval(
                Math.acos(value), 
                DoubleInterval.acos(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_atan() {
        return new WrapperInterval(
                Math.atan(value), 
                DoubleInterval.atan(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_sinh() {
        return new WrapperInterval(
                Math.sinh(value), 
                DoubleInterval.sinh(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_cosh() {
        return new WrapperInterval(
                Math.cosh(value), 
                DoubleInterval.cosh(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_tanh() {
        return new WrapperInterval(
                Math.tanh(value), 
                DoubleInterval.tanh(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_exp() {
        return new WrapperInterval(
                Math.exp(value), 
                DoubleInterval.exp(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_log() {
        return new WrapperInterval(
                Math.log(value), 
                DoubleInterval.log(interval), 
                isUnStable);
    }

    @Override  public WrapperInterval math_log10() {
        return new WrapperInterval(
                Math.log10(value), 
                DoubleInterval.log10(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_toRadians() {
        return new WrapperInterval(
                Math.toRadians(value), 
                DoubleInterval.toRadians(interval), 
                isUnStable);
    }

    @Override  public WrapperInterval math_toDegrees() {
        return new WrapperInterval(
                Math.toDegrees(value), 
                DoubleInterval.toDegrees(interval), 
                isUnStable);
    }

    @Override public WrapperInterval math_pow(WrapperInterval b) {
        return new WrapperInterval(
                Math.pow(value, b.value),
                DoubleInterval.pow(interval, b.interval),
                isUnStable);
    }
    
    @Override public int compareTo(WrapperInterval oo) {
        int compResult = this.interval.compareTo(oo.interval);
        if (COJAC_CHECK_UNSTABLE_COMPARISONS) {
            if (this.interval.strictlyEquals(oo.interval)) return 0;
            if (this.interval.overlaps(oo.interval))
                reportBadComparison();
        }
        if (compResult != 0) {
            return compResult;
        }
        return Double.compare(this.value, oo.value);
    }

    public static CommonDouble<WrapperInterval> COJAC_MAGIC_relativeError(CommonDouble<WrapperInterval> d) {
        WrapperInterval res=new WrapperInterval(d.val.relativeError());
         return new CommonDouble<>(res);
     }
    
    public static CommonDouble<WrapperInterval> COJAC_MAGIC_relativeError(CommonFloat<WrapperInterval> d) {
        WrapperInterval res=new WrapperInterval(d.val.relativeError());
        return new CommonDouble<>(res);
     }
    
    public static CommonDouble<WrapperInterval> COJAC_MAGIC_width(CommonDouble<WrapperInterval> d) {
        WrapperInterval res=new WrapperInterval(DoubleInterval.width(d.val.interval));
         return new CommonDouble<>(res);
     }

    public static CommonDouble<WrapperInterval> COJAC_MAGIC_width(CommonFloat<WrapperInterval> d) {
        WrapperInterval res=new WrapperInterval(DoubleInterval.width(d.val.interval));
        return new CommonDouble<>(res);
     }
    //-------------------------------------------------------------------------
    private boolean checkedStability(boolean wasUnstable) {
        if (wasUnstable) return wasUnstable;
        if (COJAC_STABILITY_THRESHOLD < relativeError()) {
            Reactions.react("WrapperInterval detects unstability... "+asInternalString()+" ");
            return true;
        }
        return false;
    }

    private double relativeError() {
        return relativeError(interval.inf, interval.sup);
    }
    
    // We don't see a clean way to define the relative error when the value is 
    // (very close to) zero, which is yet quite common. Here we use a very
    // dirty trick to handle that. 
    protected static double relativeError(double inf, double sup) {
        boolean containsZero = (inf<=0 && sup >= 0);
        double numerator=sup-inf;
        inf=Math.abs(inf);
        sup=Math.abs(sup);
        double denominator = Math.min(inf, sup);
        if(containsZero && Math.max(inf, sup) < COJAC_STABILITY_THRESHOLD)
            return numerator;  // here we "bet" that the true value is zero...
        if (denominator * COJAC_STABILITY_THRESHOLD < Double.MIN_NORMAL) {
            denominator = Math.max(inf,  sup);
            if (denominator * COJAC_STABILITY_THRESHOLD < Double.MIN_NORMAL)
                return numerator;  // here we "bet" that the true value is zero...
        }
        return numerator / denominator;
    }
    
    private void reportBadComparison() {
        Reactions.react("Interval wrapper detects dangerous comparison (overlap)... ");
    }

}
