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

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences.CojacReferencesBuilder;
import com.github.cojac.unit.AgentTest;

public class SelectiveInstrumentationTest {
    double nominalReturn = Math.E+Math.PI;
    double instrumentedReturn = ((float)Math.E+(float)Math.PI);
    String[] methods = {"method0","method1","method2","method3","method4"};
    String testClass = "com.github.cojac.unit.behaviours.SelectiveInstrumentationClass";
    double[][] expectedResults = {
            {nominalReturn, nominalReturn},//method0
            {instrumentedReturn, instrumentedReturn},//method1
            {instrumentedReturn, nominalReturn},//method2
            {nominalReturn, instrumentedReturn},//method3
            {instrumentedReturn, nominalReturn},//method4
    };
    
    @Test
    public void SelectInstruTest() throws Exception {
        
        Args args = new Args();
        String selection = testClass+"{method1()[D,42,52-54_34}";
        String[] options = {"-"+Arg.DOUBLE2FLOAT.shortOpt(),
                            "-"+Arg.INSTRUMENT_SELECTIVELY.shortOpt(), selection}; 
        args.parse(options);
        args.specify(Arg.PRINT);
        //args.specify(Arg.DOUBLE2FLOAT);
        CojacReferencesBuilder builder = new CojacReferencesBuilder(args);
        
        Agent agent = new Agent(builder.build());
        AgentTest.instrumentation.addTransformer(agent);
        
        Class<?> classz = ClassLoader.getSystemClassLoader().loadClass(testClass);
        AgentTest.instrumentation.retransformClasses(classz);
        
        Object object = classz.newInstance();
        for (int i = 0; i < expectedResults.length; i++) {
            Method method = classz.getMethod(methods[i]);
            double[] res = (double[]) method.invoke(object);
            double[] expect = expectedResults[i];
            String out = "On \""+methods[i]+"\", Got: [" +res[0]+";"+res[1] + "], Expected: [" +expect[0]+";"+expect[1] + "]";
            Assert.assertTrue(out,res[0]==expect[0] &&res[1]==expect[1]);
        }
        
        AgentTest.instrumentation.removeTransformer(agent);
    }
   
}