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

import org.junit.Assert;
import org.junit.Test;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences.CojacReferencesBuilder;

public class ArbitraryPrecisionTest {
    @Test
    public void testSamePrecisionAsFloat() throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            InvocationTargetException, UnmodifiableClassException {
        Args args = new Args();
        
        String[] options = {"-"+Arg.ARBITRARY_PRECISION.shortOpt(), ""+23}; 
        args.parse(options);
        args.specify(Arg.PRINT);

        CojacReferencesBuilder builder = new CojacReferencesBuilder(args);
        
        Agent agent = new Agent(builder.build());
        AgentTest.instrumentation.addTransformer(agent);
        
        Class<?> classz = ClassLoader.getSystemClassLoader().loadClass("com.github.cojac.unit.ArbitraryPrecisionTests");
        AgentTest.instrumentation.retransformClasses(classz);
        
        Object object = classz.newInstance();
        float[] expectedResults = {Math.nextUp(3.0f)};
        int i = 0;
        Method[] methods = classz.getMethods();
        for(Method m: methods){
            if(i >= expectedResults.length){
                break; //methods after that are extended from java.lang.Object
            }
            Assert.assertTrue((double)expectedResults[i++]==(double)m.invoke(object));
        }
        
        AgentTest.instrumentation.removeTransformer(agent);
    }
}