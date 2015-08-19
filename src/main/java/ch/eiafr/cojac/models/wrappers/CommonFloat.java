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

import static ch.eiafr.cojac.models.wrappers.CommonDouble.newInstance;

public class CommonFloat extends Number implements Comparable<CommonFloat> {
    //-------------------------------------------------------------------------
    //----------------- Fields and auxiliary constructors ---------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

    protected final ACojacWrapper val;
    
    protected CommonFloat(ACojacWrapper w) {
        this.val=w;
    }

    //-------------------------------------------------------------------------
    //----------------- Necessary constructors  -------------------------------
    //-------------------------------------------------------------------------

    private CommonFloat(float v) {
        val = newInstance(null).fromDouble(v, true);
    }
    
    public CommonFloat(String v) {
        this(Float.valueOf(v));
    }
    
    public CommonFloat(CommonFloat v) {
        val=newInstance(v.val);
    }
    
    public CommonFloat(CommonDouble v) {
        val=newInstance(v.val);
    }

    //-------------------------------------------------------------------------
    //----------------- Methods with 1st parameter of 'this' type -------------
    //-------------------------------------------------------------------------

    public static CommonFloat fadd(CommonFloat a, CommonFloat b) {
        return new CommonFloat(a.val.dadd(b.val));
    }
    
    public static CommonFloat fsub(CommonFloat a, CommonFloat b) {
        return new CommonFloat(a.val.dsub(b.val));
    }

    public static CommonFloat fmul(CommonFloat a, CommonFloat b) {
        return new CommonFloat(a.val.dmul(b.val));
    }

    public static CommonFloat fdiv(CommonFloat a, CommonFloat b) {
        return new CommonFloat(a.val.ddiv(b.val));
    }

    public static CommonFloat frem(CommonFloat a, CommonFloat b) {
        return new CommonFloat(a.val.drem(b.val));
    }
    
    public static CommonFloat fneg(CommonFloat a) {
        return new CommonFloat(a.val.dneg());
    }

    public static float toFloat(CommonFloat a) {
        return (float)a.val.toDouble();
    }
    
    public static Float toRealFloatWrapper(CommonFloat a){
        return toFloat(a);
    }
    
    public static int fcmpl(CommonFloat a, CommonFloat b) {
        return a.val.dcmpl(b.val);
    }
    
    public static int fcmpg(CommonFloat a, CommonFloat b) {
        return a.val.dcmpg(b.val);
    }
    
    public static CommonFloat math_abs(CommonFloat a) {
        return new CommonFloat(a.val.math_abs());
    }

    public static CommonFloat math_min(CommonFloat a, CommonFloat b) {
        return new CommonFloat(a.val.math_min(b.val));
    }

    public static CommonFloat math_max(CommonFloat a, CommonFloat b) {
        return new CommonFloat(a.val.math_max(b.val));
    }

    public static int f2i(CommonFloat a) {
        return (int)a.val.toDouble();
    }
    
    public static long f2l(CommonFloat a) {
        return (long)a.val.toDouble();
    }
    
    public static CommonDouble f2d(CommonFloat a) {
        return new CommonDouble(a.val);
    }

    //-------------------------------------------------------------------------
    //----------------- Necessarily static methods ----------------------------
    //-------------------------------------------------------------------------

    public static CommonFloat fromFloat(float a) {
        return new CommonFloat(a);
    }

    public static CommonFloat fromRealFloatWrapper(Float a) {
        return fromFloat(a);
    }

    public static CommonFloat fromString(String a){
        return fromFloat(Float.valueOf(a));
    }
    
    public static CommonFloat d2f(CommonDouble a) {
        return new CommonFloat(a.val);
    }
    
    public static CommonFloat i2f(int a) {
        return fromFloat(a);
    }
    
    public static CommonFloat l2f(long a) {
        return fromFloat(a);
    }

    //-------------------------------------------------------------------------
    //----------------- Overridden methods ------------------------------------
    //-------------------------------------------------------------------------

    @Override public int compareTo(CommonFloat o) {
        return this.val.compareTo(o.val);
	}
	
	@Override public boolean equals(Object obj) {
        if (obj == null) return false;
        if (! (obj instanceof CommonFloat)) return false;
        CommonFloat ow = (CommonFloat)obj;
        return this.val.equals(ow.val);
    }

	@Override public int hashCode() {
        return this.val.hashCode();
	}

    @Override public String toString() {
        return this.val.toString();
    }
    
	@Override public int intValue() {
        return (int)this.val.toDouble();
	}

	@Override public long longValue() {
        return (long)this.val.toDouble();
	}

    @Override public byte byteValue() {
        return (byte)this.val.toDouble();
    }
    
    @Override public short shortValue() {
        return (short)this.val.toDouble();
    }
    
	@Override public float floatValue() {
        return (float)this.val.toDouble();
	}

	@Override public double doubleValue() {
        return this.val.toDouble();
	}

    public static String COJAC_MAGIC_FLOAT_wrapper() {
        return newInstance(null).COJAC_MAGIC_wrapper();
    }

    public static String COJAC_MAGIC_FLOAT_toStr(CommonFloat n) {
        return n.val.COJAC_MAGIC_toStr();
    }
    //-------------------------------------------------------------------------
    //--------------------- Auxiliary methods ---------------------------------
    //------------ (not required for the Wrapper mechanism) -------------------
    //-------------------------------------------------------------------------

}
