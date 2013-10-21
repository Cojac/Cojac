package ch.eiafr.cojac.unit;
import java.util.LinkedList;

//TODO: a reasonable/coherent test example for FloatReplace

class TFEAux {
    static float myStaticFloat=3.0f;
    static int myStaticInt=3;
    float tfeaux_fct(float f) {
        return 2*f;
    }
}



public class TinyFloatExample {

    
    static float myStaticFloat=73.0f; 

    static float everyFloatOpcode(float f) {
        long l=2L; double d=2.0; int i=2;
        float a=f;
        a = a+f;
        a = a-f;
        a = a*f;
        a = a/f;
        a = a%10f;
        a = -a;
        if (a>f) a=f;
        if (a<f) a=f;
        a = a+0f+1f+2f;
        a += (int)f;
        a += (long)f;
        a += (double)f;
        a += (float)l;
        a += (float)d;
        a += (float)i;
        return a;
    }
    
    public static float myFct1(float f) {
        float a;
        if (f>0) {
            float b1=f; 
            a=b1*b1;
        } else  {
            int b2=4; 
            a=b2/f;
        }
        return a+a ; //+ (new TFEAux()).tfeaux_fct(a);
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

    protected static float fct2(float f, float g) {
        for(int i=0; i<10; i++){
            f= 3*f +g;
        }
        //f=Float.valueOf(f);
        return f % g;
    }
    
    public static float myFct(float f) {
        return f+f;
    }

    
    public static void go() {
        float a=4; //double a=4; //float a=3.9f;
        float b = 8.0f * a;
        b=myFct1(a);
        b=fct2(b,b);
        b+= everyFloatOpcode(b);
        int res=(int) b;
        LinkedList<Float> ll=new LinkedList<>();
        //ll.add(b);
        System.out.println("inside TinyFloatExample.go() "+res);
        System.out.println("inside TinyFloatExample.go() "+(int)(TFEAux.myStaticFloat));
    }
    
    public static void main(String[] args) {
        go();
    }
    
}
    // D:\Git-MyRepository\cojac\target\test-classes>
    // java -javaagent:..\ch.eiafr.cojac-1.3-jar-with-dependencies.jar="-v -R" 
    // -Djava.system.class.loader=ch.eiafr.cojac.VerboseClassLoader 
    // ch.eiafr.cojac.unit.TinyFloatExample > e:\auxaux.txt}
