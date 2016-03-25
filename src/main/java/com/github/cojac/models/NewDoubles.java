/*
 * *
 *    Copyright 2011-2016 Valentin Gazzola & Frédéric Bapst
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

package com.github.cojac.models;


public final class NewDoubles {

    //--------------------------------------------
    private NewDoubles() {
        throw new AssertionError();
    }

    public static double DADD(double a, double b) {
        return (float)a + (float)b;
    }
    public static double DSUB(double a, double b) {
        return (float)a - (float)b;
    }
    public static double DMUL(double a, double b) {
        return (float)a * (float)b;
    }
    public static double DDIV(double a, double b){
        return (float)a / (float)b;
    }
    public static double DREM(double a, double b){
        return (float)a %(float)b;
    }
    public static int DCMPL(double a, double b){
        System.out.println("here i am");
        return Double.compare((float)a ,(float)b);
    }
    public static int DCMPG(double a, double b){
        System.out.println("here i am");
        if(Double.isNaN(a)||Double.isNaN(b))
            return 1;
        return DCMPL(a,b);
    }
    public static double abs(double a){
        return (float) Math.abs(a);
    }
    public static double acos(double a){
        return (float) Math.acos((float)a);
    }
    public static double asin(double a){
        return (float) Math.asin((float)a);
    }
    public static double atan(double a){
        return (float) Math.atan((float)a);
    }
    public static double atan2(double x,double y){
        return (float) Math.atan2((float)x,(float)y);
    }
    public static double cbrt(double a){
        return (float) Math.cbrt((float)a);
    }
    public static double copySign(double a,double b){
        return Math.copySign((float)a,(float)b);
    }
    public static double cos(double a){
        return (float) Math.cos((float)a);
    }
    public static double cosh(double a){
        return (float) Math.cosh((float)a);
    }
    public static double exp(double a){
        return (float) Math.exp((float)a);
    }
    public static double expm1(double a){
        return (float) Math.expm1((float)a);
    }
    public static int getExponent(double a){
        return Math.getExponent((float)a);
    }
    public static double hypot(double a,double b){
        return (float) Math.hypot((float)a,(float)b);
    }
    public static double IEEEremainder(double a,double b){
        return (float) Math.IEEEremainder((float)a,(float)b);
    }
    public static double log(double a){
        return (float) Math.log((float)a);
    }
    public static double log10(double a){
        return (float) Math.log10((float)a);
    }
    public static double log1p(double a){
        return (float) Math.log1p((float)a);
    }
    public static double max(double a, double b){
        return Math.max((float)a, (float)b);
    }
    public static double min(double a, double b){
        return Math.min((float)a, (float)b);
    }
    public static double nextAfter(double a, double b){
        return Math.nextAfter((float)a, (float)b);
    }
    public static double nextDown(double a){
        return  Math.nextDown((float)a);
    }
    public static double nextUp(double a){
        return  Math.nextUp((float)a);
    }
    public static double pow(double a, double b){
        return (float) Math.pow((float)a, (float)b);
    }
    public static double random(){
        return (float) Math.random();
    }
    public static double scalb(double a, int b){
        return Math.scalb((float) a,b);
    }
    public static double sin(double a){
        return (float) Math.sin((float)a);
    }
    public static double sinh(double a){
        return (float) Math.sinh((float)a);
    }
    public static double sqrt(double a){
        return (float) Math.sqrt(a);
    }
    public static double tan(double a){
        return (float) Math.tan((float)a);
    }
    public static double tanh(double a){
        return (float) Math.tanh((float)a);
    }
    public static double toDegree(double a){
        return (float) Math.toDegrees((float)a);
    }
    public static double toRadian(double a){
        return (float) Math.toRadians((float)a);
    }
    public static double ulp(double a){
        return Math.ulp((float)a);
    }//*/
}