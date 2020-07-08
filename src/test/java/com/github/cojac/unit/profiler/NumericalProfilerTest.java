package com.github.cojac.unit.profiler;

import com.github.cojac.Agent;
import com.github.cojac.Arg;
import com.github.cojac.Args;
import com.github.cojac.CojacReferences;
import com.github.cojac.profiler.Recommendation;
import com.github.cojac.profiler.RecommendationReport;
import com.github.cojac.profiler.ThrowableRecommendationContainer;
import com.github.cojac.unit.AgentTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class NumericalProfilerTest {

   private Object object;
   private Class<?> classz;
   private Agent agent;

   /*
    * initialization method, instrumenting "NumericalProfilerTests" with Arg.NUMERICAL_PROFILER
    */
   @Before
   public void instrument() throws ClassNotFoundException, UnmodifiableClassException, InstantiationException,
           IllegalAccessException, NoSuchMethodException, InvocationTargetException {

      Args args = new Args();

      args.specify(Arg.NUMERICAL_PROFILER);
      args.specify(Arg.EXCEPTION);
      CojacReferences.CojacReferencesBuilder builder = new CojacReferences.CojacReferencesBuilder(args);

      agent = new Agent(builder.build());
      AgentTest.instrumentation.addTransformer(agent);

      classz = ClassLoader.getSystemClassLoader().loadClass("com.github.cojac.unit.profiler.NumericalProfilerTests");
      AgentTest.instrumentation.retransformClasses(classz);

      object = classz.getConstructor().newInstance();
   }

   /*
    * Post-test method removing instrumentation on "NumericalProfilerTests"
    */
   @After
   public void removeInstrumentation() throws UnmodifiableClassException {
      AgentTest.instrumentation.removeTransformer(agent);
      AgentTest.instrumentation.retransformClasses(classz);
   }

   private List<RecommendationReport> invokeAndGetResults(String method) throws NoSuchMethodException,
           InvocationTargetException,
           IllegalAccessException {
      Method m = classz.getMethod(method);
      m.invoke(object);

      try {
         // manually call on shutdown to force printing now instead of on JVM exit
         CojacReferences.getInstance().getNumericalProfiler().onShutdown();
      } catch (ThrowableRecommendationContainer recommendations) {
         return recommendations.getAllRecommendations();
      }
      return null;
   }

   @Test
   public void testFMA() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testFMA");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1, recommendations.stream()
              .filter(x -> x.getRecommendation() == Recommendation.FMA)
              .count()
      );
   }

   @Test
   public void testFMAReverse() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testFMAReverse");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1, recommendations.stream()
              .filter(x -> x.getRecommendation() == Recommendation.FMA)
              .count()
      );
   }

   @Test
   public void testScalb() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testScalb");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.SCALB)
                      .count()
      );
   }

   @Test
   public void testScalbReverse() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testScalbReverse");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.SCALB)
                      .count()
      );
   }

   @Test
   public void testLog1p() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testLog1p");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.LOG1P)
                      .count()
      );
   }

   @Test
   public void testLog1pReverse() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testLog1pReverse");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.LOG1P)
                      .count()
      );
   }

   @Test
   public void testExpm1() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testExpm1");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.EXPM1)
                      .count()
      );
   }

   @Test
   public void testAbsMixed() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testAbsMixed");
      Assert.assertNotNull(recommendations);

      Assert.assertTrue(recommendations.stream().noneMatch(x -> x.getRecommendation() == Recommendation.USELESS_ABS));
   }

   @Test
   public void testAbsOnlyPos() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testAbsOnlyPos");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.USELESS_ABS)
                      .count()
      );
   }

   @Test
   public void testPow() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testPow");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(3,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.INTPOW)
                      .count()
      );
   }

   @Test
   public void testHypFromSqrt() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testHypFromSqrt");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.PYTH_TO_HYPOT)
                      .count()
      );
   }

   @Test
   public void testHypToSqrt() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testHypToSqrt");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.HYPOT_TO_PYTH)
                      .count()
      );
   }

   @Test
   public void testSinApprox() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testSinApprox");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.SIN_APPROX)
                      .count()
      );
   }

   @Test
   public void testCosApprox() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testCosApprox");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.COS_APPROX)
                      .count()
      );
   }

   @Test
   public void testTanApprox() throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException, NoSuchMethodException, SecurityException {

      List<RecommendationReport> recommendations = invokeAndGetResults("testTanApprox");
      Assert.assertNotNull(recommendations);

      Assert.assertEquals(1,
              recommendations.stream()
                      .filter(x -> x.getRecommendation() == Recommendation.TAN_APPROX)
                      .count()
      );
   }
}