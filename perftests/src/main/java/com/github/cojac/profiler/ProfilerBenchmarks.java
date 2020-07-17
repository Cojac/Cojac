package com.github.cojac.profiler;

import org.openjdk.jmh.annotations.*;

import java.util.Random;

@Fork(value = 1)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 2, time = 10)
@State(Scope.Benchmark)
public class ProfilerBenchmarks {

   private static final long RANDOM_SEED = 2121212L;
   private static final double ALPHA = 2 * Math.cos(Math.PI / 8.0) / (1.0 + Math.cos(Math.PI / 8.0));
   private static final double BETA  = 2 * Math.sin(Math.PI / 8.0) / (1.0 + Math.cos(Math.PI / 8.0));

   @State(Scope.Thread)
   public static class RandomProvider {
      Random gen = new Random(RANDOM_SEED);
   }

   // Taken from https://github.com/richardstartin/simdbenchmarks/blob/master/src/main/java/com/openkappa/simd/DataUtil.java
   // Under Apache License 2.0
   @State(Scope.Thread)
   public static class DoubleData {

      @Param({"100000", "1000000"})
      int size;

      public double[] data1;
      public double[] data2;

      @Setup(Level.Trial)
      public void init() {
         this.data1 = createDoubleArray(size);
         this.data2 = createDoubleArray(size);
      }

      private static double[] createDoubleArray(int size) {
         Random rand = new Random(0);
         double[] array = new double[size];
         for (int i = 0; i < size; ++i) {
            array[i] = rand.nextDouble();
         }
         return array;
      }
   }

   @Benchmark
   public double log(RandomProvider rand) {
      return Math.log(rand.gen.nextDouble() + 1.0);
   }

   @Benchmark
   public double log1p(RandomProvider rand) {
      return Math.log1p(rand.gen.nextDouble());
   }

   @Benchmark
   public double exp(RandomProvider rand) {
      return Math.exp(rand.gen.nextDouble()) - 1.0;
   }

   @Benchmark
   public double expm1(RandomProvider rand) {
      return Math.expm1(rand.gen.nextDouble());
   }

   @Benchmark
   public double hypot(RandomProvider rand) {
      double a = rand.gen.nextDouble() * (rand.gen.nextBoolean() ? -1 : 1);
      double b = rand.gen.nextDouble() * (rand.gen.nextBoolean() ? -1 : 1);
      return Math.sqrt(a * a + b * b);
   }

   @Benchmark
   public double hypotMath(RandomProvider rand) {
      double a = rand.gen.nextDouble() * (rand.gen.nextBoolean() ? -1 : 1);
      double b = rand.gen.nextDouble() * (rand.gen.nextBoolean() ? -1 : 1);
      return Math.hypot(a, b);
   }

   @Benchmark
   public double hypotAlphaMaxPlusBetaMin(RandomProvider rand) {
      double a = rand.gen.nextDouble() * (rand.gen.nextBoolean() ? -1 : 1);
      double b = rand.gen.nextDouble() * (rand.gen.nextBoolean() ? -1 : 1);
      return ALPHA * Math.max(Math.abs(a), Math.abs(b)) + BETA * Math.min(Math.abs(a), Math.abs(b));
   }

   @Benchmark
   public double hypotAlphaMaxPlusBetaMinNoAbs(RandomProvider rand) {
      double a = rand.gen.nextDouble() * (rand.gen.nextBoolean() ? -1 : 1);
      double b = rand.gen.nextDouble() * (rand.gen.nextBoolean() ? -1 : 1);
      // this does not return a "true" approximation since it works only when a and b are > 0
      // but removing the negative random would make this not comparable to the other three
      return ALPHA * Math.max(a, b) + BETA * Math.min(a, b);
   }

   @Benchmark
   public double sin(RandomProvider rand) {
      double a = rand.gen.nextDouble() * 0.39 * (rand.gen.nextBoolean() ? -1 : 1); // +- 0.39
      return Math.sin(a);
   }

