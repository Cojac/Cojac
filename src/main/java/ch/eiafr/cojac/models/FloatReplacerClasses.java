/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.eiafr.cojac.models;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.objectweb.asm.Type;

/**
 *
 * @author romain
 */
public class FloatReplacerClasses {
	
	public static Class  COJAC_DOUBLE_WRAPPER_CLASS;
	public static String COJAC_DOUBLE_WRAPPER_INTERNAL_NAME;
    public static Type   COJAC_DOUBLE_WRAPPER_TYPE;
    public static String COJAC_DOUBLE_WRAPPER_TYPE_DESCR;
    
	public static Class  COJAC_FLOAT_WRAPPER_CLASS;
    public static String COJAC_FLOAT_WRAPPER_INTERNAL_NAME;
    public static Type   COJAC_FLOAT_WRAPPER_TYPE;
    public static String COJAC_FLOAT_WRAPPER_TYPE_DESCR;
	
	public static void setDoubleWrapper(String className){
		try {
			// Class can be checked with reflexion (ensure that all needed methods are awailable and that the class implements Comparable and extends Numbers)
			COJAC_DOUBLE_WRAPPER_CLASS = Class.forName(className);
			COJAC_DOUBLE_WRAPPER_TYPE = Type.getType(COJAC_DOUBLE_WRAPPER_CLASS);
			COJAC_DOUBLE_WRAPPER_INTERNAL_NAME = COJAC_DOUBLE_WRAPPER_TYPE.getInternalName();
			COJAC_DOUBLE_WRAPPER_TYPE_DESCR = COJAC_DOUBLE_WRAPPER_TYPE.getDescriptor();
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(FloatReplacerClasses.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static void setFloatWrapper(String className){
		try {
			// Class can be checked with reflexion (ensure that all needed methods are awailable and that the class implements Comparable and extends Numbers)
			COJAC_FLOAT_WRAPPER_CLASS = Class.forName(className);
			COJAC_FLOAT_WRAPPER_TYPE = Type.getType(COJAC_FLOAT_WRAPPER_CLASS);
			COJAC_FLOAT_WRAPPER_INTERNAL_NAME = COJAC_FLOAT_WRAPPER_TYPE.getInternalName();
			COJAC_FLOAT_WRAPPER_TYPE_DESCR = COJAC_FLOAT_WRAPPER_TYPE.getDescriptor();
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(FloatReplacerClasses.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
