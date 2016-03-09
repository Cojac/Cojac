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

package com.github.cojac.unit;

import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences.CojacReferencesBuilder;

public class CallBackAgentTest {
    @Test
    public void testCallBackMethodCalled() throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException, NoSuchFieldException, UnmodifiableClassException {
        Args args = new Args();
        
        args.specify(Arg.ALL);
        args.setValue(Arg.CALL_BACK, "com/github/cojac/unit/CallBacksAgent/log");

		CojacReferencesBuilder builder = new CojacReferencesBuilder(args);
		
        Agent agent = new Agent(builder.build());
        AgentTest.instrumentation.addTransformer(agent);
        
        Class<?> classz = ClassLoader.getSystemClassLoader().loadClass("com.github.cojac.unit.SimpleOverflows");
        AgentTest.instrumentation.retransformClasses(classz);
        
        Object object = classz.newInstance();
        Method m = classz.getMethod("test");
        m.invoke(object);

        classz = ClassLoader.getSystemClassLoader().loadClass("com.github.cojac.unit.CallBacksAgent");
        Field field = classz.getField("count");
        Assert.assertEquals(1, field.get(null));
        
        AgentTest.instrumentation.removeTransformer(agent);
    }
}