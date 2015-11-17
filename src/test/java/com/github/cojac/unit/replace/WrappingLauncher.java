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

// TODO: Reconsider this "agent testing" mechanism: one problem is that we
//       have to explicitly list the classes to be instrumented; 
//       Maybe a workaround could be to programmatically load/instrument every 
//       class from a particular package (e.g. java2d)...
public abstract class WrappingLauncher {
	
	protected AgentTest dummyAgentTest=new AgentTest(); // just to ensure AgentTest is loaded

    Class<?> wrappingClass;
    //Class<?> java2demoClass;
    
	public WrappingLauncher() {
        try {
            loadOperationsWithAgent(getClassFileTransformer());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

	protected abstract void specifyArgs(Args args);
	
    protected ClassFileTransformer getClassFileTransformer() {
        Args args = new Args();
        args.specify(Arg.PRINT);
        specifyArgs(args);
        
        CojacReferences.CojacReferencesBuilder builder = new CojacReferences.CojacReferencesBuilder(args);

        return new Agent(builder.build());
    }

    public void loadOperationsWithAgent(ClassFileTransformer classFileTransformer) 
	         throws ClassNotFoundException {
	    instrumentation.addTransformer(classFileTransformer, true);
	    try {
	        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	        wrappingClass = classLoader.loadClass("com.github.cojac.unit.replace.Wrapping");
	        //java2demoClass = classLoader.loadClass("java2d.Java2Demo");
	    } finally {
	        instrumentation.removeTransformer(classFileTransformer);
	    }
	}
    
	@Test public void invokeGo() throws Exception {
        invokeMethod("go");
    }
	
	//@Test 
//	public void invokeJava2D() throws Exception {
//	    Object args=new String[]{"-runs=1 -delay=0"};
//	    java2demoClass.getMethod("main", String[].class).invoke(null, args);
//	}

	
	private void invokeMethod(String methodName) throws Exception{
		//if (wrappingClass==null) return;
        Method m = wrappingClass.getMethod(methodName);
        //if (m==null) return;
        m.invoke(null);
	}
	//========================================
	// WARNING: it is necessary that each Junit Test runs in a new JVM
	//          because the class Wrapping cannot be re-instrumented once loaded...
	//========================================
	public static class IntervalWrappingTest extends WrappingLauncher {
        @Override protected void specifyArgs(Args args) {
            args.specify(Arg.INTERVAL);
        }
	}
    //========================================
    public static class StochasticWrappingTest extends WrappingLauncher {
        @Override protected void specifyArgs(Args args) {
            args.specify(Arg.STOCHASTIC);
        }
    }
    //========================================
    public static class DerivativeWrappingTest extends WrappingLauncher {
        @Override protected void specifyArgs(Args args) {
            args.specify(Arg.AUTOMATIC_DERIVATION);
        }
    }
    //========================================
    public static class BigDecimalWrappingTest extends WrappingLauncher {
        @Override protected void specifyArgs(Args args) {
            args.setValue(Arg.BIG_DECIMAL_PRECISION, "100");
        }
    }
}
