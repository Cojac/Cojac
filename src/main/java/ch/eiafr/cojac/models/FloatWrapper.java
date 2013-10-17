package ch.eiafr.cojac.models;

public class FloatWrapper {
    private final float val;
    
    public FloatWrapper(float v) {
        val=v;
    }
    
    public static FloatWrapper fromFloat(float a) {
        return new FloatWrapper(a);
    }

    public static FloatWrapper fadd(FloatWrapper a, FloatWrapper b) {
        return new FloatWrapper(a.val+b.val);
    }
    
    public static FloatWrapper fsub(FloatWrapper a, FloatWrapper b) {
        return new FloatWrapper(a.val-b.val);
    }

    public static FloatWrapper fmul(FloatWrapper a, FloatWrapper b) {
        return new FloatWrapper(a.val*b.val);
    }

    public static FloatWrapper fdiv(FloatWrapper a, FloatWrapper b) {
        return new FloatWrapper(a.val/b.val);
    }

    public static FloatWrapper frem(FloatWrapper a, FloatWrapper b) {
        return new FloatWrapper(a.val%b.val);
    }
    
    public static FloatWrapper fneg(FloatWrapper a) {
        return new FloatWrapper(-a.val);
    }

    public static float toFloat(FloatWrapper a) {
        return a.val;
    }
    
    public static int fcmpl(FloatWrapper a, FloatWrapper b) {
        if (a.val < b.val) return -1;
        if (a.val > b.val) return +1;
        return 0;
    }
    
    public static int f2i(FloatWrapper a) {
        return (int) a.val;
    }
    
    public static long f2l(FloatWrapper a) {
        return (long) a.val;
    }
    
    public static FloatWrapper i2f(int a) {
        return new FloatWrapper((float)a);
    }
    
    public static FloatWrapper l2f(long a) {
        return new FloatWrapper((float)a);
    }

    public static FloatWrapper d2f(double a) {
        return new FloatWrapper((float)a);
    }



}
