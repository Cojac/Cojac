package com.github.cojac.unit.profiler;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences;
import com.github.cojac.unit.AgentTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class NumericalProfilerTest {

   //correspond to the methods in Class "NumericalProfilerTests" that will be tested
   private static String[] methods = {"testFMA", "testFMAReverse", "testScalb", "testScalbReverse", "testLog1p",
           "testLog1pReverse", "testExpm1", "testAbsMixed",
           "testAbsOnlyPos", "testPow", "testHypFromSqrt", "testHypToSqrt"};
   //expected output of these methods, in the same order as the methods in "methods"
   private static double[] expectedResults = {22.0, 22.0, 448.0, 448.0, 1.2212125431609638E-5,
           1.2212125431609638E-5, 1.2299938845217184E-11, 0.0, 20.0,
           2.4319066147859923E-4, Double.POSITIVE_INFINITY, 2.998076996061238E154};

   private Object object;
   private Class<?> classz;
   private Agent agent;

   private String method;
   private double result;

   // constructor for JUnit parameters
   public NumericalProfilerTest(String method, double result) {
      this.method = method;
      this.result = result;
   }

   @Parameterized.Parameters(name = "{0}")
   public static Collection<Object[]> data() {
      ArrayList<Object[]> data = new ArrayList<>();
      for (int i = 0; i < methods.length; i++) {
         data.add(new Object[]{methods[i], expectedResults[i]});
      }
      return data;
   }

   /*
    * initialization method, instrumenting "NumericalProfilerTests" with Arg.NUMERICAL_PROFILER
    */
   @Before
   public void instrument() throws ClassNotFoundException, UnmodifiableClassException, InstantiationException,
           IllegalAccessException, NoSuchMethodException, InvocationTargetException {

      Assert.assertEquals(methods.length, expectedResults.length);
      Args args = new Args();

      args.specify(Arg.NUMERICAL_PROFILER);
      CojacReferences.CojacReferencesBuilder builder = new CojacReferences.CojacReferencesBuilder(args);

      agent = new Agent(builder.build());
      AgentTest.instrumentation.addTransformer(agent);

      classz = ClassLoader.getSystemClassLoader().loadClass("com.github.cojac.unit.profiler.NumericalProfilerTests");
      AgentTest.instrumentation.retransformClasses(classz);

      object = classz.getConstructor().newInstance();
   }

   /*
    * Post-test method removing instrumentation on "Double2FloatTests"
    */
   @After
   public void removeInstrumentation() throws UnmodifiableClassException {
      AgentTest.instrumentation.removeTransformer(agent);
      AgentTest.instrumentation.retransformClasses(classz);
   }

   /*
    * checks one by one that expectedResult[i] equals method[i]() call.
    */
   @Test
   public void testNumericalProfiler() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {
      Method method = classz.getMethod(this.method);
      // TODO this seems necessary because this class isn't instrumented, but is it really ?
      Object resObj = method.invoke(object);
      Class<?> c = resObj.getClass();
      Method m = c.getDeclaredMethod("doubleValue");
      double res = (double) m.invoke(resObj);

      String out = "On \"" + this.method + "\", Got: " + res + ", Expected: " + this.result;
      Assert.assertEquals(out, this.result, res, 0.0);
   }
}