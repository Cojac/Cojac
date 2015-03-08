/*
 * *
 *    Copyright 2014 Frédéric Bapst & Romain Monnard
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

package ch.eiafr.cojac.models.wrappers;

import static ch.eiafr.cojac.models.FloatReplacerClasses.COJAC_BIGDECIMAL_PRECISION;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

public class BigDecimalFloat extends Number implements Comparable<BigDecimalFloat> {
    private final BigDecimal val;
	
	private boolean isNaN = false;
	private boolean isInfinite = false;
	private boolean isPositiveInfinite = false;
	
	private static MathContext mathContext;
	
	public BigDecimalFloat(BigDecimal v) {
		mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        val = v;
    }
	
	public BigDecimal toBigDecimal(){
		return val;
	}
    
    public BigDecimalFloat(float v) {
		mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
		if(Float.isNaN(v)){
			isNaN = true;
			val = null;
		}
		else if(Float.isInfinite(v)){
			isInfinite = true;
			isPositiveInfinite = v > 0;
			val = null;
		}
		else{
			val = new BigDecimal(v, mathContext);
		}
    }
    
    public BigDecimalFloat(BigDecimalFloat v) {
		mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        val = v.val;
    }
    
    public BigDecimalFloat(String v) {
		mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        val = new BigDecimal(v, mathContext);
    }
    
    public BigDecimalFloat(BigDecimalDouble v) {
		mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        val = v.toBigDecimal();
    }
    
    public static BigDecimalFloat fromFloat(float a) {
        return new BigDecimalFloat(a);
    }
    
    public static BigDecimalFloat fromString(String a){
        return new BigDecimalFloat(a);
    }
    
    public static BigDecimalFloat fromDouble(BigDecimalDouble a){
        return new BigDecimalFloat(a.toBigDecimal());
    }

    public static BigDecimalFloat fadd(BigDecimalFloat a, BigDecimalFloat b) {
		if(a.isNaN || b.isNaN)
			return new BigDecimalFloat(Float.NaN);
		if(a.isInfinite || b.isInfinite){
			float aVal = a.getFloatInfiniteValue();
			float bVal = b.getFloatInfiniteValue();
			return new BigDecimalFloat(aVal+bVal);
		}
        return new BigDecimalFloat(a.val.add(b.val, mathContext));
    }
    
    public static BigDecimalFloat fsub(BigDecimalFloat a, BigDecimalFloat b) {
		if(a.isNaN || b.isNaN)
			return new BigDecimalFloat(Float.NaN);
		if(a.isInfinite || b.isInfinite){
			float aVal = a.getFloatInfiniteValue();
			float bVal = b.getFloatInfiniteValue();
			return new BigDecimalFloat(aVal-bVal);
		}
        return new BigDecimalFloat(a.val.subtract(b.val, mathContext));
    }

    public static BigDecimalFloat fmul(BigDecimalFloat a, BigDecimalFloat b) {
		if(a.isNaN || b.isNaN)
			return new BigDecimalFloat(Float.NaN);
		if(a.isInfinite || b.isInfinite){
			float aVal = a.getFloatInfiniteValue();
			float bVal = b.getFloatInfiniteValue();
			return new BigDecimalFloat(aVal*bVal);
		}
        return new BigDecimalFloat(a.val.multiply(b.val, mathContext));
    }

    public static BigDecimalFloat fdiv(BigDecimalFloat a, BigDecimalFloat b) {
		if(a.isNaN || b.isNaN)
			return new BigDecimalFloat(Float.NaN);
		if(a.isInfinite || b.isInfinite){
			float aVal = a.getFloatInfiniteValue();
			float bVal = b.getFloatInfiniteValue();
			return new BigDecimalFloat(aVal/bVal);
		}
		if(b.val.equals(BigDecimal.ZERO))
			return new BigDecimalFloat(a.val.floatValue()/0.0f);
        return new BigDecimalFloat(a.val.divide(b.val, mathContext));
    }

    public static BigDecimalFloat frem(BigDecimalFloat a, BigDecimalFloat b) {
		if(a.isNaN || b.isNaN)
			return new BigDecimalFloat(Float.NaN);
		if(a.isInfinite || b.isInfinite){
			float aVal = a.getFloatInfiniteValue();
			float bVal = b.getFloatInfiniteValue();
			return new BigDecimalFloat(aVal%bVal);
		}
        return new BigDecimalFloat(a.val.remainder(b.val, mathContext)); // is this correct ?
    }
    
    public static BigDecimalFloat fneg(BigDecimalFloat a) {
		if(a.isNaN)
			return new BigDecimalFloat(Float.NaN);
		if(a.isInfinite){
			float aVal = a.getFloatInfiniteValue();
			return new BigDecimalFloat(-aVal);
		}
        return new BigDecimalFloat(a.val.negate(mathContext));
    }

    public static float toFloat(BigDecimalFloat a) {
        if(a.isNaN)
            return Float.NaN;
        if(a.isInfinite){
            if(a.isPositiveInfinite)
                return Float.POSITIVE_INFINITY;
            /*else*/ return Float.NEGATIVE_INFINITY;
        }
        return a.val.floatValue();
    }
    
    public static Float toRealFloatWrapper(BigDecimalFloat a){
        return toFloat(a);
    }
    
    // TODO: correctly implement fcmpl and fcmpg
    public static int fcmpl(BigDecimalFloat a, BigDecimalFloat b) {
        return a.compareTo(b); // is this correct?
    }
    
    public static int fcmpg(BigDecimalFloat a, BigDecimalFloat b) {
        return a.compareTo(b); // is this correct?
    }
    
    public static int f2i(BigDecimalFloat a) {
        if(a.isNaN)
			return (int)Float.NaN;
		if(a.isInfinite){
			if(a.isPositiveInfinite)
				return (int)Float.POSITIVE_INFINITY;
			else
				return (int)Float.NEGATIVE_INFINITY;
		}
        return a.val.intValue();
    }
    
    public static long f2l(BigDecimalFloat a) {
        if(a.isNaN)
			return (long)Float.NaN;
		if(a.isInfinite){
			if(a.isPositiveInfinite)
				return (long)Float.POSITIVE_INFINITY;
			else
				return (long)Float.NEGATIVE_INFINITY;
		}
        return a.val.longValue();
    }
    
    public static BigDecimalDouble f2d(BigDecimalFloat a) {
        if(a.isNaN)
			return new BigDecimalDouble(Double.NaN);
		if(a.isInfinite){
			if(a.isPositiveInfinite)
				return new BigDecimalDouble(Double.POSITIVE_INFINITY);
			else
				return new BigDecimalDouble(Double.NEGATIVE_INFINITY);
		}
        return new BigDecimalDouble(a.val);
    }
    
    public static BigDecimalFloat i2f(int a) {
        return new BigDecimalFloat(a);
    }
    
    public static BigDecimalFloat l2f(long a) {
        return new BigDecimalFloat(a);
    }

    public static BigDecimalFloat d2f(BigDecimalDouble a) {
        return new BigDecimalFloat(a.toBigDecimal());
    }

    /* Mathematical function */
    // TODO make better function !
    public static BigDecimalFloat math_abs(BigDecimalFloat value)
    {
        return new BigDecimalFloat(Math.abs(value.floatValue()));
    }

    public static BigDecimalFloat math_max(BigDecimalFloat valueA, BigDecimalFloat valueB)
    {
        if(valueA.compareTo(valueB) > 0)
        {
            return valueA;
        }
        return valueB;
    }

    public static BigDecimalFloat math_min(BigDecimalFloat valueA, BigDecimalFloat valueB)
    {
        if (valueA.compareTo(valueB) < 0)
        {
            return valueA;
        }
        return valueB;
    }

    @Override
    public String toString(){
        if(isNaN) return "NaN";
        if(isInfinite) return (isPositiveInfinite?'+':'-')+"Infinity";
        return val.toString();
    }
    
	@Override
	public int compareTo(BigDecimalFloat o) {
		if(isNaN && o.isNaN)
			return 0;
		if(isNaN)
			return 1;
		if(o.isNaN)
			return -1;
		
		if(isInfinite || o.isInfinite){
			Float aVal = getFloatInfiniteValue();
			Float bVal = o.getFloatInfiniteValue();
			return aVal.compareTo(bVal);
		}
		
		return val.compareTo(o.val);
	}
	
	@Override
	public boolean equals(Object obj) {
        if(obj instanceof BigDecimalFloat == false)
			return false;
		BigDecimalFloat bdd = (BigDecimalFloat)obj;
		if(isNaN && bdd.isNaN)
			return true;
		if(isNaN || bdd.isNaN)
			return false;
		
		if(isInfinite || bdd.isInfinite){
			Float aVal = getFloatInfiniteValue();
			Float bVal = bdd.getFloatInfiniteValue();
			return aVal.equals(bVal);
		}
		
        return val.equals(bdd.val);
    }

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 83 * hash + Objects.hashCode(this.val);
		return hash;
	}

	@Override
	public int intValue() {
		return val.intValue();
	}

	@Override
	public long longValue() {
		return val.longValue();
	}

	@Override
	public float floatValue() {
		return toFloat(this);
	}

	@Override
	public double doubleValue() {
		return toFloat(this);
	}
	
	private float getFloatInfiniteValue(){
		if(!isInfinite)
			return val.floatValue();
		if(isPositiveInfinite)
			return Float.POSITIVE_INFINITY;
		return Float.NEGATIVE_INFINITY;
	}
	
}
