/*
 * *
 *    Copyright 2011-2016 Valentin Gazzola & Frédéric Bapst
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.*;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences.CojacReferencesBuilder;

public class Double2FloatTest {
    String[] methods = {"testNextUp","testPrecision"};
    float[] expectedResults = {Math.nextUp(3.0f), 0.1f+1f};
    Object object;
    Class<?> classz;
    Agent agent;
    @Before
    public void instrument() throws ClassNotFoundException, UnmodifiableClassException, InstantiationException, IllegalAccessException{
        
        Assert.assertTrue(methods.length == expectedResults.length);
        Args args = new Args();

        args.specify(Arg.DOUBLE2FLOAT);
        args.specify(Arg.PRINT);

        CojacReferencesBuilder builder = new CojacReferencesBuilder(args);

        agent = new Agent(builder.build());
        AgentTest.instrumentation.addTransformer(agent);

         classz = ClassLoader.getSystemClassLoader().loadClass("com.github.cojac.unit.Double2FloatTests");
        AgentTest.instrumentation.retransformClasses(classz);

        object = classz.newInstance();
    }
    
    @After
    public void removeInstrumentation() throws UnmodifiableClassException{
        AgentTest.instrumentation.removeTransformer(agent);
        AgentTest.instrumentation.retransformClasses(classz);
    }
    
    @Test
    public void testDouble2FloatConversion() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        for (int i = 0; i < expectedResults.length; i++) {
            Method method = classz.getMethod(methods[i]);
            System.out.println("On \""+methods[i]+"\", Got: " +(double) method.invoke(object) + ", Expected: "+(double)expectedResults[i]);
            Assert.assertTrue((double) expectedResults[i] == (double) method.invoke(object));
        }
    }
}