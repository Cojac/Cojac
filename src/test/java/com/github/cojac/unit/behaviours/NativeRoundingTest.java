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

package com.github.cojac.unit.behaviours;

import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences.CojacReferencesBuilder;
import com.github.cojac.unit.AgentTest;

public class NativeRoundingTest {
    //correspond to the methods in Class "Double2FloatTests" that will be tested
    String[] methods = {"testAddition1","testAddition2","testAddition3"};
    //expected output of these methods, in the same order as the methods in "methods"
    
    Object object;
    Class<?> classz;
    Agent agent;
    boolean isLibraryLoaded = false;
  
    /*
     * Post-test method removing instrumentation on "Double2FloatTests"
     */
    @After
    public void removeInstrumentation() throws UnmodifiableClassException{
        if(agent != null )
            AgentTest.instrumentation.removeTransformer(agent);
        if(classz != null )
        AgentTest.instrumentation.retransformClasses(classz);
    }
    
    @Test
    public void NativeRoundingUp() throws Exception {
        double[] expectedResults = {Math.nextUp(1.0), Math.nextUp(-1.0), 1.0};
        test(Arg.ROUND_NATIVELY_UP, expectedResults);
    }
    @Test
    public void NativeRoundingDown() throws Exception {
        double[] expectedResults = {1.0, -1.0, Math.nextDown(1.0)};
        test(Arg.ROUND_NATIVELY_DOWN, expectedResults);
    }
    @Test
    public void NativeRoundingTowardZero() throws Exception {
        double[] expectedResults = {1.0,  Math.nextUp(-1.0), Math.nextDown(1.0)};
        test(Arg.ROUND_NATIVELY_TO_ZERO, expectedResults);
    }
    /*
     * checks one by one that expectedResult[i] equals method[i]() call, when instrumented with Arg a.
     */
    private void test(Arg a, double[] expectedResults) throws Exception{
        Assert.assertTrue(methods.length == expectedResults.length);
        setRounding(a);
        Assume.assumeTrue(isLibraryLoaded);//if not, test will be skipped
        for (int i = 0; i < expectedResults.length; i++) {
            Method method = classz.getMethod(methods[i]);
            String out = "On \""+methods[i]+"\", Got: " +(double) method.invoke(object) + ", Expected: "+expectedResults[i];
            Assert.assertTrue(out,expectedResults[i] == (double) method.invoke(object));
        }
    }
    
    /*dynamic init with Arg a */
    private void setRounding(Arg a) throws ClassNotFoundException, UnmodifiableClassException, InstantiationException, IllegalAccessException{
        Args args = new Args();
        args.specify(a);
        args.specify(Arg.PRINT);
        CojacReferencesBuilder builder = new CojacReferencesBuilder(args);
        try{
            agent = new Agent(builder.build());
            isLibraryLoaded = true;
            AgentTest.instrumentation.addTransformer(agent);

            classz = ClassLoader.getSystemClassLoader().loadClass("com.github.cojac.unit.behaviours.NativeRoundingTests");
            AgentTest.instrumentation.retransformClasses(classz);

            object = classz.newInstance();
        }catch(RuntimeException e){
            System.err.println("Library couldn't be charged. Abording Native rounding tests.");
            isLibraryLoaded = false;
        }
    }
}