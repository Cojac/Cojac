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

public class BigDecimalDouble extends Number implements Comparable<BigDecimalDouble>{
	private final BigDecimal val;
	private boolean isNaN = false;
	private boolean isInfinite = false;
	private boolean isPositiveInfinite = false;
	
	private static MathContext mathContext;
    
	public BigDecimalDouble(BigDecimal v) {
		mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        val = v;
    }
	
	public BigDecimal toBigDecimal(){
		return val;
	}
	
    public BigDecimalDouble(double v) {
		mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
		if(Double.isNaN(v)){
			isNaN = true;
			val = null;
		}
		else if(Double.isInfinite(v)){
			isInfinite = true;
			isPositiveInfinite = v > 0;
			val = null;
		}
		else{
			val = new BigDecimal(v, mathContext);
		}
    }
    
    public BigDecimalDouble(BigDecimalDouble v) {
		mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        val = v.val;
    }
    
    public BigDecimalDouble(String v) {
		mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        val= new BigDecimal(v, mathContext);
    }
    
    public BigDecimalDouble(BigDecimalFloat v) {
		mathContext = new MathContext(COJAC_BIGDECIMAL_PRECISION);
        val= v.toBigDecimal();
    }
    
    public static BigDecimalDouble fromDouble(double a) {
        return new BigDecimalDouble(a);
    }
    
    public static BigDecimalDouble fromString(String a){
        return new BigDecimalDouble(a);
    }

    public static BigDecimalDouble dadd(BigDecimalDouble a, BigDecimalDouble b) {
		if(a.isNaN || b.isNaN)
			return new BigDecimalDouble(Double.NaN);
		if(a.isInfinite || b.isInfinite){
			double aVal = a.getDoubleInfiniteValue();
			double bVal = b.getDoubleInfiniteValue();
			return new BigDecimalDouble(aVal+bVal);
		}
        return new BigDecimalDouble(a.val.add(b.val, mathContext));
    }
    
    public static BigDecimalDouble dsub(BigDecimalDouble a, BigDecimalDouble b) {
        if(a.isNaN || b.isNaN)
			return new BigDecimalDouble(Double.NaN);
		if(a.isInfinite || b.isInfinite){
			double aVal = a.getDoubleInfiniteValue();
			double bVal = b.getDoubleInfiniteValue();
			return new BigDecimalDouble(aVal-bVal);
		}
        return new BigDecimalDouble(a.val.subtract(b.val, mathContext));
    }

    public static BigDecimalDouble dmul(BigDecimalDouble a, BigDecimalDouble b) {
		if(a.isNaN || b.isNaN)
			return new BigDecimalDouble(Double.NaN);
		if(a.isInfinite || b.isInfinite){
			double aVal = a.getDoubleInfiniteValue();
			double bVal = b.getDoubleInfiniteValue();
			return new BigDecimalDouble(aVal*bVal);
		}
        return new BigDecimalDouble(a.val.multiply(b.val, mathContext));
    }

    public static BigDecimalDouble ddiv(BigDecimalDouble a, BigDecimalDouble b) {
		if(a.isNaN || b.isNaN)
			return new BigDecimalDouble(Double.NaN);
		if(a.isInfinite || b.isInfinite){
			double aVal = a.getDoubleInfiniteValue();
			double bVal = b.getDoubleInfiniteValue();
			return new BigDecimalDouble(aVal/bVal);
		}
		if(b.val.equals(BigDecimal.ZERO))
			return new BigDecimalDouble(a.val.doubleValue()/0.0);
        return new BigDecimalDouble(a.val.divide(b.val, mathContext));
    }

    public static BigDecimalDouble drem(BigDecimalDouble a, BigDecimalDouble b) {
		if(a.isNaN || b.isNaN)
			return new BigDecimalDouble(Double.NaN);
		if(a.isInfinite || b.isInfinite){
			double aVal = a.getDoubleInfiniteValue();
			double bVal = b.getDoubleInfiniteValue();
			return new BigDecimalDouble(aVal%bVal);
		}
        return new BigDecimalDouble(a.val.remainder(b.val, mathContext)); // is this correct ?
    }
    
    public static BigDecimalDouble dneg(BigDecimalDouble a) {
		if(a.isNaN)
			return new BigDecimalDouble(Double.NaN);
		if(a.isInfinite){
			double aVal = a.getDoubleInfiniteValue();
			return new BigDecimalDouble(-aVal);
		}
        return new BigDecimalDouble(a.val.negate(mathContext));
    }

    public static double toDouble(BigDecimalDouble a) {
		if(a.isNaN)
			return Double.NaN;
		if(a.isInfinite){
			if(a.isPositiveInfinite)
				return Double.POSITIVE_INFINITY;
			else
				return Double.NEGATIVE_INFINITY;
		}
        return a.val.doubleValue();
    }
    
