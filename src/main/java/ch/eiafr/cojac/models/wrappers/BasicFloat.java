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

public class BasicFloat extends Number implements Comparable<BasicFloat> {
    private final float val;
    
    private BasicFloat(float v) {
        val=v;
    }
    
    public static BasicFloat fromFloat(float a) {
        return new BasicFloat(a);
    }
    
    public static BasicFloat fromString(String a){
        return new BasicFloat(Float.valueOf(a));
    }
    
    public static BasicFloat fromDouble(BasicDouble a){
        return new BasicFloat((float)BasicDouble.toDouble(a));
    }

    public static BasicFloat fadd(BasicFloat a, BasicFloat b) {
        return new BasicFloat(a.val+b.val);
    }
    
    public static BasicFloat fsub(BasicFloat a, BasicFloat b) {
        return new BasicFloat(a.val-b.val);
    }

    public static BasicFloat fmul(BasicFloat a, BasicFloat b) {
        return new BasicFloat(a.val*b.val);
    }

    public static BasicFloat fdiv(BasicFloat a, BasicFloat b) {
        return new BasicFloat(a.val/b.val);
    }

    public static BasicFloat frem(BasicFloat a, BasicFloat b) {
        return new BasicFloat(a.val%b.val);
    }
    
    public static BasicFloat fneg(BasicFloat a) {
        return new BasicFloat(-a.val);
    }

    public static float toFloat(BasicFloat a) {
        return a.val;
    }
    
    public static Float toRealFloatWrapper(BasicFloat a){
        return new Float(a.val);
    }
    
    // TODO: correctly implement fcmpl and fcmpg
    public static int fcmpl(BasicFloat a, BasicFloat b) {
        if (a.val < b.val) return -1;
        if (a.val > b.val) return +1;
        return 0;
    }
    
    public static int fcmpg(BasicFloat a, BasicFloat b) {
        if (a.val < b.val) return -1;
        if (a.val > b.val) return +1;
        return 0;
    }
    
    public static int f2i(BasicFloat a) {
        return (int) a.val;
    }
    
    public static long f2l(BasicFloat a) {
        return (long) a.val;
    }
    
    public static BasicDouble f2d(BasicFloat a) {
        return BasicDouble.fromDouble(a.val);
    }
    
    public static BasicFloat i2f(int a) {
        return new BasicFloat((float)a);
    }
    
    public static BasicFloat l2f(long a) {
        return new BasicFloat((float)a);
    }

    public static BasicFloat d2f(BasicDouble a) {
        return new BasicFloat((float)BasicDouble.toDouble(a));
    }

    @Override
    public String toString(){
        return Float.toString(val);
    }
    
    public static boolean isInfiniteProxy(BasicFloat a){
        return Float.isInfinite(a.val);
    }

	@Override
	public int compareTo(BasicFloat o) {
	    return Float.compare(this.val, o.val);
//		if(val > o.val)
//			return 1;
//		if(val < o.val)
//			return -1;
//		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
	    return new Float(this.val).equals(obj);
//        return (obj instanceof BasicFloat) && (((BasicFloat)obj).val == val);
    }

	@Override
	public int hashCode() {
	    return Float.hashCode(this.val);
//		int hash = 7;
//		hash = 43 * hash + Float.floatToIntBits(this.val);
//		return hash;
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
		return val;
	}

	@Override
	public double doubleValue() {
		return val;
	}


}
