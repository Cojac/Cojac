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

package com.github.cojac.unit.replace;

import static com.github.cojac.unit.AgentTest.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;
import org.junit.Test;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences;
import com.github.cojac.unit.AgentTest;

public class FloatProxyLauncherTest {
	
	protected AgentTest dummyAgentTest = new AgentTest(); // just to ensure AgentTest is loaded

    Class<?> floatProxyTest;
    
	public FloatProxyLauncherTest() throws ClassNotFoundException {
        super();
        loadOperationsWithAgent(getClassFileTransformer());
    }

    protected ClassFileTransformer getClassFileTransformer() {
        Args args = new Args();
        args.specify(Arg.ALL);
		args.specify(Arg.VERBOSE);
        args.specify(Arg.EXCEPTION);
        args.specify(Arg.REPLACE_FLOATS);
        //args.specify(Arg.INSTRUMENTATION_STATS);
        
        CojacReferences.CojacReferencesBuilder builder = new CojacReferences.CojacReferencesBuilder(args);

        return new Agent(builder.build());
    }

    public void loadOperationsWithAgent(ClassFileTransformer classFileTransformer) 
	         throws ClassNotFoundException {
	    instrumentation.addTransformer(classFileTransformer, true);
	    try {
	        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	        classLoader.loadClass("com.github.cojac.unit.replace.FloatProxyNotInstrumented");
	        floatProxyTest = classLoader.loadClass("com.github.cojac.unit.replace.FloatProxy");
	    } finally {
	        instrumentation.removeTransformer(classFileTransformer);
	    }
	}
    
	@Test
    public void staticFieldDoubleAccess() throws Exception {
        invokeMethod("staticFieldDoubleAccess");
    }
	
	@Test
    public void staticFieldFloatAccess() throws Exception {
        invokeMethod("staticFieldFloatAccess");
    }
	
	@Test
	public void instanceFieldDoubleAccess() throws Exception{
		invokeMethod("instanceFieldDoubleAccess");
	}
	
	@Test
	public void instanceFieldFloatAccess() throws Exception{
		invokeMethod("instanceFieldFloatAccess");
	}
	
	@Test
	public void objectConstructor() throws Exception{
		invokeMethod("objectConstructor");
	}
	
	@Test
	public void instanceMethod() throws Exception{
		invokeMethod("instanceMethod");
	}
	
	@Test
	public void staticMethod() throws Exception{
		invokeMethod("staticMethod");
	}
	
	@Test
	public void oneDimArrayPassingByMethod() throws Exception{
		invokeMethod("oneDimArrayPassingByMethod");
	}
	
	@Test
	public void multiDimArrayPassingByMethod() throws Exception{
		invokeMethod("multiDimArrayPassingByMethod");
	}

	@Test
	public void arrayModifiedInMethodWithReference() throws Exception{
	    invokeMethod("arrayModifiedInMethodWithReference");
	}
	    
/*	
	@Test
	public void oneDimArrayField() throws Exception{
		invokeMethod("oneDimArrayField");
	}
	
	@Test
	public void multiDimArrayField() throws Exception{
		invokeMethod("multiDimArrayField");
	}
	
	@Test
	public void castedNumberPassingByMethod() throws Exception{
		invokeMethod("castedNumberPassingByMethod");
	}
	
	@Test
	public void castedNumberReturningByMethod() throws Exception{
		invokeMethod("castedNumberReturningByMethod");
	}

	@Test
	public void castedObjectReturningByMethod() throws Exception{
		invokeMethod("castedObjectReturningByMethod");
	}
*/	

/* Note: I decided to change the specification : a call to an
         uninstrumented f(Object) or g(Object[]) will now directly
         receive our CojacWrapper; in R. Monnard version, they were converted
         by the proxy (see cojac.FloatProxyMethod.createConvertMethod())  

	//@Test
    public void castedObjectPassingByMethod() throws Exception{
        invokeMethod("castedObjectPassingByMethod");
    }

    //@Test
	public void oneDimeArrayCastedObjectPassingByMethod() throws Exception{
		invokeMethod("oneDimeArrayCastedObjectPassingByMethod");
	}

	//@Test
	public void multiDimeArrayCastedObjectPassingByMethod() throws Exception{
		invokeMethod("multiDimeArrayCastedObjectPassingByMethod");
	}

*/

	// TODO - (hard/impossible...)Fix array references & proxy limitation
	/*
	 This test fails, this is a known limitation,
	 When an array is passed to the not-instrumented side, and the array is modified
	 through an other method than the passing method, the instrumented array is not modified
	*/
	/*
	@Test
	public void arrayPassedInNotInstrumentedSideModifiedWithAnOtherMethod() throws Exception{
		invokeMethod("arrayPassedInNotInstrumentedSideModifiedWithAnOtherMethod");
	}
	*/
	
	// TODO - Fix instrumented callback passed to not-instrumented side
	/*
	 This test fails, this is a known limitation,
	 When a callback is created by the user to sort a floats array for example,
	 the class passed is instrumented and the types did not match the original types
	 in the not-instrumented side.
	*/
	/*
	@Test
	public void arraySortedWithUserDefinedComparator() throws Exception{
		invokeMethod("arraySortedWithUserDefinedComparator");
	}
	*/
	
	private void invokeMethod(String methodName) throws Exception{
		if (floatProxyTest==null) return;
		Method m = floatProxyTest.getMethod(methodName);
		m.invoke(null);
	}
}
