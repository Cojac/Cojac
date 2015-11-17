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


package com.github.cojac.perfs.image;


/**
 * A stack complex number class.
 *
 * @author Nick Efford
 * @version 1.1 [1999/08/02]
 */

public class Complex {


    /////////////////////////// INSTANCE VARIABLES ///////////////////////////


    /**
     * Real part of number.
     */
    public double re;

    /**
     * Imaginary part of number.
     */
    public double im;


    //////////////////////////////// METHODS /////////////////////////////////


    public Complex() {
    }


    public Complex(float real, float imaginary) {
        re = real;
        im = imaginary;
    }


    public float getMagnitude() {
        return (float) Math.sqrt(re * re + im * im);
    }


    public float getPhase() {
        return (float) Math.atan2(im, re);
    }


    public void setPolar(double r, double theta) {
        re = (float) (r * Math.cos(theta));
        im = (float) (r * Math.sin(theta));
    }


    public String toString() {
        return new String(re + " + " + im + "i");
    }


    public void swapWith(Complex value) {
        double temp = re;
        re = value.re;
        value.re = temp;
        temp = im;
        im = value.im;
        value.im = temp;
    }


}