    public static Double toRealDoubleWrapper(BigDecimalDouble a){
		if(a.isNaN)
			return Double.NaN;
		if(a.isInfinite){
			if(a.isPositiveInfinite)
				return Double.POSITIVE_INFINITY;
			else
				return Double.NEGATIVE_INFINITY;
		}
        return a.val.doubleValue();
    }
    
    // TODO: correctly implement dcmpl and dcmpg
    public static int dcmpl(BigDecimalDouble a, BigDecimalDouble b) {
		return a.compareTo(b); // is this correct ?
    }
    
    public static int dcmpg(BigDecimalDouble a, BigDecimalDouble b) {
		return a.compareTo(b); // is this correct ?
    }
    
    public static int d2i(BigDecimalDouble a) {
		if(a.isNaN)
			return (int)Double.NaN;
		if(a.isInfinite){
			if(a.isPositiveInfinite)
				return (int)Double.POSITIVE_INFINITY;
			else
				return (int)Double.NEGATIVE_INFINITY;
		}
        return a.val.intValue();
    }
    
    public static long d2l(BigDecimalDouble a) {
		if(a.isNaN)
			return (long)Double.NaN;
		if(a.isInfinite){
			if(a.isPositiveInfinite)
				return (long)Double.POSITIVE_INFINITY;
			else
				return (long)Double.NEGATIVE_INFINITY;
		}
        return a.val.longValue();
    }
    
    public static BigDecimalFloat d2f(BigDecimalDouble a) {
		if(a.isNaN)
			return new BigDecimalFloat(Float.NaN);
		if(a.isInfinite){
			if(a.isPositiveInfinite)
				return new BigDecimalFloat(Float.POSITIVE_INFINITY);
			else
				return new BigDecimalFloat(Float.NEGATIVE_INFINITY);
		}
        return new BigDecimalFloat(a.val);
    }
    
    @Override
    public String toString(){
        if(isNaN) return "NaN";
        if(isInfinite) return (isPositiveInfinite?'+':'-')+"Infinity";
        return val.toString();
    }
    
    public static BigDecimalDouble i2d(int a) {
        return new BigDecimalDouble(new BigDecimal(a));
    }
    
    public static BigDecimalDouble l2d(long a) {
        return new BigDecimalDouble(new BigDecimal(a));
    }

    public static BigDecimalDouble f2d(BigDecimalFloat a) {
        return new BigDecimalDouble(a);
    }

	public static BigDecimalDouble math_sqrt(BigDecimalDouble a){
		// TODO - make a better sqrt for BigDecimal!
        return new BigDecimalDouble(BigDecimal.valueOf(Math.sqrt(a.doubleValue())));
    }
	
	@Override
	public int compareTo(BigDecimalDouble o) {
		// if o > this => -1
		// if o < this => 1
		// if o == this => 0
		
		if(isNaN && o.isNaN)
			return 0;
		if(isNaN)
			return 1;
		if(o.isNaN)
			return -1;
		
		if(isInfinite || o.isInfinite){
			Double aVal = getDoubleInfiniteValue();
			Double bVal = o.getDoubleInfiniteValue();
			return aVal.compareTo(bVal);
		}
		
		return val.compareTo(o.val);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BigDecimalDouble == false)
			return false;
		BigDecimalDouble bdd = (BigDecimalDouble)obj;
		if(isNaN && bdd.isNaN)
			return true;
		if(isNaN || bdd.isNaN)
			return false;
		
		if(isInfinite || bdd.isInfinite){
			Double aVal = getDoubleInfiniteValue();
			Double bVal = bdd.getDoubleInfiniteValue();
			return aVal.equals(bVal);
		}
		
        return val.equals(bdd.val);
    }

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.val);
		return hash;
	}

	@Override
	public int intValue() {
	    if(isNaN) return 0;
	    if (isInfinite) return isPositiveInfinite?Integer.MAX_VALUE:Integer.MIN_VALUE;
		return val.intValue();
	}

	@Override
	public long longValue() {
        if(isNaN) return 0;
        if (isInfinite) return isPositiveInfinite?Long.MAX_VALUE:Long.MIN_VALUE;
		return val.longValue();
	}

	@Override
	public float floatValue() {
        if(isNaN) return Float.NaN;
        if (isInfinite) return isPositiveInfinite?Float.POSITIVE_INFINITY:Float.NEGATIVE_INFINITY;
		return val.floatValue();
	}

	@Override
	public double doubleValue() {
        if(isNaN) return Double.NaN;
        if (isInfinite) return isPositiveInfinite?Double.POSITIVE_INFINITY:Double.NEGATIVE_INFINITY;
		return val.doubleValue();
	}

	private double getDoubleInfiniteValue(){
		if(!isInfinite)
			return val.doubleValue();
		if(isPositiveInfinite)
			return Double.POSITIVE_INFINITY;
		return Double.NEGATIVE_INFINITY;
	}

}
