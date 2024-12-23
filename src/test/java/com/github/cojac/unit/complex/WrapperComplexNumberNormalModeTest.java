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

package com.github.cojac.unit.complex;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences.CojacReferencesBuilder;
import com.github.cojac.models.wrappers.CommonDouble;
import com.github.cojac.models.wrappers.WrapperComplexNumber;
import com.github.cojac.unit.AgentTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WrapperComplexNumberNormalModeTest {

    private final static double ERROR_TOLERANCE = 1e-6;

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

        args.specify(Arg.COMPLEX_NUMBER);
        args.setValue(Arg.COMPLEX_NUMBER, null);
        args.specify(Arg.PRINT);
        CojacReferencesBuilder builder = new CojacReferencesBuilder(args);

        agent = new Agent(builder.build());
        AgentTest.instrumentation.addTransformer(agent);

        classz = ClassLoader.getSystemClassLoader()
                .loadClass(WrapperComplexNumberNormalModeTests.class.getCanonicalName());
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
    public void testSqrt() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertDoubleComparison("testSqrt", -4, ERROR_TOLERANCE);
    }

    @Test
    public void testAbs() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertDoubleComparison("testAbs", 2.5, ERROR_TOLERANCE);
    }

    @Test
    public void testParseDouble_Real() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertDoubleComparison("testParseDouble_Real", 2, ERROR_TOLERANCE);
    }

    @Test
    public void testParseDouble_Imaginary() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertDoubleComparison("testParseDouble_Imaginary", -5.5, ERROR_TOLERANCE);
    }

    @Test
    public void testParseFloat_Real() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertDoubleComparison("testParseFloat_Real", 2, ERROR_TOLERANCE);
    }

    @Test
    public void testParseFloat_Imaginary() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertDoubleComparison("testParseFloat_Imaginary", -5.5, ERROR_TOLERANCE);
    }

    @Test
    public void testRealSmaller() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertBooleanComparison("testRealSmaller", false);
    }

    @Test
    public void testRealEqual() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertBooleanComparison("testRealEqual", true);
    }

    @Test
    public void testRealNotEqual() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertBooleanComparison("testRealNotEqual", true);
    }

    @Test
    public void testComplexEqual() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertBooleanComparison("testComplexEqual", true);
    }

    @Test
    public void testComplexNotEqual() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertBooleanComparison("testComplexNotEqual", false);
    }

    @Test
    public void testComplexGreater() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertBooleanComparison("testComplexGreater", true);
    }

    @Test
    public void testToDouble() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        assertDoubleComparison("testToDouble", 6, ERROR_TOLERANCE);
    }

    @SuppressWarnings("unchecked")
    private void assertDoubleComparison(String methodName, double expected, double errorTolerance) throws
            NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CommonDouble<WrapperComplexNumber> result = (CommonDouble<WrapperComplexNumber>) invokeMethod(methodName);
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