package com.github.cojac.utils;

import java.io.IOException;

/*
Generate header:
- cojac\src\main\java>javac -h ..\..\native\include\ com\github\cojac\\utils\Posit32Utils.java
 */

/**
 * This class provides a method to load the native library and is a bridge to the posit32 library functions.
 */
public class Posit32Utils {

    /*
     * Loads native libraries for changing the rounding mode.
     * Should be in a "static{}" bloc, but then it would be called even when not needed (I/O -> slowdown).
     * Therefore, should be called only if native rounding is needed and isNativeLibLoaded == false.
     *
     * Adapted from ConversionBehaviour.loadLibrary()
     * */
    public static void loadLibrary() {
        String libRoot = "/native-libraries/posits/";
        String winLib64 = libRoot + "posits_jni.dll";
        String OSName = System.getProperty("os.name");
        int arch = Integer.parseInt(System.getProperty("sun.arch.data.model"));
        try {
            if (OSName.startsWith("Windows")) {
                if (arch == 64) {
                    NativeUtils.loadLibraryFromJar(winLib64);
                }
            } else {
                throw new UnsupportedOperationException("This operation is not supported on this platform");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* ====================
     * Conversion methods
     * ====================
     */

    /**
     * Convert a posit layout to a float layout
     *
     * @param posit a posit32
     * @return a float
     */
    public native static float toFloat(float posit);


    /**
     * Convert a float layout to a posit32 layout
     *
     * @param a a float
     * @return a posit32
     */
    public native static float toPosit(float a);


    /* ====================
     * Arithmetic methods
     * ====================
     *
     * All the methods below take one or multiple posit32 as inputs.
     *
     * Use #toPosit to convert a float in a posit.
     */
    public native static float add(float a, float b);

    public native static float substract(float a, float b);

    public native static float multiply(float a, float b);

    public native static float divide(float a, float b);

    public native static float sqrt(float a);

    public native static float fma(float a, float b, float c);

    /* ====================
     * Comparison methods
     * ====================
     *
     * All the methods below take two posit32 as inputs.
     *
     * Use #toPosit to convert a float in a posit.
     */
    public native static boolean equals(float a, float b);

    public native static boolean isLess(float a, float b);

    public native static boolean isLessOrEquals(float a, float b);
}
