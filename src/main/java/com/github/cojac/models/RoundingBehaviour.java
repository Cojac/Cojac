package com.github.cojac.models;

public class RoundingBehaviour {
    
    public static native double DADD(double a, double b);
    static{
        System.load("C:/Users/Valentin/Documents/workspace/Cojac/NativeRounding.dll");
    }
    /*public static RoundingMode rm = RoundingMode.DOWN;
    
    
    
  /*  public static double DADD(double a, double b) {
        
    }
    public static double DSUB(double a, double b) {
    }
    public static double DMUL(double a, double b) {
    }
    public static double DDIV(double a, double b){
        BigDecimal bigA  = new BigDecimal(a);
        BigDecimal bigB  = new BigDecimal(b);
        return bigA.divide(bigB, rm).doubleValue();
    }
    public static double DREM(double a, double b){
    }
    
    public static float FADD(float a, float b) {
    }
    public static float FSUB(float a, float b) {
    }
    public static float FMUL(float a, float b) {
    }
    public static float FDIV(float a, float b){
    }
    public static float FREM(float a, float b){
    }*/
}
