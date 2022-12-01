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

import java.io.File;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences.CojacReferencesBuilder;

public class LogFileTestAgent {
    //TODO: discover why that test works in surefire/maven, and not as Eclipse/JUnit...
    @Test
    public void testLogFileExists() throws ClassNotFoundException, IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException, UnmodifiableClassException {
        String logFile = System.getProperty("user.dir") + "/test.log";

        Args args = new Args();

        args.specify(Arg.ALL);
        args.setValue(Arg.LOG_FILE, logFile);

		CojacReferencesBuilder builder = new CojacReferencesBuilder(args);
		
        Agent agent = new Agent(builder.build());
        AgentTest.instrumentation.addTransformer(agent);

        Class<?> classz = ClassLoader.getSystemClassLoader().loadClass("com.github.cojac.unit.SimpleOverflows");
        AgentTest.instrumentation.retransformClasses(classz);
        
        try {
            File lf = new File(logFile);
            Assert.assertFalse(lf.exists());

            Object object = classz.getDeclaredConstructor().newInstance();
            Method m = classz.getMethod("test");
            m.invoke(object);

            Assert.assertTrue("logFile not created: "+logFile, lf.exists());
            Assert.assertTrue(lf.length() > 0);

            lf.delete();
        } finally {
            AgentTest.instrumentation.removeTransformer(agent);
        }
    }
}