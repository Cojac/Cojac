/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.eiafr.cojac.unit.replace;

import static junit.framework.Assert.assertEquals;

/**
 *
 * @author romain
 */
public class FloatProxy {
	
    public static void staticFieldDoubleAccess() throws Exception {
		FloatProxyNotInstrumented.staticDouble = 25.5;
		double r = FloatProxyNotInstrumented.staticDouble;
		assertEquals(25.5, r);
    }
	
	public static void staticFieldFloatAccess() throws Exception {
		FloatProxyNotInstrumented.staticFloat = 64.6f;
		float r = FloatProxyNotInstrumented.staticFloat;
		assertEquals(64.6f, r);
    }
	
	
}
