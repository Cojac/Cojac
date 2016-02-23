/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst
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

package com.github.cojac.unit;

import static com.github.cojac.unit.AgentTest.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;

import org.junit.Test;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences.CojacReferencesBuilder;

import java.net.URL;
import java.net.URLClassLoader;

public class FloatReplaceTest {
    protected AgentTest dummyAgentTest=new AgentTest(); // just to ensure AgentTest is loaded

    Class<?> tinyExample;
    
	public FloatReplaceTest() throws ClassNotFoundException {
        super();
        loadOperationsWithAgent(getClassFileTransformer());
    }

    protected ClassFileTransformer getClassFileTransformer() {
        Args args = new Args();
        args.specify(Arg.ALL);
        args.specify(Arg.EXCEPTION);
        args.specify(Arg.REPLACE_FLOATS);
        args.specify(Arg.VERBOSE);
        //args.specify(Arg.INSTRUMENTATION_STATS);

        CojacReferencesBuilder builder = new CojacReferencesBuilder(args);

        return new Agent(builder.build());
    }

    //TODO: review the test approach, so that it automatically instruments dependent classes
    public void loadOperationsWithAgent(ClassFileTransformer classFileTransformer) 
	         throws ClassNotFoundException {
	    instrumentation.addTransformer(classFileTransformer, true);
	    try {
	        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
	        classLoader.loadClass("com.github.cojac.unit.TFEAux");
	        tinyExample = classLoader.loadClass("com.github.cojac.unit.TinyFloatExample");
	    } finally {
	        instrumentation.removeTransformer(classFileTransformer);
	    }
	}
    
    @Test 
    public void testReplaceFloat() throws Exception {
        System.out.println(tinyExample);
        
        ClassLoader cl = ClassLoader.getSystemClassLoader();
 
        URL[] urls = ((URLClassLoader)cl).getURLs();
 
        for(URL url: urls){
        	System.out.println(url.getFile());
        }
        
        if (tinyExample==null) return;
        Method m = tinyExample.getMethod("go");
        if (m==null) return;
        m.invoke(null);
	}
}