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

public class WrapperInterval extends ACojacWrapper {
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
    public WrapperInterval(ACojacWrapper w) {
        this(w==null ? 0.0 : c(w).value,
             w==null ? new DoubleInterval(0,0): c(w).interval,
             w==null ? false : c(w).isUnStable);
    }
    
    //-------------------------------------------------------------------------    
    @Override public double toDouble() {
        return value;
    }

    @Override
    public ACojacWrapper fromDouble(double a, boolean wasFromFloat) {
        return new WrapperInterval(a);
    }

    @Override public String asInternalString() {
        return String.format("%s:%s", value, interval);
    }

    @Override public String wrapperName() {
        return "Interval";
    }

    @Override public ACojacWrapper dadd(ACojacWrapper b) {
        return new WrapperInterval(
                value + c(b).value, 
                DoubleInterval.add(interval, c(b).interval), 
                isUnStable || c(b).isUnStable);
    }

    @Override public ACojacWrapper dsub(ACojacWrapper b) {
        return new WrapperInterval(
                value - c(b).value, 
                DoubleInterval.sub(interval, c(b).interval), 
                isUnStable || c(b).isUnStable);
    }

    @Override public ACojacWrapper dmul(ACojacWrapper b) {
        return new WrapperInterval(
                value * c(b).value, 
                DoubleInterval.mul(interval, c(b).interval), 
                isUnStable || c(b).isUnStable);
    }

    @Override public ACojacWrapper ddiv(ACojacWrapper b) {
        return new WrapperInterval(
                value / c(b).value, 
                DoubleInterval.div(interval, c(b).interval), 
                isUnStable || c(b).isUnStable);
    }

    @Override  public ACojacWrapper drem(ACojacWrapper b) {
        return new WrapperInterval(
                value % c(b).value, 
                DoubleInterval.modulo(interval, c(b).interval), 
                isUnStable || c(b).isUnStable);
    }

    @Override public ACojacWrapper dneg() {
        return new WrapperInterval(
                -value, 
                DoubleInterval.neg(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_sqrt() {
        return new WrapperInterval(
                Math.sqrt(value), 
                DoubleInterval.sqrt(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_abs() {
        return new WrapperInterval(
                Math.abs(value), 
                DoubleInterval.abs(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_sin() {
        return new WrapperInterval(
                Math.sin(value), 
                DoubleInterval.sin(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_cos() {
        return new WrapperInterval(
                Math.cos(value), 
                DoubleInterval.cos(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_tan() {
        return new WrapperInterval(
                Math.tan(value), 
                DoubleInterval.tan(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_asin() {
        return new WrapperInterval(
                Math.asin(value), 
                DoubleInterval.asin(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_acos() {
        return new WrapperInterval(
                Math.acos(value), 
                DoubleInterval.acos(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_atan() {
        return new WrapperInterval(
                Math.atan(value), 
                DoubleInterval.atan(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_sinh() {
        return new WrapperInterval(
                Math.sinh(value), 
                DoubleInterval.sinh(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_cosh() {
        return new WrapperInterval(
                Math.cosh(value), 
                DoubleInterval.cosh(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_tanh() {
        return new WrapperInterval(
                Math.tanh(value), 
                DoubleInterval.tanh(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_exp() {
        return new WrapperInterval(
                Math.exp(value), 
                DoubleInterval.exp(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_log() {
        return new WrapperInterval(
                Math.log(value), 
                DoubleInterval.log(interval), 
                isUnStable);
    }

    @Override  public ACojacWrapper math_log10() {
        return new WrapperInterval(
                Math.log10(value), 
                DoubleInterval.log10(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_toRadians() {
        return new WrapperInterval(
                Math.toRadians(value), 
                DoubleInterval.toRadians(interval), 
                isUnStable);
    }

    @Override  public ACojacWrapper math_toDegrees() {
        return new WrapperInterval(
                Math.toDegrees(value), 
                DoubleInterval.toDegrees(interval), 
                isUnStable);
    }

    @Override public ACojacWrapper math_pow(ACojacWrapper b) {
        return new WrapperInterval(
                Math.pow(value, c(b).value), 
                DoubleInterval.pow(interval, c(b).interval), 
                isUnStable);
    }
    
    @Override public int compareTo(ACojacWrapper oo) {
        WrapperInterval o=(WrapperInterval)oo;
        int compResult = this.interval.compareTo(o.interval);
        if (COJAC_CHECK_UNSTABLE_COMPARISONS) {
            if (this.interval.strictlyEquals(o.interval)) return 0;
            if (this.interval.overlaps(o.interval))
                reportBadComparison();
        }
        if (compResult != 0) {
            return compResult;
        }
        if (this.value < o.value) {
            return -1;
        }
        if (this.value > o.value) {
            return 1;
        }
        return 0;
    }

    public static CommonDouble COJAC_MAGIC_relativeError(CommonDouble d) {
        WrapperInterval res=new WrapperInterval(c(d.val).relativeError());
         return new CommonDouble(res);
     }
    
    public static CommonDouble COJAC_MAGIC_relativeError(CommonFloat d) {
        WrapperInterval res=new WrapperInterval(c(d.val).relativeError());
        return new CommonDouble(res);
     }
    
    public static CommonDouble COJAC_MAGIC_width(CommonDouble d) {
        WrapperInterval res=new WrapperInterval(DoubleInterval.width(c(d.val).interval));
         return new CommonDouble(res);
     }

    public static CommonDouble COJAC_MAGIC_width(CommonFloat d) {
        WrapperInterval res=new WrapperInterval(DoubleInterval.width(c(d.val).interval));
        return new CommonDouble(res);
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

    private static WrapperInterval c(ACojacWrapper a) {
        return (WrapperInterval) a;
    }


}
