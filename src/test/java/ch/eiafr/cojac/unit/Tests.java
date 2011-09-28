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

package ch.eiafr.cojac.unit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Tests implements MathOperations {
    private final Object object;

    public Tests(Object object) {
        super();

        this.object = object;
    }

    @Override
    public int iadd(int a, int b) throws Exception {
        return execInt("iadd", a, b);
    }

    @Override
    public int isub(int a, int b) throws Exception {
        return execInt("isub", a, b);
    }

    @Override
    public int imul(int a, int b) throws Exception {
        return execInt("imul", a, b);
    }

    @Override
    public int idiv(int a, int b) throws Exception {
        return execInt("idiv", a, b);
    }

    @Override
    public int iinc(int a, int b) throws Exception {
        return execInt("iinc", a, b);
    }

    @Override
    public int ineg(int a) throws Exception {
        return execInt("ineg", a);
    }

    @Override
    public long ladd(long a, long b) throws Exception {
        return execLong("ladd", a, b);
    }

    @Override
    public long lsub(long a, long b) throws Exception {
        return execLong("lsub", a, b);
    }

    @Override
    public long lmul(long a, long b) throws Exception {
        return execLong("lmul", a, b);
    }

    @Override
    public long ldiv(long a, long b) throws Exception {
        return execLong("ldiv", a, b);
    }

    @Override
    public long lneg(long a) throws Exception {
        return execLong("lneg", a);
    }

    @Override
    public double dadd(double a, double b) throws Exception {
        return execDouble("dadd", a, b);
    }

    @Override
    public double dsub(double a, double b) throws Exception {
        return execDouble("dsub", a, b);
    }

    @Override
    public double dmul(double a, double b) throws Exception {
        return execDouble("dmul", a, b);
    }

    @Override
    public double ddiv(double a, double b) throws Exception {
        return execDouble("ddiv", a, b);
    }

    @Override
    public float fadd(float a, float b) throws Exception {
        return execFloat("fadd", a, b);
    }

    @Override
    public float fsub(float a, float b) throws Exception {
        return execFloat("fsub", a, b);
    }

    @Override
    public float fmul(float a, float b) throws Exception {
        return execFloat("fmul", a, b);
    }

    @Override
    public float fdiv(float a, float b) throws Exception {
        return execFloat("fdiv", a, b);
    }

    @Override
    public double drem(double a, double b) throws Exception {
        return execDouble("drem", a, b);
    }

    @Override
    public int dcmpg(double a, double b) throws Exception {
        Method m = object.getClass().getMethod("dcmpg", Double.TYPE, Double.TYPE);
        return this.<Integer>exec(m, new Object[]{a, b});
    }

    @Override
    public int dcmpl(double a, double b) throws Exception {
        Method m = object.getClass().getMethod("dcmpl", Double.TYPE, Double.TYPE);
        return this.<Integer>exec(m, new Object[]{a, b});
    }

    @Override
    public float frem(float a, float b) throws Exception {
        return execFloat("frem", a, b);
    }

    @Override
    public int fcmpl(float a, float b) throws Exception {
        Method m = object.getClass().getMethod("fcmpl", Float.TYPE, Float.TYPE);
        return this.<Integer>exec(m, new Object[]{a, b});
    }

    @Override
    public int fcmpg(float a, float b) throws Exception {
        Method m = object.getClass().getMethod("fcmpg", Float.TYPE, Float.TYPE);
        return this.<Integer>exec(m, new Object[]{a, b});
    }

    @Override
    public int l2i(long a) throws Exception {
        Method m = object.getClass().getMethod("l2i", Long.TYPE);

        return this.<Integer>exec(m, new Object[]{a});
    }

    @Override
    public short i2s(int a) throws Exception {
        Method m = object.getClass().getMethod("i2s", Integer.TYPE);

        return this.<Short>exec(m, new Object[]{a});
    }

    @Override
    public byte i2b(int a) throws Exception {
        Method m = object.getClass().getMethod("i2b", Integer.TYPE);

        return this.<Byte>exec(m, new Object[]{a});
    }

    @Override
    public char i2c(int a) throws Exception {
        Method m = object.getClass().getMethod("i2c", Integer.TYPE);

        return this.<Character>exec(m, new Object[]{a});
    }

    @Override
    public int d2i(double a) throws Exception {
        Method m = object.getClass().getMethod("d2i", Double.TYPE);

        return this.<Integer>exec(m, new Object[]{a});
    }

    @Override
    public long d2l(double a) throws Exception {
        Method m = object.getClass().getMethod("d2l", Double.TYPE);

        return this.<Long>exec(m, new Object[]{a});
    }

    @Override
    public int f2i(float a) throws Exception {
        Method m = object.getClass().getMethod("f2i", Float.TYPE);

        return this.<Integer>exec(m, new Object[]{a});
    }

    @Override
    public long f2l(float a) throws Exception {
        Method m = object.getClass().getMethod("f2l", Float.TYPE);

        return this.<Long>exec(m, new Object[]{a});
    }

    @Override
    public float d2f(double a) throws Exception {
        Method m = object.getClass().getMethod("d2f", Double.TYPE);

        return this.<Float>exec(m, new Object[]{a});
    }

    @Override
    public double pow(double a, double b) throws Exception {
        Method m = object.getClass().getMethod("pow", Double.TYPE, Double.TYPE);

        return this.<Double>exec(m, new Object[]{a, b});
    }

    @Override
    public double asin(double a) throws Exception {
        Method m = object.getClass().getMethod("asin", Double.TYPE);

        return this.<Double>exec(m, new Object[]{a});
    }

    @Override
    public double exp(double a) throws Exception {
        Method m = object.getClass().getMethod("exp", Double.TYPE);

        return this.<Double>exec(m, new Object[]{a});
    }

    @Override
    public double log(double a) throws Exception {
        Method m = object.getClass().getMethod("log", Double.TYPE);

        return this.<Double>exec(m, new Object[]{a});
    }

    private double execDouble(String method, double a, double b) throws Exception {
        Method m = object.getClass().getMethod(method, Double.TYPE, Double.TYPE);

        return this.<Double>exec(m, new Object[]{a, b});
    }

    private float execFloat(String method, float a, float b) throws Exception {
        Method m = object.getClass().getMethod(method, Float.TYPE, Float.TYPE);

        return this.<Float>exec(m, new Object[]{a, b});
    }

    private long execLong(String method, long a, long b) throws Exception {
        Method m = object.getClass().getMethod(method, Long.TYPE, Long.TYPE);

        return this.<Long>exec(m, new Object[]{a, b});
    }

    private long execLong(String method, long a) throws Exception {
        Method m = object.getClass().getMethod(method, Long.TYPE);

        return this.<Long>exec(m, new Object[]{a});
    }

    private int execInt(String method, int a, int b) throws Exception {
        Method m = object.getClass().getMethod(method, Integer.TYPE, Integer.TYPE);

        return this.<Integer>exec(m, new Object[]{a, b});
    }

    private int execInt(String method, int a) throws Exception {
        Method m = object.getClass().getMethod(method, Integer.TYPE);

        return this.<Integer>exec(m, new Object[]{a});
    }

    private <T> T exec(Method m, Object[] passedArgv) throws Exception {
        try {
            return (T) m.invoke(object, passedArgv);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }
}