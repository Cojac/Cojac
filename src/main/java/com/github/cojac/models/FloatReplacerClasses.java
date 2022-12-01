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

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.cojac.models.wrappers.*;
import org.objectweb.asm.Type;

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
	
	public static final String COJAC_MAGIC_CALL_DOUBLE_PREFIX = "COJAC_MAGIC_DOUBLE_";
	public static final String COJAC_MAGIC_CALL_FLOAT_PREFIX = "COJAC_MAGIC_FLOAT_";
	public static final String COJAC_MAGIC_CALL_NG_PREFIX = "COJAC_MAGIC_";

	// NOT READY YET...
	
	public static Class<?> COJAC_WRAPPER_NG_CLASS=WrapperBigDecimal.class;
    public static String COJAC_WRAPPER_NG_INTERNAL_NAME;
    private static final Map<String, Set<String>> SPECIFIC_MAGIC_METHODS;
    
    static {
        SPECIFIC_MAGIC_METHODS = new HashMap<>();
        // CAUTION: to adapt when another wrapper is designed!
        populateMagicMethods(WrapperAutodiff.class, 
                "COJAC_MAGIC_derivative", 
                "COJAC_MAGIC_asDerivativeVar");
        populateMagicMethods(WrapperAutodiffReverse.class, 
                "COJAC_MAGIC_partialDerivativeIn", 
                "COJAC_MAGIC_computePartialDerivatives",
                "COJAC_MAGIC_resetPartialDerivatives");
        populateMagicMethods(WrapperChebfun.class, 
                "COJAC_MAGIC_isChebfun", 
                "COJAC_MAGIC_identityFct",
                "COJAC_MAGIC_evaluateAt",
                "COJAC_MAGIC_derivative",
                "COJAC_MAGIC_setChebfunDomain");
        populateMagicMethods(WrapperInterval.class, 
                "COJAC_MAGIC_relativeError", 
                "COJAC_MAGIC_width");
        populateMagicMethods(WrapperStochastic.class, 
                "COJAC_MAGIC_relativeError");
        populateMagicMethods(WrapperSymbolic.class, 
                "COJAC_MAGIC_isSymbolicFunction", 
                "COJAC_MAGIC_identityFct",
                "COJAC_MAGIC_evaluateAt",
                "COJAC_MAGIC_derivative",
                "COJAC_MAGIC_setSymbolicEvaluationMode",
                "COJAC_MAGIC_setConstantSubtreeMode");
        populateMagicMethods(WrapperComplexNumber.class,
				"COJAC_MAGIC_getReal",
				"COJAC_MAGIC_getImaginary",
				"COJAC_MAGIC_equals");
    }
    
    private static void populateMagicMethods(Class<?> clazz, String... methods) {
        String internalName = Type.getType(clazz).getInternalName();
        SPECIFIC_MAGIC_METHODS.putIfAbsent(internalName, new HashSet<>());
        Set<String> ms = SPECIFIC_MAGIC_METHODS.get(internalName);
        Collections.addAll(ms, methods);
    }
    
    public static boolean isGeneralMagicMethod(String methodName) {
        return methodName.equals("COJAC_MAGIC_toString") || 
               methodName.equals("COJAC_MAGIC_wrapperName");
    }

    public static boolean isSpecificMagicMethod(String methodName) {
        if(COJAC_WRAPPER_NG_INTERNAL_NAME == null) return false;
        Set<String> s = SPECIFIC_MAGIC_METHODS.get(COJAC_WRAPPER_NG_INTERNAL_NAME);
        boolean res = s != null && s.contains(methodName);
        return res;
    }

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
