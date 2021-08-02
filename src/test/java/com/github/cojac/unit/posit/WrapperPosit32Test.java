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

package com.github.cojac.unit.posit;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences.CojacReferencesBuilder;
//import com.github.cojac.models.wrappers.CommonDouble;
import com.github.cojac.models.wrappers.CommonFloat;
import com.github.cojac.unit.AgentTest;
import com.github.cojac.utils.Posit32Utils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WrapperPosit32Test {

    Object object;
    Class<?> classz;
    Agent agent;

    /*
     * initialization method, instrumenting "WrapperComplexNumberNormalModeTests" with Arg.COMPLEX_NUMBER
     */
    @Before
    public void instrument() throws ClassNotFoundException, UnmodifiableClassException, InstantiationException,
            IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Args args = new Args();

        args.specify(Arg.POSIT);
        args.specify(Arg.PRINT);
        CojacReferencesBuilder builder = new CojacReferencesBuilder(args);

        agent = new Agent(builder.build());
        AgentTest.instrumentation.addTransformer(agent);

        classz = ClassLoader.getSystemClassLoader()
                .loadClass(WrapperPosit32Tests.class.getCanonicalName());
        AgentTest.instrumentation.retransformClasses(classz);

        object = classz.getDeclaredConstructor().newInstance();
    }

    /*
     * Post-test method removing instrumentation on "WrapperComplexNumberNormalModeTests"
     */
    @After
    public void removeInstrumentation() throws UnmodifiableClassException {
        AgentTest.instrumentation.removeTransformer(agent);
        AgentTest.instrumentation.retransformClasses(classz);
    }

    @Test
    public void testAdd() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        float expected = Posit32Utils.add(Posit32Utils.toPosit(10E8f), Posit32Utils.toPosit(10E-5f));
        expected = Posit32Utils.toFloat(expected);
        assertFloatComparison("testAdd", expected, 0);
    }

    @Test
    public void testSub() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        float expected = Posit32Utils.substract(Posit32Utils.toPosit(10E8f), Posit32Utils.toPosit(10E-5f));
        expected = Posit32Utils.toFloat(expected);
        assertFloatComparison("testSub", expected, 0);
    }

    @Test
    public void testMul() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        float expected = Posit32Utils.multiply(Posit32Utils.toPosit(10E8f), Posit32Utils.toPosit(10E-5f));
        expected = Posit32Utils.toFloat(expected);
        assertFloatComparison("testMul", expected, 0);
    }

    @Test
    public void testDiv() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        float expected = Posit32Utils.divide(Posit32Utils.toPosit(10E3f), Posit32Utils.toPosit(10E-5f));
        expected = Posit32Utils.toFloat(expected);
        assertFloatComparison("testDiv", expected, 0);
    }

    @Test
    public void testFma() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        float expected = Posit32Utils.fma(
                Posit32Utils.toPosit(31E2f),
                Posit32Utils.toPosit(235E-3f),
                Posit32Utils.toPosit(65E4f)
        );
        expected = Posit32Utils.toFloat(expected);
        assertFloatComparison("testFma", expected, 0);
    }

    @Test
    public void testEquality() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertBooleanComparison("testEquality", true);
    }

    @Test
    public void testInequality() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertBooleanComparison("testInequality", false);
    }

    @Test
    public void testComparisonEquality() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertBooleanComparison("testComparisonEquality", true);
    }

    @Test
    public void testComparison() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertBooleanComparison("testComparison", true);
    }

    private void assertFloatComparison(String methodName, float expected, float errorTolerance) throws
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CommonFloat result = (CommonFloat) invokeMethod(methodName);
        String out = "On \"" + methodName + "\", Got: " + result + ", Expected: " + expected;
        Assert.assertEquals(out, expected, result.doubleValue(), errorTolerance);
    }

    private void assertBooleanComparison(String methodName, boolean expected) throws
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        boolean result = (boolean) invokeMethod(methodName);
        String out = "On \"" + methodName + "\", Got: " + result + ", Expected: " + expected;
        Assert.assertEquals(out, expected, result);
    }

    private Object invokeMethod(String methodName) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method method = classz.getMethod(methodName);
        return method.invoke(object);
    }
}