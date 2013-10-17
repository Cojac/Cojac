package ch.eiafr.cojac.unit;

public class TinyFloatExample {

    public static float myFct1(float f) {
        float a;
        if (f>0) {
            float b1=f; 
            a=b1*b1;
        } else  {
            int b2=4; 
            a=b2/f;
        }
        return a+a;
    }
    
    public static float twoReusedFloatVar(float f){
        if (f>0){
            float a=f+f;
            return a;
        } else {
            float a=f*f;
            return a;

        }
    }
    
    public static float simpleFcmp(float f){
        if (f>0){
            return f+f;
        } else {
            return f*f;
        }
    }

    
    public static float myFct(float f) {
        return f+f;
    }

    
    public static void go() {
        float a=4; //double a=4; //float a=3.9f;
        float b = 8.0f * a;
        b=myFct(a);
        int res=(int) b;
        System.out.println("inside TinyFloatExample.go() "+res);
    }
    
    public static void main(String[] args) {
        go();
    }
    
}