   @Benchmark
   public double sinApprox(RandomProvider rand) {
      double a = rand.gen.nextDouble() * 0.39 * (rand.gen.nextBoolean() ? -1 : 1); // +- 0.39
      return a;
   }

   @Benchmark
   public double tan(RandomProvider rand) {
      double a = rand.gen.nextDouble() * 0.3 * (rand.gen.nextBoolean() ? -1 : 1); // +- 0.3
      return Math.tan(a);
   }

   @Benchmark
   public double tanApprox(RandomProvider rand) {
      double a = rand.gen.nextDouble() * 0.3 * (rand.gen.nextBoolean() ? -1 : 1); // +- 0.3
      return a;
   }

   @Benchmark
   public double cos(RandomProvider rand) {
      double a = rand.gen.nextDouble() * 0.7 * (rand.gen.nextBoolean() ? -1 : 1); // +- 0.7
      return Math.cos(a);
   }

   @Benchmark
   public double cosApprox(RandomProvider rand) {
      double a = rand.gen.nextDouble() * 0.7 * (rand.gen.nextBoolean() ? -1 : 1); // +- 0.7
      return 1.0 - (a*a)/2;
   }

   @Benchmark
   public double fmaUsingCalcs(RandomProvider rand) {
      double a = rand.gen.nextDouble();
      double b = rand.gen.nextDouble();
      double c = rand.gen.nextDouble();
      return a*b+c;
   }

   @Benchmark
   public double fmaUsingMathFma(RandomProvider rand) {
      double a = rand.gen.nextDouble();
      double b = rand.gen.nextDouble();
      double c = rand.gen.nextDouble();
      return Math.fma(a,b,c);
   }

   @Benchmark
   public double[] fmaVectorizedUsingCalcs(DoubleData data, RandomProvider rand) {
      double[] a = data.data1;
      double[] b = data.data2;
      double s = rand.gen.nextDouble();

      for(int i = 0; i < a.length; i++) {
         a[i] += s * b[i];
      }

      return a;
   }

   @Benchmark
   public double[] fmaVectorizedUsingMathFma(DoubleData data, RandomProvider rand) {
      double[] a = data.data1;
      double[] b = data.data2;
      double s = rand.gen.nextDouble();

      for(int i = 0; i < a.length; i++) {
         a[i] = Math.fma(s, b[i], a[i]);
      }

      return a;
   }

   @Benchmark
   public double pow2UsingCalcs(RandomProvider rand) {
      double v = rand.gen.nextDouble();
      return v * v;
   }

   @Benchmark
   public double pow2UsingMathPow(RandomProvider rand) {
      double v = rand.gen.nextDouble();
      return Math.pow(v, 2.0);
   }

   @Benchmark
   public double pow3UsingCalcs(RandomProvider rand) {
      double v = rand.gen.nextDouble();
      return v * v * v;
   }

   @Benchmark
   public double pow3UsingMathPow(RandomProvider rand) {
      double v = rand.gen.nextDouble();
      return Math.pow(v, 3.0);
   }

   @Benchmark
   public double powm1UsingCalcs(RandomProvider rand) {
      double v = rand.gen.nextDouble();
      return 1.0 / v;
   }

   @Benchmark
   public double powm1UsingMathPow(RandomProvider rand) {
      double v = rand.gen.nextDouble();
      return Math.pow(v, -1.0);
   }

   @Benchmark
   public double sqrtUsingPow(RandomProvider rand) {
      double v = rand.gen.nextDouble();
      return Math.pow(v, 0.5);
   }

   @Benchmark
   public double sqrtUsingSqrt(RandomProvider rand) {
      double v = rand.gen.nextDouble();
      return Math.sqrt(v);
   }

   @Benchmark
   public double scalbUsingCalcs(RandomProvider rand) {
      double v = rand.gen.nextDouble() * Math.pow(2.0, rand.gen.nextInt(20));
      return v;
   }

   @Benchmark
   public double scalbUsingMath(RandomProvider rand) {
      double v = Math.scalb(rand.gen.nextDouble(), rand.gen.nextInt(20));
      return v;
   }
}
