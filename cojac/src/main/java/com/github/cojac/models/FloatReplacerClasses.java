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

package com.github.cojac.models;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.objectweb.asm.Type;

import com.github.cojac.models.wrappers.WrapperBigDecimal;

public class FloatReplacerClasses {
	
	public static Class<?>  COJAC_DOUBLE_WRAPPER_CLASS;
	public static String COJAC_DOUBLE_WRAPPER_INTERNAL_NAME;
    public static Type   COJAC_DOUBLE_WRAPPER_TYPE;
    public static String COJAC_DOUBLE_WRAPPER_TYPE_DESCR;
    
	public static Class<?>  COJAC_FLOAT_WRAPPER_CLASS;
    public static String COJAC_FLOAT_WRAPPER_INTERNAL_NAME;
    public static Type   COJAC_FLOAT_WRAPPER_TYPE;
    public static String COJAC_FLOAT_WRAPPER_TYPE_DESCR;
	
    // parameters from command line options, and their default values
	public static int     COJAC_BIGDECIMAL_PRECISION = 100; 
	public static double  COJAC_STABILITY_THRESHOLD = 1E-5;
	public static boolean COJAC_CHECK_UNSTABLE_COMPARISONS = true;
	
	// NOT READY YET...
	
	public static Class<?> COJAC_WRAPPER_NG_CLASS=WrapperBigDecimal.class;
    public static String COJAC_WRAPPER_NG_INTERNAL_NAME;

	public static void setNgWrapper(String className) {
	    try {
	        COJAC_WRAPPER_NG_CLASS = Class.forName(className);
	        COJAC_WRAPPER_NG_INTERNAL_NAME = Type.getType(COJAC_WRAPPER_NG_CLASS).getInternalName();
	    } catch (ClassNotFoundException ex) {
	        Logger.getLogger(FloatReplacerClasses.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	
	public static void setDoubleWrapper(String className) {
		try {
			// Class can be checked with reflexion (ensure that all needed methods are available and that the class implements Comparable and extends Numbers)
			COJAC_DOUBLE_WRAPPER_CLASS = Class.forName(className);
			COJAC_DOUBLE_WRAPPER_TYPE = Type.getType(COJAC_DOUBLE_WRAPPER_CLASS);
			COJAC_DOUBLE_WRAPPER_INTERNAL_NAME = COJAC_DOUBLE_WRAPPER_TYPE.getInternalName();
			COJAC_DOUBLE_WRAPPER_TYPE_DESCR = COJAC_DOUBLE_WRAPPER_TYPE.getDescriptor();
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(FloatReplacerClasses.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static void setFloatWrapper(String className) {
		try {
			// Class can be checked with reflexion (ensure that all needed methods are available and that the class implements Comparable and extends Numbers)
			COJAC_FLOAT_WRAPPER_CLASS = Class.forName(className);
			COJAC_FLOAT_WRAPPER_TYPE = Type.getType(COJAC_FLOAT_WRAPPER_CLASS);
			COJAC_FLOAT_WRAPPER_INTERNAL_NAME = COJAC_FLOAT_WRAPPER_TYPE.getInternalName();
			COJAC_FLOAT_WRAPPER_TYPE_DESCR = COJAC_FLOAT_WRAPPER_TYPE.getDescriptor();
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(FloatReplacerClasses.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static void setBigDecimalPrecision(int precision) {
		COJAC_BIGDECIMAL_PRECISION = precision;
	}
	
	public static void setStabilityThreshold(double threshold) {
	    COJAC_STABILITY_THRESHOLD=threshold;
	}

    public static void setCheckUnstableComparisons(boolean checkUnstableComparisons) {
        COJAC_CHECK_UNSTABLE_COMPARISONS=checkUnstableComparisons;
    }
}
