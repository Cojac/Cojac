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

import java.util.Arrays;
import java.util.Random;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

import com.github.cojac.models.Reactions;

public class WrapperStochastic extends ACompactWrapper<WrapperStochastic> {
    private static final int nbrParallelNumber = 3;

    private final static Random random = new Random();
    
    protected final double value;
    protected final double[] stochasticValue;
    protected final boolean isUnStable;

    private WrapperStochastic(double v, double[] tab, boolean unstable) {
        this.value = v;
        this.stochasticValue=tab;
        this.isUnStable=checkedStability(unstable);   
    }
    
    private WrapperStochastic(double v) {
        this.value = v;
        this.stochasticValue = new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            stochasticValue[i] = v;
        this.isUnStable=false;
    }
    
    //-------------------------------------------------------------------------
    //----------------- Necessary constructor  -------------------------------
    //-------------------------------------------------------------------------
    public WrapperStochastic(WrapperStochastic w) {
        this(w==null ? 0.0 : w.value);
    }
    
    //-------------------------------------------------------------------------
    @Override
    public WrapperStochastic applyUnaryOp(DoubleUnaryOperator op) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(op.applyAsDouble(this.stochasticValue[i]));
        double v=op.applyAsDouble(this.value);
        return new WrapperStochastic(v, t, this.isUnStable);
    }

    @Override
    public WrapperStochastic applyBinaryOp(DoubleBinaryOperator op, WrapperStochastic b) {
        double[] t=new double[nbrParallelNumber];
        for (int i = 0; i < nbrParallelNumber; i++)
            t[i] = rndRound(op.applyAsDouble(this.stochasticValue[i], b.stochasticValue[i]));
        double v=op.applyAsDouble(this.value, b.value);
        return new WrapperStochastic(v, t, this.isUnStable);
    }
    
    @Override public double toDouble() {
        return value;
    }

    @SuppressWarnings("unused")
    @Override
    public WrapperStochastic fromDouble(double a, boolean wasFromFloat) {
        return new WrapperStochastic(a);
    }

    @Override public String asInternalString() {
        String res = value + " : [%s]";
        StringBuilder tmp = new StringBuilder();
        for (int i = 0; true; i++) {
            tmp.append(stochasticValue[i]);
            if (i == nbrParallelNumber - 1)
                break;
            tmp.append(";");
        }
        return String.format(res, tmp);
    }

    @Override public String wrapperName() {
        return "Stochastic";
    }
    
    @Override public int compareTo(WrapperStochastic o) {
        if (COJAC_CHECK_UNSTABLE_COMPARISONS) {
            if (value==o.value && Arrays.equals(stochasticValue, o.stochasticValue)) return 0;
            if (this.overlaps(o))
                reportBadComparison();
        }
        return Double.compare(this.value, o.value);
    }

    public static CommonDouble<WrapperStochastic> COJAC_MAGIC_relativeError(CommonDouble<WrapperStochastic> d) {
       WrapperStochastic res=new WrapperStochastic(d.val.relativeError());
        return new CommonDouble<>(res);
    }
    
    public static CommonDouble<WrapperStochastic> COJAC_MAGIC_relativeError(CommonFloat<WrapperStochastic> d) {
        WrapperStochastic res=new WrapperStochastic(d.val.relativeError());
         return new CommonDouble<>(res);
     }
     
    //-------------------------------------------------------------------------
    private boolean checkedStability(boolean wasUnstable) {
        if (wasUnstable) return true;
        if (COJAC_STABILITY_THRESHOLD < relativeError()) {
            Reactions.react("WrapperStochastic detects unstability... "+asInternalString()+" ");
            return true;
        }
        return false;
    }


    private double relativeError() {
        return WrapperInterval.relativeError(min(), max());
    }
    
    /*
    private double relativeErrorAsInLiterature() {
        final double Tb = 4.303; // see chenaux 1988
        double mean = 0.0;
        for (int i = 0; i < nbrParallelNumber; i++)
            mean += this.stochasticValue[i];
        mean = mean / nbrParallelNumber;
        double squareSum = 0.0;
        for (int i = 0; i < nbrParallelNumber; i++) {
            double delta = this.stochasticValue[i] - mean;
            squareSum += delta*delta;
        }
        double sigmaSquare = (1.0 / (nbrParallelNumber - 1)) * squareSum;
        double Cr = (Math.sqrt(nbrParallelNumber) * Math.abs(mean)) /
                (Math.sqrt(sigmaSquare) * Tb);
        // Cr is the numberOfStableDigits; turn that into relativeError
        return Math.pow(10.0, -Cr);
    }
    */
    
    private double min() {
        double min=value;
        for(double f:stochasticValue)
            if(f<min) min=f;
        return min;
    }
    
    private double max() {
        double max=value;
        for(double f:stochasticValue)
            if(f>max) max=f;
        return max;
    }

    private boolean overlaps(WrapperStochastic o) {
        double min=min(), max=max(), omin=o.min(), omax=o.max();
        return Math.max(min, omin) <= Math.min(max, omax);
    }
    
    private void reportBadComparison() {
        Reactions.react("WrapperStochastic detects dangerous comparison (overlap)... ");
    }

    private static double rndRound(double value) {
        switch (random.nextInt(3)) {
        case 0: return value - Math.ulp(value); // round to negative infinity
        case 1: return value + Math.ulp(value); // round to positive infinity
        default:return value;                   // default rounding mode in Java
            
        }
    }
}
