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

import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;


public class BasicDouble extends Number implements Comparable<BasicDouble>{
    private final double val;
    
    private BasicDouble(double v) {
        val=v;
    }
    
    public static BasicDouble fromDouble(double a) {
        return new BasicDouble(a);
    }
    
    public static BasicDouble fromString(String a){
        return new BasicDouble(Double.valueOf(a));
    }

    public static BasicDouble dadd(BasicDouble a, BasicDouble b) {
        return new BasicDouble(a.val+b.val);
    }
    
    public static BasicDouble dsub(BasicDouble a, BasicDouble b) {
        return new BasicDouble(a.val-b.val);
    }

    public static BasicDouble dmul(BasicDouble a, BasicDouble b) {
        return new BasicDouble(a.val*b.val);
    }

    public static BasicDouble ddiv(BasicDouble a, BasicDouble b) {
        return new BasicDouble(a.val/b.val);
    }

    public static BasicDouble drem(BasicDouble a, BasicDouble b) {
        return new BasicDouble(a.val%b.val);
    }
    
    public static BasicDouble dneg(BasicDouble a) {
        return new BasicDouble(-a.val);
    }

    public static double toDouble(BasicDouble a) {
        return a.val;
    }
    
    public static Double toRealDoubleWrapper(BasicDouble a){
        return new Double(a.val);
    }
    
    // TODO: correctly implement dcmpl and dcmpg
    public static int dcmpl(BasicDouble a, BasicDouble b) {
        if (a.val < b.val) return -1;
        if (a.val > b.val) return +1;
        return 0;
    }
    
    public static int dcmpg(BasicDouble a, BasicDouble b) {
        if (a.val < b.val) return -1;
        if (a.val > b.val) return +1;
        return 0;
    }
    
    public static int d2i(BasicDouble a) {
        return (int) a.val;
    }
    
    public static long d2l(BasicDouble a) {
        return (long) a.val;
    }
    
    public static BasicFloat d2f(BasicDouble a) {
        return BasicFloat.fromFloat((float)a.val);
    }
    
    public static BasicDouble i2d(int a) {
        return new BasicDouble((double)a);
    }
    
    public static BasicDouble l2d(long a) {
        return new BasicDouble((double)a);
    }

    public static BasicDouble f2d(BasicFloat a) {
        return new BasicDouble((double)BasicFloat.toFloat(a));
    }

	public static BasicDouble math_sqrt(BasicDouble a){
//	    return a.afterUnaryOp(Math::sqrt);
        return new BasicDouble(Math.sqrt(a.val));
    }
	
	private BasicDouble afterUnaryOp(DoubleUnaryOperator op) {
	    return new BasicDouble(op.applyAsDouble(this.val));
	}
	
	private BasicDouble afterBinaryOp(DoubleBinaryOperator op, BasicDouble arg) {
	    return new BasicDouble(op.applyAsDouble(this.val, arg.val));
	}

	
	/*
    public static MyWrapper math_abs(MyWrapper a) {
    public static MyWrapper math_sqrt(MyWrapper a) {
    public static MyWrapper math_sin(MyWrapper a) {
    public static MyWrapper math_cos(MyWrapper a) {
    public static MyWrapper math_tan(MyWrapper a) {
    public static MyWrapper math_sinh(MyWrapper a) {
    public static MyWrapper math_cosh(MyWrapper a) {
    public static MyWrapper math_tanh(MyWrapper a) {
    public static MyWrapper math_acos(MyWrapper a) {
    public static MyWrapper math_atan(MyWrapper a) {
    public static MyWrapper math_asin(MyWrapper a) {
    public static MyWrapper math_exp(MyWrapper a) {
    public static MyWrapper math_log(MyWrapper a) {
    public static MyWrapper math_log10(MyWrapper a) {
    public static MyWrapper math_pow(MyWrapper a, MyWrapper b) {
	 */
	
	@Override
	public int compareTo(BasicDouble o) {
        return Double.compare(this.val, o.val);
//		if(val > o.val)
//			return 1;
//		if(val < o.val)
//			return -1;
//		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
	    return new Double(this.val).equals(obj);
//        return (obj instanceof BasicDouble) && (((BasicDouble)obj).val == val);
    }

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 43 * hash + (int) (Double.doubleToLongBits(this.val) ^ (Double.doubleToLongBits(this.val) >>> 32));
		return hash;
	}

	@Override
	public int intValue() {
		return (int) val;
	}

	@Override
	public long longValue() {
		return (long) val;
	}

	@Override
	public float floatValue() {
		return (float) val;
	}

	@Override
	public double doubleValue() {
		return val;
	}


}
