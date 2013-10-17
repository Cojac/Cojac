package ch.eiafr.cojac.unit;

public class TinyFloatExample {

    public static float myFct(float f) {
        return f+f;
    }
    
    public static void go() {
        float a=4; //double a=4; //float a=3.9f;
        float b = 8.0f * a;
        //b=myFct(a);
        int res=(int) b;
        System.out.println("inside TinyFloatExample.go() "+res);
    }
    
}
