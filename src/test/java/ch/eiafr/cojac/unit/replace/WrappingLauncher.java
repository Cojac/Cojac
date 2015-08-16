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

package ch.eiafr.cojac.unit.replace;

import ch.eiafr.cojac.Agent;
import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.CojacReferences;
import ch.eiafr.cojac.unit.AgentTest;
import static ch.eiafr.cojac.unit.AgentTest.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;

public abstract class WrappingLauncher {
	
	protected AgentTest dummyAgentTest=new AgentTest(); // just to ensure AgentTest is loaded

    Class<?> wrappingClass;
    Class<?> java2demoClass;
    
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
	        wrappingClass = classLoader.loadClass("ch.eiafr.cojac.unit.replace.Wrapping");
	        //java2demoClass = classLoader.loadClass("java2d.Java2Demo");
	    } finally {
	        instrumentation.removeTransformer(classFileTransformer);
	    }
	}
    
	@Test public void invokeGo() throws Exception {
        invokeMethod("go");
    }
	
	//@Test 
	public void invokeJava2D() throws Exception {
	    Object args=new String[]{"-runs=1 -delay=0"};
	    java2demoClass.getMethod("main", String[].class).invoke(null, args);
	}

	
	private void invokeMethod(String methodName) throws Exception{
		if (wrappingClass==null) return;
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
            args.setValue(Arg.INTERVAL, "0.001");
        }
	}
    //========================================
    public static class StochasticWrappingTest extends WrappingLauncher {
        @Override protected void specifyArgs(Args args) {
            args.setValue(Arg.STOCHASTIC, "0.001");
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
