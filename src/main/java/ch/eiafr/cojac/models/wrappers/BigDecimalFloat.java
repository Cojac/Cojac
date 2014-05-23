/*
 * *
 *    Copyright 2011 Baptiste Wicht & Frédéric Bapst
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

import ch.eiafr.cojac.models.*;
import java.math.BigDecimal;
import java.util.Objects;

public class BigDecimalFloat extends Number implements Comparable<BigDecimalFloat> {
    private final BigDecimal val;
	
	public BigDecimalFloat(BigDecimal v) {
        val = v;
    }
	
	public BigDecimal toBigDecimal(){
		return val;
	}
    
    public BigDecimalFloat(float v) {
        val = new BigDecimal(v);
    }
    
    public BigDecimalFloat(BigDecimalFloat v) {
        val = v.val;
    }
    
    public BigDecimalFloat(String v) {
        val = new BigDecimal(v);
    }
    
    public BigDecimalFloat(BigDecimalDouble v) {
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
        return new BigDecimalFloat(a.val.add(b.val));
    }
    
    public static BigDecimalFloat fsub(BigDecimalFloat a, BigDecimalFloat b) {
        return new BigDecimalFloat(a.val.subtract(b.val));
    }

    public static BigDecimalFloat fmul(BigDecimalFloat a, BigDecimalFloat b) {
        return new BigDecimalFloat(a.val.multiply(b.val));
    }

    public static BigDecimalFloat fdiv(BigDecimalFloat a, BigDecimalFloat b) {
        return new BigDecimalFloat(a.val.divide(b.val));
    }

    public static BigDecimalFloat frem(BigDecimalFloat a, BigDecimalFloat b) {
        return new BigDecimalFloat(a.val.remainder(b.val));
    }
    
    public static BigDecimalFloat fneg(BigDecimalFloat a) {
        return new BigDecimalFloat(a.val.negate());
    }

    public static float toFloat(BigDecimalFloat a) {
        return a.val.floatValue();
    }
    
    public static Float toRealFloatWrapper(BigDecimalFloat a){
        return a.val.floatValue();
    }
    
    // TODO: correctly implement fcmpl and fcmpg
    public static int fcmpl(BigDecimalFloat a, BigDecimalFloat b) {
        return a.val.compareTo(b.val);
    }
    
    public static int fcmpg(BigDecimalFloat a, BigDecimalFloat b) {
        return a.val.compareTo(b.val);
    }
    
    public static int f2i(BigDecimalFloat a) {
        return a.val.intValue();
    }
    
    public static long f2l(BigDecimalFloat a) {
        return a.val.longValue();
    }
    
    public static BigDecimalDouble f2d(BigDecimalFloat a) {
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

    @Override
    public String toString(){
        return val.toString();
    }
    
	@Override
	public int compareTo(BigDecimalFloat o) {
		return val.compareTo(o.val);
	}
	
	@Override
	public boolean equals(Object obj) {
        return (obj instanceof BigDecimalFloat) && (((BigDecimalFloat)obj).val.equals(val));
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
		return val.floatValue();
	}

	@Override
	public double doubleValue() {
		return val.doubleValue();
	}

}
